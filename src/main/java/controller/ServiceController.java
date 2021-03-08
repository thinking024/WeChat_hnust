package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;
import pojo.Course;
import pojo.User;
import utils.Crawler;
import utils.Encode;
import utils.GlobalInfo;
import utils.MybatisUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

@RestController
public class ServiceController {
    @PostMapping(value="/today", produces={"application/json;charset=UTF-8"})
    public String getTodayCourse(HttpServletRequest request) {
        JSONObject myJSON = new JSONObject();
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
            myJSON.put("statusCode", 401);
            myJSON.put("msg", "no auth cookie");
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
                myJSON.put("statusCode", 400);
                myJSON.put("msg", "account or password error");
            } else {
                Course course = new Course();
                course.setName("name");
                courses.add(course);
                myJSON.put("statusCode", 100);
                myJSON.put("msg", "ok");
                myJSON.put("data", courses);
            }
        } catch (Exception e) {
            e.printStackTrace();
            myJSON.put("statusCode", 500);
            myJSON.put("msg", e.getMessage());
        } finally {
            return JSON.toJSONString(myJSON);
        }
    }

    @GetMapping(value="/query/{type}", produces={"application/json;charset=UTF-8"})
    public String getWeekCourse(@PathVariable String type, @RequestParam(required=false, defaultValue="0") int week, String openId, HttpServletRequest request) {
        JSONObject myJSON = new JSONObject();
        if (week < 0) {
            week = 0;
        }
        if ((type.equals("course")) || type.equals("grade")) { // 参数正确
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
                            myJSON.put("statusCode", 500);
                            myJSON.put("msg", "URL解码失败\n" + e.getMessage());
                        }
                        String[] s = Encode.kaiserDecode(decodeAuth).split(" ");
                        account = s[0];
                        password = s[1];
                        break;
                    }
                }
            }

            // 没有cookie
            if (flag == false) {
                if (openId != null && !openId.isEmpty()) { // cookie已失效，但曾经登陆过，通过openId从数据库获取信息
                    SqlSession sqlSession = MybatisUtils.getSqlSession();
                    IUserDao mapper = sqlSession.getMapper(IUserDao.class);
                    User user = mapper.getUserByOpenId(openId);
                    sqlSession.close();

                    // 检查用户信息，openId是否已存在
                    if (user == null) {
                        String redirectURL = "http://yiyuanzhu.nat300.top/signin.html?type=" + type;
                        String auth2URL = null;
                        try {
                            auth2URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                                    + GlobalInfo.appId + "&redirect_uri=" + URLEncoder.encode(redirectURL, "utf-8") +
                                    "&response_type=code&scope=snsapi_base&state=#wechat_redirect";
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        myJSON.put("statusCode", 402);
                        myJSON.put("msg", "this user does not exist, please sign in");
                        myJSON.put("data", auth2URL);
                        return JSONObject.toJSONString(myJSON);
                    } else {
                        account = user.getAccount();
                        password = Encode.kaiserDecode(user.getPassword());
                    }
                } else { // 从未登陆过，附带的参数中无openId，进入auth2URL，开始登录
                    String redirectURL = "http://yiyuanzhu.nat300.top/signin.html?type=" + type;
                    String auth2URL = null;
                    try {
                        auth2URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                                + GlobalInfo.appId + "&redirect_uri=" + URLEncoder.encode(redirectURL, "utf-8") +
                                "&response_type=code&scope=snsapi_base&state=#wechat_redirect";
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    myJSON.put("statusCode", 402);
                    myJSON.put("msg", "this user does not exist, please sign in");
                    myJSON.put("data", auth2URL);
                    return JSONObject.toJSONString(myJSON);
                }
            }

            try {
                System.out.println(account + password);
                Object results = null;
                if ("course".equals(type)) {
                    results = Crawler.getWeekCourse(account, password, week);
                } else {
                    results = Crawler.getGrade2(account, password);
                }
                if (results == null) {

                    myJSON.put("statusCode", 401);
                    myJSON.put("msg", "account or password error");
                } else {
                    myJSON.put("statusCode", 100);
                    myJSON.put("msg", "ok");
                    myJSON.put("data", results);
                }
            } catch (IOException e) {
                e.printStackTrace();
                myJSON.put("statusCode", 500);
                myJSON.put("msg", "爬取失败\n" + e.getMessage());
                return JSON.toJSONString(myJSON);
            }

        }  else {
            myJSON.put("statusCode", 400);
            myJSON.put("msg", "parameters error");
        }
        return JSON.toJSONString(myJSON);
    }

    @GetMapping(value="/query2/{type}", produces={"application/json;charset=UTF-8"})
    public String getWeekCourse2(@PathVariable String type, @RequestParam(required=false, defaultValue="0") int week, HttpServletRequest request, HttpServletResponse response) {
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
                if (flag == false) {// 从未登陆过，附带的参数中无openId，进入auth2URL，开始登录
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
                    results = Crawler.getWeekCourse(account, password, week);
                } else {
                    results = Crawler.getGrade2(account, password);
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

    @GetMapping(value="/query3/{type}", produces={"application/json;charset=UTF-8"})
    public String getWeekCourse3(@PathVariable String type, @RequestParam(required=false, defaultValue="0") int week, HttpServletRequest request, HttpServletResponse response) {
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
                if (flag == false) {// 从未登陆过，附带的参数中无openId，进入auth2URL，开始登录
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
                    results = Crawler.getWeekCourse(account, password, week);
                } else {
                    results = Crawler.getGrade2(account, password);
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

}
