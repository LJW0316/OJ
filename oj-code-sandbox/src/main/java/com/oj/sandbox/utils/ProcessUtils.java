package com.oj.sandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.oj.sandbox.model.ExecuteMessage;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 进程工具类
 */
public class ProcessUtils {
    /**
     * 执行进程并保存信息
     *
     * @param runProcess 执行的进程
     * @param opName     操作名称
     * @return 执行信息包括返回码、输出信息、错误信息
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {

        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //等待程序执行，获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            //正常退出
            if (exitValue == 0) {
                System.out.println(opName + "成功");
                //分批获取进程的输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> outputList = new ArrayList<>();
                //逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputList.add(compileOutputLine);
                }
                executeMessage.setMessage(StringUtils.join(outputList, '\n'));
            } else {
                //异常退出
                System.out.println(opName + "失败" + exitValue);
                //分批获取进程的输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));

                List<String> outputList = new ArrayList<>();
                //逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputList.add(compileOutputLine);
                }
                executeMessage.setMessage(StringUtils.join(outputList, '\n'));
                //分批获取进程的输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                List<String> errorOutputList = new ArrayList<>();
                //逐行读取
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = bufferedReader.readLine()) != null) {
                    errorOutputList.add(errorCompileOutputLine);
                }
                executeMessage.setMessage(StringUtils.join(errorOutputList, '\n'));
            }
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return executeMessage;
    }

    /**
     * 执行交互式进程并保存信息
     *
     * @param runProcess 执行的进程
     * @param args     程序输入
     * @return 执行信息包括返回码、输出信息、错误信息
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess, String args) {

        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            //向控制台输入程序
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            String[] s = args.split(" ");
            String join = StrUtil.join("\n", s) + "\n";
            outputStreamWriter.write(join);
            outputStreamWriter.flush();

            //分批获取进程的输出
            InputStream inputStream = runProcess.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder compileOutputStringBuilder = new StringBuilder();
            String compileOutputLine;
            //逐行读取
            while ((compileOutputLine = bufferedReader.readLine()) != null) {
                compileOutputStringBuilder.append(compileOutputLine);
            }
            executeMessage.setMessage(compileOutputStringBuilder.toString());
            //释放资源
            outputStreamWriter.close();
            outputStream.close();
            inputStream.close();
            runProcess.destroy();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }
}
