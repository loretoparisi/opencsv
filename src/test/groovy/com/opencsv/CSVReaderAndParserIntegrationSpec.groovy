package com.opencsv

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * The purpose of this class is to test the CSVReader with different
 * (default) parsers and simple strings to make
 * sure they work the same.
 */
class CSVReaderAndParserIntegrationSpec extends Specification {

    @Shared
    RFC4180ParserBuilder rfc4180ParserBuilder = new RFC4180ParserBuilder();
    @Shared
    CSVParserBuilder csvParserBuilder = new CSVParserBuilder();

    @Shared
    ICSVParser rfc4180Parser = rfc4180ParserBuilder.build();
    @Shared
    ICSVParser csvParser = csvParserBuilder.build();

    @Unroll
    def 'parsing with #parserName'() {
        given:
        StringBuilder sb = new StringBuilder(ICSVParser.INITIAL_READ_SIZE);
        sb.append("a,b,c").append("\n");   // standard case
        sb.append("a,\"b,b,b\",c").append("\n");  // quoted elements
        sb.append(",,").append("\n"); // empty elements
        sb.append("a,\"PO Box 123,\nKippax,ACT. 2615.\nAustralia\",d.\n");
        StringReader sr = new StringReader(sb.toString());

        CSVReaderBuilder builder = new CSVReaderBuilder(sr);
        CSVReader reader = builder.withCSVParser(parser).build()

        expect:

        reader.readNext() == ["a", "b", "c"]
        reader.readNext() == ["a", "b,b,b", "c"]
        reader.readNext() == ["", "", ""]
        reader.readNext() == ["a", "PO Box 123,\n" +
                "Kippax,ACT. 2615.\n" +
                "Australia", "d."]
        where:
        parser        | parserName
        csvParser     | "CSVParser"
        rfc4180Parser | "RFC4180Parser"

    }
}
