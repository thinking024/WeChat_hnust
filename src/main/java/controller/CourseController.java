package controller;

import com.alibaba.fastjson.JSON;
import dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;
import pojo.Course;
import pojo.JSONResult;
import pojo.User;
import utils.Crawler;
import utils.Encode;
import utils.MybatisUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

@RestController
@RequestMapping("/course")
public class CourseController {
    @PostMapping(value="/today", produces={"application/json;charset=UTF-8"})
    public String getTodayCourse(HttpServletRequest request) {
        JSONResult myJSON = new JSONResult();
        boolean flag = false;
        String auth = "";
        // 有cookie
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("auth")) {
                flag = true;
                auth += cookie.getValue();
                break;
            }
        }
        // 没有cookie，通过微信auth2授权，未登录则将auth2URL链接作为data返回，前端进入
        if (flag == false) {
            myJSON.setMsg("no auth cookie");
            myJSON.setStatusCode(401);
            return JSON.toJSONString(myJSON);
        }

        try {
            // 获取的cookie，先url解码，再凯撒解码
            //System.out.println("auth=" + auth);
            String decodeAuth = URLDecoder.decode(auth, "ascii");
            String[] s = Encode.kaiserDecode(decodeAuth).split(" ");
            String account = s[0];
            String password = s[1];
            ArrayList<Course> courses = Crawler.getDayCourse(account, password);
            if (courses == null) {
                myJSON.setStatusCode(400);
                myJSON.setMsg("password error");
            } else {
                Course course = new Course();
                course.setName("name");
                courses.add(course);
                myJSON.setStatusCode(100);
                myJSON.setMsg("ok");
                myJSON.setData(courses);
            }
        } catch (Exception e) {
            e.printStackTrace();
            myJSON.setStatusCode(500);
            myJSON.setMsg(e.getMessage());
        } finally {
            return JSON.toJSONString(myJSON);
        }
    }

    @PostMapping(value="/week", produces={"application/json;charset=UTF-8"})
    public String getWeekCourse(@RequestParam int week, String openId, HttpServletRequest request) {
        JSONResult myJSON = new JSONResult();
        boolean flag = false;
        String auth = "";
        String account = null;
        String password = null;

        // 检查cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) { // 有cookie
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("auth")) { // 有账号密码的cookie
                    flag = true;
                    auth += cookie.getValue();
                    String decodeAuth = null;
                    try {
                        decodeAuth = URLDecoder.decode(auth, "ascii");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        myJSON.setStatusCode(500);
                        myJSON.setMsg("URL解码失败\n" + e.getMessage());
                    }
                    String[] s = Encode.kaiserDecode(decodeAuth).split(" ");
                    account = s[0];
                    password = s[1];
                    break;
                }
            }
        }

        // 没有cookie
        // 通过微信auth2授权，未登录则将auth2URL链接作为data返回，前端进入
        if (flag == false) {
            if (openId != null && !openId.isEmpty()) { // 通过openId从数据库获取信息
                SqlSession sqlSession = MybatisUtils.getSqlSession();
                IUserDao mapper = sqlSession.getMapper(IUserDao.class);
                User user = mapper.getUserByOpenId(openId);
                sqlSession.close();

                // 检查用户信息，openId是否已存在
                if (user == null) {
                    myJSON.setStatusCode(402);
                    myJSON.setMsg("this user does not exist, please sign in");
                } else {
                    account = user.getAccount();
                    password = Encode.kaiserDecode(user.getPassword());
                }
            } else { // 没有openId，进入auth2URL，开始登录；或者通过接收微信消息，直接获取openId，附带作为signin.html的参数
                myJSON.setStatusCode(402);
                myJSON.setMsg("this user does not exist, please sign in");
            }
        }

        try {
            System.out.println(account + password);
            ArrayList<Course> courses = Crawler.getWeekCourse(account, password, week);
            if (courses == null) {
                myJSON.setStatusCode(401);
                myJSON.setMsg("account or password error");
            } else {
                myJSON.setStatusCode(100);
                myJSON.setMsg("ok");
                myJSON.setData(courses);
            }
        } catch (IOException e) {
            e.printStackTrace();
            myJSON.setStatusCode(503);
            myJSON.setMsg("学期课程爬取失败\n" + e.getMessage());
        }

        finally {
            return JSON.toJSONString(myJSON);
        }
    }
}
