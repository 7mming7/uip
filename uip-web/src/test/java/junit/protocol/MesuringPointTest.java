package junit.protocol;

import com.sq.protocol.opc.repository.MesuringPointRepository;
import junit.base.TestCase;
import org.junit.Test;

import javax.annotation.Resource;

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

    @Resource
    private MesuringPointRepository mesuringPointRepository;

    @Test
    public void findAll () {
        this.mesuringPointRepository.findAll();
    }
}
