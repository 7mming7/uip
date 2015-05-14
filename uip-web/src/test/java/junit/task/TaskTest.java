package junit.task;

import com.sq.jobschedule.service.SchedulerExecuteService;
import com.sq.protocol.opc.repository.OriginalDataRepository;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/4/7
 * Time: 10:37
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class TaskTest extends TestCase{

    @Autowired
    private SchedulerExecuteService schedulerExecuteService;

    @Autowired
    private OriginalDataRepository originalDataRepository;

    /*@Test
    public void syncItem () {
        schedulerExecuteService.syncOpcItem();
    }

    @Test
    public void testProcedure () {
        originalDataRepository.dcsDataMigration("20150410");
    }

    @Test
    public void testDataMigration () {
        schedulerExecuteService.execDcsDataMigration();
    }*/

    @Test
    public void testExecLoadometerData(){
        schedulerExecuteService.execLoadometerData();
    }
}