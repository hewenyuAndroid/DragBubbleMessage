package com.hwy.dragbubble;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.lang.reflect.Field;

/**
 * 作者: hewenyu
 * 日期：2019/1/30 14:55
 * 说明: 处理触摸事件
 */
public class DragBubbleHelper implements View.OnTouchListener {

    /**
     * 需要操作的目标控件
     */
    private View mTargetView;

    /**
     * 实际拖拽的控件
     */
    private DragBubbleView mDragBubbleView;

    private Context mContext;

    /**
     * 用来获取Window
     */
    private WindowManager mWindowManager;

    /**
     * 用来获取根视图
     */
    private Window mWindow;

    /**
     * 根视图
     */
    private FrameLayout mDecorView;

    /**
     * 触摸的Down事件在 mDecorView 中的位置
     */
    private PointF mOriginPointF;

    /**
     * 用于临时记录位置的点
     */
    private PointF mTempPointF;

    /**
     * 目标控件的截图
     */
    private Bitmap mViewScreenShots;

    /**
     * 目标视图的初始位置
     */
    private int[] mTargetViewLocation;

    /**
     * 回弹动画的时长
     */
    private long mDuration = 200;
    /**
     * 回弹动画的插值器
     */
    private Interpolator mInterpolator = new OvershootInterpolator(6f);

    /**
     * 显示爆炸动画的控件
     */
    private ImageView mExplosionView;

    private OnStateListener mOnStateListener;

    private DragBubbleParams mParams;

    public DragBubbleHelper(View targetView, Context context, DragBubbleParams params, OnStateListener onStateListener) {
        this.mTargetView = targetView;
        this.mContext = context;
        this.mOnStateListener = onStateListener;
        this.mParams = params;

        if (mContext == null) {
            throw new RuntimeException("Context is null object");
        }
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mWindow = getWindowFromReflection(mWindowManager);
        if (mWindow == null) {
            throw new RuntimeException("Failed to get a Window from Windows manager!");
        }
        // 获取窗口中的根视图
        mDecorView = (FrameLayout) mWindow.getDecorView();
        mOriginPointF = new PointF();
        mTempPointF = new PointF();

        if (mParams == null) {
            mParams = new DragBubbleParams();
        }

    }

    private void init() {
        mDragBubbleView = new DragBubbleView(mContext);
        // 设置背景透明
        mDragBubbleView.setBackgroundColor(Color.TRANSPARENT);
        if (mParams.getColor() != null) {
            mDragBubbleView.setColor(mParams.getColor());
        }

        if (mParams.getDragRadius() != null) {
            mDragBubbleView.setDragRadius(mParams.getDragRadius());
        }

        if (mParams.getFixedRadius() != null) {
            mDragBubbleView.setFixedRadius(mParams.getFixedRadius());
        }

        if (mParams.getFixedMinRadius() != null) {
            mDragBubbleView.setFixedMinRadius(mParams.getFixedMinRadius());
        }

        if (mParams.getDragMaxThreshold() != null) {
            mDragBubbleView.setDragMaxThreshold(mParams.getDragMaxThreshold());
        }

        mTargetViewLocation = new int[2];

        mExplosionView = new ImageView(mContext);
        mExplosionView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 通过反射获取WindowManager中的 Window 变量
     *
     * @param windowManager
     * @return
     */
    private Window getWindowFromReflection(WindowManager windowManager) {
        Field[] fields = windowManager.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getType().getName().equals(Window.class.getName())) {
                try {
                    return (Window) field.get(windowManager);
                } catch (IllegalAccessException e) {
                }
            }
        }
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 解决ListView/RecyclerView 中的事件冲突
                v.getParent().requestDisallowInterceptTouchEvent(true);

                init();
                // 将贝塞尔曲线的拖拽控件添加上来
                mDecorView.addView(mDragBubbleView);
                mDragBubbleView.setTargetView(mTargetView);
                // 获取目标控件在屏幕中的位置
                mTargetView.getLocationOnScreen(mTargetViewLocation);

