package zee.engine.domain;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import org.apache.commons.math.linear.Array2DRowRealMatrix;

/**
 * Maps a String variable name to a double[]
 *
 * put, putAll, and remove are unsupported: Domains are immutable.
 *
 * to get the number of points in this domain:
 * size()
 *
 * to see if this domain has recombined a variable with name "name":
 * domain.keySet().contains(name);
 *
 * to get the recombined variable names in this domain:
 * (String[])domain.keySet().toArray()
 * 
 * A Domain takes domain variables required for output and recombines them 
 * so that all points are evaluated. Once you have this initial domain 
 * with recombined variables, you pass this Domain into each output node 
 * and store that output node's results into the corresponding output column 
 * for the output set.
 * 
 * If you specify multiple functions like { x  y  f(x,y)  g(x)  h(y) } to be the 
 * columns of your output, they need to be tied together by common domain 
 * points, or the output won't line up. The union of the domain variables 
 * which appear in the output nodes (here, x and y) is the set of variables 
 * which need to be recombined for the initial domain. This gives you automatic 
 * domain culling.
 *
 * You need to save all initial domain information so you can
 * recombine and calculate points when you come across a domain variable that's 
 * not defined (possibly further down the evaluation chain). 
 *
 *
 */
public class Domain implements DomainInterface {
   
   private Map<String,double[]>   allDefs = null; // names to defs
   private Map<String,double[]> points = null;  // names to values
   private int length = 0;
   
   /** 
    * Creates an instance of Domain. 
    * Definition key Strings are trimmed.
    */
   public Domain(Map<String,double[]> newdefs) {
      allDefs = new HashMap<String,double[]>(newdefs);
      points = new HashMap<String,double[]>();
   }
   
   /**
    * Creates a copy of Domain, the double[] for each variable are copied
    * so you can modify the Domain copy without hurting the original Domain.
    */
   public Domain(Domain toCopy) {

      points = new HashMap<String,double[]>(toCopy.points);
      allDefs = new HashMap<String,double[]>(toCopy.allDefs);
      length = toCopy.length;

      // replace all double[]'s with copies so the caller can later modify the passed in object
      Set<String> keys = points.keySet();
      for(String key : keys )
      {
          double[] values = points.get(key);
          double[] copy = Arrays.copyOf(values, values.length);
          points.put(key, copy);
       }

      // replace all double[]'s with copies so the caller can later modify the passed in object
      keys = allDefs.keySet();
      for(String key : keys )
      {
          double[] values = allDefs.get(key);
          double[] copy = Arrays.copyOf(values, values.length);
          allDefs.put(key, copy);
       }
   }
   
    @Override
   public double[] getDef(String variable) {
      return allDefs.get(variable);
   }

    @Override
   public boolean isSameLengthDefs()
   {

       if(allDefs.isEmpty())
         return true;

      double[] firstDef = allDefs.values().iterator().next();
      int defLength = firstDef.length;
      for(double[] def : allDefs.values()) {
         if( defLength != def.length )
            return false;
      }

      return true;
   }
   
    @Override
   public Vector<? extends DomainInterface> splitDomain(int numBlocks) {
      
      if( numBlocks < 1 || numBlocks > length)
      {
          String message = "numblocks must be > 0 and < length of domain";
          throw new IllegalArgumentException(message);
      }
      
      Vector<Domain> blocks = new Vector<Domain>();

      int blockSize = Math.round((float)Math.ceil( ((float)length)/((float)numBlocks) ));
      int startIndex = 0;
      int endIndex = blockSize;
      while(startIndex < length) {
         Domain d = new Domain(this);
         d.length = endIndex - startIndex;
         Iterator<String> v = d.keySet().iterator();
         while(v.hasNext()) {
            String var = v.next();
            double[] orig = d.get(var);
            double[] newpoints = new double[d.length];
            System.arraycopy(orig,startIndex,newpoints,0,newpoints.length);
            d.points.put(var,newpoints);
         }
         startIndex = endIndex;
         endIndex = Math.min(length, endIndex + blockSize);

         blocks.add(d);
      }

      return blocks;
   }
   
