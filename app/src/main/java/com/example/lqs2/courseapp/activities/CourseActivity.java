package com.example.lqs2.courseapp.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.SpinnerAdapter;
import com.example.lqs2.courseapp.entity.Course;
import com.example.lqs2.courseapp.global.ThreadPoolExecutorFactory;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.CropUtils;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.ImageTools;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.SharedPreferenceUtil;
import com.example.lqs2.courseapp.utils.StatusBarUtils;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.example.lqs2.courseapp.utils.Tools;
import com.github.ybq.android.spinkit.style.Wave;
import com.yalantis.ucrop.UCrop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * 课表活动
 *
 * @author lqs2
 */
public class CourseActivity extends ActivityCollector implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private List<Course> mCourseList;
    private GridLayout gridLayout;
    private ProgressBar progressBar;
    private ImageButton flushButton;
    private Spinner weekSpinner;
    private Spinner yearSpinner;
    private Spinner teamSpinner;
    private String sourceCode;
    private int width;
    private int weekNow;
    private SwipeRefreshLayout swipeRefresh;

    private String xn;
    private String xq;


    private ImageView imageView;
    private boolean changeYearTeamFlag = false;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.TURN_PROGRESS_BAR_ON:
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case Constant.TURN_PROGRESS_BAR_OFF:
                    progressBar.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    };

    /**
     * 设置spinner中的值
     *
     * @param spinner 所设置的spinner
     * @param value   所设置的值
     */
    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        android.widget.SpinnerAdapter apsAdapter = spinner.getAdapter();
        for (int i = 0; i < apsAdapter.getCount(); i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);
                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        xn = (String) SharedPreferenceUtil.get(CourseActivity.this, "xnc", "");
        xq = (String) SharedPreferenceUtil.get(CourseActivity.this, "xqc", "");
        weekNow = (int) SharedPreferenceUtil.get(CourseActivity.this, "weekNow", 0);

        StatusBarUtils.setStatusTransparent(this);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;


        bindViews();
        initComponents();

        Intent intent = getIntent();
        sourceCode = intent.getStringExtra("sourceCode");
        mCourseList = HtmlCodeExtractUtil.getCourseList(sourceCode, weekNow == 0 ? 1 : weekNow, 0);

        weatherAddWeek();
        initWeekSpinner();
        initYearTeamSpinner();
        showDate();
        showCourseBg();
    }

    /**
     * 初始化组件
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initComponents() {
        flushButton.setOnClickListener(this);
        progressBar.setIndeterminateDrawable(new Wave());
        weekSpinner.setGravity(View.TEXT_ALIGNMENT_CENTER);
        weekSpinner.setOnItemSelectedListener(this);
        yearSpinner.setGravity(View.TEXT_ALIGNMENT_CENTER);
        yearSpinner.setOnItemSelectedListener(this);
        teamSpinner.setGravity(View.TEXT_ALIGNMENT_CENTER);
        teamSpinner.setOnItemSelectedListener(this);
        swipeRefresh.setColorSchemeResources(R.color.r4);
        swipeRefresh.setOnRefreshListener(() -> {
            Intent intent1 = new Intent(CourseActivity.this, MainActivity.class);
            startActivity(intent1);
            swipeRefresh.setRefreshing(false);
        });
    }

    /**
     * 绑定视图
     */
    private void bindViews() {
        progressBar = findViewById(R.id.course_page_progress_bar);
        gridLayout = findViewById(R.id.grid_layout);
        imageView = findViewById(R.id.course_bg_img_view);
        weekSpinner = findViewById(R.id.switchWeek);
        yearSpinner = findViewById(R.id.switchYear);
        teamSpinner = findViewById(R.id.switchTeam);
        flushButton = findViewById(R.id.flush_course_button);
        swipeRefresh = findViewById(R.id.course_swipe_refresh);
    }

    /**
     * 判断是否是周一并且增加一周
     */
    private void weatherAddWeek() {
        int weekToday = Tools.getWeek();
        Calendar cal = Calendar.getInstance();
        String key = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1) + "" + cal.get(Calendar.DAY_OF_MONTH);
        if (weekToday == 1) {
            boolean added = (boolean) SharedPreferenceUtil.get(this, key, false);
            if (!added) {
                SharedPreferenceUtil.put(this, key, true);
                weekNow++;
                SharedPreferenceUtil.put(this, "weekNow", weekNow);

            }
        } else {
            Date weekMonday = Tools.getThisWeekMonday(new Date());
            cal.setTime(weekMonday);
            String key1 = cal.get(Calendar.YEAR) + "" + (cal.get(Calendar.MONTH) + 1) + "" + cal.get(Calendar.DAY_OF_MONTH);
            boolean added = (boolean) SharedPreferenceUtil.get(this, key1, false);
            if (!added) {
                SharedPreferenceUtil.put(this, key, true);
                weekNow++;
                SharedPreferenceUtil.put(this, "weekNow", weekNow);
            }
        }
    }

    /**
     * 显示今天的日期
     */
    @SuppressLint("SetTextI18n")
    private void showDate() {
        int[] days = Tools.getTimeInterval(new Date());

        int month = Tools.getMonth();
        int day = Tools.getCurrentMonthDay();

        TextView t = findViewById(R.id.date);
        t.setText(month + "月");
        TextView t1 = findViewById(R.id.date_1);
        TextView t2 = findViewById(R.id.date_2);
        TextView t3 = findViewById(R.id.date_3);
        TextView t4 = findViewById(R.id.date_4);
        TextView t5 = findViewById(R.id.date_5);
        TextView t6 = findViewById(R.id.date_6);
        TextView t7 = findViewById(R.id.date_7);

        if (days[0] > days[6]) {
//            day< 20, 表示现在是下一个月
            if (day < 20) {
                month--;
            }
            if (days[0] < days[1]) {
                t1.setText(month + "." + days[0]);
            } else {
                t1.setText(month + "." + days[0]);
                month++;
            }
            if (days[1] < days[2]) {
                t2.setText(month + "." + days[1]);
            } else {
                t2.setText(month + "." + days[1]);
                month++;
            }
            if (days[2] < days[3]) {
                t3.setText(month + "." + days[2]);
            } else {
                t3.setText(month + "." + days[2]);
                month++;
            }
            if (days[3] < days[4]) {
                t4.setText(month + "." + days[3]);
            } else {
                t4.setText(month + "." + days[3]);
                month++;
            }
            if (days[4] < days[5]) {
                t5.setText(month + "." + days[4]);
            } else {
                t5.setText(month + "." + days[4]);
                month++;
            }
            if (days[5] < days[6]) {
                t6.setText(month + "." + days[5]);
                t7.setText(month + "." + days[6]);
            } else {
                t6.setText(month + "." + days[5]);
                month++;
                t7.setText(month + "." + days[6]);
            }
        } else {
            t1.setText(month + "." + days[0]);
            t2.setText(month + "." + days[1]);
            t3.setText(month + "." + days[2]);
            t4.setText(month + "." + days[3]);
            t5.setText(month + "." + days[4]);
            t6.setText(month + "." + days[5]);
            t7.setText(month + "." + days[6]);
        }

        if (days[0] == day) {
            t1.setTextColor(getResources().getColor(R.color.r1));
            t1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            return;
        }
        if (days[1] == day) {
            t2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            t2.setTextColor(getResources().getColor(R.color.fri_title_bg));
            return;
        }
        if (days[2] == day) {
            t3.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            t3.setTextColor(getResources().getColor(R.color.r5));
            return;
        }
        if (days[3] == day) {
            t4.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            t4.setTextColor(getResources().getColor(R.color.r7));
            return;
        }
        if (days[4] == day) {
            t5.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            t5.setTextColor(getResources().getColor(R.color.pink_pressed));
            return;
        }
        if (days[5] == day) {
            t6.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            t6.setTextColor(getResources().getColor(R.color.r12));
            return;
        }
        if (days[6] == day) {
            t7.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
            t7.setTextColor(Color.BLUE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flush_course_button:
                MaterialDialog[] dialogs = new MaterialDialog[1];

                dialogs[0] = MaterialDialogUtils.getItemListDialog(this, "更多", (parent, view, position, id) -> {
                    switch (position) {
                        case 0:
                            dialogs[0].dismiss();
                            changeYearTeam((String) SharedPreferenceUtil.get(this, "xnc", ""), (String) SharedPreferenceUtil.get(this, "xqc", ""), false);
                            break;
                        case 1:
                            CropUtils.openAlbumAndCrop(this);
                            dialogs[0].dismiss();
                            break;
                        case 2:
                            SharedPreferenceUtil.put(this, "diy_course_bg", false);
                            new Thread(() -> changeCourseBg(CourseActivity.this, ImageTools.compressImage(BitmapFactory.decodeResource(getResources(), R.drawable.course_bg)), true, false)).start();
                            dialogs[0].dismiss();
                            break;
                        default:
                            break;
                    }
                }, new ArrayList<String>() {{
                    add("更新课表");
                    add("更换背景");
                    add("恢复默认背景");
                }});
                dialogs[0].show();

                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.IMAGE_REQUEST_CODE_CROP:
                    ImageTools.cropRawPhoto(data.getData(), CourseActivity.this, this, false);
                    break;
                case UCrop.REQUEST_CROP:
                    Uri uri = UCrop.getOutput(data);
                    try {
                        Bitmap bitmap = ImageTools.getBitmapFormUri(this, uri);
                        if (bitmap != null) {
                            SharedPreferenceUtil.put(this, "diy_course_bg", true);
                            SharedPreferenceUtil.putImage(this, "course_bg", bitmap);
                            changeCourseBg(this, bitmap, false, false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 显示课程
     */
    @SuppressLint("SetTextI18n")
    private void showCourses() {
        isShowProgressBar(true);
        gridLayout.removeAllViews();
        int[] colors = new int[]{R.color.r1, R.color.r2, R.color.r3, R.color.r4,
                R.color.r5, R.color.r6, R.color.r7, R.color.r8, R.color.r9, R.color.r10, R.color.r11, R.color.r12, R.color.r14};
//        先填充对角线，否则空列偏移
        for (int i = 0; i < 7; i++) {
            TextView blankView = new TextView(this);
            blankView.setWidth((int) ((width - Tools.dip2px(40)) / 7));
            blankView.setHeight(Tools.dip2px(50 * 2));
            blankView.setGravity(Gravity.CENTER);
            GridLayout.Spec rowSpec1 = GridLayout.spec(i, 2);
            GridLayout.Spec columnSpec1 = GridLayout.spec(i);
            GridLayout.LayoutParams params1 = new GridLayout.LayoutParams(rowSpec1, columnSpec1);
            params1.setGravity(Gravity.CENTER);
            gridLayout.addView(blankView, params1);
        }

        for (int i = 0; i < mCourseList.size(); ++i) {
            final Course course = mCourseList.get(i);
            int row = course.getClsNum();
            int col = course.getDay();
            int size = course.getClsCount();

            TextView textView = new TextView(this);
//            70.5 = 30 * 1.5 + 2 * 1.5 * 8 + 1.5
            textView.setWidth((width - Tools.dip2px(54)) / 7);
            textView.setHeight(Tools.dip2px(55 * size));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.rgb(255, 255, 255));
            textView.setText(course.getDialogName() + "\n@" + course.getDialogLocation());
            textView.setTextSize(12);
            textView.setPadding(1, 1, 1, 1);
            textView.setBackgroundResource(R.drawable.single_course_style);
            GradientDrawable myGrad = (GradientDrawable) textView.getBackground();
            myGrad.setColor(getResources().getColor(colors[course.getColor()]));
            final String times = (row <= 4 ? "上午第" : "下午第") + (size == 2 ? row + ", " + (row + 1) : row + "," + (row + 1) + "," + (row + 2)) + "节";
            textView.setOnClickListener(v -> {
                final MaterialDialog mMaterialDialog = new MaterialDialog(CourseActivity.this);
                mMaterialDialog
                        .setTitle(course.getDialogName())
                        .setMessage("节数:  " + times
                                + "\n周数:  " + course.getDialogWeeks()
                                + "\n老师:  " + course.getDialogTeacher()
                                + "\n教室:  " + course.getDialogLocation())
                        .setCanceledOnTouchOutside(true);
                mMaterialDialog.show();
            });
//            设定View在表格的行列
            GridLayout.Spec rowSpec = GridLayout.spec(row - 1, size);
            GridLayout.Spec columnSpec = GridLayout.spec(col - 1);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
            params.setGravity(Gravity.CENTER);
            gridLayout.addView(textView, params);
        }
        isShowProgressBar(false);
    }

    /**
     * 修改课程的背景
     *
     * @param context     上下文
     * @param bitmap      背景位图
     * @param backDefault 是否恢复默认背景
     * @param isInit      是否初始化加载背景，如果false，则显示背景更换结果
     */
    private void changeCourseBg(Context context, Bitmap bitmap, boolean backDefault, boolean isInit) {
        runOnUiThread(() -> {
            if (backDefault) {
                Glide.with(context).load(bitmap).into(imageView);
                if (!isInit) {
                    ToastUtils.showToast(CourseActivity.this, "恢复默认背景成功", Toast.LENGTH_LONG);
                }
            } else {
                Glide.with(context).load(bitmap).into(imageView);
                if (!isInit) {
                    ToastUtils.showToast(CourseActivity.this, "更换背景成功", Toast.LENGTH_LONG);
                }
            }
        });

    }

    /**
     * 加载背景
     */
    private void showCourseBg() {
        boolean diyCourseBg = (boolean) SharedPreferenceUtil.get(this, "diy_course_bg", false);
        if (diyCourseBg) {
            Bitmap bitmap = SharedPreferenceUtil.getImage(this, "course_bg");
            changeCourseBg(this, bitmap, false, true);
        } else {
            ThreadPoolExecutorFactory.getThreadPoolExecutor().execute(() -> changeCourseBg(CourseActivity.this, ImageTools.compressImage(BitmapFactory.decodeResource(getResources(), R.drawable.course_bg)), true, true));
        }
    }

    /**
     * 是否显示加载条
     *
     * @param isShow 显示/隐藏
     */
    private void isShowProgressBar(boolean isShow) {
        Message message = new Message();
        if (isShow) {
            message.what = Constant.TURN_PROGRESS_BAR_ON;
        } else {
            message.what = Constant.TURN_PROGRESS_BAR_OFF;
        }
        handler.sendMessage(message);
    }

    /**
     * 初始化周下拉框
     */
    private void initWeekSpinner() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < HtmlCodeExtractUtil.MaxWeek; i++) {
            list.add(i + 1);
        }
        ArrayAdapter spinnerAdapter = new SpinnerAdapter(CourseActivity.this, R.layout.week_spiner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpinner.setAdapter(spinnerAdapter);

        if (weekNow == 0) {
            showToast("您可能需要手动修改当前周", 1);
        } else {
//            会默认去调用onItemSelect()方法, 索引从0开始
            weekSpinner.setSelection(weekNow - 1, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.switchYear:
                String year1 = (String) yearSpinner.getItemAtPosition(position);
                if (!xn.equals(year1)) {
                    String team1 = (String) teamSpinner.getSelectedItem();
                    changeYearTeam(year1, team1, true);
                }
                break;
            case R.id.switchTeam:
                String team2 = (String) teamSpinner.getItemAtPosition(position);
                if (!xq.equals(team2)) {
                    String year2 = (String) yearSpinner.getSelectedItem();
                    changeYearTeam(year2, team2, false);
                }
                break;
            case R.id.switchWeek:
                int week = (int) weekSpinner.getItemAtPosition(position);
                mCourseList.clear();
                mCourseList = HtmlCodeExtractUtil.getCourseList(sourceCode, week, 0);
                SharedPreferenceUtil.put(CourseActivity.this, "weekNow", week);
                showCourses();
                sendCourseChangedBC();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化学年/学期下拉框
     */
    private void initYearTeamSpinner() {

        List<String> xnList = new ArrayList<>();
        List<String> xqList = new ArrayList<>();
        String xns = (String) SharedPreferenceUtil.get(this, "xns", "");
        String xqs = (String) SharedPreferenceUtil.get(this, "xqs", "");
        if (xns != null) {
            Collections.addAll(xnList, xns.split("\t"));
        }
        if (xqs != null) {
            Collections.addAll(xqList, xqs.split("\t"));
        }
        ArrayAdapter yearAdapter = new SpinnerAdapter(CourseActivity.this, R.layout.week_spiner_item, xnList);
        ArrayAdapter teamAdapter = new SpinnerAdapter(CourseActivity.this, R.layout.week_spiner_item, xqList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        teamSpinner.setAdapter(teamAdapter);
        setSpinnerItemSelectedByValue(yearSpinner, xn);
        setSpinnerItemSelectedByValue(teamSpinner, xq);


    }

    /**
     * 修改学年/学期
     *
     * @param year 当前下拉框中的学年
     * @param team 当前下拉框中的学期
     * @param isXn 是否修改学年，如果false，则修改学期
     */
    public void changeYearTeam(String year, String team, boolean isXn) {
        changeYearTeamFlag = true;
        Toast.makeText(CourseActivity.this, "课表更新", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(CourseActivity.this, LoginNjitActivity.class);
        intent.putExtra("TODO", "COURSE");
        intent.putExtra("year", year);
        intent.putExtra("team", team);
        intent.putExtra("team", team);
        intent.putExtra("isXn", isXn);
        startActivity(intent);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        showToast("什么也没有选中", 0);
    }


    private void showToast(String message, int time) {
        Toast.makeText(CourseActivity.this, message, time == 0 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (changeYearTeamFlag) {
            changeYearTeamFlag = false;
            sourceCode = (String) SharedPreferenceUtil.get(this, "courseSourceCode", "");
            mCourseList.clear();
            mCourseList = HtmlCodeExtractUtil.getCourseList(sourceCode, weekNow == 0 ? 1 : weekNow, 0);
        }
    }

    /**
     * 发送课程信息被修改的广播给主页活动
     */
    private void sendCourseChangedBC() {
        Intent intent = new Intent(Constant.ACTION3);
        MainActivity.localBroadcastManager.sendBroadcast(intent);
    }
}
