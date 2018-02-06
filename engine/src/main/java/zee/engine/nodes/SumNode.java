
package zee.engine.nodes;

import java.util.Arrays;
import zee.engine.domain.DomainInterface;

public class SumNode extends MathNode {
   
   public SumNode(Object id) {
      super(id);
   }
   
   @Override
   public boolean isSplittable() {
      return false;
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] v1 = getChild(0).evaluate(domain);
      double sum = 0;
      for(int i=0; i < v1.length; i++) {
         sum += v1[i];
      }
      Arrays.fill(v1,sum);
      return v1;
   }
   
}
