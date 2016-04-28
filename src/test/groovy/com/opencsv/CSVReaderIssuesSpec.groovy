package com.opencsv

import spock.lang.Specification

import static java.nio.channels.Channels.newChannel
import static java.nio.channels.Channels.newInputStream
import static java.nio.charset.Charset.forName

class CSVReaderIssuesSpec extends Specification {


  def 'issue 2992134 out of place quotes'() {
    given:
      def input = 'a,b,c,ddd\\"eee\nf,g,h,"iii,jjj"'

    and:
      def reader = new CSVReader(new StringReader(input))

    when:
      def output = reader.readNext()

    then:
      output.size() == 4
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'
      output[3] == 'ddd"eee'
  }

  def 'issue 102'() {
    given:
      def input = '"",a\n"",b\n'

    and:
      def reader = new CSVReader(new StringReader(input))

    when:
      def output = reader.readNext()

    then:
      output.size() == 2
      output[0].empty
      output[1] == 'a'

    when:
      output = reader.readNext()

    then:
      output.size() == 2
      output[0].empty
      output[1] == 'b'
  }

  def 'issue 108 - reader work well with channels'() {
    given:
      def bytes = 'name\r\nvalue\r\n'.getBytes('UTF-8')
      def bais = new ByteArrayInputStream(bytes)
      def ch = newChannel(bais)
      def is = newInputStream(ch)
      def input = new InputStreamReader(is, forName('UTF-8'))

    and:
      def reader = new CSVReaderBuilder(input).withVerifyReader(false).build()

    expect:
      reader.readAll().size() == 2
  }
}
