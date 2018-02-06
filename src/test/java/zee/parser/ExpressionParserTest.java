package zee.parser;

import java.util.HashMap;
import java.util.Map;
import zee.engine.parser.DomainParser;
import zee.engine.parser.ExpressionParser;
import zee.engine.parser.EquationSet;
import zee.engine.domain.DomainInterface;
import zee.engine.nodes.MathNode;

import zee.engine.nodes.NumberNode;
import zee.engine.nodes.VariableNode;
import zee.engine.nodes.DividedByNode;
import zee.engine.nodes.MinusNode;
import zee.engine.nodes.PlusNode;
import zee.engine.nodes.PowerNode;
import zee.engine.nodes.SinNode;
import zee.engine.nodes.TimesNode;
import org.junit.Assert;
import org.junit.Test;
import zee.engine.parser.ParseException;

import static org.junit.Assert.*;

public class ExpressionParserTest {

    @Test
    public void testDAG() throws ParseException {

        EquationSet eqs = new EquationSet();

        eqs.addSymbol("f(x,y)", "x+y");
        eqs.addSymbol("g(x,y)", "f(x,y)+x^y");
        eqs.addSymbol("g2(x,y)", "x^y+f(x,y)");
        eqs.addSymbol("a", "1+1");
        ExpressionParser parser = new ExpressionParser(eqs);
        MathNode fxy = parser.parse("f(x,y)");

        assertTrue(fxy.getID().equals("f(x,y)"));
        assertTrue(fxy.getChild(0).getID().equals("x+y"));

        // should reuse functions it's already parsed
        MathNode g = parser.parse("g(x,y)");
        assertTrue(g.getChild(0).getChild(0) == fxy);

        // should reuse functions it's parsed even if under same parent
        // here the definition of a=1+1 parses to:
        // "a" containing "+" which contains two "1"'s
        // so "1" should only have one instance though it's parsed twice
        MathNode a = parser.parse("a");
        assertTrue(a.getChild(0).getChild(0) == a.getChild(0).getChild(1));

        // subsequent parses of "f(x,y)" should result in the same object
        // since nodes are linked within the DAG
        assertTrue(fxy == parser.parse("f(x,y)"));

        // same for elements in the domain
        assertTrue(parser.parse("x") == parser.parse("x"));
        assertTrue(fxy.getChild(0).getChild(0) == parser.parse("x"));

        // not same if you swap the passed variables
        // but definition of f is the same
        MathNode fyx = parser.parse("f(y,x)");
        assertTrue(fyx != fxy);
        assertTrue(fyx.getChild(0) == fxy.getChild(0));

        // check for parsing function at end of input
        assertTrue(parser.parse("g2(x,y)").getChild(0).getChild(1) == fxy);

    }

    @Test(expected=ParseException.class)
    public void badBinaryOpLHS() throws ParseException {
        EquationSet eqs = new EquationSet();
        eqs.addSymbol("f(x,y)", " + + x");
        ExpressionParser parser = new ExpressionParser(eqs);
        parser.parse("f");
    }

    @Test(expected=ParseException.class)
    public void testParseErrorsFunction() throws ParseException {
        EquationSet eqs = new EquationSet();
        eqs.addSymbol("f(x,y)", " x+y +");
        eqs.addSymbol("g(x,y)", "a+f");
        eqs.addSymbol("a", "1 asdf");

        ExpressionParser parser = new ExpressionParser(eqs);

        parser.parse("f");
    }

    @Test(expected=ParseException.class)
    public void testParseErrorsConstant() throws ParseException {
        EquationSet eqs = new EquationSet();
        eqs.addSymbol("f(x,y)", " x+y +");
        eqs.addSymbol("g(x,y)", "a+f");
        eqs.addSymbol("a", "1 asdf");

        ExpressionParser parser = new ExpressionParser(eqs);

        parser.parse("a");
    }

    @Test
    public void testParseConstantFunction() throws ParseException {
        EquationSet eqs = new EquationSet();
        eqs.addSymbol("f(x,y)", " x+y");
        eqs.addSymbol("g(x,y)", "a+f");
        eqs.addSymbol("a", "1");

        ExpressionParser parser = new ExpressionParser(eqs);

        MathNode root = parser.parse("a");
        assertTrue(root.getID().equals("a"));
        assertTrue(root.getChild(0).getID().equals("1"));
    }

