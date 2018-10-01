package com.example.lqs2.courseapp.utils;

import com.example.lqs2.courseapp.MyApplication;
import com.example.lqs2.courseapp.activities.NoticeActivity;
import com.example.lqs2.courseapp.entity.Course;
import com.example.lqs2.courseapp.entity.Grade;
import com.example.lqs2.courseapp.entity.Notice;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlCodeExtractUtil {
    private static List<Course> courses = new ArrayList<>();
    private static Map<String, Integer> map = new HashMap<>();
    private static Random random = new Random();
    public static int MaxWeek = 10;
    public static List<Integer> usedColor = new ArrayList<>();


//    写完这个类整个人都不好了

    private static String parse_Name_Location(String text) {
        String[] split = text.split("<br>");
        return split[0].substring(split[0].lastIndexOf(">") + 1) + " @" + split[split.length - 1].substring(0, split[split.length - 1].lastIndexOf("</"));
    }

    private static int[] parsePerWeek(String text, Course course) {

        String regex_week = "\\{第.*[-].*周\\}";
        String regex_teacher = "\\}<br>.*<br>";
        Pattern pattern1 = Pattern.compile(regex_week);
        Pattern pattern2 = Pattern.compile(regex_teacher);
        Matcher matcher1 = pattern1.matcher(text);
        Matcher matcher2 = pattern2.matcher(text);
        int[] ints = new int[2];
        if (matcher1.find()) {
//            System.out.println(matcher.group());
            String str = matcher1.group();
//            设置对话框中的周数
            course.setDialog_weeks(str.substring(1, str.length() - 1));
            ints[0] = Integer.parseInt(str.substring(str.indexOf("第") + 1, str.indexOf("-")));
            ints[1] = Integer.parseInt(str.substring(str.indexOf("-") + 1, str.indexOf("周")));
        }
        if (matcher2.find()) {
            String str = matcher2.group();
            course.setDialog_teacher(str.substring(str.indexOf(">") + 1, str.lastIndexOf("<")));
        }
        if (ints[1] > MaxWeek)
            MaxWeek = ints[1];
        return ints;
    }

    public static List<Course> getCourseList(String sourcecode, int weekNow, int day) {
        usedColor.clear();
        for (int i = 0; i < 13; i++) {
            usedColor.add(i);
        }
        map.clear();
//        List<Course> courses = new ArrayList<>();
        Document doc = Jsoup.parse(sourcecode);
//        用来保存课表的颜色
        Random random = new Random();
        //获取Table
        Element table = doc.getElementById("Table1");
        //获取table中的td节点
        Elements trs = table.select("tr");
        //移除不需要的参数，星期与时间
        trs.remove(0);
        trs.remove(0);
        //遍历td节点
        for (int i = 0; i < trs.size(); ++i) {
            Element tr = trs.get(i);
            //获取tr下的td节点
            Elements tds = tr.select("td[align]");
            //遍历td节点
            for (int j = 0; j < tds.size(); ++j) {
                if (day != 0) {
                    if ((j + 1) != day) {
                        continue;
                    }
                }
                Element td = tds.get(j);
                String text = td.text();
//                System.out.println(text+"这是一个td中的全部内容， 可能包含多个课程");
                String tdCode = td.toString();
                int tdLen = tdCode.split("<br><br>").length;
                if (tdLen > 1) {
                    String[] split = tdCode.split("<br><br>");
                    for (int k = 0; k < split.length; k++) {
                        if (k == 0) {
                            split[k] = split[k] + "</";
                            getOneCourse(split[k], i, j, weekNow, day);
                            continue;
                        }
                        if (k == split.length - 1) {
                            split[k] = ">" + split[k];
                            getOneCourse(split[k], i, j, weekNow, day);
                            continue;
                        }
                        split[k] = ">" + split[k] + "</";
                        getOneCourse(split[k], i, j, weekNow, day);
                    }
                    continue;
                }
//                getOneCourse(tdCode, i, j, weekNow);
                if (text.length() > 10) {
                    //解析文本数据
                    Course course = new Course();
                    int[] ints = parsePerWeek(tdCode, course);
                    if (!(ints[0] <= weekNow && weekNow <= ints[1])) {
                        continue;
                    }
//                    显示在视图中的信息 课程名+教室地点
                    text = parse_Name_Location(tdCode);
                    String name = text.substring(0, text.indexOf("@"));
//                    设置对话框中的课程名
                    course.setDialog_name(name);
//                    设置对话框中的教室地点
                    course.setDialog_location(text.substring(text.indexOf("@") + 1));
                    if (!map.containsKey(name)) {
                        int temp = random.nextInt(usedColor.size());
                        int color = usedColor.get(temp);
                        usedColor.remove(temp);
                        map.put(name, color);
                        course.setColor(color);
                    } else {
                        course.setColor(map.get(name));
                    }
                    course.setClsName(text);
//                    System.out.println(text + "---------------------------本周要上这一节课-----------------------------------------------");
                    course.setDay(j + 1);
                    course.setClsCount(Integer.valueOf(td.attr("rowspan")));
                    course.setClsNum(i + 1);
                    courses.add(course);
                }
            }
        }
        SharedPreferenceUtil.put(MyApplication.getContext(), "CourseMaxWeek", MaxWeek);
        return courses;
    }


    private static void getOneCourse(String code, int i, int j, int weekNow, int day) {


        if (code.length() > 10) {
            if (day != 0) {
                if ((j + 1) != day) {
                    return;
                }
            }
            Course course = new Course();
//            System.err.println("有多节课内容需要解析!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            System.out.println(code);
            int[] ints = parsePerWeek(code, course);
            if (!(ints[0] <= weekNow && weekNow <= ints[1])) {
                return;
            }
            String text = parse_Name_Location(code);

            String name = text.substring(0, text.indexOf("@"));
//            设置对话框中的课程名
            course.setDialog_name(name);
//            设置对话框中的教室地点
            course.setDialog_location(text.substring(text.indexOf("@") + 1));
            if (!map.containsKey(name)) {
                int temp = random.nextInt(usedColor.size());
                int color = usedColor.get(temp);
                usedColor.remove(temp);
                map.put(name, color);
                course.setColor(color);
            } else {
                course.setColor(map.get(name));
            }
            course.setClsName(text);
            course.setDay(j + 1);
            course.setClsCount(2);
            course.setClsNum(i + 1);
            courses.add(course);
//            System.err.println(course.toString() + "这里是一节课");
        } else {
            return;
        }
    }


    public static List<Grade> getGradeList(String html) {

        List<Grade> gradeList = new ArrayList<>();
        Elements dataTable = Jsoup.parse(html).getElementsByClass("datelist");
        Element data = dataTable.first();
        Elements trs = data.select("tr");
//        移除表头
        trs.remove(0);
        for (int i = 0; i < trs.size(); i++) {
            Element tr = trs.get(i);
            Elements tds = tr.select("td");
            Grade grade = new Grade();
            for (int j = 0; j < tds.size(); j++) {
                Element td = tds.get(j);
                String text = td.text();
                switch (j) {
                    case 0:
                        grade.setSchool_year(text);
                        break;
                    case 1:
                        grade.setTeam(Integer.parseInt(text));
                        break;
                    case 2:
                        grade.setCourse_no(text);
                        break;
                    case 3:
                        grade.setCourse_name(text);
                        break;
                    case 4:
                        grade.setCourse_nature(text);
                        break;
                    case 5:
                        grade.setCourse_attr(text);
                        break;
                    case 6:
                        grade.setCourse_credit(text);
                        break;
//                    case 7:
//                        grade.setCourse_usual_grade(text);
//                        break;
                    case 7:
                        grade.setCourse_middle_garde(text);
                        break;
                    case 8:
                        grade.setCourse_final_grade(text);
                    case 9:
                        grade.setCourse_exp_grade(text);
                        break;
                    case 10:
                        grade.setCourse_grade(text);
                        break;
                    case 11:
                        grade.setRetest_grade(text);
                        break;
                    case 12:
                        grade.setIsReconstruct(text);
                        break;
                    case 13:
                        grade.setCourse_belong(text);
                        break;
                    case 14:
                        grade.setMore(text);
                        break;
                    case 15:
                        grade.setRetest_more(text);
                        break;
                    default:
                        break;
                }
            }
            gradeList.add(grade);
        }
        return gradeList;
    }


    public static List<Notice> parseHtmlForNotice(String html, boolean b) {
        List<Notice> notices = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(html);
            Elements lis = doc.select("li[id^=line_u5]");
            if (b) {
                String anchorText = doc.select("a[class='Next']").first().attr("href");
                NoticeActivity.anchorPosition = Integer.parseInt(anchorText.substring(anchorText.indexOf("/") + 1, anchorText.lastIndexOf(".")));
            }

            for (Element li : lis) {
                Notice notice = new Notice();
                Element a = li.select("a").first();
                notice.setTitle(a.attr("title"));
                notice.setContentUrl(Constant.notice_base_url + a.attr("href").substring(2));
                notice.setTime(li.select(".date").first().text());
                notices.add(notice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notices;
    }

    public static Notice getSingleNoticeDetail(String html) {
        Notice notice = new Notice();

        if (html.contains("您无权访问此页面")) {
            notice.setTitle("访问限制");
            notice.setContent("您无权访问此页面");
            return notice;
        }
        try {
            Document doc = Jsoup.parse(html);
            String title = doc.select(".link_16").first().text();
            notice.setTitle(title);
            String time = doc.select(".link_1").first().text();
            notice.setTime(time.substring(0, time.indexOf("浏览")));
            Elements ps = doc.select("#vsb_content").first().select("p");
            StringBuilder builder = new StringBuilder();
            builder.append("标题：").append("\n").append(title).append("\n\n");
            for (Element p : ps) {
                builder.append(p.text()).append("\n");
            }
            notice.setContent(builder.toString());


            Element annexEle = doc.select("#fujianlist").first();
            if (annexEle != null) {
                String href = Constant.notice_base_url + annexEle.attr("href");
                notice.setAnnexText(annexEle.select("span").first().attr("dt"));
                notice.setAnnexUrl(href);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return notice;
    }
}