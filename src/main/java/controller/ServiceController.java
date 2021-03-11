package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dao.ICourseDao;
import dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;
import pojo.Course;
import pojo.User;
import utils.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
public class ServiceController {

    @GetMapping(value="/query/{type}", produces={"application/json;charset=UTF-8"})
    public String getWeekCourse(@PathVariable String type, @RequestParam(required=false, defaultValue="0") int week, HttpServletRequest request, HttpServletResponse response) {
        JSONObject myJSON = new JSONObject();
        Cookie authCookie = null;
        if (week < 0) {
            week = 0;
        }
        if ((type.equals("course")) || type.equals("grade")) { // 参数正确
            boolean flag = false;
            String auth = "";
            String account = null;
            String password = null;
            try {
                // 检查cookie
                Cookie[] cookies = request.getCookies();
                if (cookies != null) { // 有cookie
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("auth")) { // 有账号密码的cookie
                            flag = true;
                            authCookie = cookie;
                            auth += cookie.getValue();
                            String decodeAuth = URLDecoder.decode(auth, "ascii");
                            String[] s = Encode.kaiserDecode(decodeAuth).split(" ");
                            account = s[0];
                            password = s[1];
                            break;
                        }
                    }
                }

                // 没有cookie
                if (flag == false) { // 从未登陆过，附带的参数中无openId，进入auth2URL，开始登录
                    String redirectURL = "http://yiyuanzhu.nat300.top/signin.html?type=" + type;
                    String auth2URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                            + GlobalInfo.appId + "&redirect_uri=" + URLEncoder.encode(redirectURL, "utf-8") +
                            "&response_type=code&scope=snsapi_base&state=#wechat_redirect";
                    myJSON.put("statusCode", 402);
                    myJSON.put("msg", "this user does not exist, please sign in");
                    myJSON.put("data", auth2URL);
                    return JSONObject.toJSONString(myJSON);
                }

                System.out.println(account + password);
                Object results = null;
                if ("course".equals(type)) {
                    results = returnWeekCourse(account, password, week);
                } else {
                    results = Crawler.getGrade(account, password);
                }
                if (results == null) {
                    String redirectURL = "http://yiyuanzhu.nat300.top/signin.html?type=" + type;
                    String auth2URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                            + GlobalInfo.appId + "&redirect_uri=" + URLEncoder.encode(redirectURL, "utf-8") +
                            "&response_type=code&scope=snsapi_base&state=#wechat_redirect";
                    myJSON.put("statusCode", 401);
                    myJSON.put("msg", "account or password error");
                    myJSON.put("data", auth2URL);
                    return JSONObject.toJSONString(myJSON);
                } else {
                    myJSON.put("statusCode", 100);
                    myJSON.put("msg", "ok");
                    myJSON.put("data", results);
                    authCookie.setMaxAge(60 * 60 * 24 * 30);
                    authCookie.setPath("/");
                    response.addCookie(authCookie);
                }

            } catch (UnsupportedEncodingException encodingException) {
                encodingException.printStackTrace();
                myJSON.put("statusCode", 520);
                myJSON.put("msg", "URL解码失败\n" + encodingException.getMessage());
                return JSON.toJSONString(myJSON);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                myJSON.put("statusCode", 500);
                myJSON.put("msg", "爬取失败\n" + ioException.getMessage());
                return JSON.toJSONString(myJSON);
            }
        }  else {
            myJSON.put("statusCode", 400);
            myJSON.put("msg", "parameters error");
        }
        return JSON.toJSONString(myJSON);
    }

    public ArrayList<Course> returnWeekCourse(String account, String password, int week) throws IOException {
        ArrayList<Course> courses = null;

        // 查询本周课程
        if (week == 0) {

            SqlSession sqlSession = MybatisUtils.getSqlSession();
            IUserDao userMapper = sqlSession.getMapper(IUserDao.class);
            User user = userMapper.getUserByAccount(account);
            if (user == null) {
                return null;
            }
            ICourseDao courseMapper = sqlSession.getMapper(ICourseDao.class);
            Date currentUtil = new Date();
            java.sql.Date currentSql = new java.sql.Date(currentUtil.getTime()); // 当前时间
            java.sql.Date expireSql = user.getExpire(); // 过期时间

            if (expireSql == null || currentSql.after(expireSql)) { // 课程信息不存在或者已过期，则需重新爬取
                courses = Crawler.getWeekCourse(account, password, 0);

                // 课程信息不存在，顺便存入数据库
                if (courses != null) {
                    courseMapper.deleteCourse(user.getOpenId()); // 删除已过期信息
                    System.out.println("delete===========");
                    Date newExpireUtil = DateUtil.getNextWeekMonday(currentUtil); // 新的过期时间
                    java.sql.Date newExpireSql = new java.sql.Date(newExpireUtil.getTime());

                    for (int i = 0; i < courses.size() - 2; i++) {
                        Course course = courses.get(i);
                        course.setOpenId(user.getOpenId());
                        courseMapper.insertCourse(course);
                    }

                    String info = courses.get(courses.size() - 2).getName(); // 更新备注信息
                    System.out.println("info=" + info);
                    int currentWeek = courses.get(courses.size() - 1).getDay(); // 更新当前周
                    userMapper.updateCourseInfo(newExpireSql, info, currentWeek, user.getOpenId());
                }
            } else { // 从数据库提取本周信息
                courses = courseMapper.getCourse(user.getOpenId());

                Course course1 = new Course();
                course1.setName(user.getInfo());
                courses.add(course1);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date monday = DateUtil.getThisWeekMonday(new Date());
                String beginDate = sdf.format(monday);
                Course course2 = new Course();
                course2.setClassroom(beginDate);
                course2.setDay(user.getCurrentWeek());
                courses.add(course2);
            }

            sqlSession.close();

        } else {
            courses = Crawler.getWeekCourse(account, password, week);
        }
        return courses;
    }

}
