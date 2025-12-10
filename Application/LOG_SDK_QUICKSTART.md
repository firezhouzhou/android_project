# 日志模块快速使用指南

## 🚀 已完成的集成工作

### ✅ 1. 代码集成
- ✅ 创建了 `MyApplication.java` 并初始化日志模块
- ✅ 在 `AndroidManifest.xml` 中注册了 Application 和 LogDemoActivity
- ✅ 在 `MainActivity` 中添加了跳转按钮
- ✅ 创建了 `activity_log_demo.xml` 布局文件

### ✅ 2. 功能说明

#### LogDemoActivity 包含以下测试功能：

| 按钮 | 功能 | 说明 |
|------|------|------|
| **测试基础日志** | 输出 5 个级别的日志 | VERBOSE/DEBUG/INFO/WARN/ERROR |
| **测试 JSON 日志** | 输出格式化的 JSON | 自动美化 JSON 格式 |
| **测试 XML 日志** | 输出格式化的 XML | 自动美化 XML 格式 |
| **测试异常日志** | 捕获并记录异常 | 包含完整堆栈信息 |
| **性能测试** | 写入 10000 条日志 | 测试吞吐量和性能 |
| **查看日志文件** | 显示日志文件列表 | 查看文件大小和数量 |

---

## 📱 使用步骤

### 1. 运行应用

```bash
# 在 Android Studio 中点击运行按钮，或使用命令：
./gradlew installDebug
```

### 2. 操作流程

1. **启动应用** → 进入 MainActivity
2. **点击"查看日志模块 Demo"按钮** → 跳转到 LogDemoActivity
3. **点击各个测试按钮** → 查看日志输出效果
4. **查看 Logcat** → 实时查看日志输出
5. **点击"查看日志文件"** → 查看文件日志信息

### 3. 查看日志

#### 方式1：Logcat（实时查看）

```bash
# 在 Android Studio 的 Logcat 中过滤：
Tag: LogDemoActivity
或
Tag: MyApp
```

#### 方式2：文件日志（持久化）

```bash
# 日志文件位置：
/data/data/com.baidu.application/files/logs/

# 使用 adb 查看：
adb shell
cd /data/data/com.baidu.application/files/logs/
ls -lh
cat app_log_*.log
```

---

## 🎯 测试示例

### 1. 基础日志测试

点击"测试基础日志"按钮后，Logcat 会输出：

```
V/LogDemoActivity: [Thread: main] 这是一条 Verbose 日志
D/LogDemoActivity: [Thread: main] 这是一条 Debug 日志
I/LogDemoActivity: [Thread: main] 这是一条 Info 日志
W/LogDemoActivity: [Thread: main] 这是一条 Warn 日志
E/LogDemoActivity: [Thread: main] 这是一条 Error 日志
```

### 2. JSON 日志测试

点击"测试 JSON 日志"按钮后，输出格式化的 JSON：

```json
{
    "name": "张三",
    "age": 25,
    "city": "北京",
    "skills": [
        "Java",
        "Kotlin",
        "Android"
    ]
}
```

### 3. 性能测试

点击"性能测试"按钮后，界面会显示：

```
✅ 性能测试完成
日志数量: 10000 条
总耗时: 450 ms
平均耗时: 0.045 ms/条
吞吐量: 22222 条/秒
```

### 4. 查看日志文件

点击"查看日志文件"按钮后，界面会显示：

```
📁 日志文件列表:

📄 app_log_20231210_1702188000000.log
   大小: 2.35 MB

📄 app_log_20231210_1702193000000.log
   大小: 1.87 MB

总计: 2 个文件, 4.22 MB
```

---

## 🔧 配置说明

### 当前配置（MyApplication.java）

```java
LogConfig config = new LogConfig.Builder(this)
        .setEnable(true)                          // 启用日志
        .setLogLevel(LogLevel.VERBOSE)            // 最低级别
        .setGlobalTag("MyApp")                    // 全局 Tag
        .setEnableConsole(true)                   // 启用控制台
        .setEnableFile(true)                      // 启用文件
        .setMaxFileSize(5 * 1024 * 1024)         // 5MB
        .setRetentionDays(7)                      // 保留 7 天
        .setShowThreadInfo(true)                  // 显示线程
        .setShowStackTrace(false)                 // 关闭堆栈
        .build();
```

