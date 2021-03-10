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
