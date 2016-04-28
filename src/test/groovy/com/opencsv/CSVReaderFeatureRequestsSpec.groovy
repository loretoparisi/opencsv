package com.opencsv

import spock.lang.Specification

import static com.opencsv.enums.CSVReaderNullFieldIndicator.*

class CSVReaderFeatureRequestsSpec extends Specification {
  def 'feature request 60 - by default empty fields are blank'() {
    given:
      def input = ',,,"",'

    and:
      def reader = new CSVReaderBuilder(new StringReader(input)).build()

    when:
      def output = reader.readNext()

    then:
      output.size() == 5
      output.every { it.empty }
  }

  def 'feature request 60 - try empty fields as null'() {
    given:
      def input = ',,,"",'

    and:
      def reader = new CSVReaderBuilder(new StringReader(input)).withFieldAsNull(EMPTY_SEPARATORS).build()

    when:
      def output = reader.readNext()

    then:
      output.size() == 5
      output[0] == null
      output[1] == null
      output[2] == null
      output[3] == ''
      output[4] == null
  }

  def 'feature request 60 - try empty delimited fields as null'() {
    given:
      def input = ',,,"",'

    and:
      def reader = new CSVReaderBuilder(new StringReader(input)).withFieldAsNull(EMPTY_QUOTES).build()

    when:
      def output = reader.readNext()

    then:
      output.size() == 5
      output[0].empty
      output[1].empty
      output[2].empty
      output[3] == null
      output[4].empty
  }

  def 'feature request 60 - try empty fields (delimited or not) as null'() {
    given:
      def input = ',,,"",'

    and:
      def reader = new CSVReaderBuilder(new StringReader(input)).withFieldAsNull(BOTH).build()

    when:
      def output = reader.readNext()

    then:
      output.size() == 5
      output.every { it == null }
  }


}
