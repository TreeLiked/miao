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
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lqs2.courseapp.R;
import com.example.lqs2.courseapp.adapters.SpinnerAdapter;
import com.example.lqs2.courseapp.entity.Course;
import com.example.lqs2.courseapp.utils.Constant;
import com.example.lqs2.courseapp.utils.CropUtils;
import com.example.lqs2.courseapp.utils.HtmlCodeExtractUtil;
import com.example.lqs2.courseapp.utils.ImageTools;
import com.example.lqs2.courseapp.utils.MaterialDialogUtils;
import com.example.lqs2.courseapp.utils.SharedPreferenceUtil;
import com.example.lqs2.courseapp.utils.ToastUtils;
import com.example.lqs2.courseapp.utils.Tools;
import com.github.ybq.android.spinkit.style.Wave;
import com.yalantis.ucrop.UCrop;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

public class CourseActivity extends ActivityCollector implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private List<Course> mCourseList;
    private GridLayout gridLayout;
    private ProgressBar progressBar;
    private ImageButton flushButton;
    private Spinner weekSpinner;
    private Spinner yearSpinner;
    private Spinner teamSpinner;
    private ArrayAdapter spinnerAdapter;
    private ArrayAdapter yearAdapter;
    private ArrayAdapter teamAdapter;
    private String sourceCode;
    private int width;
    private int height;
    private int weekNow;
    private SwipeRefreshLayout swipeRefresh;
    //    从initSpinner跳转的点击事项不予显示设置周数
    private boolean flag1 = false;
    private String xn;
    private String xq;


    private ImageView imageView;
    private boolean chageYearTeamFlag = false;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        LinearLayout layout = findViewById(R.id.course_layout);

        xn = (String) SharedPreferenceUtil.get(CourseActivity.this, "xnc", "");
        xq = (String) SharedPreferenceUtil.get(CourseActivity.this, "xqc", "");
        weekNow = (int) SharedPreferenceUtil.get(CourseActivity.this, "weekNow", 0);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();


        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        width = dm.widthPixels;
        height = dm.heightPixels;

        progressBar = findViewById(R.id.course_page_progress_bar);
        progressBar.setIndeterminateDrawable(new Wave());

        Intent intent = getIntent();
        sourceCode = intent.getStringExtra("sourceCode");

//        定义
        gridLayout = findViewById(R.id.grid_layout);
        imageView = findViewById(R.id.course_bg_img_view);

        weekSpinner = findViewById(R.id.switchWeek);
        yearSpinner = findViewById(R.id.switchYear);
        teamSpinner = findViewById(R.id.switchTeam);

        flushButton = findViewById(R.id.flush_course_button);
        swipeRefresh = findViewById(R.id.course_swipe_refresh);


//        FloatingActionButton fab = findViewById(R.id.fab_course);


        mCourseList = HtmlCodeExtractUtil.getCourseList(sourceCode, weekNow == 0 ? 1 : weekNow, 0);

        flushButton.setOnClickListener(this);

//        初始化控件
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

        initWeekSpinner();
        initYearTeamSpinner();


        showDate();
        showCourseBg();
    }

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
//            params1.setMargins(0, 0, 0, 0);
            gridLayout.addView(blankView, params1);
        }

//        Typeface typeface = Typeface.createFromAsset(CourseActivity.this.getAssets(), "fonts/arial.ttf");

        for (int i = 0; i < mCourseList.size(); ++i) {
            final Course course = mCourseList.get(i);
            int row = course.getClsNum();
            int col = course.getDay();
            int size = course.getClsCount();

//            ColorDrawable drawable = (ColorDrawable) getResources().getDrawable(colors[course.getColor()]);

            TextView textView = new TextView(this);
//            70.5 = 30 * 1.5 + 2 * 1.5 * 8 + 1.5

//            Typeface typeFace =Typeface.createFromAsset(getResources().getAssets(),"fonts/arial.ttf");
//            textView.setTypeface(typeFace);
            textView.setWidth((int) ((width - Tools.dip2px(54)) / 7));
            textView.setHeight(Tools.dip2px(55 * size));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.rgb(255, 255, 255));
            textView.setText(course.getDialog_name() + "\n@" + course.getDialog_location());
