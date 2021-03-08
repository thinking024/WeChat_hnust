package dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import pojo.User;

import java.sql.Date;

public interface IUserDao {
    @Select("select * from user where account = #{account}")
    User getUserByAccount(@Param("account") String account);

    @Select("select * from user where open_id = #{openId}")
    User getUserByOpenId(@Param("openId") String openId);

    @Insert("insert into user values (#{account}, #{password}, #{openId}, #{expire}, #{info})")
    int insertUser(User user);

    @Update("update user set account=#{account}, password=#{password} where open_id=#{openId}")
    int updateAccount(@Param("account") String account, @Param("password") String password, @Param("openId") String openId);

    @Update("update user set expire=#{expire} where open_id=#{openId}")
    int updateExpire(@Param("expire") Date expire, @Param("openId") String openId);


    @Update("update user set info=#{info} where open_id=#{openId}")
    int updateInfo(@Param("info") String info, @Param("openId") String openId);
}
