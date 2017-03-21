package tex61;

import java.util.List;

/** A PageAssembler that collects its lines into a designated List.
    It only stores lines in a List.
 *  @author Eric Escobar
 */

class PageCollector extends PageAssembler {

    /** output of page collector.. */
    private List<String> outPut;

    /** A new PageCollector that stores lines in OUT. */
    PageCollector(List<String> out) {
        outPut = out;
    }

    /** Add LINE to my List. */
    @Override
    void write(String line) {
        outPut.add(line);
    }
}
