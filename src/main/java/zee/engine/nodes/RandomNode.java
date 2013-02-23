package zee.engine.nodes;

import java.util.Random;
import zee.engine.domain.DomainInterface;

public class RandomNode extends MathNode {
   
   public RandomNode(Object id) {
      super(id);
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] d = null;
      Random generator = new Random();
      if(getChildCount()==0) {
         d = new double[domain.getLength()];
         for(int i=0; i < d.length; i++)
            d[i] = generator.nextDouble();
      }
      else if(getChildCount() == 1) {
         d = getChild(0).evaluate(domain);
         generator.setSeed((long)d[0]);
         for(int i=0; i < d.length; i++)
            d[i] = generator.nextDouble();         
      }
      else {
         String error = "random function " + id + " has too many arguments";
         throw new RuntimeException(error);
      }
      return d;
   }

}
