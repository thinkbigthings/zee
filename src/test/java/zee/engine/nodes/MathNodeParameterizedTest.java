package zee.engine.nodes;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import zee.engine.parser.DomainParser;
import zee.engine.domain.DomainInterface;
import java.util.HashMap;
import static java.lang.Math.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import zee.engine.EquationProcessor;

import zee.engine.parser.EquationSet;
import zee.engine.parser.ExpressionParser;


@RunWith(Parameterized.class)
public class MathNodeParameterizedTest {
    
    public static final double EPSILON = 1.0E-9;

    private MathNode node;
    private HashMap<String,String> defs = new HashMap<String,String>();
    private String functionCall;

    private boolean expectedIsSplittable;
    private double[] expectedValues;

    // TODO is MaxNode.isSplittable() correct? really, you can't split along max(5)?

    @Parameterized.Parameters
    public static Collection getTestArgs() {
        return Arrays.asList(new Object[][] {

            { "--x", "[0,1,2]", true, new double[]{0,1,2}},
            { "5--5", "[0]", true, new double[]{10}},
            { "5+-5", "[0]", true, new double[]{0}},
            { "1 * -1", "[0]", true, new double[]{-1}},
            { "1-2-3", "[0]", true, new double[]{-4}},
            { "1-(2+3)", "[0]", true, new double[]{-4}},

            { "abs(x^3)", "[-2:2]", true, new double[]{8, 1, 0, 1, 8}},
            { "min(x^3)", "[-2:2]", false, new double[]{-8, -8, -8, -8, -8}},
            { "epsilonEquals(x^3,cos(toradians(90)))", "[-2:2]", true, new double[]{0, 0, 1, 0, 0}},
            { "not( x^3 < 0 )", "[-2:2]", true, new double[]{0, 0, 1, 1, 1}},

            { "max(x,5)", "[1:10]", true, new double[]{5,5,5,5,5,6,7,8,9,10}},
            { "max(5,x)", "[1:10]", true, new double[]{5,5,5,5,5,6,7,8,9,10}},
            { "max(5)", "[1:10]", false, getConstArray(5,10)},
            { "max(x)", "[1:10]", false, getConstArray(10,10)},
            { "max(x)", "[1:10000]", false, getConstArray(10000, 10000)},
            { "max(0.5, x, 0.6-x)", "[0:0.1:1]", true, new double[]{0.6, 0.5, 0.5, 0.5, 0.5, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0}},
            { "min(x,5)", "[1:10]", true, new double[]{1,2,3,4,5,5,5,5,5,5}},
            { "min(5,x)", "[1:10]", true, new double[]{1,2,3,4,5,5,5,5,5,5}},
            { "min(5)", "[1:10]", false, getConstArray(5,10)},
            { "min(x)", "[1:10]", false, getConstArray(1,10)},
            { "min(x)", "[1:10000]", false, getConstArray(1, 10000)},
            { "min(5, x, 10-x)", "[1:10]", true, new double[]{1,2,3,4,5,4,3,2,1,0}},
            { "cumsum(x)", "[1:5]", false, new double[]{1,3,6,10,15}},
            { "exp(x)", "[1:3]", true, new double[]{exp(1), exp(2), exp(3)}},
            { "e^x", "[1:3]", true, new double[]{exp(1), exp(2), exp(3)}},
            { "pi", "[1:3]", true, new double[]{3.141592653589,3.141592653589,3.141592653589}},
            { "cos(x)", "[0,3.141592653589]", true, new double[]{1,-1}},
            { "cosd(x)", "[0,180]", true, new double[]{1,-1}},
            { "acos(x)", "[1,-1]", true, new double[]{0,3.141592653589}},
            { "acosd(x)", "[1,-1]", true, new double[]{0,180}},
            { "sin(x)", "[0, 1.5707963267948966]", true, new double[]{0, 1}},
            { "sind(x)", "[0,90]", true, new double[]{0, 1}},
            { "asin(x)", "[0, 1]", true, new double[]{0, 1.5707963267948966}},
            { "asind(x)", "[0,1]", true, new double[]{0,90}},
            { "tan(x)", "[0,0.785398163]", true, new double[]{0, 1}},
            { "atan(x)", "[0,1]", true, new double[]{0, 0.785398163}},
            { "cot(x)", "[0,1.5707963267948966]", true, new double[]{Double.POSITIVE_INFINITY, 0}},
            { "sec(x)", "[0,3.141592653589]", true, new double[]{1, -1}},
            { "csc(x)", "[1.5707963267948966,1]", true, new double[]{1, 1.1883951057781 }},
            { "todegrees(x,3)", "[0,1.5707963267948966,3.141592653589]", true, new double[]{0,90,180}},
            { "mod(x,3)", "[1:10]", true, new double[]{1,2,0,1,2,0,1,2,0,1}},

            { "ln(x)",   "[0,1,10]", true, new double[]{Double.NEGATIVE_INFINITY, 0, 2.302585092994046}},
            { "log2(x)", "[0,1,2,4]", true, new double[]{Double.NEGATIVE_INFINITY,0,1,2}},

            { "cosh(x)", "[0,1.5707963267948966]", true, new double[]{1, 2.50917847866}},
            { "sinh(x)", "[0,1.5707963267948966]", true, new double[]{0, 2.30129890231}},
            { "tanh(x)", "[0,1.5707963267948966]", true, new double[]{0, 0.917152335667}},

            { "ceil(x)", "[0.1, 1.2, 2.3]", true, new double[]{1,2,3}},
            { "floor(x)","[0.1, 1.2, 2.3]", true, new double[]{0,1,2}},
            { "round(x)","[0.1, 1.2, 2.6]", true, new double[]{0,1,3}},

            { "if x < 0 then -x if x >= 0 then x", "[-1,0,1]", true, new double[]{1, 0, 1}},
            { "if x < 0 then -x else x", "[-1,0,1]", true, new double[]{1, 0, 1}},
            { "if x < 0 & x >  -2 then -x else x", "[-2,0,1]", true, new double[]{-2, 0, 1}},
            { "if x < 0 & x >= -2 then -x else x", "[-2,0,1]", true, new double[]{2, 0, 1}},
            { "if x < -1 | x >  1 then abs(x) else 0", "[-1,-0.5,0,0.5,1]", true, new double[]{0,0,0,0,0}},
            { "if x <= -1 | x >=  1 then abs(x) else 0", "[-1,-0.5,0,0.5,1]", true, new double[]{1,0,0,0,1}},
            { "if 1 < x then x+1, if x==5 then x", "[0,2,5]", true, new double[]{Double.NaN, 3, Double.NaN}}, // f(0) underdefined, f(5) overdefined
            { "if 1<=x&x<5 then x+1, if x==5 then x, else -1", "[1,5,6]", true, new double[]{2,5,-1}}, // f(6) piecewise out of range

            
            { "random(1)","[0.5]", true, new double[]{0.7308781907032909}}



        });
    }
    
