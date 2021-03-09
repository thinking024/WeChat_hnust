package dao;

import org.apache.ibatis.annotations.*;
import pojo.Course;
import pojo.User;

import java.util.ArrayList;

public interface ICourseDao {
    @Select("select * from course where open_id = #{openId}")
    ArrayList<Course> getCourse(@Param("openId") String openId);


    @Select("select * from course where open_id = #{openId} and day=#{day}")
    ArrayList<Course> getDayCourse(@Param("openId") String openId, @Param("day") int day);
/*
    @Insert("insert into course values (#{openId}, #{classroom}, #{orderBegin}, #{orderEnd}, #{name})")
    int insertCourse(String openId, String classroom, int orderBegin, int orderEnd, String name);*/

    @Insert("insert into course values (#{openId}, #{classroom}, #{orderBegin}, #{orderEnd}, #{name}, #{day})")
    int insertCourse(Course course);

    @Delete("delete from course where open_id = #{openId}")
    int deleteCourse(@Param("openId") String openId);
}
