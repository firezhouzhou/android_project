# Android 埋点系统（Analytics SDK）

## 📋 目录

- [简介](#简介)
- [功能特性](#功能特性)
- [快速开始](#快速开始)
- [详细使用](#详细使用)
- [高级功能](#高级功能)
- [API 文档](#api-文档)
- [常见问题](#常见问题)

---

## 简介

这是一个**企业级 Android 埋点系统**，提供完整的事件采集、缓存、上传、重试、脱敏、加密等功能。

### 核心特性

✅ **统一埋点入口** - 简单易用的 API  
✅ **本地缓存队列** - Room 数据库持久化  
✅ **批量上传** - 支持批量、分片上传  
✅ **失败重试** - 指数退避重试策略  
✅ **隐私保护** - PII 脱敏、隐私开关  
✅ **数据压缩** - GZIP 压缩节省流量  
✅ **数据加密** - AES 加密保护数据  
✅ **多进程支持** - ContentProvider 跨进程通信  
✅ **H5 互通** - JSBridge 支持  
✅ **易扩展** - Pipeline 架构，可插拔  

---

## 功能特性

### 1. 事件采集

- 原生埋点（API 调用）
- 自动埋点（可选）
- H5 埋点（JSBridge）
- 批量埋点

### 2. 数据处理

- 参数合并（全局参数 + 业务参数）
- 设备信息自动添加
- traceId / sessionId 自动管理
- PII 脱敏（手机号、身份证等）

### 3. 本地存储

- Room 数据库持久化
- 事件状态管理
- 过期事件自动清理
- 多进程安全

### 4. 上传策略

- 批量上传（默认 20 条）
- 定时上传（默认 30 秒）
- WiFi 优先上传
- 失败重试（最多 3 次）
- 指数退避策略

### 5. 隐私合规

- 隐私开关
- PII 脱敏
- 数据加密
- GDPR 合规

---

## 快速开始

### 1. 初始化

在 `Application` 中初始化：

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val config = AnalyticsConfig.Builder(this)
            .setAppKey("your_app_key")
            .setUploadUrl("https://api.example.com/analytics")
            .setEnableDebug(BuildConfig.DEBUG)
            .setBatchSize(20)
            .setUploadInterval(30_000)
            .build()
        
        Analytics.init(config)
    }
}
```

### 2. 记录事件

```kotlin
// 基础事件
Analytics.logEvent("button_click")

// 带参数事件
Analytics.logEvent("button_click", mapOf(
    "button_id" to "submit",
    "page" to "login"
))
```

### 3. 设置用户信息

```kotlin
// 设置用户 ID
Analytics.setUserId("user_12345")

// 设置用户属性
Analytics.setUserProperty("vip_level", "gold")
Analytics.setUserProperty("age", "25")
```

---

## 详细使用

### 配置选项

```kotlin
val config = AnalyticsConfig.Builder(context)
    .setAppKey("your_app_key")              // 必填：应用密钥
    .setUploadUrl("https://...")            // 必填：上传地址
    .setEnableDebug(true)                   // 调试模式
    .setEnablePrivacy(true)                 // 隐私保护
    .setBatchSize(20)                       // 批量上传数量
    .setUploadInterval(30_000)              // 上传间隔（毫秒）
    .setMaxCacheSize(1000)                  // 最大缓存数量
    .setEnableEncryption(false)             // 是否加密
    .setEnableCompression(true)             // 是否压缩
    .setWifiOnly(false)                     // 仅 WiFi 上传
    .setRetryCount(3)                       // 重试次数
    .setEnableAutoTrack(false)              // 自动埋点
    .setEnableCrashTrack(true)              // Crash 追踪
    .setSessionTimeout(30 * 60 * 1000)      // 会话超时
    .build()
```

### 事件参数

```kotlin
// 简单参数
Analytics.logEvent("page_view", mapOf(
    "page_name" to "MainActivity",
    "enter_time" to System.currentTimeMillis().toString()
))

// 复杂参数
Analytics.logEvent("purchase", mapOf(
    "product_id" to "12345",
    "product_name" to "iPhone 15",
    "price" to "5999",
    "quantity" to "1",
    "payment_method" to "alipay"
))
```

### 用户属性

```kotlin
// 单个属性
Analytics.setUserProperty("gender", "male")
Analytics.setUserProperty("city", "Beijing")

// 批量属性
Analytics.setUserProperties(mapOf(
    "vip_level" to "gold",
    "age" to "25",
    "register_date" to "2024-01-01"
))
```

### 立即上传

```kotlin
// 触发立即上传
Analytics.flush()
```

### 清空数据

```kotlin
// 清空缓存
Analytics.clearCache()

// 清空所有数据（包括用户信息）
Analytics.clearAllData()
```

### 隐私开关

```kotlin
// 关闭隐私保护（停止采集并清空数据）
Analytics.setPrivacyEnabled(false)

// 开启隐私保护
Analytics.setPrivacyEnabled(true)
```

---

## 高级功能

### 1. 自定义 Pipeline 处理器

```kotlin
// 自定义处理器
class CustomProcessor : IPipelineProcessor {
    override fun process(event: EventModel): EventModel? {
        // 自定义处理逻辑
        return event
    }
}

// 添加处理器
Analytics.addPipelineProcessor(CustomProcessor())
```

### 2. 自定义上传器

```kotlin
// 自定义上传器
class CustomUploader : IUploader {
    override suspend fun upload(events: List<EventModel>): Result<Unit> {
        // 自定义上传逻辑
        return Result.success(Unit)
    }
}

// 设置上传器
Analytics.setUploader(CustomUploader())
```

### 3. H5 互通（JSBridge）

#### Android 端

```kotlin
// 在 WebView 中注入 JSBridge
webView.addJavascriptInterface(JSBridgeHandler(), "AndroidAnalytics")
```

#### H5 端

```javascript
// H5 调用埋点
window.AndroidAnalytics.logEvent(
    "h5_button_click",
    JSON.stringify({
        button_id: "submit",
        page: "login"
    })
);

// 设置用户 ID
window.AndroidAnalytics.setUserId("user_12345");

// 获取会话 ID
const sessionId = window.AndroidAnalytics.getSessionId();
```

### 4. Java 适配器

```java
// Java 代码中使用
AnalyticsJavaAdapter.init(config);

// 记录事件
AnalyticsJavaAdapter.logEvent("button_click");

// 带参数
Map<String, String> params = new HashMap<>();
params.put("button_id", "submit");
AnalyticsJavaAdapter.logEvent("button_click", params);

// 设置用户 ID
AnalyticsJavaAdapter.setUserId("user_12345");
```

---

## API 文档

### Analytics

| 方法 | 说明 |
|------|------|
| `init(config)` | 初始化埋点系统 |
| `logEvent(name, params)` | 记录事件 |
| `setUserId(userId)` | 设置用户 ID |
| `setUserProperty(key, value)` | 设置用户属性 |
| `setUserProperties(properties)` | 批量设置用户属性 |
| `setPrivacyEnabled(enabled)` | 设置隐私开关 |
| `flush()` | 立即上传 |
| `clearCache()` | 清空缓存 |
| `clearAllData()` | 清空所有数据 |
| `getSessionId()` | 获取会话 ID |
| `getTraceId()` | 获取追踪 ID |
| `getEventStats()` | 获取事件统计 |

### AnalyticsConfig.Builder

| 方法 | 说明 | 默认值 |
|------|------|--------|
| `setAppKey(key)` | 设置应用密钥 | 必填 |
| `setUploadUrl(url)` | 设置上传地址 | 必填 |
| `setEnableDebug(enable)` | 调试模式 | false |
| `setEnablePrivacy(enable)` | 隐私保护 | true |
| `setBatchSize(size)` | 批量上传数量 | 20 |
| `setUploadInterval(interval)` | 上传间隔（毫秒） | 30000 |
| `setMaxCacheSize(size)` | 最大缓存数量 | 1000 |
| `setEnableEncryption(enable)` | 是否加密 | false |
| `setEnableCompression(enable)` | 是否压缩 | true |
| `setWifiOnly(wifiOnly)` | 仅 WiFi 上传 | false |
| `setRetryCount(count)` | 重试次数 | 3 |

---

## 常见问题

### 1. 如何查看日志？

开启调试模式后，可在 Logcat 中查看日志：

```kotlin
.setEnableDebug(true)
```

过滤 Tag：`Analytics`、`UploadScheduler`

### 2. 事件什么时候上传？

满足以下任一条件时触发上传：

- 事件数量 ≥ 批量大小（默认 20 条）
- 距上次上传 ≥ 上传间隔（默认 30 秒）
- 切换到 WiFi 网络
- 应用退到后台
- 手动调用 `flush()`

### 3. 如何处理敏感信息？

系统会自动脱敏以下字段：

- `phone` - 手机号
- `id_card` - 身份证
- `email` - 邮箱
- `address` - 地址
- `name` - 姓名

### 4. 如何支持多进程？

系统使用 ContentProvider 实现多进程通信，无需额外配置。

### 5. 如何自定义上传地址？

```kotlin
.setUploadUrl("https://your-api.com/analytics")
```

### 6. 如何关闭埋点？

```kotlin
Analytics.setPrivacyEnabled(false)
```

### 7. 数据存储在哪里？

数据存储在应用私有目录的 Room 数据库中：

```
/data/data/your.package.name/databases/analytics_events.db
```

### 8. 如何导出数据？

```kotlin
val stats = Analytics.getEventStats()
```

---

## 性能指标

| 指标 | 目标值 |
|------|--------|
| 单次埋点耗时 | < 1ms |
| 内存占用 | < 10MB |
| 数据库大小 | < 50MB |
| 上传成功率 | > 99% |
| 事件丢失率 | < 0.1% |

---

## 技术栈

- **语言**：Kotlin + Java
- **数据库**：Room
- **任务调度**：WorkManager
- **序列化**：Kotlinx Serialization
- **压缩**：GZIP
- **加密**：AES

---

## 目录结构

```
analytics/
├── Analytics.kt                    # 统一入口
├── AnalyticsConfig.kt              # 配置类
├── AnalyticsJavaAdapter.java       # Java 适配器
├── model/
│   ├── EventModel.kt               # 事件模型
│   └── EventStatus.kt              # 事件状态
├── processor/
│   ├── EventProcessor.kt           # 事件处理器
│   └── SessionManager.kt           # 会话管理
├── pipeline/
│   ├── IPipelineProcessor.kt       # 处理器接口
│   ├── FilterProcessor.kt          # 过滤器
│   ├── RedactorProcessor.kt        # 脱敏器
│   ├── CompressorProcessor.kt      # 压缩器
│   └── EncryptorProcessor.kt       # 加密器
├── repository/
│   ├── EventRepository.kt          # 事件仓库
│   ├── EventDao.kt                 # Room DAO
│   ├── EventDatabase.kt            # Room Database
│   └── EventEntity.kt              # Room Entity
├── upload/
│   ├── IUploader.kt                # 上传器接口
│   ├── HttpUploader.kt             # HTTP 上传器
│   ├── UploadScheduler.kt          # 上传调度器
│   └── UploadWorker.kt             # WorkManager Worker
├── bridge/
│   └── JSBridgeHandler.kt          # JSBridge 处理器
├── privacy/
│   └── PIIRedactor.kt              # PII 脱敏器
└── util/
    ├── DeviceInfoUtil.kt           # 设备信息工具
    ├── NetworkUtil.kt              # 网络工具
    ├── CompressionUtil.kt          # 压缩工具
    └── CryptoUtil.kt               # 加密工具
```

---

## 许可证

本项目仅供学习和参考使用。

---

## 联系方式

如有问题，请联系开发团队。
