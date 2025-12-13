# Android 埋点系统架构设计文档

## 1. 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         Application Layer                        │
│                    (业务代码调用埋点 API)                         │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Analytics (统一入口)                        │
│              logEvent() / setUserProperty() / ...                │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      EventProcessor                              │
│         (参数合并、设备信息、traceId、sessionId)                  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Pipeline Chain                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │ Filter   │→ │ Redactor │→ │Compressor│→ │Encryptor │        │
│  │(过滤器)  │  │(脱敏器)  │  │(压缩器)  │  │(加密器)  │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      EventRepository                             │
│              (本地缓存队列 - Room Database)                      │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      UploadScheduler                             │
│         (WorkManager - 批量上传、重试、网络判断)                 │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Uploader (上传器)                           │
│              (HTTP 上传、失败重试、指数退避)                      │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. 核心模块职责

### 2.1 Analytics（统一入口）
- **职责**：提供统一的埋点 API
- **功能**：
  - `logEvent(name, params)` - 记录事件
  - `setUserProperty(key, value)` - 设置用户属性
  - `setUserId(userId)` - 设置用户 ID
  - `setPrivacyEnabled(enabled)` - 隐私开关
  - `flush()` - 立即上传
  - `clearCache()` - 清空缓存

### 2.2 EventProcessor（事件处理器）
- **职责**：事件参数合并与增强
- **功能**：
  - 合并全局参数（appVersion、osVersion、deviceId 等）
  - 生成 traceId / sessionId
  - 添加时间戳
  - 参数校验

### 2.3 Pipeline Chain（处理链）
- **职责**：事件处理流水线
- **组件**：
  1. **Filter**：过滤不需要的事件（如调试事件）
  2. **Redactor**：脱敏敏感信息（手机号、身份证等）
  3. **Compressor**：压缩事件数据（gzip）
  4. **Encryptor**：加密事件数据（AES）

### 2.4 EventRepository（事件仓库）
- **职责**：本地事件存储与管理
- **功能**：
  - 插入事件到本地队列
  - 批量读取待上传事件
  - 删除已上传事件
  - 清空所有事件

### 2.5 UploadScheduler（上传调度器）
- **职责**：管理事件上传时机
- **策略**：
  - 事件数量达到阈值（如 20 条）
  - 时间间隔达到阈值（如 30 秒）
  - WiFi 网络下优先上传
  - 应用退到后台时上传
  - Crash 前同步上传

### 2.6 Uploader（上传器）
- **职责**：执行实际的网络上传
- **功能**：
  - HTTP POST 上传
  - 失败重试（指数退避）
  - 分片上传（大批量数据）
  - 多环境支持（测试/生产）

---

## 3. Pipeline 执行顺序

```
Event Input
    ↓
EventProcessor (参数合并)
    ↓
Filter (过滤)
    ↓
Redactor (脱敏)
    ↓
Compressor (压缩)
    ↓
Encryptor (加密)
    ↓
EventRepository (存储)
    ↓
UploadScheduler (调度)
    ↓
Uploader (上传)
    ↓
Server
```

---

## 4. 数据模型

### 4.1 EventModel（事件模型）

```kotlin
data class EventModel(
    val eventId: String,           // 事件唯一 ID
    val eventName: String,          // 事件名称
    val timestamp: Long,            // 时间戳
    val traceId: String,            // 追踪 ID
    val sessionId: String,          // 会话 ID
    val userId: String?,            // 用户 ID
    val commonParams: Map<String, Any>,  // 公共参数
    val businessParams: Map<String, Any>, // 业务参数
    val status: EventStatus         // 事件状态
)

enum class EventStatus {
    PENDING,    // 待上传
    UPLOADING,  // 上传中
    SUCCESS,    // 上传成功
    FAILED      // 上传失败
}
```

### 4.2 AnalyticsConfig（配置模型）

```kotlin
data class AnalyticsConfig(
    val appKey: String,                    // 应用密钥
    val uploadUrl: String,                 // 上传地址
    val enableDebug: Boolean = false,      // 调试模式
    val enablePrivacy: Boolean = true,     // 隐私开关
    val batchSize: Int = 20,               // 批量上传数量
    val uploadInterval: Long = 30_000,     // 上传间隔（毫秒）
    val maxCacheSize: Int = 1000,          // 最大缓存数量
    val enableEncryption: Boolean = false, // 是否加密
    val enableCompression: Boolean = true, // 是否压缩
    val wifiOnly: Boolean = false,         // 仅 WiFi 上传
    val retryCount: Int = 3                // 重试次数
)
```

---

## 5. 多进程策略

### 5.1 问题分析
- Android 应用可能有多个进程（主进程、推送进程、WebView 进程等）
- 多进程同时写入数据库会导致冲突

