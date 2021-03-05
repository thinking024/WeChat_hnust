package controller;

import com.alibaba.fastjson.JSON;
import dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;
import pojo.JSONResult;
import pojo.User;
import utils.Crawler;
import utils.Encode;
import utils.MybatisUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/login")
public class LoginController {
    @PostMapping(produces={"application/json;charset=UTF-8"})
    public String login(@RequestParam String openId, @RequestParam String account, @RequestParam String password, HttpServletResponse response) {
        JSONResult json = new JSONResult();
        try {
            /*if (Crawler.check(account, password) == false) {
                json.setStatusCode(400);
                json.setMsg("password error");
            } else*/ {
                SqlSession sqlSession = MybatisUtils.getSqlSession();
                IUserDao mapper = sqlSession.getMapper(IUserDao.class);
                User user = mapper.getUserByOpenId(openId);
                int result;
                if (user == null) { // 用户不存在
                    result = mapper.insertUser(new User(account, Encode.kaiserEncode(password), openId));
                } else { // 修改存储的教务网账号信息
                    result = mapper.updateUser(account, Encode.kaiserEncode(password), openId);
                }
                sqlSession.close();

                if (result == 1) { // db操作成功
                    json.setStatusCode(100);
                    json.setMsg("ok");
                } else {
                    json.setStatusCode(501);
                    json.setMsg("fail to operate user table");
                }

                // cookie中不允许特殊字符，需要经过url编码
                String encode = Encode.kaiserEncode(account + " " + password);
                Cookie cookie = new Cookie("auth", URLEncoder.encode(encode, "ascii"));
                cookie.setMaxAge(60 * 60 * 24 * 365);
                response.addCookie(cookie);
            }
        } catch (Exception e) {
            e.printStackTrace();
            json.setStatusCode(500);
            json.setMsg("server failed to check" + e.getMessage());
        } finally {
            return JSON.toJSONString(json);
        }
    }
}
