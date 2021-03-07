import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import utils.GlobalInfo;
import utils.NetWorkHelper;


/**
 * 设置菜单的工具类
 */
public class MenuSetting {

    public static void main(String[] args) throws JSONException {
        //add();
        delete();
    }

    public static void add() throws JSONException{
        String s = getAccessToken();
        NetWorkHelper netHelper = new NetWorkHelper();

        String json ="{\n" +
                "    \"button\": [\n" +
                "    {\n" +
                "        \"name\": \"查询课表\",\n" +
                "        \"sub_button\": [\n" +
                "            {\n" +
                "                \"type\": \"click\",\n" +
                "                \"name\": \"今日课表\",\n" +
                "                \"key\": \"TIMETABLE_TODAY\",\n" +
                "                \"sub_button\": [ ]\n" +
                "            },\n" +
                "            {\n" +
                "                \"type\": \"view\",\n" +
                "                \"name\": \"本周课表\",\n" +
                "                \"key\": \"TIMETABLE_WEEK\",\n" +
                "                \"url\":\"http://yiyuanzhu.nat300.top/course.html\",\n" +
                "                \"sub_button\": [ ]\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    {\n" +
                "        \"type\": \"click\",\n" +
                "        \"name\":\"我的成绩\",\n" +
                "        \"key\":\"ACHIEVEMENT_MY\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"type\": \"click\",\n" +
                "        \"name\":\"考试安排\",\n" +
                "        \"key\":\"EXAMINATION_SHOW\"\n" +
                "    }\n" +
                "        ]\n" +
                "}";
        System.out.println(json);

        String Url = String.format("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s", s);
        String result = netHelper.getHttpsResponsePostBody(Url, "POST", json);
        System.out.println(result);
    }

    public static void delete() throws JSONException {
        String s = getAccessToken();
        NetWorkHelper netHelper = new NetWorkHelper();
        String Url = String.format("https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=%s", s);
        String result = netHelper.getHttpsResponse(Url, "");
        System.out.println(result);
    }

    public static String getAccessToken() throws JSONException {
        NetWorkHelper netHelper = new NetWorkHelper();
        String Url = String.format(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", GlobalInfo.appId,GlobalInfo.appSecret);
        String result = netHelper.getHttpsResponse(Url, "");
        System.out.println(result);
        JSONObject json = JSON.parseObject(result);
        return json.getString("access_token");
    }
}

/*
{
    "button": [
    {
        "name": "查询课表",
        "sub_button": [
            {
                "type": "click",
                "name": "今日课表",
                "key": "TIMETABLE_TODAY",
                "sub_button": [ ]
            },
            {
                "type": "view",
                "name": "本周课表",
                "key": "TIMETABLE_WEEK",
                "url":"http://mpmrix.natappfree.cc/login.html",
                "sub_button": [ ]
            }
        ]
    },
    {
        "type": "click",
        "name":"我的成绩",
        "key":"ACHIEVEMENT_MY"
    },
    {
        "type": "click",
        "name":"考试安排",
        "key":"EXAMINATION_SHOW"
    }
    ]
}
*/
