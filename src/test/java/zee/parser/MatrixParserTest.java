
package zee.parser;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import zee.engine.parser.MatrixParser;
import zee.engine.parser.ParseException;

public class MatrixParserTest {

   private Map<String,String> meta = new HashMap<>();

   @Test(expected=ParseException.class)
   public void testOneColumn() throws ParseException {
      MatrixParser parser = new MatrixParser();
      parser.parse("[1;2;3;4]", meta);
   }

   @Test(expected=ParseException.class)
   public void testOneRow() throws ParseException {
      MatrixParser parser = new MatrixParser();
      parser.parse("[1,2,3,4]", meta);
   }

   @Test(expected=ParseException.class)
   public void testNotNumber() throws ParseException {
      MatrixParser parser = new MatrixParser();
      parser.parse("[-3,9;-2,4;-1,q;0,0;1,1;2,4;3,9]", meta); // there's an "Q" instead of a "one"
   }
}
