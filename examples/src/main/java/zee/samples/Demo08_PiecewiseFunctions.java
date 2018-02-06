package zee.samples;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zee.engine.EquationProcessor;

public class Demo08_PiecewiseFunctions {

    /**
     * This demonstrates the use of piecewise functions.
     * we can define our own absolute value function and compare it to
     * the predefined abs() function.
     *
     * @param args
     * @throws ParseException
     */
   public static void main(String[] args) throws ParseException {

      Map<String,String> eqs = new HashMap<String,String>();
      eqs.put("myAbs(x)","if x < 0 then -x, if x == 0 then 0, else x");
      eqs.put("diff(x)","myAbs - abs(x)");
      EquationProcessor k = new EquationProcessor(eqs);

      Map<String,String> defs = new HashMap<String,String>();
      defs.put("x","[-10:10]");

      List<String> cols = Arrays.asList("x", "myAbs(x)", "diff");
      List<double[]> output = k.evaluate(defs,cols);

      System.out.println(OutputFormatter.toString(output));
   }
}
