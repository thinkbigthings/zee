package zee.samples;

import static java.lang.Math.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import zee.engine.EquationProcessor;
import zee.engine.parser.MatrixParser;

public class Demo10_NumericFunction2D {

    /**
     * This demonstrates the use of numerically-defined 2D functions.
     *
     * @param args
     * @throws ParseException
     */
   public static void main(String[] args) throws ParseException {

      double[] x = new double[] { 0, 1, 2, 3, 4, 5 };
      double[] y = new double[] { 0, 1, 2, 3, 4, 5 };
      Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(x.length, y.length);
      matrix.setRow(0, x);
      matrix.setColumn(0, y);
      for(int i=1; i < x.length; i++) {
         for(int j=1; j< y.length; j++) {
             matrix.setEntry(i, j, pow(x[i],2) + cos(y[j])); // f(x,y) = x^2 + cos(y)
         }
      }
      String def = MatrixParser.toString(matrix);
      Map<String,String> eqs = new HashMap<String,String>();
      eqs.put("xSquaredPlusCosY(x,y)", def);
      EquationProcessor k = new EquationProcessor(eqs);

      Map<String,String> defs = new HashMap<String,String>();
      defs.put("x","[1:5]");
      defs.put("y","[1:5]");

      List<String> cols = Arrays.asList("x", "y", "xSquaredPlusCosY", "x^2+cos(y)", "abs(xSquaredPlusCosY-(x^2+cos(y)))");
      List<double[]> output = k.evaluate(defs,cols);

      System.out.println(OutputFormatter.toString(output));

   }
   
}
