package samples;

import zee.engine.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static org.junit.Assert.*;
import static java.lang.Math.*;

import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.junit.Before;
import zee.engine.parser.MatrixParser;



public class SamplesTest {

   final private double epsilon = 10E-10;
   private Map<String,String> eqs = new HashMap<>();
   private EquationProcessor k = null;
   private List<String> cols = new ArrayList<>();
   private Map<String,String> defs = new HashMap<>();

   @Before
   public void setupForEachMethod()
   {
        eqs = new HashMap<>();
        k = null;
        cols = new ArrayList<>();
        defs = new HashMap<>();
   }

   @Test
   public void simpleExpression1() throws ParseException
   {
      String toParse = "log10(1000) / (1.4 + 1.6)";
      double result;

      result = EquationProcessor.evaluateExpression(toParse);
      double expected = Math.log10(1000) / (1.4 + 1.6);
      assertEquals(expected, result, epsilon);

      result = EquationProcessor.evaluateExpression("5 - 10 + 5");
      Assert.assertEquals(0, result, epsilon);
   }

   @Test
   public void simpleExpression2() throws ParseException
   {
      String toParse = "5 - 10 + 5";
      double result;

      result = EquationProcessor.evaluateExpression(toParse);
      double expected = 0;
      assertEquals(expected, result, epsilon);
   }

   @Test
   public void usingVariables() throws ParseException
    {
      // first define the variable
      eqs.put("a","2");

      // instantiate the kernel with your variable definition
      // and do the evaluation
      k = new EquationProcessor(eqs);
      double result = k.evaluate("2 + a");
      double expected = 4;
      assertEquals(expected, result, epsilon);
    }

   @Test
   public void singleFunctions() throws ParseException
    {
      // define the function signature and function definition
      // here we're using one function: f(x)=x^2
      eqs.put("f(x)","x^2");

      // define the domain on which to evaluate f
      // here x takes on the 5 integer values from 1 to 5
      defs.put("x","[1 2 3 4 5]");

      // define what we want to evaluate
      // there are two Strings to be evaluated
      // so we'll see a list of two double[]'s in the output
      List<String> outputColumns = Arrays.asList("x", "f");

      // instantiate your evaluator with your definitions
      // and do the evaluation
      k = new EquationProcessor(eqs);
      List<double[]> results = k.evaluate(defs,outputColumns);

      // this is what you think "x" and "f" evaluate to
      double[] expectedX = new double[]{1,2,3,4,5};
      double[] expectedXSquared = new double[]{1,4,9,16,25};
      
      assertArrayEquals(expectedX, results.get(0), epsilon);
      assertArrayEquals(expectedXSquared, results.get(1), epsilon);

    }

    @Test
    public void multipleFunctions() throws ParseException
    {
      // define the function signatures and function definitions
      eqs.put("f(x)","x^2");
      eqs.put("g(x)","3 * x^2 + 2 * x + 1");
      eqs.put("h(x)","sin(x)");

      // define the domain on which to evaluate the functions
      // here x takes on the integer values from 1 to 5
      defs.put("x","[1 2 3 4 5]");

      // define what we want to evaluate
      // putting "f" here is the same as putting "f(x)"
      // there are 4 items to evaluate
      // so we'll see a list of 4 double[]'s in the output
      List<String> outputColumns = Arrays.asList("x", "f", "g", "h");

      // instantiate your evaluator with your definitions
      // and do the evaluation
      k = new EquationProcessor(eqs);
      List<double[]> results = k.evaluate(defs,outputColumns);

      // this is what you think "x" "g" "f" and "h" evaluate to
      double[] expectedX = new double[]{1,2,3,4,5};
      double[] expectedF = new double[]{1,4,9,16,25};
      double[] expectedG = new double[]{g(1), g(2), g(3), g(4), g(5)};
      double[] expectedH = new double[]{sin(1),sin(2), sin(3), sin(4), sin(5)};

      assertArrayEquals(expectedX, results.get(0), epsilon);
      assertArrayEquals(expectedF, results.get(1), epsilon);
      assertArrayEquals(expectedG, results.get(2), epsilon);
      assertArrayEquals(expectedH, results.get(3), epsilon);
    }
    
    /** goes with multipleFunctions test */
    private double g(double x) {
        return 3*Math.pow(x,2)+2*x+1;
    }

