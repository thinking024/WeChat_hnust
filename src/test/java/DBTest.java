import dao.IUserDao;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import pojo.User;
import utils.Encode;
import utils.MybatisUtils;

import java.util.List;

public class DBTest {
    @Test
    public void test() {
        String openId = "oQY_X6QicgzDEhj_dTMd3NR4ylAg";
        String account = "1805050213";
        String password = "hn095573";
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);
        User user = mapper.getUserByOpenId(openId);
        int result;
        //System.out.println(user);
        if (user == null) { // 用户不存在
            result = mapper.insertUser(new User(account, Encode.kaiserEncode(password), openId));
        } else { // 修改存储的教务网账号信息
            result = mapper.updateUser(account, Encode.kaiserEncode(password), openId);
        }
        sqlSession.close();
    }
}
