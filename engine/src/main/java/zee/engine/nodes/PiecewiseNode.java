package zee.engine.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import zee.engine.domain.DomainInterface;

/**
 *
 * Piecewise functions are evaluated like so:
 * Each if node is evaluated in turn (order doesn't matter) and points 
 * which evaluated true are saved, and else node is evaluated last on any 
 * unevaluated points. It starts with an empty result vector of the same length 
 * as the domain, and fills it with NaN's. This result vector is passed by 
 * reference into each if node and else node.
 * Each if  node calls evaluate on the lhs to get a truth vector. 
 * Then at each true point in the truth vector the rhs is evaluated and stored 
 * into the result vector. The result vector is returned.
 * The piecewise function node stores the truth vectors of each if node. 
 * If any of the logical vectors are true at the same point, the piecewise 
 * function is over-defined at those points and sets that point to NaN and 
 * issues a warning. When all of the returned boolean vectors are or'ed 
 * together, the negation of that vector shows the points where to set the 
 * points from the else evaluation node. The else node contains a single 
 * math node which can be evaluated.
 *
 */
public class PiecewiseNode extends MathNode {
   
   public PiecewiseNode(Object id) {
      super(id);
   }
   
   /**
    * Assumes a double[] of 0.0's and 1.0's
    * returns true if any of them are 1.0
    * otherwise returns false
    */
   private boolean hasOne(double[] toOr) {
      boolean result = false;
      for(int i=0; i < toOr.length; i++) {
         if(toOr[i]==1.0) {
            result = true;
            break;
         }
      }
      return result;
   }
 
   /**
    * Assumes a double[] of 0.0's and 1.0's
    * returns true if any of them are 0.0
    * otherwise returns false
    */
   private boolean hasZero(double[] toAnd) {
      boolean result = false;
      for(int i=0; i < toAnd.length; i++) {
         if(toAnd[i]==0.0) {
            result = true;
            break;
         }
      }
      return result;
   }

   @Override
   public double[] performCalculation(DomainInterface domain) {
      
      double[] evaluation        = new double[domain.getLength()];
      List<double[]> conditions  = new ArrayList<double[]>();
      List<double[]> results     = new ArrayList<double[]>();
      boolean hasElse            = (getChildCount() % 2 != 0);
      double[] elseResults       = null;
      
      Arrays.fill(evaluation,Double.NaN);      

      // evaluate results only for statements where the condition is true
      for(int i=0; i < getChildCount()-1; i+=2) {
         double[] condition = getChild(i).evaluate(domain);
         boolean shouldEval = hasOne(condition);
         if(shouldEval) {
            conditions.add(condition);
            results.add(getChild(i+1).evaluate(domain));
         }
      }

      // evaluate conditions and results
      boolean overdefined = false;
      double[] evaluated = new double[evaluation.length];
      for(int i=0; i < conditions.size(); i++) {
         double[] condition = conditions.get(i);
         double[] result    = results.get(i);
         for(int c = 0; c < condition.length; c++) {
            // test for over-defined points
            if(condition[c] == 1 && evaluated[c] == 1) {
               overdefined = true;
               evaluation[c] = Double.NaN;
            }
            // if defined but not over-defined, set evaluation
            if(condition[c] ==1 && evaluated[c] != 1) {
               evaluation[c] = result[c];
               evaluated[c] = 1;
            }
         }
      }
      if(overdefined) {
         String message = "Piecewise function is over-defined. ";
         message = message.substring(0,message.length()-2);
         // TODO logger goes here
      }
      
      // if not everything was evaluated,
      // apply "else" node if possible
      // and throw warning otherwise
      if( hasZero(evaluated)) { // if unfinished
         if(hasElse) {
            elseResults = getChild(getChildCount()-1).evaluate(domain);
            for(int i=0; i < evaluated.length; i++) {
               if(evaluated[i] == 0)
                  evaluation[i] = elseResults[i];
            }
         }
         else {
            String message = "Piecewise function is under-defined. ";
            message = message.substring(0,message.length()-2);
            // TODO logger goes here
         }
      }
      
      return evaluation;
   }

}