                // 获取控件的截图
                mViewScreenShots = DragBubbleUtil.getBitmapByView(mTargetView);
                mDragBubbleView.setScreenShots(mViewScreenShots);

                // 计算控件的中心在DecorView中的位置
                calculateStartLocation();
                mDragBubbleView.setOriginPointF(mOriginPointF);
                break;
            case MotionEvent.ACTION_MOVE:
                mDragBubbleView.updatePointF(event.getRawX(), event.getRawY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTempPointF.set(event.getRawX(), event.getRawY());
                checkResult();
                break;
        }
        return true;
    }

    /**
     * 判断执行结果
     */
    private void checkResult() {
        if (mDragBubbleView != null) {
            if (DragBubbleUtil.calculateDistance(mOriginPointF.x, mOriginPointF.y, mTempPointF.x, mTempPointF.y) > mDragBubbleView.getDragMaxThreshold()) {
                // 执行爆炸效果
                executeExplosion();
            } else {
                // 执行回弹效果
                executeRollback();
            }
        }
    }

    /**
     * 执行爆炸效果
     */
    private void executeExplosion() {
        mDecorView.removeView(mDragBubbleView);
        mDecorView.addView(mExplosionView);
        mExplosionView.setBackgroundResource(mParams.getExplosionDrable() != null ? mParams.getExplosionDrable() : R.drawable.anim_bubble_pop);

        AnimationDrawable drawable = (AnimationDrawable) mExplosionView.getBackground();
        mExplosionView.setX(mTempPointF.x - drawable.getIntrinsicWidth() / 2);
        mExplosionView.setY(mTempPointF.y - drawable.getIntrinsicHeight());

        drawable.start();
        mExplosionView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDragBubbleView.reset();
                mDecorView.removeView(mExplosionView);
                mExplosionView = null;

                if (mOnStateListener != null) {
                    mOnStateListener.onDismiss(mTargetView);
                }

            }
        }, getAnimationDrawableTime(drawable));

    }

    private long getAnimationDrawableTime(AnimationDrawable drawable) {
        int numberOfFrames = drawable.getNumberOfFrames();
        long totalTime = 0;
        for (int i = 0; i < numberOfFrames; i++) {
            totalTime += drawable.getDuration(i);
        }
        return totalTime;
    }


    /**
     * 执行回弹效果
     */
    private void executeRollback() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(mParams.getDuration() != null ? mParams.getDuration() : mDuration);
        animator.setInterpolator(mParams.getInterpolator() != null ? mParams.getInterpolator() : mInterpolator);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float tempPercent = (float) animation.getAnimatedValue();
                if (tempPercent > 0f) {
                    mDragBubbleView.setDrawFixedPoint(false);
                }
                PointF pointF = DragBubbleUtil.getPointByPercent(mTempPointF, mOriginPointF, tempPercent);
                mDragBubbleView.updatePointF(pointF.x, pointF.y);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画执行结束
                mDragBubbleView.reset();
                mViewScreenShots = null;
                mTargetView.setVisibility(View.VISIBLE);

                if (mOnStateListener != null) {
                    mOnStateListener.onRollback(mTargetView);
                }
            }

        });
        animator.start();
    }

    /**
     * 计算开始的位置
     */
    private void calculateStartLocation() {
        int x = mTargetViewLocation[0] + mTargetView.getWidth() / 2;
        int y = mTargetViewLocation[1] + mTargetView.getHeight() / 2;//- (isUseNavigationBar() ? DragBubbleUtil.getStatusBarHeight(mContext) : 0);
        mOriginPointF.set(x, y);
    }

    /**
     * 判断是否使用了导航栏
     *
     * @return
     */
    private boolean isUseNavigationBar() {
        // 1. 获取DecorView的所有孩子
        // 2. 判断是否有孩子的高度同反射获取的导航栏的高度一致

        // 获取系统状态栏的高度
        int statusBarHeight = DragBubbleUtil.getStatusBarHeight(mContext);
        for (int i = 0; i < mDecorView.getChildCount(); i++) {
            View view = mDecorView.getChildAt(i);
            if (view.getHeight() == statusBarHeight) {
                return true;
            }
        }
        return false;
    }


}
