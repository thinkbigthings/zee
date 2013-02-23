package zee.engine.parser;

import zee.engine.nodes.MathNodeFactory;
import zee.engine.nodes.MathNode;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import zee.engine.nodes.DomainTransformation;
import zee.engine.nodes.VariableNode;

/**
 * Each element in the output set is parsed and plugged into the DAG. 
 * As the parser encounters symbols, it searches the symbol store 
 * (the list of names in the domain or function set) for an appropriate symbol. 
 * This process ensures that only functions and constants required for the 
 * output are parsed into the tree, it gives you automatic function culling.
 */
public class ExpressionParser implements ParserStrategy {

    private Hashtable<SymbolMetaKey, MathNode> keysToNode = new Hashtable<SymbolMetaKey, MathNode>();
    private EquationSet equations; // no setter methods are called in this class
    private MathNodeFactory factory = new MathNodeFactory();
    private ParserStrategy matrixParser = null;
    private ParserStrategy piecewiseParser = null;
    private ParserStrategy numberParser = null;
    private ParserStrategy parenParser = null;
    private ParserStrategy variableParser = null;
    private ParserStrategy predefParser = null;
    private ParserStrategy userdefParser = null;

    private class SymbolMetaKey {

        private String symbol;
        private Map<String, String> meta;

        public SymbolMetaKey(String s, Map<String, String> m) {
            symbol = s;
            meta = m;
        }

        @Override
        public boolean equals(Object other) {
            SymbolMetaKey smk = (SymbolMetaKey) other;
            return smk.symbol.equals(symbol) && smk.meta.equals(meta);
        }

        @Override
        public int hashCode() {
            return symbol.hashCode();
        }
    };

    public ExpressionParser(EquationSet eqs) {
        equations = eqs;

        matrixParser = new MatrixParser();
        piecewiseParser = new PiecewiseParser(this);
        numberParser = new NumberParser();
        parenParser = new ParenGroupParser(this);
        variableParser = new VariableParser();
        predefParser = new PreDefinedSymbolParser(this);
        userdefParser = new UserDefinedSymbolParser(equations, this);
    }

    /**
     * Here, symbols refers to the 'key' part of a function set,
     * say, the function name or variable name that identifies a function.
     *
     *
     * @throws java.text.ParseException
     */
    public void parseAllSymbols() throws ParseException {
        Vector<String> symbols = equations.getAllSymbols();
        for (String symbol : symbols) {
            parse(symbol, equations.getMetadata(symbol));
        }
    }

    public MathNode parse(String toParse) throws ParseException {
        return parse(toParse, equations.getMetadata(toParse));
    }

    /**
     * This is the main entry point to parse a string.
     * Strings can be piecewise definitions, matrices, or analytic expressions
     * (mathematical or logical)
     *
     * returns null if couldn't parse
     */
    @Override
    public MathNode parse(String toParse, Map<String, String> meta) throws ParseException {

        MathNode parent = null;
        toParse = toParse.trim();

        // if you get a function with no arguments, assume it's the default args
        if (MathString.isVariable(toParse) && equations.isSymbolDefined(toParse)) {
            toParse = equations.getSignature(toParse);
        }

        // if it's a function, strip spaces so signatures are consistent
        if (MathString.isFunction(toParse)) {
            toParse = toParse.replace(" ", "");
        }

        // error check that a function is defined
        // if this block isn't here, the parse exception is thrown
        // when the expression parser can't parse apart the function name
        // the problem with doing it there is that the parser doesn't know it's a function
        if (MathString.isFunction(toParse)
                && !equations.isSymbolDefined(MathString.getFunctionName(toParse))
                && !factory.isOperatorDefined(MathString.getFunctionName(toParse))) {
            throw new ParseException("Function is not defined: "
                    + MathString.getFunctionName(toParse), 0);
        }


        // if you've parsed this before,
        // and it has the same metadata,
        // get the same node.
        SymbolMetaKey symbolAndMeta = new SymbolMetaKey(toParse, meta);
        if (keysToNode.containsKey(symbolAndMeta)) {
            parent = keysToNode.get(symbolAndMeta);
        } else {

            // if you haven't parsed this before or it has different metadata,
            // parse it now (this is the uncached parse)
            // you should use each parser where possible
            // so that the last parser is the one that actually is the producer
            // of the results (so you can override earlierly added parsers)
            // (say, put user-defined symbols after pre-defined symbols to override them)

            // TODO this should be a Chain of Responsibility pattern
            // or might be a little faster if we reversed the order of if-blocks and
            // used if-else instead of just if's
            if (numberParser.isParsable(toParse)) {
                parent = numberParser.parse(toParse, meta);
            }

            if (piecewiseParser.isParsable(toParse)) {
                parent = piecewiseParser.parse(toParse, meta);
            }

            if (matrixParser.isParsable(toParse)) {
                parent = matrixParser.parse(toParse, meta);
            }

            if (parenParser.isParsable(toParse)) {
                parent = parenParser.parse(toParse, meta);
            }

            if (variableParser.isParsable(toParse)) {
                parent = variableParser.parse(toParse, meta);
            }

            if (predefParser.isParsable(toParse)) {
                parent = predefParser.parse(toParse, meta);
            }

            if (userdefParser.isParsable(toParse)) {
                parent = userdefParser.parse(toParse, meta);
            }
            // if nobody else handled it, assume it's splittable and try it
            if (parent == null) {
                parent = this.parseSplittableExpression(toParse, meta);
            }

            keysToNode.put(symbolAndMeta, parent);

        }

        return parent;
    }

