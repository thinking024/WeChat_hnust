package dao;

import org.apache.ibatis.annotations.*;
import pojo.User;

import java.sql.Date;

public interface IUserDao {
    @Select("select * from user where account = #{account}")
    User getUserByAccount(@Param("account") String account);

    @Select("select * from user where open_id = #{openId}")
    User getUserByOpenId(@Param("openId") String openId);

    @Insert("insert into user values (#{account}, #{password}, #{openId}, #{expire}, #{info}, #{currentWeek})")
    int insertUser(User user);

    @Update("update user set account=#{account}, password=#{password} where open_id=#{openId}")
    int updateAccount(@Param("account") String account, @Param("password") String password, @Param("openId") String openId);

    @Update("update user set expire=#{expire}, info=#{info}, currentWeek=#{currentWeek}  where open_id=#{openId}")
    int updateCourseInfo(@Param("expire") Date expire, @Param("info") String info, @Param("currentWeek") int currentWeek, @Param("openId") String openId);

    @Delete("delete from user where open_id=#{openId}")
    int deleteUser(@Param("openId") String openId);
}
