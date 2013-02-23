
package zee.engine.nodes;


import zee.engine.domain.DomainInterface;

public class DividedByNode extends MathNode {
   
   public DividedByNode(Object id) {
      super(id);
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] v1 = getChild(0).evaluate(domain);
      double[] v2 = getChild(1).evaluate(domain);
      for(int i=0; i < v1.length; i++)
         v1[i] /= v2[i];
      return v1;
   }
   
}
