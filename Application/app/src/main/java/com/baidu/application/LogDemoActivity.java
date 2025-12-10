package com.baidu.application;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.baidu.application.logger.LogManager;
import java.io.File;

/**
 * 日志模块使用示例
 * 
 * @author Android架构师
 * @since 1.0.0
 */
public class LogDemoActivity extends AppCompatActivity {
    
    private static final String TAG = "LogDemoActivity";
    private TextView tvOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_demo);
        
        tvOutput = findViewById(R.id.tv_output);
        
        setupButtons();
        
        // 记录 Activity 创建
        LogManager.getInstance().i(TAG, "LogDemoActivity created");
    }

    private void setupButtons() {
        // 基础日志示例
        findViewById(R.id.btn_basic_log).setOnClickListener(v -> testBasicLog());
        
        // JSON 日志示例
        findViewById(R.id.btn_json_log).setOnClickListener(v -> testJsonLog());
        
        // XML 日志示例
        findViewById(R.id.btn_xml_log).setOnClickListener(v -> testXmlLog());
        
        // 异常日志示例
        findViewById(R.id.btn_exception_log).setOnClickListener(v -> testExceptionLog());
        
        // 性能测试
        findViewById(R.id.btn_performance_test).setOnClickListener(v -> testPerformance());
        
        // 查看日志文件
        findViewById(R.id.btn_view_logs).setOnClickListener(v -> viewLogFiles());
    }

    /**
     * 测试基础日志
     */
    private void testBasicLog() {
        LogManager.getInstance().v(TAG, "这是一条 Verbose 日志");
        LogManager.getInstance().d(TAG, "这是一条 Debug 日志");
        LogManager.getInstance().i(TAG, "这是一条 Info 日志");
        LogManager.getInstance().w(TAG, "这是一条 Warn 日志");
        LogManager.getInstance().e(TAG, "这是一条 Error 日志");
        
        updateOutput("✅ 基础日志已输出，请查看 Logcat");
    }

    /**
     * 测试 JSON 日志
     */
    private void testJsonLog() {
        String json = "{\"name\":\"张三\",\"age\":25,\"city\":\"北京\",\"skills\":[\"Java\",\"Kotlin\",\"Android\"]}";
        
        LogManager.getInstance().json(TAG, json);
        
        updateOutput("✅ JSON 日志已输出（已格式化）");
    }

    /**
     * 测试 XML 日志
     */
    private void testXmlLog() {
        String xml = "<user><name>张三</name><age>25</age><city>北京</city></user>";
        
        LogManager.getInstance().xml(TAG, xml);
        
        updateOutput("✅ XML 日志已输出（已格式化）");
    }

    /**
     * 测试异常日志
     */
    private void testExceptionLog() {
        try {
            // 故意制造异常
            int result = 10 / 0;
        } catch (Exception e) {
            LogManager.getInstance().e(TAG, "捕获到异常", e);
            updateOutput("✅ 异常日志已记录\n异常类型: " + e.getClass().getSimpleName());
        }
    }

    /**
     * 性能测试
     */
    private void testPerformance() {
        updateOutput("⏳ 正在进行性能测试...");
        
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            int count = 10000;
            
            for (int i = 0; i < count; i++) {
                LogManager.getInstance().d(TAG, "性能测试日志 #" + i);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            runOnUiThread(() -> {
                String result = String.format(
                    "✅ 性能测试完成\n" +
                    "日志数量: %d 条\n" +
                    "总耗时: %d ms\n" +
                    "平均耗时: %.3f ms/条\n" +
                    "吞吐量: %.0f 条/秒",
                    count,
                    duration,
                    duration / (double) count,
                    count * 1000.0 / duration
                );
                updateOutput(result);
            });
        }).start();
    }

    /**
     * 查看日志文件
     */
    private void viewLogFiles() {
        String logDir = LogManager.getInstance().getConfig().getLogDir();
        File dir = new File(logDir);
        
        if (!dir.exists() || !dir.isDirectory()) {
            updateOutput("❌ 日志目录不存在");
            return;
        }
        
        File[] files = dir.listFiles((d, name) -> name.endsWith(".log"));
        
        if (files == null || files.length == 0) {
            updateOutput("📁 日志目录为空");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("📁 日志文件列表:\n\n");
        
        long totalSize = 0;
        for (File file : files) {
            long size = file.length();
            totalSize += size;
            
            sb.append(String.format(
                "📄 %s\n   大小: %s\n\n",
                file.getName(),
                formatFileSize(size)
            ));
        }
        
        sb.append(String.format(
            "总计: %d 个文件, %s",
            files.length,
            formatFileSize(totalSize)
        ));
        
        updateOutput(sb.toString());
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        }
    }

    /**
     * 更新输出文本
     */
    private void updateOutput(String text) {
        tvOutput.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogManager.getInstance().i(TAG, "LogDemoActivity destroyed");
    }
}
