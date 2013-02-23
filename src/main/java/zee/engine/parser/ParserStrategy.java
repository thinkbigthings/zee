package zee.engine.parser;

import zee.engine.nodes.MathNode;
import java.text.ParseException;
import java.util.Map;

/**
 *
 */
public interface ParserStrategy {
    
    public boolean isParsable(String input);
    
    public MathNode parse(String input, Map<String,String> meta) throws ParseException;
    
}
