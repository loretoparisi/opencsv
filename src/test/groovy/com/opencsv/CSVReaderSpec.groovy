package com.opencsv

import spock.lang.Shared
import spock.lang.Specification

import static com.opencsv.CSVParser.*
import static com.opencsv.CSVReader.DEFAULT_SKIP_LINES

class CSVReaderSpec extends Specification {

  @Shared
  def input =
      '''|a,b,c
         |a,"b,b,b",c
         |,,
         |a,"PO Box 123,
         |Kippax,ACT. 2615.
         |Australia",d.
         |"Glen ""The Man"" Smith",Athlete,Developer
         |"""""","test"
         |"a
         |b",b,"
         |d",e'''.stripMargin()

  def 'lines are parsed correctly'() {
    given:
      def reader = new CSVReaderBuilder(new StringReader(input)).build()

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'

    when:
      output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b,b,b'
      output[2] == 'c'

    when:
      output = reader.readNext()

    then:
      output.size() == 3
      output.every { it.empty }

    when:
      output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '''|PO Box 123,
                      |Kippax,ACT. 2615.
                      |Australia'''.stripMargin()
      output[2] == 'd.'

    when:
      output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'Glen "The Man" Smith'
      output[1] == 'Athlete'
      output[2] == 'Developer'

    when:
      output = reader.readNext()

    then:
      output.size() == 2
      output[0] == '""'
      output[1] == 'test'

    when:
      output = reader.readNext()

    then:
      output.size() == 4
      output[0] == '''|a
                      |b'''.stripMargin()
      output[1] == 'b'
      output[2] == '''|
                      |d'''.stripMargin()
      output[3] == 'e'

    when:
      output = reader.readNext()

    then:
      output == null
  }

  def 'reader can handle null in string'() {
    given:
      def input = 'a,\0b,c'

    and:
      def reader = new CSVReaderBuilder(new StringReader(input)).build()

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '\0b'
      output[1][0].toCharacter().charValue() == 0 as char
      output[2] == 'c'
  }

  def 'lines are parsed correctly with strict quote'() {
    given:
      def reader = new CSVReader(new StringReader(input), ','.toCharacter(), '"'.toCharacter(), true)

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0].empty
      output[1].empty
      output[2].empty

    when:
      output = reader.readNext()

    then:
      output.size() == 3
      output[0].empty
      output[1] == 'b,b,b'
      output[2].empty

    when:
      output = reader.readNext()

    then:
      output.size() == 3
      output.every { it.empty }

    when:
      output = reader.readNext()

    then:
      output.size() == 3
      output[0].empty
      output[1] == '''|PO Box 123,
                      |Kippax,ACT. 2615.
                      |Australia'''.stripMargin()
      output[2].empty

    when:
      output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'Glen "The Man" Smith'
      output[1].empty
      output[2].empty

    when:
      output = reader.readNext()

    then:
      output.size() == 2
      output[0] == '""'
      output[1] == 'test'

    when:
      output = reader.readNext()

    then:
      output.size() == 4
      output[0] == '''|a
                      |b'''.stripMargin()
      output[1].empty
      output[2] == '''|
                      |d'''.stripMargin()
      output[3].empty

    when:
      output = reader.readNext()

