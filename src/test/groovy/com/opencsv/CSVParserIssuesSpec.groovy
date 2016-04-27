package com.opencsv

import spock.lang.Specification

import static com.opencsv.CSVParser.*

class CSVParserIssuesSpec extends Specification {

  def 'issue 3314579'() {
    given:
      def parser = new CSVParser(';'.toCharacter(),
          DEFAULT_QUOTE_CHARACTER,
          DEFAULT_ESCAPE_CHARACTER,
          DEFAULT_STRICT_QUOTES,
          DEFAULT_IGNORE_LEADING_WHITESPACE,
          true
      )

    and:
      def input = '''RPO;2012;P; ; ; ;SDX;ACCESSORY WHEEL, 16", ALUMINUM, DESIGN 1'''

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 8
      output[0] == 'RPO'
      output[1] == '2012'
      output[2] == 'P'
      output[3] == ' '
      output[4] == ' '
      output[5] == ' '
      output[6] == 'SDX'
      output[7] == 'ACCESSORY WHEEL, 16", ALUMINUM, DESIGN 1'
  }

  def 'issue 2263439'() {
    given:
      def parser = new CSVParser(','.toCharacter(), "'".toCharacter())

    and:
      def input = "865,0,'AmeriKKKa\\'s_Most_Wanted','',294,0,0,0.734338696798625,'20081002052147',242429208,18448"

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 11
      output[0] == '865'
      output[1] == '0'
      output[2] == "AmeriKKKa's_Most_Wanted"
      output[3] == ''
      output[4] == '294'
      output[5] == '0'
      output[6] == '0'
      output[7] == '0.734338696798625'
      output[8] == '20081002052147'
      output[9] == '242429208'
      output[10] == '18448'
  }

  def 'issue 2859181'() {
    given:
      def parser = new CSVParser(';'.toCharacter())

    and:
      def input = '''field1;\\=field2;"""field3"""'''

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'field1'
      output[1] == '=field2'
      output[2] == '"field3"'
  }

  def 'issue 2726363'() {
    given:
      def parser = new CSVParser()

    and:
      def input = '''"804503689","London",""London"shop","address","116.453182","39.918884"'''

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 6
      output[0] == '804503689'
      output[1] == 'London'
      output[2] == '"London"shop'
      output[3] == 'address'
      output[4] == '116.453182'
      output[5] == '39.918884'
  }

  def 'issue 2958242'() {
    given:
      def parser = new CSVParser('\t'.toCharacter())

    and:
      def input = 'zo""har""at\t10-04-1980\t29\tC:\\\\foo.txt'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 4
      output[0] == 'zo"har"at'
      output[1] == '10-04-1980'
      output[2] == '29'
      output[3] == 'C:\\foo.txt'
  }

  def 'issue 93'() {
    given:
      def builder = new CSVParserBuilder()
      def parser = builder.withStrictQuotes(false).build()

    and:
      def input = '"",2'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 2
      output[0].empty
      output[1] == '2'
  }
}
