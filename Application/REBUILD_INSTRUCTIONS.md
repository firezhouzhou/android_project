# 🔧 重新构建项目指南

## ✅ 已完成的操作

1. ✅ 修改了 `EventDao.kt`，所有方法都有明确的返回类型
2. ✅ 清理了 kapt 缓存目录：
   - `app/build/tmp/kapt3`
   - `app/build/generated`
   - `app/build/intermediates`

## 🎯 接下来您需要在 Android Studio 中执行

### 方法 1：使用 Android Studio 菜单（推荐）

1. **关闭所有打开的文件**
   - 在 Android Studio 中关闭所有编辑器标签页

2. **同步 Gradle**
   - 点击顶部工具栏的 `File` → `Sync Project with Gradle Files`
   - 或者点击顶部提示条的 `Sync Now` 按钮
   - 等待同步完成（约 1-2 分钟）

3. **清理项目**
   - 点击 `Build` → `Clean Project`
   - 等待清理完成

4. **重新构建项目**
   - 点击 `Build` → `Rebuild Project`
   - 等待构建完成（首次可能需要 3-5 分钟）

5. **运行应用**
   - 点击 `Run` → `Run 'app'`

### 方法 2：使用 Invalidate Caches（如果方法 1 失败）

如果上述方法仍然报错，说明 IDE 缓存也需要清理：

1. **Invalidate Caches**
   - 点击 `File` → `Invalidate Caches / Restart...`
   - 在弹出的对话框中，勾选：
     - ✅ Invalidate and Restart
   - 点击 `Invalidate and Restart` 按钮

2. **等待 IDE 重启**
   - Android Studio 会自动重启
   - 重启后会自动重新索引项目

3. **重新构建**
   - 等待索引完成后
   - 点击 `Build` → `Rebuild Project`

### 方法 3：使用命令行（备选方案）

如果 Android Studio 仍然有问题，可以尝试命令行构建：

```bash
cd /Users/zhangsan/AndroidStudioProjects/AndroidJavaCode/android_project/Application

# 清理项目
./gradlew clean

# 重新构建
./gradlew assembleDebug
```

## 🔍 验证修改是否生效

### 检查 EventDao.kt 文件

确认以下方法都有返回类型：

```kotlin
// ✅ 应该是这样的（有返回类型 Int）
@Query("DELETE FROM events WHERE eventId = :eventId")
suspend fun deleteById(eventId: String): Int

// ❌ 不应该是这样的（没有返回类型）
@Query("DELETE FROM events WHERE eventId = :eventId")
suspend fun deleteById(eventId: String)
```

您可以打开 `app/src/main/java/com/baidu/application/analytics/repository/EventDao.kt` 文件检查。

### 检查缓存是否已清理

确认以下目录不存在或为空：
- `app/build/tmp/kapt3/` - 应该不存在
- `app/build/generated/` - 应该不存在

## ⚠️ 如果仍然报错

### 错误：仍然提示 "java.lang.Object"

**原因**：Android Studio 可能还在使用内存中的旧缓存

**解决方法**：

1. **完全关闭 Android Studio**
   - 不要只是关闭项目，而是完全退出 Android Studio 应用

2. **手动删除 IDE 缓存**（可选）
   ```bash
   # 删除 Android Studio 的缓存
   rm -rf ~/Library/Caches/Google/AndroidStudio*
   rm -rf ~/Library/Application\ Support/Google/AndroidStudio*
   ```

3. **重新打开 Android Studio**
   - 打开项目
   - 等待索引完成
   - 执行 `Build` → `Rebuild Project`

### 错误：Gradle 同步失败

**可能原因**：网络问题或依赖下载失败

**解决方法**：

1. 检查网络连接
2. 重试同步：`File` → `Sync Project with Gradle Files`
3. 如果仍然失败，查看 `Build` 窗口的详细错误信息

## 📋 完整的 EventDao.kt 应该是这样的

```kotlin
@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>): List<Long>

    @Update
    suspend fun update(event: EventEntity): Int

    @Delete
    suspend fun delete(event: EventEntity): Int

    @Query("DELETE FROM events WHERE eventId = :eventId")
    suspend fun deleteById(eventId: String): Int

    @Query("DELETE FROM events WHERE eventId IN (:eventIds)")
    suspend fun deleteByIds(eventIds: List<String>): Int

    @Query("DELETE FROM events")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM events WHERE eventId = :eventId")
    suspend fun getById(eventId: String): EventEntity?

    @Query("SELECT * FROM events WHERE status = 'PENDING' ORDER BY createTime ASC LIMIT :limit")
    suspend fun getPendingEvents(limit: Int): List<EventEntity>

    @Query("SELECT * FROM events WHERE status = 'FAILED' AND retryCount < :maxRetryCount ORDER BY createTime ASC LIMIT :limit")
    suspend fun getFailedEvents(maxRetryCount: Int, limit: Int): List<EventEntity>

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM events WHERE status = :status")
    suspend fun getCountByStatus(status: String): Int

    @Query("DELETE FROM events WHERE createTime < :expireTime")
    suspend fun deleteExpiredEvents(expireTime: Long): Int

    @Query("UPDATE events SET status = :status, updateTime = :updateTime WHERE eventId = :eventId")
    suspend fun updateStatus(eventId: String, status: String, updateTime: Long): Int

    @Query("UPDATE events SET status = :status, updateTime = :updateTime WHERE eventId IN (:eventIds)")
    suspend fun updateStatusBatch(eventIds: List<String>, status: String, updateTime: Long): Int

    @Query("UPDATE events SET retryCount = retryCount + 1, updateTime = :updateTime WHERE eventId = :eventId")
    suspend fun incrementRetryCount(eventId: String, updateTime: Long): Int
}
```

## ✅ 成功标志

当您看到以下信息时，说明构建成功：

```
BUILD SUCCESSFUL in Xs
```

然后就可以运行应用了！

## 📞 需要帮助？

如果按照上述所有步骤仍然无法解决问题，请：

1. 截图完整的错误信息
2. 检查 `EventDao.kt` 文件内容是否正确
3. 查看 `Build` 窗口的完整日志

---

**重要提示**：kapt 缓存已经清理，现在只需要在 Android Studio 中执行 `Build` → `Rebuild Project` 即可！
