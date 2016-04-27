package com.opencsv

import spock.lang.Specification

import static com.opencsv.CSVParser.*
import static com.opencsv.enums.CSVReaderNullFieldIndicator.BOTH
import static com.opencsv.enums.CSVReaderNullFieldIndicator.NEITHER

class CSVParserBuilderSpec extends Specification {

  CSVParserBuilder builder = new CSVParserBuilder()

  def 'default settings should be configured for builder'() {
    expect:
      DEFAULT_SEPARATOR == builder.separator
      DEFAULT_QUOTE_CHARACTER == builder.quoteChar
      DEFAULT_ESCAPE_CHARACTER == builder.escapeChar
      DEFAULT_STRICT_QUOTES == builder.strictQuotes
      DEFAULT_IGNORE_LEADING_WHITESPACE == builder.ignoreLeadingWhiteSpace
      DEFAULT_IGNORE_QUOTATIONS == builder.ignoreQuotations
      NEITHER == builder.nullFieldIndicator()
  }

  def 'default settings should be configured for built parser'() {
    when:
      CSVParser parser = builder.build()

    then:
      DEFAULT_SEPARATOR == parser.separator
      DEFAULT_QUOTE_CHARACTER == parser.quotechar //TODO nonunified name
      DEFAULT_ESCAPE_CHARACTER == parser.escape // TODO nonunified name
      DEFAULT_STRICT_QUOTES == parser.strictQuotes
      DEFAULT_IGNORE_LEADING_WHITESPACE == parser.ignoreLeadingWhiteSpace
      DEFAULT_IGNORE_QUOTATIONS == parser.ignoreQuotations

    and:
      NEITHER == parser.nullFieldIndicator()
  }

  def 'separator is set correctly'() {
    given:
      def separator = '1'.toCharacter()

    when:
      builder.withSeparator(separator)

    then:
      builder.separator == separator

    when:
      CSVParser parser = builder.build()

    then:
      parser.separator == separator
  }

  def 'quote char is set correctly'() {
    given:
      def quoteChar = '2'.toCharacter()

    when:
      builder.withQuoteChar(quoteChar)

    then:
      builder.quoteChar == quoteChar

    when:
      CSVParser parser = builder.build()

    then:
      parser.quotechar == quoteChar
  }

  def 'escape char is set correctly'() {
    given:
      def escapeChar = '3'.toCharacter()

    when:
      builder.withEscapeChar(escapeChar)

    then:
      builder.escapeChar == escapeChar

    when:
      CSVParser parser = builder.build()

    then:
      parser.escape == escapeChar
  }

  def 'strict quotes are set correctly'() {
    given:
      def strictQuotes = true

    when:
      builder.withStrictQuotes(strictQuotes)

    then:
      builder.strictQuotes

    when:
      CSVParser parser = builder.build()

    then:
      parser.strictQuotes
  }

  def 'ignore leading white space is set correctly'() {
    given:
      def ignoreLeadingWhiteSpace = true

    when:
      builder.withIgnoreLeadingWhiteSpace(ignoreLeadingWhiteSpace)

    then:
      builder.ignoreLeadingWhiteSpace

    when:
      CSVParser parser = builder.build()

    then:
      parser.ignoreLeadingWhiteSpace
  }

  def 'ignore quotations is set correctly'() {
    given:
      def ignoreQuotations = true

    when:
      builder.withIgnoreQuotations(ignoreQuotations)

    then:
      builder.ignoreQuotations

    when:
      CSVParser parser = builder.build()

    then:
      parser.ignoreQuotations
  }

  def 'null field indicator is set correctly'() {
    given:
      def nullFieldIndicator = BOTH

    when:
      builder.withFieldAsNull(nullFieldIndicator)

    then:
      builder.nullFieldIndicator() == nullFieldIndicator

    when:
      CSVParser parser = builder.build()

    then:
      parser.nullFieldIndicator() == nullFieldIndicator
  }
}
