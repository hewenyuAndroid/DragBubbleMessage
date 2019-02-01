### DragBubbleMessage
本项目是模仿QQ中的消息拖拽的功能，作用的对象可以是任意的`View`，实现QQ中的消息提示全屏拖拽删除，拖拽距离不够时自动回弹的效果，[项目地址](https://github.com/hewenyuAndroid/DragBubbleMessage);

### 效果图
![拖拽回弹](https://github.com/hewenyuAndroid/DragBubbleMessage/blob/master/screen/rollback.gif?raw=true)
![拖拽爆炸](https://github.com/hewenyuAndroid/DragBubbleMessage/blob/master/screen/explosion.gif?raw=true)


[点我下载](https://github.com/hewenyuAndroid/DragBubbleMessage/blob/master/apk/app-debug.apk)安装包；


### 引用地址
> compile 'com.hewenyu:DragBubble:1.0'

### 使用方式
关于DragBubble的使用是非常简单的，我们可以一行代码就绑定任意的View:
1. 正常使用
```Java
DragBubbleView.attach(findViewById(R.id.tv_message_count), this, new OnStateListener() {
    @Override
    public void onRollback(View targetView) {
        showToast("TextView 执行了回弹");
    }
    @Override
    public void onDismiss(View targetView) {
        showToast("TextView 执行了销毁");
    }
});
```

2. 自定义配置 DragBubble 的相关属性
```Java
// 这里配置自定义的参数
DragBubbleParams params = new DragBubbleParams();
params.setColor(Color.parseColor("#FF6600"))
        .setInterpolator(new BounceInterpolator())
        .setDuration(300)
        .setDragMaxThreshold(400);

// 调用重构的方法将定制的参数传入即可
DragBubbleView.attach(findViewById(R.id.iv_image_view), this, params, new OnStateListener() {
    @Override
    public void onRollback(View targetView) {
        showToast("ImageView  执行了回弹");
    }

    @Override
    public void onDismiss(View targetView) {
        showToast("ImageView  执行了销毁");
    }
});
```

### 实现思路
我们知道Android开发的过程中，要想将一个 `View` 显示到屏幕上必须要将我们的 `View` 放到一个容器当中执行 测量、摆放、绘制这三个过程，而我们的 `View` 能够显示的空间只能是父布局的大小，如果超过了父布局的大小，`Android` 中提供了 `Scroller` 机制让我们内容可以滚动显示，例如`ScrollView` 等,因此如果想要全屏拖动一个View只能是让它的父布局全屏显示显然我们第一个能想到的就是 `mDecorView` 即根布局，关于 `DecorView` 可以查看我的这篇文章 [View的绘制流程](https://github.com/hewenyuAndroid/SourceRead/blob/master/View%E7%9A%84%E7%BB%98%E5%88%B6%E6%B5%81%E7%A8%8B.md)；
拿到了 `mDecorView` 之后我们只需要重写目标控件的的触摸监听的方法在 `mDecorView` 的最上层及时的增加/移除一个实现了贝塞尔曲线的 `View` 即可，关于贝塞尔曲线不懂的可以网上找找资料；

