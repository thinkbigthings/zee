package zee.engine.nodes;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

import zee.engine.parser.EquationSet;
import zee.engine.parser.ExpressionParser;
import zee.engine.domain.DomainInterface;
import zee.engine.domain.DomainTest;
import zee.engine.parser.DomainParser;

public class TimesNodeTest {

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
      assertArrayEquals(zeros,fx.performCalculation(d),1E-6);
      assertArrayEquals(zeros,gx.performCalculation(d),1E-6);
      assertArrayEquals(x    ,hx.performCalculation(d),1E-6);
      
   }
   
}
