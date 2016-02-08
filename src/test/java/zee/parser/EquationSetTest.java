package zee.parser;

import java.text.ParseException;
import zee.engine.parser.EquationSet;
import java.util.HashMap;
import java.util.Map;
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
   
   @Test(expected = ParseException.class)
   public void testOverlap() throws Exception {
       
      // Shouldn't be able to specify a function name as an argument 
      // in another function's signature (leading to ambiguous definitions)
      EquationSet instance = new EquationSet();
      
      // this is an ambiguous definition 
      // since the domain variable overlaps a function name
      instance.addSymbol("someName(x)","x^2");
      instance.addSymbol("someFunction(someName)","someName+1");
   }
   
   @Test(expected = ParseException.class)
   public void testOverlapDifferentOrder() throws Exception {
      
      EquationSet instance = new EquationSet();

      // try again with a different order
      instance.addSymbol("someFunction(someName)","someName+1");
      instance.addSymbol("someName(x)","x^2");
   }
   
   @Test
   public void testIsSymbolDefined() throws Exception {

      EquationSet instance = new EquationSet();
      
      instance.addSymbol("f( x ,y)","x+y");
      instance.addSymbol("a","1");

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

   @Test
   public void testGetArguments() throws Exception {

      String symbol = "f";
      EquationSet instance = new EquationSet();
      instance.addSymbol("f(x, y)","x+y");
      instance.addSymbol("a","1");
      
      String[] expResult = new String[]{"x","y"};
      String[] result = instance.getArguments(symbol);
      assertArrayEquals(expResult, result);
      
      symbol = "a";
      expResult = new String[0];
      result = instance.getArguments(symbol);
      assertArrayEquals(expResult, result);
      
   }

   @Test
   public void testMetadata() throws Exception {

      Map<String,String> linear = new HashMap<>();
      linear.put("INTERPOLATION", "LINEAR");
      linear.put("INDEPENDANT VARIABLE", "x");
      
      String symbol = "f";
      EquationSet instance = new EquationSet();

      instance.addSymbol("f(x)","[1 1; 2 2; 3 3]", linear);
      
      String expResult = "LINEAR";
      String result = instance.getMetadata(symbol).get("INTERPOLATION");
      assertEquals(expResult, result);
      
   }
   
   @Test
   public void testGetDefinition() throws Exception {

      String symbol = "f";
      EquationSet instance = new EquationSet();
      instance.addSymbol("f(x , y)","x+y");
      instance.addSymbol("a","1");
      
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

   @Test
   public void testAddSymbol() throws Exception {

      String name = "";
      String def = "";
      EquationSet instance = new EquationSet();
      instance.addSymbol("f(x,y)","x+y");

      assertTrue(instance.isSymbolDefined("f"));
      assertTrue(instance.getDefinition("f").equals("x+y"));

   }
   
}
