import org.junit.Test;
import utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTest {
    @Test
    public void test() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date monday = DateUtil.getThisWeekMonday(new Date());
        String date = sdf.format(monday);
        System.out.println(date);
    }
}
