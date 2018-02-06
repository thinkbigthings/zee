package zee.samples;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zee.engine.EquationProcessor;

public class Demo03_SingleFunctions {
   
   public static void main(String[] args) throws ParseException {
      
      // define the function signature and function definition
      // here we're using one function: f(x)=x^2
      Map<String,String> eqs = new HashMap<String,String>();
      eqs.put("f(x)","x^2");

      // define the domain on which to evaluate f
      // here x takes on the 5 integer values from 1 to 5
      Map<String,String> domainDefinition = new HashMap<String,String>();
      domainDefinition.put("x","[1 2 3 4 5]");

      // define what we want to evaluate
      // there are two Strings to be evaluated
      // so we'll see a list of two double[]'s in the output
      List<String> outputColumns = Arrays.asList("x", "f");

      // instantiate your evaluator with your definitions
      // and do the evaluation
      EquationProcessor k = new EquationProcessor(eqs);
      List<double[]> results = k.evaluate(domainDefinition,outputColumns);

      // puts out two columns of x and x^2
      System.out.println(OutputFormatter.toString(results));

   }
   
}
