package junit.task;

import com.sq.jobschedule.service.SchedulerExecuteService;
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

    @Test
    public void syncItem () {
        schedulerExecuteService.syncOpcItem();
    }
}
