package zee.engine.nodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import zee.engine.parser.DomainParser;
import zee.engine.parser.ExpressionParser;
import zee.engine.parser.EquationSet;
import zee.engine.domain.DomainInterface;

import java.text.ParseException;
import java.util.Hashtable;
import org.junit.Test;
import zee.engine.EquationProcessor;
import static org.junit.Assert.*;

public class NodeEvaluationTest {

    @Test
    public void testCache() throws ParseException {

        EquationSet eqs = new EquationSet();
        eqs.addSymbol("a", "-(1/2)");
        eqs.addSymbol("f1(x,y)", "a * ( x - y^2 )");
        eqs.addSymbol("f2(x,y)", "-f1 + 2*f1 + 0");

        ExpressionParser parser = new ExpressionParser(eqs);
        MathNode a = parser.parse("a");
        MathNode f1 = parser.parse("f1");
        MathNode f2 = parser.parse("f2");

        Map<String, String> defs = new HashMap<String, String>();
        defs.put("x", "[1:3]");
        defs.put("y", "[4 5]");
        defs.put("z", "[6 7]");

        DomainInterface d = new DomainParser().getDomain(defs);

        DomainInterface d1 = d.recombineVariable("x");
        d1 = d1.recombineVariable("y");

        DomainInterface d2 = d.recombineVariable("x");
        d2 = d2.recombineVariable("y");
        d2 = d2.recombineVariable("z");

        f1.evaluate(d1);
        assertTrue(f1.isCached(d1));
        assertFalse(f1.isCached(d2));
        f1.clearCache();
        assertFalse(f1.isCached(d1));


        assertFalse(f2.isCached(d1));
        f2.evaluate(d1);
        assertFalse(f2.isCached(d1));


    }

    @Test
    public void testDomainSwitcharoo() throws ParseException {
        
        Map<String, String> eqs = new HashMap<String, String>();
        eqs.put("f(y)", "y^2");

        Map<String, String> defs = new HashMap<String, String>();
        defs.put("x", "[1:3]");

        List<String> out = Arrays.asList("x", "f(x)");


        EquationProcessor k = new EquationProcessor(eqs);
        List<double[]> cols = k.evaluate(defs, out);

        Assert.assertArrayEquals(cols.get(1), new double[]{1, 4, 9}, 0.0001);
    }

}
