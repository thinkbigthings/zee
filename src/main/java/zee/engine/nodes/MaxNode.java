
package zee.engine.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import zee.engine.domain.DomainInterface;

public class MaxNode extends MathNode {
   
   public MaxNode(Object id) {
      super(id);
   }
   
   @Override
   public boolean isSplittable() {
      return getChildCount() > 1;
   }
 
   /**
    * See JDK Javadocs for how Math.max handles NaN and infinity.
    * That's relevent if you have more than one argument to this max node.
    */
   @Override
   public double[] performCalculation(DomainInterface domain) {
      
      double[] v1 = null;
      
      if(getChildCount() == 0) {
         String msg = "max function (" + id + ") doesn't have any arguments";
         throw new RuntimeException(msg);
      }
      else if(getChildCount() == 1) {
         v1 = getChild(0).evaluate(domain);
         double curMin = v1[0];
         for(int i=1; i < v1.length; i++) {
            curMin = Math.max(v1[i], curMin);
         }
         Arrays.fill(v1,curMin);
      }
      else {
         v1 = getChild(0).evaluate(domain);
         List<double[]> args = new ArrayList<>();
         for(int i=0; i<getChildCount(); i++)
            args.add(getChild(i).evaluate(domain));
         
         for(int i=0; i < v1.length; i++) {
            double current = Double.NEGATIVE_INFINITY;
            for(double[] arg : args) {
               current = Math.max(current, arg[i]);
            }
            v1[i] = current;
         }
      }
       
      return v1;
   }
   
}
