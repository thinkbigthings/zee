package zee.engine.nodes;

import org.junit.Test;
import zee.engine.domain.DomainInterface;
import zee.engine.parser.DomainParser;
import zee.engine.parser.EquationSet;
import zee.engine.parser.ExpressionParser;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TimesNodeTest {

   @Test
   public void testOptimization() throws Exception {

      // FIXME the zero optimization doesn't run when the second node is zero
      // (only works when first one is zero)
      // not sure how to test except with a timed test?
      // or add logging and mock that logging got called?

      String toParse = "2*0";
      double result = zee.engine.EquationProcessor.evaluateExpression(toParse);

      double expected = 0;
      assertEquals(expected, result, 1E-6);

   }

   @Test
   public void testEvaluateUncached() throws Exception {
      EquationSet eqs = new EquationSet();

      eqs.addSymbol("f(x)","x*0");
      eqs.addSymbol("g(x)","0*x");
      eqs.addSymbol("h(x)","x");
      ExpressionParser parser = new ExpressionParser(eqs);

      MathNode fx = parser.parse("f");
      MathNode gx = parser.parse("g");
      MathNode hx = parser.parse("h");

      Map<String,String> defs = new HashMap<String,String>();
      defs.put("x","[1:3]");
      DomainInterface d = new DomainParser().getDomain(defs);
      d = d.recombineVariable("x");


      double[] zeros = new double[]{0,0,0};
      double[] x     = new double[]{1,2,3};
      assertArrayEquals(zeros, fx.performCalculation(d), 1E-6);
      assertArrayEquals(zeros, gx.performCalculation(d), 1E-6);
      assertArrayEquals(x    , hx.performCalculation(d), 1E-6);
      
   }
   
}
