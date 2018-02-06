
package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

public class NotNode extends MathNode {
   
   public NotNode(Object id) {
      super(id);
   }
 
   /**
    * if value is zero, sets to one. if value is nonzero, sets to zero.
    */
   @Override
   public double[] performCalculation(DomainInterface domain) {

      double[] v1 = getChild(0).evaluate(domain);
      for(int i=0; i < v1.length; i++) {
         if(v1[i] == 0)
            v1[i] = 1;
         else
            v1[i] = 0;
      }
      return v1;
   }
   
}