    /**
     * A string that needs to be further split into tokens
     * before it can be parsed. This method splits the string and parses one
     * of the strings into a MathNode which is returned, and passes the rest
     * of the strings back into the parser's main entry point to complete the
     * parse.
     */
    private MathNode parseSplittableExpression(String toParse, Map<String, String> meta) throws ParseException {

        MathNode node = null;
        List<String> tokens = MathString.splitExpression(toParse);

        // if one token couldn't be split:
        // it's a predefined constant or parenthesis group
        // (numbers, functions and variables should get caught before this point)
        // also things like "*x" or "/1" won't be split, so get caught here
        if (tokens.size() == 1) {
            throw new ParseException(toParse + " is not splittable", 0);
        }

        // if two tokens:   first is unary op, second is rhs
        // invalid unary operators like * are not split, so caught before this
        if (tokens.size() == 2) {
            Iterator<String> iterator = tokens.iterator();
            String parentInput = iterator.next(); // first element
            String childInput = iterator.next(); // last element
            node = factory.createNode(toParse, parentInput);
            node.addChild(parse(childInput, meta));
        }

        // if three tokens: middle is binary op, first and third are lhs and rhs
        if (tokens.size() == 3) {
            Iterator<String> iterator = tokens.iterator();
            String lhsInput = iterator.next(); // first element
            String opInput  = iterator.next(); //
            String rhsInput = iterator.next(); // last element

            // catch something like "+ + x" where maybe they meant "1 + x"
            // things lik " x + +" and "x ~ 1" get caught before this,
            // so don't need to check operator or RHS
            if (MathString.isBinOp(lhsInput)) {
                throw new ParseException(lhsInput + " is not a binary operator argument", 0);
            }

            node = factory.createNode(toParse, opInput);
            MathNode lhs = parse(lhsInput, meta);
            MathNode rhs = parse(rhsInput, meta);
            node.addChild(lhs);
            node.addChild(rhs);
        }

        return node;
    }

    /**
     * Returns the names of all variable nodes which are referenced in this
     * node (inclusive) without descending into any DomainTransformations.
     * When there is a DomainTransformation with no arguments,
     * this method also returns that DomainTransformation's
     * default required arguments.
     */
    public Vector<Object> getReferencedVariables(MathNode parent) {

        Vector<Object> results = new Vector<Object>();

        // we found a variable, get it and return
        if (parent instanceof VariableNode) {
            results.add(parent.getID());
        } // function with different arguments
        else if (parent instanceof DomainTransformation && parent.getChildCount() > 1) {
            for (int i = 1; i < parent.getChildCount(); i++) {
                results.addAll(getReferencedVariables(parent.getChild(i)));
            }
        } // default function, get default arguments
        else if (parent instanceof DomainTransformation && parent.getChildCount() == 1) {
            String dtName = MathString.getFunctionName(parent.getID().toString());
            String[] reqArgs = equations.getArguments(dtName);
            results.addAll(Arrays.asList(reqArgs));
        } else {
            for (int i = 0; i < parent.getChildCount(); i++) {
                results.addAll(getReferencedVariables(parent.getChild(i)));
            }
        }

        return results;
    }

    @Override
    public boolean isParsable(String input) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
