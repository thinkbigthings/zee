package zee.engine;

import zee.engine.parser.EquationSet;
import zee.engine.parser.ExpressionParser;
import zee.engine.nodes.MathNodeWrapper;
import zee.engine.nodes.MathNode;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import zee.engine.domain.DomainInterface;
import zee.engine.parser.DomainParser;

/**
 *
 * To construct a complete evaluation, you need a set of functions, 
 * a domain on which to evaluate, and the names of the functions and 
 * variables you want evaluated over that domain.
 * <P>
 * 
 * EQUATION SET 
 * You have a set of functions which are analytic, piecewise, or numeric strings
 * Numeric functions are defined as a matlab-style matrix string.
 * Piecewise functions have format "if s1 then s2, if s3 then s4, else s5" 
 * where each s is a logical or mathematical statement which can be evaluated. 
 * Commas between then/if are optional
 * Analytic function definitions require text to be explicit and 
 * ascii-representable (ie like 5*x not 5x as in standard written notation or 
 * Mathematica). For example: "f(x) = x^2"
 * Variables and function names must start with a letter and may only 
 * contain letters, numbers, and underscores.
 * Can skip function arguments in a definition if the arguments are intended to 
 * be the same as the arguments specified in that function's signature.
 * f(x,y) = - f1  + 2 f1 could be shorthand for f(x,y) = - f1 (x,y) + 2 f1 (x,y)
 * when f1 is defined elsewhere as  f1 (x,y)
 * Constants can be defined such as "a=1" without specifying that a is a function.
 * <P>
 *
 * DOMAIN
 * You have a set of domain variables each of which is defined by a name 
 * and a corresponding set of numbers. The values of each domain variable 
 * may be defined as a matlab-style matrix string. For example: x=[1:10]
 * Any intersection of names between the function set and domain is an error.
 * <P>
 *
 * OUTPUT
 * Output functions may be specified by function name ("f") or by function name 
 * with arguments ("f(x)"). Also you can specify nested functions or arbitrary 
 * arguments ("f(x,y)", "f(x,y1(x))", "f(1,y)"). This provides more flexibility 
 * by allowing you to evaluate top-level functions in different ways without 
 * defining new functions.
 * <P>
 *
 *
 * @see EquationSet
 *
 */
public class EquationProcessor {
   
   private EquationSet eqs = new EquationSet();
   
   public EquationProcessor() {

   }
      
   /** 
    * Creates a new instance of Kernel.
    * Need to specify the set of equation definitions
    *
    * @param defs the set of equations on which to evaluate
    *
    * @see EquationSet
    */
   public EquationProcessor(Map<String,String> defs) throws ParseException {
       for(Map.Entry<String,String> entry : defs.entrySet())
       {
           eqs.addSymbol(entry.getKey(), entry.getValue());
       }
   }

   /** 
    * Creates a new instance of Kernel.
    * Need to specify the set of function definitions
    *
    * @param defs the set of functions on which to evaluate
    * <P>
    * @param meta associated metadata for each function,
    * for example interpolation type for a numerically-defined function
    * The structure of 'meta' is: the function symbol is the key, and all
    * metadata (key-value pairs) for that symbol are contained
    * in the associated hashtable
    *
    * @see EquationSet
    */
   public EquationProcessor( Map<String,String> defs,
                  Hashtable<String,Hashtable<String,String>> meta
                 ) throws ParseException 
   {
       for(Map.Entry<String,String> entry : defs.entrySet())
       {
         String key = entry.getKey();
         String def = entry.getValue();
         Hashtable<String,String> data = meta.get(key);
         eqs.addSymbol(key, def, data);        
      }
   }

