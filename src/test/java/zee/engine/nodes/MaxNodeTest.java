package zee.engine.nodes;

import org.junit.Test;

public class MaxNodeTest {

   @Test(expected=RuntimeException.class)
   public void testExceptionOnNoChildren() throws Exception {
        MathNode max = new MaxNode("max()");
        max.evaluate(null);
   }
   
}
