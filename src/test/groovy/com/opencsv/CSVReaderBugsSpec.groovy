package com.opencsv

import spock.lang.Specification

import static com.opencsv.CSVParser.*
import static com.opencsv.CSVReader.DEFAULT_SKIP_LINES

class CSVReaderBugsSpec extends Specification {


  def 'bug 106 line with carriage return and new line with strict quotes'() {
    given:
      def input = '"a","123\r\n4567","c"'

    and:
      def reader = new CSVReader(
          new StringReader(input),
          DEFAULT_SEPARATOR,
          DEFAULT_QUOTE_CHARACTER,
          DEFAULT_ESCAPE_CHARACTER,
          DEFAULT_SKIP_LINES,
          true,
          DEFAULT_IGNORE_LEADING_WHITESPACE,
          true
      )

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[0].size() == 1
      output[1] == '123\r\n4567'
      output[2] == 'c'
  }
}
