package junit.mongo;

import com.mongo.domain.MongoOriginalDataHistory;
import com.mongo.repository.MongoOrignalDataHistoryRespository;
import com.sq.util.DateUtil;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/6/30
 * Time: 11:24
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class MongoTest extends TestCase {

    @Autowired
    private MongoOrignalDataHistoryRespository mongo;

    @Test
    @Rollback(false)
    public void testInsert(){
        MongoOriginalDataHistory originalData = new MongoOriginalDataHistory();
        originalData.setSysId(1);
        originalData.setBatchNum(1243l);
        originalData.setInstanceTime(DateUtil.formatCalendar(Calendar.getInstance(), DateUtil.DATE_FORMAT_DAFAULTYMDHMS));
        originalData.setItemCode("sdgsg");
        originalData.setItemValue(32.23d);
        mongo.save(originalData);
    }
}
