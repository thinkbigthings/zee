
package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

public class Log2Node extends MathNode {
   
   public Log2Node(Object id) {
      super(id);
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] v1 = getChild(0).evaluate(domain);
      for(int i=0; i < v1.length; i++)
         v1[i] = Math.log(v1[i])/Math.log(2.0);
      return v1;
   }

   
}
