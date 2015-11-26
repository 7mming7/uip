package junit.loadometer;

import com.sq.loadometer.service.TradeDataService;
import com.sq.util.DateUtil;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

/**
 * User: shuiqing
 * Date: 2015/9/17
 * Time: 11:31
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class LoadometerTest extends TestCase {

    @Autowired
    private TradeDataService tradeDataService;

    /*@Test
    public void syncLoadometerTrade(){
        tradeDataService.syncLoadometerTrade("20150901");
    }*/
}
