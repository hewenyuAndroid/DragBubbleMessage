package com.hwy.dragbubble;

import android.view.View;

/**
 * 作者: hewenyu
 * 日期：2019/1/31 11:12
 * 说明:
 */
public interface OnStateListener {

    /**
     * 执行回弹动画结束
     *
     * @param targetView
     */
    void onRollback(View targetView);

    /**
     * 爆炸效果执行结束
     *
     * @param targetView
     */
    void onDismiss(View targetView);

}
