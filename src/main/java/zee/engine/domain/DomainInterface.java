package zee.engine.domain;

import java.util.Map;
import java.util.Vector;

/**
 *
 */
public interface DomainInterface extends Map<String, double[]> {

    double[] getDef(String variable);

    int getLength();
    
    /**
     * Returns true if all definitions in this Domain parse to arrays of the
     * same length. If no variables are defined, this method returns true;
     */
    boolean isSameLengthDefs();

    /**
     * Creates a copy of this Domain and sets its variable data so that
     * it's recombined with varName. Any whitespace around varName is trimmed.
     *
     * If original has already recombined varName,
     * a copy of original is returned.
     */
    DomainInterface recombineVariable(String varName);

    /**
     * Recombines the specified variables.
     * All possible combinations of values for the specified variables with
     * each other are calculated and stored by name.
     */
    DomainInterface recombineVariables(String[] varNames);

    DomainInterface getCopy();

    /**
     * Returns a copy of this Domain with the named variable
     * removed and the points compressed along the direction of the named
     * variable. This is like a projection that removes the variable.
     */
    DomainInterface removeVariable(String varToRemove);

    DomainInterface setVariable(String var, double[] data);

    Vector<? extends DomainInterface> splitDomain(int numBlocks);

}
