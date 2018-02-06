package zee.samples;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zee.engine.EquationProcessor;

public class Demo06_FunctionComposition {
   
   public static void main(String[] args) throws ParseException {
      
      // define the function signatures and function definitions
      // in the h(x,y) definition, f is implied to be f(x)
      // g evaluated at y (instead of just y as in previous demo) is passed in to sin(g(y))
      // g evaluated at x is passed in as the argument to sin(g(x))
      // sin(g) would be the same as sin(g(y)) since g is defined as g(y)
      Map<String,String> eqs = new HashMap<String,String>();
      eqs.put("f(x)","sqrt(x)");
      eqs.put("g(y)","3 * y^2 + 2 * y + 1");
      eqs.put("h(x,y)","f - sin(g(y)) + sin(g(x))");

      // define the domain on which to evaluate the functions
      // here x and y both take on the integer values from 1 to 5
      Map<String,String> domainDefinition = new HashMap<String,String>();
      domainDefinition.put("x","[1:5]");
      domainDefinition.put("y","[1:5]");

      // define what we want to evaluate
      // putting "f" here is the same as putting "f(x)"
      // there are 5 items to evaluate
      // so we'll see a list of 5 double[]'s in the output
      // each double[] will be 25 elements long,
      // because the domain is automatically evaluated at every combination of x and y
      List<String> outputColumns = Arrays.asList("x", "y", "f", "g", "h");

      // instantiate your evaluation kernel with your definitions
      // and do the evaluation
      EquationProcessor k = new EquationProcessor(eqs);
      List<double[]> results = k.evaluate(domainDefinition,outputColumns);

      System.out.println(OutputFormatter.toString(results));

   }
   
}
