package tex61;

import java.util.ArrayList;
import static tex61.FormatException.error;

/** An object that receives a sequence of words of text and formats
 *  the words into filled and justified text lines that are sent to a receiver.
 *  SHOULD FORMAT LINES--THEN SEND THEM TO A DESIGNATED PAGEASSEMBLER.
 *  @author Eric Escobar
 */

class LineAssembler {
    /** Stores all words in a line object. */
    private ArrayList<String> wordArray = new ArrayList<String>();
    /** Stores all lines in a line object. */
    private ArrayList<String> lineArray = new ArrayList<String>();

    /** Defaults variables. */
    private int textHeight = Defaults.TEXT_HEIGHT;
    /** default for paragraph skip. */
    private int parSkip = Defaults.PARAGRAPH_SKIP;
    /** default for indentation. */
    private int inDent = Defaults.INDENTATION;
    /** default for paragraph indentation. */
    private int parIndent = Defaults.PARAGRAPH_INDENTATION;
    /** default for text width. */
    private int textWidth = Defaults.TEXT_WIDTH;

    /** start of current word. */
    private String currentWord = "";
    /** keeps track of current character length. */
    private int currentCharLength;
    /** keeps track of current page. */
    private boolean currentPage;
    /** keeps track of current paragraph. */
    private boolean currentParagraph;
    /** keeps track of fill mode. */
    private boolean fillMode;
    /** keeps track of justify mode. */
    private boolean justifyMode;


    /** A new, empty line assembler with default settings of all
     *  parameters, sending finished lines to PAGES. */
    LineAssembler(PageAssembler pages) {
        _pages = pages;
        textHeight = Defaults.TEXT_HEIGHT;
        parSkip = Defaults.PARAGRAPH_SKIP;
        inDent = Defaults.INDENTATION;
        parIndent = Defaults.PARAGRAPH_INDENTATION;
        textWidth = Defaults.TEXT_WIDTH;
        fillMode = true;
        justifyMode = true;
        currentParagraph = true;
    }

    /** new lineAssembler object for dealing with end notes.
        @param pages returns pages of page assembler.
        @param endNoteOn keeps track of state of end notes mode. */
    LineAssembler(PageAssembler pages, boolean endNoteOn) {
        this(pages);
        if (endNoteOn) {
            textWidth = Defaults.ENDNOTE_TEXT_WIDTH;
            inDent = Defaults.ENDNOTE_INDENTATION;
            parSkip = Defaults.ENDNOTE_PARAGRAPH_SKIP;
            parIndent = Defaults.ENDNOTE_PARAGRAPH_INDENTATION;
        }
    }

    /** Add TEXT to the word currently being built. */
    void addText(String text) {
        currentWord += text;
    }

    /** Finish the current word, if any, and add to words being accumulated. */
    void finishWord() {
        int indentNumber = indentSize();
        if (currentWord == null || currentWord.equals("")) {
            return;
        }
        if (wordArray.isEmpty()
            && (currentWord.length() + indentNumber) > textWidth) {
            wordArray.add(currentWord);
            currentCharLength += currentWord.length();
            beginLine(false);
        } else if ((currentWord.length() + indentNumber
                    + currentCharLength + wordArray.size()) > textWidth) {
            String newWord = currentWord;
            beginLine(false);
            wordArray.add(newWord);
            currentCharLength += newWord.length();
        } else {
            wordArray.add(currentWord);
            currentCharLength += currentWord.length();
        }
        currentWord = "";
    }

    /** return the indentation size. */
    private int indentSize() {
        if (!fillMode) {
            return 0;
        } else {
            int size = currentParagraph ? inDent + parIndent : inDent;
            return size;
        }
    }

    /** Add WORD to the formatted text. */
    void addWord(String word) {
        wordArray.add(word);
    }

    /** Add LINE to our output, with no preceding paragraph skip.  There must
     *  not be an unfinished line pending. */
    void addLine(String line) {
        if (wordArray.isEmpty()) {
            appendToLine(line);
        }
    }

