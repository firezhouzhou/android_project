# Android 日志模块（Log SDK）使用文档

## 📚 目录

1. [快速开始](#快速开始)
2. [详细配置](#详细配置)
3. [使用示例](#使用示例)
4. [高级功能](#高级功能)
5. [扩展开发](#扩展开发)
6. [性能优化](#性能优化)
7. [最佳实践](#最佳实践)

---

## 🚀 快速开始

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
                .setEnable(true)                          // 启用日志
                .setLogLevel(LogLevel.DEBUG)              // 设置日志级别
                .setGlobalTag("MyApp")                    // 全局 Tag
                .setEnableConsole(true)                   // 启用控制台输出
                .setEnableFile(true)                      // 启用文件输出
                .setMaxFileSize(5 * 1024 * 1024)         // 单文件最大 5MB
                .setRetentionDays(7)                      // 保留 7 天
                .setShowThreadInfo(true)                  // 显示线程信息
                .setShowStackTrace(true)                  // 显示堆栈信息
                .build();
        
        LogManager.getInstance().init(config);
    }
}
```

### 2. 基本使用

```java
// 使用全局 Tag
LogManager.getInstance().d("这是一条调试日志");
LogManager.getInstance().i("这是一条信息日志");
LogManager.getInstance().w("这是一条警告日志");
LogManager.getInstance().e("这是一条错误日志");

// 使用自定义 Tag
LogManager.getInstance().d("MainActivity", "Activity 已创建");
LogManager.getInstance().e("NetworkError", "网络请求失败", exception);
```

---

## ⚙️ 详细配置

### LogConfig 配置项说明

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `enable` | boolean | true | 是否启用日志系统 |
| `enableConsole` | boolean | true | 是否输出到 Logcat |
| `enableFile` | boolean | true | 是否写入文件 |
| `logLevel` | LogLevel | VERBOSE | 最低日志级别 |
| `globalTag` | String | "AppLog" | 全局默认 Tag |
| `logDir` | String | files/logs | 日志文件目录 |
| `maxFileSize` | long | 5MB | 单文件最大大小 |
| `retentionDays` | int | 7 | 日志保留天数 |
| `showThreadInfo` | boolean | true | 显示线程信息 |
| `showStackTrace` | boolean | true | 显示调用堆栈 |
| `stackTraceDepth` | int | 5 | 堆栈深度 |

### Release 版本配置

```java
public class MyApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        LogConfig.Builder builder = new LogConfig.Builder(this)
                .setGlobalTag("MyApp");
        
        if (BuildConfig.DEBUG) {
            // Debug 版本：全功能
            builder.setEnable(true)
                   .setLogLevel(LogLevel.VERBOSE)
                   .setEnableConsole(true)
                   .setEnableFile(true);
        } else {
            // Release 版本：仅记录错误到文件
            builder.setEnable(true)
                   .setLogLevel(LogLevel.ERROR)
                   .setEnableConsole(false)  // 关闭控制台
                   .setEnableFile(true);      // 仅文件记录
        }
        
        LogManager.getInstance().init(builder.build());
    }
}
```

---

## 📖 使用示例

### 1. 基础日志

```java
public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Verbose
        LogManager.getInstance().v(TAG, "onCreate called");
        
        // Debug
        LogManager.getInstance().d(TAG, "初始化完成");
        
        // Info
        LogManager.getInstance().i(TAG, "用户登录成功");
        
        // Warn
        LogManager.getInstance().w(TAG, "网络连接不稳定");
        
        // Error
        LogManager.getInstance().e(TAG, "数据加载失败");
    }
    
    private void handleError() {
        try {
            // 可能抛出异常的代码
            int result = 10 / 0;
        } catch (Exception e) {
            // 记录异常
            LogManager.getInstance().e(TAG, "计算错误", e);
        }
    }
}
```

### 2. JSON 日志

```java
public class ApiService {
    
    private static final String TAG = "ApiService";
    
    public void fetchUserData() {
        String jsonResponse = "{\"name\":\"张三\",\"age\":25,\"city\":\"北京\"}";
        
        // 自动格式化 JSON
        LogManager.getInstance().json(TAG, jsonResponse);
        
        // 输出效果：
        // {
        //     "name": "张三",
        //     "age": 25,
        //     "city": "北京"
        // }
    }
}
```

### 3. XML 日志

```java
public class XmlParser {
    
    private static final String TAG = "XmlParser";
    
    public void parseXml() {
        String xmlData = "<user><name>张三</name><age>25</age></user>";
        
        // 自动格式化 XML
        LogManager.getInstance().xml(TAG, xmlData);
        
        // 输出效果：
        // <user>
        //     <name>张三</name>
        //     <age>25</age>
        // </user>
    }
}
```

### 4. 自定义格式化器

```java
// 自定义对象格式化器
public class UserFormatter implements ILogFormatter {
    
    @Override
    public String format(Object obj) {
        if (obj instanceof User) {
            User user = (User) obj;
            return String.format("User[id=%d, name=%s, email=%s]",
                    user.getId(), user.getName(), user.getEmail());
        }
        return obj.toString();
    }
}

// 使用
User user = new User(1, "张三", "zhangsan@example.com");
LogManager.getInstance().getLogger().object(
    "UserInfo", 
    user, 
    new UserFormatter()
);
```

---

## 🔥 高级功能

### 1. 日志上传

```java
public class LogUploadManager {
    
    private HttpLogUploader uploader;
    
    public void init() {
        // 初始化上传器
        uploader = new HttpLogUploader("https://api.example.com/logs/upload");
    }
    
    /**
     * 上传所有日志文件
     */
    public void uploadAllLogs() {
        LogConfig config = LogManager.getInstance().getConfig();
        LogFileManager fileManager = new LogFileManager(config);
        File[] logFiles = fileManager.getAllLogFiles();
        
        uploader.uploadBatch(logFiles, new ILogUploader.UploadCallback() {
            @Override
            public void onSuccess() {
                Log.i("Upload", "所有日志上传成功");
                // 上传成功后可以删除本地文件
                deleteUploadedLogs(logFiles);
            }
            
            @Override
            public void onFailure(String error) {
                Log.e("Upload", "上传失败: " + error);
            }
            
            @Override
            public void onProgress(int progress) {
                Log.d("Upload", "上传进度: " + progress + "%");
            }
        });
    }
    
    /**
     * 上传单个日志文件
     */
    public void uploadSingleLog(File logFile) {
        uploader.upload(logFile, new ILogUploader.UploadCallback() {
            @Override
            public void onSuccess() {
                Log.i("Upload", "日志上传成功: " + logFile.getName());
            }
            
            @Override
            public void onFailure(String error) {
                Log.e("Upload", "上传失败: " + error);
            }
            
            @Override
            public void onProgress(int progress) {
                // 单文件上传进度
            }
        });
    }
    
    private void deleteUploadedLogs(File[] files) {
        for (File file : files) {
            file.delete();
        }
    }
}
```

### 2. 定时上传日志

```java
public class ScheduledLogUploader {
    
    private ScheduledExecutorService scheduler;
    private HttpLogUploader uploader;
    
    public void startScheduledUpload() {
        scheduler = Executors.newScheduledThreadPool(1);
        uploader = new HttpLogUploader("https://api.example.com/logs/upload");
        
        // 每天凌晨 3 点上传日志
        long initialDelay = calculateInitialDelay();
        long period = 24 * 60 * 60 * 1000; // 24小时
        
        scheduler.scheduleAtFixedRate(
            this::uploadLogs,
            initialDelay,
            period,
            TimeUnit.MILLISECONDS
        );
    }
    
    private void uploadLogs() {
        LogConfig config = LogManager.getInstance().getConfig();
        LogFileManager fileManager = new LogFileManager(config);
        File[] logFiles = fileManager.getAllLogFiles();
        
        uploader.uploadBatch(logFiles, new ILogUploader.UploadCallback() {
            @Override
            public void onSuccess() {
                // 上传成功，删除旧日志
                for (File file : logFiles) {
                    file.delete();
                }
            }
            
            @Override
            public void onFailure(String error) {
                // 上传失败，保留日志
            }
            
            @Override
            public void onProgress(int progress) {
                // 进度更新
            }
        });
    }
    
    private long calculateInitialDelay() {
        Calendar now = Calendar.getInstance();
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, 3);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        
        if (next.before(now)) {
            next.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return next.getTimeInMillis() - now.getTimeInMillis();
    }
    
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
        if (uploader != null) {
            uploader.shutdown();
        }
    }
}
```

### 3. 日志文件管理

```java
public class LogFileHelper {
    
