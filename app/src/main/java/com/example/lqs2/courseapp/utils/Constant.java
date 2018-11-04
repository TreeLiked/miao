package com.example.lqs2.courseapp.utils;

import android.os.Environment;

/**
 * 常量
 *
 * @author lqs2
 */
public class Constant {

    /**
     * 教务网首页
     */
    public static final String MAIN_URL = "http://jwjx.njit.edu.cn/Default2.aspx";

    /**
     * 重定向网页
     */
    public static final String FORWARD_URL = "http://jwjx.njit.edu.cn/xs_main.aspx?xh=";

    /**
     * 查询成绩前缀
     */
    public static final String QUERY_PREFIX_URL_1 = "http://jwjx.njit.edu.cn/xscjcx_dq.aspx";

    /**
     * 查询学分前缀
     */
    public static final String QUERY_PREFIX_URL_2 = "http://jwjx.njit.edu.cn/xscjcx.aspx";

    /**
     * 验证码请求网址
     */
    public static final String CHECK_CODE_URL = "http://jwjx.njit.edu.cn/CheckCode.aspx";

    /**
     * 防盗链
     */
    public static final String REFER_URL = "http://jwjx.njit.edu.cn/";

    /**
     * bing每日一图api
     */
    public static final String BING_PIC_API = "http://guolin.tech/api/bing_pic";

    /**
     * 校历选择网址
     */
    public static final String SCHOOL_CALENDAR_CHOOSE_URL = "http://jwc.njit.edu.cn/xl_list.jsp?urltype=tree.TreeTempUrl&wbtreeid=1129";


    public static final String SERVER_OFF = "服务器发生错误";
    public static final String NETWORK_OFF = "网络连接断开";
    public static final String CODE_FLUSH_FAIL = "验证码刷新失败";
    public static final String LOGIN_ERROR_CORD = "验证码不正确";
    public static final String LOGIN_ERROR_PW = "密码错误";
    public static final String LOGIN_ERROR_NO_COMMENT = "你还没有进行本学期的教学质量评价,在本系统的“教学质量评价”栏中完成评价工作后，才能进入系统";
    public static final String LOGIN_SUCCESS_INFO = "QAQ登录成功";
    public static final String LOGIN_ERROR_XH = "用户名不存在或未按照要求参加教学活动";
    public static final String LOGIN_JW_SUCCESS = "为保障您的个人信息的安全，请点在退出时，请击安全退出";


    public static final String BOOK_URL_BEFORE = "http://opac.lib.njit.edu.cn/opac/openlink.php?s2_type=title&s2_text=";
    public static final String BOOK_URL_AFTER = "&search_bar=new&doctype=ALL&with_ebook=off&match_flag=forward&showmode=list&location=ALL";

    public static final String GRADE_QUERY_WELCOME = "欢迎使用成绩查询系统";
    public static final String CREDIT_QUERY_WELCOME = "欢迎使用学分查询系统";
    public static final String GRADE_QUERY_ITEM1 = "I\t学期成绩查询";
    public static final String GRADE_QUERY_ITEM2 = "II\t学分查询";
    public static final String GRADE_QUERY_ITEM3 = "III\t体测成绩查询";
    public static final String GRADE_QUERY_ITEM4 = "IV\t四六级查询";


    public static final String MAIN_NO_COURSE_LEFT_INFO = "今天已经没有课了, 快去休息一下吧";


    public static final String NO_SIG_INFO = "一句话介绍一下自己吧";


    public static final String NO_COURSE_INFO = "该学期暂无成绩";
    public static final String COURSE_ALL_PASSED_INFO = "恭喜你， 全部通过";


    public static final String MEMO_HELP_TEXT = "1、备忘录可被创建和删除\n" +
            "2、备忘录有两种类型\n" +
            "       ·一般\n" +
            "       ·紧急\n" +
            "3、备忘录有两种状态\n" +
            "       ·待完成\n" +
            "       ·已完成\n" +
            "4、登录后可与[darkme.cn]同步";


    public static final int SERVER_ERROR = 0;
    public static final int INFO_ERROR = 1;
    public static final int TRANSFER_CODE = 2;
    public static final int TURN_PROGRESS_BAR_ON = 3;
    public static final int TURN_PROGRESS_BAR_OFF = 4;
    public static final int SHOW_TOAST = 5;


    public static final String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();


    /**
     * 个人中心请求
     */
    public static final int CENTER_FILE = 101;
    public static final int LOCAL_FILE = 102;


    /**
     * Main广播
     */
    public static final String ACTION1 = "com.example.lqs2.courseapp.OPEN_DIY_MAIN_BG";
    public static final String ACTION2 = "com.example.lqs2.courseapp.CLOSE_DIY_MAIN_BG";
    public static final String ACTION3 = "com.example.lqs2.courseapp.CHANGE_COURSE";


