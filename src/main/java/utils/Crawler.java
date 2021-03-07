package utils;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pojo.Course;
import pojo.Exam;
import pojo.Grade;
import pojo.MyCookies;

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
        if (loginResponse.isSuccessful()) { // 登录请求成功
            if (homeUrl.equals(loginResponse.request().url().toString().trim())) { // 账号密码正确
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
                ArrayList<Course> courses = dayCourseParser(courseResponse.body().string());

                courseResponse.close();
                loginResponse.close();
                return courses;
            }
        }

        // 登录请求失败
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
        if (loginResponse.isSuccessful()) { // 请求成功
            if (homeUrl.equals(loginResponse.request().url().toString().trim())) { // 账号密码正确
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
            }
        }

        // 登录请求失败
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

    public static ArrayList<Grade> getGrade(String account, String password) throws IOException {
        String encodeUrl = "http://kdjw.hnust.edu.cn//Logon.do?method=logon&flag=sess";
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        MyCookies myCookies = new MyCookies(cookieStore);
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(myCookies).build();

        Request encodeRequest = new Request.Builder().url(encodeUrl).build();
        Response response = client.newCall(encodeRequest).execute();
        if (response.isSuccessful()) {
            String dataStr = response.body().string();

            // 加密算法
            String scode = dataStr.split("#")[0];
            String sxh = dataStr.split("#")[1];
            String code = account + "%%%" + password;
            String encoded = "";
            for (int i = 0; i < code.length(); i++) {
                if (i < 20) {
                    encoded = encoded + code.substring(i, i + 1) + scode.substring(0, parseInt(sxh.substring(i, i + 1)));
                    scode = scode.substring(parseInt(sxh.substring(i, i + 1)));
                } else {
                    encoded = encoded + code.substring(i);
                    i = code.length();
                }
            }
            response.close();

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
            if (loginResponse.isSuccessful()) {
                if (homeUrl.equals(loginResponse.request().url().toString().trim())) {
                    String gradeUrl = "http://kdjw.hnust.edu.cn/jsxsd/kscj/cjcx_list";
                    Request gradeRequest = new Request.Builder()//创建Request 对象。
                            .url(gradeUrl)
                            .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)")
                            .build();
                    Response gradeResponse = client.newCall(gradeRequest).execute();
                    ArrayList<Grade> grades = gradeParser(gradeResponse.body().string());

                    gradeResponse.close();
                    loginResponse.close();
                    response.close();
                    return grades;
                }
            }
            loginResponse.close();
        }

        //密钥请求失败
        response.close();
        return null;
    }

    public static ArrayList<Grade> gradeParser(String html) {
//        System.out.println(html);
        ArrayList<Grade> grades = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element div = doc.getElementsByTag("div").first();
        String s = div.ownText();
        String total = s.substring(s.indexOf(" ") + 1);
        Grade totalGrade = new Grade();
        totalGrade.setScore(total);
        totalGrade.setTerm("0");
        grades.add(totalGrade);

        Elements tr = doc.getElementsByTag("tr");
        Element head = tr.remove(0);
        for (Element element : tr) {
            Grade grade = new Grade();
            String[] text = element.text().trim().split(" ");
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(text));
            if (text.length == 12) {
                arrayList.add(8, "");
            }
            arrayList.remove(0);
            arrayList.remove(1);
            arrayList.remove(4);
//            System.out.println(arrayList);

            grade.setTerm(arrayList.get(0));
            grade.setName(arrayList.get(1));
            grade.setScore(arrayList.get(2));
            grade.setCredit(arrayList.get(3));
            grade.setGradePoint(arrayList.get(4));
            //grade.setTerm_again(arrayList.get(5));
            //grade.setExam_type(arrayList.get(6) + " " + arrayList.get(7));
            //grade.setCourse_type(arrayList.get(8) + " " + arrayList.get(9));

            grades.add(grade);
        }
        return grades;
    }

    public static ArrayList<Exam> getExam(String account, String password) throws IOException {
        String encodeUrl = "http://kdjw.hnust.edu.cn//Logon.do?method=logon&flag=sess";
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        MyCookies myCookies = new MyCookies(cookieStore);
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(myCookies).build();

        Request encodeRequest = new Request.Builder().url(encodeUrl).build();
        Response response = client.newCall(encodeRequest).execute();
        if (response.isSuccessful()) {
            String dataStr = response.body(). string();
            String scode = dataStr.split("#")[0];
            String sxh = dataStr.split("#")[1];
            String code = account + "%%%" + password;
            String encoded = "";
            for (int i = 0; i < code.length(); i++) {
                if (i < 20) {
                    encoded = encoded + code.substring(i, i + 1) + scode.substring(0, parseInt(sxh.substring(i, i + 1)));
                    scode = scode.substring(parseInt(sxh.substring(i, i + 1)));
                } else {
                    encoded = encoded + code.substring(i);
                    i = code.length();
                }
            }

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
            if (loginResponse.isSuccessful()) { // 请求成功
                if (homeUrl.equals(loginResponse.request().url().toString().trim())) { // 账号密码正确
                    String examUrl = "http://kdjw.hnust.edu.cn/jsxsd/xsks/xsksap_list";
                    FormBody.Builder formBody2 = new FormBody.Builder();//创建表单请求体
                    formBody2.add("xqlbmc", "");
                    formBody2.add("xnxqid", GlobalInfo.currentTerm);
                    formBody2.add("xqlb", "");

                    Request examRequest = new Request.Builder()//创建Request 对象。
                            .url(examUrl)
                            .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)")
                            .post(formBody2.build())//传递请求体
                            .build();
                    Response examResponse = client.newCall(examRequest).execute();

                    ArrayList<Exam> exams = examParser(examResponse.body().string());
                    examResponse.close();
                    loginResponse.close();
                    response.close();
                    return exams;
                }
            }
            loginResponse.close();
        }

        // 密钥请求失败
        response.close();
        return null;
    }

    public static ArrayList<Exam> examParser(String html) {
        Document doc = Jsoup.parse(html);
        Elements tr = doc.getElementsByTag("tr");
        tr.remove(0);

        ArrayList<Exam> exams = new ArrayList<>();
        for (Element element : tr) {
            String text = element.text();
            if (text.equals("未查询到数据")) {
                break;
            }
            Exam exam = new Exam();
            String[] info = text.split(" ");
            exam.setName(info[4]);
            exam.setTime(info[6] + " " + info[7]);
            exam.setPlace(info[8]);
            exams.add(exam);
        }
        return exams;
    }

    public static TreeMap<String, ArrayList<Grade>> getGrade2(String account, String password) throws IOException {
        String encodeUrl = "http://kdjw.hnust.edu.cn//Logon.do?method=logon&flag=sess";
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        MyCookies myCookies = new MyCookies(cookieStore);
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(myCookies).build();

        Request encodeRequest = new Request.Builder().url(encodeUrl).build();
        Response response = client.newCall(encodeRequest).execute();
        if (response.isSuccessful()) {
            String dataStr = response.body().string();

            // 加密算法
            String scode = dataStr.split("#")[0];
            String sxh = dataStr.split("#")[1];
            String code = account + "%%%" + password;
            String encoded = "";
            for (int i = 0; i < code.length(); i++) {
                if (i < 20) {
                    encoded = encoded + code.substring(i, i + 1) + scode.substring(0, parseInt(sxh.substring(i, i + 1)));
                    scode = scode.substring(parseInt(sxh.substring(i, i + 1)));
                } else {
                    encoded = encoded + code.substring(i);
                    i = code.length();
                }
            }
            response.close();

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
            if (loginResponse.isSuccessful()) {
                if (homeUrl.equals(loginResponse.request().url().toString().trim())) {
                    String gradeUrl = "http://kdjw.hnust.edu.cn/jsxsd/kscj/cjcx_list";
                    Request gradeRequest = new Request.Builder()//创建Request 对象。
                            .url(gradeUrl)
                            .addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)")
                            .build();
                    Response gradeResponse = client.newCall(gradeRequest).execute();
                    TreeMap<String, ArrayList<Grade>> grades = gradeParser2(gradeResponse.body().string());

                    gradeResponse.close();
                    loginResponse.close();
                    response.close();
                    return grades;
                }
            }
            loginResponse.close();
        }

        //密钥请求失败
        response.close();
        return null;
    }

    public static TreeMap<String, ArrayList<Grade>> gradeParser2(String html) {
//        System.out.println(html);
        TreeMap<String, ArrayList<Grade>> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });

        Document doc = Jsoup.parse(html);
        Element div = doc.getElementsByTag("div").first();
        String s = div.ownText();
        String total = s.substring(s.indexOf(" ") + 1);
        Grade totalGrade = new Grade();
        totalGrade.setScore(total);
        ArrayList<Grade> list = new ArrayList<>();
        list.add(totalGrade);
        //totalGrade.setTerm("0");
        map.put("0", list);

        Elements tr = doc.getElementsByTag("tr");
        Element head = tr.remove(0);
        for (Element element : tr) {
            Grade grade = new Grade();
            String[] text = element.text().trim().split(" ");
            ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(text));
            if (text.length == 12) {
                arrayList.add(8, "");
            }
            arrayList.remove(0);
            arrayList.remove(1);
            arrayList.remove(4);
//            System.out.println(arrayList);

            //grade.setTerm(arrayList.get(0));
            grade.setName(arrayList.get(1));
            grade.setScore(arrayList.get(2));
            grade.setCredit(arrayList.get(3));
            grade.setGradePoint(arrayList.get(4));
            //grade.setTerm_again(arrayList.get(5));
            //grade.setExam_type(arrayList.get(6) + " " + arrayList.get(7));
            //grade.setCourse_type(arrayList.get(8) + " " + arrayList.get(9));

            String term = arrayList.get(0);
            if (!map.containsKey(term)) {
                map.put(term, new ArrayList<>());
            }
            map.get(term).add(grade);
        }
        return map;
    }
}


