package zee.engine.nodes;

import java.util.HashMap;
import zee.engine.domain.DomainInterface;
import zee.engine.parser.DomainParser;
import zee.engine.parser.ExpressionParser;
import zee.engine.parser.EquationSet;
import org.junit.Test;
import static org.junit.Assert.*;

public class ENodeTest {

   @Test
   public void testEvaluateUncached() throws Exception {
      EquationSet eqs = new EquationSet();

      ExpressionParser parser = new ExpressionParser(eqs);

      MathNode e = parser.parse("e");

      HashMap<String,String> defs = new HashMap<String,String>();
      defs.put("x","1");
      DomainInterface d = new DomainParser().getDomain(defs);
      d = (DomainInterface)d.recombineVariable("x");

      assertTrue(e.performCalculation(d)[0]==Math.E);

   }
   
}
