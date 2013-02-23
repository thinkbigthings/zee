package zee.engine.parser;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class MathString {

    public final static String number = "((-*|\\+?)((\\d*\\.?\\d+)|(\\d+\\.?\\d*)))";
    public final static String sciNum = "(" + number + "([Ee]" + number + ")?)";
    public final static String name = "([a-zA-Z_])([\\w])*";
    public final static String logicOp = "(<=?)|(==?)|(>=?)|(\\|)|(&)";
    public final static String implicitMatrix =
            "("
            + "\\["
            + "(" + sciNum + ":" + sciNum + ")"
            + "(" + ":" + sciNum + ")?"
            + "\\]"
            + ")";
    public final static String explicitMatrixRow =
            "(" + "([\\s]*)" + sciNum + "([\\s,]+)" + ")*"
            + "(" + sciNum + "([\\s]*)" + ")";
    public final static String explicitMatrix =
            "("
            + "\\["
            + explicitMatrixRow
            + "\\]"
            + ")";
    public final static String explicitMatrix2d =
            "("
            + "\\["
            + "(" + explicitMatrixRow + "([\\s;]+)" + ")*"
            + "(" + explicitMatrixRow + ")"
            + "\\]"
            + ")";
    public final static String matrixPattern =
            "(" + explicitMatrix
            + "|" + implicitMatrix
            + "|" + explicitMatrix2d
            + ")";
    public final static String functionStart = name + "(\\()"; // name(
    public final static Pattern functionStartPattern = Pattern.compile(functionStart);
    public final static Pattern namePattern = Pattern.compile(name);
    public final static Pattern logicPattern = Pattern.compile(logicOp);
    public final static Pattern numberPattern = Pattern.compile(sciNum);

    /**
     * Given a String and the position of a left parenthesis,
     * return the position of the parenthesis which matches the specified paren.
     */
    public static int getMatchingParenthesis(String expression, int position) {
        int matchPosition = -1;
        int countLeftParens = 1;
        for (int i = position + 1; i < expression.length(); i++) {
            String current = expression.substring(i, i + 1);
            if (current.equals("(")) {
                countLeftParens++;
            }
            if (current.equals(")")) {
                countLeftParens--;
            }
            if (countLeftParens == 0) {
                matchPosition = i;
                break;
            }
        }
        return matchPosition;
    }

    /**
     * Splits an analytic expression (either mathematical or logical)
     */
    public static List<String> splitExpression(String input) throws ParseException {

        List<String> tokens = new ArrayList<String>();

        while (input.length() > 0) {

            // strip whitespace as you scan it
            // commas could be stripped here too but they are already
            // removed in ExpressionParser.parsePiecewise before getting
            // to this method
            if (Pattern.matches("\\s", input.substring(0, 1))) {
                input = input.substring(1);
                continue;
            }

            // get a single-character binary operator
            if (input.startsWith("*")
                    || input.startsWith("/")
                    || input.startsWith("%")
                    || input.startsWith("+")
                    || input.startsWith("-")
                    || input.startsWith("^"))
            {
                tokens.add(input.substring(0, 1));
                input = input.substring(1);
                continue;
            }

            Matcher matcher;

            // find a logical operator
            matcher = logicPattern.matcher(input);
            if (matcher.find() && matcher.start() == 0) {
                String found = matcher.group();
                tokens.add(found);
                input = input.substring(found.length());
                continue;
            }

            // get a number
            matcher = numberPattern.matcher(input);
            if (matcher.find() && matcher.start() == 0) {
                String found = matcher.group();
                tokens.add(found);
                input = input.substring(found.length());
                continue;
            }

            // get a function
            // must start with a letter or underscore
            matcher = functionStartPattern.matcher(input);
            if (matcher.find() && matcher.start() == 0) {
                int lastParen = getMatchingParenthesis(input, input.indexOf("("));
                if (lastParen < 1) {
                    String err = "Function not closed, matching parenthesis not found in " + input;
                    throw new ParseException(err, 0);
                }
                String found = input.substring(0, lastParen + 1);
                tokens.add(found);
                input = input.substring(found.length());
                continue;
            }

            // get a variable
            // must start with a letter or underscore
            matcher = namePattern.matcher(input);
            if (matcher.find() && matcher.start() == 0) {
                String foundStr = matcher.group();
                tokens.add(foundStr);
                input = input.substring(foundStr.length());
                continue;
            }

            // get a parenthesis group
            if (input.startsWith("(")) {
                int match = getMatchingParenthesis(input, 0);
                if (match < 1) {
                    String err = "Matching parenthesis not found in " + input;
                    throw new ParseException(err, 0);
                }
                tokens.add(input.substring(0, match + 1));
                input = input.substring(match + 1);
                continue;
            }

            // if you get here then you have a syntax error
            String err = "Can't parse: " + input;
            throw new ParseException(err, 0);
        }

        if (tokens.isEmpty()) {
            throw new ParseException("Can't parse: " + input, 0);
        }

        // if you have a binary op, split around that
        // if not and you have a unary op, split around that
        // otherwise don't split it
        int opIndex = findBinaryOperator(tokens);
        List<String> splitTokens = new ArrayList<String>();
        // if it's a binary operator
        if (opIndex != -1) {
            splitTokens.add(concatStrings(tokens.subList(0, opIndex)));
            splitTokens.add(tokens.get(opIndex));
            splitTokens.add(concatStrings(tokens.subList(opIndex + 1, tokens.size())));
        } else {

            // if it's a negation, split as a unary operator
            if (isUnaryOp(tokens.get(0))) {
                splitTokens.add(tokens.get(0));
                splitTokens.add(concatStrings(tokens.subList(1, tokens.size())));
            }

            // if you haven't determined what it is yet,
            // it's probably a number or variable
            if (splitTokens.isEmpty()) {
                splitTokens.add(concatStrings(tokens));
            }
        }

        return splitTokens;
    }

    private static String concatStrings(List tokens) {
        StringBuffer string = new StringBuffer();
        Iterator strings = tokens.iterator();
        while (strings.hasNext()) {
            string.append(strings.next().toString());
        }
        return string.toString();
    }

    public final static double[] parseMatrixPiece(String toParse) throws NumberFormatException {
        double[] d = new double[0];

        toParse = toParse.replace('[', ' ');
        toParse = toParse.replace(']', ' ');
        toParse = toParse.trim();

        // format "1:0.1:2" or "1:5"
        if (toParse.indexOf(":") != -1) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(":");
            String[] n = p.split(toParse);
            // use BigDecimals to avoid machine error and roundoff error
            // when calculating the double at each step
            BigDecimal min = null;
            BigDecimal step = null;
            BigDecimal max = null;
            if (n.length == 3) {
                min = new BigDecimal(n[0]);
                step = new BigDecimal(n[1]);
                max = new BigDecimal(n[2]);
            } else if (n.length == 2) {
                min = new BigDecimal(n[0]);
                step = new BigDecimal("1");
                max = new BigDecimal(n[1]);
            } else {
                // exceptional condition
                String err = "Can't parse around multiple colons: " + toParse;
                throw new NumberFormatException(err);
            }
            Vector<BigDecimal> values = new Vector<BigDecimal>();
            for (BigDecimal cur = min; cur.compareTo(max) <= 0; cur = cur.add(step)) {
                values.add(cur);
            }
            d = new double[values.size()];
            for (int s = 0; s < values.size(); s++) {
                d[s] = values.get(s).doubleValue();
            }
        } else { // format "1" (just a number)
            d = new double[]{Double.parseDouble(toParse)};

        }

        return d;
    }

    /**
     * Parses a matlab-style array declaration into a string array.
     * Valid strings include things like "[1,2]" "[1 2]" or
     * "[1:0.1:2]" or "[1:5]" or "[1 2:5 6:1:9 10]"
     */
    public final static double[] toDoubleArray(String toParse) throws NumberFormatException {
        double[] d = new double[0];

        toParse = toParse.replace('[', ' ');
        toParse = toParse.replace(']', ' ');
        toParse = toParse.trim();

        java.util.regex.Pattern p = java.util.regex.Pattern.compile("[,\\s]+");

        String[] pieces = p.split(toParse);
        for (int i = 0; i < pieces.length; i++) {
            double[] piece = parseMatrixPiece(pieces[i]);
            double[] newd = new double[piece.length + d.length];
            System.arraycopy(d, 0, newd, 0, d.length);
            System.arraycopy(piece, 0, newd, d.length, piece.length);
            d = newd;
        }

        return d;
    }

    public static boolean isMatrix(String input) {
        boolean hasBrackets = false;
        if (input.length() >= 3) {
            hasBrackets = input.charAt(0) == '[' && input.charAt(input.length() - 1) == ']';
        }
        return hasBrackets;

        // this is too slow
        //return Pattern.matches(matrixPattern,input);
    }

    public static boolean isNumber(String input) {
        return Pattern.matches(sciNum, input);
    }

    public static boolean isParenGroup(String input) {

        // the current implementation of isParenGroup checks that the outside
        // parentheses actually match to each other

        input = input.trim();
        boolean ispg = false;
        if (input.startsWith("(") && input.endsWith(")")) {
            int closer = MathString.getMatchingParenthesis(input, 0);
            ispg = (closer == input.length() - 1);
        }

        return ispg;
    }

    public static String getFunctionName(String signature) {
        String fname = null;
        if (signature.contains("(")) {
            fname = signature.substring(0, signature.indexOf("("));
        }
        if (isVariable(signature)) { // so you can parse "f" or "f(x)"
            fname = signature;
        }
        return fname;
    }

    /**
     * If you pass in a function name like "f" instead of "f(x)"
     * then it returns an array of length zero
     */
    public static String[] getFunctionArgs(String call) {
        String[] args = new String[0];
        call = call.trim();
        if (isFunction(call)) {
            call = call.substring(call.indexOf("(") + 1, call.length() - 1);
            if (!call.contains(",")) {
                args = new String[]{call};
            } else {
                // parse around commas that aren't inside parens
                Vector<String> argv = new Vector<String>();
                int curIndex = 0;
                for (int i = 1; i < call.length(); i++) {
                    if (call.substring(i, i + 1).equals("(")) {
                        i = MathString.getMatchingParenthesis(call, i);
                    }
                    if (call.substring(i, i + 1).equals(",")) {
                        argv.add(call.substring(curIndex, i).trim());
                        curIndex = i + 1;
                        i++;
                    }
                    if (i == call.length() - 1) {
                        argv.add(call.substring(curIndex).trim());
                    }
                }
                args = argv.toArray(args);
            }
        }

        return args;
    }

    public static boolean isUnaryOp(String input) {

        return (input == null || input.equals("-"));
    }

    public static boolean isBinOp(String input) {

        return (input == null
                || input.equals("+")
                || input.equals("-")
                || input.equals("*")
                || input.equals("/")
                || input.equals("^")
                || input.equals("%")
                || input.equals("<")
                || input.equals("<=")
                || input.equals("==")
                || input.equals(">")
                || input.equals(">=")
                || input.equals("&")
                || input.equals("|"));
    }

    public static boolean isFunction(String input) {
        input = input.trim();
        boolean matches = false;
        Matcher matcher = functionStartPattern.matcher(input);
        if (matcher.find()
                && matcher.start() == 0
                && getMatchingParenthesis(input, input.indexOf("(")) == input.length() - 1) {
            matches = true;
        }
        return matches;
    }

    public static boolean isVariable(String input) {
        return Pattern.matches(name, input);
    }

    public static boolean isPiecewise(String input) {
        return input.trim().toLowerCase().startsWith("if ");
    }

    public static int nrows(String m) {
        assert isMatrix(m);
        return m.split(";").length;
    }

    public static int ncols(String m) {
        assert isMatrix(m);
        Pattern p = java.util.regex.Pattern.compile("[,\\s]+");
        if (m.indexOf(";") > 0) {
            m = m.substring(0, m.indexOf(";")); // get first row
            m = m.substring(1).trim(); // get rid of leading "["
        } else {
            m = m.substring(1, m.length() - 1).trim(); // get rid of leading "[" and closing "["
        }
        int length = p.split(m).length;
        return length;
    }

    /**
     * Parses a matlab-style array declaration into a string array
     * of the format [a1 a2; b1 b2] or [a1,a2; b1, b2 ] etc.
     * Note that a DefaultTableModel can be constructed with the returned array
     * like DefaultTableModel(toStringTable(toParse),new String[]{"h1","h2"});
     */
    public static String[][] toStringTable(String toParse) {
        int numcols = ncols(toParse);
        toParse = toParse.replace("[", " ");
        toParse = toParse.replace("]", " ");
        toParse = toParse.trim();
        Pattern p = java.util.regex.Pattern.compile("[,\\s]+");
        String[] rows = toParse.split(";");
        String[][] table = new String[rows.length][numcols];
        for (int r = 0; r < rows.length; r++) {
            rows[r] = rows[r].trim();
            String[] row = p.split(rows[r]);
            for (int c = 0; c < row.length; c++) {
                table[r][c] = row[c];
            }
        }
        return table;
    }

    public static double[][] toDoubleTable(String toParse) throws NumberFormatException {
        String[][] strings = toStringTable(toParse);
        int numrows = strings.length;
        int numcols = strings[0].length;
        double[][] doubles = new double[numrows][numcols];
        for (int r = 0; r < numrows; r++) {
            for (int c = 0; c < numcols; c++) {
                if (strings[r][c].equalsIgnoreCase("NaN")) {
                    doubles[r][c] = Double.NaN;
                } else {
                    doubles[r][c] = Double.parseDouble(strings[r][c]);
                }
            }
        }

        return doubles;
    }

    /**
     * Returns index of rightmost lowest-precedence operator
     * or -1 if none is found
     * Looks for -, +, % /, *, ^
     * Rightmost operator ensures that equal-precendence operators
     * are evaluated left to right
     */
    public static int findBinaryOperator(List tokens) {
        int index = -1;

        // logic has lower precedence
        index = tokens.lastIndexOf("&");
        if (index == -1) {
            index = tokens.lastIndexOf("|");
        }
        if (index == -1) {
            index = tokens.lastIndexOf("<");
        }
        if (index == -1) {
            index = tokens.lastIndexOf("<=");
        }
        if (index == -1) {
            index = tokens.lastIndexOf("==");
        }
        if (index == -1) {
            index = tokens.lastIndexOf(">=");
        }
        if (index == -1) {
            index = tokens.lastIndexOf(">");
        }

        // minus and/or negation
        if (index == -1) {
            index = tokens.lastIndexOf("-");
        }

        // TODO handle --x and ---5...
        if (index != -1 && (index == 0 || isBinOp(tokens.get(index - 1).toString()))) {
            index = index - 1;
            // allow for nested unary operators (say, "--5")
            while(index > 0 && isUnaryOp(tokens.get(index-1).toString())) {
                index --;
            }
            if(index == 0 && isUnaryOp(tokens.get(index).toString())) {
                index --;
            }
        }
        // then binary operators
        // this allows + and - to have equal precedence but evaluate left to right
        index = Math.max(index, tokens.lastIndexOf("+"));

        if (index == -1) {
            index = tokens.lastIndexOf("%");
        }
        if (index == -1) {
            index = tokens.lastIndexOf("/");
        }
        if (index == -1) {
            index = tokens.lastIndexOf("*");
        }
        if (index == -1) {
            index = tokens.lastIndexOf("^");
        }

        return index;
    }
}
