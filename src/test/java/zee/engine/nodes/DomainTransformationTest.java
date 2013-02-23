package zee.engine.nodes;

import java.text.ParseException;
import org.junit.Test;
import zee.engine.parser.EquationSet;
import zee.engine.parser.ExpressionParser;
import static org.junit.Assert.*;

public class DomainTransformationTest {

   /**
    * Test of addTransformation method, of class kernel.DomainTransformation.
    */
    @Test
   public void testAddTransformation() {


      try {
         EquationSet eqs = new EquationSet();
         eqs.addSymbol("f1(x,y)","-1/2 * ( x - y^2 )");

         ExpressionParser parser = new ExpressionParser(eqs);
         MathNode x = parser.parse("x");
         MathNode y = parser.parse("y");
         DomainTransformation f1xy = (DomainTransformation)parser.parse("f1(x,y)");
         DomainTransformation f1yx = (DomainTransformation)parser.parse("f1(y,x)");
         assertTrue(f1yx.getTransformation("x") == y);
         assertTrue(f1yx.getTransformation("y") == x);
         assertTrue(f1xy.getTransformation("x") == x);
         assertTrue(f1xy.getTransformation("y") == y);
      }
      catch(ParseException e) {}

   }

   /**
    * Test of transform method, of class kernel.DomainTransformation.
    */
    @Test
   public void testTransform() {

      try {
         EquationSet eqs = new EquationSet();
         eqs.addSymbol("f1(x,y)","-1/2 * ( x - y^2 )");

         ExpressionParser parser = new ExpressionParser(eqs);
         DomainTransformation f1sinxy = (DomainTransformation)parser.parse("f1(sin(x),y)");
         assertTrue(f1sinxy.getTransformation("x") == parser.parse("sin(x)"));
         assertTrue(f1sinxy.getTransformation("y") == parser.parse("y"));
      }
      catch(ParseException e) {}
   }
   
}
