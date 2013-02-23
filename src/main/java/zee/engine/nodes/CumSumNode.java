
package zee.engine.nodes;


import zee.engine.domain.DomainInterface;

public class CumSumNode extends MathNode {

   private String[] args;

   public CumSumNode(Object id) {
      super(id);

   }
   
   @Override
   public boolean isSplittable() {
      return false;
   }
 
   /**
    * Performs the cumulative sum operation.
    * This only works if there is exactly one domain variable.
    * 
    * @param domain
    */
   @Override
   public double[] performCalculation(DomainInterface domain)
   {

      if(domain.entrySet().size() != 1) {
          String msg = "cumsum (" + id + ") currently requires a domain with exactly one variable";
         throw new RuntimeException(msg);
      }


      double[] v0 = getChild(0).evaluate(domain); 


     double curSum = 0;
     for(int i=0; i < v0.length; i++) {
        curSum += v0[i];
        v0[i] = curSum;
     }

     return v0;
   }

}
