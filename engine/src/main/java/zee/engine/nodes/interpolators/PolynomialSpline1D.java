
package zee.engine.nodes.interpolators;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

public class PolynomialSpline1D implements UnivariateFunction, VectorFunction {

    UnivariateFunction spline;
    UnivariateInterpolator interpolator;

    public PolynomialSpline1D(double x[], double y[], Interpolation.TYPE type) throws IllegalArgumentException {

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
    public double value(double v) {
        return spline.value(v);
    }

    @Override
    public double[] value(double[] v)  {
        double[] result = new double[v.length];
        for(int i=0; i < v.length; i++) {
            result[i] = spline.value(v[i]);
//            catch(FunctionEvaluationException e) {result[i] = Double.NaN; }
        }
        return result;

    }

}
