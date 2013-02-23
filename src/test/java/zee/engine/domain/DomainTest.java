package zee.engine.domain;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.Vector;
import junit.framework.Assert;
import zee.engine.parser.DomainParser;

public class DomainTest {
   
   private Hashtable<String,String> defs;
   private DomainInterface d;


   @Before
   public void createDefs() {
      defs = new Hashtable<String,String>();
      defs.put("x","[1:3]");
      defs.put("y","[4 5]");
      defs.put("z","[6]");

      DomainParser parser = new DomainParser();
      d = parser.getDomain(defs);
   }

   @Test(expected=IllegalArgumentException.class)
   public void badSplitTooFew()
   {
       d.splitDomain(0);
   }

   @Test(expected=IllegalArgumentException.class)
   public void badSplitTooMany()
   {
       d.splitDomain(10);
   }

   @Test
   public void isSameLengthDefs()
   {
       d = new Domain(new HashMap<String, double[]>());
      assertTrue(d.isSameLengthDefs());
   }

   /**
    * Ensure that Domain returns a copy 
    * when you request a double[] for a variable.
    */
   @Test
   public void testGet()
   {
     d = d.recombineVariable("x");
     double[] v0 = d.get("x");
     double[] v1 = d.get("x");
     assertNotSame(v0, v1);
   }

   @Test
   public void testRemove() {

         assertFalse(d.containsKey("x"));
         assertFalse(d.containsKey("y"));
         assertFalse(d.containsKey("z"));


         d = d.recombineVariable("x");
         d = d.recombineVariable("y");
         d = d.recombineVariable("z");

         assertTrue(d.containsKey("x"));
         assertTrue(d.containsKey("y"));
         assertTrue(d.containsKey("z"));
         assertArrayEquals(new double[]{1,2,3,1,2,3}, d.get("x"), 10E-10);
         assertArrayEquals(new double[]{4,4,4,5,5,5}, d.get("y"), 10E-10);
         assertArrayEquals(new double[]{6,6,6,6,6,6}, d.get("z"), 10E-10);

         d = d.removeVariable("x");

         assertFalse(d.containsKey("x"));
         assertTrue(d.containsKey("y"));
         assertTrue(d.containsKey("z"));
         assertArrayEquals(new double[]{4,5}, d.get("y"), 10E-10);
         assertArrayEquals(new double[]{6,6}, d.get("z"), 10E-10);

         d = d.removeVariable("wtf");

         assertFalse(d.containsKey("x"));
         assertTrue(d.containsKey("y"));
         assertTrue(d.containsKey("z"));
         assertArrayEquals(new double[]{4,5}, d.get("y"), 10E-10);
         assertArrayEquals(new double[]{6,6}, d.get("z"), 10E-10);
   }

   @Test
   public void testSplitDomain() {

      try {
         d = d.recombineVariable("x");
      } catch(Exception e) { assertTrue(false); }

      Vector<? extends DomainInterface> ds = d.splitDomain(1);
      assertTrue(ds.size() == 1);
      assertArrayEquals(ds.get(0).get("x"),new double[]{1,2,3},0.0001);
      
      ds = d.splitDomain(2);
      assertTrue(ds.size() == 2);
      assertArrayEquals(ds.get(0).get("x"),new double[]{1,2},0.0001);
      assertArrayEquals(ds.get(1).get("x"),new double[]{3},0.0001);
      
      ds = d.splitDomain(3);
      assertTrue(ds.size() == 3);
      assertArrayEquals(ds.get(0).get("x"),new double[]{1},0.0001);
      assertArrayEquals(ds.get(1).get("x"),new double[]{2},0.0001);
      assertArrayEquals(ds.get(2).get("x"),new double[]{3},0.0001);
      
      Hashtable<String,String> defs2 = new Hashtable<String,String>();
      defs2.put("x","[1:0.001:100]");
      d = new DomainParser().getDomain(defs2);
     d = d.recombineVariable("x");
     ds = d.splitDomain(10);
     assertTrue(ds.size() == 10);
   }
   
   /**
    * Test of equals method, of class kernel.Domain.
    */
   @Test
   public void testEquals() {

      Object other = new DomainParser().getDomain(defs);
      
      Assert.assertEquals(false, d.equals(other));
      Assert.assertTrue(d.equals(d));

   }

   /**
    * Test of setVariable method, of class kernel.Domain.
    */
   @Test
   public void testSetVariable() {
      
      String var = "x";
      double[] data = new double[]{1, 1, 1};
      
      d = d.setVariable(var, data);
      assertArrayEquals(d.get(var), data, 0.00001);

   }

   /**
    * Test of recombineVariables method, of class kernel.Domain.
    */
   public void testRecombineVariables() {
      
      d = d.recombineVariables(new String[]{"x","y","z"});
      
      double[] result = d.get("x");
      double[] expResult = new double[]{1,2,3,1,2,3};
      assertArrayEquals(expResult, result, 0.00001);
      
      result = d.get("y");
      expResult = new double[]{4,4,4,5,5,5};
      assertArrayEquals(expResult, result, 0.00001);
      
      result = d.get("z");
      expResult = new double[]{6,6,6,6,6,6};
      assertArrayEquals(expResult, result, 0.00001);
   }

   @Test(expected=UnsupportedOperationException.class)
   public void testNotImplemented1()
   {
       d.put(null, null);
   }

    @Test(expected=UnsupportedOperationException.class)
   public void testNotImplemented2()
   {
       d.putAll(null);
   }

    @Test(expected=UnsupportedOperationException.class)
   public void testNotImplemented3()
   {
       d.remove(null);
   }

    @Test(expected=UnsupportedOperationException.class)
   public void testNotImplemented4()
   {
       d.clear();
   }

   @Test
   public void containsValue()
   {
      d = d.recombineVariables(new String[]{"x","y","z"});
      assertTrue(d.containsValue(d.get("x")));
      assertFalse(d.containsValue(new double[]{}));
   }

   @Test
   public void isEmpty()
   {
       assertTrue(d.isEmpty());

       d = d.recombineVariables(new String[]{"x","y","z"});
       
       assertFalse(d.isEmpty());
   }

   @Test
   public void size()
   {
       assertEquals(3, d.size());
   }

   @Test
   public void values()
   {

       assertTrue(d.isEmpty());

       d = d.recombineVariables(new String[]{"x","y","z"});

       assertFalse(d.isEmpty());
   }
}
