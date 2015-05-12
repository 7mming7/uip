package junit.protocol;

import com.sq.protocol.opc.repository.OriginalDataRepository;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/4/21
 * Time: 11:09
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io
 * _
 * |_)._ _
 * | o| (_
 */
public class OriginalDataTest extends TestCase {

    @Autowired
    private OriginalDataRepository originalDataRepository;

    /*@Test
    public void listAnHourPreOriginalDataTest () {
        originalDataRepository.listAnHourPreOriginalData("Macsv5.Device3.Group3.B1PT1312A");
    }*/
}
