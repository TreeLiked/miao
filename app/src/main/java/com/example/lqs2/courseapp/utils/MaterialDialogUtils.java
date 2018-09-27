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

public class MaterialDialogUtils {

    public static abstract class DialogOnConfirmClickListener extends DialogBothDoSthOnClickListener {
        public abstract void onConfirmButtonClick();

        public void onCancelButtonClick() {

        }

    }

    public static abstract class DialogOnCancelClickListener extends DialogBothDoSthOnClickListener {
        public void onConfirmButtonClick() {

        }

        public abstract void onCancelButtonClick();

    }

    public static abstract class DialogBothDoSthOnClickListener {
        public abstract void onConfirmButtonClick();

        public abstract void onCancelButtonClick();
    }

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


    public static void showYesOrNoDialogWithBothSthTodo(Context context, String[] config, DialogBothDoSthOnClickListener listener, boolean dismissOnClick) {
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

    public static void showYesOrNoDialog(Context context, String[] config, DialogOnConfirmClickListener listener, boolean dismissOnClick) {
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




    public static void showSimpleConfirmDialog(Context context, String[] config) {
        showYesOrNoDialog(context, config, null, true);
    }

    public static MaterialDialog showYesOrNoDialogWithCustomView(Context context, String[] config, View view, DialogOnConfirmClickListener listener, boolean dismissOnClick) {
        return showYesOrNoDialogWithAll(context, config, view, -1, listener, dismissOnClick);
    }


    public static MaterialDialog showYesOrNoDialogWithBackground(Context context, String[] config, int resId, DialogOnConfirmClickListener listener, boolean dismissOnClick) {
        return showYesOrNoDialogWithAll(context, config, null, resId, listener, dismissOnClick);
    }


    public static MaterialDialog showYesOrNoDialogWithAll(Context context, String[] config, View view, int resId, DialogBothDoSthOnClickListener listener, boolean dismissOnClick) {
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
                    })
                    .setNegativeButton(config[3], v -> {
                        if (dismissOnClick) {
                            dialog.dismiss();
                        }
                        listener.onCancelButtonClick();
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


}
