package expression;

import com.sq.comput.component.ComputHelper;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

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
public class TestJeval {
    public static void main(String[] args) {
        Evaluator eva = ComputHelper.getEvaluatorInstance();

        try {
            System.out.println(eva.evaluate("sum(10.0,sum(1,2,sum(2,3)))"));

            eva.putVariable("a","23");
            eva.putVariable("b","5");
            System.out.println(eva.evaluate("sum(10.0,sum(#{a},2,sum(#{b},3)))"));
            System.out.println(eva.evaluate("div(mul(#{a},#{b}),100)"));

        } catch (EvaluationException e) {
            e.printStackTrace();
        }
    }

}
