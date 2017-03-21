package tex61;

import static tex61.FormatException.error;

/** A PageAssembler accepts complete lines of text (minus any
 *  terminating newlines) and turns them into pages, adding form
 *  feeds as needed.  It prepends a form feed (Control-L  or ASCII 12)
 *  to the first line of each page after the first.  By overriding the
 *  'write' method, subtypes can determine what is done with
 *  the finished lines.
 *  @author Eric Escobar
 */
abstract class PageAssembler {
    /** text height of current page. */
    private int textHeight;

    /** Create a new PageAssember. Initially, its text height is unlimited.
        It prepends a form feed character to the first line of each page
        except the first. */
    PageAssembler() {
        textHeight = Defaults.TEXT_HEIGHT;
    }

    /** Set text height to VAL, where VAL > 0. */
    void setTextHeight(int val) {
        if (val > 0) {
            textHeight = val;
        } else {
            throw error("wrong text height");
        }
    }

    /** Perform final disposition of LINE, as determined by the
     *  concrete subtype. */
    abstract void write(String line);

}
