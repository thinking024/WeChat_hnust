package utils;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.Course;
import pojo.MyCookies;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Crawler {
    public static boolean check(String account, String password) throws Exception {
        String encodeUrl = "http://kdjw.hnust.edu.cn//Logon.do?method=logon&flag=sess";
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        MyCookies myCookies = new MyCookies(cookieStore);
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(myCookies).build();

        // ajax请求密钥
        Request encodeRequest = new Request.Builder().url(encodeUrl).build();
        Response response = client.newCall(encodeRequest).execute();
        String encoded = "";
        if (response.isSuccessful()) {
            String dataStr = response.body().string();
            String scode = dataStr.split("#")[0];
            String sxh = dataStr.split("#")[1];
            String code = account + "%%%" + password;
            for (int i = 0; i < code.length(); i++) {
                if (i < 20) {
                    encoded = encoded + code.substring(i, i + 1) + scode.substring(0, parseInt(sxh.substring(i, i + 1)));
                    scode = scode.substring(parseInt(sxh.substring(i, i + 1)));
                } else {
                    encoded = encoded + code.substring(i);
                    i = code.length();
                }
            }
        }

        // 登录
        String loginUrl = "http://kdjw.hnust.edu.cn/Logon.do?method=logon";
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("userAccount", account);
        formBody.add("userPassword", "");
        formBody.add("encoded", encoded);
        Request loginRequest = new Request.Builder()//创建Request 对象。
                .url(loginUrl)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)")
                .post(formBody.build())//传递请求体
                .build();
        Response loginResponse = client.newCall(loginRequest).execute();
        String homeUrl = "http://kdjw.hnust.edu.cn/jsxsd/framework/xsMain.jsp";
        System.out.println(loginResponse.request().url().toString().trim());
        if (loginResponse.isSuccessful()) { // 成功登录进入主页
            if (homeUrl.equals(loginResponse.request().url().toString().trim())) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Course> getCourse(String account, String password, int week) throws Exception {
        String encodeUrl = "http://kdjw.hnust.edu.cn//Logon.do?method=logon&flag=sess";
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        MyCookies myCookies = new MyCookies(cookieStore);
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(myCookies).build();

        // ajax请求密钥
        Request encodeRequest = new Request.Builder().url(encodeUrl).build();
        Response response = client.newCall(encodeRequest).execute();
        String encoded = "";
        if (response.isSuccessful()) {
            String dataStr = response.body().string();
            String scode = dataStr.split("#")[0];
            String sxh = dataStr.split("#")[1];
            String code = account + "%%%" + password;
            for (int i = 0; i < code.length(); i++) {
                if (i < 20) {
                    encoded = encoded + code.substring(i, i + 1) + scode.substring(0, parseInt(sxh.substring(i, i + 1)));
                    scode = scode.substring(parseInt(sxh.substring(i, i + 1)));
                } else {
                    encoded = encoded + code.substring(i);
                    i = code.length();
                }
            }
        }

        // 登录
        String loginUrl = "http://kdjw.hnust.edu.cn/Logon.do?method=logon";
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("userAccount", account);
        formBody.add("userPassword", "");
        formBody.add("encoded", encoded);
        Request loginRequest = new Request.Builder()//创建Request 对象。
                .url(loginUrl)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)")
                .post(formBody.build())//传递请求体
                .build();
        Response loginResponse = client.newCall(loginRequest).execute();

        // 爬取课程
        String homeUrl = "http://kdjw.hnust.edu.cn/jsxsd/framework/xsMain.jsp";
        if (loginResponse.isSuccessful()) { // 成功登录进入主页
            if (homeUrl.equals(loginResponse.request().url().toString().trim())) {
                ArrayList<Course> courses = new ArrayList<>();
                // 获取待查询课表节次
                String orderUrl = "http://kdjw.hnust.edu.cn/jsxsd/framework/xsMain_new.jsp";
                Request orderRequest = new Request.Builder().url(orderUrl).build();
                Response orderResponse = client.newCall(orderRequest).execute();
                Document orderDoc = Jsoup.parse(orderResponse.body().string());
                //System.out.println();

                String time = "";
                if (week > 0) { // 查询当日课表
                    time += orderDoc.getElementById("week").getElementsByAttribute("selected").first().attr("value");
                } else { // 查询某周课表
                    time += orderDoc.getElementById("week").child(week).attr("value");
                }
                String order = orderDoc.getElementById("kbjcmsid").getElementsByAttribute("selected").first().attr("value");

                // 查询课表
                String courseUrl = String.format("http://kdjw.hnust.edu.cn/jsxsd/framework/main_index_loadkb.jsp?rq=%s&sjmsValue=%s", time, order);
                Request courseRequest = new Request.Builder().url(courseUrl).build();
                Response courseResponse = client.newCall(courseRequest).execute();
                return dayCourseParser(courseResponse.body().string());
            } else { // 密码错误
                return null;
            }
        }
        return null;
    }

    public static ArrayList<Course> getDayCourse(String account, String password) throws Exception {
        String encodeUrl = "http://kdjw.hnust.edu.cn//Logon.do?method=logon&flag=sess";
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        MyCookies myCookies = new MyCookies(cookieStore);
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(myCookies).build();

        // ajax请求密钥
        Request encodeRequest = new Request.Builder().url(encodeUrl).build();
        Response response = client.newCall(encodeRequest).execute();
        String encoded = "";
        if (response.isSuccessful()) {
            String dataStr = response.body().string();
            String scode = dataStr.split("#")[0];
            String sxh = dataStr.split("#")[1];
            String code = account + "%%%" + password;
            for (int i = 0; i < code.length(); i++) {
                if (i < 20) {
                    encoded = encoded + code.substring(i, i + 1) + scode.substring(0, parseInt(sxh.substring(i, i + 1)));
                    scode = scode.substring(parseInt(sxh.substring(i, i + 1)));
                } else {
                    encoded = encoded + code.substring(i);
                    i = code.length();
                }
            }
        }
        response.close();

        // 登录
        String loginUrl = "http://kdjw.hnust.edu.cn/Logon.do?method=logon";
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("userAccount", account);
        formBody.add("userPassword", "");
        formBody.add("encoded", encoded);
        Request loginRequest = new Request.Builder()//创建Request 对象。
                .url(loginUrl)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)")
                .post(formBody.build())//传递请求体
                .build();
        Response loginResponse = client.newCall(loginRequest).execute();

        // 爬取课程
        String homeUrl = "http://kdjw.hnust.edu.cn/jsxsd/framework/xsMain.jsp";
        if (loginResponse.isSuccessful()) { // 成功登录进入主页
            if (homeUrl.equals(loginResponse.request().url().toString().trim())) {
                ArrayList<Course> courses = new ArrayList<>();
                // 获取待查询课表节次
                String orderUrl = "http://kdjw.hnust.edu.cn/jsxsd/framework/xsMain_new.jsp";
                Request orderRequest = new Request.Builder().url(orderUrl).build();
                Response orderResponse = client.newCall(orderRequest).execute();
                Document orderDoc = Jsoup.parse(orderResponse.body().string());
                //System.out.println();
                String time = orderDoc.getElementById("week").getElementsByAttribute("selected").first().attr("value");
                String order = orderDoc.getElementById("kbjcmsid").getElementsByAttribute("selected").first().attr("value");

                // 查询课表
                String courseUrl = String.format("http://kdjw.hnust.edu.cn/jsxsd/framework/main_index_loadkb.jsp?rq=%s&sjmsValue=%s", time, order);
                Request courseRequest = new Request.Builder().url(courseUrl).build();
                Response courseResponse = client.newCall(courseRequest).execute();
                loginResponse.close();
                return dayCourseParser(courseResponse.body().string());
            } else { // 密码错误
                loginResponse.close();
                return null;
            }
        }
        loginResponse.close();
        return null;
    }

    public static ArrayList<Course> dayCourseParser(String html) {
        //System.out.println("html" + html);
        Document document = Jsoup.parse(html);
        ArrayList<Course> courses = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            day = 7;
        }

        Elements rows = document.getElementsByTag("tbody").first().children();
        for (int i = 0; i < rows.size() - 1; i++) {
            //System.out.println(rows.get(i) + "    " + rows.get(i).childrenSize());
            Elements p = rows.get(i).child(day).getElementsByTag("p");
            if (p != null && p.size() != 0) {
                Course course = new Course();
                String[] info = p.first().attr("title").split("<br/>");
                course.setOrderBegin(i * 2 + 1);
                course.setOrderEnd((i + 1) * 2);
                course.setName(info[2].substring(info[2].indexOf("：") + 1));
                course.setClassroom(info[4].substring(info[4].indexOf("：") + 1));
                courses.add(course);
            } else {
//                System.out.println("no class");
            }
        }

        String text = rows.last().text();
        System.out.println("before" + text);
        if (text == null) { // 无备注信息
            text = "";
        } else { // 有备注信息
            text = text.replace(";", "\n");
        }
        Course course = new Course();
        course.setName(text);
        courses.add(course);
        System.out.println("after" + text);
        return courses;
    }

    public static ArrayList<Course> getWeekCourse(String account, String password, int week) throws IOException {
        String encodeUrl = "http://kdjw.hnust.edu.cn//Logon.do?method=logon&flag=sess";
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        MyCookies myCookies = new MyCookies(cookieStore);
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(myCookies).build();

        // ajax请求密钥
        Request encodeRequest = new Request.Builder().url(encodeUrl).build();
        Response response = client.newCall(encodeRequest).execute();
        String encoded = "";
        if (response.isSuccessful()) {
            String dataStr = response.body().string();
            String scode = dataStr.split("#")[0];
            String sxh = dataStr.split("#")[1];
            String code = account + "%%%" + password;
            for (int i = 0; i < code.length(); i++) {
                if (i < 20) {
                    encoded = encoded + code.substring(i, i + 1) + scode.substring(0, parseInt(sxh.substring(i, i + 1)));
                    scode = scode.substring(parseInt(sxh.substring(i, i + 1)));
                } else {
                    encoded = encoded + code.substring(i);
                    i = code.length();
                }
            }
        }
        response.close();

        // 登录
        String loginUrl = "http://kdjw.hnust.edu.cn/Logon.do?method=logon";
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("userAccount", account);
        formBody.add("userPassword", "");
        formBody.add("encoded", encoded);
        Request loginRequest = new Request.Builder()//创建Request 对象。
                .url(loginUrl)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)")
                .post(formBody.build())//传递请求体
                .build();
        Response loginResponse = client.newCall(loginRequest).execute();

        // 爬取课程
        String homeUrl = "http://kdjw.hnust.edu.cn/jsxsd/framework/xsMain.jsp";
        if (loginResponse.isSuccessful()) { // 成功登录进入主页
            if (homeUrl.equals(loginResponse.request().url().toString().trim())) {
                // 获取待查询课表节次
                String orderUrl = "http://kdjw.hnust.edu.cn/jsxsd/framework/xsMain_new.jsp";
                Request orderRequest = new Request.Builder().url(orderUrl).build();
                Response orderResponse = client.newCall(orderRequest).execute();
                Document orderDoc = Jsoup.parse(orderResponse.body().string());
                //System.out.println();
                String time = "";
                if (week == 0) { // 默认当前周
                    Element selectDefault = orderDoc.getElementById("week").getElementsByAttribute("selected").first(); // 默认选中周的元素
                    Elements options = orderDoc.getElementById("week").children();
                    for (int i = 0; i < options.size(); i++) {
                        if (selectDefault == options.get(i)) {
                            week = i;
                            break;
                        }
                    }
                    time += selectDefault.attr("value");
                } else {
                    time += orderDoc.getElementById("week").child(week).attr("value");
                }

                // 调整每周起始日期
                String beginDate = "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = null;
                try {
                    date = sdf.parse(time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Calendar instance = Calendar.getInstance();
                instance.setTime(date);
                int day = instance.get(Calendar.DAY_OF_WEEK) - 1;
                if (day == 0) {
                    instance.add(Calendar.DATE, 1);
                    beginDate += sdf.format(instance.getTime());
                    //System.out.println("begindate" + beginDate);
                }

                String order = orderDoc.getElementById("kbjcmsid").getElementsByAttribute("selected").first().attr("value");

                // 查询课表
                String courseUrl = String.format("http://kdjw.hnust.edu.cn/jsxsd/framework/main_index_loadkb.jsp?rq=%s&sjmsValue=%s", time, order);
                Request courseRequest = new Request.Builder().url(courseUrl).build();
                Response courseResponse = client.newCall(courseRequest).execute();

                // 返回日期，周次
                ArrayList<Course> courses = weekCourseParser(courseResponse.body().string());
                Course course = new Course();
                course.setDay(week);
                course.setClassroom(beginDate);
                courses.add(course);

                loginResponse.close();
                courseResponse.close();
                return courses;
            } else { // 密码错误
                loginResponse.close();
                return null;
            }
        }
        loginResponse.close();
        return null;
    }

    // 可以合并函数，并且通过直接get td标签元素，无需先获取tr
    public static ArrayList<Course> weekCourseParser(String html) {
        Document document = Jsoup.parse(html);
        Elements rows = document.getElementsByTag("tbody").first().children();

        ArrayList<Course> courses = new ArrayList<>();
        for (int i = 0; i < rows.size() - 1; i++) {
            Elements tds = rows.get(i).children();
            for (int j = 1; j < tds.size(); j++) {
//                System.out.println(tds.get(j));
                Elements p = tds.get(j).getElementsByTag("p");
                if (p != null && p.size() != 0) {
                    Course course = new Course();
                    String[] info = p.first().attr("title").split("<br/>");
                    course.setDay(j);
                    course.setOrderBegin(i * 2 + 1);
                    course.setOrderEnd((i + 1) * 2);
                    course.setName(info[2].substring(info[2].indexOf("：") + 1));
                    course.setClassroom(info[4].substring(info[4].indexOf("：") + 1));
                    //System.out.println(course);
                    courses.add(course);
                }
            }
        }
        return courses;
    }
}


