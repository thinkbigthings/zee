package zee.engine.nodes;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Hashtable;
import zee.engine.parser.EquationSet;
import zee.engine.parser.ExpressionParser;
import zee.engine.domain.DomainInterface;
import zee.engine.parser.DomainParser;

public class RandomNodeTest {

   @Test(expected=RuntimeException.class)
   public void testExceptionOnTooManyChildren() throws Exception {
      EquationSet eqs = new EquationSet();
      ExpressionParser parser = new ExpressionParser(eqs);
      MathNode node = parser.parse("random(0,1)");
      node.evaluate(null);
   }

    @Test
   public void testEvaluateUncached() throws Exception {
      EquationSet eqs = new EquationSet();

      ExpressionParser parser = new ExpressionParser(eqs);

      MathNode rand    = parser.parse("rand");
      MathNode random  = parser.parse("random");
      MathNode rand0   = parser.parse("rand(0)");
      MathNode random0 = parser.parse("random(0)");
      MathNode rand1   = parser.parse("rand(1)");
      MathNode random1 = parser.parse("random(1)");

      Hashtable<String,String> defs = new Hashtable<String,String>();
      defs.put("x","1");
      DomainInterface d = new DomainParser().getDomain(defs);
      d = d.recombineVariable("x");

      rand.performCalculation(d);
      random.performCalculation(d);
      assertTrue(rand0.performCalculation(d)[0]==random0.performCalculation(d)[0]);
      assertTrue(rand1.performCalculation(d)[0]==random1.performCalculation(d)[0]);
      assertTrue(rand0.performCalculation(d)[0]!=random1.performCalculation(d)[0]);

   }
   
}