//            textView.setTypeface(typeface);
            textView.setTextSize(12);
            textView.setPadding(1, 1, 1, 1);
//            textView.setTexS

//            textView.setBackground(drawable);
            textView.setBackgroundResource(R.drawable.single_course_style);
            GradientDrawable myGrad = (GradientDrawable) textView.getBackground();
            myGrad.setColor(getResources().getColor(colors[course.getColor()]));
            final String times = (row <= 4 ? "上午第" : "下午第") + (size == 2 ? row + ", " + (row + 1) : row + "," + (row + 1) + "," + (row + 2)) + "节";
            textView.setOnClickListener(v -> {
                final MaterialDialog mMaterialDialog = new MaterialDialog(CourseActivity.this);
                mMaterialDialog
                        .setTitle(course.getDialog_name())
                        .setMessage("节数:  " + times
                                + "\n周数:  " + course.getDialog_weeks()
                                + "\n老师:  " + course.getDialog_teacher()
                                + "\n教室:  " + course.getDialog_location())
                        .setCanceledOnTouchOutside(true);
                mMaterialDialog.show();
            });

            //设定View在表格的行列
            GridLayout.Spec rowSpec = GridLayout.spec(row - 1, size);
            GridLayout.Spec columnSpec = GridLayout.spec(col - 1);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);

            //设置View的宽高
//            params.width = mTableDistance*2;
//            params.height = (int) getResources().getDimension(R.dimen.table_row_height) * size;
            params.setGravity(Gravity.CENTER);
//            params.setMargins(0, 0, 0, 0);
            gridLayout.addView(textView, params);
        }
        isShowProgressBar(false);
    }

