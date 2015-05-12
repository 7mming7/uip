package junit.ws;

import com.sq.protocol.ws.wsimpl.indicator.WsServerIndiCompet4Standard;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/4/29
 * Time: 11:03
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class TestWs extends TestCase {

    @Autowired
    private WsServerIndiCompet4Standard wsServerIndiCompet4Standard;

    /*@Test
    public void testWsServerComput4Standard() throws IOException, URISyntaxException {
        String response = this.wsServerIndiCompet4Standard.receiveIndicatorCompetInfo("20150423");
        System.out.println(response);
    }*/
}