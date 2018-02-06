
package zee.engine.nodes;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import zee.engine.domain.Domain;


public class CumSumNodeTest {

   @Test(expected=RuntimeException.class)
   public void testExceptionOnNoChildren() throws Exception {
        MathNode node = new CumSumNode("cumsum()");
        node.evaluate(null);
   }

   @Test(expected=RuntimeException.class)
   public void testExceptionOnMultiVariateDomain() throws Exception {
        MathNode node = new CumSumNode("cumsum(x,y)");
        
        Map<String,double[]> defs = new HashMap<String, double[]>();
        defs.put("x", new double[]{0,1});
        defs.put("y", new double[]{0,1});
        
        Domain domain = new Domain(defs);
        domain = domain.recombineVariable("x");
        domain = domain.recombineVariable("y");

        node.evaluate(domain);
   }
}
