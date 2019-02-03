package com.hwy.dragbubble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * 作者: hewenyu
 * 日期：2019/1/30 14:55
 * 说明:
 */
public class DragBubbleUtil {

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return dip2px(25, context);
    }

    /**
     * dip 转换成 px
     *
     * @param dip
     * @param context
     * @return
     */
    public static int dip2px(float dip, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }

    /**
     * 根据百分比获取两点之间的某个点坐标
     *
     * @param p1
     * @param p2
     * @param percent
     * @return
     */
    public static PointF getPointByPercent(PointF p1, PointF p2, float percent) {
        return new PointF(evaluateValue(percent, p1.x, p2.x), evaluateValue(
                percent, p1.y, p2.y));
    }

    /**
     * 根据分度值，计算从start到end中，fraction位置的值。fraction范围为0 - 1
     *
     * @param fraction = 1
     * @param start    = 10
     * @param end      = 3
     * @return
     */
    public static float evaluateValue(float fraction, Number start, Number end) {
        // start = 10   end = 2
        //fraction = 0.5
        // result = 10 + (-8) * fraction = 6
        return start.floatValue() + (end.floatValue() - start.floatValue())
                * fraction;
    }

    /**
     * 计算两点的间距
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @return
     */
    public static double calculateDistance(float startX, float startY, float endX, float endY) {
        return Math.sqrt((startX - endX) * (startX - endX) + (startY - endY) * (startY - endY));
    }

    /**
     * 获取View的截图
     *
     * @param view
     * @return
     */
    public static Bitmap getBitmapByView(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

}
