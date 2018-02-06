package zee.engine.nodes.interpolators;

import org.junit.Test;
import static java.lang.Math.*;

import static org.junit.Assert.*;

public class InterpolationTest {

   @Test
   public void testLinear1D() throws Exception {

       // define function for f(x) = |x|
      double[] x = new double[]{ -1, 0, 1};
      double[] y = new double[]{  1, 0, 1};      

      PolynomialSpline1D p = new PolynomialSpline1D(x,y,Interpolation.TYPE.LINEAR);
      double v = p.value(0.5);
      assertEquals(0.5, v, 1E-6);
      
      double[] vs = new double[]
      {
            p.value(-1.0),
            p.value(-0.9),
            p.value(-0.8),
            p.value(-0.5),
            p.value( 0.0),
            p.value( 0.5),
            p.value( 0.8),
            p.value( 0.9),
            p.value( 1.0)  
      };
      
      double[] expVal = new double[]{1.0, 0.9, 0.8, 0.5, 0.0, 0.5, 0.8, 0.9, 1.0};
      assertArrayEquals(expVal, vs, 1E-10);
   }

    @Test
   public void testCubic1D() throws Exception {

      // should match f(x) = x^2
      double[] x = new double[]{-3, -2, -1, 0, 1, 2, 3};
      double[] y = new double[]{9, 4, 1, 0, 1, 4, 9};
      PolynomialSpline1D p = new PolynomialSpline1D(x,y,Interpolation.TYPE.CUBIC);
      
      double[] exact = p.value(x);
      assertArrayEquals(exact,y,10E-10);
      
      double v = p.value(0.25);
      double ve = Math.pow(0.25,2);
      assertEquals(v, ve, 0.0025);
      assertEquals(ve, 0.0625, 10E-14);

      v = p.value(-0.25);
      ve = Math.pow(-0.25,2);
      assertEquals(v, ve, 0.0025);
      assertEquals(ve, 0.0625, 10E-14);
      
      double[] vs = new double[] {
            p.value(-1.0),
            p.value(-0.8),
            p.value(-0.5),
            p.value( 0.0),
            p.value( 0.5),
            p.value( 0.8),
            p.value( 1.0)  };
      double[] exactVal = new double[]{1.0, 0.64, 0.25, 0.0, 0.25, 0.64, 1.0};
      double[] expectVal = new double[]{1.0, 0.64492, 0.25481, 
                                          0.0, 0.25481, 0.64492, 1.0};
      assertArrayEquals(vs,exactVal,1E-2);
      assertArrayEquals(vs,expectVal,1E-5);
   }


    /**
     * Interpolation should be exact for polynomials of degree 1 or less.
     */
//    @Test
//   public void testLinear2D() throws Exception {
//
//      // f(x,y) = 2x + y, should be exact with linear interp
//      // f(0.5, 0.5) = 1.5
//      // f(0.5, 1.5) = 2.5
//      // f(1.5, 0.5) = 4.5
//      // f(1.5, 1.5) = 5.5
//      double[] xData = new double[] { 0, 1, 2};
//      double[] yData = new double[] { 0, 1, 2};
//      double[][] zData = new double[xData.length][yData.length];
//      for(int i=0; i < xData.length; i++) {
//          for(int j=0; j<yData.length; j++) {
//             zData[i][j] = 2*xData[i] + yData[j];
//         }
//      }
//
//      PolynomialSpline2D p = new PolynomialSpline2D(xData,yData,zData,Interpolation.TYPE.LINEAR);
//
//      double[] evalx = new double[] {0.5, 0.5, 1.5, 1.5};
//      double[] evaly = new double[] {0.5, 1.5, 0.5, 1.5};
//      double[] evalz = new double[evalx.length];
//      double[] exactz = new double[evalx.length];
//      for(int i=0; i < evalx.length; i++) {
//         evalz[i]  = p.value(evalx[i], evaly[i]);
//         exactz[i] = 2 * evalx[i] + evaly[i];
//      }
//
//      assertArrayEquals(exactz, evalz, 10E-10);
//
//   }

    /**
     * Interpolation should be exact for polynomials of degree 1 or less.
     *
     * @throws MathException
     */
    @Test
   public void testCubic2D() throws Exception {

      // f(x,y) = x^2 + y^2, should be exact with cubic interp
      double[] xData = new double[] { 0, .025, .5, .75, 1, 1.25, 1.5, 1.75, 2};
      double[] yData = new double[] { 0, .025, .5, .75, 1, 1.25, 1.5, 1.75, 2};
      double[][] zData = new double[xData.length][yData.length];
      for(int i=0; i < xData.length; i++) {
          for(int j=0; j<yData.length; j++) {
             zData[i][j] = pow(xData[i],2) + pow(yData[j],2);
         }
      }

      PolynomialSpline2D p = new PolynomialSpline2D(xData,yData,zData,Interpolation.TYPE.CUBIC);

      double[] evalx = new double[] {0.5, 0.5, 1.5, 1.5};
      double[] evaly = new double[] {0.5, 1.5, 0.5, 1.5};
      double[] evalz = new double[evalx.length];
      double[] exactz = new double[evalx.length];
      for(int i=0; i < evalx.length; i++) {
         evalz[i]  = p.value(evalx[i], evaly[i]);
         exactz[i] = pow(evalx[i],2) + pow(evaly[i],2);
      }

      assertArrayEquals(exactz, evalz, 0.25);

   }

}