    @Test
    public void testParseStandardFunction() throws ParseException {
        EquationSet eqs = new EquationSet();
        eqs.addSymbol("f(x,y)", "sqrt( x + g(x,y) )");
        eqs.addSymbol("g(x,y)", "x + y");


        ExpressionParser parser = new ExpressionParser(eqs);

        MathNode f = parser.parse("f");
        assertTrue(f.getChild(0).getChild(0) instanceof PlusNode);
        assertTrue(f.getChild(0).getChild(0).getChild(0).getID().equals("x"));
        assertTrue(f.getChild(0).getChild(0).getChild(1).getID().equals("g(x,y)"));

    }

    @Test
    public void testParseDefaultFunction() throws ParseException {
        EquationSet eqs = new EquationSet();
        eqs.addSymbol("f(x,y)", " x+y");
        eqs.addSymbol("g(x,y)", "f - x");


        ExpressionParser parser = new ExpressionParser(eqs);

        MathNode root = parser.parse("g");
        assertTrue(root.getID().equals("g(x,y)"));
        assertTrue(root.getChild(0).getChild(0).getID().equals("f(x,y)"));

    }

    @Test
    public void testParse() throws ParseException {

        Map<String, String> meta = new HashMap<>();
        meta.put("INDEPENDANT VARIABLE", "x");

        EquationSet eqs = new EquationSet();
        eqs.addSymbol("f(x, y)", " x +y");
        eqs.addSymbol("g( x ,y)", "x^y");
        eqs.addSymbol("f5(x)", "[1 1;2 2;3 3;4 4;5 5;6 6;7 7;8 8;9 9;10 10]", meta);

        ExpressionParser parser = new ExpressionParser(eqs);

        // should be able to correctly evaluate negation symbols
        MathNode root = parser.parse("5- -5");
        assertTrue(root.getID().equals("5- -5"));
        assertTrue(root instanceof MinusNode);
        assertTrue(root.getChild(0) instanceof NumberNode);
        assertTrue(root.getChild(0).getID().equals("5"));
        assertTrue(root.getChild(1) instanceof NumberNode);
        assertTrue(root.getChild(1).getID().equals("-5"));

        // addition and subtraction should have equal priority
        // and evaluate left to right
        root = parser.parse("5-10+5");
        assertTrue(root.getID().equals("5-10+5"));
        assertTrue(root instanceof PlusNode);
        assertTrue(root.getChild(0) instanceof MinusNode);
        assertTrue(root.getChild(1).getID().equals("5"));
        assertTrue(root.getChild(0).getChild(0).getID().equals("5"));
        assertTrue(root.getChild(0).getChild(1).getID().equals("10"));

        root = parser.parse("x+y");
        assertTrue(root.getID().equals("x+y"));
        assertTrue(root instanceof PlusNode);
        assertTrue(root.getChild(0).getID().equals("x"));
        assertTrue(root.getChild(1).getID().equals("y"));

        root = parser.parse("sin(x+y)");
        assertTrue(root.getID().equals("sin(x+y)"));
        assertTrue(root instanceof SinNode);
        assertTrue(root.getChild(0).getID().equals("x+y"));
        assertTrue(root.getChild(0) instanceof PlusNode);

        root = parser.parse("(b+1)");
        assertTrue(root.getID().equals("b+1"));

        root = parser.parse("f5(x)");
        assertTrue(root.getID().equals("f5(x)"));

        // check for correct ordering when parsing mixed operations
        root = parser.parse("-(1/2) * ( x - y^2) ");
        assertTrue(root instanceof TimesNode);
        assertTrue(root.getChild(0) instanceof MinusNode);
        assertTrue(root.getChild(0).getChild(0) instanceof DividedByNode);
        assertTrue(root.getChild(0).getChild(0).getChild(0) instanceof NumberNode);
        assertTrue(root.getChild(0).getChild(0).getChild(1) instanceof NumberNode);
        assertTrue(root.getChild(1) instanceof MinusNode);
        assertTrue(root.getChild(1).getChild(0) instanceof VariableNode);
        assertTrue(root.getChild(1).getChild(1) instanceof PowerNode);
        assertTrue(root.getChild(1).getChild(1).getChild(0) instanceof VariableNode);
        assertTrue(root.getChild(1).getChild(1).getChild(1) instanceof NumberNode);

    }

