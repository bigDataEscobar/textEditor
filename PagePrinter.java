package tex61;

import java.io.PrintWriter;

/** A PageAssembler that sends lines immediately to a PrintWriter, with
 *  terminating newlines.
 *  @author Eric Escobar
 */

class PagePrinter extends PageAssembler {
    /** output for PagePrinter. */
    private PrintWriter outPut;

    /** A new PagePrinter that sends lines to OUT. */
    PagePrinter(PrintWriter out) {
        outPut = out;
    }

    /** Print LINE to my output. */
    @Override
    void write(String line) {
        System.out.println(line);
    }
}
