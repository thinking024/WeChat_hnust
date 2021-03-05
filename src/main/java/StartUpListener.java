import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import pojo.AccessToken;
import utils.GlobalInfo;
import utils.NetWorkHelper;

public class StartUpListener implements ApplicationContextAware {

    private AccessToken getAccessToken(String appId, String appSecret) {
        NetWorkHelper netHelper = new NetWorkHelper();
        /**
         * 接口地址为https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET，其中grant_type固定写为client_credential即可。
         */
        String Url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", appId, appSecret);
        //此请求为https的get请求，返回的数据格式为{"access_token":"ACCESS_TOKEN","expires_in":7200}
        String result = netHelper.getHttpsResponse(Url, "");
        JSONObject json = JSON.parseObject(result);

        String Url2 = String.format("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi", json.getString("access_token"));
        //此请求为https的get请求，返回的数据格式为{
        //"errcode":0,
        //"errmsg":"ok",
        //"ticket":"JSAPI_TICKET",
        //"expires_in":7200}
        String result2 = netHelper.getHttpsResponse(Url2, "");
        //System.out.println("获取到的ticket="+result2);
        //使用FastJson将Json字符串解析成Json对象
        JSONObject json2 = JSON.parseObject(result2);

        AccessToken token = new AccessToken();
        token.setValue(json.getString("access_token"));
        token.setExpiresIn(json.getInteger("expires_in"));
        token.setTicket(json2.getString("ticket"));

        return token;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        //获取accessToken
                        GlobalInfo.accessToken = getAccessToken(GlobalInfo.appId, GlobalInfo.appSecret);
                        //获取成功
                        if (GlobalInfo.accessToken != null) {
                            System.out.println("accessToken = " + GlobalInfo.accessToken);
                            //获取到access_token 休眠7000秒,大约2个小时左右
                            Thread.sleep(7000 * 1000);
                            //Thread.sleep(10 * 1000);//10秒钟获取一次
                        } else {
                            //获取失败
                            System.out.println("fail to get access_token");
                            Thread.sleep(1000 * 3); //获取的access_token为空 休眠3秒
                        }
                    } catch (Exception e) {
                        System.out.println("发生异常：" + e.getMessage());
                        e.printStackTrace();
                        try {
                            Thread.sleep(1000 * 10); //发生异常休眠1秒
                        } catch (Exception e1) {

                        }
                    }
                }

            }
        }).start();
    }
}