### 5.2 解决方案

#### 方案 1：ContentProvider（推荐）
```
主进程 ──┐
         ├──→ ContentProvider ──→ Room Database
子进程 ──┘
```

- 使用 ContentProvider 作为跨进程通信桥梁
- 所有进程通过 ContentProvider 访问数据库
- ContentProvider 运行在主进程，保证数据一致性

#### 方案 2：进程隔离
```
主进程 → Room Database (main.db)
子进程 → Room Database (sub.db)
```

- 每个进程使用独立的数据库文件
- 主进程定期合并子进程数据
- 适用于进程间数据独立的场景

#### 方案 3：AIDL（复杂场景）
- 使用 AIDL 定义跨进程接口
- 主进程提供埋点服务
- 子进程通过 AIDL 调用主进程服务

**本模块采用方案 1（ContentProvider）**

---

## 6. 隐私与合规策略

### 6.1 隐私开关
```kotlin
// 用户关闭隐私开关
Analytics.setPrivacyEnabled(false)

// 执行操作：
// 1. 停止所有事件采集
// 2. 清空本地缓存
// 3. 停止上传任务
// 4. 清除 userId / deviceId
```

### 6.2 PII 脱敏规则

| 数据类型 | 脱敏规则 | 示例 |
|---------|---------|------|
| 手机号 | 保留前 3 后 4 | 138****1234 |
| 身份证 | 保留前 6 后 4 | 110101****1234 |
| 邮箱 | 保留前 3 和域名 | abc***@gmail.com |
| 地址 | 仅保留省市 | 北京市朝阳区*** |
| 姓名 | 仅保留姓氏 | 张** |

### 6.3 GDPR 合规

#### 用户同意机制
```kotlin
// 应用启动时，先获取用户同意
if (!hasUserConsent()) {
    showPrivacyDialog()
}

// 用户同意后才初始化埋点
if (userAgreed) {
    Analytics.init(config)
}
```

#### 数据删除权
```kotlin
// 用户请求删除数据
Analytics.clearAllData()
Analytics.requestServerDataDeletion(userId)
```

#### 数据导出权
```kotlin
// 导出用户数据
val userData = Analytics.exportUserData(userId)
```

---

## 7. 上传策略详解

### 7.1 触发条件（满足任一即触发）

| 条件 | 说明 |
|------|------|
| 事件数量 | 缓存事件 ≥ 20 条 |
| 时间间隔 | 距上次上传 ≥ 30 秒 |
| 网络切换 | 切换到 WiFi |
| 应用后台 | 应用退到后台 |
| 手动触发 | 调用 `flush()` |
| Crash 前 | 捕获到 Crash |

### 7.2 失败重试策略

```
第 1 次失败 → 等待 2 秒 → 重试
第 2 次失败 → 等待 4 秒 → 重试
第 3 次失败 → 等待 8 秒 → 重试
第 4 次失败 → 放弃，标记为失败
```

### 7.3 批量上传

```kotlin
// 每次最多上传 50 条
val events = repository.getPendingEvents(limit = 50)

// 分片上传（如果数据量大）
if (events.size > 100) {
    events.chunked(50).forEach { chunk ->
        uploader.upload(chunk)
    }
}
```

---

## 8. 自动埋点（可选）

### 8.1 View 点击埋点

```kotlin
// 使用 AccessibilityDelegate
view.setOnClickListener {
    // 业务逻辑
    onClick()
    
    // 自动埋点
    Analytics.logEvent("view_click", mapOf(
        "view_id" to view.id,
        "view_class" to view::class.simpleName
    ))
}
```

### 8.2 页面曝光埋点

```kotlin
// 在 Activity/Fragment 生命周期中
override fun onResume() {
    super.onResume()
    Analytics.logEvent("page_view", mapOf(
        "page_name" to this::class.simpleName,
        "enter_time" to System.currentTimeMillis()
    ))
}
```

---

## 9. H5 互通（JSBridge）

### 9.1 Android → H5

```kotlin
// 注入 JavaScript 接口
webView.addJavascriptInterface(JSBridgeHandler(), "AndroidAnalytics")
```

### 9.2 H5 → Android

```javascript
// H5 调用
window.AndroidAnalytics.logEvent(
    "h5_button_click",
    JSON.stringify({ button_id: "submit" })
);
```

### 9.3 Android 处理

```kotlin
@JavascriptInterface
fun logEvent(eventName: String, paramsJson: String) {
    val params = Json.decodeFromString<Map<String, Any>>(paramsJson)
    Analytics.logEvent(eventName, params)
}
```

---

## 10. 调试面板（可选 UI）

### 10.1 功能列表

