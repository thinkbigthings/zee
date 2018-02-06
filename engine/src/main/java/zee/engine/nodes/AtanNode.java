
package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

public class AtanNode extends MathNode {
   
   public AtanNode(Object id) {
      super(id);
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] v1 = getChild(0).evaluate(domain);
      double[] result = new double[v1.length];
      for(int i=0; i < v1.length; i++)
         result[i] = Math.atan(v1[i]);
      return result;
   }

   
}