    @Test
    public void multiVariateDomain() throws ParseException
    {

      // define the function signature and function definition
      eqs.put("f(x)","sqrt(x)");
      eqs.put("g(y)","3 * y^2 + 2 * y + 1");
      eqs.put("h(x,y)","x-sin(y)");

      // define the domain on which to evaluate the functions
      // here x and y both take on the integer values from 1 to 3
      defs.put("x","[1:3]");
      defs.put("y","[1:3]");

      // define what we want to evaluate
      // putting "f" here is the same as putting "f(x)"
      // there are 5 items to evaluate
      // so we'll see a list of 5 double[]'s in the output
      // each double[] will be 9 elements long,
      // because the domain is automatically evaluated at every combination of x and y
      List<String> outputColumns = Arrays.asList("x", "y", "f", "g", "h");

      // instantiate your evaluation kernel with your definitions
      // and do the evaluation
      k = new EquationProcessor(eqs);
      List<double[]> results = k.evaluate(defs,outputColumns);

      double[] x = new double[]{1,2,3,1,2,3,1,2,3};
      double[] y = new double[]{1,1,1,2,2,2,3,3,3};

      double[] f = new double[]{1,sqrt(2),sqrt(3),1,sqrt(2),sqrt(3),1,sqrt(2),sqrt(3)};
      double[] g = new double[]{g(1),g(1),g(1),g(2),g(2),g(2),g(3),g(3),g(3)};
      double[] h = new double[]{1-sin(1),2-sin(1),3-sin(1),1-sin(2),2-sin(2),3-sin(2),1-sin(3),2-sin(3),3-sin(3)};

      assertArrayEquals(x, results.get(0), 10E-10);
      assertArrayEquals(y, results.get(1), 10E-10);
      assertArrayEquals(f, results.get(2), 10E-10);
      assertArrayEquals(g, results.get(3), 10E-10);
      assertArrayEquals(h, results.get(4), 10E-10);
    }

    @Test
    public void functionComposition() throws ParseException
    {

      // define the function signatures and function definitions
      // in the h(x,y) definition, f is implied to be f(x)
      // g evaluated at y (instead of just y as in previous demo) is passed in to sin(g(y))
      // g evaluated at x is passed in as the argument to sin(g(x))
      // sin(g) would be the same as sin(g(y)) since g is defined as g(y)
      eqs.put("f(x)","sqrt(x)");
      eqs.put("g(y)","3 * y^2 + 2 * y + 1");
      eqs.put("h(x,y)","f - sin(g(y)) + sin(g(x))");

      // define the domain on which to evaluate the functions
      // here x and y both take on the integer values from 1 to 5
      defs.put("x","[1:5]");
      defs.put("y","[1:5]");

      // define what we want to evaluate
      // putting "f" here is the same as putting "f(x)"
      // there are 5 items to evaluate
      // so we'll see a list of 5 double[]'s in the output
      // each double[] will be 25 elements long,
      // because the domain is automatically evaluated at every combination of x and y
      List<String> outputColumns = Arrays.asList("x", "y", "f", "g", "h");

      // instantiate your evaluation kernel with your definitions
      // and do the evaluation
      k = new EquationProcessor(eqs);
      List<double[]> results = k.evaluate(defs, outputColumns);

      assertEquals(5, results.size());
      assertEquals(25, results.get(0).length);

      assertEquals("x", 3.0, results.get(0)[2], epsilon);
      assertEquals("y", 1.0, results.get(1)[4], epsilon);
      assertEquals("f", 1.7320508075688772, results.get(2)[7], epsilon);
      assertEquals("g", 6.0, results.get(3)[4], epsilon);
      assertEquals("h", 2.540548991887827, results.get(4)[2], epsilon);

    }

    @Test
    public void summation() throws ParseException
    {
      eqs.put("piApprox(k)","8*cumsum(ChebyshevTerm(k))");
      eqs.put("ChebyshevTerm(k)","( (-1)^k * (sqrt(2)-1)^(2*k+1)) / (2*k+1)");
      eqs.put("absError(k)", "abs(pi-piApprox)");

      k = new EquationProcessor(eqs);

      defs.put("k","[0:20]");

      cols = Arrays.asList("k", "ChebyshevTerm", "piApprox", "absError");
      List<double[]> output = k.evaluate(defs, cols);

      assertEquals(Math.PI, output.get(2)[20], epsilon);
      assertEquals(0, output.get(3)[20], epsilon);
    }

    @Test
    public void piecewiseFunctions() throws ParseException
    {
      eqs.put("myAbs(x)","if x < 0 then -x, if x == 0 then 0, else x");
      eqs.put("diff(x)","myAbs - abs(x)");
      k = new EquationProcessor(eqs);

      defs.put("x","[-10:10]");

      cols = Arrays.asList("x", "myAbs(x)", "diff");
      List<double[]> output = k.evaluate(defs,cols);

      double[] actuals = new double[21];
      Arrays.fill(actuals, 0);
      assertArrayEquals(actuals, output.get(2), epsilon);
    }

