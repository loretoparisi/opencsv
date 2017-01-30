package com.opencsv

import com.opencsv.enums.CSVReaderNullFieldIndicator
import spock.lang.Specification
import spock.lang.Unroll

class RFC4180ParserSpec extends Specification {
    private static final char SINGLE_QUOTE = '\''
    private static final char PERIOD = '.'

    def 'create a parser from the default constructor'() {
        when:
        RFC4180Parser parser = new RFC4180Parser();

        then:
        parser.getQuotechar() == ICSVParser.DEFAULT_QUOTE_CHARACTER;
        parser.getSeparator() == ICSVParser.DEFAULT_SEPARATOR;
        parser.nullFieldIndicator() == CSVReaderNullFieldIndicator.NEITHER;
    }

    def 'able to parse a simple line'() {
        given:
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.build()
        String testLine = "This,is,a,test"

        when:
        String[] values = parser.parseLine(testLine)

        then:
        values[0] == "This"
        values[1] == "is"
        values[2] == "a"
        values[3] == "test"
    }

    def 'able to parse a multiple line record'() {
        given:
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.build()
        String testLine = "\"This\",\"is\",\"a multiple \n line\",\"test\""

        when:
        String[] values = parser.parseLineMulti(testLine)

        then:
        values[0] == "This"
        values[1] == "is"
        values[2] == "a multiple \n line"
        values[3] == "test"
    }

    @Unroll
    def 'parsing #testLine yields values #expected1 #expected2 #expected3 and #expected4'(String testLine, String expected1, String expected2, String expected3, String expected4) {
        given:
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.build()

        expect:
        parser.parseLine(testLine) == [expected1, expected2, expected3, expected4]

        where:
        testLine                                               | expected1 | expected2 | expected3                          | expected4
        "This,is,a,test"                                       | "This"    | "is"      | "a"                                | "test"
        "7,seven,7.89,12/11/16"                                | "7"       | "seven"   | "7.89"                             | "12/11/16"
        "1,\"\\\"\"\",\"this is a quote \"\" character\",test" | "1"       | "\\\""    | "this is a quote \" character"     | "test"
        "2,\\ ,\"this is a comma , character\",two"            | "2"       | "\\ "     | "this is a comma , character"      | "two"
        "3,\\\\ ,this is a backslash \\ character,three"       | "3"       | "\\\\ "   | "this is a backslash \\ character" | "three"
        "5,\"21,34\",test comma,five"                          | "5"       | "21,34"   | "test comma"                       | "five"
        "8,\\',\"a big line with \n" +
                "multiple carriage returns\n" +
                "in it.\",eight"                               | "8"       | "\\'"     | "a big line with \n" +
                "multiple carriage returns\n" +
                "in it."                                                                                                    | "eight"
    }

    @Unroll
    def 'parsing #testLine with custom quote yields values #expected1 #expected2 #expected3 and #expected4'(String testLine, String expected1, String expected2, String expected3, String expected4) {
        given:
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.withQuoteChar(SINGLE_QUOTE).build()

        expect:
        parser.parseLine(testLine) == [expected1, expected2, expected3, expected4]

        where:
        testLine                                         | expected1 | expected2 | expected3                          | expected4
        "This,is,a,test"                                 | "This"    | "is"      | "a"                                | "test"
        "7,seven,7.89,12/11/16"                          | "7"       | "seven"   | "7.89"                             | "12/11/16"
        "1,'\\''','this is a quote '' character',test"   | "1"       | "\\'"     | "this is a quote ' character"      | "test"
        "2,\\ ,'this is a comma , character',two"        | "2"       | "\\ "     | "this is a comma , character"      | "two"
        "3,\\\\ ,this is a backslash \\ character,three" | "3"       | "\\\\ "   | "this is a backslash \\ character" | "three"
        "5,'21,34',test comma,five"                      | "5"       | "21,34"   | "test comma"                       | "five"
        "8,\\\",'a big line with \n" +
                "multiple carriage returns\n" +
                "in it.',eight"                          | "8"       | "\\\""    | "a big line with \n" +
                "multiple carriage returns\n" +
                "in it."                                                                                              | "eight"
    }

