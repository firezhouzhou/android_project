package com.baidu.application;

import android.app.Application;
import com.baidu.application.logger.LogConfig;
import com.baidu.application.logger.LogLevel;
import com.baidu.application.logger.LogManager;

/**
 * 应用程序入口
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class MyApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 初始化日志模块
        initLogger();
    }
    
    /**
     * 初始化日志模块
     */
    private void initLogger() {
        LogConfig config = new LogConfig.Builder(this)
                .setEnable(true)                          // 启用日志
                .setLogLevel(LogLevel.VERBOSE)            // 设置日志级别
                .setGlobalTag("MyApp")                    // 全局 Tag
                .setEnableConsole(true)                   // 启用控制台输出
                .setEnableFile(true)                      // 启用文件输出
                .setMaxFileSize(5 * 1024 * 1024)         // 单文件最大 5MB
                .setRetentionDays(7)                      // 保留 7 天
                .setShowThreadInfo(true)                  // 显示线程信息
                .setShowStackTrace(false)                 // 关闭堆栈信息（提高性能）
                .build();
        
        LogManager.getInstance().init(config);
        
        // 记录应用启动
        LogManager.getInstance().i("Application", "应用启动成功");
    }
}
