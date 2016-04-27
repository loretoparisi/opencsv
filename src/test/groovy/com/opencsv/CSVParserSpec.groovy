package com.opencsv

import spock.lang.Ignore
import spock.lang.Specification

import static com.opencsv.CSVParser.*

class CSVParserSpec extends Specification {

  def 'line is parsed correctly'() {
    given:
      def parser = new CSVParser()
    and:
      def input = 'This, is, a, test.'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 4
      output[0] == 'This'
      output[1] == ' is'
      output[2] == ' a'
      output[3] == ' test.'
  }

  def 'string is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = "a,b,c"

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'

    and:
      !parser.pending
  }

  def 'quoted string is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = '"a","b","c"'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'

    and:
      !parser.pending
  }

  def 'quoted string with spaces is parsed correctly'() {
    given:
      def parser = new CSVParser(
          DEFAULT_SEPARATOR,
          DEFAULT_QUOTE_CHARACTER,
          DEFAULT_ESCAPE_CHARACTER,
          true,
          false
      )

    and:
      def input = ' "a" , "b" , "c" '

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'
  }

  def 'string with internal quote is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = 'a,123"4"567,c'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '123"4"567'
      output[2] == 'c'
  }

  def 'quoted string with commas is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = "a,\"b,b,b\",c"

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b,b,b'
      output[2] == 'c'
  }

  def 'quoted string with defined separator is parsed correctly'() {
    given:
      def parser = new CSVParser(':'.toCharacter())

    and:
      def input = 'a:"b:b:b":c'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b:b:b'
      output[2] == 'c'
  }

  def 'quoted string with defined separator and quote is parsed correctly'() {
    given:
      def parser = new CSVParser(':'.toCharacter(), "'".toCharacter())

    and:
      def input = "a:'b:b:b':c"

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b:b:b'
      output[2] == 'c'
  }

  def 'empty elements are parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = ',,'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output.every { it.empty }
  }

  def 'multiline string with internal quotes is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input =
          '''|a,"PO Box 123,
             |Kippax,ACT. 2615.
             |Australia",d.\n'''.stripMargin()
    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '''|PO Box 123,
                      |Kippax,ACT. 2615.
                      |Australia'''.stripMargin()
      output[2] == 'd.\n'
  }

  def 'multiline string with internal quotes and carriage returns is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input =
          '''|a,"PO Box 123,\r
             |Kippax,ACT. 2615.\r
             |Australia",d.\n'''.stripMargin()
    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '''|PO Box 123,\r
                      |Kippax,ACT. 2615.\r
                      |Australia'''.stripMargin()
      output[2] == 'd.\n'
  }

  def 'string with double quote as data element is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = 'a,"""",c'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1].size() == 1
      output[1] == '"'
      output[2] == 'c'
  }

  def 'string with escaped double quote as data element is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = '''"test","this,test,is,good",""test"",""quote""'''

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 4
      output[0] == 'test'
      output[1] == 'this,test,is,good'
      output[2] == '"test"'
      output[3] == '"quote"'
  }

  def 'string with internal quoted characters is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = 'Glen "The Man" Smith,Athlete,Developer\n'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == '''Glen "The Man" Smith'''
      output[1] == 'Athlete'
      output[2] == 'Developer\n'
  }

  def 'string with multiple quotes is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = '"""""","test"\n'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 2
      output[0] == '""'
      output[1] == 'test"\n'
  }

  def 'tricky string is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = '''|"a
                     |b",b,"
                     |d",e
                     |'''.stripMargin()

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 4
      output[0] == '''|a
                      |b'''.stripMargin()
      output[1] == 'b'
      output[2] == '''|
                      |d'''.stripMargin()
      output[3] == '''|e
                      |'''.stripMargin()
  }

  def 'string with multiple lines inside quotes is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = '''|Small test,"This is a test across
                     |two lines."'''.stripMargin()

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 2
      output[0] == 'Small test'
      output[1] == '''|This is a test across
                      |two lines.'''.stripMargin()
  }

  def 'string with strict quote set is parsed correctly'() {
    given:
      def parser = new CSVParser(','.toCharacter(), '"'.toCharacter(), '\\'.toCharacter(), true)

    and:
      def input = '"a","b","c"'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'
  }

  def 'string with strict quote unset is parsed correctly'() {
    given:
      def parser = new CSVParser(','.toCharacter(), '"'.toCharacter(), '\\'.toCharacter(), false)

    and:
      def input = '"a","b","c"'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'
  }

  def 'string with strict quote set and spaces and tabs is parsed correctly'() {
    given:
      def parser = new CSVParser(','.toCharacter(), '\"'.toCharacter(), '\\'.toCharacter(), true)

    and:
      def input = ''' \t      "a","b"      \t       ,   "c"   '''

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == 'b'
      output[2] == 'c'
  }

  def 'string with strict quote unset and spaces and tabs is parsed correctly'() {
    given:
      def parser = new CSVParser(','.toCharacter(), '\"'.toCharacter(), '\\'.toCharacter(), false)

    and:
      def input = ''' \t      "a","b"      \t       ,   "c"   '''

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'a'
      output[1] == '''b"      \t       '''
      output[2] == 'c"   '
  }

  def 'string with strict quote set and garbage is parsed wll'() {
    given:
      def parser = new CSVParser(','.toCharacter(), '"'.toCharacter(), '\\'.toCharacter(), true)

    and:
      def input = '''abc',!@#",\\""   xyz,'''

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == ''
      output[1] == ',"'
      output[2] == ''
  }

  def 'string is parsed well when quotations are ignored'() {
    given:
      def parser = new CSVParser(
          DEFAULT_SEPARATOR,
          DEFAULT_QUOTE_CHARACTER,
          DEFAULT_ESCAPE_CHARACTER,
          DEFAULT_STRICT_QUOTES,
          DEFAULT_IGNORE_LEADING_WHITESPACE,
          true
      )

    and:
      def input = '''Bob,test",Beaumont,TX'''

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 4
      output[0] == 'Bob'
      output[1] == 'test'
      output[2] == 'Beaumont'
      output[3] == 'TX'
  }

  def 'exception is thrown when quotations are not ignored'() {
    given:
      def parser = new CSVParser(
          DEFAULT_SEPARATOR,
          DEFAULT_QUOTE_CHARACTER,
          DEFAULT_ESCAPE_CHARACTER,
          DEFAULT_STRICT_QUOTES,
          DEFAULT_IGNORE_LEADING_WHITESPACE,
          false
      )

    and:
      def input = '''Bob,test",Beaumont,TX'''

    when:
      parser.parseLine(input)

    then:
      def e = thrown(IOException)
      e.message == 'Un-terminated quoted field at end of CSV line'
  }

  def 'exception is thrown if string ends inside a quoted string'() {
    given:
      def parser = new CSVParser()

    and:
      def input = 'This,is a "bad line to parse.'

    when:
      parser.parseLine(input)

    then:
      def e = thrown(IOException)
      e.message == 'Un-terminated quoted field at end of CSV line'
  }

  def "quotes across multiple lines are allowed when 'parseLineMultiUsed'"() {
    given:
      def parser = new CSVParser()

    and:
      def input = 'This,"is a "good" line\\\\ to parse'

    when:
      def output = parser.parseLineMulti(input)

    then:
      output.size() == 1
      output[0] == 'This'
      parser.pending

    when:
      input = 'because we are using parseLineMulti."'
      output = parser.parseLineMulti(input)

    then:
      output.size() == 1
      output[0] == '''|is a "good" line\\ to parse
                      |because we are using parseLineMulti.'''.stripMargin()
      !parser.pending
  }

  def "pending is cleared after call to 'parseLine'"() {
    given:
      def parser = new CSVParser()

    and:
      def input = 'This,"is a "good" line\\\\ to parse'

    when:
      def output = parser.parseLineMulti(input)

    then:
      output.size() == 1
      output[0] == 'This'
      parser.pending

    when:
      input = 'because we are using parseLineMulti.'
      output = parser.parseLine(input)

    then:
      output.size() == 1
      output[0] == 'because we are using parseLineMulti.'
      !parser.pending
  }

  def 'pending is false when null passed to multiline parse'() {
    given:
      def parser = new CSVParser()

    and:
      def input = 'This,"is a "goo\\d" line\\\\ to parse\\'

    when:
      def output = parser.parseLineMulti(input)

    then:
      output.size() == 1
      output[0] == 'This'
      parser.pending

    when:
      input = null
      output = parser.parseLineMulti(input)

    then:
      output.size() == 1
      output[0] == '''|is a "good" line\\ to parse
                      |'''.stripMargin()
      !parser.pending
  }

  def "spaces at the end of quoted string do not count if 'strictQuotes' is set to true"() {
    given:
      def parser = new CSVParser(
          DEFAULT_SEPARATOR,
          DEFAULT_QUOTE_CHARACTER,
          DEFAULT_ESCAPE_CHARACTER,
          true
      )

    and:
      def input = '"Line with", "spaces at end"  '

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 2
      output[0] == 'Line with'
      output[1] == 'spaces at end'
  }

  def 'null is returned if null passed'() {
    given:
      def parser = new CSVParser()

    and:
      def input = null

    when:
      def output = parser.parseLine(input)

    then:
      output == null
  }

  def 'check escapable characters'() {
    given:
      def parser = new CSVParser()

    and:
      def input = '\\\\1\\2\\"3\\' // \\1\2\"\3\

    and:
      assert input
      assert input.size() == 9

    expect:
      parser.isNextCharacterEscapable(input, inQuotes, index) == escapable

    where:
      inQuotes | index | escapable
      true     | 0     | true
      false    | 0     | false
      true     | 1     | false
      false    | 1     | false
      true     | 3     | false
      false    | 3     | false
      true     | 5     | true
      false    | 5     | false
      true     | 8     | false
      false    | 8     | false
  }

  def 'string with whitespace before escape is parsed correctly'() {
    given:
      def parser = new CSVParser()

    and:
      def input = '"this", "is","a test"'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 3
      output[0] == 'this'
      output[1] == 'is'
      output[2] == 'a test'
  }

  def 'quote and escape characters cannot be the same'() {
    when:
      new CSVParser(DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER, DEFAULT_QUOTE_CHARACTER)

    then:
      def e = thrown(UnsupportedOperationException)
      e.message == 'The separator, quote, and escape characters must be different!'
  }

  def 'quote and escape characters can be the same if both null'() {
    when:
      new CSVParser(DEFAULT_SEPARATOR, NULL_CHARACTER, NULL_CHARACTER)

    then:
      noExceptionThrown()
  }

  def 'separator character cannot be null'() {
    when:
      new CSVParser(NULL_CHARACTER)

    then:
      def e = thrown(UnsupportedOperationException)
      e.message == 'The separator character must be defined!'
  }

  def 'separator and escape characters cannot be the same'() {
    when:
      new CSVParser(DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER, DEFAULT_SEPARATOR)

    then:
      def e = thrown(UnsupportedOperationException)
      e.message == 'The separator, quote, and escape characters must be different!'
  }

  def 'separator and quote characters cannot be the same'() {
    when:
      new CSVParser(DEFAULT_SEPARATOR, DEFAULT_SEPARATOR, DEFAULT_ESCAPE_CHARACTER);

    then:
      def e = thrown(UnsupportedOperationException)
      e.message == 'The separator, quote, and escape characters must be different!'
  }

  def 'parser handles null in string'() {
    given:
      def parser = new CSVParser()

    and:
      def input = 'because we are using\0 parseLineMulti.'

    when:
      def output = parser.parseLine(input)

    then:
      output.size() == 1
      output[0] == 'because we are using\0 parseLineMulti.'
  }

  def 'field is ended at quote with strict quotes'() {
    given:
      def builder = new CSVParserBuilder()
      def parser = builder.withStrictQuotes(true).build()

    and:
      def input = '"one","t"wo,"three"'

    when:
      def output = parser.parseLineMulti(input)

    then:
      output.size() == 3
      output[0] == 'one'
      output[1] == 't'
      output[2] == 'three'
  }

  def 'field is ended at quote with escaped quote in the middle'() {
    given:
      def builder = new CSVParserBuilder()
      def parser = builder.withStrictQuotes(true).build()

    and:
      def input = '"one","t""w"o,"three"'

    when:
      def output = parser.parseLineMulti(input)

    then:
      output.size() == 3
      output[0] == 'one'
      output[1] == 't"w'
      output[2] == 'three'
  }

  def 'non strict quote allows embedded escape quote'() {
    given:
      def builder = new CSVParserBuilder()
      def parser = builder.withStrictQuotes(false).build()

    and:
      def input = '"one","t""wo","three"'

    when:
      def output = parser.parseLineMulti(input)

    then:
      output.size() == 3
      output[0] == 'one'
      output[1] == 't"wo'
      output[2] == 'three'
  }

  def 'non strict quote allows embedded quote'() {
    given:
      def builder = new CSVParserBuilder();
      def parser = builder.withStrictQuotes(false).build();

    and:
      def input = '"one",t""wo,"three"'

    when:
      def output = parser.parseLineMulti(input)

    then:
      output.size() == 3
      output[0] == 'one'
      output[1] == 't"wo'
      output[2] == 'three'
  }

  @Ignore('This spec should be used for Excel CSV Parser')
  def 'excel generated string is parsed'() {
    given:
      def builder = new CSVParserBuilder()
      def parser = builder.withStrictQuotes(true).build()

    and:
      def input = '"\\""",\\,\\,"""",""","'

    when:
      def output = parser.parseLine(input)

    then:
      output[0] == '\\"'
  }
}
