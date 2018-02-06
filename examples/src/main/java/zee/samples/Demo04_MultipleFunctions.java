package zee.samples;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zee.engine.EquationProcessor;

public class Demo04_MultipleFunctions {
   
   public static void main(String[] args) throws ParseException {
      
      // define the function signatures and function definitions
      Map<String,String> eqs = new HashMap<String,String>();
      eqs.put("f(x)","x^2");
      eqs.put("g(x)","3 * x^2 + 2 * x + 1");
      eqs.put("h(x)","sin(x)");

      // define the domain on which to evaluate the functions
      // here x takes on the integer values from 1 to 5
      Map<String,String> domainDefinition = new HashMap<String,String>();
      domainDefinition.put("x","[1 2 3 4 5]");
      

      // define what we want to evaluate
      // putting "f" here is the same as putting "f(x)"
      // there are 4 items to evaluate
      // so we'll see a list of 4 double[]'s in the output
      List<String> outputColumns = Arrays.asList("x", "f", "g", "h");

      // instantiate your evaluator with your definitions
      // and do the evaluation
      EquationProcessor k = new EquationProcessor(eqs);
      List<double[]> results = k.evaluate(domainDefinition,outputColumns);

   }
   
}
