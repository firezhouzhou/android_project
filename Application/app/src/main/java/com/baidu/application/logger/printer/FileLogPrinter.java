package com.baidu.application.logger.printer;

import android.util.Log;
import com.baidu.application.logger.ILogPrinter;
import com.baidu.application.logger.LogConfig;
import com.baidu.application.logger.LogLevel;
import com.baidu.application.logger.util.LogFileManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 文件日志输出器（异步写入）
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class FileLogPrinter implements ILogPrinter {
    
    private static final String TAG = "FileLogPrinter";
    private static final int QUEUE_SIZE = 1000;
    
    private final LogConfig config;
    private final LogFileManager fileManager;
    private final LinkedBlockingQueue<LogItem> logQueue;
    private final ExecutorService executorService;
    private final SimpleDateFormat dateFormat;
    
    private volatile boolean isRunning = true;

    public FileLogPrinter(LogConfig config) {
        this.config = config;
        this.fileManager = new LogFileManager(config);
        this.logQueue = new LinkedBlockingQueue<>(QUEUE_SIZE);
        this.executorService = Executors.newSingleThreadExecutor();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        
        // 启动写入线程
        startWriteThread();
        
        // 清理过期日志
        fileManager.cleanOldLogs();
    }

    @Override
    public void print(LogLevel level, String tag, String message) {
        print(level, tag, message, null);
    }

    @Override
    public void print(LogLevel level, String tag, String message, Throwable throwable) {
        if (!isRunning) {
            return;
        }
        
        LogItem logItem = new LogItem(level, tag, message, throwable);
        
        // 队列满时，丢弃最旧的日志
        if (!logQueue.offer(logItem)) {
            logQueue.poll();
            logQueue.offer(logItem);
        }
    }

    /**
     * 启动写入线程
     */
    private void startWriteThread() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                BufferedWriter writer = null;
                File currentFile = null;
                
                while (isRunning || !logQueue.isEmpty()) {
                    try {
                        LogItem logItem = logQueue.take();
                        
                        // 检查是否需要切换文件
                        File logFile = fileManager.getCurrentLogFile();
                        if (currentFile == null || !currentFile.equals(logFile)) {
                            closeWriter(writer);
                            currentFile = logFile;
                            writer = new BufferedWriter(new FileWriter(currentFile, true));
                        }
                        
                        // 写入日志
                        String logLine = formatLogLine(logItem);
                        writer.write(logLine);
                        writer.newLine();
                        writer.flush();
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to write log", e);
                    }
                }
                
                closeWriter(writer);
            }
        });
    }

    /**
     * 格式化日志行
     */
    private String formatLogLine(LogItem logItem) {
        StringBuilder sb = new StringBuilder();
        
        // 时间戳
        sb.append(dateFormat.format(new Date(logItem.timestamp)));
        sb.append(" ");
        
        // 进程ID/线程ID
        sb.append(android.os.Process.myPid());
        sb.append("-");
        sb.append(logItem.threadId);
        sb.append(" ");
        
        // 级别
        sb.append(logItem.level.getLabel());
        sb.append("/");
        
        // Tag
        sb.append(logItem.tag);
        sb.append(": ");
        
        // 消息
        sb.append(logItem.message);
        
        // 异常信息
        if (logItem.throwable != null) {
            sb.append("\n");
            sb.append(Log.getStackTraceString(logItem.throwable));
        }
        
        return sb.toString();
    }

    /**
     * 关闭写入器
     */
    private void closeWriter(BufferedWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to close writer", e);
            }
        }
    }

    @Override
    public void release() {
        isRunning = false;
        executorService.shutdown();
    }

    /**
     * 日志项
     */
    private static class LogItem {
        final LogLevel level;
        final String tag;
        final String message;
        final Throwable throwable;
        final long timestamp;
        final long threadId;

        LogItem(LogLevel level, String tag, String message, Throwable throwable) {
            this.level = level;
            this.tag = tag;
            this.message = message;
            this.throwable = throwable;
            this.timestamp = System.currentTimeMillis();
            this.threadId = Thread.currentThread().getId();
        }
    }
}
