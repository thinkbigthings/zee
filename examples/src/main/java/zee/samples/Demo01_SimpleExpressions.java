package zee.samples;

import java.text.ParseException;
import zee.engine.EquationProcessor;

/**
 *
 */
public class Demo01_SimpleExpressions {
   
   public static void main(String[] args) throws ParseException {
      
      String toParse = "log10(1000) / (1.4 + 1.6)";
      double result = EquationProcessor.evaluateExpression(toParse);

      // writes out "1.0"
      System.out.println(result);
   }
   
}