    /**
     * Make sure that two numeric functions with the same definition
     * but different metadata parse to different nodes, and that
     * the interpolation for both is correct
     */
    @Test
    public void testMetadata() throws ParseException {


        Map<String, String> linearMetadata = new HashMap<>();
        linearMetadata.put("INTERPOLATION", "Linear");
        linearMetadata.put("INDEPENDANT VARIABLE", "x");

        Map<String, String> cubicMetadata = new HashMap<>();
        cubicMetadata.put("INTERPOLATION", "Cubic");
        cubicMetadata.put("INDEPENDANT VARIABLE", "x");

        EquationSet eqs = new EquationSet();

        // use the square functions from InterpolationTest.java
        // should match f(x) = x^2
        String def = "[-3 9; -2 4; -1 1; 0 0; 1 1; 2 4; 3 9]";


        eqs.addSymbol("squareLinear(x)", def, linearMetadata);
        eqs.addSymbol("squareCubic(x)", def, cubicMetadata);
        ExpressionParser parser = new ExpressionParser(eqs);

        Map<String, String> domainDefs = new HashMap<>();
        domainDefs.put("x", "[-1.0, -0.8, -0.5, 0, 0.5, 0.8, 1]");
        DomainInterface d = new DomainParser().getDomain(domainDefs);
        d = d.recombineVariable("x");

        double[] rc = parser.parse("squareCubic").evaluate(d);
        double[] rn = parser.parse("squareLinear").evaluate(d);

        double[] expVal = new double[]{1.0, 0.8, 0.5, 0.0, 0.5, 0.8, 1.0};
        Assert.assertArrayEquals(rn, expVal, 1E-10);

        double[] exactVal = new double[]{1.0, 0.64, 0.25, 0.0, 0.25, 0.64, 1.0};
        double[] expectVal = new double[]{1.0, 0.64492, 0.25481, 0.0, 0.25481, 0.64492, 1.0};

        Assert.assertArrayEquals(rc, exactVal, 1E-2);
        Assert.assertArrayEquals(rc, expectVal, 1E-5);
    }

    @Test
    public void testEvaluate() throws ParseException {

        Map<String, String> defs = new HashMap<>();
        defs.put("x", "[1:10]");
        defs.put("y", "[1:10]");
        DomainInterface d = new DomainParser().getDomain(defs);
        EquationSet eqs = new EquationSet();
        try {
            d = d.recombineVariables(new String[]{"x", "y"});
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        eqs.addSymbol("f(x, y)", " x +y");
        eqs.addSymbol("g( x ,y)", "x^y");

        ExpressionParser parser = new ExpressionParser(eqs);


        MathNode root = parser.parse("if x<5 then (99+1), if x==5 then 1, else 0");
        assertTrue(root.getChildCount() == 5);
        double[] x = d.get("x");
        double[] y = d.get("y");
        double[] results = root.evaluate(d);
        for (int i = 0; i < x.length; i++) {
            if (x[i] < 5) {
                assertTrue(results[i] == 100);
            }
            if (x[i] == 5) {
                assertTrue(results[i] == 1);
            }
            if (x[i] > 5) {
                assertTrue(results[i] == 0);
            }
        }

    }

    @Test
    public void testNegation() throws ParseException {

        Map<String, String> defs = new HashMap<String, String>();
        defs.put("x", "[1:10]");
        DomainInterface d = new DomainParser().getDomain(defs);

        d = d.recombineVariable("x");


        ExpressionParser parser = new ExpressionParser(new EquationSet());
        MathNode mx = parser.parse("-x");
        assertTrue(mx instanceof MinusNode);
        assertTrue(mx.getChildCount() == 1);
        assertTrue(mx.getChild(0).getID().equals("x"));
        assertTrue(mx.getChild(0) instanceof VariableNode);

    }

    @Test
    public void doubleNegative() throws ParseException {

        Map<String, String> defs = new HashMap<String, String>();
        defs.put("x", "[1:10]");
        DomainInterface d = new DomainParser().getDomain(defs);

        d = d.recombineVariable("x");

        ExpressionParser parser = new ExpressionParser(new EquationSet());
        MathNode mx = parser.parse("--x");
        assertTrue(mx instanceof MinusNode);
        assertTrue(mx.getChild(0) instanceof MinusNode);
        assertTrue(mx.getChild(0).getChild(0) instanceof VariableNode);

        // TODO parse --5



    }

}
