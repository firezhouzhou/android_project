# 埋点系统依赖说明

## 📦 必需依赖

埋点系统需要以下依赖库才能正常运行：

### 1. Kotlin 协程（Coroutines）

```gradle
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0'
```

**用途**：
- 异步事件处理
- 非阻塞 IO 操作
- 后台任务调度

### 2. Room 数据库

```gradle
def room_version = "2.4.2"
implementation "androidx.room:room-runtime:$room_version"
implementation "androidx.room:room-ktx:$room_version"
kapt "androidx.room:room-compiler:$room_version"
```

**用途**：
- 本地事件缓存
- 事件持久化存储
- 数据库操作

### 3. WorkManager

```gradle
def work_version = "2.7.1"
implementation "androidx.work:work-runtime-ktx:$work_version"
```

**用途**：
- 后台上传任务调度
- 定时上传
- 网络状态监听

### 4. Kotlinx Serialization

```gradle
implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2'
```

**用途**：
- JSON 序列化/反序列化
- 事件数据转换

### 5. CardView（可选）

```gradle
implementation 'androidx.cardview:cardview:1.0.0'
```

**用途**：
- 演示界面 UI

---

## 🔧 Gradle 配置

### 项目根目录 build.gradle

```gradle
plugins {
    id 'com.android.application' version '7.2.2' apply false
    id 'com.android.library' version '7.2.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.7.10' apply false
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.7.10' apply false
}
```

### app/build.gradle

```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-kapt'
    id 'org.jetbrains.kotlin.plugin.serialization'
}

dependencies {
    // Kotlin 协程
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0'
    
    // Room 数据库
    def room_version = "2.4.2"
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"
    
    // WorkManager
    def work_version = "2.7.1"
    implementation "androidx.work:work-runtime-ktx:$work_version"
    
    // Kotlinx Serialization
    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2'
    
    // CardView
    implementation 'androidx.cardview:cardview:1.0.0'
}
```

---

## 🚀 同步依赖

### 方法 1：Android Studio

1. 打开 Android Studio
2. 点击顶部提示条的 **"Sync Now"**
3. 或者：`File` → `Sync Project with Gradle Files`

### 方法 2：命令行

```bash
cd /Users/zhangsan/AndroidStudioProjects/AndroidJavaCode/android_project/Application
./gradlew clean build
```

---

## ⚠️ 常见问题

### 1. Unresolved reference: kotlinx

**原因**：缺少 Kotlin 协程或 Kotlinx Serialization 依赖

**解决**：
```gradle
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0'
implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2'
```

### 2. Unresolved reference: room

**原因**：缺少 Room 数据库依赖

**解决**：
```gradle
implementation "androidx.room:room-runtime:2.4.2"
kapt "androidx.room:room-compiler:2.4.2"
```

### 3. Unresolved reference: work

**原因**：缺少 WorkManager 依赖

**解决**：
```gradle
implementation "androidx.work:work-runtime-ktx:2.7.1"
```

### 4. Cannot access 'Serializable'

**原因**：缺少 Kotlinx Serialization 插件

**解决**：
在 app/build.gradle 的 plugins 中添加：
```gradle
id 'org.jetbrains.kotlin.plugin.serialization'
```

### 5. kapt not found

**原因**：缺少 kapt 插件

**解决**：
在 app/build.gradle 的 plugins 中添加：
```gradle
id 'kotlin-kapt'
```

---

## 📊 依赖大小

| 依赖 | 大小（约） |
|------|-----------|
| Kotlin 协程 | ~1.5 MB |
| Room 数据库 | ~500 KB |
| WorkManager | ~300 KB |
| Kotlinx Serialization | ~200 KB |
| **总计** | **~2.5 MB** |

---

## ✅ 验证依赖

同步完成后，检查以下文件是否可以正常编译：

```bash
./gradlew :app:compileDebugKotlin
```

如果没有错误，说明依赖配置成功！

---

## 📝 注意事项

1. **版本兼容性**：确保所有依赖版本与 Kotlin 版本兼容
2. **ProGuard**：如果开启混淆，需要添加相应的 ProGuard 规则
3. **多模块项目**：如果是多模块项目，确保在正确的 module 中添加依赖
4. **网络代理**：如果下载依赖失败，可能需要配置 Gradle 代理

---

## 🔗 官方文档

- [Kotlin 协程](https://kotlinlang.org/docs/coroutines-overview.html)
- [Room 数据库](https://developer.android.com/training/data-storage/room)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
