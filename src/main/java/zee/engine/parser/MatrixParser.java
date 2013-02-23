
package zee.engine.parser;

import zee.engine.nodes.MathNode;
import java.text.ParseException;
import java.util.Map;
import org.apache.commons.math.MathException;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import zee.engine.nodes.NumericFunction1D;
import zee.engine.nodes.NumericFunction2D;
import zee.engine.nodes.interpolators.Interpolation;


public class MatrixParser implements ParserStrategy {
    
    public static final String INDEPENDANT_VARIABLE = "INDEPENDANT VARIABLE";
    public static final String INDEPENDANT_VARIABLE_1 = "INDEPENDANT VARIABLE 1";
    public static final String INDEPENDANT_VARIABLE_2 = "INDEPENDANT VARIABLE 2";
    public static final String INTERPOLATION_TYPE = "INTERPOLATION";

    @Override
    public boolean isParsable(String input) {
        return MathString.isMatrix(input);
    }

    @Override
    public MathNode parse(String toParse, Map<String, String> meta) throws ParseException {

       MathNode node = null;
        try {
           RealMatrix matrix = new Array2DRowRealMatrix(MathString.toDoubleTable(toParse),true);

           if(matrix.getColumnDimension() <= 1 || matrix.getRowDimension() <= 1) {
               throw new ParseException("Not enough data is defined", 0);
           }
           if(matrix.getColumnDimension() == 2) {
                String indVar = meta.get(INDEPENDANT_VARIABLE);
                String reqType = meta.get(INTERPOLATION_TYPE);
                Interpolation.TYPE type = Interpolation.TYPE.CUBIC;
                if("Cubic".equalsIgnoreCase(reqType))
                    type = Interpolation.TYPE.CUBIC;
                if("Linear".equalsIgnoreCase(reqType))
                    type = Interpolation.TYPE.LINEAR;
                node = new NumericFunction1D(toParse, matrix.getData(), type, indVar);
           }
           if(matrix.getColumnDimension() > 2) {
             String reqType = meta.get(INTERPOLATION_TYPE);
             String indVar1 = meta.get(INDEPENDANT_VARIABLE_1);
             String indVar2 = meta.get(INDEPENDANT_VARIABLE_2);
             Interpolation.TYPE type = Interpolation.TYPE.CUBIC;
             if("Cubic".equalsIgnoreCase(reqType))
                 type = Interpolation.TYPE.CUBIC;
             if("Linear".equalsIgnoreCase(reqType))
                 type = Interpolation.TYPE.LINEAR;
              node = new NumericFunction2D(toParse,matrix.getData(), type, indVar1, indVar2);
           }
        }
        catch(NumberFormatException nfe) {
           String message = "Can't parse matrix: " + nfe.getMessage();
           throw new ParseException(message, 0);
        }
        catch(MathException iae) {
           throw new ParseException(iae.getMessage(), 0);
        }
       
       return node;
    }

    public static String toString(Array2DRowRealMatrix matrix)
    {
        return matrix.toString().replace("Array2DRowRealMatrix{{", "[").replace("}}", "]").replace("},{", ";");
    }

}
