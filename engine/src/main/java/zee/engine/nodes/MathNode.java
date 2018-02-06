package zee.engine.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zee.engine.domain.DomainInterface;

public abstract class MathNode {
  
   protected static boolean debugOutput = false;
   protected List<MathNode> children = new ArrayList<>();
   protected List<MathNode> parents  = new ArrayList<>();
   protected Object id = null;
   protected Map<DomainInterface,double[]> cache = new HashMap<>();
   protected boolean splittable = true;
   
  /**
   * The smallest number of parents this node may have to cache its evaluation.
   * Default is two. A higher setting may reduce memory usage at the expense
   * of increased cpu time.
   */
   private static int cacheLimit = 2;

   public MathNode(Object id) { 
      this.id = id;
   }
   
   public void clearCache() {
       cache.clear();
       for(MathNode child : children)
           child.clearCache();
   }
   
   public List<MathNode> getParentsOfType(Class c) {
      List<MathNode> typed = new ArrayList<>();
      for(MathNode d : parents) {
         if(d.getClass().equals(c))
            typed.add(d);
         else
            typed.addAll(d.getParentsOfType(c));
      }
      return typed;
   }
   
   /**
    * Returns true if this string is the ID of this node or any child node
    * anywhere in the tree underneath
    * 
    */
   public boolean isStringInTree(String sid) {
       boolean found = sid.equals(id);
       
       if(found)
           return found;
       
       for(MathNode child : children) {
           if(child.isStringInTree(sid)) {
               found = true;
               break;
           }
       }
       
       return found;
   }
   
  /**
   * The number of parents this node must have to cache its evaluation.
   * Default is two. A higher setting may relieve memory at the expense
   * of cpu.
   */
   public static int getCacheLimit() {
      return cacheLimit;
   }

   public static void setCacheLimit(int newLimit) {
      cacheLimit = newLimit;
   }
   
   public Object getID() {
      return id;
   }
   
   public int getChildCount() {
      return children.size();
   }
   
   public MathNode getChild(int index) {
      return children.get(index);
   }
   
   /**
    * Adds a child node. A child node may be added more than once.
    * If the child node already has this node as a parent
    * then this parent is not added to the child's parent list again
    */
   public void addChild(MathNode node) {
      children.add(node);
      if( ! node.parents.contains(node)) {
         node.parents.add(this);
      }
   }
   
   public int getParentCount() {
      return parents.size();
   }

   public boolean isCached(DomainInterface d) {
      return cache.containsKey(d);
   }
   
   /**
    * By default this method returns true.
    *
    * Sometimes the domain will be split for memory performance reasons
    * and only a part of the domain is evaluated at one time.
    * Some nodes will not return the correct answers
    * if you split the domain because they need to function over the ENTIRE
    * domain at one time to work. This method indicates if the MathNode
    * requires an unsplit domain in order to correctly calculate its value.
    * If this class or any children are unsplittable, this method returns false.
    * Otherwise it returns true. 
    */
   public boolean isSplittable() {
      boolean compositeSplittable = splittable;
      for(MathNode child : children)
         compositeSplittable &= child.isSplittable();//compositeSplittable;
      return compositeSplittable;
   }
   
   /**
    * Evaluates this node on the specified domain. May or may not perform the
    * operation or just return cached data.
    * <P>
    *
    * Gets results from (and stores results to) cache if its number
    * of parents is greater than or equal to the cacheLimit.
    * You can alter the returned double[] without affecting the cache.
    *
    * @param domain maps Strings (variable names) to double[] (values)
    * @return this node evaluated on the specified domain.
    */
   public final double[] evaluate(DomainInterface domain) {
      double[] results;
      
      if( getParentCount() >= cacheLimit ) {

         results = cache.get(domain);
         if(results == null) {
            results = performCalculation(domain);
            cache.put(domain, results);
         }

         // whether you put results into cache or get from cache
         // you want the cache to be disassociated from what you return
         // so you don't have to worry if a user alters the results
         double[] copiedResults = new double[results.length];
         System.arraycopy(results,0,copiedResults,0,results.length);
         results = copiedResults;
      }
      else {
         results = performCalculation(domain);
      }
      
      return results;
   }

   /**
    * Override this method to evaluate this node. When caching is appropriate,
    * caching is performed automatically immediately after this calculation.
    */
   public abstract double[] performCalculation(DomainInterface domain);

}

