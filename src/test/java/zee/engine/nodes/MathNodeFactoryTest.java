package zee.engine.nodes;

import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.*;

public class MathNodeFactoryTest {
   
   /**
    * Test of createNode method, of class kernel.MathNodeFactory.
    */
    @Test
   public void testCreateNode() {

      MathNode result;
      MathNodeFactory factory = new MathNodeFactory();
      
      boolean passed = true;
      try { 
         result = factory.createNode("shouldn't recognize this","5x");
      }
      catch(ParseException e) {
         passed = false;
      }
      assertTrue( ! passed);
      
      passed = true;
      try {
         result = factory.createNode("sin(x)","sin");
         assertTrue(result instanceof SinNode);

         result = factory.createNode("sin(x)","SIN");
         assertTrue(result instanceof SinNode);

         result = factory.createNode("cos(x)","cos");
         assertTrue(result instanceof CosNode);
      }
      catch(ParseException e) {
         passed = false;
      }
      assertTrue( passed );
   }
   
}
