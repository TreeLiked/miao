package com.example.lqs2.courseapp.utils;

import android.os.Environment;

public class Constant {

    //    教务网首页
    public static final String main_url = "http://jwjx.njit.edu.cn/Default2.aspx";
    //    重定向网页
    public static final String forward_url = "http://jwjx.njit.edu.cn/xs_main.aspx?xh=";


    public static final String refer_url = "http://jwjx.njit.edu.cn/";
    public static final String get_ecardPW_url = "http://ecard.njit.edu.cn/GetPW.aspx";
    public static final String tzgg_url = "http://www.njit.edu.cn/index/tzgg.htm";
    public static final String bing_pic_api = "http://guolin.tech/api/bing_pic";
    //    网络服务登录网址
    public static final String net_login_url = "http://net.njit.edu.cn:8080/Self/LogoutAction.action";
    //    校历选择网址
    public static final String school_calendar_choose_url = "http://jwc.njit.edu.cn/xl_list.jsp?urltype=tree.TreeTempUrl&wbtreeid=1129";


    public static final String server_off = "服务器发生错误";
    public static final String network_off = "网络连接断开";
    public static final String code_flush_fail = "验证码刷新失败";
    public static final String login_error_cord = "验证码不正确";
    public static final String login_error_pw = "密码错误";
    public static final String login_error_noComment = "你还没有进行本学期的教学质量评价,在本系统的“教学质量评价”栏中完成评价工作后，才能进入系统";
    public static final String login_success_info = "QAQ登录成功";
    public static final String login_error_xh = "用户名不存在或未按照要求参加教学活动";
    public static final String login_jw_success = "为保障您的个人信息的安全，请点在退出时，请击安全退出";


    public static final String gradeQueryWelcome = "欢迎使用成绩查询系统";
    public static final String gradeQueryItem1 = "1、教务系统成绩查询";
    public static final String gradeQueryItem2 = "2、四六级成绩查询";


    public static final String main_no_course_left_info = "今天已经没有课了, 快去休息一下吧";
    public static final String main_no_course_info = "今天没有课, 快去好好放松一下吧";


    public static final String no_login_info = "您还没有登录";
    public static final String no_sig_info = "一句话介绍一下自己吧";


    public static final String no_course_info = "该学期暂无成绩";
    public static final String course_all_passed_info = "恭喜你， 全部通过";


    public static final String MEMO_HELP_TEXT = "1、备忘录可被创建和删除\n" +
            "2、备忘录有两种类型\n" +
            "       ·一般\n" +
            "       ·紧急\n" +
            "3、备忘录有两种状态\n" +
            "       ·待完成\n" +
            "       ·已完成\n" +
            "4、登录后可与[darkme.cn]同步";

    public static final String daily_introduction = "您可以理解这为一个记事本, 每天可以重复编辑, 但是今天之前禁止修改, 它会在您返回的时候自动保存, 您只可以在每周的星期一查看之前的记录";
    public static final String create_daily_table = "create table Daily ("
            + "id integer primary key autoincrement, "
            + "date String, "
            + "content String)";

    public static final int SERVER_ERROR = 0;
    public static final int INFO_ERROR = 1;
    public static final int TRANSFER_CODE = 2;
    public static final int TURN_PROGRESS_BAR_ON = 3;
    public static final int TURN_PROGRESS_BAR_OFF = 4;
    public static final int SHOW_TOAST = 5;


    public static final String __VIEWSTATE_COMMENT = "";


    //    个人中心请求
    public static final String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
    public static final int CENTER_FILE = 101;
    public static final int LOCAL_FILE = 102;
    public static final int CENTER_MEMO = 103;
    public static final int CENTER_FRIEND = 104;


    //    广播
    public static final String ACTION1 = "com.example.lqs2.courseapp.OPEN_DIY_MAIN_BG";
    public static final String ACTION2 = "com.example.lqs2.courseapp.CLOSE_DIY__MAIN_BG";