   /**
    * Test for equivalence based on the value of the pointer.
    *
    * Domains are immutable. Their points make them unique but comparing 
    * every point at each function entry would take a lot of runtime.
    *
    * Note that it is generally necessary to override the hashCode method 
    * whenever equals() is overridden, so as to maintain the general 
    * contract for the hashCode() method, which states that equal objects must 
    * have equal hash codes. However, with reference-based equality, equal
    * objects are the same object and so will always have the same hashcode. So
    * a reference-based equality does not need to override the hashCode method.
    */
   @Override
   public boolean equals(Object other) {
      return (other == this);
   }

   /**
    * Only uses defs to calculate hashcode. Could alternatively use
    * other private members in the calculation.
    * 
    * @return hashcode value 
    */
    @Override
    public int hashCode() {
        return allDefs.hashCode();
    }

    // TODO need to decide how to handle if you have a domain of one length and set variable to another length
    // this is done in the tests, but the check here was that you might not want that to happen
    // this method is used two places: EquationProcessor and DomainTransformation
    // I think the usage in EquationProcessor could be better accomplished by just setting the points to the defs
    // and the usage in DomainTransformation needs to be clearer before changing that around

    // there is some confusion over setting variable definition (allDefs) and setting evaluation points (points)
    // allDefs can be different lengths generally, points should ALWAYS be the same length otherwise getLength() breaks

    @Override
    public DomainInterface setVariable(String var, double[] data) {

      Domain reset = new Domain(this);

      /*
      if(reset.points.values().size() > 1 && reset.points.values().iterator().next().length != data.length)
      {
          throw new IllegalArgumentException("Can't assign domain variables with different lengths");
      }
       */

      reset.points.put(var, data);
      reset.length = data.length;
      return reset;
   }
    
   /**
    * Creates a copy of this Domain and sets its variable data so that
    * it's recombined with varName. Any whitespace around varName is trimmed.
    * 
    * If original has already recombined varName,
    * a copy of original is returned.
    */
    @Override
   public Domain recombineVariable(String varName) {
      
      varName = varName.trim();
       
      Domain recombined = new Domain(this);
      
      if(points.containsKey(varName))
         return recombined;
      
      // for a single variable name
      // these are the new values being recombined
      // remember how many times you'll need to replicate this one
      double[] curVals = recombined.allDefs.get(varName);
      int numReps = 1;
      if( recombined.points.keySet().size() > 0 )
         numReps = (recombined.points.values().iterator().next()).length;
      
      // replicate the variables previously recombined
      // each previous variable is replicated as many times as there are 
      // elements in curVals
      Iterator<Map.Entry<String,double[]>> prevEntries;
      prevEntries = recombined.points.entrySet().iterator();
      while(prevEntries.hasNext()) {
         Map.Entry<String,double[]> entry = prevEntries.next();
         double[] p  = entry.getValue();
         double[] pr = new double[ p.length * curVals.length ];
         for(int i=0; i < curVals.length; i++)
            System.arraycopy(p, 0, pr, i*p.length, p.length);
         recombined.points.put(entry.getKey(), pr);
      }

      // recombine the current variable
      double[] cr = new double[numReps*curVals.length];
      for(int i=0; i < curVals.length; i++) {
         int fromIndex = i*numReps;
         int toIndex = fromIndex + numReps;
         double value = curVals[i];
         Arrays.fill(cr,fromIndex,toIndex,value);
      }
      recombined.points.put(varName, cr);
      recombined.length = cr.length;
      
      return recombined;
   }
   
    @Override
   public int getLength() {
      return length;
   }
   
   /**
    * Recombines the specified variables.
    * All possible combinations of values for the specified variables with
    * each other are calculated and stored by name.
    */
    @Override
   public DomainInterface recombineVariables(String[] varNames) {
      
      // start with 
      Domain recombined = new Domain(this);
      
      // recombine each variable in turn
      for(int n=0; n < varNames.length; n++)
         recombined = recombined.recombineVariable(varNames[n]);
      
      return recombined;
   }

