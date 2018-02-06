package zee.engine.nodes;

import java.util.Arrays;
import zee.engine.domain.DomainInterface;

public class NumberNode extends MathNode {
   
   private double value = 0;
   
   public NumberNode(Object id) {
      super(id);
      value = Double.parseDouble(id.toString());
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] d = new double[domain.getLength()];
      Arrays.fill(d,value);
      return d;
   }
   

   
}
