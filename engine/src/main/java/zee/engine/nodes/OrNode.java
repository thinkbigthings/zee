
package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

public class OrNode extends MathNode {
   
   public OrNode(Object id) {
      super(id);
   }
 
   /**
    * returns 1 where lhs == 1 OR rhs == 1
    * returns 0 elsewhere
    */
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] v1 = getChild(0).evaluate(domain);
      double[] v2 = getChild(1).evaluate(domain);
      for(int i=0; i < v1.length; i++) {
         if(v1[i] == 1 || v2[i] == 1)
            v1[i] = 1;
         else
            v1[i] = 0;
      }
      return v1;
   }

}
