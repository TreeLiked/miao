package com.example.lqs2.courseapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MyDrawerLayout extends DrawerLayout {

    private DrawerLayout drawerLayout;

    public MyDrawerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.drawer, this);

        final NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setCheckedItem(R.id.nav_course);

        drawerLayout = findViewById(R.id.drawer_layout);

        //        导航栏的动作监听
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                boolean hasLogin_JW = (boolean) SharedPreferenceUtil.get(MyApplication.getContext(), "hasLoginJW", false);
//                switch (item.getItemId()) {
//                    case R.id.nav_course:
//                        Log.d("CourseApp", "hasLoginJW" + hasLogin_JW);
//                        if (!hasLogin_JW) {
//                            Intent intent = new Intent(MyApplication.getContext(), LoginNjitActivity.class);
//                            intent.putExtra("TODO", "COURSE");
//                            MyApplication.getContext().startActivity(intent);
//                        } else {
//                            String data = loadCourseCode();
//                            Intent intent = new Intent(MyApplication.getContext(), CourseActivity.class);
//                            intent.putExtra("sourceCode", data);
//                            MyApplication.getContext().startActivity(intent);
//
//                        }
//                        break;
//                    case R.id.nav_e_card:
//                        boolean hasLogin_ECARD = (boolean) SharedPreferenceUtil.get(MyApplication.getContext(), "hasLoginECARD", false);
//                        boolean hasRememberPW_ECARED = (boolean) SharedPreferenceUtil.get(MyApplication.getContext(), "remember_password_ecard", false);
//                        if (!hasLogin_ECARD) {
//                            Intent intent = new Intent(MyApplication.getContext(), Login_ECARD_Activity.class);
//                            MyApplication.getContext().startActivity(intent);
//
//                        } else if (!hasRememberPW_ECARED) {
//                            //TODO
//                        }
//                        break;
//
//                    case R.id.nav_grade:
//                        final List<String> items = new ArrayList<>();
//                        items.add("教务系统成绩查询");
//                        items.add("四六级成绩查询");
//                        final ArrayAdapter<String> adapter
//                                = new ArrayAdapter<>(MyApplication.getContext(), android.R.layout.simple_list_item_1, items);
//
//                        final ListView listView = new ListView(MyApplication.getContext());
//                        final MaterialDialog alert = new MaterialDialog(MyApplication.getContext()).setTitle(
//                                "MaterialDialog").setContentView(listView);
//
//                        listView.setLayoutParams(new ViewGroup.LayoutParams(
//                                ViewGroup.LayoutParams.MATCH_PARENT,
//                                ViewGroup.LayoutParams.MATCH_PARENT));
//                        float scale = getResources().getDisplayMetrics().density;
//                        int dpAsPixels = (int) (8 * scale + 0.5f);
//                        listView.setPadding(0, dpAsPixels, 0, dpAsPixels);
//                        listView.setDividerHeight(0);
//                        listView.setAdapter(adapter);
//
//                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                if (position == 0) {
//                                    Toast.makeText(MyApplication.getContext(), "欢迎使用成绩查询系统", Toast.LENGTH_LONG).show();
//                                    Intent intent = new Intent(MyApplication.getContext(), LoginNjitActivity.class);
//                                    intent.putExtra("TODO", "GRADE");
//                                    MyApplication.getContext().startActivity(intent);
//                                    alert.dismiss();
//                                }
//
//                            }
//                        });
//                        alert.setContentView(listView);
//                        alert.setCanceledOnTouchOutside(true);
//                        alert.show();
//                        break;
//
//                    case R.id.nav_network:
//                        boolean hasSavePwd = (Boolean) SharedPreferenceUtil.get(MyApplication.getContext(), "remember_password_jw", false);
//                        if (!hasSavePwd) {
//                            Intent intent = new Intent(MyApplication.getContext(), LoginNjitActivity.class);
//                            intent.putExtra("TODO", "NETWORK");
//                            MyApplication.getContext().startActivity(intent);
//                        } else {
//                            Intent intent = new Intent(MyApplication.getContext(), NetworkActivity.class);
//                            MyApplication.getContext().startActivity(intent);
//                        }
//                        break;
//
//
//                    case R.id.nav_should_do:
//                        Intent intent = new Intent(MyApplication.getContext(), DailyRecordActivity.class);
//                        MyApplication.getContext().startActivity(intent);
//                        break;
//
//                    case R.id.nav_settings:
//                        Intent set_intent = new Intent(MyApplication.getContext(), SettingActivity.class);
//                        MyApplication.getContext().startActivity(set_intent);
//                        break;
//
//                    case R.id.nav_login_out:
//                        final MaterialDialog mMaterialDialog = new MaterialDialog(MyApplication.getContext());
//                        mMaterialDialog
//                                .setTitle("确认登出")
//                                .setMessage("此操作不可撤销")
//                                .setPositiveButton("确认", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        MyApplication.getContext().deleteFile("courseSourceCode");
//                                        SharedPreferenceUtil.clear(MyApplication.getContext());
//                                        Snackbar.make(navigationView, "已清除所有信息", Snackbar.LENGTH_LONG).show();
//                                        mMaterialDialog.dismiss();
//                                    }
//                                })
//                                .setNegativeButton("取消", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        mMaterialDialog.dismiss();
//                                    }
//                                });
//                        mMaterialDialog.show();
//                        break;
//                    default:
//                        break;
//
//                }
//                drawerLayout.closeDrawers();
//                return true;
//            }
//        });
    }

    private String loadCourseCode() {
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream in = MyApplication.getContext().openFileInput("courseSourceCode");
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

}
