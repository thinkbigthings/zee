package zee.engine.nodes;

import java.util.Arrays;
import zee.engine.domain.DomainInterface;

/**
 *
 *
 */
public class PiNode extends MathNode {
   
   private double value = 0;

   public PiNode(Object id) {
      super(id);
      value = Math.PI;
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {
      double[] d = new double[domain.getLength()];
      Arrays.fill(d,value);
      return d;
   }
   

   
}
