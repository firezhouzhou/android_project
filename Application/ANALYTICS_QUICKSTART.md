# 埋点系统快速开始指南

## 🚀 5 分钟快速集成

### 步骤 1：初始化（Application）

在 `MyApplication.java` 中添加埋点初始化：

```java
import com.baidu.application.analytics.Analytics;
import com.baidu.application.analytics.AnalyticsConfig;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 初始化日志模块（已有）
        initLogger();
        
        // 初始化埋点模块（新增）
        initAnalytics();
    }
    
    private void initAnalytics() {
        AnalyticsConfig config = new AnalyticsConfig.Builder(this)
                .setAppKey("your_app_key_here")
                .setUploadUrl("https://api.example.com/analytics")
                .setEnableDebug(true)  // 调试模式
                .setBatchSize(20)      // 批量上传 20 条
                .setUploadInterval(30000)  // 30 秒上传一次
                .build();
        
        Analytics.init(config);
    }
}
```

### 步骤 2：添加跳转按钮（MainActivity）

在 `MainActivity.kt` 中添加埋点 Demo 按钮：

```kotlin
private fun setupButton() {
    // 现有的 Widget Demo 按钮
    val btnWidgetDemo = findViewById<Button>(R.id.btn_widget_demo)
    btnWidgetDemo.setOnClickListener {
        val intent = Intent(this, WidgetDemoActivity::class.java)
        startActivity(intent)
    }
    
    // 现有的 Log Demo 按钮
    val btnLogDemo = findViewById<Button>(R.id.btn_log_demo)
    btnLogDemo.setOnClickListener {
        val intent = Intent(this, LogDemoActivity::class.java)
        startActivity(intent)
    }
    
    // 新增：Analytics Demo 按钮
    val btnAnalyticsDemo = findViewById<Button>(R.id.btn_analytics_demo)
    btnAnalyticsDemo.setOnClickListener {
        val intent = Intent(this, AnalyticsDemoActivity::class.java)
        startActivity(intent)
    }
}
```

### 步骤 3：更新布局文件（activity_main.xml）

在 `activity_main.xml` 中添加按钮：

```xml
<Button
    android:id="@+id/btn_analytics_demo"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="查看埋点模块 Demo"
    android:textSize="16sp"
    app:layout_constraintTop_toBottomOf="@id/btn_log_demo"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    android:layout_marginTop="16dp"/>
```

### 步骤 4：注册 Activity（AndroidManifest.xml）

在 `AndroidManifest.xml` 中注册 `AnalyticsDemoActivity`：

```xml
<activity
    android:name=".AnalyticsDemoActivity"
    android:exported="false"
    android:label="埋点模块测试" />
```

### 步骤 5：运行测试

1. 运行应用
2. 点击"查看埋点模块 Demo"按钮
3. 进入埋点测试界面
4. 点击各个测试按钮，查看效果

---

## 📱 基础使用示例

### 1. 记录简单事件

```kotlin
Analytics.logEvent("button_click")
```

### 2. 记录带参数事件

```kotlin
Analytics.logEvent("page_view", mapOf(
    "page_name" to "MainActivity",
    "enter_time" to System.currentTimeMillis().toString()
))
```

### 3. 设置用户信息

```kotlin
Analytics.setUserId("user_12345")
Analytics.setUserProperty("vip_level", "gold")
```

### 4. 立即上传

```kotlin
Analytics.flush()
```

---

## 🔍 查看日志

在 Logcat 中过滤以下 Tag：

- `Analytics` - 埋点系统日志
- `UploadScheduler` - 上传调度日志
- `EventProcessor` - 事件处理日志

---

## ✅ 验证清单

- [ ] MyApplication 中已初始化埋点模块
- [ ] MainActivity 中已添加跳转按钮
- [ ] activity_main.xml 中已添加按钮
- [ ] AndroidManifest.xml 中已注册 AnalyticsDemoActivity
- [ ] 应用可以正常运行
- [ ] 可以进入埋点测试界面
- [ ] 可以看到测试按钮和输出结果

---

## 📚 下一步

- 阅读 [ANALYTICS_README.md](ANALYTICS_README.md) 了解详细功能
- 阅读 [ANALYTICS_ARCHITECTURE.md](ANALYTICS_ARCHITECTURE.md) 了解架构设计
- 根据业务需求自定义配置和扩展

---

## 💡 提示

1. **调试模式**：开发时建议开启 `setEnableDebug(true)`
2. **上传地址**：需要替换为实际的后端接口地址
3. **应用密钥**：需要替换为实际的应用密钥
4. **隐私合规**：上线前确保获取用户同意
