package zee.engine.nodes;


import zee.engine.domain.DomainInterface;

public class CotNode extends MathNode {
   
   public CotNode(Object id) {
      super(id);
   }
 
   /**
    * @return cotangent function, cot(x) = cos(x)/sin(x)
    */
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] v1 = getChild(0).evaluate(domain);
      for(int i=0; i < v1.length; i++)
         v1[i] = Math.cos(v1[i])/Math.sin(v1[i]);
      return v1;
   }

   
}
