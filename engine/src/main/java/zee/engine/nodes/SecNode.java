package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

public class SecNode extends MathNode {
   
   public SecNode(Object id) {
      super(id);
   }
 
   /**
    * @return secant function, sec(x) = 1/cos(x)
    */
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] v1 = getChild(0).evaluate(domain);
      for(int i=0; i < v1.length; i++)
         v1[i] = 1/Math.cos(v1[i]);
      return v1;
   }

   
}
