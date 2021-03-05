import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.Course;
import pojo.MyCookies;
import utils.Crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Test {

    @org.junit.Test
    public void getCourse() throws Exception {
        String account = "1805050213";
        String password = "hn095573";
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
        if (loginResponse.isSuccessful()) { // 成功进入主页
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
                //courseParser(courseResponse.body().string());
            }
        }
    }

    @org.junit.Test
    public void courseParser() throws Exception{
        File file = new File("C:\\Users\\26438\\Desktop\\main_index_loadkb_null.htm");
        Document document = Jsoup.parse(file, "UTF-8");
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
                System.out.println("no class");
            }
        }
        String text = rows.last().text();
        System.out.println(text.replace(";", "\n"));
    }

    @org.junit.Test
    public void test() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            day = 7;
        }
        System.out.println(day);
    }

    @org.junit.Test
    public void getWeekCourse() throws Exception {
        String account = "1805050213";
        String password = "hn095573";
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
        if (loginResponse.isSuccessful()) { // 成功进入主页
            if (homeUrl.equals(loginResponse.request().url().toString().trim())) {
                ArrayList<Course> courses = new ArrayList<>();
                // 获取待查询课表节次
                String orderUrl = "http://kdjw.hnust.edu.cn/jsxsd/framework/xsMain_new.jsp";
                Request orderRequest = new Request.Builder().url(orderUrl).build();
                Response orderResponse = client.newCall(orderRequest).execute();
                Document orderDoc = Jsoup.parse(orderResponse.body().string());
                //System.out.println();
                int week = 7;
                String time = orderDoc.getElementById("week").child(week).attr("value");
                String order = orderDoc.getElementById("kbjcmsid").getElementsByAttribute("selected").first().attr("value");
                System.out.println(time + " " + order);
                // 查询课表
                String courseUrl = String.format("http://kdjw.hnust.edu.cn/jsxsd/framework/main_index_loadkb.jsp?rq=%s&sjmsValue=%s", time, order);
                Request courseRequest = new Request.Builder().url(courseUrl).build();

                Response courseResponse = client.newCall(courseRequest).execute();
                //courseParser(courseResponse.body().string());
            }
        }
    }

    @org.junit.Test
    public void weekCourseParser() throws Exception{
        File file = new File("C:\\Users\\26438\\Desktop\\main_index_loadkb.htm");
        Document document = Jsoup.parse(file, "UTF-8");
        Elements rows = document.getElementsByTag("tbody").first().children();

        ArrayList<ArrayList<Course>> weekCourses = new ArrayList<>();
        for (int i = 0; i < rows.size() - 1; i++) {
            //System.out.println(rows.get(i) + "    " + rows.get(i).childrenSize());
            // 遍历每行
            ArrayList<Course> rowCourses = new ArrayList<>();

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
                    rowCourses.add(course);
                } else {
                    //System.out.println("no class");
                }
            }

            if (rowCourses.size() != 0) {
                weekCourses.add(rowCourses);
            }
            //System.out.println("======");
        }

        for (ArrayList<Course> weekCourse : weekCourses) {
            for (Course course : weekCourse) {
                System.out.println(course);
            }
            System.out.println("========");
        }
    }

    @org.junit.Test
    public void test_check() {
        try {
            System.out.println(Crawler.check("180505021", "hn095573"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
