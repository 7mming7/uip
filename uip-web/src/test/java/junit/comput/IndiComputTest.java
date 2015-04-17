package junit.comput;

import com.sq.comput.service.IndiComputService;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/4/17
 * Time: 16:09
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io
 * _
 * |_)._ _
 * | o| (_
 */
public class IndiComputTest extends TestCase {

    @Autowired
    private IndiComputService indiComputService;

    @Test
    public void testInterfaceDataGather () {
        this.indiComputService.interfaceDataGather();
    }
}
