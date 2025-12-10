package com.baidu.application.logger;

import com.baidu.application.logger.formatter.ILogFormatter;
import com.baidu.application.logger.formatter.JsonFormatter;
import com.baidu.application.logger.formatter.XmlFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志核心实现类
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class Logger {
    
    private final LogConfig config;
    private final List<ILogPrinter> printers;
    private final JsonFormatter jsonFormatter;
    private final XmlFormatter xmlFormatter;

    public Logger(LogConfig config, List<ILogPrinter> printers) {
        this.config = config;
        this.printers = printers;
        this.jsonFormatter = new JsonFormatter();
        this.xmlFormatter = new XmlFormatter();
    }

    /**
     * Verbose 级别日志
     */
    public void v(String message) {
        log(LogLevel.VERBOSE, config.getGlobalTag(), message, null);
    }

    public void v(String tag, String message) {
        log(LogLevel.VERBOSE, tag, message, null);
    }

    public void v(String tag, String message, Throwable throwable) {
        log(LogLevel.VERBOSE, tag, message, throwable);
    }

    /**
     * Debug 级别日志
     */
    public void d(String message) {
        log(LogLevel.DEBUG, config.getGlobalTag(), message, null);
    }

    public void d(String tag, String message) {
        log(LogLevel.DEBUG, tag, message, null);
    }

    public void d(String tag, String message, Throwable throwable) {
        log(LogLevel.DEBUG, tag, message, throwable);
    }

    /**
     * Info 级别日志
     */
    public void i(String message) {
        log(LogLevel.INFO, config.getGlobalTag(), message, null);
    }

    public void i(String tag, String message) {
        log(LogLevel.INFO, tag, message, null);
    }

    public void i(String tag, String message, Throwable throwable) {
        log(LogLevel.INFO, tag, message, throwable);
    }

    /**
     * Warn 级别日志
     */
    public void w(String message) {
        log(LogLevel.WARN, config.getGlobalTag(), message, null);
    }

    public void w(String tag, String message) {
        log(LogLevel.WARN, tag, message, null);
    }

    public void w(String tag, String message, Throwable throwable) {
        log(LogLevel.WARN, tag, message, throwable);
    }

    /**
     * Error 级别日志
     */
    public void e(String message) {
        log(LogLevel.ERROR, config.getGlobalTag(), message, null);
    }

    public void e(String tag, String message) {
        log(LogLevel.ERROR, tag, message, null);
    }

    public void e(String tag, String message, Throwable throwable) {
        log(LogLevel.ERROR, tag, message, throwable);
    }

    /**
     * 打印 JSON（自动格式化）
     */
    public void json(String json) {
        json(config.getGlobalTag(), json);
    }

    public void json(String tag, String json) {
        String formatted = jsonFormatter.format(json);
        log(LogLevel.DEBUG, tag, formatted, null);
    }

    /**
     * 打印 XML（自动格式化）
     */
    public void xml(String xml) {
        xml(config.getGlobalTag(), xml);
    }

    public void xml(String tag, String xml) {
        String formatted = xmlFormatter.format(xml);
        log(LogLevel.DEBUG, tag, formatted, null);
    }

    /**
     * 打印对象（使用自定义格式化器）
     */
    public void object(Object obj, ILogFormatter formatter) {
        object(config.getGlobalTag(), obj, formatter);
    }

    public void object(String tag, Object obj, ILogFormatter formatter) {
        String formatted = formatter.format(obj);
        log(LogLevel.DEBUG, tag, formatted, null);
    }

    /**
     * 核心日志方法
     */
    private void log(LogLevel level, String tag, String message, Throwable throwable) {
        // 检查是否启用
        if (!config.isEnable()) {
            return;
        }
        
        // 检查日志级别
        if (!level.isLoggable(config.getLogLevel())) {
            return;
        }
        
        // 构建完整消息
        String fullMessage = buildMessage(message);
        
        // 分发到各个 Printer
        for (ILogPrinter printer : printers) {
            printer.print(level, tag, fullMessage, throwable);
        }
    }

    /**
     * 构建完整消息（添加线程信息、堆栈信息等）
     */
    private String buildMessage(String message) {
        StringBuilder sb = new StringBuilder();
        
        // 添加线程信息
        if (config.isShowThreadInfo()) {
            Thread thread = Thread.currentThread();
            sb.append("[Thread: ").append(thread.getName()).append("] ");
        }
        
        // 添加堆栈信息
        if (config.isShowStackTrace()) {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            int depth = Math.min(config.getStackTraceDepth(), stackTrace.length);
            
            for (int i = 0; i < depth; i++) {
                StackTraceElement element = stackTrace[i];
                String className = element.getClassName();
                
                // 跳过日志框架自身的堆栈
                if (className.contains("Logger") || 
                    className.contains("LogManager") ||
                    className.contains("Thread") ||
                    className.contains("VMStack")) {
                    continue;
                }
                
                sb.append("\n    at ")
                  .append(element.getClassName())
                  .append(".")
                  .append(element.getMethodName())
                  .append("(")
                  .append(element.getFileName())
                  .append(":")
                  .append(element.getLineNumber())
                  .append(")");
                break; // 只显示第一个有效堆栈
            }
            
            if (sb.length() > 0) {
                sb.append("\n");
            }
        }
        
        sb.append(message);
        return sb.toString();
    }

    /**
     * 释放资源
     */
    public void release() {
        for (ILogPrinter printer : printers) {
            printer.release();
        }
    }
}
