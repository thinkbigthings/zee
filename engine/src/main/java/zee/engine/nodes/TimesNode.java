
package zee.engine.nodes;

import java.util.Arrays;
import zee.engine.domain.DomainInterface;

public class TimesNode extends MathNode {

   public TimesNode(Object id) {
      super(id);
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {
      
      // for things that are multiplied by zero
      // this optimization can help out a lot if one child is zero
      // and the other child is time-intensive
      // in one production evaluation, this reduced evaluation time by 70%
      NumberNode number = null;
      if(getChild(0) instanceof NumberNode)
         number = (NumberNode)getChild(0);
      else if(getChild(1) instanceof NumberNode)
         number = (NumberNode)getChild(1);
      if(number != null && Double.parseDouble(number.getID().toString()) == 0) {
         double[] product = new double[domain.getLength()];
         Arrays.fill(product, 0);
         return product;
      }
      
      double[] v1 = getChild(0).evaluate(domain);
      double[] v2 = getChild(1).evaluate(domain);
      for(int i=0; i < v1.length; i++)
         v1[i] *= v2[i];
      return v1;
   }
   
}
