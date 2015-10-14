package com.sq.quota.function.math;

import com.sq.quota.function.logical.DateTimeFunction;
import net.sourceforge.jeval.Evaluator;
import net.sourceforge.jeval.function.Function;
import net.sourceforge.jeval.function.FunctionGroup;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: shuiqing
 * Date: 2015/8/25
 * Time: 9:44
 * Email: shuiqing301@gmail.com
 * GitHub: https://github.com/shuiqing301
 * Blog: http://shuiqing301.github.io/
 * _
 * |_)._ _
 * | o| (_
 */
@Component
public class MathFunctions implements FunctionGroup {
    /**
     * Used to store instances of all of the functions loaded by this class.
     */
    private List functions = new ArrayList();

    /**
     * Default contructor for this class. The functions loaded by this class are
     * instantiated in this constructor.
     */
    public MathFunctions() {
        functions.add(new AvgFunction());
        functions.add(new DivideFunction());
        functions.add(new MaxAllFunction());
        functions.add(new MinAllFunction());
        functions.add(new MultiplyFunction());
        functions.add(new PstFunction());
        functions.add(new SubFunction());
        functions.add(new SumFunction());
    }

    /**
     * Returns the name of the function group - "stringFunctions".
     *
     * @return The name of this function group class.
     */
    public String getName() {
        return "mathFunctions";
    }

    /**
     * Returns a list of the functions that are loaded by this class.
     *
     * @return A list of the functions loaded by this class.
     */
    public List getFunctions() {
        return functions;
    }

    /**
     * Loads the functions in this function group into an instance of Evaluator.
     *
     * @param evaluator
     *            An instance of Evaluator to load the functions into.
     */
    public void load(final Evaluator evaluator) {
        Iterator functionIterator = functions.iterator();

        while (functionIterator.hasNext()) {
            evaluator.putFunction((Function) functionIterator.next());
        }
    }

    /**
     * Unloads the functions in this function group from an instance of
     * Evaluator.
     *
     * @param evaluator
     *            An instance of Evaluator to unload the functions from.
     */
    public void unload(final Evaluator evaluator) {
        Iterator functionIterator = functions.iterator();

        while (functionIterator.hasNext()) {
            evaluator.removeFunction(((Function) functionIterator.next())
                    .getName());
        }
    }
}
