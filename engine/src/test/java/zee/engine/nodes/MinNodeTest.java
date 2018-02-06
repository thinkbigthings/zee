package zee.engine.nodes;

import org.junit.Test;

public class MinNodeTest {

   @Test(expected=RuntimeException.class)
   public void testExceptionOnNoChildren() throws Exception {
        MathNode node = new MinusNode("-");
        node.evaluate(null);
   }
   
}
