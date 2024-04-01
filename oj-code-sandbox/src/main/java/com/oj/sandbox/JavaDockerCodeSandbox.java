package com.oj.sandbox;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.oj.sandbox.model.ExecuteCodeRequest;
import com.oj.sandbox.model.ExecuteCodeResponse;
import com.oj.sandbox.model.ExecuteMessage;
import com.oj.sandbox.model.JudgeInfo;
import com.oj.sandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class JavaDockerCodeSandbox extends JavaCodeSandboxTemplate {

    private static Boolean FIRST_INIT = true;

//    public static void main(String[] args) {
//        JavaDockerCodeSandbox javaNativeCodeSandbox = new JavaDockerCodeSandbox();
//        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
//        executeCodeRequest.setInputList(Arrays.asList("1 2", "1 3"));
//        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
////        String code = ResourceUtil.readStr("testCode/unsafeCode/Main.java", StandardCharsets.UTF_8);
//        executeCodeRequest.setCode(code);
//        executeCodeRequest.setLanguage("java");
//        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(executeCodeRequest);
//        System.out.println(executeCodeResponse);
//    }


    /**
     * 获取输出结果
     * @param executeMessageList 执行信息列表
     * @return 执行程序响应信息
     */
    @Override
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        //整理收集输出结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        long totalMaxTime = 0L;
        long totalMaxMemory = 0L;
        for (ExecuteMessage msg : executeMessageList) {
            String errorMsg = msg.getErrorMessage();
            if (StrUtil.isNotBlank(errorMsg)) {
                executeCodeResponse.setMessage(errorMsg);
                //执行中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(msg.getMessage());
            Long t = msg.getTime();
            Long m = msg.getMemory();
            if (t != null) {
                totalMaxTime = Math.max(totalMaxTime, t);
            }
            if (m != null) {
                totalMaxMemory = Math.max(totalMaxMemory, m);
            }
        }
        executeCodeResponse.setOutputList(outputList);
        //正常运行完成
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(1);
        }
//        executeCodeResponse.setStatus(1);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMemory(totalMaxMemory);
        judgeInfo.setTime(totalMaxTime);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();

        //1. 把用户代码保存为文件
        File userCodeFile = saveCodeToFile(code);

        //2.编译代码，得到class文件
        ExecuteMessage compileFileExecuteMessage = compileFile(userCodeFile);
        System.out.println(compileFileExecuteMessage);

        //3. 创建容器，上传编译文件
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        //拉取镜像
        String image = "openjdk:8-alpine";
        if (FIRST_INIT) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像：" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd.exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
            System.out.println("下载完成");
            FIRST_INIT = false;
        }
        //创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        HostConfig hostConfig = new HostConfig();
        //指定文件路径映射（容器挂载目录）
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        hostConfig.setBinds(new Bind(userCodeParentPath, new Volume("/app")));
        hostConfig.withMemory(100 * 1000 * 1000L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true) //禁用网络连接
                .withReadonlyRootfs(true) //根目录只读
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();

        //启动容器
        dockerClient.startContainerCmd(containerId).exec();
        //执行命令并获取结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        final String[] message = {""};
        final String[] errorMessage = {""};
        long time = 0L;
        final boolean[] timeout = {true};
        for (String inputArgs : inputList) {
            ExecuteMessage executeMessage = new ExecuteMessage();
            StopWatch stopWatch = new StopWatch();
            String[] inputArgsArray = inputArgs.split(" ");
            String[] cmdArray = ArrayUtil.append(new String[]{"java", "-cp", "/app", "Main"}, inputArgsArray);
            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            System.out.println("创建执行命令" + execCreateCmdResponse);
            String execId = execCreateCmdResponse.getId();
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    //如果执行完成则表示没有超时
                    timeout[0] = false;
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();
                    if (StreamType.STDERR.equals(streamType)) {
                        errorMessage[0] = new String(frame.getPayload());
                        System.out.println("错误输出结果：" + errorMessage[0]);
                    } else {
                        message[0] = new String(frame.getPayload());
                        System.out.println("输出结果：" + message[0]);
                    }
                    super.onNext(frame);
                }
            };
            final long[] maxMemory = {0L};
            //获取内存
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }

                @Override
                public void close() throws IOException {

                }

                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }
            });
            statsCmd.exec(statisticsResultCallback);
            statsCmd.close();
            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
                stopWatch.stop();
            } catch (InterruptedException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }
            executeMessage.setErrorMessage(errorMessage[0]);
            executeMessage.setMessage(message[0]);
            time = stopWatch.getLastTaskTimeMillis();
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessageList.add(executeMessage);
        }

        //4.整理收集输出结果
        ExecuteCodeResponse executeCodeResponse = getOutputResponse(executeMessageList);

        //5. 文件清理
        boolean b = deleteFile(userCodeFile);
        if (!b) {
            log.error("delete file error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
        }
        //删除容器
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();

        return executeCodeResponse;
    }

    /**
     * 获取错误响应
     *
     * @param e
     * @return
     */
    private ExecuteCodeResponse getErrorResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        //标识代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}
