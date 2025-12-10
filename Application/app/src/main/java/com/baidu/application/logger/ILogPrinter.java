package com.baidu.application.logger;

/**
 * 日志输出接口
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public interface ILogPrinter {
    
    /**
     * 打印日志
     *
     * @param level 日志级别
     * @param tag 标签
     * @param message 消息内容
     */
    void print(LogLevel level, String tag, String message);
    
    /**
     * 打印日志（带异常）
     *
     * @param level 日志级别
     * @param tag 标签
     * @param message 消息内容
     * @param throwable 异常对象
     */
    void print(LogLevel level, String tag, String message, Throwable throwable);
    
    /**
     * 清理资源
     */
    void release();
}
