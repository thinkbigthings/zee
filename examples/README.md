# ZEE Samples

This is a set of sample code for using the Z Evaluation Engine. ZEE allows you to do everything from define and evaluate simple expressions and variables, to define sets of functions and evaluate them together over the domain of your choice.

```
// simple expression evaluation
String toParse = "log10(1000) / (1.4 + 1.6)";
double result = EquationProcessor.evaluateExpression(toParse);
System.out.println(result);      // writes out "1.0"
```

```
// simple expression with variables
Map<String, String> variables = new HashMap<String, String>() {{ put("a","2"); }};
EquationProcessor k = new EquationProcessor(variables);
System.out.println(k.evaluate("2 + a"));      // writes "4"
```

```
// define a set of functions
Map<String,String> eqs = new HashMap<String,String>();
eqs.put("piApprox(k)","8*cumsum(ChebyshevTerm(k))");
eqs.put("ChebyshevTerm(k)","( (-1)^k * (sqrt(2)-1)^(2*k+1)) / (2*k+1)");
eqs.put("absError(k)", "abs(pi-piApprox)");
EquationProcessor k = new EquationProcessor(eqs);

// define the domain variable and its values to evaluate
Map<String,String> defs = new HashMap<String,String>();
defs.put("k","[0:20]");

// evaluate the complete function set over the domain, outputting the functions that you want
List<String> cols = Arrays.asList("k", "ChebyshevTerm", "piApprox", "absError");
List<double[]> output = k.evaluate(defs,cols);

```

For a complete set of examples, see the code in the samples project. 