    /**
     * 获取所有日志文件
     */
    public static File[] getAllLogFiles() {
        LogConfig config = LogManager.getInstance().getConfig();
        LogFileManager fileManager = new LogFileManager(config);
        return fileManager.getAllLogFiles();
    }
    
    /**
     * 获取日志总大小
     */
    public static long getTotalLogSize() {
        LogConfig config = LogManager.getInstance().getConfig();
        LogFileManager fileManager = new LogFileManager(config);
        return fileManager.getTotalLogSize();
    }
    
    /**
     * 手动清理过期日志
     */
    public static void cleanOldLogs() {
        LogConfig config = LogManager.getInstance().getConfig();
        LogFileManager fileManager = new LogFileManager(config);
        fileManager.cleanOldLogs();
    }
    
    /**
     * 压缩日志文件
     */
    public static File zipLogFiles() throws IOException {
        File[] logFiles = getAllLogFiles();
        File zipFile = new File(
            LogManager.getInstance().getConfig().getLogDir(),
            "logs_" + System.currentTimeMillis() + ".zip"
        );
        
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        
        for (File logFile : logFiles) {
            FileInputStream fis = new FileInputStream(logFile);
            ZipEntry zipEntry = new ZipEntry(logFile.getName());
            zos.putNextEntry(zipEntry);
            
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            
            fis.close();
            zos.closeEntry();
        }
        
        zos.close();
        return zipFile;
    }
}
```

---

## 🔧 扩展开发

### 1. 自定义日志输出器

```java
/**
 * 自定义数据库日志输出器
 */
