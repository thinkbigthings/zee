/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package zee.engine.parser;

import java.util.HashMap;
import java.util.Map;
import zee.engine.domain.DomainInterface;
import zee.engine.domain.Domain;

/**
 *
 */
public class DomainParser {

   public DomainInterface getDomain(Map<String, String> domainDefs)
   {
      Map<String, double[]> doubleDomainDefs = new HashMap<String, double[]>();
      for(String key : domainDefs.keySet())
      {
          String def = domainDefs.get(key);
          double[] curVals = MathString.toDoubleArray(def);
          doubleDomainDefs.put(key, curVals);
      }

      return new Domain(doubleDomainDefs);
   }

}
