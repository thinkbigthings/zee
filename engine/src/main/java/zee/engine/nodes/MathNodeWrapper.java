package zee.engine.nodes;

import zee.engine.domain.DomainInterface;

/**
 * This class does nothing but call evaluate on a child node.
 * Its purpose is to provide an additional parent for nodes
 * so that that the child can have caching triggered by having 
 * more than one parent. For example, if you ask for output
 * [x f(x) f(x)] and f(x) is not referenced by any other function
 * f(x) will not be cached even though here it is appropriate.
 * To resolve this, make each output node wrapped in this math node
 * That way multiple evaluations are more accurately represented
 * by the evaluation graph.
 *
 */
public class MathNodeWrapper extends MathNode {

   public MathNodeWrapper(Object id) { 
      super(id);
   }
      
   /**
    * Returns evaluate(domain) on the single child
    */
   @Override
   public double[] performCalculation(DomainInterface domain) {
      return getChild(0).evaluate(domain);
   }

}
