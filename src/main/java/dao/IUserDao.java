package dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import pojo.User;

public interface IUserDao {
    @Select("select * from user where account = #{account}")
    User getUserByAccount(@Param("account") String account);

    @Select("select * from user where open_id = #{openId}")
    User getUserByOpenId(@Param("openId") String openId);

    @Insert("insert into user values (#{account}, #{password}, #{openId})")
    int insertUser(User user);

    @Update("update user set account=#{account}, password=#{password} where open_id=#{openId}")
    int updateUser(@Param("account") String account, @Param("password") String password, @Param("openId") String openId);
}
