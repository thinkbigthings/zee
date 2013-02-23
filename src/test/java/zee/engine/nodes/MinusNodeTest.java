package zee.engine.nodes;

import org.junit.Test;

public class MinusNodeTest {

   @Test(expected=RuntimeException.class)
   public void testExceptionOnNoChildren() throws Exception {
        MathNode node = new MinNode("min()");
        node.evaluate(null);
   }
   
}
