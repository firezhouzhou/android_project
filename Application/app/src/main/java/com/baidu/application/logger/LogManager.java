package com.baidu.application.logger;

import android.content.Context;
import com.baidu.application.logger.printer.ConsoleLogPrinter;
import com.baidu.application.logger.printer.FileLogPrinter;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志管理器（单例）
 * 全局日志入口
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class LogManager {
    
    private static volatile LogManager instance;
    private Logger logger;
    private LogConfig config;

    private LogManager() {
    }

    /**
     * 获取单例实例
     */
    public static LogManager getInstance() {
        if (instance == null) {
            synchronized (LogManager.class) {
                if (instance == null) {
                    instance = new LogManager();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化日志系统
     *
     * @param config 日志配置
     */
    public void init(LogConfig config) {
        this.config = config;
        
        List<ILogPrinter> printers = new ArrayList<>();
        
        // 添加控制台输出
        if (config.isEnableConsole()) {
            printers.add(new ConsoleLogPrinter());
        }
        
        // 添加文件输出
        if (config.isEnableFile()) {
            printers.add(new FileLogPrinter(config));
        }
        
        this.logger = new Logger(config, printers);
    }

    /**
     * 快速初始化（使用默认配置）
     *
     * @param context 上下文
     */
    public void init(Context context) {
        LogConfig config = new LogConfig.Builder(context)
                .build();
        init(config);
    }

    /**
     * 获取 Logger 实例
     */
    public Logger getLogger() {
        if (logger == null) {
            throw new IllegalStateException("LogManager not initialized. Call init() first.");
        }
        return logger;
    }

    /**
     * 获取配置
     */
    public LogConfig getConfig() {
        return config;
    }

    // ==================== 便捷方法 ====================

    public void v(String message) {
        getLogger().v(message);
    }

    public void v(String tag, String message) {
        getLogger().v(tag, message);
    }

    public void v(String tag, String message, Throwable throwable) {
        getLogger().v(tag, message, throwable);
    }

    public void d(String message) {
        getLogger().d(message);
    }

    public void d(String tag, String message) {
        getLogger().d(tag, message);
    }

    public void d(String tag, String message, Throwable throwable) {
        getLogger().d(tag, message, throwable);
    }

    public void i(String message) {
        getLogger().i(message);
    }

    public void i(String tag, String message) {
        getLogger().i(tag, message);
    }

    public void i(String tag, String message, Throwable throwable) {
        getLogger().i(tag, message, throwable);
    }

    public void w(String message) {
        getLogger().w(message);
    }

    public void w(String tag, String message) {
        getLogger().w(tag, message);
    }

    public void w(String tag, String message, Throwable throwable) {
        getLogger().w(tag, message, throwable);
    }

    public void e(String message) {
        getLogger().e(message);
    }

    public void e(String tag, String message) {
        getLogger().e(tag, message);
    }

    public void e(String tag, String message, Throwable throwable) {
        getLogger().e(tag, message, throwable);
    }

    public void json(String json) {
        getLogger().json(json);
    }

    public void json(String tag, String json) {
        getLogger().json(tag, json);
    }

    public void xml(String xml) {
        getLogger().xml(xml);
    }

    public void xml(String tag, String xml) {
        getLogger().xml(tag, xml);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (logger != null) {
            logger.release();
        }
    }
}
