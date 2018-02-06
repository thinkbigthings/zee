
package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

public class CosNode extends MathNode {
   
   public CosNode(Object id) {
      super(id);
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] v1 = getChild(0).evaluate(domain);
      for(int i=0; i < v1.length; i++)
         v1[i] = Math.cos(v1[i]);
      return v1;
   }

   
}
