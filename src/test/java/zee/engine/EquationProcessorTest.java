package zee.engine;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static org.junit.Assert.*;
import static java.lang.Math.*;

import org.junit.Test;
import java.util.List;
import java.util.Map;
import org.junit.Before;



public class EquationProcessorTest {

   final private double epsilon = 10E-10;
   private Map<String,String> eqs = new HashMap<String,String>();
   private EquationProcessor k;
   private List<String> cols = new ArrayList<String>();
   private Map<String,String> defs = new HashMap<String,String>();

   @Before
   public void setupForEachMethod()
   {
        eqs = new HashMap<String,String>();
        k = null;
        cols = new ArrayList<String>();
        defs = new HashMap<String,String>();
   }

    ////////////////////////////////////////////////
    
   @Test(expected=ParseException.class)
   public void outputColumnsEmptyFails() throws ParseException
   {
       k = new EquationProcessor(eqs);
       k.evaluate(defs,cols);
   }

   @Test(expected=ParseException.class)
   public void outputReferencesNonexistentDomainVariable() throws ParseException
   {
      cols = Arrays.asList("y");

      k = new EquationProcessor(eqs);
      k.evaluate(defs, cols);
   }


   @Test(expected=ParseException.class)
   public void outputReferencesNonexistentEquation() throws ParseException
   {
      cols = Arrays.asList("f(2)");

      k = new EquationProcessor(eqs);
      k.evaluate(defs, cols);
   }

   @Test
   public void automaticSplitting() throws ParseException
   {
      eqs.put("f(x)","x^2");
      defs.put("x","[0.001:0.001:10]");

      double[] x = new double[10000];
      double[] f = new double[10000];
      x[0] = 0.001;
      f[0] = pow(0.001, 2);
      for(int i=1; i < x.length; i++)
      {
          x[i] = x[i-1] + 0.001;
          f[i] = pow(x[i], 2);
      }

      k = new EquationProcessor(eqs);
      List<double[]> results = k.evaluate(defs, Arrays.asList("x", "f"), -1, true);

      assertEquals(10000, results.get(0).length);

      assertArrayEquals(x, results.get(0), 10E-10);
      assertArrayEquals(f, results.get(1), 10E-10);
   }

   @Test
   public void explicitDomainPoints() throws ParseException
   {
      // specify an evaluation just on a diagonal line across the xy domain
      // here the function is a constant function, the point of interest here
      // is that the results are not recombined to a length of 25 points
      eqs.put("f(x,y)","1");
      defs.put("x","[1 2 5 6 9]");
      defs.put("y","[1 2 5 6 9]");

      k = new EquationProcessor(eqs);
      List<double[]> results = k.evaluate(defs, Arrays.asList("x", "y", "f"), -1, false);

      assertEquals(5, results.get(0).length);
   }

   @Test
   public void explicitDomainPointsButNotUsed() throws ParseException
   {
      // specify an evaluation just on a diagonal line across the xy domain
      // here the function is a constant function, the point of interest here
      // is that the results are not recombined to a length of 25 points
      eqs.put("f(x,y)","1");
      defs.put("x","[1 2 5 6 9]");
      defs.put("y","[1 2 5 6 9]");

      k = new EquationProcessor(eqs);
      List<double[]> results = k.evaluate(defs, Arrays.asList("1+1"), -1, false);

      assertEquals(1, results.get(0).length);
   }

   @Test(expected=ParseException.class)
   public void explicitDomainPointsNotSameLength() throws ParseException
   {
      // specify an evaluation just on a diagonal line across the xy domain
      // here the function is a constant function, the point of interest here
      // is that the results are not recombined to a length of 25 points
      eqs.put("f(x,y)","1");
      defs.put("x","[1 2 5 6 9]");
      defs.put("y","[1 2 5 6 9 10]");

      k = new EquationProcessor(eqs);
      k.evaluate(defs, Arrays.asList("x", "y", "f"), -1, false);
   }
    /*
   @Test
   public void testCallable() throws Exception {
      Double d1 =  new Double(Kernel.evaluateExpression("1+1"));
      FutureTask<Double> t = new FutureTask<Double>(Kernel.createCallable("1+1"));
      t.run();
      Double d2 = t.get();
      Assert.assertTrue(d1.equals(d2));


      Hashtable<String,String> eqs = new Hashtable<String,String>();
      eqs.put("piApprox(k)","8*sum(ChebyshevTerm(k))");
      eqs.put("ChebyshevTerm(k)","( (-1)^k * (sqrt(2)-1)^(2*k+1)) / (2*k+1)");
      Kernel k = new Kernel(eqs);
      Hashtable<String,String> defs = new Hashtable<String,String>();
      defs.put("k","[0:50]");
      Vector<String> cols = new Vector<String>(Arrays.asList("k", "ChebyshevTerm", "piApprox"));
      FutureTask<Vector<double[]>> ft = new FutureTask<Vector<double[]>>(k.createCallable(defs,cols));

      ft.run();
      Vector<double[]> r1 = ft.get();
      Vector<double[]> r2 = k.evaluate(defs,cols);

      for(int i=0; i < r1.size(); i++)
        Assert.assertArrayEquals(r1.get(i), r2.get(i),1E-10);


   }
*/
}
