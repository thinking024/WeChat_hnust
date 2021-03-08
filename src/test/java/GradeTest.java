import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import pojo.Exam;
import pojo.Grade;
import utils.Crawler;

import java.io.File;
import java.util.*;

public class GradeTest {

    @Test
    public void test() throws Exception{
        TreeMap<String, ArrayList<Grade>> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.compareTo(o1);
            }
        });
        ArrayList<Grade> grades = Crawler.getGrade("1805050213", "hn095573");

        for (Grade grade : grades) {
            String term = grade.getTerm();
            if (!map.containsKey(term)) {
                map.put(term, new ArrayList<>());
            }
            map.get(term).add(grade);
        }

        Set<String> keySet = map.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            System.out.println(key);
            ArrayList<Grade> list = map.get(key);
            for (Grade grade : list) {
                System.out.println(grade);
            }
        }
    }

    @Test
    public void test3() throws Exception{
        TreeMap<String, ArrayList<Grade>> map = Crawler.getGrade2("1805050213", "hn095573");
        Set<String> keySet = map.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            System.out.println(key);
            ArrayList<Grade> list = map.get(key);
            for (Grade grade : list) {
                System.out.println(grade);
            }
        }
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

    @Test
    public void test_() {

    }
}
