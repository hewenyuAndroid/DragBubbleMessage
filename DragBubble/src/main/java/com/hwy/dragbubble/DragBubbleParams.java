package com.hwy.dragbubble;

import android.view.animation.Interpolator;

/**
 * 作者: hewenyu
 * 日期：2019/2/1 11:31
 * 说明: 参数封装
 */
public class DragBubbleParams {

    // region ----------- DragBubbleView 的参数 -----------

    private Float mFixedRadius;

    private Float mFixedMinRadius;

    private Float mDragRadius;

    private Integer mColor;

    private Float mDragMaxThreshold;

    // endregion --------------------------------------------

    // region ----------- DragBubbleHelper 的参数 -----------

    /**
     * 回弹动画的插值器
     */
    private Interpolator mInterpolator;

    /**
     * 回弹动画的时长
     */
    private Long mDuration;

    /**
     * 爆炸动画的Drawable Id
     */
    private Integer mExplosionResId;

    // endregion --------------------------------------------

    public DragBubbleParams() {

    }

    public DragBubbleParams setFixedRadius(float fixedRadius) {
        this.mFixedRadius = fixedRadius;
        return this;
    }

    public DragBubbleParams setFixedMinRadius(float fixedMinRadius) {
        this.mFixedMinRadius = fixedMinRadius;
        return this;
    }

    public DragBubbleParams mDragRadius(float dragRadius) {
        this.mDragRadius = dragRadius;
        return this;
    }

    public DragBubbleParams setColor(int color) {
        this.mColor = color;
        return this;
    }

    public DragBubbleParams setDragMaxThreshold(float dragMaxThreshold) {
        this.mDragMaxThreshold = dragMaxThreshold;
        return this;
    }

    public DragBubbleParams setDuration(long duration) {
        this.mDuration = duration;
        return this;
    }

    public DragBubbleParams setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
        return this;
    }

    public DragBubbleParams setExplosionResId(int resId) {
        this.mExplosionResId = resId;
        return this;
    }

    public Float getFixedRadius() {
        return mFixedRadius;
    }

    public Float getFixedMinRadius() {
        return mFixedMinRadius;
    }

    public Float getDragRadius() {
        return mDragRadius;
    }

    public Integer getColor() {
        return mColor;
    }

    public Float getDragMaxThreshold() {
        return mDragMaxThreshold;
    }

    public Interpolator getInterpolator() {
        return mInterpolator;
    }

    public Long getDuration() {
        return mDuration;
    }

    public Integer getExplosionDrable() {
        return mExplosionResId;
    }
}
