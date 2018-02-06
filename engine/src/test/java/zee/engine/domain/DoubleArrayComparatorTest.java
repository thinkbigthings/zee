package zee.engine.domain;

import java.util.Comparator;
import org.junit.Test;
import static org.junit.Assert.*;

public class DoubleArrayComparatorTest {
    
   /**
    * Test of compare method.
    */
    @Test
   public void testCompare() {

      double[] r1;
      double[] r2;
      Comparator<double[]> instance = new DoubleArrayComparator();
      
      r1 = new double[]{1,2,3};
      r2 = new double[]{1,2,3};      
      assertEquals(0, instance.compare(r1, r2));

      r1 = new double[]{1,2,3};
      r2 = new double[]{1,3,2};      
      assertEquals(-1, instance.compare(r1, r2));
      
      r1 = new double[]{1,3,3};
      r2 = new double[]{1,3,2};      
      assertEquals(1, instance.compare(r1, r2));
      
   }

    @Test(expected=IllegalArgumentException.class)
    public void testDifferentLengths()
    {
      double[] r1 = new double[0];
      double[] r2 = new double[1];
      Comparator<double[]> instance = new DoubleArrayComparator();
      instance.compare(r1, r2);
    }

    @Test
    public void testEquals()
    {
      Comparator<double[]> c1 = new DoubleArrayComparator();
      Comparator<double[]> c2 = new DoubleArrayComparator();
      assertTrue(c1.equals(c2));
    }

    @Test
    public void testHash()
    {
      Comparator<double[]> c1 = new DoubleArrayComparator();
      Comparator<double[]> c2 = new DoubleArrayComparator();
      assertEquals(c1.hashCode(), c2.hashCode());
    }
}
