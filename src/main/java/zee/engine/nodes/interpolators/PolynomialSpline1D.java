
package zee.engine.nodes.interpolators;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math.analysis.interpolation.UnivariateRealInterpolator;

/**
 *
 */
public class PolynomialSpline1D implements UnivariateRealFunction, VectorFunction {

    UnivariateRealFunction spline;
    UnivariateRealInterpolator interpolator;

    public PolynomialSpline1D(double x[], double y[], Interpolation.TYPE type) throws IllegalArgumentException, MathException {

        switch( type )
        {
            case LINEAR:
                interpolator = new LinearInterpolator();
                break;

            default: // default is case CUBIC:
                interpolator = new SplineInterpolator();
                break;
        }

        spline = interpolator.interpolate(x, y) ;

    }
    
    @Override
    public double value(double v) throws FunctionEvaluationException {
        return spline.value(v);
    }

    @Override
    public double[] value(double[] v)  {
        double[] result = new double[v.length];
        for(int i=0; i < v.length; i++) {
            try{ result[i] = spline.value(v[i]); }
            catch(FunctionEvaluationException e) {result[i] = Double.NaN; }
        }
        return result;

    }

}