public class DatabaseLogPrinter implements ILogPrinter {
    
    private LogDatabase database;
    
    public DatabaseLogPrinter(Context context) {
        this.database = LogDatabase.getInstance(context);
    }
    
    @Override
    public void print(LogLevel level, String tag, String message) {
        print(level, tag, message, null);
    }
    
    @Override
    public void print(LogLevel level, String tag, String message, Throwable throwable) {
        LogEntity entity = new LogEntity();
        entity.setLevel(level.name());
        entity.setTag(tag);
        entity.setMessage(message);
        entity.setTimestamp(System.currentTimeMillis());
        
        if (throwable != null) {
            entity.setException(Log.getStackTraceString(throwable));
        }
        
        database.logDao().insert(entity);
    }
    
    @Override
    public void release() {
        // 关闭数据库连接
    }
}

// 使用自定义输出器
LogConfig config = new LogConfig.Builder(context)
        .build();

List<ILogPrinter> printers = new ArrayList<>();
printers.add(new ConsoleLogPrinter());
printers.add(new FileLogPrinter(config));
printers.add(new DatabaseLogPrinter(context)); // 添加数据库输出

Logger logger = new Logger(config, printers);
```

### 2. 自定义日志格式化器

```java
/**
 * Protobuf 格式化器
 */
public class ProtobufFormatter implements ILogFormatter {
    
    @Override
    public String format(Object obj) {
        if (obj instanceof MessageLite) {
            MessageLite message = (MessageLite) obj;
            return TextFormat.printToString(message);
        }
        return obj.toString();
    }
}

// 使用
UserProto.User user = UserProto.User.newBuilder()
        .setId(1)
        .setName("张三")
        .build();

LogManager.getInstance().getLogger().object(
    "UserProto",
    user,
    new ProtobufFormatter()
);
```

---

## ⚡ 性能优化

### 1. 线程安全保证

- **文件写入**：使用单线程 `ExecutorService`，保证日志顺序
- **队列缓冲**：使用 `LinkedBlockingQueue`，避免阻塞主线程
- **批量写入**：使用 `BufferedWriter`，减少 IO 次数

### 2. 内存优化

```java
// 限制队列大小，防止内存溢出
private static final int QUEUE_SIZE = 1000;

// 队列满时，丢弃最旧的日志
if (!logQueue.offer(logItem)) {
    logQueue.poll();
    logQueue.offer(logItem);
}
```

### 3. 文件分片策略

- **按日期分片**：每天创建新文件
- **按大小分片**：超过 5MB 创建新文件
- **自动清理**：删除 7 天前的日志

### 4. 性能测试

```java
public class LogPerformanceTest {
    
