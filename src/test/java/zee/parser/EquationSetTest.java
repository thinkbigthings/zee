package zee.parser;

import zee.engine.parser.EquationSet;
import java.text.ParseException;
import java.util.Hashtable;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

public class EquationSetTest {


   /*
   public void testGetVariables() {
      
      EquationSet d = new EquationSet();
      d.addSymbol("f( x ,y)","x+y");
      d.addSymbol("a","1");
      Vector<String> vars;
      
      vars = d.getVariables("f(x)");
      assertTrue(vars.contains("x"));
      
      vars = d.getVariables("x");
      assertTrue(vars.contains("x"));
      
      vars = d.getVariables("y+x");
      assertTrue(vars.contains("x"));
      assertTrue(vars.contains("y"));
      
      vars = d.getVariables("x2");
      assertTrue( ! vars.contains("x"));
      
      vars = d.getVariables("bix");
      assertTrue( ! vars.contains("x"));
      
      vars = d.getVariables("xavier");
      assertTrue( ! vars.contains("x"));

   }
   */
   
   /**
    * Shouldn't be able to specify a function name as an argument 
    * in another function's signature (leading to ambiguous definitions)
    */
   @Test
   public void testOverlap() {
      EquationSet instance = new EquationSet();
      
      boolean caughtOverlap = false;
      try {
         // this is an ambiguous definition 
         // since the domain variable overlaps a function name
         instance.addSymbol("someName(x)","x^2");
         instance.addSymbol("someFunction(someName)","someName+1");
      } catch (ParseException ex) {
         caughtOverlap = true;
      }
      assertTrue(caughtOverlap);
      
      instance = new EquationSet();
      caughtOverlap = false;
      try {
         // try again with a different order
         instance.addSymbol("someFunction(someName)","someName+1");
         instance.addSymbol("someName(x)","x^2");
      } catch (ParseException ex) {
         caughtOverlap = true;
      }
      assertTrue(caughtOverlap);
   }
   
   /**
    * Test of isSymbolDefined method, of class kernel.EquationSet.
    */
   @Test
   public void testIsSymbolDefined() {

      EquationSet instance = new EquationSet();
      
      try {
         instance.addSymbol("f( x ,y)","x+y");
         instance.addSymbol("a","1");
      } catch (ParseException ex) {
         ex.printStackTrace();
      }
      
      String name = "f";
      boolean expResult = true;
      boolean result = instance.isSymbolDefined(name);
      assertEquals(expResult, result);
      
      name = "a";
      expResult = true;
      result = instance.isSymbolDefined(name);
      assertEquals(expResult, result);
      
      name = "b";
      expResult = false;
      result = instance.isSymbolDefined(name);
      assertEquals(expResult, result);
      
   }

   /**
    * Test of getArguments method, of class kernel.EquationSet.
    */
   @Test
   public void testGetArguments() {

      String symbol = "f";
      EquationSet instance = new EquationSet();
      
      try {
         instance.addSymbol("f(x, y)","x+y");
         instance.addSymbol("a","1");
      } catch (ParseException ex) {
         ex.printStackTrace();
      }
      
      String[] expResult = new String[]{"x","y"};
      String[] result = instance.getArguments(symbol);
      assertArrayEquals(expResult, result);
      
      symbol = "a";
      expResult = new String[0];
      result = instance.getArguments(symbol);
      assertArrayEquals(expResult, result);
      
   }

   @Test
   public void testMetadata() {

      Hashtable<String,String> linear = new Hashtable<String,String>();
      linear.put("INTERPOLATION", "LINEAR");
      linear.put("INDEPENDANT VARIABLE", "x");
      
      String symbol = "f";
      EquationSet instance = new EquationSet();
      try {
         instance.addSymbol("f(x)","[1 1; 2 2; 3 3]", linear);
      } catch (ParseException ex) {
         ex.printStackTrace();
      }
      
      String expResult = "LINEAR";
      String result = instance.getMetadata(symbol).get("INTERPOLATION").toString();
      assertEquals(expResult, result);
      
   }
   
   /**
    * Test of getDefinition method, of class kernel.EquationSet.
    */
   @Test
   public void testGetDefinition() {

      String symbol = "f";
      EquationSet instance = new EquationSet();
      try {
         instance.addSymbol("f(x , y)","x+y");
         instance.addSymbol("a","1");
      } catch (ParseException ex) {
         ex.printStackTrace();
      }
      
      String expResult = "x+y";
      String result = instance.getDefinition(symbol);
      assertEquals(expResult, result);
      
      symbol = "a";
      expResult = "1";
      result = instance.getDefinition(symbol);
      assertEquals(expResult, result);
      
      symbol = "b";
      expResult = null;
      result = instance.getDefinition(symbol);
      assertEquals(expResult, result);
      
   }

   /**
    * Test of addSymbol method, of class kernel.EquationSet.
    */
   @Test
   public void testAddSymbol() {

      String name = "";
      String def = "";
      EquationSet instance = new EquationSet();
      try {
         
         instance.addSymbol("f(x,y)","x+y");
      } catch (ParseException ex) {
         ex.printStackTrace();
      }
      assertTrue(instance.isSymbolDefined("f"));
      assertTrue(instance.getDefinition("f").equals("x+y"));

   }
   
}
