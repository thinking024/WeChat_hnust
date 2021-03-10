package controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;
import utils.Encode;
import utils.GlobalInfo;
import utils.MessageHandler;
import utils.NetWorkHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;

@RestController
@RequestMapping("/WX")
public class WXController {

    @GetMapping()
    public String responseWX(@RequestParam String signature, @RequestParam String timestamp, @RequestParam String nonce, @RequestParam String echostr) {
        String[] strArray = {GlobalInfo.token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(str);
        }
        String sortString = sb.toString();
        String mySignature = Encode.sha1(sortString);
        if (signature.equals(mySignature)) {
            System.out.println("签名校验通过。");
            return echostr;
        } else {
            System.out.println("签名校验失败.");
            return "";
        }
    }

    // auth2URL放置在菜单中，用户主动点击，进入回调页面，回调页面为登录页面，登录页面获取code，通过ajax请求下面的方法，获取openId
    /*@GetMapping("/authorize")
    public String authorize() throws Exception {
        String redirectURL = "http://yiyuanzhu.nat300.top/index.jsp";
        String auth2URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + GlobalInfo.appId + "&redirect_uri=" + URLEncoder.encode(redirectURL, "utf-8") +
                          "&response_type=code&scope=snsapi_base&state=#wechat_redirect";
        return auth2URL;
    }*/

    @GetMapping("/authorize")
    public String authorize(@RequestParam String code) {
        NetWorkHelper netHelper = new NetWorkHelper();
        String Url = String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code", GlobalInfo.appId, GlobalInfo.appSecret, code);
        String result = netHelper.getHttpsResponse(Url, "");
        System.out.println("获取到的auth="+result);
        //使用FastJson将Json字符串解析成Json对象
        JSONObject json = JSON.parseObject(result);
        String openId = json.getString("openid");
        return openId;
    }

    /**
     * 处理微信服务器发来的消息
     * @param request
     * @param response
     * @throws IOException
     */
    @PostMapping(produces={"text/plain;charset=UTF-8"})
    public String getMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置编码格式
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        //接收、处理、响应由微信服务器转发的用户发送给公众号的消息
        String result = "";
        try{
            //System.out.println("开始构造消息" + request);
            result = MessageHandler.processRequest(request);
            if(result == null || "".equals(result)) {
                result = "未正确响应";
            }
            //System.out.println("构造结果：" + result);
        } catch (Exception e){
            e.printStackTrace();
            result = e.getMessage();
            //System.out.println("发生异常："+ e.getMessage());
        }
        finally {
            return result;
        }
    }


    /**
     * 排序并拼接
     * @param token
     * @param timestamp
     * @param nonce
     * @return
     */
    public String sort(String token, String timestamp, String nonce) {
        String[] strArray = {token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(str);
        }
        return sb.toString();
    }
}
