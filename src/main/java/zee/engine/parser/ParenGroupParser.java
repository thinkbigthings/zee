

package zee.engine.parser;

import zee.engine.nodes.MathNode;
import java.util.Map;


public class ParenGroupParser implements ParserStrategy {
    
    private ParserStrategy insideParser = null;
    
    public ParenGroupParser(ParserStrategy mainParser) {
        insideParser = mainParser;
    }

    @Override
    public boolean isParsable(String input) {
        return MathString.isParenGroup(input.trim());
    }

    @Override
    public MathNode parse(String toParse, Map<String, String> meta) throws ParseException {
        return insideParser.parse(toParse.substring(1,toParse.length()-1), meta);
    }
}
