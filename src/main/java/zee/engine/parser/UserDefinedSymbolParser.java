
package zee.engine.parser;

import zee.engine.nodes.MathNodeFactory;
import zee.engine.nodes.MathNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import zee.engine.nodes.DomainTransformation;
import zee.engine.nodes.VariableNode;

public class UserDefinedSymbolParser implements ParserStrategy {

    private final EquationSet equations; // no setter methods are called in this class
    private final ParserStrategy mainParser;
    private final MathNodeFactory factory = new MathNodeFactory();
    
    public UserDefinedSymbolParser(EquationSet eqset, ParserStrategy ps) {
        equations = eqset;
        mainParser = ps;
    }
    
    @Override
    public boolean isParsable(String input) {
        return equations.isSymbolDefined(MathString.getFunctionName(input))
            && (MathString.isFunction(input) || MathString.isVariable(input));
    }

    
   /**
    * 
    * 
    * 
    * @param toParse defined function means it is user-defined in the
    * equation set.
    * 
    * @param meta is ignored here, the parsed function's stored metadata is
    * passed down to parsed strings in the function's body, and the default
    * parse() method is used for arguments (since metadata for a function
    * shouldn't be applied to it's arguments). 
    * 
    * For example:
    * What if you have 
    * g1(x) = [numeric linear function],  
    * g2(x) = [same data but cubic linear function], 
    * f(x) = [nearest function],  
    * then you call f(g1) and call f(g2). 
    * 
    * f's metadata isn't applied to g1 or g2 since they're not called 
    * from f's function definition.
    * 
    * @return a MathNode representing this string
    * 
    * @throws ParseException
    */
   @Override
    public MathNode parse(String toParse, Map<String, String> meta) throws ParseException {
       
       DomainTransformation parent = null;
       
        if(MathString.isFunction(toParse)) {
              // first get the name and the arguments
              // the definition associated with this function
              // could be a matrix, piecewise, or expression
              String symbol = MathString.getFunctionName(toParse);
              String[] args = MathString.getFunctionArgs(toParse);
              Map<String,String> newmeta = equations.getMetadata(symbol);
              String def = equations.getDefinition(symbol);

              if(factory.isOperatorDefined(symbol)) {
                 String message = "The predefined function " + symbol + " is being over-ridden.";
                 //Logger klogger = LogManager.getLogManager().getLogger("zee.engine");
                 //klogger.log(Level.WARNING, message);
              }

              // check that the number of arguments is correct
              int reqArgLength = equations.getArguments(symbol).length;
              if(args.length != reqArgLength) {
                 String err = symbol + " has " + args.length 
                         + " arguments instead of  " + reqArgLength
                         + " as defined by its signature";
                 throw new ParseException(err);
              }

              // parent is the defined function in equation set
              // first child is what the new domain is passed to
              // other children are nodes for transforming the domain
              // (or none if no transformation is required)
              parent = new DomainTransformation(toParse);
              String[] defArgs = equations.getArguments(symbol);
              MathNode child = mainParser.parse(def, newmeta);
              parent.addChild( child );

              // do some error checking here related to function signature.
              // before, was only doing this if it wasn't a numeric function
              // so this was part of the conditions, but I don't know why
              // it was passing the tests without it, so I took it out.
              // but if something starts failing later on, you can put this back
              /*   
              if(   ! (child instanceof NumericFunction2D) 
                 && ! (child instanceof NumericFunction1D) 
                 && defArgs.length > 0 )
              {
              */    
              if( defArgs.length > 0 )
              {

                 // to check the definition, you'll need the variables defined
                 // in this definition directly (ie don't descend into any 
                 // domain transformation nodes)
                 List<Object> variables = getReferencedVariables(parent);
                 List<String> requiredVars = new ArrayList<String>(Arrays.asList(defArgs));

                 // Make sure variables used in the definition
                 // are all defined in the signature
                 if( ! requiredVars.containsAll(variables) ) {
                    variables.removeAll(requiredVars);
                    String err = symbol + "'s definition "
                            + "uses undefined variables: " + variables;
                    throw new ParseException(err);
                 }


                 // Make sure variables defined in the signature
                 // are all used in the definition
                 if( ! variables.containsAll(requiredVars) ) {
                    requiredVars.removeAll(variables);
                    String err = symbol + "'s definition "
                            + "does not use arguments: " + requiredVars;
                    throw new ParseException(err);
                 }
              }

              // if the arguments being passed in are the same as what is defined
              // in this function's signature, don't add any other nodes to this
              // domain transformation.
              for(int i=0; i < defArgs.length; i++) {
                 parent.addTransformation(defArgs[i], mainParser.parse(args[i], meta));
              }
        }
        else if(MathString.isVariable(toParse)) {
              String symbol = MathString.getFunctionName(toParse);
              String def = equations.getDefinition(symbol);

              if(factory.isOperatorDefined(symbol)) {
                 String message = "The predefined function " + symbol
                             + " is being over-ridden.";
                 //Logger klogger = LogManager.getLogManager().getLogger("zee.kernel");
                 //klogger.log(Level.WARNING, message);
              }

              // If toParse doesn't have arguments specified, and the symbol is defined 
              // in the equation set, and the equation set defines arguments,
              // it's a default function reference. Assign this node to a default
              // DomainTransformation node.
              parent = new DomainTransformation(toParse);
              parent.addChild(mainParser.parse(def, meta));
        }
       
       return parent;
       
   }
   
   /**
    * Returns the names of all variable nodes which are referenced in this 
    * node (inclusive) without descending into any DomainTransformations.
    * When there is a DomainTransformation with no arguments,
    * this method also returns that DomainTransformation's
    * default required arguments.
    */
   public List<Object> getReferencedVariables(MathNode parent) {
      
      List<Object> results = new ArrayList<Object>();
      
      // we found a variable, get it and return
      if(parent instanceof VariableNode ) {
         results.add(parent.getID());
      }
      // function with different arguments
      else if(parent instanceof DomainTransformation && parent.getChildCount() > 1) {
         for(int i=1; i < parent.getChildCount(); i++)
            results.addAll(getReferencedVariables(parent.getChild(i)));
      }
      // default function, get default arguments
      else if(parent instanceof DomainTransformation && parent.getChildCount() == 1) {
         String dtName =MathString.getFunctionName(parent.getID().toString());
         String[] reqArgs = equations.getArguments(dtName);
         results.addAll(Arrays.asList(reqArgs));
      }
      else {
         for(int i=0; i < parent.getChildCount(); i++)
            results.addAll(getReferencedVariables(parent.getChild(i)));
      }
     
      return results;
   }

}