### 修改配置

如需修改配置，编辑 `MyApplication.java` 中的 `initLogger()` 方法。

---

## 📊 日志文件说明

### 文件命名规则

```
app_log_20231210_1702188000000.log
│        │        │
│        │        └─ 时间戳（毫秒）
│        └─ 日期（yyyyMMdd）
└─ 前缀
```

### 文件内容格式

```
2023-12-10 15:30:45.123 12345-67890 D/LogDemoActivity: [Thread: main]
测试基础日志
```

格式说明：
- `2023-12-10 15:30:45.123`：时间戳
- `12345`：进程 ID
- `67890`：线程 ID
- `D`：日志级别
- `LogDemoActivity`：Tag
- `[Thread: main]`：线程名称
- 日志消息

---

## 🎨 UI 界面说明

### LogDemoActivity 界面布局

```
┌─────────────────────────────────┐
│      日志模块测试                │
├─────────────────────────────────┤
│  [测试基础日志]                  │
│  [测试 JSON 日志]                │
│  [测试 XML 日志]                 │
│  [测试异常日志]                  │
│  [性能测试（10000条）]           │
│  [查看日志文件]                  │
├─────────────────────────────────┤
│  输出结果：                      │
│  ┌───────────────────────────┐  │
│  │ 点击上方按钮测试日志功能... │  │
│  │                           │  │
│  │                           │  │
│  └───────────────────────────┘  │
├─────────────────────────────────┤
│  💡 提示：日志会同时输出到      │
│  Logcat 和文件                  │
│  📁 日志文件位置：              │
│  /data/data/包名/files/logs/    │
└─────────────────────────────────┘
```

---

## 🐛 常见问题

### 1. 日志未输出到 Logcat

**原因**：Logcat 过滤器设置不正确

**解决**：
- 在 Logcat 中选择 "No Filters"
- 或者搜索 Tag: `LogDemoActivity` 或 `MyApp`

### 2. 日志文件未生成

**原因**：应用没有文件写入权限或初始化失败

**解决**：
- 检查 `MyApplication` 是否在 `AndroidManifest.xml` 中注册
- 查看 Logcat 是否有错误信息
- 确认 `LogManager.getInstance().init()` 被调用

### 3. 性能测试卡顿

**原因**：10000 条日志写入需要时间

**解决**：
- 这是正常现象，测试在后台线程执行
- 等待几秒钟，界面会显示测试结果
- 可以在 Logcat 中实时查看日志输出

### 4. 查看日志文件显示为空

**原因**：日志还未写入文件或文件未刷新

**解决**：
- 等待几秒钟后再点击"查看日志文件"
- 先执行其他测试，生成日志后再查看
- 检查日志目录权限

---

## 📚 更多文档

| 文档 | 说明 |
|------|------|
| `LOG_SDK_README.md` | 项目总览、快速开始 |
| `LOG_SDK_USAGE.md` | 详细使用指南 |
| `LOG_SDK_ARCHITECTURE.md` | 架构设计文档 |
| `LOG_SDK_ADVANCED.md` | 高级功能（加密、Crash 等） |
| `LOG_SDK_STRUCTURE.md` | 项目结构说明 |
| `LOG_SDK_SUMMARY.md` | 完整交付总结 |

---

## ✅ 验证清单

完成以下步骤，确认集成成功：

- [ ] 应用启动成功
- [ ] 点击"查看日志模块 Demo"按钮，成功跳转
- [ ] LogDemoActivity 界面正常显示
- [ ] 点击"测试基础日志"，Logcat 有输出
- [ ] 点击"测试 JSON 日志"，JSON 格式化正确
- [ ] 点击"性能测试"，显示测试结果
- [ ] 点击"查看日志文件"，显示文件列表
- [ ] 使用 adb 查看日志文件，内容正确

---

## 🎉 完成！

现在您可以：

1. ✅ 运行应用并测试日志功能
2. ✅ 查看 Logcat 实时日志
3. ✅ 查看文件日志
4. ✅ 进行性能测试
5. ✅ 参考文档进行二次开发

**祝您使用愉快！** 🚀
