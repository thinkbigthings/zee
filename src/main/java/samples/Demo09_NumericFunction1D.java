package samples;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zee.engine.EquationProcessor;

public class Demo09_NumericFunction1D {

    /**
     * This demonstrates the use of numerically-defined 1D functions.
     *
     * @param args
     * @throws ParseException
     */
   public static void main(String[] args) throws ParseException {

      Map<String,String> eqs = new HashMap<String,String>();
      eqs.put("xSquaredData(x)","[-3,9;-2,4;-1,1;0,0;1,1;2,4;3,9]");
      EquationProcessor k = new EquationProcessor(eqs);

      Map<String,String> defs = new HashMap<String,String>();
      defs.put("x","[" + 0.1 + ", " + Math.sqrt(2) + ", " + Math.sqrt(3) + "]");

      List<String> cols = Arrays.asList("x", "xSquaredData(x)", "x^2", "abs(xSquaredData-x^2)");
      List<double[]> output = k.evaluate(defs,cols);

      System.out.println(OutputFormatter.toString(output));
      

   }
   
}
