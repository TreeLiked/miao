package com.example.lqs2.courseapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * material dialog 工具类
 *
 * @author lqs2
 */
public class MaterialDialogUtils {

    /**
     * 生成list型的对话框
     *
     * @param context  上下文
     * @param title    对话框标题
     * @param listener 按钮监听
     * @param items    选项
     * @return 对话框
     */
    public static MaterialDialog getItemListDialog(Context context, String title, ListView.OnItemClickListener listener, List<String> items) {
        final ArrayAdapter<String> adapter
                = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, items);

        final ListView listView = new ListView(context);
        final MaterialDialog alert = new MaterialDialog(context)
                .setTitle(title)
                .setContentView(listView);
        listView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (8 * scale + 0.5f);
        listView.setPadding(0, dpAsPixels, 0, dpAsPixels);
        listView.setDividerHeight(0);
        listView.setDivider(new ColorDrawable(Color.GRAY));
        listView.setDividerHeight(1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(listener);
        alert.setContentView(listView);
        alert.setCanceledOnTouchOutside(true);
        return alert;
    }

    /**
     * 显示yes/no风格对话框
     *
     * @param context        上下文
     * @param config         配置信息包括0：标题，1：内容，2：确认文字，3：取消文字
     * @param listener       按钮监听
     * @param dismissOnClick 是否点击空白可以取消
     */
    public static void showYesOrNoDialogWithBothSthTodo(Context context, String[] config, AbstractDialogBothDoSthOnClickListener listener, boolean dismissOnClick) {
        if (config.length == 4) {
            final MaterialDialog dialog = new MaterialDialog(context);
            dialog
                    .setTitle(config[0])
                    .setMessage(config[1])
                    .setPositiveButton(config[2], v -> {
                        if (dismissOnClick) {
                            dialog.dismiss();
                        }
                        listener.onConfirmButtonClick();
                    })
                    .setNegativeButton(config[3], v -> {
                        if (dismissOnClick) {
                            dialog.dismiss();
                        }
                        listener.onCancelButtonClick();
                    })
                    .setCanceledOnTouchOutside(true);
            dialog.show();
        }
    }

    /**
     * 显示yes/no风格对话框
     *
     * @param context        上下文
     * @param config         配置信息包括0：标题，1：内容，2：确认文字，3：取消文字
     * @param listener       按钮监听
     * @param dismissOnClick 是否点击空白可以取消
     */
    public static void showYesOrNoDialog(Context context, String[] config, AbstractDialogOnConfirmClickListener listener, boolean dismissOnClick) {
        if (config.length == 4) {
            final MaterialDialog dialog = new MaterialDialog(context);
            dialog
                    .setTitle(config[0])
                    .setMessage(config[1])
                    .setPositiveButton(config[2], v -> {
                        if (dismissOnClick) {
                            dialog.dismiss();
                        }
                        if (null != listener) {
                            listener.onConfirmButtonClick();
                        }
                    })
                    .setNegativeButton(config[3], v -> dialog.dismiss());
            dialog.show();
        }
    }

    /**
     * 显示简单confirm风格对话框
     *
     * @param context 上下文
     * @param config  配置信息包括0：标题，1：内容，2：确认文字，3：取消文字
     */
    public static void showSimpleConfirmDialog(Context context, String[] config) {
        showYesOrNoDialog(context, config, null, true);
    }

    /**
     * 显示自定义视图风格对话框
     *
     * @param context        上下文
     * @param view           加载指定视图
     * @param config         配置信息包括0：标题，1：内容，2：确认文字，3：取消文字
     * @param listener       按钮监听
     * @param dismissOnClick 是否点击空白可以取消
     */
    public static MaterialDialog showYesOrNoDialogWithCustomView(Context context, String[] config, View view, AbstractDialogOnConfirmClickListener listener, boolean dismissOnClick) {
        return showYesOrNoDialogWithAll(context, config, view, -1, listener, dismissOnClick);
    }

    /**
     * 显示带背景的对话框
     *
     * @param context        上下文
     * @param config         配置
     * @param resId          资源id
     * @param listener       动作监听
     * @param dismissOnClick 空白取消
     * @return 对话框啊哦
     */
    public static MaterialDialog showYesOrNoDialogWithBackground(Context context, String[] config, int resId, AbstractDialogOnConfirmClickListener listener, boolean dismissOnClick) {
        return showYesOrNoDialogWithAll(context, config, null, resId, listener, dismissOnClick);
    }

    /**
     * 功能超全的对话框
     *
     * @param context        上下文
     * @param config         配置
     * @param view           视图
     * @param resId          背景色
     * @param listener       监听
     * @param dismissOnClick 空白取消
     * @return 对话框啊哦
     */
    public static MaterialDialog showYesOrNoDialogWithAll(Context context, String[] config, View view, int resId, AbstractDialogBothDoSthOnClickListener listener, boolean dismissOnClick) {
        if (config.length == 4) {
            final MaterialDialog dialog = new MaterialDialog(context);
            if (null != view) {
                dialog.setContentView(view);
            }
            dialog
                    .setTitle(config[0])
                    .setMessage(config[1])
                    .setPositiveButton(config[2], v -> {
                        if (dismissOnClick) {
                            dialog.dismiss();
                        }
                        listener.onConfirmButtonClick();
                        dialog.dismiss();
                    })
                    .setNegativeButton(config[3], v -> {
                        if (dismissOnClick) {
                            dialog.dismiss();
                        }
                        listener.onCancelButtonClick();
                        dialog.dismiss();
                    })
                    .setCanceledOnTouchOutside(true);
            if (-1 != resId) {
                dialog.setBackgroundResource(resId);
            }

            dialog.show();
            return dialog;
        }
        return null;
    }

    public static abstract class AbstractDialogOnConfirmClickListener extends AbstractDialogBothDoSthOnClickListener {

        /**
         * 确认按钮触发事件
         */
        @Override
        public abstract void onConfirmButtonClick();

        @Override
        public void onCancelButtonClick() {

        }

    }

    public static abstract class AbstractDialogOnCancelClickListener extends AbstractDialogBothDoSthOnClickListener {
        @Override
        public void onConfirmButtonClick() {

        }

        /**
         * 取消按钮触发事件
         */
        @Override
        public abstract void onCancelButtonClick();

    }

    public static abstract class AbstractDialogBothDoSthOnClickListener {
        /**
         * 确认按钮触发事件
         */
        public abstract void onConfirmButtonClick();

        /**
         * 取消按钮触发事件
         */
        public abstract void onCancelButtonClick();
    }
}
