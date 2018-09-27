package com.example.lqs2.courseapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

public class ScaleImageView extends android.support.v7.widget.AppCompatImageView {


    private float lastX, lastY;// 上一次记录的点
    private float lastDistance;//上一次两点间的距离

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleImageView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2) {//两点触摸
                    final float disX = Math.abs(event.getX(0) - event.getX(1));//第一个点的偏移量
                    final float disY = Math.abs(event.getY(0) - event.getY(1));//第二个点的偏移量
                    final float dis = (float) Math.sqrt(disX * disX + disY * disY);//记录两点间的距离
                    if (lastDistance == 0) {
                        lastDistance = dis;//记录第一次
                    } else {
                        float scale = dis / lastDistance;
                        lastDistance = dis;//替换上一次
                        scaleImage(scale);
                    }
                } else if (event.getPointerCount() == 1) {//单点触摸
                    final float currentX = event.getX();
                    final float currentY = event.getY();
                    final float disX = currentX - lastX;
                    final float disY = currentY - lastY;
                    scrollBy(-(int) disX, -(int) disY);//进行拖动视图
                    lastX = currentX;//替换上一次位置
                    lastY = currentY;
                }
                break;
            case MotionEvent.ACTION_UP:
                lastX = 0;//恢复初始化状态
                lastY = 0;
                lastDistance = 0;
                break;
            default:
                break;
        }

        super.onTouchEvent(event);
        return true;
    }

    /**
     * 进行缩放
     * @param scale
     */
    private void scaleImage(float scale) {
        final int width = getWidth();
        final int newWidth = (int) (width * scale);
        final int height = getHeight();
        final int newHeight = (int) (height * scale);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = newHeight;
        params.width = newWidth;
        setLayoutParams(params);
    }
}