    /**
     * 权限请求码
     */
    public static final int IMAGE_REQUEST_CODE = 1;
    public static final int IMAGE_REQUEST_CODE_CROP = 202;
    public static final int GET_CONTENT = 301;


    //    public static final String API_BASE_URL = "http://darkme.cn:8880/android";
    public static final String API_BASE_URL = "http://darkme.cn:8880/android";


    public static final String UPLOAD_IMAGE_URL = API_BASE_URL + "/image/ui";
    public static final String IMG_ACCESS_URL = "http://darkme.cn:8880/file/img";
    public static final String CARD_COVER_URL = IMG_ACCESS_URL + "/cover.jpg";

    public static final String FILE_SHOW_URL = API_BASE_URL + "/file/gmf";
    public static final String FILE_DEL_URL = API_BASE_URL + "/file/rmf";
    public static final String FILE_SEARCH_URL = API_BASE_URL + "/file/smf";
    public static final String FILE_GENERATE_URL = API_BASE_URL + "/file/gnf";
    public static final String FILE_INSERT_RECORD_URL = API_BASE_URL + "/file/ifr";
    public static final String TWEET_POST_URL = API_BASE_URL + "/tweet/nt";
    public static final String TWEET_GET_ALL_URL = API_BASE_URL + "/tweet/gat";
    public static final String TWEET_DEL_URL = API_BASE_URL + "/tweet/dt";
    public static final String TWEET_PRAISE_ADD_URL = API_BASE_URL + "/tweet/mtg";
    public static final String USER_DO_EXIST_URL = API_BASE_URL + "/user/ude";
    public static final String USER_DO_LOGIN_URL = API_BASE_URL + "/user/ulv";
    public static final String USER_DO_REGISTER_URL = API_BASE_URL + "/user/urv";
    public static final String USER_SET_PROFILE_PICTURE_URL = API_BASE_URL + "/user/supp";
    public static final String USER_GET_PROFILE_PICTURE_URL = API_BASE_URL + "/user/gupp";
    public static final String USER_GET_HANDOFF_TEXT_URL = API_BASE_URL + "/user/guht";
    public static final String USER_TURN_OFF_HANDOFF_TEXT_URL = API_BASE_URL + "/user/offuht";
    public static final String USER_POST_HANDOFF_TEXT_URL = API_BASE_URL + "/user/suht";
    public static final String USER_TWEET_INFO_URL = API_BASE_URL + "/user/guti";
    public static final String USER_PRAISE_TWEET_URL = API_BASE_URL + "/user/upt";
    public static final String USER_COLLECT_TWEET_URL = API_BASE_URL + "/user/uct";
    public static final String USER_SHOW_MY_FRIEND_URL = API_BASE_URL + "/user/gmf";
    public static final String USER_CHANGE_FRIEND_MARK = API_BASE_URL + "/user/cfm";
    public static final String USER_DELETE_FRIEND_URL = API_BASE_URL + "/user/duf";
    public static final String USER_SEND_MAKE_FRIEND_MSG = API_BASE_URL + "/user/smfm";
    public static final String USER_SEND_AGREE_MAKE_FRIEND_MSG = API_BASE_URL + "/user/amfm";
    public static final String USER_GET_MSG = API_BASE_URL + "/user/gum";
    public static final String USER_DEL_MSG = API_BASE_URL + "/user/dum";
    public static final String USER_GET_MEMO_BY_STATE_URL = API_BASE_URL + "/memo/gmbs";
    public static final String USER_CHANGE_MEMO_STATE_URL = API_BASE_URL + "/memo/cmbs";
    public static final String USER_NEW_MEMO_URL = API_BASE_URL + "/memo/nm";
    public static final String CHECK_UPDATE_URL = API_BASE_URL + "/version/cu";
    public static final String PUSH_UPDATE_URL = API_BASE_URL + "/version/pu";
    public static final String SCHOOL_NOTICE_URL = "https://www.njit.edu.cn/index/tzgg.htm";
    public static final String NOTICE_NEXT_BASE_URL = "https://www.njit.edu.cn/index/tzgg/";
    public static final String NOTICE_BASE_URL = "https://www.njit.edu.cn";


    /**
     * ImageAdapter的三种类型
     */
    public static final int ADAPTER_FOR_NEW_TWEET_ACTIVITY = 1;
    public static final int ADAPTER_TO_DRAWABLE = 2;
    public static final int ADAPTER_FOR_MAIN_ACTIVITY = 3;
    public static final int ADAPTER_FOR_TWEET_DETAIL_ACTIVITY = 4;

}