    then:
      output == null
  }

  def 'read all reads all the lines'() {
    given:
      def reader = new CSVReader(new StringReader(input), ','.toCharacter(), '"'.toCharacter(), true)

    expect:
      reader.readAll().size() == 7
  }

  def 'optional constructors are setting values correctly'() {
    given:
      def input = '''|a\tb\tc
                     |a\t'b\tb\tb'\tc'''.stripMargin()

    and:
      def reader = new CSVReader(new StringReader(input), '\t'.toCharacter(), "'".toCharacter())

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'

    when:
      output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b\tb\tb'
      output[2] == 'c'
  }

  def 'reader works well with tab separator'() {
    given:
      def input = 'a\tb\tc\n'

    and:
      def reader = new CSVReader(new StringReader(input), '\t'.toCharacter())

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'
  }

  def 'reader skips lines correctly'() {
    given:
      def input = '''|Skip this line\t with tab
                     |And this line too
                     |a\t'b\tb\tb'\tc'''.stripMargin()

    and:
      def reader = new CSVReader(new StringReader(input), '\t'.toCharacter(), "'".toCharacter(), 2)

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b\tb\tb'
      output[2] == 'c'
  }

  def 'lines and records are read correctly'() {
    given:
      def input = '''|Skip this line\t with tab
                     |And this line too
                     |a,b,c
                     |
                     |a,"b
                     |b",c'''.stripMargin()

    and:
      def reader = new CSVReaderBuilder(new StringReader(input)).
          withCSVParser(new CSVParser()).
          withSkipLines(2).
          build()

      assert 0l == reader.linesRead
      assert 0l == reader.recordsRead

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'

      reader.linesRead == 3
      reader.recordsRead == 1

    when:
      output = reader.readNext()

    then:
      output.size() == 1
      output[0].empty

      reader.linesRead == 4
      reader.recordsRead == 2

    when:
      output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '''|b
                      |b'''.stripMargin()
      output[2] == 'c'

      reader.linesRead == 6
      reader.recordsRead == 3

    when:
      output = reader.readNext()

    then:
      output == null

      reader.linesRead == 6
      reader.recordsRead == 3
  }

  def 'lines with different escape are skipped'() {
    given:
      def input = '''|Skip this line?t with tab
                     |And this line too
                     |a\t'b\tb\tb'\t'c\''''.stripMargin()

    and:
      def reader = new CSVReader(new StringReader(input), '\t'.toCharacter(), "'".toCharacter(), '?'.toCharacter(), 2)

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b\tb\tb'
      output[2] == 'c'
  }

  def 'non-quoted line with three elements is parsed correctly'() {
    given:
      def input = 'a,1234567,b'

    and:
      def reader = new CSVReader(new StringReader(input))

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '1234567'
      output[2] == 'b'
  }

  def 'single quote is recognized as data element'() {
    given:
      def input = "a,'''',c"

    and:
      def reader = new CSVReader(new StringReader(input), ','.toCharacter(), "'".toCharacter())

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == "'"
      output[1].size() == 1
      output[2] == 'c'
  }

  def 'single quote with empty field is recognized as data element'() {
    given:
      def input = "a,'',c"

    and:
      def reader = new CSVReader(new StringReader(input), ','.toCharacter(), "'".toCharacter())

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == ''
      output[1].size() == 0
      output[2] == 'c'
  }

  def 'test if spaces at the end of string are ignored'() {
    given:
      def input = '"a","b","c"    '

    and:
      def reader = new CSVReader(new StringReader(input), DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER, true)

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'
  }

  def 'input with escaped quote is parsed well'() {
    given:
      def input = 'a,"123\\"4567",c'

    and:
      def reader = new CSVReader(new StringReader(input))

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '123"4567'
      output[2] == 'c'
  }

  def 'escaped escape sign is parsed well'() {
    given:
      def input = 'a,"123\\\\4567",c'

    and:
      def reader = new CSVReader(new StringReader(input))

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '123\\4567'
      output[2] == 'c'
  }

  def 'single quote is recognized well when double quote is quote char'() {
    given:
      def input = "a,'',c"

    and:
      def reader = new CSVReader(new StringReader(input))

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1].size() == 2
      output[1] == "''"
      output[2] == 'c'
  }

  def 'quoted line is parsed correctly'() {
    given:
      def input = '"a","1234567","c"'

    and:
      def reader = new CSVReader(new StringReader(input), DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER, true)

    when:
      def output = reader.readNext()

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '1234567'
      output[2] == 'c'
  }

  def 'quote and escape characters must be different'() {
    when:
      new CSVReader(
          new StringReader(''),
          DEFAULT_SEPARATOR,
          DEFAULT_QUOTE_CHARACTER,
          DEFAULT_QUOTE_CHARACTER,
          DEFAULT_SKIP_LINES,
          DEFAULT_STRICT_QUOTES,
          DEFAULT_IGNORE_LEADING_WHITESPACE
      )

    then:
      def e = thrown(UnsupportedOperationException)
      e.message == 'The separator, quote, and escape characters must be different!'
  }

  def 'separator and escape characters must be different'() {
    when:
      new CSVReader(
          new StringReader(''),
          DEFAULT_SEPARATOR,
          DEFAULT_QUOTE_CHARACTER,
          DEFAULT_SEPARATOR,
          DEFAULT_SKIP_LINES,
          DEFAULT_STRICT_QUOTES,
          DEFAULT_IGNORE_LEADING_WHITESPACE
      )

    then:
      def e = thrown(UnsupportedOperationException)
      e.message == 'The separator, quote, and escape characters must be different!'
  }

  def 'separator and qupte characters must be different'() {
    when:
      new CSVReader(
          new StringReader(''),
          DEFAULT_SEPARATOR,
          DEFAULT_SEPARATOR,
          DEFAULT_ESCAPE_CHARACTER,
          DEFAULT_SKIP_LINES,
          DEFAULT_STRICT_QUOTES,
          DEFAULT_IGNORE_LEADING_WHITESPACE
      )

    then:
      def e = thrown(UnsupportedOperationException)
      e.message == 'The separator, quote, and escape characters must be different!'
  }

  def 'reader works correctly as iterator'() {
    given:
      def reader = new CSVReader(new StringReader(input))

    and:
      def expected = [
          ["a", "b", "c"],
          ["a", "b,b,b", "c"],
          ["", "", ""],
          ["a", "PO Box 123,\nKippax,ACT. 2615.\nAustralia", "d."],
          ["Glen \"The Man\" Smith", "Athlete", "Developer"],
          ["\"\"", "test"],
          ["a\nb", "b", "\nd", "e"],
      ] as List<String[]>

    expect:
      expected == reader.collect { it }
  }

  def 'no exception thrown on call to close'() {
    given:
      def reader = new CSVReader(new StringReader(input))

    when:
      reader.close()

    then:
      noExceptionThrown()
  }

  def 'reader acts as iterator'() {
    given:
      def reader = new CSVReader(new StringReader(input))

    expect:
      reader.iterator()
  }

  def 'iterator created from reader with null data should not throw any exception'() {
    given:
      def exception = new IOException('Test exception')
      def mockReader = GroovyMock(Reader)
      mockReader./read/(*_) >> { throw exception }

    and:
      def reader = new CSVReader(mockReader)

    when:
      reader.iterator()

    then:
      noExceptionThrown()
  }

  def 'attempt to read closed stream returns null'() {
    given:
      def input = new StringReader('')
      input.close()

    and:
      def reader = new CSVReader(input)

    when:
      def output = reader.readNext()

    then:
      output == null
  }
}
