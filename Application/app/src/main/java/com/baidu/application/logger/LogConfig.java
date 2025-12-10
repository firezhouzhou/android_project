package com.baidu.application.logger;

import android.content.Context;
import java.io.File;

/**
 * 日志配置类
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class LogConfig {
    
    /** 是否启用日志 */
    private boolean enable;
    
    /** 是否启用控制台输出 */
    private boolean enableConsole;
    
    /** 是否启用文件输出 */
    private boolean enableFile;
    
    /** 日志级别 */
    private LogLevel logLevel;
    
    /** 全局 Tag */
    private String globalTag;
    
    /** 日志文件目录 */
    private String logDir;
    
    /** 单个日志文件最大大小（字节） */
    private long maxFileSize;
    
    /** 日志保留天数 */
    private int retentionDays;
    
    /** 是否显示线程信息 */
    private boolean showThreadInfo;
    
    /** 是否显示堆栈信息 */
    private boolean showStackTrace;
    
    /** 堆栈深度 */
    private int stackTraceDepth;

    private LogConfig(Builder builder) {
        this.enable = builder.enable;
        this.enableConsole = builder.enableConsole;
        this.enableFile = builder.enableFile;
        this.logLevel = builder.logLevel;
        this.globalTag = builder.globalTag;
        this.logDir = builder.logDir;
        this.maxFileSize = builder.maxFileSize;
        this.retentionDays = builder.retentionDays;
        this.showThreadInfo = builder.showThreadInfo;
        this.showStackTrace = builder.showStackTrace;
        this.stackTraceDepth = builder.stackTraceDepth;
    }

    public boolean isEnable() {
        return enable;
    }

    public boolean isEnableConsole() {
        return enableConsole;
    }

    public boolean isEnableFile() {
        return enableFile;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public String getGlobalTag() {
        return globalTag;
    }

    public String getLogDir() {
        return logDir;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public int getRetentionDays() {
        return retentionDays;
    }

    public boolean isShowThreadInfo() {
        return showThreadInfo;
    }

    public boolean isShowStackTrace() {
        return showStackTrace;
    }

    public int getStackTraceDepth() {
        return stackTraceDepth;
    }

    /**
     * 配置构建器
     */
    public static class Builder {
        private boolean enable = true;
        private boolean enableConsole = true;
        private boolean enableFile = true;
        private LogLevel logLevel = LogLevel.VERBOSE;
        private String globalTag = "AppLog";
        private String logDir;
        private long maxFileSize = 5 * 1024 * 1024; // 5MB
        private int retentionDays = 7;
        private boolean showThreadInfo = true;
        private boolean showStackTrace = true;
        private int stackTraceDepth = 5;

        public Builder(Context context) {
            // 默认日志目录：/data/data/package/files/logs
            this.logDir = new File(context.getFilesDir(), "logs").getAbsolutePath();
        }

        /**
         * 设置是否启用日志
         */
        public Builder setEnable(boolean enable) {
            this.enable = enable;
            return this;
        }

        /**
         * 设置是否启用控制台输出
         */
        public Builder setEnableConsole(boolean enableConsole) {
            this.enableConsole = enableConsole;
            return this;
        }

        /**
         * 设置是否启用文件输出
         */
        public Builder setEnableFile(boolean enableFile) {
            this.enableFile = enableFile;
            return this;
        }

        /**
         * 设置日志级别
         */
        public Builder setLogLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        /**
         * 设置全局 Tag
         */
        public Builder setGlobalTag(String globalTag) {
            this.globalTag = globalTag;
            return this;
        }

        /**
         * 设置日志目录
         */
        public Builder setLogDir(String logDir) {
            this.logDir = logDir;
            return this;
        }

        /**
         * 设置单个文件最大大小（字节）
         */
        public Builder setMaxFileSize(long maxFileSize) {
            this.maxFileSize = maxFileSize;
            return this;
        }

        /**
         * 设置日志保留天数
         */
        public Builder setRetentionDays(int retentionDays) {
            this.retentionDays = retentionDays;
            return this;
        }

        /**
         * 设置是否显示线程信息
         */
        public Builder setShowThreadInfo(boolean showThreadInfo) {
            this.showThreadInfo = showThreadInfo;
            return this;
        }

        /**
         * 设置是否显示堆栈信息
         */
        public Builder setShowStackTrace(boolean showStackTrace) {
            this.showStackTrace = showStackTrace;
            return this;
        }

        /**
         * 设置堆栈深度
         */
        public Builder setStackTraceDepth(int stackTraceDepth) {
            this.stackTraceDepth = stackTraceDepth;
            return this;
        }

        /**
         * 构建配置对象
         */
        public LogConfig build() {
            return new LogConfig(this);
        }
    }
}
