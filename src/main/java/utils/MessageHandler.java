package utils;

import dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import pojo.Course;
import pojo.Exam;
import pojo.TextMessage;
import pojo.User;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * 消息处理工具类（处理微信发来的请求）
 */
public class MessageHandler {

    public static String processRequest(HttpServletRequest request) {
        String result = null;
        try {
            //默认返回的文本消息内容
            String respContent = "";
            //解析xml请求
            Map<String, String> map = MessageUtil.parseXML(request);

            //发送方帐号（open_id），公众号，消息类型
            String fromUserName = map.get("FromUserName");
            String toUserName = map.get("ToUserName");
            String msgType = map.get("MsgType");
            System.out.println("openid=" + fromUserName);
            //判断消息类型
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                respContent = "发送了文本消息";
            } else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {

                String eventType = map.get("Event");
                String eventKey = map.get("EventKey");

                if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                    SqlSession sqlSession = MybatisUtils.getSqlSession();
                    IUserDao mapper = sqlSession.getMapper(IUserDao.class);
                    User user = mapper.getUserByOpenId(fromUserName);
                    sqlSession.close();

                    if (user == null) { // 未登录，带上openid登录
                        respContent = String.format("用户信息不存在，请点击下方链接\n" + "<a href=\"%s?openId=%s\">登录</a>", GlobalInfo.loginUrl, fromUserName);
                    } else {
                        //判断事件key值，对应自定义菜单
                        if (eventKey.equals("TIMETABLE_TODAY")) {
                            ArrayList<Course> courses = Crawler.getDayCourse(user.getAccount(), Encode.kaiserDecode(user.getPassword()));
                            if (courses == null) { // 用户信息过期，带上openid登录
                                respContent = String.format("用户信息失效，请重新登录\n" + "<a href=\"%s?openId=%s\">登录</a>", GlobalInfo.loginUrl, fromUserName);
                            } else { // 拿到课程
                                if (courses.size() == 1) {
                                    respContent += "今日无课\n";
                                } else {
                                    for (int i = 0; i < courses.size() - 1; i++) {
                                        respContent += String.format("第%d、%d节：\n课程：%s\n教室：%s\n", courses.get(i).getOrderBegin(), courses.get(i).getOrderEnd(), courses.get(i).getName(), courses.get(i).getClassroom());
                                    }
                                }
                                respContent += "\n" + courses.get(courses.size() - 1).getName(); // 带上备注
                            }

                        } else if (eventKey.equals("ACHIEVEMENT_MY")) {
                            respContent = "我的成绩被点击";

                        } else if (eventKey.equals("EXAMINATION_SHOW")) {
                            ArrayList<Exam> exams = Crawler.getExam(user.getAccount(), Encode.kaiserDecode(user.getPassword()));
                            if (exams == null) { // 用户信息过期，带上openid登录
                                respContent = String.format("用户信息失效，请重新登录\n" + "<a href=\"%s?openId=%s\">登录</a>", GlobalInfo.loginUrl, fromUserName);
                            } else { // 拿到课程
                                if (exams.size() == 0) {
                                    respContent += "暂无考试\n";
                                } else {
                                    for (int i = 0; i < exams.size(); i++) {
                                        respContent += String.format("课程：%s\n时间：%s\n地点：%s\n", exams.get(i).getName(), exams.get(i).getTime(), exams.get(i).getPlace());
                                    }
                                }
                            }

                        }
                    }
                }
            }

            //回复文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(new Date().getTime());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            textMessage.setContent(respContent);
            result = MessageUtil.textMessageToXml(textMessage);

            //回复图文消息


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

}
