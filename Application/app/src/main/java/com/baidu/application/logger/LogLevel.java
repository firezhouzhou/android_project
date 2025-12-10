package com.baidu.application.logger;

/**
 * 日志级别枚举
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public enum LogLevel {
    /**
     * 详细日志（最低级别）
     */
    VERBOSE(2, "V"),
    
    /**
     * 调试日志
     */
    DEBUG(3, "D"),
    
    /**
     * 信息日志
     */
    INFO(4, "I"),
    
    /**
     * 警告日志
     */
    WARN(5, "W"),
    
    /**
     * 错误日志
     */
    ERROR(6, "E"),
    
    /**
     * 不输出任何日志
     */
    NONE(Integer.MAX_VALUE, "N");

    private final int priority;
    private final String label;

    LogLevel(int priority, String label) {
        this.priority = priority;
        this.label = label;
    }

    public int getPriority() {
        return priority;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 判断当前级别是否可以输出
     *
     * @param configLevel 配置的最低级别
     * @return true 可以输出
     */
    public boolean isLoggable(LogLevel configLevel) {
        return this.priority >= configLevel.priority;
    }
}
