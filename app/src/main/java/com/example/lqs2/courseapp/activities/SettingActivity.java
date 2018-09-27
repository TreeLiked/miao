package com.example.lqs2.courseapp.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lqs2.courseapp.MyApplication;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.ImageTools;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.SharedPreferenceUtil;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.example.lqs2.courseapp.utils.UsualSharedPreferenceUtil;
import com.example.lqs2.courseapp.utils.VersionUtils;
import com.timmy.tdialog.TDialog;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class SettingActivity extends ActivityCollector implements View.OnClickListener {
    private TextView setting_account;
    private TextView setting_sig;
    private TextView setting_test;
    private TextView setting_login_out;
    private TextView setting_check_update_hand;
    private TextView setting_about;
    private TextView setting_chooseDefaultBg;

    private com.kyleduo.switchbutton.SwitchButton toggle_diy_main_bg_switch;
    private com.kyleduo.switchbutton.SwitchButton toggle_handoff_switch;
    private com.kyleduo.switchbutton.SwitchButton toggle_auto_detect_update_switch;
    private com.kyleduo.switchbutton.SwitchButton toggle_darkme_mode_switch;


    //    我又忘了
    private boolean flagForPb1 = false;
    private ProgressBar pb1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        StatusBarUtils.setStatusBarTransparentAndTextColorBlack(this);

        bindViews();

        initOptions();

        bindListeners();


    }

    private void bindListeners() {
        setting_account.setOnClickListener(this);
        setting_sig.setOnClickListener(this);
        setting_test.setOnClickListener(this);
        setting_login_out.setOnClickListener(this);
        setting_about.setOnClickListener(this);
        setting_check_update_hand.setOnClickListener(this);
        setting_chooseDefaultBg.setOnClickListener(this);

        toggle_diy_main_bg_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constant.IMAGE_REQUEST_CODE_CROP);

            } else {
                changeDefaultBg(0, true);
                setToggleDiyMainBgMemory(false);
            }
        });

        toggle_handoff_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !flagForPb1) {
                flagForPb1 = true;
                ObjectAnimator animator = ObjectAnimator.ofInt(pb1, "progress", 0, 1500);
                animator.setDuration(500);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        pb1.setVisibility(View.VISIBLE);
                        toggle_handoff_switch.setChecked(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toggle_handoff_switch.setChecked(true);
                        pb1.setVisibility(View.INVISIBLE);
                        setToggleHandoffMemory(true);

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animator.start();
            } else {
                setToggleHandoffMemory(false);
            }
        });
        toggle_auto_detect_update_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setToggleAutoDetectUpdateMemory(true);
            } else {
                setToggleAutoDetectUpdateMemory(false);
            }
        });

        toggle_darkme_mode_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setToggleDarkModeMemory(true);
                setDefaultBgNum(1);
            } else {
                setToggleDarkModeMemory(false);
                setDefaultBgNum(0);
            }
        });
    }

    private void bindViews() {
        setting_account = findViewById(R.id.setting_account);
        setting_sig = findViewById(R.id.setting_sig);
        setting_test = findViewById(R.id.setting_test);
        setting_check_update_hand = findViewById(R.id.setting_check_update_hand);
        setting_login_out = findViewById(R.id.setting_login_out);
        setting_chooseDefaultBg = findViewById(R.id.setting_choose_default_bg);
        toggle_diy_main_bg_switch = findViewById(R.id.setting_diy_main_bg_switch);
        toggle_handoff_switch = findViewById(R.id.setting_toggle_handoff_switch);
        toggle_auto_detect_update_switch = findViewById(R.id.setting_toggle_auto_detect_update_switch);
        toggle_darkme_mode_switch = findViewById(R.id.setting_toggle_dark_mode_switch);
        setting_about = findViewById(R.id.setting_about);
        pb1 = findViewById(R.id.setting_pb_1);
    }

    private void sendChangeMainBgBroadcast() {
        Intent intent = new Intent(Constant.ACTION2);
        MainActivity.localBroadcastManager.sendBroadcast(intent);
    }

    private void setDefaultBgNum(int i) {
        SharedPreferenceUtil.put(this, "default_main_bg_num", i);
    }


    private void changeDefaultBg(int i, boolean backDefault) {
        MainActivity.bgType = i;
        sendChangeMainBgBroadcast();
        setDefaultBgNum(i);
        if (backDefault) {
            ToastUtils.showToast(this, "恢复默认壁纸", 0);
        } else {
            ToastUtils.showToast(this, "更换成功", 0);
        }
    }

    private void initOptions() {
        if ((boolean) SharedPreferenceUtil.get(SettingActivity.this, "toggle_diy_main_bg", false)) {
            toggle_diy_main_bg_switch.setCheckedImmediatelyNoEvent(true);
        }

        if ((boolean) SharedPreferenceUtil.get(SettingActivity.this, "toggle_handoff", false)) {
            toggle_handoff_switch.setCheckedImmediatelyNoEvent(true);
        }

        if ((boolean) SharedPreferenceUtil.get(SettingActivity.this, "toggle_auto_detect_update", false)) {
            toggle_auto_detect_update_switch.setCheckedImmediatelyNoEvent(true);
        }
        if (UsualSharedPreferenceUtil.isDarkModeOn(SettingActivity.this)) {
            toggle_darkme_mode_switch.setCheckedImmediatelyNoEvent(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_account:
                String darkme_un = getValueByKeyFromSharedPxx("darkme_un");
                String xh = getValueByKeyFromSharedPxx("xh");

                if (TextUtils.isEmpty(xh) && TextUtils.isEmpty(darkme_un)) {
                    ToastUtils.showToast(this, "暂无登录", Toast.LENGTH_SHORT);
                } else {
                    ArrayList<String> items = new ArrayList<>();
                    boolean f0 = false;
                    boolean f1 = false;
                    if (!TextUtils.isEmpty(darkme_un)) {
                        f0 = true;
                        items.add("Darkme Account");
                    }
                    if (!TextUtils.isEmpty(xh)) {
                        f1 = true;
                        items.add("NJIT Account");
                    }
                    MaterialDialog[] dialogs = new MaterialDialog[1];
                    boolean finalF = f0;
                    boolean finalF1 = f1;
                    dialogs[0] = MaterialDialogUtils.getItemListDialog(this, "账号管理", (parent, view, position, id) -> {
                        dialogs[0].dismiss();
                        if (finalF && finalF1) {
                            if (0 == position) {
                                showAccountManageDialog(true, darkme_un);
                            } else if (1 == position) {
                                showAccountManageDialog(false, xh);
                            }
                        } else {
                            if (finalF) {
                                if (0 == position) {
                                    showAccountManageDialog(true, darkme_un);
                                }
                            } else if (finalF1) {
                                if (0 == position) {
                                    showAccountManageDialog(false, xh);
                                }
                            }
                        }
                    }, items);
                    dialogs[0].show();


                }
                break;
            case R.id.setting_sig:
                String sig = (String) SharedPreferenceUtil.get(SettingActivity.this, "signature", "");
                EditText sigEditText = new EditText(SettingActivity.this);
                if (!"".equals(sig)) {
                    sigEditText.setText(sig);
                } else {
                    sigEditText.setHint("您还没有设置个性签名哦");
                }
                final MaterialDialog dialog1 = new MaterialDialog(SettingActivity.this);
                dialog1.setTitle("我的签名")
                        .setContentView(sigEditText)
                        .setCanceledOnTouchOutside(true)
                        .setPositiveButton("保存", v1 -> {
                            SharedPreferenceUtil.put(SettingActivity.this, "signature", sigEditText.getText());
                            dialog1.dismiss();
                            Toast.makeText(SettingActivity.this, "签名修改成功", Toast.LENGTH_SHORT).show();
                            MainActivity.userInfoChangeFlag = true;
                        })
                        .setNegativeButton("取消", v1 -> {
                            if ("".equals(sigEditText.getText())) {
                                SharedPreferenceUtil.put(SettingActivity.this, "signature", "");
                            }
                            dialog1.dismiss();
                        });
                dialog1.show();
                break;

            case R.id.setting_choose_default_bg:
                final MaterialDialog[] dialogs = new MaterialDialog[1];
                dialogs[0] = MaterialDialogUtils.getItemListDialog(this, "选择壁纸", (parent, view, position, id) -> {
                    changeDefaultBg(position, false);
                    dialogs[0].dismiss();
                }, new ArrayList<String>() {{
                    add("壁纸1");
                    add("壁纸2");
                    add("壁纸3");
                    add("壁纸4");
                    add("壁纸5");
                }});
                dialogs[0].show();
                break;
            case R.id.setting_test:
                TDialog tDialog = new TDialog.Builder(getSupportFragmentManager())
                        .setLayoutRes(R.layout.t_dialog_evaluate)    //设置弹窗展示的xml布局
//                .setDialogView(view)  //设置弹窗布局,直接传入View
                        .setWidth(600)  //设置弹窗宽度(px)
                        .setHeight(800)  //设置弹窗高度(px)
                        .setScreenWidthAspect(this, 0.8f)   //设置弹窗宽度(参数aspect为屏幕宽度比例 0 - 1f)
                        .setScreenHeightAspect(this, 0.3f)  //设置弹窗高度(参数aspect为屏幕宽度比例 0 - 1f)
                        .setGravity(Gravity.CENTER)     //设置弹窗展示位置
                        .setTag("DialogTest")   //设置Tag
                        .setDimAmount(0.6f)     //设置弹窗背景透明度(0-1f)
                        .setCancelableOutside(true)     //弹窗在界面外是否可以点击取消
                        .setCancelable(true)    //弹窗是否可以取消
                        .setOnDismissListener(new DialogInterface.OnDismissListener() { //弹窗隐藏时回调方法
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                Toast.makeText(SettingActivity.this, "弹窗消失回调", Toast.LENGTH_SHORT).show();
                            }
                        })
//                        .setOnBindViewListener(new OnBindViewListener() {   //通过BindViewHolder拿到控件对象,进行修改
//                            @Override
//                            public void bindView(BindViewHolder bindViewHolder) {
//                                bindViewHolder.setText(R.id.tv_content, "abcdef");
//                                bindViewHolder.setText(R.id.tv_title, "我是Title");
//                            }
//                        })
//                        .addOnClickListener(R.id.btn_left, R.id.btn_right, R.id.tv_title)   //添加进行点击控件的id
//                        .setOnViewClickListener(new OnViewClickListener() {     //View控件点击事件回调
//                            @Override
//                            public void onViewClick(BindViewHolder viewHolder, View view, TDialog tDialog) {
//                                switch (view.getId()) {
//                                    case R.id.btn_left:
//                                        Toast.makeText(DiffentDialogActivity.this, "left clicked", Toast.LENGTH_SHORT).show();
//                                        break;
//                                    case R.id.btn_right:
//                                        Toast.makeText(DiffentDialogActivity.this, "right clicked", Toast.LENGTH_SHORT).show();
//                                        tDialog.dismiss();
//                                        break;
//                                    case R.id.tv_title:
//                                        Toast.makeText(DiffentDialogActivity.this, "title clicked", Toast.LENGTH_SHORT).show();
//                                        break;
//                                }
//                            }
//                        })
                        .create()   //创建TDialog
                        .show();    //展示

                break;


            case R.id.setting_login_out:
                MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(SettingActivity.this, new String[]{"确认登出", "此操作不可撤销", "关闭", "确认登出"}, new MaterialDialogUtils.DialogOnCancelClickListener() {
                    @Override
                    public void onCancelButtonClick() {
                        UsualSharedPreferenceUtil.loginOutNjit(SettingActivity.this);
                        UsualSharedPreferenceUtil.loginOutDarkMe(SettingActivity.this);
                        SharedPreferenceUtil.clear(SettingActivity.this);
                        ActivityCollector.finishAll();
                    }
                }, true);
                break;

            case R.id.setting_check_update_hand:
                VersionUtils.checkUpdate(this, this, false, setting_check_update_hand);
                break;

            case R.id.setting_about:

            default:
                String vs = String.valueOf(VersionUtils.getVersionCode());
                String vn = VersionUtils.getVersionName();
                String c = "版  本  号：" + vs + "\n" +
                        "版本名称：" + vn + "\n" +
                        "版权所有：\n" + "@cm\n@djt\n@qcy\n@TreeLiked\n@ttw";
                MaterialDialogUtils.showSimpleConfirmDialog(SettingActivity.this, new String[]{"关于喵", c, "确认", ""});
                break;
        }

    }


    private void showAccountManageDialog(boolean isDarkme, String accountName) {

        MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(SettingActivity.this, new String[]{"管理我的账户 [ " + (isDarkme ? "darkme" : "njit") + " ]", "账户名： " + accountName, "关闭", "注销"}, new MaterialDialogUtils.DialogBothDoSthOnClickListener() {
            @Override
            public void onConfirmButtonClick() {
            }

            @Override
            public void onCancelButtonClick() {
                if (isDarkme) {
                    MainActivity.userInfoChangeFlag = true;
                    UsualSharedPreferenceUtil.loginOutDarkMe(SettingActivity.this);
                } else {
                    UsualSharedPreferenceUtil.loginOutNjit(SettingActivity.this);
                }
                ToastUtils.showToast(SettingActivity.this, "注销成功", Toast.LENGTH_SHORT);
            }
        }, true);
    }

    private String getValueByKeyFromSharedPxx(String key) {
        return (String) SharedPreferenceUtil.get(this, key, "");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.IMAGE_REQUEST_CODE:
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumns = {MediaStore.Images.Media.DATA};
                        Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePathColumns[0]);
                        String imagePath = c.getString(columnIndex);
                        c.close();
                    }
                    break;
                case Constant.IMAGE_REQUEST_CODE_CROP:
                    ImageTools.cropRawPhoto(data.getData(), SettingActivity.this, this, false);
                    break;
                case UCrop.REQUEST_CROP:
                    Uri uri = UCrop.getOutput(data);
                    saveMainBg(uri.getPath());
                    Intent intent1 = new Intent(Constant.ACTION1);
                    setToggleDiyMainBgMemory(true);
                    final MaterialDialog mMaterialDialog = new MaterialDialog(SettingActivity.this);
                    mMaterialDialog
                            .setTitle("模糊选项")
                            .setMessage("是否需要模糊背景以增加文字辨识度")
                            .setPositiveButton("哦好的", v -> {
                                SharedPreferenceUtil.put(SettingActivity.this, "blurBackground", true);
                                mMaterialDialog.dismiss();
                                MainActivity.localBroadcastManager.sendBroadcast(intent1);
                            })
                            .setNegativeButton("不要", v -> {
                                SharedPreferenceUtil.put(SettingActivity.this, "blurBackground", false);
                                mMaterialDialog.dismiss();
                                MainActivity.localBroadcastManager.sendBroadcast(intent1);
                            });
                    mMaterialDialog.show();
                    break;
                default:
                    break;
            }
        }
    }

    public void saveMainBg(String imagePath) {
        Luban.with(this) // 初始化
                .load(imagePath) // 要压缩的图片
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // 压缩开始前调用 可以在方法内启动loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // 压缩成功后调用，返回压缩后的图片文件
                        Bitmap bitmap = ImageTools.getSmallBitmap(file.getAbsolutePath(), false);        SharedPreferenceUtil.putImage(SettingActivity.this, "main_bg", bitmap);
                    }
                    @Override
                    public void onError(Throwable e) {
                        // 压缩过程中出现异常
//                                        Toast.makeText(MainActivity.this, "丫的，翻车了" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).launch(); // 启动压缩
    }

    private void setToggleDiyMainBgMemory(boolean r) {
        SharedPreferenceUtil.put(SettingActivity.this, "toggle_diy_main_bg", r);
    }

    private void setToggleDarkModeMemory(boolean r) {
        SharedPreferenceUtil.put(SettingActivity.this, "toggle_dark_mode", r);
        String str = "开启成功， 重启后生效\n ";
        if (!r) {
            str = "关闭成功，重启后生效\n ";
        }
        MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(this, new String[]{"设置", str, "立刻重启", "稍后由我决定"}, new MaterialDialogUtils.DialogOnConfirmClickListener() {
            @Override
            public void onConfirmButtonClick() {
                MyApplication.restartApp();
            }
        }, true);

    }

    private void setToggleHandoffMemory(boolean r) {
        flagForPb1 = false;
        SharedPreferenceUtil.put(SettingActivity.this, "toggle_handoff", r);
        showSwitchResult(r);
    }


    private void setToggleAutoDetectUpdateMemory(boolean r) {
        SharedPreferenceUtil.put(SettingActivity.this, "toggle_auto_detect_update", r);
        showSwitchResult(r);
    }


    private void showSwitchResult(boolean r) {
        if (r) {
            ToastUtils.showToast(this, "开启成功", Toast.LENGTH_SHORT);
        } else {
            ToastUtils.showToast(this, "关闭成功", Toast.LENGTH_SHORT);
        }
    }

}