- 📊 实时事件列表
- 🔍 事件详情查看
- 📤 手动触发上传
- 🗑️ 清空缓存
- ⚙️ 配置修改
- 📈 统计信息（事件数、上传成功率等）

### 10.2 入口

```kotlin
// 摇一摇打开调试面板
sensorManager.registerListener(object : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent) {
        if (isShakeDetected(event)) {
            startActivity(Intent(this, DebugPanelActivity::class.java))
        }
    }
})
```

---

## 11. 性能指标

| 指标 | 目标值 |
|------|--------|
| 单次埋点耗时 | < 1ms |
| 内存占用 | < 10MB |
| 数据库大小 | < 50MB |
| 上传成功率 | > 99% |
| 事件丢失率 | < 0.1% |

---

## 12. 扩展点

### 12.1 自定义 Pipeline

```kotlin
// 添加自定义处理器
Analytics.addPipelineProcessor(object : IPipelineProcessor {
    override fun process(event: EventModel): EventModel {
        // 自定义处理逻辑
        return event
    }
})
```

### 12.2 自定义上传通道

```kotlin
// 替换上传器
Analytics.setUploader(object : IUploader {
    override suspend fun upload(events: List<EventModel>): Result<Unit> {
        // 自定义上传逻辑
    }
})
```

### 12.3 A/B Test 集成

```kotlin
// 根据 A/B Test 配置决定是否上报
val abTestConfig = ABTestManager.getConfig("analytics_sample_rate")
if (Random.nextFloat() < abTestConfig.sampleRate) {
    Analytics.logEvent(eventName, params)
}
```

---

## 13. 技术栈

| 技术 | 用途 |
|------|------|
| Kotlin | 主要开发语言 |
| Room | 本地数据库 |
| WorkManager | 后台任务调度 |
| Kotlinx Serialization | JSON 序列化 |
| OkHttp | 网络请求 |
| Gzip | 数据压缩 |
| AES | 数据加密 |
| ContentProvider | 多进程通信 |

---

## 14. 目录结构

```
analytics/
├── src/main/
│   ├── java/com/baidu/analytics/
│   │   ├── Analytics.kt                 # 统一入口
│   │   ├── AnalyticsConfig.kt           # 配置类
│   │   ├── model/
│   │   │   ├── EventModel.kt            # 事件模型
│   │   │   └── EventStatus.kt           # 事件状态
│   │   ├── processor/
│   │   │   ├── EventProcessor.kt        # 事件处理器
│   │   │   └── SessionManager.kt        # 会话管理
│   │   ├── pipeline/
│   │   │   ├── IPipelineProcessor.kt    # 处理器接口
│   │   │   ├── FilterProcessor.kt       # 过滤器
│   │   │   ├── RedactorProcessor.kt     # 脱敏器
│   │   │   ├── CompressorProcessor.kt   # 压缩器
│   │   │   └── EncryptorProcessor.kt    # 加密器
│   │   ├── repository/
│   │   │   ├── EventRepository.kt       # 事件仓库
│   │   │   ├── EventDao.kt              # Room DAO
│   │   │   ├── EventDatabase.kt         # Room Database
│   │   │   └── EventEntity.kt           # Room Entity
│   │   ├── upload/
│   │   │   ├── IUploader.kt             # 上传器接口
│   │   │   ├── HttpUploader.kt          # HTTP 上传器
│   │   │   ├── UploadScheduler.kt       # 上传调度器
│   │   │   └── UploadWorker.kt          # WorkManager Worker
│   │   ├── bridge/
│   │   │   └── JSBridgeHandler.kt       # JSBridge 处理器
│   │   ├── privacy/
│   │   │   ├── PrivacyManager.kt        # 隐私管理器
│   │   │   └── PIIRedactor.kt           # PII 脱敏器
│   │   ├── debug/
│   │   │   └── DebugPanelActivity.kt    # 调试面板
│   │   └── util/
│   │       ├── DeviceInfoUtil.kt        # 设备信息工具
│   │       ├── NetworkUtil.kt           # 网络工具
│   │       └── CryptoUtil.kt            # 加密工具
│   └── AndroidManifest.xml
└── README.md
```

---

## 15. 总结

本埋点系统是一个**企业级、生产可用**的 Android 埋点框架，具备：

✅ **完整的功能**：事件采集、缓存、上传、重试、脱敏、加密  
✅ **高性能**：异步处理、批量上传、压缩优化  
✅ **高可靠**：失败重试、数据持久化、Crash 前刷盘  
✅ **易扩展**：Pipeline 架构、接口化设计  
✅ **合规性**：隐私开关、PII 脱敏、GDPR 支持  
✅ **多进程**：ContentProvider 跨进程通信  
✅ **易调试**：调试面板、日志输出  

适用于各种规模的 Android 项目，可直接投入生产使用！
