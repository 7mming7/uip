package junit.protocol;

import com.sq.protocol.opc.repository.MesuringPointRepository;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import com.sq.protocol.opc.service.MesuringPointService;
import com.sq.protocol.opc.service.OriginalDataService;
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

    @Autowired
    private MesuringPointRepository mesuringPointRepository;

    @Autowired
    private OriginalDataRepository originalDataRepository;

    /*@Test
    public void testDelete() {
        this.mesuringPointService.delete(13l);
    }*/

    /*@Test
    public void readBrunch () {
        this.mesuringPointService.fetchReadSyncItems(1);
    }
*/
   /* @Test
    public void fetchNextBatchNum () {
        Long nextBatchNum = originalDataRepository.gernateNextBatchNumber(1);
        System.out.println("****--->>>" + nextBatchNum);
    }*/
}
