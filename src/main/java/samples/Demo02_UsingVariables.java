package samples;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import zee.engine.EquationProcessor;

public class Demo02_UsingVariables {
   
   public static void main(String[] args) throws ParseException {
      
      // first define the variable
      Map<String,String> variables = new HashMap<String,String>();
      variables.put("a","2");

      // instantiate the kernel with your variable definition
      // and do the evaluation
      EquationProcessor k = new EquationProcessor(variables);
      double result = k.evaluate("2 + a");

      // writes "4"
      System.out.println(result);

   }
   
}
