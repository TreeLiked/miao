package com.example.lqs2.courseapp.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 设置
 *
 * @author lqs2
 */
public class SettingActivity extends ActivityCollector implements View.OnClickListener {
    private TextView settingAccount;
    private TextView settingSig;
    private TextView settingLoginOut;
    private TextView settingCheckUpdateHand;
    private TextView settingAbout;
    private TextView settingChooseDefaultBg;

    private com.kyleduo.switchbutton.SwitchButton toggleDiyMainBgSwitch;
    private com.kyleduo.switchbutton.SwitchButton toggleHandoffSwitch;
    private com.kyleduo.switchbutton.SwitchButton toggleAutoDetectUpdateSwitch;
    private com.kyleduo.switchbutton.SwitchButton toggleDarkmeModeSwitch;


    /**
     * 又忘了
     */
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

    /**
     * 绑定监听事件
     */
    private void bindListeners() {
        settingAccount.setOnClickListener(this);
        settingSig.setOnClickListener(this);
        settingLoginOut.setOnClickListener(this);
        settingAbout.setOnClickListener(this);
        settingCheckUpdateHand.setOnClickListener(this);
        settingChooseDefaultBg.setOnClickListener(this);

        toggleDiyMainBgSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, Constant.IMAGE_REQUEST_CODE_CROP);
            } else {
                changeDefaultBg(0, true);
                setToggleDiyMainBgMemory(false);
            }
        });

        toggleHandoffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !flagForPb1) {
                flagForPb1 = true;
                ObjectAnimator animator = ObjectAnimator.ofInt(pb1, "progress", 0, 1500);
                animator.setDuration(500);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        pb1.setVisibility(View.VISIBLE);
                        toggleHandoffSwitch.setChecked(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        toggleHandoffSwitch.setChecked(true);
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
        toggleAutoDetectUpdateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setToggleAutoDetectUpdateMemory(true);
            } else {
                setToggleAutoDetectUpdateMemory(false);
            }
        });

        toggleDarkmeModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setToggleDarkModeMemory(true);
                setDefaultBgNum(1);
            } else {
                setToggleDarkModeMemory(false);
                setDefaultBgNum(0);
            }
        });
    }

    /**
     * 绑定视图
     */
    private void bindViews() {
        settingAccount = findViewById(R.id.setting_account);
        settingSig = findViewById(R.id.setting_sig);
        settingCheckUpdateHand = findViewById(R.id.setting_check_update_hand);
        settingLoginOut = findViewById(R.id.setting_login_out);
        settingChooseDefaultBg = findViewById(R.id.setting_choose_default_bg);
        toggleDiyMainBgSwitch = findViewById(R.id.setting_diy_main_bg_switch);
        toggleHandoffSwitch = findViewById(R.id.setting_toggle_handoff_switch);
        toggleAutoDetectUpdateSwitch = findViewById(R.id.setting_toggle_auto_detect_update_switch);
        toggleDarkmeModeSwitch = findViewById(R.id.setting_toggle_dark_mode_switch);
        settingAbout = findViewById(R.id.setting_about);
        pb1 = findViewById(R.id.setting_pb_1);
    }

    /**
     * 发送首页壁纸更换广播
     */
    private void sendChangeMainBgBroadcast() {
        Intent intent = new Intent(Constant.ACTION2);
        MainActivity.localBroadcastManager.sendBroadcast(intent);
    }

    /**
     * 保存当然选择的壁纸
     *
     * @param i 壁纸编号
     */
    private void setDefaultBgNum(int i) {
        SharedPreferenceUtil.put(this, "default_main_bg_num", i);
    }


    /**
     * 修改默认壁纸
     *
     * @param i           壁纸编号
     * @param backDefault 是否恢复默认
     */
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

    /**
     * 初始化switch
     */
    private void initOptions() {
        if ((boolean) SharedPreferenceUtil.get(SettingActivity.this, "toggle_diy_main_bg", false)) {
            toggleDiyMainBgSwitch.setCheckedImmediatelyNoEvent(true);
        }

        if ((boolean) SharedPreferenceUtil.get(SettingActivity.this, "toggle_handoff", false)) {
            toggleHandoffSwitch.setCheckedImmediatelyNoEvent(true);
        }

        if ((boolean) SharedPreferenceUtil.get(SettingActivity.this, "toggle_auto_detect_update", false)) {
            toggleAutoDetectUpdateSwitch.setCheckedImmediatelyNoEvent(true);
        }
        if (UsualSharedPreferenceUtil.isDarkModeOn(SettingActivity.this)) {
            toggleDarkmeModeSwitch.setCheckedImmediatelyNoEvent(true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_account:
                String darkmeUn = getValueByKeyFromSharedPxx("darkme_un");
                String xh = getValueByKeyFromSharedPxx("xh");

                if (TextUtils.isEmpty(xh) && TextUtils.isEmpty(darkmeUn)) {
                    ToastUtils.showToast(this, "暂无登录", Toast.LENGTH_SHORT);
                } else {
                    ArrayList<String> items = new ArrayList<>();
                    boolean f0 = false;
                    boolean f1 = false;
                    if (!TextUtils.isEmpty(darkmeUn)) {
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
                                showAccountManageDialog(true, darkmeUn);
                            } else if (1 == position) {
                                showAccountManageDialog(false, xh);
                            }
                        } else {
                            if (finalF) {
                                if (0 == position) {
                                    showAccountManageDialog(true, darkmeUn);
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
                            if ("".contentEquals(sigEditText.getText())) {
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
//            case R.id.setting_test_h5:
//                Intent i = new Intent(this, WebViewActivity.class);
//                startActivity(i);
//                break;

            case R.id.setting_login_out:
                MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(SettingActivity.this, new String[]{"确认登出", "此操作不可撤销", "关闭", "确认登出"}, new MaterialDialogUtils.AbstractDialogOnCancelClickListener() {
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
                VersionUtils.checkUpdate(this, this, false, settingCheckUpdateHand);
                break;

            case R.id.setting_about:

            default:
                String vn = VersionUtils.getVersionName();
                String c = "版  本  号：" + vn + "\n" +
                        "版权所有：" + "@cm、\t@djt、\t@qcy、\t@TreeLiked、\t@ttw"
                        + "\n您可以在：\nhttps://github.com/TreeLiked/miao \n上找到此项目";
                MaterialDialogUtils.showSimpleConfirmDialog(SettingActivity.this, new String[]{"关于喵", c, "确认", ""});
                break;
        }

    }


    /**
     * 显示账户对话框
     *
     * @param isDarkme    是否为darkme账户
     * @param accountName 账户名
     */
    private void showAccountManageDialog(boolean isDarkme, String accountName) {
        MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(SettingActivity.this, new String[]{"管理我的账户 [ " + (isDarkme ? "darkme" : "njit") + " ]", "账户名： " + accountName, "关闭", "注销"}, new MaterialDialogUtils.AbstractDialogBothDoSthOnClickListener() {
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

    /**
     * 获取shared value
     *
     * @param key value 的key
     * @return value
     */
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
                        assert selectedImage != null;
                        Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                        assert c != null;
                        c.moveToFirst();
                        c.close();
                    }
                    break;
                case Constant.IMAGE_REQUEST_CODE_CROP:
                    ImageTools.cropRawPhoto(data.getData(), SettingActivity.this, this, false);
                    break;
                case UCrop.REQUEST_CROP:
                    Uri uri = UCrop.getOutput(data);
                    assert uri != null;
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
        Luban.with(this)
                .load(imagePath)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        // 压缩成功后调用，返回压缩后的图片文件
                        Bitmap bitmap = ImageTools.getSmallBitmap(file.getAbsolutePath(), false);
                        SharedPreferenceUtil.putImage(SettingActivity.this, "main_bg", bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();
    }

    /**
     * 设置是否自定义壁纸
     *
     * @param r 是/否
     */
    private void setToggleDiyMainBgMemory(boolean r) {
        SharedPreferenceUtil.put(SettingActivity.this, "toggle_diy_main_bg", r);
    }

    /**
     * 设置是否打开黑暗模式
     *
     * @param r 是/否
     */
    private void setToggleDarkModeMemory(boolean r) {
        SharedPreferenceUtil.put(SettingActivity.this, "toggle_dark_mode", r);
        String str = "开启成功， 重启后生效\n ";
        if (!r) {
            str = "关闭成功，重启后生效\n ";
        }
        MaterialDialogUtils.showYesOrNoDialogWithBothSthTodo(this, new String[]{"设置", str, "立刻重启", "稍后由我决定"}, new MaterialDialogUtils.AbstractDialogOnConfirmClickListener() {
            @Override
            public void onConfirmButtonClick() {
                MyApplication.restartApp();
            }
        }, true);

    }

    /**
     * 是否打开handoff功能
     *
     * @param r 是/否
     */
    private void setToggleHandoffMemory(boolean r) {
        flagForPb1 = false;
        SharedPreferenceUtil.put(SettingActivity.this, "toggle_handoff", r);
        showSwitchResult(r);
    }


    /**
     * 是否打开自动更新
     *
     * @param r 是/否
     */
    private void setToggleAutoDetectUpdateMemory(boolean r) {
        SharedPreferenceUtil.put(SettingActivity.this, "toggle_auto_detect_update", r);
        showSwitchResult(r);
    }


    /**
     * 显示开关切换的结果
     *
     * @param r 打开/关闭
     */
    private void showSwitchResult(boolean r) {
        if (r) {
            ToastUtils.showToast(this, "开启成功", Toast.LENGTH_SHORT);
        } else {
            ToastUtils.showToast(this, "关闭成功", Toast.LENGTH_SHORT);
        }
    }

}
