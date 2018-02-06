package zee.samples;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zee.engine.EquationProcessor;
import zee.engine.parser.MatrixParser;

public class Demo11_NumericFunctionInterpType {

    /**
     * This demonstrates the use of numerically-defined 1D functions.
     *
     * @param args
     * @throws ParseException
     */
   public static void main(String[] args) throws ParseException {

      Map<String,String> eqs = new HashMap<>();
      eqs.put("xSquaredLinearData(x)","[-3,9;-2,4;-1,1;0,0;1,1;2,4;3,9]");
      eqs.put("xSquaredCubicData(x)", "[-3,9;-2,4;-1,1;0,0;1,1;2,4;3,9]");

      Map<String,Map<String,String>> meta = new HashMap<>();
      Map<String,String> metaForLinearFunction = new HashMap<>();
      Map<String,String> metaForCubicFunction = new HashMap<>();
      metaForCubicFunction.put(MatrixParser.INTERPOLATION_TYPE, "Cubic");
      metaForLinearFunction.put(MatrixParser.INTERPOLATION_TYPE, "Linear");
      meta.put("xSquaredLinearData(x)", metaForLinearFunction);
      meta.put("xSquaredCubicData(x)", metaForCubicFunction);

      EquationProcessor k = new EquationProcessor(eqs,meta);

      double sqrtp = 0.1;
      double sqrt2 = Math.sqrt(2);
      double sqrt3 = Math.sqrt(3);
      Map<String,String> defs = new HashMap<>();
      defs.put("x","[" + sqrtp + ", " + sqrt2 + ", " + sqrt3 + "]");

      List<String> cols = Arrays.asList("x", "xSquaredLinearData(x)", "xSquaredCubicData", "x^2", "abs(xSquaredLinearData-x^2)", "abs(xSquaredCubicData-x^2)");
      List<double[]> output = k.evaluate(defs,cols);

      System.out.println(OutputFormatter.toString(output));

   }
   
}
