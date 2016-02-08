package zee.engine.nodes;

import java.util.ArrayList;
import java.util.List;
import zee.engine.domain.DomainInterface;

/**
 * A function has a signature, such as "f(x)". When you call "f(sin(x))"
 * you are saying to evaluate "sin(x)" and pass that into the function,
 * essentially you are transforming the inputs (domain) in the function f.
 *
 * This class represents the transformation of arguments to a function call.
 * It maps variable names (Strings) to nodes which can be
 * re-evaluated (MathNodes). When a Domain enters this node for evaluation, 
 * this node evaluates other nodes and sets the domain's variable to the results
 * of that evaluation. When all variables for the domain have been 
 * re-evaluated, the transformed domain can be passed on to the next node
 * in the evaluation chain.
 *
 * The first child of this DomainTransformation node is the head of the function definition. 
 * Each node in sequence after that corresponds to a transformation of a variable
 * which is defined in the signature.
 *
 */
public class DomainTransformation extends MathNode {
   
   private List<String> names = new ArrayList<String>();
   
   /** Creates a new instance of DomainTransformation */
   public DomainTransformation(Object id) {
      super(id);
   }

   public void addTransformation(String variable, MathNode n) {
      names.add(variable);
      this.addChild(n);
   }
   
   public MathNode getTransformation(String variable) {
      return  getChild( 1 + names.indexOf(variable) );
   }

   @Override
   public double[] performCalculation(DomainInterface domain) {
      return getChild(0).evaluate(transform(domain));
   }
   
   /**
    * If there is only one child, the Domain argument is returned.
    * If there are more child nodes, each name in this transformation
    * is recalculated with d and the results reassigned into a new Domain.
    */
   public DomainInterface transform(DomainInterface d) {
      
      DomainInterface n;
      
      if( getChildCount() == 1 ) {
         n = d;
      }
      else {
         n = d.getCopy();
         for(String name : names)
            n = n.setVariable(name, getTransformation(name).evaluate(d));
      }
      return n;
   }
   
}
