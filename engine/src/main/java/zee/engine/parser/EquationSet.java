package zee.engine.parser;

import java.util.*;

/**
 * Given key "f(x,y)=x+y", symbol is f, arguments are [x,y], definition is x+y,
 * and signature is f(x,y) with no spaces.
 *
 */
public final class EquationSet {
   
   private final List<String> symbols = new ArrayList<>();
   private final Map<String, String[]> arguments = new HashMap<>();
   private final Map<String, String> definitions = new HashMap<>();
   private final Map<String, Map<String,String>> meta = new HashMap<>();;
   
   /** Creates a new instance of EquationSet */
   public EquationSet() {

   }
   
   public EquationSet(Map<String,String> symbolsAndDefs) throws ParseException {
      for(String key : symbolsAndDefs.keySet()) {
         addSymbol(key, symbolsAndDefs.get(key));
      }
   }

   public List<String> getAllDomainVariables() {
      Set<String> d = new TreeSet<>();
      for(String symbol : symbols) {
         String[] args = arguments.get(symbol);
         for(int i=0; i < args.length; i++)
            d.add(args[i]);
      }
      return new ArrayList<>(d);
   }

   public List<String> getAllSignatures() {
      List<String> sigs = new ArrayList<>();
      for(String name : symbols)
         sigs.add(getSignature(name));
      return sigs;
   }
   
   /**
    * returns false if name is null or if name is not contained in the symbol
    * list.
    */
   public boolean isSymbolDefined(String name) {
      if(name == null)
         return false;
      return symbols.contains(name);
   }
   
   public List<String> getAllSymbols() {
      return new ArrayList<>(symbols);
   }
   
   /**
    * reconstructs the signature of a function which was added.
    * If a function was added without arguments (ie it's a constant)
    * then just the function name is returned.
    */
   public String getSignature(String symbol) {
      StringBuffer signature = new StringBuffer();
      String retVal = null;
      signature.append(symbol);
      String[] args = getArguments(symbol);
      if(args.length > 0) {
         signature.append("(");
         for(int s = 0; s < args.length; s++)
            signature.append(args[s]+",");
         signature.append(")");
         retVal = signature.toString().replace(",)", ")");
      }
      else {
         retVal = signature.toString();
      }
      
      return retVal;
   }
   
   /**
    * after calling addSymbol("f(x,y)","x+y") ,
    * then getArguments("f") returns ["x" "y"]
    * after calling addSymbol("a","1") ,
    * then getArguments("a") returns String[0]
    */
   public String[] getArguments(String symbol) {
      return arguments.get(symbol);
   }
   
   /**
    * after calling addSymbol("f(x,y)","x+y") ,
    * then getDefinition("f") returns "x+y"
    * if symbol is not in this set, returns null
    */
   public String getDefinition(String symbol) {
      return definitions.get(symbol);
   }
   
   /**
    * If there's no metadata for this symbol,
    * return an empty table.
    * 
    * Metadata is a set of key/value strings for each function
    * 
    * @param symbol
    * @return a Map of metadata
    */
   public Map<String,String> getMetadata(String symbol) {
      Map<String,String> values = meta.get(symbol);
      if( values == null)
          values = new HashMap<>();
      return values;
   }
   
   public String getMetadata(String symbol, String key) {
      String value = null;
      Map<String,String> data = meta.get(symbol);
      if(data != null)
         value = data.get(key);
      return value;
   }
   
   public void addSymbol(   String signature,
                            String def, 
                            Map<String,String> metaData) throws ParseException 
   {
      
      String symbol = MathString.getFunctionName(signature.trim());
      if( ! symbols.contains(symbol)) {
         addSymbol(signature,def);
         if(metaData != null)
         {
            meta.put(symbol, metaData);
            if(MathString.isMatrix(def))
                addNumericFunctionMetadata(signature);
         }
      }
      
   }

   
   /**
    * addSymbol("f(x,y)","x+y") adds the symbol "f" with arguments ["x" "y"]
    * and definition "x+y". 
    * 
    * addSymbol("a", "1") adds the symbol "a" with arguments "" and
    * definition "1"
    *
    * if the symbol is already present in this equation set, this method fails
    */
   public void addSymbol(String signature, String def) throws ParseException {

      signature = signature.trim();
      
      // default if it has no function arguments, for example, "a"
      String sym = MathString.getFunctionName(signature);
      String[] argArray = new String[0];

      // if it has function arguments, for example, "f(x,y)"
      if(signature.indexOf("(") != -1)
         argArray = MathString.getFunctionArgs(signature);
      
      // check that the argument names don't overlap a function name
      List<String> argList = Arrays.asList(argArray);
      Set<String> intersection = new HashSet<String>(symbols);
      intersection.retainAll(argList);
      if( intersection.size() == 1) {
         String msg = sym + " has an signature argument " + intersection 
                     + " which is already a defined function";
         throw new ParseException(msg);
      }
      if( intersection.size() > 1) {
         String msg = sym + " has arguments " + intersection 
                     + " in the signature which are already defined functions";
         throw new ParseException(msg);
      }
      
      
      // check that the new function name don't overlap an existing arg name
      if( getAllDomainVariables().contains(sym)) {
         String msg = sym + " can't be used as a function, "
                 + "it is already defined as a domain variable";
         throw new ParseException(msg);
      }

        if(MathString.isMatrix(def))
            addNumericFunctionMetadata(signature);

      
      // if symbol is already present, don't add it
      if( ! symbols.contains(sym)) {
         symbols.add(sym);
         arguments.put(sym,argArray);
         definitions.put(sym,def);
      }

   }

    private void addNumericFunctionMetadata(String signature) {

      signature = signature.trim();

      // default if it has no function arguments, for example, "a"
      String sym = MathString.getFunctionName(signature);
      String[] argArray = new String[0];

      // if it has function arguments, for example, "f(x,y)"
      if(signature.indexOf("(") != -1)
         argArray = MathString.getFunctionArgs(signature);

        Map<String, String> symMeta = getMetadata(sym);
        if (argArray.length == 1) {
            symMeta.put(MatrixParser.INDEPENDANT_VARIABLE, argArray[0]);
        }
        if (argArray.length == 2) {
            symMeta.put(MatrixParser.INDEPENDANT_VARIABLE_1, argArray[0]);
            symMeta.put(MatrixParser.INDEPENDANT_VARIABLE_2, argArray[1]);
        }
        meta.put(sym, symMeta);
    }
   
   
}
