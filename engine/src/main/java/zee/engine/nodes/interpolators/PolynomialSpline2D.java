package zee.engine.nodes.interpolators;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.apache.commons.math3.analysis.interpolation.BivariateGridInterpolator;
import org.apache.commons.math3.analysis.interpolation.PiecewiseBicubicSplineInterpolator;

/**
 *
 */
public class PolynomialSpline2D implements BivariateFunction, TwoVectorFunction {
   
    BivariateFunction spline;
    BivariateGridInterpolator interpolator;

   /** 
    * Creates a new instance
    * Given that f( x1[i], x2[j] ) = z[i][j]
    */
   public PolynomialSpline2D(double[] x1, double[] x2, double[][] z, Interpolation.TYPE type)
   {
        switch( type )
        {
            case LINEAR: 
                // FIXME linear 2D interpolation was removed in Apache Commons Math 3.X
                throw new UnsupportedOperationException("Linear 2D interpolations are not supported.");
            default: // case CUBIC: is default
                interpolator = new PiecewiseBicubicSplineInterpolator();
                break;
        }

        spline = interpolator.interpolate(x1, x2, z) ;
    }

   @Override
   public double value(double x1, double x2) {
      return spline.value(x1, x2);
   }

    @Override
    public double[] value(double[] u, double[] v)  {
        double[] result = new double[v.length];
        for(int i=0; i < v.length; i++) {
//            try{ 
                result[i] = spline.value(u[i], v[i]); 
//            }
//            catch(FunctionEvaluationException e) {result[i] = Double.NaN; }
        }
        return result;
    }
   
}
