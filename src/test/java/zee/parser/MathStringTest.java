package zee.parser;

import zee.engine.parser.MathString;
import zee.engine.parser.EquationSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.*;
import zee.engine.parser.ExpressionParser;

public class MathStringTest {

    @Test(expected = ParseException.class)
    public void badUnaryOperator() throws ParseException {
        EquationSet eqs = new EquationSet();
        eqs.addSymbol("f(x)", "#x");

        ExpressionParser parser = new ExpressionParser(eqs);
        parser.parse("f");
    }

    @Test
    public void testNrowsNcols() {
        String m = "[ 1 2; 3,4; 5  6]";
        assertTrue(MathString.nrows(m) == 3);
        assertTrue(MathString.ncols(m) == 2);

        assertTrue(MathString.ncols("[1.0 2.0 ]") == 2);

    }

    /**
     * Test of getMatchingParenthesis method, of class kernel.MathString.
     */
    @Test
    public void testGetMatchingParenthesis() {

        String expression = "( () (( )() )  )";
        int position = 5;

        int expResult = 12;
        int result = MathString.getMatchingParenthesis(expression, position);
        Assert.assertSame(expResult, result);

    }

    @Test
    public void testIsPiecewise() {
        String m1 = " if a then b";
        String m2 = "\nIf a then b";
        String m3 = "IF a then b";

        assertTrue(MathString.isPiecewise(m1));
        assertTrue(MathString.isPiecewise(m2));
        assertTrue(MathString.isPiecewise(m3));
    }

    @Test
    public void testIsFunction() {
        String m1 = "f(x)";
        String m2 = "f(x,(y+1))";
        String m3 = "f(";
        String m4 = "f()";
        String m5 = "2f()";
        String m6 = "f2()";

        assertTrue(MathString.isFunction(m1));
        assertTrue(MathString.isFunction(m2));
        assertFalse(MathString.isFunction(m3));
        assertTrue(MathString.isFunction(m4));
        assertFalse(MathString.isFunction(m5));
        assertTrue(MathString.isFunction(m6));
    }

    @Test
    public void testGetFunctionArgs() {
        String m2 = "f(x,(y+1))";
        assertTrue(MathString.getFunctionArgs(m2)[0].equals("x"));
        assertTrue(MathString.getFunctionArgs(m2)[1].equals("(y+1)"));

        m2 = "f(x,f2(y,1))";
        assertTrue(MathString.getFunctionArgs(m2)[0].equals("x"));
        assertTrue(MathString.getFunctionArgs(m2)[1].equals("f2(y,1)"));
    }

    @Test
    public void testIsNumber() {
        String m1 = "1";
        String m2 = ".0";
        String m3 = "0.1";
        String m4 = "0.1E5";
        String m5 = "-.1";
        String m6 = "1 asdf";

        assertTrue(MathString.isNumber(m1));
        assertTrue(MathString.isNumber(m2));
        assertTrue(MathString.isNumber(m3));
        assertTrue(MathString.isNumber(m4));
        assertTrue(MathString.isNumber(m5));
        assertFalse(MathString.isNumber(m6));
    }

    @Test
    public void testIsParenGroup() {

        String[] pg = new String[]{"()", "(()())", "(()()())"};
        String[] nonpg = new String[]{"(()", "(()(()", "((1+1)-(0)*(1)"};
        for (int i = 0; i < pg.length; i++) {
            assertTrue(MathString.isParenGroup(pg[i]));
        }
        for (int i = 0; i < nonpg.length; i++) {
            assertFalse(MathString.isParenGroup(nonpg[i]));
        }

    }

    @Test
    public void testIsMatrix() {
        String m1 = "[0 1 2]";
        String m2 = "[1:10]";
        String m3 = "[1:0.1:10]";
        String m4 = "[1 1;2 2;3 3;4 4;5 5;6 6;7 7;8 8;9 9;10 10]";
        String m5 = "[ 1 2; 3, 4; 5 6;7 8 ; 9,10]";

        assertTrue(MathString.isMatrix(m1));
        assertTrue(MathString.isMatrix(m2));
        assertTrue(MathString.isMatrix(m3));
        assertTrue(MathString.isMatrix(m4));
        assertTrue(MathString.isMatrix(m5));
    }