   /**
    * This is a convenience "calculator" method.
    * <P>
    * You can't specify any variables or  equations with it. 
    * If you need to evaluate a large set of strings,
    * consider using the evaluate() method to evaluate them all at once, which
    * would be faster than calling this method repeatedly.
    *
    * @param expression a single parsable expression string such as "0.45 * log10(1000)"
    *
    * @return a single value which is the result of the computation.
    *
    */
   public static double evaluateExpression(String expression) throws ParseException {
       EquationProcessor k = new EquationProcessor();
       List<String> output = Arrays.asList(expression);
       Vector<double[]> results = k.evaluate(new HashMap<String,String>(), output);
       return results.get(0)[0];
   }
   /*
   public static Callable<Double> createCallable(final String expression) {
      Callable<Double> callableKernel = new Callable<Double> () {
         @Override
         public Double call() throws ParseException {
            return new Double(Kernel.evaluateExpression(expression));
         }
      };
      return callableKernel;
   }
   */
   /**
    * Returns a Callable that calls the
    * equivalent to evaluate(domainDefs,outputCols,1,true) on another thread
    *
    *   Callable<Vector<double[]>> callableKernel = k.createCallable(defs,cols);
    *   FutureTask<Vector<double[]>> ft = new FutureTask<Vector<double[]>>(callableKernel);
    *   ft.run();
    *   Vector<double[]> r1 = ft.get(); // ft blocks while waiting for this method to return
    */
   /*
   public Callable<Vector<double[]>> createCallable(final Hashtable<String,String> domainDefs, final Vector<String> outputCols) {
      Callable<Vector<double[]>> callableKernel = new Callable<Vector<double[]>> () {
         @Override
         public Vector<double[]> call() throws ParseException {
            return evaluate(domainDefs,outputCols);
         }
      };
      return callableKernel;
   }
      
   public Callable<Vector<double[]>> createCallable(final Hashtable<String,String> domainDefs, 
                                                    final Vector<String> outputCols,
                                                    final int numBlocks, 
                                                    final boolean recombineDomain)
  
      Callable<Vector<double[]>> callableKernel = new Callable<Vector<double[]>> () {
          @Override
         public Vector<double[]> call() throws ParseException {
            return evaluate(domainDefs,outputCols, numBlocks, recombineDomain);
         }
      };
      return callableKernel;
   }
    */

   /**
    * 
    * @param domainDefs
    * @param outputCols
    * @return
    * @throws ParseException
    */
   public Vector<double[]> evaluate(Map<String,String> domainDefs, List<String> outputCols) throws ParseException {
      return evaluate(domainDefs,outputCols,1,true);
   }

   /**
    * like static evaluate (for evaluating a single expression)
    * but can use user-defined equations and variables (which were defined in the constructor).
    *
    * @param toEvaluate string to evaluate, like "1+1"
    * 
    * @return value of the expression
    *
    * @throws ParseException
    */
   public double evaluate(String toEvaluate) throws ParseException {
      return evaluate(new HashMap<String,String>(), Arrays.asList(toEvaluate),1,true).get(0)[0];
   }

