package zee.engine.nodes;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import zee.engine.parser.EquationSet;
import zee.engine.parser.ExpressionParser;
import zee.engine.parser.ParseException;

public class PiecewiseNodeTest {

   /**
    * Test of getParentFunctions method
    */
    @Test
   public void testGetParentFunctions() {

      try {
         EquationSet eqs = new EquationSet();
         eqs.addSymbol("f1(x)","f2(x)+1");
         eqs.addSymbol("f2(x)","if x<=0 then 0 if x ==0 then 0 else x");
         eqs.addSymbol("f3(x)","if x<=0 then 0 if x ==0 then 0 else x");
         ExpressionParser parser = new ExpressionParser(eqs);
         
         MathNode f1 = parser.parse("f1");
         MathNode f2 = parser.parse("f2");
         MathNode f3 = parser.parse("f3");
         
         PiecewiseNode p = (PiecewiseNode)parser.parse("f2").getChild(0);
         List<MathNode> v = p.getParentsOfType(DomainTransformation.class);
         
         assertTrue(v.contains(f2));
         assertTrue(v.contains(f3));
         assertTrue( ! v.contains(f1));
      }
      catch(ParseException pe) {
         assertTrue(false);
      }
   }

   
}
