
package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

public class EpsilonEqualsNode extends MathNode {
   
   public final static double EPSILON = 1E-15;

   public EpsilonEqualsNode(Object id) {
      super(id);
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {

      double[] v1 = getChild(0).evaluate(domain);
      double[] v2 = getChild(1).evaluate(domain);
      
      for(int i=0; i < v1.length; i++) {
         if(Math.abs(v1[i]-v2[i]) <= EPSILON)
            v1[i] = 1;
         else
            v1[i] = 0;
      }

      return v1;
   }
   
}
