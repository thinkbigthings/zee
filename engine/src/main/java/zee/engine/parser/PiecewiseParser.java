
package zee.engine.parser;

import zee.engine.nodes.MathNode;
import java.util.Map;
import zee.engine.nodes.PiecewiseNode;

public class PiecewiseParser implements ParserStrategy {

    private ParserStrategy mainParser;
    
    public PiecewiseParser(ParserStrategy parser) {
        mainParser = parser;
        
    }
    
    @Override
    public boolean isParsable(String input) {
        return MathString.isPiecewise(input);
    }

    @Override
    public MathNode parse(String input, Map<String, String> meta) throws ParseException {

      // piecewise node
      PiecewiseNode parent = new PiecewiseNode(input);
      
      if(input.toLowerCase().startsWith("if ")) {
         input = input.substring(3);
         input = input.replace(", if "," if ");
         input = input.replace(",if "," if ");
         input = input.replace(", then "," then ");
         input = input.replace(",then "," then ");
         input = input.replace(", else "," else ");
         input = input.replace(",else "," else ");
         String[] tokenized = input.split("(\\sif\\s)|(\\sthen\\s)|(\\selse\\s)");
         int i=0;
         while(i < tokenized.length-1) {
            parent.addChild(mainParser.parse(tokenized[i], meta));   // condition
            parent.addChild(mainParser.parse(tokenized[i+1], meta)); // result
            i += 2;
         }
         if(i == tokenized.length-1) {
            parent.addChild(mainParser.parse(tokenized[i], meta));   // result of else
         }
      }

      return parent;
    }

}
