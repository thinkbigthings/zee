package samples;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zee.engine.EquationProcessor;

public class Demo07_Summation {

    /**
     * This demonstrates the use of summation, convergence, and predefined constants
     * using a "real" mathematic example.
     *
     * @param args
     * @throws ParseException
     */
   public static void main(String[] args) throws ParseException {

      Map<String,String> eqs = new HashMap<String,String>();
      eqs.put("piApprox(k)","8*cumsum(ChebyshevTerm(k))");
      eqs.put("ChebyshevTerm(k)","( (-1)^k * (sqrt(2)-1)^(2*k+1)) / (2*k+1)");
      eqs.put("absError(k)", "abs(pi-piApprox)");
      EquationProcessor k = new EquationProcessor(eqs);

      Map<String,String> defs = new HashMap<String,String>();
      defs.put("k","[0:20]");


      List<String> cols = Arrays.asList("k", "ChebyshevTerm", "piApprox", "absError");
      List<double[]> output = k.evaluate(defs,cols);

      System.out.println(OutputFormatter.toString(output));

   }
   
}
