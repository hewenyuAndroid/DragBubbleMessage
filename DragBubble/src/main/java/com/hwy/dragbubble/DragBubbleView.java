package com.hwy.dragbubble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 作者: hewenyu
 * 日期：2019/1/30 15:11
 * 说明: 拖拽的View
 */
public class DragBubbleView extends View {

    private Paint mPaint;

    private int mColor = Color.RED;

    /**
     * 拖拽圆的半径
     */
    private float mDragRadius = 12;

    /**
     * 固定圆的半径
     */
    private float mFixedRadius = 8;

    /**
     * 固定圆的最小半径
     */
    private float mFixedMinRadius = 5;

    private float mFixedTempRadius;

    /**
     * 控件的截图
     */
    private Bitmap mScreenShots;

    /**
     * 贝塞尔曲线的路径
     */
    private Path mBezierPath;

    private int mWidth, mHeight;

    private PointF mDragPointF;

    private PointF mOriginPointF;

    private PointF mControlPointF;

    /**
     * 拖拽到爆炸的阈值
     */
    private float mDragMaxThreshold = 100;

    /**
     * 操作的控件对象
     */
    private View mTargetView;

    /**
     * 是否绘制固定的点
     */
    private boolean mIsDrawFixedPoint;

    public DragBubbleView(Context context) {
        this(context, null);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFixedRadius = dipToPx(mFixedRadius);
        mDragRadius = dipToPx(mDragRadius);
        mDragMaxThreshold = dipToPx(mDragMaxThreshold);
        mFixedMinRadius = dipToPx(mFixedMinRadius);
        init();

    }