    @Test
    public void numericFunction1D() throws ParseException
    {
      eqs.put("xSquaredData(x)","[-3,9;-2,4;-1,1;0,0;1,1;2,4;3,9]");
      k = new EquationProcessor(eqs);

      defs.put("x","[" + 0.1 + ", " + Math.sqrt(2) + ", " + Math.sqrt(3) + "]");

      cols = Arrays.asList("x", "xSquaredData(x)", "x^2", "abs(xSquaredData-x^2)");
      List<double[]> output = k.evaluate(defs,cols);

      double[] actuals = new double[3];
      Arrays.fill(actuals, 0);
      assertArrayEquals(actuals, output.get(3), 10E-2);
    }

    @Test
    public void numericFunction2D() throws ParseException
    {
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

      eqs.put("xSquaredPlusCosY(x,y)", MatrixParser.toString(matrix));
      k = new EquationProcessor(eqs);

      defs.put("x","[1:5]");
      defs.put("y","[1:5]");

      cols = Arrays.asList("x", "y", "xSquaredPlusCosY", "x^2+cos(y)", "abs(xSquaredPlusCosY-(x^2+cos(y)))");
      List<double[]> output = k.evaluate(defs,cols);

      double[] expected = new double[25];
      Arrays.fill(expected, 0);
      assertArrayEquals(expected, output.get(4), epsilon);
    }

    @Test
    public void numericFunctionInterpType1D() throws ParseException
    {
      eqs.put("xSquaredLinearData(x)","[-3,9;-2,4;-1,1;0,0;1,1;2,4;3,9]");
      eqs.put("xSquaredCubicData(x)", "[-3,9;-2,4;-1,1;0,0;1,1;2,4;3,9]");

      Map<String, Map<String, String>> meta = new HashMap<>();
      Map<String,String> metaForLinearFunction = new HashMap<>();
      Map<String,String> metaForCubicFunction = new HashMap<>();
      metaForCubicFunction.put(MatrixParser.INTERPOLATION_TYPE, "Cubic");
      metaForLinearFunction.put(MatrixParser.INTERPOLATION_TYPE, "Linear");
      meta.put("xSquaredLinearData(x)", metaForLinearFunction);
      meta.put("xSquaredCubicData(x)", metaForCubicFunction);

      defs.put("x","[" + sqrt(2) + ", " + sqrt(3) + ", " + sqrt(4) + "]");
      cols = Arrays.asList("x", "xSquaredLinearData(x)", "xSquaredCubicData", "x^2", "abs(xSquaredLinearData-x^2)", "abs(xSquaredCubicData-x^2)");

      k = new EquationProcessor(eqs,meta);
      List<double[]> output = k.evaluate(defs,cols);

      double linearTolerance = 0.25;
      double cubicTolerance  = 0.025;
      Assert.assertArrayEquals(output.get(3), output.get(1), linearTolerance);
      Assert.assertArrayEquals(output.get(3), output.get(2), cubicTolerance);

    }

    @Test
    public void numericFunctionInterpType2D() throws ParseException
    {
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

      Map<String, Map<String, String>> meta = new HashMap<>();
      Map<String,String> metaForLinearFunction = new HashMap<>();
      Map<String,String> metaForCubicFunction = new HashMap<>();
      metaForCubicFunction.put(MatrixParser.INTERPOLATION_TYPE, "Cubic");
      metaForLinearFunction.put(MatrixParser.INTERPOLATION_TYPE, "Linear");
      meta.put("xSquaredPlusCosYLinear(x,y)", metaForLinearFunction);
      meta.put("xSquaredPlusCosYCubic(x,y)", metaForCubicFunction);

      eqs.put("xSquaredPlusCosYLinear(x,y)", MatrixParser.toString(matrix));
      eqs.put("xSquaredPlusCosYCubic(x,y)", MatrixParser.toString(matrix));
      k = new EquationProcessor(eqs, meta);

      defs.put("x","[1:5]");
      defs.put("y","[1:5]");

      cols = Arrays.asList( "x",
                            "y",
                         //   "xSquaredPlusCosYLinear",
                            "xSquaredPlusCosYCubic",
                            "x^2+cos(y)", 
                        //    "abs(xSquaredPlusCosYLinear-(x^2+cos(y)))",
                            "abs(xSquaredPlusCosYCubic-(x^2+cos(y)))");

      List<double[]> output = k.evaluate(defs,cols);

      double[] expectedDiffs = new double[25];
      Arrays.fill(expectedDiffs, 0);
      
      // FIXME should be able to do linear 2D interpolations
      
      // assertArrayEquals(expectedDiffs, output.get(5), 3);
      assertArrayEquals(expectedDiffs, output.get(4), epsilon);

    }

}
