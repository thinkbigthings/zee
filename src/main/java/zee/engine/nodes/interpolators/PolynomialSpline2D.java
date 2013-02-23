package zee.engine.nodes.interpolators;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.BivariateRealFunction;
import org.apache.commons.math.analysis.interpolation.BicubicSplineInterpolator;
import org.apache.commons.math.analysis.interpolation.BivariateRealGridInterpolator;
import org.apache.commons.math.analysis.interpolation.SmoothingPolynomialBicubicSplineInterpolator;

/**
 *
 */
public class PolynomialSpline2D implements BivariateRealFunction, TwoVectorFunction {
   
    BivariateRealFunction spline;
    BivariateRealGridInterpolator interpolator;

   /** 
    * Creates a new instance of PolynomialSplineFunction2D 
    * Given that f( x1[i], x2[j] ) = z[i][j]
    */
   public PolynomialSpline2D(double[] x1, double[] x2, double[][] z, Interpolation.TYPE type) throws MathException
   {
        switch( type )
        {
            case LINEAR:
                interpolator = new SmoothingPolynomialBicubicSplineInterpolator(1);
                break;

            default: // case CUBIC: is default
                interpolator = new BicubicSplineInterpolator();
                break;
        }

        spline = interpolator.interpolate(x1, x2, z) ;
    }

   @Override
   public double value(double x1, double x2) throws FunctionEvaluationException  {
      return spline.value(x1, x2);
   }

    @Override
    public double[] value(double[] u, double[] v)  {
        double[] result = new double[v.length];
        for(int i=0; i < v.length; i++) {
            try{ result[i] = spline.value(u[i], v[i]); }
            catch(FunctionEvaluationException e) {result[i] = Double.NaN; }
        }
        return result;
    }
   
}
