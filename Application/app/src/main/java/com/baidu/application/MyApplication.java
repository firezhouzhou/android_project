package com.baidu.application;

import android.app.Application;
import com.baidu.application.logger.LogConfig;
import com.baidu.application.logger.LogLevel;
import com.baidu.application.logger.LogManager;
import com.baidu.application.analytics.Analytics;
import com.baidu.application.analytics.AnalyticsConfig;

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
        
        // 初始化埋点模块
        initAnalytics();
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
    
    /**
     * 初始化埋点模块
     */
    private void initAnalytics() {
        AnalyticsConfig config = new AnalyticsConfig.Builder(this)
                .setAppKey("demo_app_key_12345")                    // 应用密钥
                .setUploadUrl("https://api.example.com/analytics")  // 上传地址
                .setEnableDebug(true)                               // 调试模式
                .setBatchSize(20)                                   // 批量上传 20 条
                .setUploadInterval(30000)                           // 30 秒上传一次
                .setEnableCompression(true)                         // 启用压缩
                .setEnableEncryption(false)                         // 关闭加密（演示用）
                .setWifiOnly(false)                                 // 允许移动网络上传
                .setRetryCount(3)                                   // 失败重试 3 次
                .build();
        
        Analytics.init(config);
        
        // 记录应用启动事件
        LogManager.getInstance().i("Application", "埋点模块初始化成功");
    }
}
