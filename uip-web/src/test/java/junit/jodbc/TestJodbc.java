package junit.jodbc;

import com.sq.comput.service.IndiComputService;
import com.sq.protocol.jodbc.service.TradeService;
import com.sq.util.DateUtil;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/5/13
 * Time: 15:37
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class TestJodbc extends TestCase {

    @Autowired
    private TradeService tradeService;

    @Test
    public void testDataMig(){
        try {
            tradeService.listTradesBySearchable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenTradeIndicators(){
        try {
            tradeService.genTradeIndicators(Calendar.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