//    private void saveCourseSourceCode(String source) {
//        if (source != null) {
//            FileOutputStream out = null;
//            BufferedWriter writer = null;
//            try {
//                out = openFileOutput("courseSourceCode", Context.MODE_PRIVATE);
//                writer = new BufferedWriter(new OutputStreamWriter(out));
//                writer.write(source);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    if (writer != null) {
//                        writer.close();
//                    }
//                    if (out != null) {
//                        out.close();
//                    }
//                    SharedPreferenceUtil.put(CourseActivity.this, "hasGetCourse", true);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

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
//                case Constant.IMAGE_REQUEST_CODE:
//                    if (data != null) {
//                        Uri selectedImage = data.getData();
//                        String[] filePathColumns = {MediaStore.Images.Media.DATA};
//                        Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
//                        c.moveToFirst();
//                        int columnIndex = c.getColumnIndex(filePathColumns[0]);
//                        String imagePath = c.getString(columnIndex);
//                        c.close();
//                    }
//                    break;
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

    private void changeCourseBg(Context context, Bitmap bitmap, boolean backDefault, boolean isInit) {
        runOnUiThread(() -> {
            if (backDefault) {
//                Blurry.with(context).radius(4).from(bitmap).into(imageView);
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

    private void showCourseBg() {
        boolean diy_course_bg = (boolean) SharedPreferenceUtil.get(this, "diy_course_bg", false);
        if (diy_course_bg) {
            Bitmap bitmap = SharedPreferenceUtil.getImage(this, "course_bg");
            changeCourseBg(this, bitmap, false, true);
        } else {
            new Thread(() -> changeCourseBg(CourseActivity.this, ImageTools.compressImage(BitmapFactory.decodeResource(getResources(), R.drawable.course_bg)), true, true)).start();
        }
    }

    private void isShowProgressBar(boolean isShow) {

        Message message = new Message();
        if (isShow) {
            message.what = Constant.TURN_PROGRESS_BAR_ON;
        } else {
            message.what = Constant.TURN_PROGRESS_BAR_OFF;
        }
        handler.sendMessage(message);
    }

    private void initWeekSpinner() {

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < HtmlCodeExtractUtil.MaxWeek; i++) {
            list.add(i + 1);
        }

        spinnerAdapter = new SpinnerAdapter(CourseActivity.this, R.layout.week_spiner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpinner.setAdapter(spinnerAdapter);

        if (weekNow == 0) {
            flag1 = false;
            showToast("您可能需要手动修改当前周", 1);
        } else {
//            会默认去调用onItemSelect()方法, 索引从0开始
            weekSpinner.setSelection(weekNow - 1, true);
            flag1 = true;
        }
    }

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

        yearAdapter = new SpinnerAdapter(CourseActivity.this, R.layout.week_spiner_item, xnList);
        teamAdapter = new SpinnerAdapter(CourseActivity.this, R.layout.week_spiner_item, xqList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        teamSpinner.setAdapter(teamAdapter);
        setSpinnerItemSelectedByValue(yearSpinner, xn);
        setSpinnerItemSelectedByValue(teamSpinner, xq);


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
//                String str = (String) weekSpinner.getItemAtPosition(position);
//                int week = Integer.parseInt(str.substring(str.indexOf("第") + 1, str.indexOf("周")));
                int week = (int) weekSpinner.getItemAtPosition(position);
                mCourseList.clear();
                mCourseList = HtmlCodeExtractUtil.getCourseList(sourceCode, week, 0);
                SharedPreferenceUtil.put(CourseActivity.this, "weekNow", week);
//        if (!flag1) {
//            String date = format.format(Tools.getThisWeekMonday(new Date()));
//            SharedPreferenceUtil.put(CourseActivity.this, "weekStartDate", date);
//            Log.d("date", "onItemSelected: " + date);
//            Snackbar.make(view, "已设置当前周为: 第" + week + "周", Snackbar.LENGTH_LONG).setAction("取消设置", v -> {
//                showToast("已恢复当前周为: 第" + weekNow + "周", 1);
//                weekSpinner.setSelection(weekNow - 1, true);
//            }).show();
//        }
//        flag1 = false;
//        weekSpinner.setSelection(week, true);
                showCourses();
                break;
            default:
                break;
        }
    }

    public void changeYearTeam(String year, String team, boolean isXn) {
        chageYearTeamFlag = true;
        Toast.makeText(CourseActivity.this, "课表更新", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(CourseActivity.this, LoginNJITActivity.class);
        intent.putExtra("TODO", "COURSE");
        intent.putExtra("year", year);
        intent.putExtra("team", team);
        intent.putExtra("team", team);
        intent.putExtra("isXn", isXn);
        startActivity(intent);
    }


    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        android.widget.SpinnerAdapter apsAdapter = spinner.getAdapter();
        for (int i = 0; i < apsAdapter.getCount(); i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
                spinner.setSelection(i, true);// 默认选中项
                break;
            }
        }
    }

    public void decideWeekNow() throws ParseException {
        int todayWeekWitch = Tools.getWeek();
        Log.d("date", "今天是周几 : " + todayWeekWitch);
        if (todayWeekWitch == 1) {
            String weekStartDate = (String) SharedPreferenceUtil.get(CourseActivity.this, "weekStartDate", "");
            Log.d("date", "decideWeekNow: weekStartDate : " + weekStartDate);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (!"".equals(weekStartDate)) {
                Log.d("date", "------------------");
                Date today = new Date();
                Date startDate = format.parse(weekStartDate);

                Log.d("date", "周开始日期: " + startDate);
                Log.d("date", "今天日期: " + today);

                int days = (int) ((today.getTime() - startDate.getTime()) / (1000 * 3600 * 24));
                Log.d("date", "间隔几天 : " + days);
                int i = days / 7;
                Log.d("date", "增加了几周: " + i);
                boolean hasModifiedWeek = (boolean) SharedPreferenceUtil.get(CourseActivity.this, "hasModifiedWeek", false);
                if (!hasModifiedWeek) {
                    if (i > 0) {
                        weekNow += i;
                        weekSpinner.setSelection(weekNow - 1, true);
                        flag1 = true;
                    }
                    SharedPreferenceUtil.put(CourseActivity.this, "hasModifiedWeek", true);
                }
            }
        } else {
            SharedPreferenceUtil.put(CourseActivity.this, "hasModifiedWeek", false);
        }
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
        if (chageYearTeamFlag) {
            chageYearTeamFlag = false;
            sourceCode = (String) SharedPreferenceUtil.get(this, "courseSourceCode", "");
            mCourseList.clear();
            mCourseList = HtmlCodeExtractUtil.getCourseList(sourceCode, weekNow == 0 ? 1 : weekNow, 0);
        }
//        try {
//            decideWeekNow();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }
}
