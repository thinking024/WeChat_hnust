package utils;

import dao.ICourseDao;
import dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import pojo.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * 消息处理工具类（处理微信发来的请求）
 */
public class MessageHandler {
    public static String sendTodayCourse(String fromUserName) {
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        IUserDao userMapper = sqlSession.getMapper(IUserDao.class);
        User user = userMapper.getUserByOpenId(fromUserName);
        ICourseDao courseMapper = sqlSession.getMapper(ICourseDao.class);
        String respContent = "";
        ArrayList<Course> courses = null;
        Date utilDate = new Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime()); // 当前时间
        java.sql.Date expire = user.getExpire(); // 过期时间

        if (expire == null || sqlDate.after(expire)) { // 课程信息不存在或者已过期，则需重新爬取

            try {
                courses = Crawler.getDayCourse(user.getAccount(), Encode.kaiserDecode(user.getPassword()));
                //courses = Crawler.getWeekCourse(user.getAccount(), Encode.kaiserDecode(user.getPassword()), 9);
            } catch (Exception e) { // 爬取失败
                e.printStackTrace();
                respContent += e.getMessage();
                sqlSession.close();
                return respContent;
            }

            if (courses == null) { // 用户信息过期，带上openid登录
                respContent += String.format("用户信息失效，请重新登录\n" + "<a href=\"%s?openId=%s\">登录</a>", GlobalInfo.loginUrl, fromUserName);
            } else { // 拿到课程
                if (courses.size() == 1) {
                    respContent += "今日无课\n\n";
                } else {
                    for (int i = 0; i < courses.size() - 1; i++) {
                        Course course = courses.get(i);
                        respContent += String.format("第%d、%d节：\n课程：%s\n教室：%s\n\n", course.getOrderBegin(), course.getOrderEnd(), course.getName(), course.getClassroom());
                    }
                }

                String info = courses.get(courses.size() - 1).getName();
                respContent += "\n" + info.replace(";", "\n"); // 带上备注
            }
        } else { // 从数据库提取当天信息
            int day = DateUtil.getDay(new Date());
            courses = courseMapper.getDayCourse(fromUserName, day);
            if (courses == null || courses.size() == 0) {
                respContent += "今日无课\n";
            } else {
                for (int i = 0; i < courses.size(); i++) {
                    Course course = courses.get(i);
                    respContent += String.format("第%d、%d节：\n课程：%s\n教室：%s\n\n", course.getOrderBegin(), course.getOrderEnd(), course.getName(), course.getClassroom());
                }
            }
            respContent += user.getInfo().replace(";", "\n"); // 带上备注
        }

        respContent += String.format("\n<a href=\"%s\">点击查看学期课表详情</a>", "http://yiyuanzhu.nat300.top/course.html");
        sqlSession.close();
        return respContent;
    }

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
            String eventType = map.get("Event");

            System.out.println(eventType + "----" + msgType);
            if (MessageUtil.REQ_MESSAGE_TYPE_TEXT.equals(msgType)) {
                respContent += "hello world";
            } else if (MessageUtil.EVENT_TYPE_UNSUBSCRIBE.equals(eventType)) { // 取消关注
                SqlSession sqlSession = MybatisUtils.getSqlSession();
                IUserDao userMapper = sqlSession.getMapper(IUserDao.class);
                userMapper.deleteUser(fromUserName);
                sqlSession.close();

            } else if (MessageUtil.EVENT_TYPE_SUBSCRIBE.equals(eventType)) { // 关注
                respContent += String.format("欢迎关注，请点击下方链接\n" + "<a href=\"%s?openId=%s\">登录</a>", GlobalInfo.loginUrl, fromUserName);

            } else if (MessageUtil.REQ_MESSAGE_TYPE_EVENT.equals(msgType)) { // 触发菜单
                String eventKey = map.get("EventKey");

                if (MessageUtil.EVENT_TYPE_CLICK.equals(eventType)) {
                    SqlSession sqlSession = MybatisUtils.getSqlSession();
                    IUserDao userMapper = sqlSession.getMapper(IUserDao.class);
                    User user = userMapper.getUserByOpenId(fromUserName);
                    sqlSession.close();

                    if (user == null) { // 未登录，带上openid登录
                        respContent += String.format("用户信息不存在，请点击下方链接\n" + "<a href=\"%s?openId=%s\">登录</a>", GlobalInfo.loginUrl, fromUserName);
                    } else {

                        //判断事件key值，对应自定义菜单
                        if (eventKey.equals("TIMETABLE_TODAY")) { // 今日课程
                            respContent += sendTodayCourse(fromUserName);

                        } else if (eventKey.equals("ACHIEVEMENT_MY")) { // 近期成绩
                            TreeMap<String, ArrayList<Grade>> gradeMap = Crawler.getGrade(user.getAccount(), Encode.kaiserDecode(user.getPassword()));
                            if (gradeMap == null) { // 用户信息过期，带上openid登录
                                respContent += String.format("用户信息失效，请重新登录\n" + "<a href=\"%s?openId=%s\">登录</a>", GlobalInfo.loginUrl, fromUserName);
                            } else { // 拿到课程
                                if (gradeMap.size() == 1) {
                                    respContent += "暂无任何学期成绩\n";
                                } else {
                                    ArrayList<Grade> gradeList = gradeMap.firstEntry().getValue();
                                    respContent += "学期：" + gradeMap.firstEntry().getKey() + "\n\n";
                                    for (Grade grade : gradeList) {
                                        respContent += String.format("%s\n分数：%s，学分：%s，绩点：%s\n\n", grade.getName(), grade.getScore(), grade.getCredit(), grade.getGradePoint());
                                    }
                                    respContent += String.format("\n<a href=\"%s\">点击查看成绩详情</a>", "http://yiyuanzhu.nat300.top/grade.html");
                                }
                            }

                        } else if (eventKey.equals("EXAMINATION_SHOW")) { // 考试安排
                            ArrayList<Exam> exams = Crawler.getExam(user.getAccount(), Encode.kaiserDecode(user.getPassword()));
                            if (exams == null) { // 用户信息过期，带上openid登录
                                respContent = String.format("用户信息失效，请重新登录\n" + "<a href=\"%s?openId=%s\">登录</a>", GlobalInfo.loginUrl, fromUserName);
                            } else { // 拿到课程
                                if (exams.size() == 0) {
                                    respContent += "当前学期暂无考试\n";
                                } else {
                                    for (int i = 0; i < exams.size(); i++) {
                                        respContent += String.format("课程：%s\n时间：%s\n地点：%s\n", exams.get(i).getName(), exams.get(i).getTime(), exams.get(i).getPlace());
                                    }
                                }
                            }

                        }
                    }

                } // 点击事件
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
