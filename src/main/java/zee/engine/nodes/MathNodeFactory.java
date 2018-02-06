package zee.engine.nodes;


import zee.engine.parser.ParseException;

public class MathNodeFactory {

   public MathNodeFactory() {  }
   
   public MathNode createNode(final Object id, final String input) throws ParseException
   {
      String inputLowerCase = input.toLowerCase();
      switch(inputLowerCase) {
         case "e":
            return new ENode(id);
         case "pi":
            return new PiNode(id);
         case "rand":
            return new RandomNode(id);
         case "random":
            return new RandomNode(id);
         case "mod":
            return new ModNode(id);
         case "%":
            return new ModNode(id);
         case "sum":
            return new SumNode(id);
         case "cumsum":
            return new CumSumNode(id);
         case "abs":
            return new AbsNode(id);
         case "floor":
            return new FloorNode(id);
         case "ceil":
            return new CeilingNode(id);
         case "ceiling":
            return new CeilingNode(id);
         case "round":
            return new RoundNode(id);
         case "max": 
            return new MaxNode(id);
         case "epsilonequals": 
            return new EpsilonEqualsNode(id);
         case "not": 
            return new NotNode(id);
         case "min": 
            return new MinNode(id);
         case "sqrt": 
            return new SqrtNode(id);
         case "ln": 
            return new NaturalLogNode(id);   
         case "log10": 
            return new Log10Node(id);
         case "log2": 
            return new Log2Node(id);
         case "exp": 
            return new ExpNode(id);   
         case "todegrees": 
            return new ToDegreesNode(id);
         case "rad2deg": 
            return new ToDegreesNode(id);   
         case "toradians": 
            return new ToRadiansNode(id);
         case "deg2rad": 
            return new ToRadiansNode(id);
         case "+": 
            return new PlusNode(id);
         case "-": 
            return new MinusNode(id);   
         case "*": 
            return new TimesNode(id);
         case "/": 
            return new DividedByNode(id);
         case "^": 
            return new PowerNode(id);
         case "sin": 
            return new SinNode(id);   
         case "cos": 
            return new CosNode(id);
         case "tan": 
            return new TanNode(id);
         case "<": 
            return new LessThanNode(id);
         case "<=": 
            return new LTENode(id);
         case "==": 
            return new EqualsNode(id);   
         case ">=": 
            return new GTENode(id);
         case ">": 
            return new GreaterThanNode(id);
         case "&": 
            return new AndNode(id);
         case "|": 
            return new OrNode(id);
         case "sinh": 
            return new SinhNode(id);
         case "cosh": 
            return new CoshNode(id);
         case "tanh": 
            return new TanhNode(id);
         case "sind": 
            return new SinDegreesNode(id);
         case "cosd": 
            return new CosDegreesNode(id);
         case "asin": 
            return new AsinNode(id);
         case "acos": 
            return new AcosNode(id);
         case "acosd": 
            return new AcosDegreesNode(id);
         case "asind": 
            return new AsinDegreesNode(id);
         case "atan": 
            return new AtanNode(id);
         case "cot": 
            return new CotNode(id);
         case "sec": 
            return new SecNode(id);
         case "csc": 
            return new CscNode(id);
         default:
            throw new ParseException("Operation is not defined: " + input);
      }
   }

   public boolean isOperatorDefined(String op) 
   {
      try { 
         createNode(null, op); 
      }
      catch(ParseException pe) {
         return false;
      }
      return true;
   }
   
}