    private void init() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColor);

        mBezierPath = new Path();
        mOriginPointF = new PointF();
        mDragPointF = new PointF();
        mControlPointF = new PointF();

        mIsDrawFixedPoint = true;

    }

    /**
     * 官方的 dip 转 px 方法
     *
     * @param dip
     * @return
     */
    private float dipToPx(float dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mOriginPointF.x == 0f && mOriginPointF.y == 0f) {
            return;
        }

        float cx = mOriginPointF.x;
        float cy = mOriginPointF.y;

        // 判断是否中断贝塞尔曲线
        double distance = DragBubbleUtil.calculateDistance(cx, cy, mDragPointF.x, mDragPointF.y);
        if (mIsDrawFixedPoint && distance <= mDragMaxThreshold) {
            // 计算固定圆的大小
            float percent = (float) (distance / mDragMaxThreshold);
            mFixedTempRadius = mFixedMinRadius + (1 - percent) * (mFixedRadius - mFixedMinRadius);
            // 绘制固定的圆
            canvas.drawCircle(cx, cy, mFixedTempRadius, mPaint);
            // 绘制贝塞尔曲线
            calculatePath(cx, cy, mDragPointF.x, mDragPointF.y);
            // 绘制贝塞尔曲线
            canvas.drawPath(mBezierPath, mPaint);
        }

        if (mScreenShots != null) {
            // 绘制View的截图
            canvas.drawBitmap(mScreenShots, mDragPointF.x - mScreenShots.getWidth() / 2, mDragPointF.y - mScreenShots.getHeight() / 2, mPaint);
        } else {
            // 绘制默认的圆
//            canvas.drawCircle(mDragPointF.x, mDragPointF.y, mDragRadius, mPaint);
        }

        // 在绘制View的截图之后将View隐藏，防止出现视图抖动
        if (mTargetView != null && mTargetView.getVisibility() == View.VISIBLE) {
            mTargetView.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 计算贝塞尔曲线的路径
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    private void calculatePath(float startX, float startY, float endX, float endY) {
        // 通过反三角函数计算角度
        double arcTanA = Math.atan((endY - startY) / (endX - startX));

        float fixedTopX = (float) (startX + mFixedTempRadius * Math.sin(arcTanA));
        float fixedTopY = (float) (startY - mFixedTempRadius * Math.cos(arcTanA));

        float fixedBottomX = (float) (startX - mFixedTempRadius * Math.sin(arcTanA));
        float fixedBottomY = (float) (startY + mFixedTempRadius * Math.cos(arcTanA));

        float dragTopX = (float) (endX + mDragRadius * Math.sin(arcTanA));
        float dragTopY = (float) (endY - mDragRadius * Math.cos(arcTanA));

        float dragBottomX = (float) (endX - mDragRadius * Math.sin(arcTanA));
        float dragBottomY = (float) (endY + mDragRadius * Math.cos(arcTanA));

        // 计算中心点
        mControlPointF.set((startX + endX) / 2, (startY + endY) / 2);

        mBezierPath.reset();
        mBezierPath.moveTo(fixedTopX, fixedTopY);
        mBezierPath.quadTo(mControlPointF.x, mControlPointF.y, dragTopX, dragTopY);

        mBezierPath.lineTo(dragBottomX, dragBottomY);
        mBezierPath.quadTo(mControlPointF.x, mControlPointF.y, fixedBottomX, fixedBottomY);
        mBezierPath.close();

    }

    /**
     * 设置截图
     *
     * @param bitmap
     */
    public void setScreenShots(Bitmap bitmap) {
        this.mScreenShots = bitmap;
    }

    /**
     * 设置初始位置
     *
     * @param originPointF
     */
    public void setOriginPointF(PointF originPointF) {
        this.mOriginPointF.set(originPointF);
        this.mDragPointF.set(originPointF);
        invalidate();
    }

    /**
     * 更新位置
     *
     * @param rawX
     * @param rawY
     */
    public void updatePointF(float rawX, float rawY) {
        this.mDragPointF.set(rawX, rawY);
        if (mDragPointF.x - mDragRadius < 0) {
            mDragPointF.x = mDragRadius;
        }
        if (mDragPointF.x + mDragRadius > mWidth) {
            mDragPointF.x = mWidth - mDragRadius;
        }
        if (mDragPointF.y - mDragRadius < 0) {
            mDragPointF.y = mDragRadius;
        }
        if (mDragPointF.y + mDragRadius > mHeight) {
            mDragPointF.y = mHeight - mDragRadius;
        }
        invalidate();
    }

    /**
     * 设置目标控件
     *
     * @param targetView
     */
    public void setTargetView(View targetView) {
        this.mTargetView = targetView;
    }

    public float getDragMaxThreshold() {
        return this.mDragMaxThreshold;
    }

    public void setDrawFixedPoint(boolean drawFixedPoint) {
        this.mIsDrawFixedPoint = drawFixedPoint;
    }

    /**
     * 绑定控件需要拖动的View
     *
     * @param view
     * @param context
     */
    public static void attach(View view, Context context, OnStateListener onStateListener) {
        view.setOnTouchListener(new DragBubbleHelper(view, context, null, onStateListener));
    }

    /**
     * 绑定控件需要拖动的View
     *
     * @param view
     * @param context
     * @param params
     */
    public static void attach(View view, Context context, DragBubbleParams params, OnStateListener onStateListener) {
        view.setOnTouchListener(new DragBubbleHelper(view, context, params, onStateListener));
    }

    public void reset() {
        mTargetView = null;
        mScreenShots = null;
        mBezierPath.reset();
    }

    // region ---------- get/set -------------

    public void setColor(int color) {
        this.mColor = color;
        mPaint.setColor(mColor);
        invalidate();
    }

    public void setDragRadius(float dragRadius) {
        this.mDragRadius = dragRadius;
    }

    public void setFixedRadius(float fixedRadius) {
        this.mFixedRadius = fixedRadius;
    }

    public void setFixedMinRadius(float fixedMinRadius) {
        this.mFixedMinRadius = fixedMinRadius;
    }

    public void setDragMaxThreshold(float dragMaxThreshold) {
        this.mDragMaxThreshold = dragMaxThreshold;
    }


    // endregion ----------------------------

}
