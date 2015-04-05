package junit.protocol;

import com.sq.protocol.opc.service.MesuringPointService;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/4/1
 * Time: 11:36
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class MesuringPointTest extends TestCase {

    @Autowired
    private MesuringPointService mesuringPointService;

    @Test
    public void readBrunch () {
        this.mesuringPointService.fetchReadSyncItems(1);
    }
}
