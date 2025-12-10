package com.baidu.application.logger.printer;

import android.util.Log;
import com.baidu.application.logger.ILogPrinter;
import com.baidu.application.logger.LogLevel;

/**
 * 控制台日志输出器（Logcat）
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class ConsoleLogPrinter implements ILogPrinter {
    
    private static final int MAX_LOG_LENGTH = 4000; // Logcat 单条最大长度

    @Override
    public void print(LogLevel level, String tag, String message) {
        print(level, tag, message, null);
    }

    @Override
    public void print(LogLevel level, String tag, String message, Throwable throwable) {
        if (message == null) {
            message = "";
        }
        
        // 如果有异常，追加异常信息
        if (throwable != null) {
            message = message + "\n" + Log.getStackTraceString(throwable);
        }
        
        // 分段输出（避免超长日志被截断）
        int length = message.length();
        if (length <= MAX_LOG_LENGTH) {
            printLog(level, tag, message);
        } else {
            int start = 0;
            while (start < length) {
                int end = Math.min(start + MAX_LOG_LENGTH, length);
                String segment = message.substring(start, end);
                printLog(level, tag, segment);
                start = end;
            }
        }
    }

    /**
     * 根据级别输出日志
     */
    private void printLog(LogLevel level, String tag, String message) {
        switch (level) {
            case VERBOSE:
                Log.v(tag, message);
                break;
            case DEBUG:
                Log.d(tag, message);
                break;
            case INFO:
                Log.i(tag, message);
                break;
            case WARN:
                Log.w(tag, message);
                break;
            case ERROR:
                Log.e(tag, message);
                break;
            default:
                break;
        }
    }

    @Override
    public void release() {
        // Console 无需释放资源
    }
}
