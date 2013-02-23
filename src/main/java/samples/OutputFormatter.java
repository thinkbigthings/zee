
package samples;

import java.util.List;

public class OutputFormatter {

   public static String toString(List<double[]> output)
   {
       StringBuilder builder = new StringBuilder();

      for(int row=0; row < output.get(0).length; row++)
      {
          for(double[] column : output) {
            builder.append(column[row] + "\t");
          }
          builder.append(System.getProperty("line.separator"));
      }
      return builder.toString();
   }
}
