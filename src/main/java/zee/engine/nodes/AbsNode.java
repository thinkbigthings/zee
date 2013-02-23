package zee.engine.nodes;

import java.text.ParseException;
import zee.engine.domain.DomainInterface;

public class AbsNode extends MathNode {
   
   public AbsNode(Object id) {
      super(id);
   }
 
   @Override
   public double[] performCalculation(DomainInterface domain) {

      double[] v1 = getChild(0).evaluate(domain);
      for(int i=0; i < v1.length; i++) {
         v1[i] = Math.abs(v1[i]);
      }
      return v1;
   }

}
