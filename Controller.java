package tex61;

import java.io.PrintWriter;


/** Receives (partial) words and commands, performs commands, and
 *  accumulates and formats words into lines of text, which are sent to a
 *  designated PageAssembler.  At any given time, a Controller has a
 *  current word, which may be added to by addText, a current list of
 *  words that are being accumulated into a line of text, and a list of
 *  lines of endnotes.
 *  @author Eric Escobar
 */

class Controller {
    /** new page printer object. */
    private PagePrinter pagePrinterObj;
    /** new line assembler object. */
    private LineAssembler lineAssemblerObj;
    /** new endnoted object. */
    private LineAssembler endNotesObj;
    /** new print writer object. */
    private PrintWriter _out;
    /** boolean endnote. */
    private boolean _endnoteMode;
    /** Number of next endnote. */
    private int _refNum;

    /** A new Controller that sends formatted output to OUT. */
    Controller(PrintWriter out) {
        _out = out;
        _refNum = 1;
        _endnoteMode = false;
        pagePrinterObj = new PagePrinter(out);
        lineAssemblerObj = new LineAssembler(pagePrinterObj);
        endNotesObj = new LineAssembler(pagePrinterObj, true);
    }
    /** Add TEXT to the end of the word of formatted text currently
     *  being accumulated. */
    void addText(String text) {
        if (_endnoteMode) {
            endNotesObj.addText(text);
        } else {
            lineAssemblerObj.addText(text);
        }
    }

    /** Finish any current word of text and, if present, add to the
     *  list of words for the next line.  Has no effect if no unfinished
     *  word is being accumulated. */
    void endWord() {
        if (_endnoteMode) {
            endNotesObj.finishWord();
        } else {
            lineAssemblerObj.finishWord();
        }
    }

    /** Finish any current word of formatted text and process an end-of-line
     *  according to the current formatting parameters. */
    void addNewline() {
        if (_endnoteMode) {
            endNotesObj.beginLine(false);
        } else {
            lineAssemblerObj.beginLine(false);
        }
    }

    /** Finish any current word of formatted text, format and output any
     *  current line of text, and start a new paragraph. */
    void endParagraph() {
        if (_endnoteMode) {
            endNotesObj.endParagraph();
        } else {
            lineAssemblerObj.endParagraph();
        }
    }

    /** If valid, process TEXT into an endnote, first appending a reference
     *  to it to the line currently being accumulated. */
    void formatEndnote(String text) {
        InputParser endNotesParseObj = new InputParser(text, this);
        lineAssemblerObj.addText("[" + _refNum + "]");
        setEndnoteMode();
        endNotesObj.addWord("[" + _refNum + "]");
        endNotesParseObj.process();
        endNotesObj.finishWord();
        endNotesObj.endParagraph();
        setNormalMode();
        _refNum += 1;
    }

    /** Set the current text height (number of lines per page) to VAL, if
     *  it is a valid setting.  Ignored when accumulating an endnote. */
    void setTextHeight(int val) {
        if (!_endnoteMode) {
            lineAssemblerObj.setTextHeight(val);
        }
    }

    /** Set the current text width (width of lines including indentation)
     *  to VAL, if it is a valid setting. */
    void setTextWidth(int val) {
        if (_endnoteMode) {
            endNotesObj.setTextWidth(val);
        } else {
            lineAssemblerObj.setTextWidth(val);
        }
    }

    /** Set the current text indentation (number of spaces inserted before
     *  each line of formatted text) to VAL, if it is a valid setting. */
    void setIndentation(int val) {
        if (_endnoteMode) {
            endNotesObj.setIndentation(val);
        } else {
            lineAssemblerObj.setIndentation(val);
        }
    }

    /** Set the current paragraph indentation (number of spaces inserted before
     *  first line of a paragraph in addition to indentation) to VAL, if it is
     *  a valid setting. */
    void setParIndentation(int val) {
        if (_endnoteMode) {
            endNotesObj.setParIndentation(val);
        } else {
            lineAssemblerObj.setParIndentation(val);
        }
    }

    /** Set the current paragraph skip (number of blank lines inserted before
     *  a new paragraph, if it is not the first on a page) to VAL, if it is
     *  a valid setting. */
    void setParSkip(int val) {
        if (_endnoteMode) {
            endNotesObj.setParSkip(val);
        } else {
            lineAssemblerObj.setParSkip(val);
        }
    }

    /** Iff ON, begin filling lines of formatted text. */
    void setFill(boolean on) {
        if (_endnoteMode) {
            endNotesObj.setFill(on);
        } else {
            lineAssemblerObj.setFill(on);
        }
    }

    /** Iff ON, begin justifying lines of formatted text whenever filling is
     *  also on. */
    void setJustify(boolean on) {
        if (_endnoteMode) {
            endNotesObj.setJustify(on);
        } else {
            lineAssemblerObj.setJustify(on);
        }
    }

    /** Finish the current formatted document or endnote (depending on mode).
     *  Formats and outputs all pending text. */
    void close() {
        if (!_endnoteMode) {
            lineAssemblerObj.finalOutput();
            endNotesObj.finalOutput();
        }
    }

    /** Start directing all formatted text to the endnote assembler. */
    private void setEndnoteMode() {
        _endnoteMode = true;
    }

    /** Return to directing all formatted text to _mainText. */
    private void setNormalMode() {
        _endnoteMode = false;
    }

}
