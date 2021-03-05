import org.junit.Test;
import org.springframework.util.DigestUtils;
import pojo.Course;
import utils.Crawler;
import utils.Encode;
import utils.GlobalInfo;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MsgCode {

    @Test
    public void test() throws Exception {
        ArrayList<Course> courses = Crawler.getWeekCourse("1805050213", "hn095573", 5);
        for (Course cours : courses) {
            System.out.println(cours);
        }
    }

    @Test
    public void test2() throws Exception {
        String redirectURL = "http://yiyuanzhu.nat300.top/WX/authorize";
        String auth2URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
                + GlobalInfo.appId + "&redirect_uri=" + URLEncoder.encode(redirectURL, "utf-8") +
                "&response_type=code&scope=snsapi_base&state=#wechat_redirect";
        System.out.println(auth2URL);
    }

    @Test
    public void test3() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            // 注意格式需要与上面一致，不然会出现异常
            date = sdf.parse("2021-03-14");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("字符串转换成时间:" + date);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        int day = instance.get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) {
            instance.add(Calendar.DATE, 1);
            System.out.println(sdf.format(instance.getTime()));
        }
    }


}
