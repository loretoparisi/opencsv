package com.opencsv;


import com.opencsv.enums.CSVReaderNullFieldIndicator;

/**
 * Builder for creating a RFC4180Parser.
 * <p>Example code for using this class:<br><br>
 * <code>
 * final RFC4180Parser parser =<br>
 * new RFC4180ParserBuilder()<br>
 * .withSeparator('\t')<br>
 * .build();<br>
 * </code></p>
 *
 * @see RFC4180Parser
 * @since 3.9
 */
public class RFC4180ParserBuilder {

    private char separator = ICSVParser.DEFAULT_SEPARATOR;
    private char quoteChar = ICSVParser.DEFAULT_QUOTE_CHARACTER;
    private CSVReaderNullFieldIndicator nullFieldIndicator = CSVReaderNullFieldIndicator.NEITHER;

    /**
     * Default constructor.
     */
    public RFC4180ParserBuilder() {
    }

    /**
     * @return The defined separator.
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * @return The defined quotation character.
     */
    public char getQuoteChar() {
        return quoteChar;
    }

    /**
     * @return The null field indicator.
     */
    public CSVReaderNullFieldIndicator nullFieldIndicator() {
        return nullFieldIndicator;
    }

    /**
     * Constructs RFC4180Parser.
     *
     * @return A new RFC4180Parser with defined settings.
     */
    public RFC4180Parser build() {

        return new RFC4180Parser(quoteChar, separator, nullFieldIndicator);

    }

    /**
     * Sets the delimiter to use for separating entries.
     *
     * @param separator The delimiter to use for separating entries
     * @return The RFC4180ParserBuilder
     */
    public RFC4180ParserBuilder withSeparator(
            final char separator) {
        this.separator = separator;
        return this;
    }


    /**
     * Sets the character to use for quoted elements.
     *
     * @param quoteChar The character to use for quoted element.
     * @return The RFC4180ParserBuilder
     */
    public RFC4180ParserBuilder withQuoteChar(
            final char quoteChar) {
        this.quoteChar = quoteChar;
        return this;
    }

    /**
     * Sets the NullFieldIndicator.
     *
     * @param fieldIndicator CSVReaderNullFieldIndicator set to what should be considered a null field.
     * @return The RFC4180ParserBuilder
     */
    public RFC4180ParserBuilder withFieldAsNull(final CSVReaderNullFieldIndicator fieldIndicator) {
        this.nullFieldIndicator = fieldIndicator;
        return this;
    }

}
