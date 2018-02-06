package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

public class VariableNode extends MathNode {
   
   private String name = null;
   
   /** Creates a new instance of MathNode */
   public VariableNode(Object id) {
      super(id);
      name = id.toString();
   }
 
   /**
    * Gets the double[] from the domain and returns it
    * You can modify the returned double[] without hurting the domain,
    * since Domains are immutable.
    * 
    */
    @Override
   public double[] performCalculation(DomainInterface domain) {
      return domain.get(name);
   }
   

}
