
package zee.parser;


import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import zee.engine.parser.MatrixParser;

public class MatrixParserTest {

    private Map<String,String> meta = new HashMap<String,String>();

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
      parser.parse("[-3,9;-2,4;-1,l;0,0;1,1;2,4;3,9]", meta); // there's an "L" instead of a "one"
   }
}
