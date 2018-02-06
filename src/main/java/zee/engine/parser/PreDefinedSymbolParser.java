
package zee.engine.parser;

import java.util.Map;
import zee.engine.nodes.MathNode;
import zee.engine.nodes.MathNodeFactory;


public class PreDefinedSymbolParser implements ParserStrategy {

    private MathNodeFactory factory = new MathNodeFactory();
    private ParserStrategy mainParser;
    
    public PreDefinedSymbolParser(ParserStrategy argParser) {
        mainParser = argParser;
    }
    
    @Override
    public boolean isParsable(String input) {
        return (MathString.isVariable( input ) || MathString.isFunction( input ) ) 
                && factory.isOperatorDefined(MathString.getFunctionName(input));
    }

    
   /**
    * 
    * 
    * 
    * @param toParse a function name that's defined internally to the software
    * such as sin() or max(), or parse a predefined variable name like e, pi, or rand
    * 
    * @param meta is applied here, but generally not used.
    * the default
    * parse() method is used for arguments (since metadata for a function
    * shouldn't be applied to it's arguments). For example:
    * What if you have 
    * g1(x) = [numeric linear function],  
    * g2(x) = [same data but cubic linear function], 
    * f(x) = [nearest function],  
    * then you call f(g1) and call f(g2). 
    * f's metadata isn't applied to g1 or g2 since they're not called 
    * from f's function definition.
    * 
    * @return a MathNode representing this string
    * 
    * @throws ParseException
    */
   @Override
    public MathNode parse(String toParse, Map<String, String> meta) throws ParseException {
       MathNode parent = null;
       
        if(MathString.isFunction(toParse)) {
            // first get the name and the arguments
            // the definition associated with this function
            // could be a matrix, piecewise, or expression
            String symbol = MathString.getFunctionName(toParse);
            String[] args = MathString.getFunctionArgs(toParse);

            // looks like we know it's a standard operator (sin, etc)
            // so can parse it now
            parent = factory.createNode(toParse, symbol);
            for(int i=0; i < args.length; i++)
                parent.addChild(mainParser.parse(args[i], meta));

            return parent;
        }
        else { 
            // otherwise, always variable (MathString.isVariable(toParse))
            // because of previous call to isParsable()
            parent = factory.createNode(toParse, toParse);
        }
       
       return parent;
       
   }
}
