package com.example.lqs2.courseapp.adapters;


import android.support.v7.widget.CardView;

/**
 * 便签适配器
 *
 * @author lqs2
 */
public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    /**
     * 获取仰角
     *
     * @return float
     */
    float getBaseElevation();

    /**
     * 获取指定位置的view
     *
     * @param position 位置
     * @return CardView
     */
    CardView getCardViewAt(int position);

    /**
     * 获取数目
     *
     * @return int 数目
     */
    int getCount();
}
