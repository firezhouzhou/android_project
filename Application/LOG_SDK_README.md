# Android 日志模块（Log SDK）

> 🚀 企业级 Android 日志框架，支持控制台/文件输出、异步写入、日志分片、自动清理、日志上传等功能

[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![Language](https://img.shields.io/badge/language-Java-orange.svg)](https://www.java.com)
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

---

## ✨ 特性

- ✅ **多输出方式**：支持 Logcat 控制台 + 文件输出
- ✅ **异步写入**：不阻塞主线程，高性能队列缓冲
- ✅ **日志分片**：按日期/大小自动分片，单文件最大 5MB
- ✅ **自动清理**：自动删除过期日志（默认 7 天）
- ✅ **多级别**：支持 VERBOSE / DEBUG / INFO / WARN / ERROR
- ✅ **格式化**：内置 JSON / XML 美化输出
- ✅ **线程安全**：单线程写入 + 队列隔离
- ✅ **易扩展**：接口化设计，支持自定义输出器
- ✅ **零依赖**：无第三方库依赖
- ✅ **生产可用**：Release 版本自动降级

---

## 📦 快速开始

### 1. 在 Application 中初始化

```java
public class MyApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // 方式1：使用默认配置
        LogManager.getInstance().init(this);
        
        // 方式2：自定义配置
        LogConfig config = new LogConfig.Builder(this)
                .setEnable(true)
                .setLogLevel(LogLevel.DEBUG)
                .setGlobalTag("MyApp")
                .setEnableConsole(true)
                .setEnableFile(true)
                .setMaxFileSize(5 * 1024 * 1024)  // 5MB
                .setRetentionDays(7)
                .build();
        
        LogManager.getInstance().init(config);
    }
}
```

### 2. 使用日志

```java
// 基础日志
LogManager.getInstance().d("MainActivity", "Activity 已创建");
LogManager.getInstance().e("NetworkError", "请求失败", exception);

// JSON 日志（自动格式化）
String json = "{\"name\":\"张三\",\"age\":25}";
LogManager.getInstance().json("UserInfo", json);

// XML 日志（自动格式化）
String xml = "<user><name>张三</name></user>";
LogManager.getInstance().xml("UserData", xml);
```

---

## 📚 文档

### 核心文档

| 文档 | 说明 |
|------|------|
| [使用文档](LOG_SDK_USAGE.md) | 详细使用指南、API 说明、最佳实践 |
| [架构设计](LOG_SDK_ARCHITECTURE.md) | 架构设计、类图、流程图、并发设计 |
| [高级功能](LOG_SDK_ADVANCED.md) | 日志加密、Crash 捕获、ANR 监控、远程开关 |

### 快速链接

- [基础使用](#基础使用)
- [配置说明](#配置说明)
- [日志级别](#日志级别)
- [文件管理](#文件管理)
- [日志上传](#日志上传)
- [性能优化](#性能优化)

---

## 🎯 基础使用

### 日志级别

```java
// Verbose（详细）
LogManager.getInstance().v("Tag", "详细信息");

// Debug（调试）
LogManager.getInstance().d("Tag", "调试信息");

// Info（信息）
LogManager.getInstance().i("Tag", "重要信息");

// Warn（警告）
LogManager.getInstance().w("Tag", "警告信息");

// Error（错误）
LogManager.getInstance().e("Tag", "错误信息");
LogManager.getInstance().e("Tag", "错误信息", exception);
```

### 格式化输出

```java
// JSON 格式化
String json = "{\"name\":\"张三\",\"age\":25,\"city\":\"北京\"}";
LogManager.getInstance().json("UserInfo", json);

// 输出：
// {
//     "name": "张三",
//     "age": 25,
//     "city": "北京"
// }

// XML 格式化
String xml = "<user><name>张三</name><age>25</age></user>";
LogManager.getInstance().xml("UserData", xml);

// 输出：
// <user>
//     <name>张三</name>
//     <age>25</age>
// </user>
```

### 自定义格式化器

```java
public class UserFormatter implements ILogFormatter {
    @Override
    public String format(Object obj) {
        if (obj instanceof User) {
            User user = (User) obj;
            return String.format("User[id=%d, name=%s]", user.getId(), user.getName());
        }
        return obj.toString();
    }
}

// 使用
User user = new User(1, "张三");
LogManager.getInstance().getLogger().object("UserInfo", user, new UserFormatter());
```

---

## ⚙️ 配置说明

### LogConfig 配置项

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `enable` | boolean | true | 是否启用日志 |
| `enableConsole` | boolean | true | 是否输出到 Logcat |
| `enableFile` | boolean | true | 是否写入文件 |
| `logLevel` | LogLevel | VERBOSE | 最低日志级别 |
| `globalTag` | String | "AppLog" | 全局默认 Tag |
| `logDir` | String | files/logs | 日志文件目录 |
| `maxFileSize` | long | 5MB | 单文件最大大小 |
| `retentionDays` | int | 7 | 日志保留天数 |
| `showThreadInfo` | boolean | true | 显示线程信息 |
| `showStackTrace` | boolean | true | 显示调用堆栈 |

### Release 版本配置

```java
LogConfig.Builder builder = new LogConfig.Builder(this);

if (BuildConfig.DEBUG) {
    // Debug 版本：全功能
    builder.setEnable(true)
           .setLogLevel(LogLevel.VERBOSE)
           .setEnableConsole(true)
           .setEnableFile(true);
} else {
    // Release 版本：仅记录错误
    builder.setEnable(true)
           .setLogLevel(LogLevel.ERROR)
           .setEnableConsole(false)  // 关闭控制台
           .setEnableFile(true);      // 仅文件记录
}

LogManager.getInstance().init(builder.build());
```

---

## 📁 文件管理

### 日志文件命名

```
app_log_20231210_1702188000000.log
│        │        │
│        │        └─ 时间戳（毫秒）
│        └─ 日期（yyyyMMdd）
└─ 前缀
```

### 日志文件操作

```java
// 获取所有日志文件
LogConfig config = LogManager.getInstance().getConfig();
LogFileManager fileManager = new LogFileManager(config);
File[] logFiles = fileManager.getAllLogFiles();

// 获取日志总大小
long totalSize = fileManager.getTotalLogSize();

// 手动清理过期日志
fileManager.cleanOldLogs();
```

### 日志文件格式

```
2023-12-10 15:30:45.123 12345-67890 D/MainActivity: [Thread: main]
    at com.baidu.application.MainActivity.onCreate(MainActivity.kt:25)
用户点击了登录按钮
```

---

## 📤 日志上传

### 基础上传

```java
// 初始化上传器
HttpLogUploader uploader = new HttpLogUploader("https://api.example.com/logs/upload");

// 上传单个文件
uploader.upload(logFile, new ILogUploader.UploadCallback() {
    @Override
    public void onSuccess() {
        Log.i("Upload", "上传成功");
    }
    
    @Override
    public void onFailure(String error) {
        Log.e("Upload", "上传失败: " + error);
    }
    
    @Override
    public void onProgress(int progress) {
        Log.d("Upload", "进度: " + progress + "%");
    }
});

// 批量上传
File[] logFiles = fileManager.getAllLogFiles();
uploader.uploadBatch(logFiles, callback);
```

### 定时上传

```java
// 每天凌晨 3 点自动上传
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
scheduler.scheduleAtFixedRate(
    () -> uploadLogs(),
    calculateInitialDelay(),
    24 * 60 * 60 * 1000,
    TimeUnit.MILLISECONDS
);
```

---

## ⚡ 性能优化

### 性能指标

| 指标 | 数值 | 说明 |
|------|------|------|
| 单条日志耗时 | < 0.1ms | 主线程仅写入队列 |
| 10000条日志 | < 500ms | 异步写入文件 |
| 内存占用 | < 5MB | 队列大小 1000 |
| 文件写入速度 | > 10MB/s | BufferedWriter |
| 队列吞吐量 | > 20000/s | LinkedBlockingQueue |

### 优化建议

1. **避免在循环中大量打印日志**
   ```java
   // ❌ 错误
   for (int i = 0; i < 10000; i++) {
       LogManager.getInstance().d("Tag", "处理第 " + i + " 条");
   }
   
   // ✅ 正确
   LogManager.getInstance().d("Tag", "开始处理 10000 条数据");
   for (int i = 0; i < 10000; i++) {
       // 处理数据
       if (i % 1000 == 0) {
           LogManager.getInstance().d("Tag", "已处理 " + i + " 条");
       }
   }
   ```

2. **Release 版本降级**
   - 关闭控制台输出
   - 提高日志级别（仅 ERROR）
   - 减少保留天数

3. **避免敏感信息泄露**
   ```java
   // ❌ 错误
   LogManager.getInstance().d("Tag", "密码: " + password);
   
   // ✅ 正确
   LogManager.getInstance().d("Tag", "密码: ******");
   ```

---

## 🔧 扩展开发

### 自定义输出器

```java
// 1. 实现 ILogPrinter 接口
public class DatabaseLogPrinter implements ILogPrinter {
    @Override
    public void print(LogLevel level, String tag, String message) {
        // 写入数据库
    }
    
    @Override
    public void release() {
        // 释放资源
    }
}

// 2. 添加到 Logger
List<ILogPrinter> printers = new ArrayList<>();
printers.add(new ConsoleLogPrinter());
printers.add(new FileLogPrinter(config));
printers.add(new DatabaseLogPrinter());

Logger logger = new Logger(config, printers);
```

### 自定义上传器

```java
// 1. 实现 ILogUploader 接口
public class OSSLogUploader implements ILogUploader {
    @Override
    public void upload(File logFile, UploadCallback callback) {
        // 上传到 OSS
    }
}

// 2. 使用
OSSLogUploader uploader = new OSSLogUploader();
uploader.upload(logFile, callback);
```

---

## 🎨 高级功能

### 1. 日志加密（AES）

```java
// 使用加密日志输出器
String encryptionKey = "your-32-byte-key";
EncryptedFileLogPrinter printer = new EncryptedFileLogPrinter(config, encryptionKey);

// 解密日志
LogDecryptor decryptor = new LogDecryptor(encryptionKey);
decryptor.decryptFile(encryptedFile, decryptedFile);
```

### 2. Crash 捕获

```java
// 在 Application 中初始化
CrashHandler.init(this);

// 自动捕获并记录所有 Crash
```

### 3. ANR 监控

```java
// 启动 ANR 监控
ANRWatcher watcher = new ANRWatcher();
watcher.start();

// 主线程卡顿检测
BlockDetector detector = new BlockDetector();
detector.start();
```

### 4. Logcat 抓取

```java
// 实时抓取
LogcatCapture capture = new LogcatCapture();
capture.start(outputFile);

// 抓取快照
LogcatCapture.captureLogcat(outputFile, 0);

// 按级别过滤
LogcatCapture.captureLogcatByLevel(outputFile, "E");
```

### 5. 远程开关

```java
// 初始化远程配置
IRemoteConfig remoteConfig = new HttpRemoteConfig();

// 动态日志管理
DynamicLogManager dynamicLogger = new DynamicLogManager(
    LogManager.getInstance(),
    remoteConfig
);

// 根据远程配置决定是否记录日志
dynamicLogger.d("Tag", "消息");
```

---

## 📊 架构设计

### 整体架构

```
Application
    │
    ▼
LogManager (单例)
    │
    ▼
Logger (核心)
    │
    ├─> ConsoleLogPrinter (Logcat)
    │
    └─> FileLogPrinter (异步文件)
            │
            └─> LogFileManager (分片、清理)
```

### 设计模式

- **单例模式**：LogManager 全局唯一
- **建造者模式**：LogConfig 配置构建
- **策略模式**：ILogPrinter 输出策略
- **工厂模式**：Printer 创建管理

### 并发设计

- **队列缓冲**：LinkedBlockingQueue（1000）
- **单线程写入**：ExecutorService
- **异步刷盘**：BufferedWriter
- **线程安全**：synchronized + volatile

---

## 📝 最佳实践

### 1. Tag 命名规范

```java
// ✅ 推荐：使用类名
private static final String TAG = "MainActivity";

// ✅ 推荐：使用模块名
private static final String TAG = "Network";
private static final String TAG = "Database";

// ❌ 不推荐
private static final String TAG = "Test";
```

### 2. 日志级别使用

| 级别 | 使用场景 |
|------|----------|
| VERBOSE | 详细的调试信息（变量值、方法调用） |
| DEBUG | 调试信息（流程跟踪、状态变化） |
| INFO | 重要信息（用户操作、业务事件） |
| WARN | 警告信息（可恢复的错误、性能问题） |
| ERROR | 错误信息（异常、崩溃、严重错误） |

### 3. 性能考虑

- ✅ 避免在循环中大量打印
- ✅ Release 版本降级
- ✅ 合理设置保留天数
- ✅ 定期清理日志文件

### 4. 安全考虑

- ✅ 不记录敏感信息（密码、Token）
- ✅ 使用加密（生产环境）
- ✅ 定期上传并删除本地日志
- ✅ 使用 ProGuard 混淆

---

## 🔍 故障排查

### 日志未输出

1. 检查是否初始化：`LogManager.getInstance().init(config)`
2. 检查日志级别：`config.getLogLevel()`
3. 检查是否启用：`config.isEnable()`

### 文件未生成

1. 检查文件输出是否启用：`config.isEnableFile()`
2. 检查目录权限：`config.getLogDir()`
3. 查看错误日志：`adb logcat | grep FileLogPrinter`

### 性能问题

1. 检查队列大小：默认 1000
2. 检查文件大小限制：默认 5MB
3. 减少日志输出频率

---

## 📄 许可证

```
Copyright 2023 Android架构师

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

## 📮 联系方式

- **作者**：Android架构师
- **版本**：v1.0.0
- **更新日期**：2023-12-10

---

## 🎉 致谢

感谢所有为本项目做出贡献的开发者！

---

**⭐ 如果这个项目对你有帮助，请给个 Star！**
