package com.baidu.application.logger.util;

import android.util.Log;
import com.baidu.application.logger.LogConfig;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志文件管理器
 * 负责文件创建、分片、清理
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class LogFileManager {
    
    private static final String TAG = "LogFileManager";
    private static final String LOG_FILE_PREFIX = "app_log_";
    private static final String LOG_FILE_SUFFIX = ".log";
    
    private final LogConfig config;
    private final SimpleDateFormat dateFormat;
    private File currentLogFile;

    public LogFileManager(LogConfig config) {
        this.config = config;
        this.dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        ensureLogDirExists();
    }

    /**
     * 确保日志目录存在
     */
    private void ensureLogDirExists() {
        File logDir = new File(config.getLogDir());
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            if (!created) {
                Log.e(TAG, "Failed to create log directory: " + config.getLogDir());
            }
        }
    }

    /**
     * 获取当前日志文件
     * 如果文件不存在或超过大小限制，则创建新文件
     */
    public synchronized File getCurrentLogFile() {
        if (currentLogFile == null || needCreateNewFile(currentLogFile)) {
            currentLogFile = createNewLogFile();
        }
        return currentLogFile;
    }

    /**
     * 判断是否需要创建新文件
     */
    private boolean needCreateNewFile(File file) {
        // 文件不存在
        if (!file.exists()) {
            return true;
        }
        
        // 文件超过大小限制
        if (file.length() >= config.getMaxFileSize()) {
            return true;
        }
        
        // 日期变化（跨天）
        String currentDate = dateFormat.format(new Date());
        String fileName = file.getName();
        if (!fileName.contains(currentDate)) {
            return true;
        }
        
        return false;
    }

    /**
     * 创建新的日志文件
     */
    private File createNewLogFile() {
        String date = dateFormat.format(new Date());
        String fileName = LOG_FILE_PREFIX + date + "_" + System.currentTimeMillis() + LOG_FILE_SUFFIX;
        File logFile = new File(config.getLogDir(), fileName);
        
        try {
            if (!logFile.exists()) {
                boolean created = logFile.createNewFile();
                if (!created) {
                    Log.e(TAG, "Failed to create log file: " + logFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to create log file", e);
        }
        
        return logFile;
    }

    /**
     * 清理过期日志
     */
    public void cleanOldLogs() {
        File logDir = new File(config.getLogDir());
        if (!logDir.exists() || !logDir.isDirectory()) {
            return;
        }
        
        long retentionMillis = config.getRetentionDays() * 24L * 60 * 60 * 1000;
        long currentTime = System.currentTimeMillis();
        
        File[] files = logDir.listFiles();
        if (files == null) {
            return;
        }
        
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(LOG_FILE_SUFFIX)) {
                long fileAge = currentTime - file.lastModified();
                if (fileAge > retentionMillis) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        Log.d(TAG, "Deleted old log file: " + file.getName());
                    }
                }
            }
        }
    }

    /**
     * 获取所有日志文件
     */
    public File[] getAllLogFiles() {
        File logDir = new File(config.getLogDir());
        if (!logDir.exists() || !logDir.isDirectory()) {
            return new File[0];
        }
        
        File[] files = logDir.listFiles((dir, name) -> name.endsWith(LOG_FILE_SUFFIX));
        return files != null ? files : new File[0];
    }

    /**
     * 获取日志目录总大小
     */
    public long getTotalLogSize() {
        File[] files = getAllLogFiles();
        long totalSize = 0;
        for (File file : files) {
            totalSize += file.length();
        }
        return totalSize;
    }
}
