
package zee.engine.parser;

import zee.engine.nodes.MathNode;
import java.util.Map;
import zee.engine.nodes.VariableNode;

public class VariableParser implements ParserStrategy {

    @Override
    public boolean isParsable(String input) {
        return MathString.isVariable(input);
    }

   /**
    * 
    * 
    * @param toParse a String that's not defined as a function in the eqset
    * so presumably it's a variable name. 
    * 
    * @param meta
    * 
    * @return a variable node when you parse a variable 
    * that is defined in a domain
    * 
    * @throws ParseException
    */
   @Override
   public MathNode parse(String toParse, Map<String,String> meta) throws ParseException {
      return new VariableNode(toParse);
   }


}