   /**
    * Returns a copy of this Domain with the named variable
    * removed and the points compressed along the direction of the named
    * variable. This is like a projection that removes the variable.
    *
    * If the current domain doesn't have the specified variable defined,
    * a copy of the existing domain is returned.
    */
    @Override
   public DomainInterface removeVariable(String varToRemove) {

      Domain nd = new Domain(this);

      if(! allDefs.containsKey(varToRemove)) {
         return nd;
      }
      
      // now that you have varToRemove
      // remove it and remove any duplicate points
      // can't just recombine the other points
      // because this Domain might be the result of direct setting of variables
      

      nd.allDefs.remove(varToRemove);
      nd.points.remove(varToRemove);
      
      Object[] keyArray = nd.points.keySet().toArray();
      double[][] vals = new double[keyArray.length][];
      for(int i=0; i < keyArray.length; i++)
      {
         vals[i] = nd.points.get(keyArray[i]);
      }

      Array2DRowRealMatrix matrix = new Array2DRowRealMatrix(vals, true);
      vals = matrix.transpose().getData();
      
      Comparator<double[]> comparator = new DoubleArrayComparator();
      TreeSet<double[]> uniquePoints = new TreeSet<double[]>(comparator);
      for(int r=0; r < vals.length; r++)
         uniquePoints.add(vals[r]);
      
      nd.length = uniquePoints.size();
      
      vals = new double[uniquePoints.size()][keyArray.length];
      int r=0;
      for(double[] row : uniquePoints) {
         vals[r] = row;
         r++;
      }

      matrix = new Array2DRowRealMatrix(vals, true);
      vals = matrix.transpose().getData();

      for(r=0; r < vals.length; r++)
         nd.points.put(keyArray[r].toString(), vals[r]);
      
      return nd;
   }
   
   ////////////////////////////////////////////////////////////////////////
   // implemented methods from the Map interface
   // I didn't override HashMap because I wanted to restrict use of
   // put, putAll, and remove, so that Domains would be immutable

   
   /** this operation is not supported, Domains are immutable */
   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }
   @Override
   public boolean containsKey(Object key) {
      return points.containsKey((String)key);
   }
   @Override
   public boolean containsValue(Object value) {
       double[] valueToFind = (double[])value;
       boolean found = false;
       for(double[] data : values())
       {
           found |= Arrays.equals(data, valueToFind);
       }
       return found;
   }
   
   @Override
   public Set<Map.Entry<String,double[]>> entrySet() {
      return new Domain(this).points.entrySet();
   }
   /**
    * The double[] is a copy of the domain's double[]
    * Domains are immutable, so you may change the returned double[]
    * without hurting the Domain.
    */
   @Override
   public double[] get(Object key) {
      double[] variableData = points.get(key);
      double[] copiedResults = new double[variableData.length];
      System.arraycopy(variableData,0,copiedResults,0,copiedResults.length);
      return copiedResults;
   }
   @Override
   public boolean isEmpty() {
      return points.isEmpty();
   }
   @Override
   public Set<String> keySet() {
      return new HashSet<String>(points.keySet());
   }
   
   /** this operation is not supported, Domains are immutable */
   @Override
   public double[] put(String key, double[] value)  {
      throw new UnsupportedOperationException();
   }
   /** this operation is not supported, Domains are immutable */
   @Override
   public void putAll(Map<? extends String,? extends double[]> map) {
      throw new UnsupportedOperationException();
   }
   /** this operation is not supported, Domains are immutable */
   @Override
   public double[] remove(Object key) {
      throw new UnsupportedOperationException();
   }

   /**
    * This method returns the number of variables available on this domain
    * (but not the number of variables defined for evaluation)
    *
    * For the length of the recombined double[]'s defined in this domain, use getLength().
    *
    *
    * @return
    */
   @Override
   public int size() {
      return allDefs.size();
   }
   
   /**
    * the returned collection may be changed without affecting the domain's
    * Collection of points, but the double[]'s are backed by the domain's 
    * double[]'s. Do not change the returned double[]'s!
    * 
    */
   @Override
   public Collection<double[]> values() {
      return new Vector<double[]>(points.values());
   }

    @Override
    public DomainInterface getCopy() {
        return new Domain(this);
    }

}