    /** @param line gets added to page. */
    private void appendToLine(String line) {
        if (currentParagraph && !lineArray.isEmpty()) {
            for (int i = 0; i < parSkip; i += 1) {
                lineArray.add("");
            }
            currentParagraph = false;
        }
        if (currentPage) {
            lineArray.add('\f' + line);
            currentPage = false;
        } else {
            lineArray.add(line);
        }
        if (lineArray.size() == textHeight) {
            currentPage = true;
        }
    }
    /** Set the current indentation to VAL. VAL >= 0. */
    void setIndentation(int val) {
        if (val >= 0) {
            inDent = val;
        } else {
            throw error("error: wrong indentation");
        }
    }

    /** Set the current paragraph indentation to VAL. VAL >= 0. */
    void setParIndentation(int val) {
        if (val >= 0) {
            parIndent = val;
        } else {
            throw error("error: wrong indentation");
        }
    }

    /** Set the text width to VAL, where VAL >= 0. */
    void setTextWidth(int val) {
        if (val >= 0) {
            textWidth = val;
        } else {
            throw error("error: wrong width");
        }
    }

    /** Iff ON, set fill mode. */
    void setFill(boolean on) {
        fillMode = on;
        if (!on) {
            justifyMode = false;
        }
    }

    /** Iff ON, set justify mode (which is active only when filling is
     *  also on). */
    void setJustify(boolean on) {
        if (!fillMode) {
            return;
        }
        justifyMode = on;
    }

    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParSkip(int val) {
        if (val >= 0) {
            parSkip = val;
        } else {
            throw error("error: wrong paragraph skip");
        }
    }

    /** Set page height to VAL > 0. */
    void setTextHeight(int val) {
        if (val > 0) {
            textHeight = val;
        } else {
            throw error("error: wrong text heigth");
        }
    }

    /** Process the end of the current input line.  No effect if
     *  current line accumulator is empty or in fill mode.  Otherwise,
     *  adds a new complete line to the finished line queue and clears
     *  @param startLine the line accumulator. */
    void beginLine(boolean startLine) {
        if (wordArray.size() > 0) {
            int wordSize = wordArray.size();
            int indentNumber = indentSize();
            int totalSpaces = textWidth - currentCharLength - indentNumber;
            if (!justifyMode || startLine) {
                totalSpaces = wordSize - 1;
            }
            if (totalSpaces > 3 * (wordSize - 1)) {
                totalSpaces = 3 * (wordSize - 1);
            }
            emitLine(indentNumber, totalSpaces);
        }
    }

    /** When finished, sends to output. */
    void finalOutput() {
        beginLine(true);
        for (String line : lineArray) {
            _pages.write(line);
        }
    }

    /** If there is a current unfinished paragraph pending, close it
     *  out and start a new one. */
    public void endParagraph() {
        wordArray.add(currentWord);
        currentCharLength += currentWord.length();
        beginLine(true);
        currentParagraph = true;
    }

    /** Transfer contents of _words to _pages, adding INDENT characters of
     *  indentation, and a total of SPACES spaces between words, evenly
     *  distributed.  Assumes _words is not empty.  Clears _words and _chars. */
    private void emitLine(int indent, int spaces) {
        int moreSpaces = 0;
        int spacesTotal = 0;
        String newLine = "";
        double spacesToDouble = (double) spaces;
        for (int i = 0; i < indent; i += 1) {
            newLine += " ";
        }
        int wordSize = wordArray.size();
        newLine += wordArray.get(0);
        for (int k = 1; k < wordSize; k += 1) {
            double currentSpaces = 0.5 + spacesToDouble
                * (double) k / ((double) wordSize - 1.0);
            moreSpaces = (int) currentSpaces;
            for (int j = 0; j < moreSpaces - spacesTotal; j += 1) {
                newLine += " ";
            }
            newLine += wordArray.get(k);
            spacesTotal = moreSpaces;
        }
        currentCharLength = 0;
        appendToLine(newLine);
        currentWord = "";
        wordArray.clear();

    }

    /** Destination given in constructor for formatted lines. */
    private final PageAssembler _pages;

}
