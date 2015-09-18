package junit.comput;

import com.sq.comput.domain.IndicatorConsts;
import com.sq.entity.search.MatchType;
import com.sq.entity.search.Searchable;
import com.sq.quota.domain.QuotaTemp;
import com.sq.quota.service.QuotaComputService;
import com.sq.quota.service.QuotaTempService;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/8/31
 * Time: 16:08
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class QuotaComputTest extends TestCase {

    @Autowired
    private QuotaComputService quotaComputService;

    @Autowired
    private QuotaTempService quotaTempService;

    /*@Test
    public void testReComput () {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 30);
        cal.set(Calendar.MONTH,7);
        List<QuotaTemp> quotaTempList = new ArrayList<QuotaTemp>();
        Searchable searchable = Searchable.newSearchable()
                .addSearchFilter("indicatorCode", MatchType.EQ,"AMDMMG007_D");
        quotaTempList = quotaTempService.findAll(searchable).getContent();
        this.quotaComputService.reComputQuota(cal, quotaTempList);
    }*/
}