    //    权限请求码
    public static final int IMAGE_REQUEST_CODE = 1;
    public static final int IMAGE_REQUEST_CODE_CROP = 202;
    public static final int GET_CONTENT = 301;
    public static final int SET_CONTENT = 302;


    //    请求网址 darkme.cn


    private static String LQS2_1_SecretId = "AKIDysfHk4P8sPktrzlq34u0ztdBXlofpm3B";
    private static String LQS2_1_SecretKey = "50iFKLDuPkuwnSvGPM7g3FH9pLn37zjH";
    private static String APPID = "1253931949";


    public static String BASE_URL = "http://darkme.cn:8880/android";
    public static String upload_image_url = "http://darkme.cn:8880/android/image/ui";
    public static String img_access_url = "http://darkme.cn:8880/file/img";


    public static String file_show_url = "http://darkme.cn:8880/android/file/gmf";
    public static String file_del_url = "http://darkme.cn:8880/android/file/rmf";
    public static String file_sear_url = "http://darkme.cn:8880/android/file/smf";
    public static String file_generate_url = "http://darkme.cn:8880/android/file/gnf";

    public static String file_insert_record_url = "http://darkme.cn:8880/android/file/ifr";


    public static String tweet_post_url = "http://darkme.cn:8880/android/tweet/nt";
    public static String tweet_get_all_url = "http://darkme.cn:8880/android/tweet/gat";
    public static String tweet_del_url = "http://darkme.cn:8880/android/tweet/dt";
    public static String tweet_praise_add_url = "http://darkme.cn:8880/android/tweet/mtg";


    public static String user_do_exist_url = "http://darkme.cn:8880/android/user/ude";
    public static String user_do_login_url = "http://darkme.cn:8880/android/user/ulv";
    public static String user_do_register_url = "http://darkme.cn:8880/android/user/urv";

    public static String user_set_profile_picture_url = "http://darkme.cn:8880/android/user/supp";
    public static String user_get_profile_picture_url = "http://darkme.cn:8880/android/user/gupp";

    public static String user_get_handoff_text_url = "http://darkme.cn:8880/android/user/guht";
    public static String user_turn_off_handoff_text_url = "http://darkme.cn:8880/android/user/offuht";
    public static String user_post_handoff_text_url = "http://darkme.cn:8880/android/user/suht";

    public static String user_tweet_info_url = "http://darkme.cn:8880/android/user/guti";
    public static String user_praise_tweet_url = "http://darkme.cn:8880/android/user/upt";
    public static String user_collect_tweet_url = "http://darkme.cn:8880/android/user/uct";


    public static String user_show_my_friend_url = "http://darkme.cn:8880/android/user/gmf";
    public static String user_change_friend_mark = "http://darkme.cn:8880/android/user/cfm";
    public static String user_delete_friend_url = "http://darkme.cn:8880/android/user/duf";
    public static String user_send_make_friend_msg ="http://darkme.cn:8880/android/user/smfm";
    public static String user_send_agree_make_friend_msg ="http://darkme.cn:8880/android/user/amfm";

    public static String user_get_msg ="http://darkme.cn:8880/android/user/gum";
    public static String user_del_msg ="http://darkme.cn:8880/android/user/dum";



    public static String user_get_memo_by_state_url = "http://darkme.cn:8880/android/memo/gmbs";
    public static String user_change_memo_state_url = "http://darkme.cn:8880/android/memo/cmbs";
    public static String user_new_memo_url = "http://darkme.cn:8880/android/memo/nm";

    public static String check_update_url = "http://darkme.cn:8880/android/version/cu";
    public static String push_update_url = "http://darkme.cn:8880/android/version/pu";

    //ImageAdapter的三种类型
    public static final int ADAPTER_FOR_NewTweetActivity = 1;
    public static final int ADAPTER_TO_DRAWABLE = 2;
    public static final int ADAPTER_FOR_MainActivity = 3;
    public static final int ADAPTER_FOR_TweetDetailActivity = 4;

}