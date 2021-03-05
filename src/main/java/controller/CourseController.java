package controller;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.*;
import pojo.Course;
import pojo.JSONResult;
import utils.Crawler;
import utils.Encode;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
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
    public String getWeekCourse(@RequestParam String account, @RequestParam String password, @RequestParam int week) throws Exception
    {
        JSONResult myJSON = new JSONResult();
        /*account = "1805050213";
        password = "hn095573";*/
        ArrayList<Course> courses = Crawler.getWeekCourse(account, password, week);
        if (courses == null) {
            myJSON.setStatusCode(400);
            myJSON.setMsg("password error");
        } else {
            myJSON.setStatusCode(100);
            myJSON.setMsg("ok");
            myJSON.setData(courses);
        }
        return JSON.toJSONString(myJSON);
    }
}
