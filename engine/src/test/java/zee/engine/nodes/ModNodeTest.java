package zee.engine.nodes;


import org.junit.Test;

public class ModNodeTest {

   @Test(expected=RuntimeException.class)
   public void testExceptionOnWrongArguments() throws Exception {
        MathNode node = new ModNode("mod(1)");
        node.evaluate(null);
   }
   
}
