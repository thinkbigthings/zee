
package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

public class ModNode extends MathNode {
   
   private double value = 0;
   
   public ModNode(Object id) {
      super(id);
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {
      
      if(getChildCount() != 2) {
         String msg = "mod function (" + id + ") requires 2 arguments";
         throw new RuntimeException(msg);
      }
      
      double[] v1 = getChild(0).evaluate(domain);
      double[] v2 = getChild(1).evaluate(domain);
      for(int i=0; i < v1.length; i++) {
         v1[i] = v1[i] % v2[i];
      }

      return v1;
   }
   

   
}
