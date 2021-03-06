import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import pojo.Grade;
import utils.Crawler;

import java.io.File;
import java.util.ArrayList;

public class GradeTest {

    @Test
    public void test() throws Exception{
        ArrayList<Grade> grades = Crawler.getGrade("1805050213", "hn095573");
        for (Grade grade : grades) {
            System.out.println(grade);
        }
        System.out.println(grades.size());
    }

    @Test
    public void test2() throws Exception {
        File file = new File("C:\\Users\\26438\\Desktop\\学生个人考试成绩.html");
        Document document = Jsoup.parse(file, "UTF-8");
        Element div = document.getElementsByTag("div").first();
        String s = div.ownText();
        System.out.println(s);
        String substring = s.substring(s.indexOf(" ") + 1);
        System.out.println(substring);
    }
}
