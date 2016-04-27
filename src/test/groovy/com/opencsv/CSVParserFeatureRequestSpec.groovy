package com.opencsv

import spock.lang.Specification

import static com.opencsv.enums.CSVReaderNullFieldIndicator.*

class CSVParserFeatureRequestSpec extends Specification {

  def 'FR 60 by default empty fields are blank'() {
    given:
      def parser = new CSVParser()

    and:
      def input = ',,,"",'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 5
      output[0] == ''
      output[1] == ''
      output[2] == ''
      output[3] == ''
      output[4] == ''
  }

  def 'FR 60 empty fields are threaten as null'() {
    given:
      def builder = new CSVParserBuilder();
      def parser = builder.withFieldAsNull(EMPTY_SEPARATORS).build();

    and:
      def input = ', ,,"",'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 5
      output[0] == null
      output[1] == ' '
      output[2] == null
      output[3] == ''
      output[4] == null
  }

  def 'FR 60 empty delimited fields are threaten as null'() {
    given:
      def builder = new CSVParserBuilder();
      def parser = builder.withFieldAsNull(EMPTY_QUOTES).build();

    and:
      def input = '," ",,"",'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 5
      output[0] == ''
      output[1] == ' '
      output[2] == ''
      output[3] == null
      output[4] == ''
  }

  def 'FR 60 empty fields delimited or nor are threaten as null'() {
    given:
      def builder = new CSVParserBuilder();
      def parser = builder.withFieldAsNull(BOTH).build();

    and:
      def input = ',,,"",'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 5
      output.every { it == null }
  }
}
