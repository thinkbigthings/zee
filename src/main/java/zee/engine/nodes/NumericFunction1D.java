
package zee.engine.nodes;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import zee.engine.domain.DomainInterface;
import zee.engine.nodes.interpolators.VectorFunction;
import zee.engine.nodes.interpolators.Interpolation;
import zee.engine.nodes.interpolators.PolynomialSpline1D;


public class NumericFunction1D extends MathNode {

   private VectorFunction f = null;
   private String indVar = "";
   private RealMatrix matrix = null;

   /**
    * Creates a new instance of NumericFunction1D
    */
   public NumericFunction1D(Object id, double[][] xy, Interpolation.TYPE type, String var) 
   {
      super(id);

      matrix = new Array2DRowRealMatrix(xy, true);

      indVar = var;

      double[] x = matrix.getColumn(0);
      double[] y = matrix.getColumn(1);

      f = new PolynomialSpline1D(x, y, type);
   }
   
    @Override
   public double[] performCalculation(DomainInterface d) {
      double[] x = d.get(indVar);
      return f.value(x);
   }
 
      
}