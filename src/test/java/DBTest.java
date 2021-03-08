import dao.ICourseDao;
import dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import pojo.Course;
import pojo.User;
import utils.*;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DBTest {
    @Test
    public void test() {
        String openId = "oQY_X6QicgzDEhj_dTMd3NR4ylAg";
        String account = "1805050213";
        String password = "hn095573";
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        User user = mapper.getUserByOpenId(openId);
        ICourseDao mapper1 = sqlSession.getMapper(ICourseDao.class);
        int result = mapper1.deleteCourse("oQY_X6QicgzDEhj_dTMd3NR4ylAg");
        System.out.println(result);
        sqlSession.close();
    }

    @Test
    public void test2() throws Exception {
        String openId = "oQY_X6QicgzDEhj_dTMd3NR4ylAg";
        String account = "1805050213";
        String password = "hn095573";
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        ICourseDao courseMapper = sqlSession.getMapper(ICourseDao.class);
        User user = mapper.getUserByOpenId(openId);
        int result;
        if (user == null) { // 用户不存在
            result = mapper.insertUser(new User(account, Encode.kaiserEncode(password), openId));
        } else { // 修改存储的教务网账号信息
            Date date = new Date();
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            java.sql.Date expire = user.getExpire();
            System.out.println(expire);
            System.out.println(sqlDate);
            System.out.println(date);
            Date expireUtilDate = new Date(expire.getTime());
            System.out.println(DateUtil.getDay(expireUtilDate));
            System.out.println(DateUtil.getDay(date));

        }
        sqlSession.close();
    }

    @Test
    public void test_() {
        String result = MessageHandler.sendTodayCourse("oQY_X6QicgzDEhj_dTMd3NR4ylAg");
        System.out.println(result);
    }
}
