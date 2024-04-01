package com.yupi.oj.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题信息消息枚举
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public enum JudgeInfoMessageEnum {

    ACCEPT("Accepted", "Accepted"),
    WRONG_ANSWER("Wrong Answer", "Wrong Answer"),
    COMPILE_ERROR("Compile Error", "Compile Error"),
    MEMORY_LIMIT_EXCEEDED("Memory Limit Exceed", "MEMORY_LIMIT_EXCEEDED"),
    TIME_LIMIT_EXCEEDED("Time Limit Exceed", "Time Limit Exceed"),
    PRESENTATION_ERROR("Presentation Error", "Presentation Error"),
    WAITING("Waiting", "Waiting"),
    OUTPUT_LIMIT_ERROR("Output Limit Error", "Output Limit Error"),
    DANGEROUS_OPERATION("Dangerous Operation", "Dangerous Operation"),
    RUNNING_ERROR("Running Error", "Running Error"),
    SYSTEM_ERROR("System Error", "System Error");

    private final String text;

    private final String value;

    JudgeInfoMessageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static JudgeInfoMessageEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoMessageEnum anEnum : JudgeInfoMessageEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
