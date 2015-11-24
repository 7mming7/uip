package junit.ndxt;

import com.sq.protocol.opc.controller.RtdbPushController;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: shuiqing
 * Date: 15/11/16
 * Time: 下午5:19
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class NdxtTest extends TestCase {

    @Autowired
    private RtdbPushController rtdbPushController;

    /*@Test
    public void testNdxt(){
        String str = rtdbPushController.ndxtRtdbPush("Macsv5.Device3.Group3.B1_DUTYZSJ");
        System.out.println(str);
    }*/
}