   /**
    * Main entry point for evaluating an equation set.
    * <P>
    *
    * Some nodes (sum, min, max) will not return the correct overall answers
    * if you split the domain because they need to function over the ENTIRE
    * domain at one time. This method tests for the presence of nodes that
    * require the whole domain and configures itself to only split the
    * domain (using the numBlocks parameter) if they are not present.
    * <P>
    *
    * NOTE: IT IS VERY EASY TO RUN OUT OF MEMORY FROM THIS METHOD!
    * Some pathological inputs are impossible to recover/retry from
    * (for example, domain variable x=[1:1E10]), 
    * so you don't want to keep trying to recover. Additionally,
    * MemoryPoolMXBean's aren't meant for detecting and recovering 
    * from low memory conditions - at best they are for early warning.
    * See http://www.javaspecialists.co.za/archive/Issue092.html.
    * And the OperatingSystemMXBean and Runtime classes don't offer much
    * practical help either. For now:, detect out of memory errors 
    * and notify user appropriately. There are many pathological cases to run 
    * out of memory and not be able to recover and retry. 
    * The best you can do is make a best attempt to evaluate it 
    * and notify the user if it's not working.
    * <P>
    *
    * Recommend wrapping this method in try{..}catch{OutOfMemoryError e}{}
    *
    * @param domainDefs definition of each domain variable in the format
    * Map<variableName,variableDefinition>. 
    * <P>
    * @param outputCols the expressions to be evaluated.
    * <P>
    * @param numBlocks the number of subdomains in which to split 
    * the specified domain if possible. This is for easing memory usage 
    * and tracking progress. If numBlocks is less than zero, the method
    * attempts to split the domain into blocks each of size 1000.
    * <P>
    * @param recombineDomain flag to tell the Kernel if each domain variable's 
    * values should be recombined with each other domain variable's value,
    * or if the domain values should be interpreted as a set of points. In the
    * latter case, each domain variable's definition must be the same length.
    * <P> 
    * @return the results of the computation. Each element of the returned
    * Vector corresponds to an element from outputCols. 
    */
   public Vector<double[]> evaluate( Map<String,String> domainDefs,
                                     List<String> outputCols,
                                     int numBlocks,
                                     boolean recombineDomain)  throws ParseException
   {

      
      Vector<double[]> output = new Vector<double[]>();
      
      // pre-parse all the required nodes so evaluation time will be more even
      // wrap output in new nodes so parent-based caching is more accurate
      Vector<MathNode> outNodes = new Vector<MathNode>();
      ExpressionParser parser = new ExpressionParser(eqs);
      for(String col : outputCols) {
         MathNode outWrapper = new MathNodeWrapper(null);
         MathNode parsed = parser.parse(col); // meta could be null
         outWrapper.addChild(parsed);
         outNodes.add(outWrapper);
      }
      
      // do basic error checking here
      if( outputCols.size() < 1 ) {
         String msg = "No output columns are specified";
         throw new ParseException(msg,0);
      }
      
      boolean usesDomain = false;
      for(MathNode n : outNodes) {
         // this just catches variables
         // don't worry, functions that don't exist in the eqset are caught
         // when the output nodes are parsed above
         Vector varNodes = parser.getReferencedVariables(n);
         usesDomain |= varNodes.size() > 0;
         for(Object var : varNodes) {
            boolean defined = domainDefs.keySet().contains(var);
            if( ! defined ) {
               String err = var.toString() + " is not defined in the domain";
               throw new ParseException(err,0);
            }
         }
      }

      // some nodes (sum, min, max, depending on how many arguments they have)
      // will not return the correct overall answers
      // if you split the domain because they need to function over the ENTIRE
      // domain at one time. This block tests for the presence of nodes that
      // require the whole domain and configure this method to only split the
      // domain if they are not present
      boolean splittable = true;
      for(MathNode n : outNodes)
         splittable &= n.isSplittable();
      if( ! splittable)
         numBlocks = 1;

      // need to get domain variables for variables that are parsed
      // recombine the domain if the defs are different lengths
      // if same lengths, don't recombine.
      

     DomainInterface d = new DomainParser().getDomain(domainDefs);

     // error check if necessary
     if( usesDomain && ! recombineDomain && ! d.isSameLengthDefs())
     {
         throw new ParseException("Can't assign domain variables with different lengths", 0);
     }

     // these three blocks are for if you're using the domain, and if you're setting explicit points or not
      Set<String> definedVars = domainDefs.keySet();
      if( recombineDomain && usesDomain) {
         for(String defined : definedVars) {
            for(MathNode n : outNodes) {
                if(n.isStringInTree(defined))
                    d = d.recombineVariable(defined);
            }
         }
      }
      if( ! recombineDomain && usesDomain ) {
         for(String defined : definedVars) {
            for(MathNode n : outNodes) {
                if(n.isStringInTree(defined)) {
                   double[] v = d.getDef(defined);
                   d = d.setVariable(defined,v);
                }
            }
         }
      }
      if( ! usesDomain)
      {
         d = d.setVariable("dummyVariableForDomainlessEvaluation",new double[]{0});
         numBlocks = 1;
      }

      // user can specify -1 to automatically split the domain
      // into blocks of size 1000 each, or use 1 block if there 
      // are less than 1000 points.
      if(usesDomain && splittable && (numBlocks <= 0) ) {
         numBlocks = Math.max(1, d.getLength() / 1000 );
      }
      
      // set up the split domain and the final output storage
      Vector<? extends DomainInterface> domainBlocks = d.splitDomain(numBlocks);
      for(int c=0; c < outputCols.size(); c++)
         output.add(new double[d.getLength()]);
     
      /*
      * Tracking of progress can be done by evaluating over subsets of the 
      * specified domain and tracking the progress of domain evaluated, 
      * rather than tracking edges evaluated (which might be less accurate). 
      * This technique lends itself to evaluation in parallel.
      */
      
      // evaluate all the columns
      int progress = 0;
      for(DomainInterface subdomain : domainBlocks) {
          
         // evaluate on the subdomain
         for(int c = 0; c < outNodes.size(); c++) {
            MathNode col = outNodes.get(c);
            double[] subResults = col.evaluate(subdomain);
            double[] allResults = output.get(c);
            System.arraycopy(subResults,0,allResults,progress,subResults.length);
         }

         // clear cache, you won't be using these again
         for(MathNode col : outNodes)
            col.clearCache();

         // need to know where you are
         // if you split the domain
         progress += subdomain.getLength();
         
      }

      // return the output in the order of outputCols
      return output;
   }

}
