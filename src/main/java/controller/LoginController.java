package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;
import pojo.User;
import utils.Encode;
import utils.GlobalInfo;
import utils.MybatisUtils;
import utils.NetWorkHelper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/login")
public class LoginController {
    @PostMapping(produces={"application/json;charset=UTF-8"})
    public String login(@RequestParam(required=false) String code, @RequestParam(required=false) String openId, @RequestParam String account, @RequestParam String password, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        if (code == null && openId == null) {
            json.put("statusCode", 400);
            json.put("msg", "openId or code is required");
            return JSONObject.toJSONString(json);
        }
        try {
            /*if (Crawler.check(account, password) == false) {
                json.setStatusCode(401);
                json.setMsg("account or password error");
            } else*/ {
                SqlSession sqlSession = MybatisUtils.getSqlSession();
                IUserDao mapper = sqlSession.getMapper(IUserDao.class);

                if (code != null && openId == null) {
                    NetWorkHelper netHelper = new NetWorkHelper();
                    String url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", GlobalInfo.appId, GlobalInfo.appSecret, code);
                    String result = netHelper.getHttpsResponse(url, "");
                    JSONObject resultJson = JSON.parseObject(result);
                    openId = resultJson.getString("openid");
                }

                System.out.println("openId = " + openId);

                User user = mapper.getUserByOpenId(openId);
                int result;
                if (user == null) { // 用户不存在
                    result = mapper.insertUser(new User(account, Encode.kaiserEncode(password), openId));
                } else { // 修改存储的教务网账号信息
                    result = mapper.updateAccount(account, Encode.kaiserEncode(password), openId);
                }
                if (result == 1) { // db操作成功
                    json.put("statusCode", 100);
                    json.put("msg", "ok");
                } else {
                    json.put("statusCode", 510);
                    json.put("msg", "fail to operate user table");
                }
                sqlSession.close();

                // cookie中不允许特殊字符，需要经过url编码
                String encode = Encode.kaiserEncode(account + " " + password);
                Cookie cookie = new Cookie("auth", URLEncoder.encode(encode, "ascii"));
                cookie.setMaxAge(60 * 60 * 24 * 30);
                response.addCookie(cookie);
            }
        } catch (UnsupportedEncodingException encodingException) {
            encodingException.printStackTrace();
            json.put("statusCode", 520);
            json.put("msg", "decode error" + encodingException.getMessage());
            return JSON.toJSONString(json);

        } catch (Exception e) {
            e.printStackTrace();
            json.put("statusCode", 501);
            json.put("msg", "server failed to check account and password" + e.getMessage());
            return JSON.toJSONString(json);
        }
        return JSON.toJSONString(json);
    }
}