    @Unroll
    def 'parsing #testLine with custom separator yields values #expected1 #expected2 #expected3 and #expected4'(String testLine, String expected1, String expected2, String expected3, String expected4) {
        given:
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.withSeparator(PERIOD).build()

        expect:
        parser.parseLine(testLine) == [expected1, expected2, expected3, expected4]

        where:
        testLine                                               | expected1 | expected2 | expected3                          | expected4
        "This.is.a.test"                                       | "This"    | "is"      | "a"                                | "test"
        "7.seven.7,89.12/11/16"                                | "7"       | "seven"   | "7,89"                             | "12/11/16"
        "1.\"\\\"\"\".\"this is a quote \"\" character\".test" | "1"       | "\\\""    | "this is a quote \" character"     | "test"
        "2.\\ .\"this is a comma . character\".two"            | "2"       | "\\ "     | "this is a comma . character"      | "two"
        "3.\\\\ .this is a backslash \\ character.three"       | "3"       | "\\\\ "   | "this is a backslash \\ character" | "three"
        "5.\"21.34\".test comma.five"                          | "5"       | "21.34"   | "test comma"                       | "five"
        "8.\\'.\"a big line with \n" +
                "multiple carriage returns\n" +
                "in it.\".eight"                               | "8"       | "\\'"     | "a big line with \n" +
                "multiple carriage returns\n" +
                "in it."                                                                                                    | "eight"
    }

    def 'parser with nullfieldindicator'() {
        given:
        StringBuilder sb = new StringBuilder(ICSVParser.INITIAL_READ_SIZE)
        sb.append(", ,,\"\",")
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.withFieldAsNull(CSVReaderNullFieldIndicator.EMPTY_SEPARATORS).build()

        when:
        String[] items = parser.parseLine(sb.toString())

        then:
        items[0] == null
        items[1] == " "
        items[2] == null
        items[3] == ""
        items[4] == null
    }

    def 'parse a complex string'() {
        given:
        StringBuilder sb = new StringBuilder(ICSVParser.INITIAL_READ_SIZE)
        sb.append("1,'\\''','this is a quote '' character',test")
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.withQuoteChar(SINGLE_QUOTE).build()

        when:
        String[] items = parser.parseLine(sb.toString())

        then:
        items[0] == "1"
        items[1] == "\\'"
        items[2] == "this is a quote ' character"
        items[3] == "test"
    }

    @Unroll
    def 'Parser with NullFieldindicator of #nullField should return #string1 #string2 #string3 #string4 and #string5'(CSVReaderNullFieldIndicator nullField, String string1, String string2, String string3, String string4, String string5) {
        given:
        StringBuilder sb = new StringBuilder(ICSVParser.INITIAL_READ_SIZE)
        sb.append(", ,,\"\",")
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.withFieldAsNull(nullField).build()

        expect:

        parser.parseLine(sb.toString()) == [string1, string2, string3, string4, string5]

        where:
        nullField                                    | string1 | string2 | string3 | string4 | string5
        CSVReaderNullFieldIndicator.NEITHER          | ""      | " "     | ""      | ""      | ""
        CSVReaderNullFieldIndicator.EMPTY_SEPARATORS | null    | " "     | null    | ""      | null
        CSVReaderNullFieldIndicator.EMPTY_QUOTES     | ""      | " "     | ""      | null    | ""
        CSVReaderNullFieldIndicator.BOTH             | null    | " "     | null    | null    | null
    }

    def 'able to parse a field that has a single quote at the end'() {
        given:
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.build()
        String testLine = "line 1\""

        when:
        String[] values = parser.parseLine(testLine)

        then:
        values[0] == "line 1\""
        values.length == 1
    }

    def 'if given a null then return a null'() {
        given:
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.build()
        String testLine = null

        when:
        String[] values = parser.parseLine(testLine)

        then:
        values == null
    }

    def 'parse excel generated string'() {
        given:
        RFC4180ParserBuilder builder = new RFC4180ParserBuilder()
        RFC4180Parser parser = builder.build()
        String testLine = "\"\\\"\"\",\\,\\,\"\"\"\",\"\"\",\"";

        when:
        String[] values = parser.parseLine(testLine)

        then:
        // \" \ \ " ",
        values[0] == "\\\""
        values[1] == "\\"
        values[2] == "\\"
        values[3] == "\""
        values[4] == "\","
        values.length == 5
    }
}
