
package zee.engine.parser;

import zee.engine.nodes.MathNode;
import java.util.Map;
import zee.engine.nodes.NumberNode;


public class NumberParser implements ParserStrategy {

    @Override
    public boolean isParsable(String input) {
        return MathString.isNumber(input);
    }

    @Override
    public MathNode parse(String input, Map<String, String> meta) throws ParseException {
        return new NumberNode(input);
    }

}
