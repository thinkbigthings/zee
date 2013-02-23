package zee.engine.nodes;


import zee.engine.domain.DomainInterface;

public class CscNode extends MathNode {
   
   public CscNode(Object id) {
      super(id);
   }
 
   /**
    * @return cosecant function, csc(x) = 1/sin(x)
    */
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] v1 = getChild(0).evaluate(domain);
      for(int i=0; i < v1.length; i++)
         v1[i] = 1/Math.sin(v1[i]);
      return v1;
   }

   
}