    @Test
    public void testFindOperator() {

        List<String> t1 = new ArrayList<>();

        t1.add("a");
        t1.add("*");
        t1.add("b");
        assertTrue(MathString.findBinaryOperator(t1) == 1);

        t1.add("+");
        t1.add("q");
        assertTrue(MathString.findBinaryOperator(t1) == 3);

        t1.add("/");
        t1.add("2");
        assertTrue(MathString.findBinaryOperator(t1) == 3);

        t1.add("-");
        t1.add("1");
        assertTrue(MathString.findBinaryOperator(t1) == 7);

        ////////////////////////////////////////////////
        // test for finding binary logical operators

        t1.clear();

        t1.add("a");
        t1.add("<");
        t1.add("1");
        assertTrue(MathString.findBinaryOperator(t1) == 1);

    }

    @Test
    public void testFindBinaryOperator() throws Exception {

        List<String> tokens = new ArrayList<String>();
        tokens.add("-");
        tokens.add("-");
        tokens.add("5");

        int index = MathString.findBinaryOperator(tokens);

        assertEquals(-1, index);

    }

    @Test
    public void testSplit() throws ParseException {

//        Map<String, String> defs = new HashMap<>();
//        defs.put("x", "[1:10]");
//        defs.put("y", "[1:10]");
//        DomainInterface d = new DomainParser().getDomain(defs);
//        d.recombineVariables(new String[]{"x", "y"});

        EquationSet eqs = new EquationSet();
        eqs.addSymbol("f(x, y)", " x +y");
        eqs.addSymbol("g( x ,y)", "x^y");

        String def = "x";
        List<String> tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("x"));

        def = "-x";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("-"));
        assertTrue(tokens.get(1).equals("x"));

        def = "2*-x";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("2"));
        assertTrue(tokens.get(1).equals("*"));
        assertTrue(tokens.get(2).equals("-x"));

        def = "x1";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("x1"));

        def = "(a) + ((1+2)/(a-1))";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("(a)"));
        assertTrue(tokens.get(1).equals("+"));
        assertTrue(tokens.get(2).equals("((1+2)/(a-1))"));

        def = "f(x,y) - x/(x*y) + 5.0E+1+2^2";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("f(x,y)-x/(x*y)+5.0E+1"));
        assertTrue(tokens.get(1).equals("+"));
        assertTrue(tokens.get(2).equals("2^2"));


        def = "f(x,y)-x/(x*y)+5.0E+1";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("f(x,y)-x/(x*y)"));
        assertTrue(tokens.get(1).equals("+"));
        assertTrue(tokens.get(2).equals("5.0E+1"));

        def = "f(x,y)-x/(x*y)";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("f(x,y)"));
        assertTrue(tokens.get(1).equals("-"));
        assertTrue(tokens.get(2).equals("x/(x*y)"));

        def = "x/(x*y)";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("x"));
        assertTrue(tokens.get(1).equals("/"));
        assertTrue(tokens.get(2).equals("(x*y)"));

        def = "a<1";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("a"));
        assertTrue(tokens.get(1).equals("<"));
        assertTrue(tokens.get(2).equals("1"));

        def = "a<1 & b <= 2 & d ==3 | sin(x)>=0";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("a<1&b<=2"));
        assertTrue(tokens.get(1).equals("&"));
        assertTrue(tokens.get(2).equals("d==3|sin(x)>=0"));

        def = "b<=2&d==3|sin(x)>=0";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("b<=2"));
        assertTrue(tokens.get(1).equals("&"));
        assertTrue(tokens.get(2).equals("d==3|sin(x)>=0"));

        def = "d==3|sin(x)>=0";
        tokens = MathString.splitExpression(def);
        assertTrue(tokens.get(0).equals("d==3"));
        assertTrue(tokens.get(1).equals("|"));
        assertTrue(tokens.get(2).equals("sin(x)>=0"));

    }

    /**
     * Test of toDoubleArray method
     */
    @Test
    public void testToDoubleArray() {

        String toParse = "[1 2 3]";
        double[] expResult = new double[]{1.0, 2.0, 3.0};
        double[] result = MathString.toDoubleArray(toParse);
        assertArrayEquals(expResult, result, 0.00001);

        toParse = "[0:0.1:0.5]";
        expResult = new double[]{0.0, 0.1, 0.2, 0.3, 0.4, 0.5};
        result = MathString.toDoubleArray(toParse);
        assertArrayEquals(expResult, result, 0.00001);

        toParse = "[1:4]";
        expResult = new double[]{1, 2, 3, 4};
        result = MathString.toDoubleArray(toParse);
        assertArrayEquals(expResult, result, 0.00001);

        toParse = "[1 2:5 6:1:9 10]";
        expResult = new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        result = MathString.toDoubleArray(toParse);
        assertArrayEquals(expResult, result, 0.00001);

    }
}