    @Test
    public void testLogPerformance() {
        long startTime = System.currentTimeMillis();
        
        // 写入 10000 条日志
        for (int i = 0; i < 10000; i++) {
            LogManager.getInstance().d("Test", "日志消息 " + i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("写入 10000 条日志耗时: " + duration + "ms");
        System.out.println("平均每条: " + (duration / 10000.0) + "ms");
    }
}
```

---

## 💡 最佳实践

### 1. 日志级别使用建议

| 级别 | 使用场景 | 示例 |
|------|----------|------|
| VERBOSE | 详细的调试信息 | 变量值、方法调用 |
| DEBUG | 调试信息 | 流程跟踪、状态变化 |
| INFO | 重要信息 | 用户操作、业务事件 |
| WARN | 警告信息 | 可恢复的错误、性能问题 |
| ERROR | 错误信息 | 异常、崩溃、严重错误 |

### 2. Tag 命名规范

```java
// 推荐：使用类名作为 Tag
private static final String TAG = "MainActivity";

// 推荐：使用业务模块名
private static final String TAG = "Network";
private static final String TAG = "Database";
private static final String TAG = "Payment";

// 不推荐：使用无意义的 Tag
private static final String TAG = "Test";
private static final String TAG = "Log";
```

### 3. 避免敏感信息泄露

```java
// ❌ 错误：记录敏感信息
LogManager.getInstance().d(TAG, "用户密码: " + password);
LogManager.getInstance().d(TAG, "信用卡号: " + cardNumber);

// ✅ 正确：脱敏处理
LogManager.getInstance().d(TAG, "用户密码: ******");
LogManager.getInstance().d(TAG, "信用卡号: " + maskCardNumber(cardNumber));

private String maskCardNumber(String cardNumber) {
    if (cardNumber.length() < 8) return "****";
    return cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4);
}
```

### 4. 合理使用日志

```java
// ❌ 错误：在循环中大量打印日志
for (int i = 0; i < 10000; i++) {
    LogManager.getInstance().d(TAG, "处理第 " + i + " 条数据");
}

// ✅ 正确：批量记录或采样记录
LogManager.getInstance().d(TAG, "开始处理 10000 条数据");
for (int i = 0; i < 10000; i++) {
    // 处理数据
    if (i % 1000 == 0) {
        LogManager.getInstance().d(TAG, "已处理 " + i + " 条数据");
    }
}
LogManager.getInstance().d(TAG, "数据处理完成");
```

### 5. Release 版本优化

```java
public class MyApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        if (BuildConfig.DEBUG) {
            // Debug 版本：详细日志
            initDebugLog();
        } else {
            // Release 版本：精简日志
            initReleaseLog();
        }
    }
    
    private void initDebugLog() {
        LogConfig config = new LogConfig.Builder(this)
                .setEnable(true)
                .setLogLevel(LogLevel.VERBOSE)
                .setEnableConsole(true)
                .setEnableFile(true)
                .setShowThreadInfo(true)
                .setShowStackTrace(true)
                .build();
        
        LogManager.getInstance().init(config);
    }
    
    private void initReleaseLog() {
        LogConfig config = new LogConfig.Builder(this)
                .setEnable(true)
                .setLogLevel(LogLevel.ERROR)      // 仅记录错误
                .setEnableConsole(false)          // 关闭控制台
                .setEnableFile(true)              // 仅文件记录
                .setShowThreadInfo(false)         // 关闭线程信息
                .setShowStackTrace(false)         // 关闭堆栈信息
                .setRetentionDays(3)              // 仅保留3天
                .build();
        
        LogManager.getInstance().init(config);
    }
}
```

---

## 📊 日志文件格式

### 文件命名规则

```
app_log_20231210_1702188000000.log
│        │        │
│        │        └─ 时间戳（毫秒）
│        └─ 日期（yyyyMMdd）
└─ 前缀
```

### 日志内容格式

```
2023-12-10 15:30:45.123 12345-67890 D/MainActivity: [Thread: main]
    at com.baidu.application.MainActivity.onCreate(MainActivity.kt:25)
用户点击了登录按钮
```

格式说明：
- `2023-12-10 15:30:45.123`：时间戳
- `12345`：进程 ID
- `67890`：线程 ID
- `D`：日志级别
- `MainActivity`：Tag
- `[Thread: main]`：线程名称
- 堆栈信息（可选）
- 日志消息

---

## 🎯 总结

本日志模块具备以下特性：

✅ **功能完整**：支持控制台/文件输出、多级别、格式化  
✅ **性能优异**：异步写入、队列缓冲、批量处理  
✅ **线程安全**：单线程写入、队列隔离  
✅ **易于扩展**：接口化设计、支持自定义输出器  
✅ **生产可用**：文件分片、自动清理、上传支持  
✅ **零依赖**：无第三方库依赖  

适用于各种规模的 Android 项目！
