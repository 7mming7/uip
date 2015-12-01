package expression;

import com.sq.quota.component.QuotaComputHelper;
import junit.base.TestCase;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.FunctionHelper;
import org.junit.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shuiqing
 * Date: 2015/5/19
 * Time: 11:21
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/ShuiQing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
public class TestJeval extends TestCase {

    /*@Test
    public void test () {
        Evaluator eva = new Evaluator();
        QuotaComputHelper.loadLocalFunctions(eva);
        try {
            String calculateExpression = "inst('AW7002/DPU1062.HW.AI010401.PV','201511301826')";
            System.out.println(calculateExpression);
            List<String> variableList = QuotaComputHelper.getVariableList(calculateExpression,eva);
            for (String var:variableList) {
                System.out.println(var);
            }
            System.out.println(eva.evaluate(calculateExpression));
        } catch (EvaluationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Evaluator eva = new Evaluator();
        QuotaComputHelper.loadLocalFunctions(eva);
        try {
            String calculateExpression = "inst(FC3002AI:FT024302A.RO01,2015111815)";
            System.out.println(calculateExpression);
            List<String> variableList = QuotaComputHelper.getVariableList(calculateExpression,eva);
            for (String var:variableList) {
                System.out.println(var);
            }
            System.out.println(eva.evaluate(calculateExpression));

            eva.putVariable("a","23");
            eva.putVariable("b","5");
            System.out.println(eva.evaluate("sum(10.0,sum(#{a},2,sum(#{b},3)))"));
            System.out.println(eva.evaluate("div(mul(#{a},#{b}),100)"));
            System.out.println(eva.evaluate("div(0,8508.72)*100"));
            System.out.println(eva.evaluate("maxAll(maxAll(5.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,34.0,5.0,5.0,5.0,5.0,5.0,5.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,7.0,2.0,3.0,6.0,5.0,2.0,5.0,4.0,2.0,4.0,2.0,4.0,2.0,4.0,2.0,4.0,2.0,9.0,9.0,2.0,2.0,2.0,2.0,4.0,6.0,9.0,6.0,3.0,4.0,5.0,6.0,5.0,3.0,7.0,5.0,9.0,5.0,6.0,4.0,9.0,5.0,8.0,2.0,8.0,2.0,8.0,2.0,5.0,9.0,9.0,9.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,6.0,5.0,8.0,8.0,8.0,81.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,8.0,4.0,14.0,5.0,5.0,2.0,5.0,8.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,5.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,8.0,9.0,4.0,15.0,6.0,5.0,8.0,1.0,6.0,4.0,5.0,8.0,4.0,6.0,8.0,5.0,5.0,2.0,2.0,2.0,2.0,2.0,2.0,2.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0,10.0))"));

        } catch (EvaluationException e) {
            e.printStackTrace();
        }
    }*/

}
