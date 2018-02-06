
package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

public class MinusNode extends MathNode {
   
   public MinusNode(Object id) {
      super(id);
   }
 
   /**
    * If has two children, returns child1 - child2.
    * If has one child, returns negation of it.
    */
   @Override
   public double[] performCalculation(DomainInterface domain) {

       if(getChildCount() == 0)
       {
         String msg = "minus function (" + id + ") doesn't have any arguments";
         throw new RuntimeException(msg);
       }

      double[] v1 = null;
      if( getChildCount() == 2) { // as binary operator
         v1 = getChild(0).evaluate(domain);
         double[] v2 = getChild(1).evaluate(domain);
         for(int i=0; i < v1.length; i++)
            v1[i] -= v2[i];
      }
      else { // unary operator
         v1 = getChild(0).evaluate(domain);
         for(int i=0; i < v1.length; i++)
            v1[i] =  - v1[i];
      }

      
      return v1;
   }
   
}
