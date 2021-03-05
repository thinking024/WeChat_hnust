package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pojo.Course;
import utils.Crawler;

import java.util.ArrayList;

@Controller
@RequestMapping("/Data")
public class DataController {
    @RequestMapping("/name")
    public String user1(@RequestParam String name, Model model) {
        // 1.接收前端传递name的参数
        //System.out.println(name);
        // 2.将参数再传递给前端
        model.addAttribute("msg",name);
        String account = "1805050213";
        String password = "hn095573";
        try {
            ArrayList<Course> courses = Crawler.getWeekCourse(account, password, 9);
            if (courses != null) {
                for (Course course : courses) {
                    System.out.println(course);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }
}
