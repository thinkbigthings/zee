package zee.engine.domain;

import java.util.Comparator;

/**
 *
 */
public class DoubleArrayComparator implements Comparator<double[]> {
   
   /** Creates a new instance of DoubleArrayComparator */
   public DoubleArrayComparator() {
   }

   /**
    * if r1 &lt; r2, returns -1, if same, returns 0, if r1 &gt; r2, returns 1.
    *
    * r1 &lt; r2 if, for the leftmost (smallest-indexed) element which is not
    * the same between r1 and r2, that element in r1 is smaller than the one
    * in r2. See the DoubleArrayComparatorTest.java for expected behavior.
    */
   @Override
   public int compare(double[] r1, double[] r2) {
      if(r1.length != r2.length) {
         throw new IllegalArgumentException();
      }
      int diff = 0;
      for(int c=0; c< r1.length; c++) {
         double sub = r1[c] - r2[c];
         if( sub == 0) {
            continue;
         }
         else if ( sub < 0) {
            diff = -1;
            break;
         }
         else {
            diff = 1;
            break;
         }
      }
      
      return diff;
   }

   /**
   * No state is stored, so any instance of rowComparator is basically
   * the same as any other instance
   */
   @Override
   public boolean equals(Object obj) {
      return obj.getClass().equals(this.getClass());
   }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

}
