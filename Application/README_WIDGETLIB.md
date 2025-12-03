# WidgetLib Module 使用文档

## 📦 模块简介

`widgetlib` 是一个Android Library Module，提供了可复用的自定义控件，支持包含多个子控件的容器组件。

## 🎯 核心组件

### 1. MultiChildContainer
多子控件容器，支持灵活的布局和子控件管理。

#### 特性
- ✅ 支持水平/垂直布局
- ✅ 支持子控件间距设置
- ✅ 支持子控件对齐方式（start/center/end）
- ✅ 支持动态添加/删除子控件
- ✅ 自动测量和布局

#### XML属性
```xml
<com.baidu.widgetlib.MultiChildContainer
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:orientation="horizontal"        <!-- 布局方向: horizontal/vertical -->
    app:childSpacing="12dp"             <!-- 子控件间距 -->
    app:childGravity="center"/>         <!-- 对齐方式: start/center/end -->
```

#### Kotlin代码使用
```kotlin
val container = findViewById<MultiChildContainer>(R.id.container)

// 设置属性
container.orientation = MultiChildContainer.ORIENTATION_HORIZONTAL
container.childSpacing = 16.dp
container.childGravity = MultiChildContainer.GRAVITY_CENTER

// 添加子控件
container.addChildView(button)
container.addChildViews(view1, view2, view3)

// 移除子控件
container.removeChildAt(0)
container.clearChildren()

// 获取子控件数量
val count = container.getChildrenCount()
```

### 2. ColorfulButton
彩色按钮组件，支持自定义背景色和圆角。

#### 使用示例
```kotlin
val button = ColorfulButton(context).apply {
    text = "点击我"
    setButtonBackgroundColor(Color.parseColor("#4ECDC4"))
    setCornerRadius(16f)
    setOnClickListener {
        // 处理点击事件
    }
}
```

### 3. IconTextView
图标文字组合控件，包含图标和文字标签。

#### 使用示例
```kotlin
val iconTextView = IconTextView(context).apply {
    setIcon(R.drawable.ic_home)
    setText("首页")
    setTextColor(Color.BLACK)
}
```

## 🚀 集成方式

### 1. 在settings.gradle中添加模块
```gradle
include ':app'
include ':widgetlib'
```

### 2. 在app/build.gradle中添加依赖
```gradle
dependencies {
    implementation project(':widgetlib')
}
```

### 3. 同步项目
点击 "Sync Now" 同步Gradle配置。

## 📝 完整示例

查看 `WidgetDemoActivity.kt` 获取完整的使用示例，包括：
- 水平布局容器示例
- 垂直布局容器示例
- 动态添加子控件示例

## 🎨 自定义扩展

你可以基于 `MultiChildContainer` 创建自己的自定义控件：

```kotlin
class MyCustomContainer(context: Context, attrs: AttributeSet?) 
    : MultiChildContainer(context, attrs) {
    
    init {
        // 自定义初始化
        orientation = ORIENTATION_VERTICAL
        childSpacing = 20.dp
    }
    
    // 添加自定义方法
    fun addCustomChild() {
        // 实现逻辑
    }
}
```

## 📐 布局原理

`MultiChildContainer` 使用自定义的 `onMeasure` 和 `onLayout` 方法：

1. **测量阶段**：计算所有子控件的尺寸和总尺寸
2. **布局阶段**：根据orientation和gravity放置子控件
3. **间距处理**：自动在子控件之间添加指定间距

## 🔧 高级用法

### 动态切换布局方向
```kotlin
// 切换为垂直布局
container.orientation = MultiChildContainer.ORIENTATION_VERTICAL

// 切换为水平布局
container.orientation = MultiChildContainer.ORIENTATION_HORIZONTAL
```

### 动态调整间距
```kotlin
container.childSpacing = 24.dp
```

### 动态调整对齐方式
```kotlin
container.childGravity = MultiChildContainer.GRAVITY_END
```

## 📱 运行Demo

1. 在 `AndroidManifest.xml` 中注册 `WidgetDemoActivity`
2. 在 `MainActivity` 中添加跳转按钮
3. 运行应用查看效果

## 🎯 最佳实践

1. **性能优化**：避免频繁添加/删除子控件，考虑使用RecyclerView
2. **内存管理**：及时清理不需要的子控件
3. **布局嵌套**：避免过深的布局嵌套，影响性能
4. **属性设置**：优先使用XML属性，代码动态设置作为补充

## 📄 License

MIT License