    private static double[] getConstArray(double value, int length)
    {
        double[] array = new double[length];
        Arrays.fill(array, value);
        return array;
    }

    public MathNodeParameterizedTest(String functionCall, String domainX, boolean expectedIsSplittable, double[] expectedValues) throws ParseException
    {
       EquationSet eqs = new EquationSet();
       ExpressionParser parser = new ExpressionParser(eqs);
       node  = parser.parse(functionCall);

       defs.put("x", domainX);

       this.functionCall = functionCall;
       this.expectedIsSplittable = expectedIsSplittable;
       this.expectedValues = expectedValues;
    }

   @Test
   public void testIsSplittable() throws Exception
   {
       String message = "testing " + functionCall;
       Assert.assertEquals(message, expectedIsSplittable, node.isSplittable());
   }

   @Test
   public void testCalculation() throws Exception
   {
       DomainInterface domain = new DomainParser().getDomain(defs);
       domain = domain.recombineVariable("x");
       double[] actuals = node.performCalculation(domain);
       
       String message = "testing " + functionCall;
       Assert.assertArrayEquals(message, expectedValues, actuals, EPSILON);
   }

   @Test
   public void testCalculationWithEquationProcessor() throws ParseException
   {
       // pass "x" so that node is evaluated over whole domain
       EquationProcessor processor = new EquationProcessor(new HashMap<String,String>());
       double[] actuals = processor.evaluate(defs, Arrays.asList(functionCall, "x"), -1, true).get(0);

       String message = "testing " + functionCall;
       Assert.assertArrayEquals(message, expectedValues, actuals, EPSILON);
   }


}
