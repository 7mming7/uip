package junit.comput;

import com.sq.comput.domain.IndicatorTemp;
import com.sq.comput.service.IndiComputService;
import com.sq.comput.service.IndicatorTempService;
import com.sq.protocol.socket.SocketServer;
import com.sq.util.DateUtil;
import junit.base.TestCase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

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

    @Autowired
    private IndicatorTempService indicatorTempService;

    @Autowired
    private SocketServer socketServerComp;

    /*@Test
    public void testInterfaceDataGather () {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 26);
        cal.set(Calendar.HOUR, 02);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        System.out.println("uuuuuuuuuuuuu" + DateUtil.formatCalendar(cal, DateUtil.DATE_FORMAT_Y_M_D));
        this.indiComputService.interfaceDataGather(cal);
    }

    @Test
    public void testInterfaceReComput () {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 2);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE, 0);
        List<IndicatorTemp> indicatorTempList = new ArrayList<IndicatorTemp>();
        indicatorTempList.add(indicatorTempService.findOne(1l));
        this.indiComputService.reComputInterface(cal,cal,indicatorTempList);
    }*/

    /*@Test

    public void testReComput(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 20);
        List<IndicatorTemp> indicatorTempList = new ArrayList<IndicatorTemp>();
        Searchable searchable = Searchable.newSearchable().addSearchFilter("indicatorCode", MatchType.LIKE,"%PT%");
        indicatorTempList = indicatorTempService.findAll(searchable).getContent();
        this.indiComputService.reComputIndicator(cal,indicatorTempList);
    }*/

    /*@Test
    public void testCalculateDataGather () {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.MONTH, 1);
        System.out.println("uuuuuuuuuuuuu" + DateUtil.formatCalendar(cal, DateUtil.DATE_FORMAT_Y_M_D));
        this.indiComputService.calculateDataGater(cal);
    }

    @Test
    public void testInterfaceIndicatorDataGater(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 3);
        cal.set(Calendar.MONTH, 5);
        this.indiComputService.interfaceIndicatorDataGater(cal);
    }*/

    /*@Test
    public void batchSave(){
        for (int i=1;i<100;i++) {
            IndicatorInstance indicatorInstance = new IndicatorInstance();
            indicatorInstance.setDataSource(2);
            indicatorInstance.setDecimalNum(2);
            indicatorInstance.setFloatValue(23d);
            indicatorInstance.setCategoryId(201l);
            indicatorInstance.setCreateTime(Calendar.getInstance());
            indicatorInstance.setIndicatorTempId(275l);
            indicatorInstance.setStatDateNum(20150504);
            indicatorInstance.setValueType(1);
            indicatorInstance.setFetchCycle(1);
            indicatorInstance.setOperCalType(1);
            indicatorInstance.setIndicatorCode("asdasdsa");
            indicatorInstance.setIndicatorName("test");
            indicatorInstance.setUnit(5);
            this.indiComputService.save(indicatorInstance);
        }
    }*/

    /*@Test
    public void testSocketService() {
        SocketServer socketServerComp1 = new SocketServer();
        socketServerComp1.start();
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void buildMap(){
        TreeMap<Integer,List<IndicatorTemp>> integerListTreeMap = new TreeMap<>();
        List<IndicatorTemp> indicatorTempList = new ArrayList<IndicatorTemp>();
        indicatorTempList.add(indicatorTempService.findOne(526l));
        integerListTreeMap = indiComputService.buildIndiSortTreeMap(integerListTreeMap,indicatorTempList,1);
        int i = 1;
    }*/
}
