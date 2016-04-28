package com.opencsv

import spock.lang.Specification

import static com.opencsv.CSVReader.*
import static com.opencsv.enums.CSVReaderNullFieldIndicator.EMPTY_SEPARATORS

class CSVReaderBuilderSpec extends Specification {

  def 'default settings are preserved'() {
    given:
      def ioReader = Mock(Reader)
      def builder = new CSVReaderBuilder(ioReader)

      assert ioReader == builder.reader
      assert null == builder.csvParser
      assert DEFAULT_SKIP_LINES == builder.skipLines

    when:
      def reader = builder.build()

    then:
      reader.skipLines == DEFAULT_SKIP_LINES
      reader.keepCarriageReturns() == DEFAULT_KEEP_CR
      reader.verifyReader() == DEFAULT_VERIFY_READER
  }

  def 'exception thrown when null reader passed'() {
    when:
      new CSVReaderBuilder(null)

    then:
      def e = thrown(IllegalArgumentException)
      e.message == 'Reader may not be null'
  }

  def 'csv parser is set to null'() {
    given:
      def builder = new CSVReaderBuilder(Mock(Reader))

    when:
      builder.withCSVParser(Mock(CSVParser))
      builder.withCSVParser(null)

    then:
      builder.csvParser == null
  }

  def 'csv parser is set correctly'() {
    given:
      def parser = Mock(CSVParser)
      def builder = new CSVReaderBuilder(Mock(Reader)).withCSVParser(parser)

    expect:
      parser == builder.csvParser
      parser == builder.build().parser
  }

  def 'skip lines configuration is set correctly'() {
    given:
      def builder = new CSVReaderBuilder(Mock(Reader)).withSkipLines(actual)

    expect:
      expected == builder.skipLines
      expected == builder.build().skipLines

    where:
      expected | actual
      99       | 99
      0        | 0
      0        | -1
  }

  def 'keep CR configuration is set correctly'() {
    given:
      def builder = new CSVReaderBuilder(Mock(Reader)).withKeepCarriageReturn(true)

    expect:
      builder.keepCarriageReturn()
      builder.build().keepCarriageReturns()
  }

  def 'verify reader is set correctly'() {
    given:
      def builder = new CSVReaderBuilder(Mock(Reader))
      def reader = builder.withVerifyReader(false).build()

    expect:
      !reader.verifyReader()
  }

  def 'null field indicator is set correctly'() {
    given:
      def builder = new CSVReaderBuilder(Mock(Reader))
      def reader = builder.withFieldAsNull(EMPTY_SEPARATORS).build()

    expect:
      EMPTY_SEPARATORS == reader.parser.nullFieldIndicator()
  }
}
