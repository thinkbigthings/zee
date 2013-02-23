package zee.engine.nodes;

import java.util.Arrays;
import org.apache.commons.math.MathException;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import zee.engine.domain.DomainInterface;
import zee.engine.nodes.interpolators.Interpolation;
import zee.engine.nodes.interpolators.PolynomialSpline2D;
import zee.engine.nodes.interpolators.TwoVectorFunction;


/**
 * 
 */
public class NumericFunction2D extends MathNode {

   private TwoVectorFunction f = null;
   private String indVar1 = "";
   private String indVar2 = "";
   private RealMatrix matrix = null;

   /**
    * Creates a new instance of NumericFunction
    *
    * @param id identifier for this instance (usually a String)
    *
    * @param data defines a 2D function on a grid.
    * The first row/column of the double[][] are "headers" defining the domain of the grid.
    * The subsequent rows/columns are the values at the intersection of each "header" value.
    */
   public NumericFunction2D(Object id, double[][] data, Interpolation.TYPE type, String x1Name, String x2Name) throws MathException
   {
      super(id);
      matrix = new Array2DRowRealMatrix(data, true);

      // these specify where the data points are
      // the top-left corner is dummy
      double[] x1 = matrix.getColumn(0);//Matrix.getColumn(data,0);
      double[] x2 = matrix.getRow(0);//Matrix.getRow(data,0);
      x1 = Arrays.copyOfRange(x1, 1, x1.length);
      x2 = Arrays.copyOfRange(x2, 1, x2.length);

      // function data
      double[][] dataNoHeader = matrix.getSubMatrix(1, matrix.getRowDimension()-1, 1, matrix.getColumnDimension()-1).getData();
      f = new PolynomialSpline2D(x1, x2, dataNoHeader, type);

      indVar1 = x1Name;
      indVar2 = x2Name;
   }
      
    @Override
   public double[] performCalculation(DomainInterface d)
    {
      double[] x1 = d.get(indVar1);
      double[] x2 = d.get(indVar2);

      return f.value(x1,x2);
   }

}