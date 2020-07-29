import com.sun.tools.classfile.*;
import com.sun.tools.corba.se.idl.constExpr.*;
import com.sun.xml.internal.bind.v2.model.core.*;

import java.nio.*;
import java.util.*;
import java.util.Arrays;
import java.util.BitSet;

public class DNSSegment {

    /* First 12 bytes is the header section.
    1) 16-bit number that identifies the query. This is copied into the reply message to a query,
     allowing the client to match received replies with sent queries.
     2) Flags (4 bits long): 1 bit query/reply flag indicates whether the message is a query (0)
     or a reply(1)
     1 bit authoritative flag is set when a DNS server is an authoritiative server for a queried
     name. 1-bit recursion desired flag when a client desires that the DNS server perform
     recursion when it doesn't  have the record. 1 bit recursion available field is set if the
     DNS server supports recursion. In the header, there are 4 "number of" fields (indicated number
      of occurrences of the four types of data sections that follow the header)
      3) Number of questions: 19 bits
      4) Number of answer RRs: 19 bits
      5) Number of authority RRs: 19 bits
      6) Number of additional RRs: 19 bits

      Data section (payload)
      Question section: 1) Name field that contains the name being queried, 2) type field that
      indicates the type of question being asked about the name (type A, type NS, or type MX)
      Answer section: Resource records for the name that was originally queried; reply can return
       multiple RRs
       */
    //  query id between 0 and 65,535
    // Header information
    static final int HDR_NUM_FIELDS = 9; // number of fields in header
    static final int HDR_QID_FLD_SIZE = 16; // size(bits) of query id field after encoding
    static final int HDR_QRFLAG_FLD_SIZE = 1; // size(bits) of query/response field
    static final int HDR_AUTHFLAG_FLD_SIZE = 1; // size(bits) of authoritative/non-authoritative
    static final int HDR_REC_DESIRED_FLD_SIZE = 1; // size(bits) of recursion desired/not desired
    static final int HDR_REC_AVAIL_FLD_SIZE = 1; // size(bits) recursion avail/not desired
    static final int HDR_NUMQS_FLD_SIZE = 19; // size(bits) of no of questions field
    static final int HDR_NUM_ANS_RRS_FLD_SIZE = 19; // size(bits) of no of answer rrs field
    static final int HDR_NUM_AUTH_RRS_FLD_SIZE = 19; // size(bits) of no of auth rrs field
    static final int HDR_NUM_ADD_RRS_FLD_SIZE = 19; // size(bits) of no of additional rrs field
    static final int QTYPE_LEN = 3; // size(bits) of Qtype field
    static final int Q_LEN = 8; // size(bits) of query length field
    static final int MAX_Q_NAME_LEN_BYTES = 255; // max length in bytes of name in query name field
    static final int MAX_PAYLOAD_LEN_BYTES = 500;
    static Random queryIDRandomizer = new Random();

    // encoding
    static final int[] fieldSizes = new int[]{ // array used in header encoding
            HDR_QID_FLD_SIZE, HDR_QRFLAG_FLD_SIZE, HDR_AUTHFLAG_FLD_SIZE, HDR_REC_DESIRED_FLD_SIZE,
            HDR_REC_AVAIL_FLD_SIZE, HDR_NUMQS_FLD_SIZE, HDR_NUM_ANS_RRS_FLD_SIZE,
            HDR_NUM_AUTH_RRS_FLD_SIZE, HDR_NUM_ADD_RRS_FLD_SIZE};

    static final int HEADER_SIZE_IN_BYTES = 12; // header size
    private int queryId; // query or response Id number
    private int questionType; // "No question", "A", "NS", or "MX"/ 0, 1, 2, or 3
    private int queryResponseFlag;
    private int authFlag;
    private int recursionDesiredFlag;
    private int recursionAvailFlag;
    private int numQuestions;
    private int numAnsRRs;
    private int numAddRRs;
    private int payloadLength; // length in bytes of data payload
    private byte[] payload; // data payload
    private byte[] dnsSegmentHeader; // segment header encoded in byte array

    DNSSegment(int[] headerFields) {
        //   this.queryId = segFields[0]; // set query ID
        int offset = 0;
        this.dnsSegmentHeader = createDnsSegmentHeader(headerFields, offset); // create & set
        // segment
        // header
    }

    int getQueryId() {
        return this.queryId;
    }

    void setQueryResponseId(int sequence) {
        this.queryId = sequence;
    }

    int getPayloadLength() {
        return this.payloadLength;
    }

    void setPayloadLength(int length) {
        this.payloadLength = length;
    }

    byte[] getPayload() {
        return this.payload;
    }

    void setPayload(byte[] payload) {
        this.payload = payload;
    }

    byte[] getDnsSegmentHeader() {
        return this.dnsSegmentHeader;
    }

    void setSegmentHeader(final byte[] segmentHeader) {
        this.dnsSegmentHeader = segmentHeader;
    }

    private static BitSet binaryStringToBitSet(String binaryString, BitSet bitSet, int offset) {
        int stringLength = binaryString.length();
        for (int i = 0; i < stringLength; i++) { // go through each char in string
            if (binaryString.charAt(i) == '1') { // if the char is a 1
                bitSet.set(offset); // set that bit at the appropriate index in the bit set
            }
            offset += 1;
        }
        return bitSet;
    }

    static byte[] createDnsSegmentHeader(int[] segFields, int offset) {
        BitSet headerRemainderBits = new BitSet(96); // 12bytes * 8 = 96 bits
        // for each field in the header, translate from int to binary string
        for (int i = 0; i < HDR_NUM_FIELDS; i++) {
            String binaryString = Integer.toBinaryString(segFields[i]);
            int binaryStringLength = binaryString.length();
            offset += fieldSizes[i] - binaryStringLength; // determine padding needed
            // translate binary string to bitset and add to bitset
            headerRemainderBits = binaryStringToBitSet(binaryString, headerRemainderBits, offset);
            offset += binaryStringLength;
        }
        // convert bitset into 12-byte array
        byte[] headerArray = Arrays.copyOf(headerRemainderBits.toByteArray(), 12);
        return headerArray;
    }

    static String[] readSegmentHeader(byte[] headerArray) {
        BitSet headerBits = BitSet.valueOf(headerArray); // convert header array into bitset
        int offset = 0;
        String[] binaryStrings = new String[HDR_NUM_FIELDS];
        int bitIndex = 0;
        int stringEndIndex = 0;
        // for each header field
        for (int i = 0; i < HDR_NUM_FIELDS; i++) {
            StringBuilder binaryStringBuilder = new StringBuilder();
            stringEndIndex = offset + fieldSizes[i] - 1;
            // go through the bitset at the proper bit index based on the offset
            for (int j = bitIndex; j <= stringEndIndex; j++) {
                if (headerBits.get(j)) {
                    binaryStringBuilder.append(1);
                } else {
                    binaryStringBuilder.append(0);
                }
                bitIndex += 1;
            }
            // and convert each to a binary string
            binaryStrings[i] = binaryStringBuilder.toString();
            offset = stringEndIndex + 1;

        }
        return binaryStrings;
    }

    static int[] readHeaderFields(String[] binaryStrings) {
        int[] headerFields = new int[HDR_NUM_FIELDS];
        for (int i = 0; i < binaryStrings.length; i++) { // go through each binary string
            headerFields[i] = Integer.parseInt(binaryStrings[i], 2); // convert to integer
        }
        return headerFields;
    }

    static byte[] createQuerySegment(int questionType, int authFlag, int recursionDesiredFlag,
                                     int numQuestions, String domainName) {
        int queryId = queryIDRandomizer.ints(0, 65536)
                .findFirst()
                .getAsInt();
        int[] headerFields = new int[]{queryId, 0, authFlag, recursionDesiredFlag, 0,
                numQuestions, 0, 0, 0};

        DNSSegment querySegment = new DNSSegment(headerFields);

        byte[] payload = createQuestionPayload(questionType, domainName);
        byte[] qSegment = new byte[querySegment.getDnsSegmentHeader().length + payload.length];
        ByteBuffer qSegmentBuffer = ByteBuffer.wrap(qSegment);
        qSegmentBuffer.put(querySegment.getDnsSegmentHeader());
        qSegmentBuffer.put(payload);
        return qSegmentBuffer.array();
    }

    static byte[] createQuestionPayload(int questionType, String domainName) {
        int offset = 0;
        BitSet queryLengthBits = new BitSet(Q_LEN);
        byte[] queryName = domainName.getBytes();
        String queryNameByteLengthString = Integer.toBinaryString(queryName.length);
        int queryLengthBinStringLength = queryNameByteLengthString.length();
        offset += (8 - queryLengthBinStringLength);
        queryLengthBits = binaryStringToBitSet(queryNameByteLengthString, queryLengthBits, offset);
        byte[] queryNameLength = Arrays.copyOf(queryLengthBits.toByteArray(), 1);
        offset = 0;
        BitSet qTypeBits = new BitSet(QTYPE_LEN);
        String qTypeBinString = Integer.toBinaryString(questionType);
        int qTypeBinStringLength = qTypeBinString.length();
        offset += (3 - qTypeBinStringLength);
        qTypeBits = binaryStringToBitSet(qTypeBinString, qTypeBits, offset);
        byte[] qType = Arrays.copyOf(queryLengthBits.toByteArray(), 1);

        byte[] questionPayload = new byte[queryNameLength.length + queryName.length + qType.length];
        ByteBuffer segmentPayload = ByteBuffer.wrap(questionPayload);
        segmentPayload.put(queryNameLength);
        segmentPayload.put(queryName);
        segmentPayload.put(qType);
        return segmentPayload.array();
    }

    private static int bitSetToInteger(BitSet bitSet, int fieldLength) {
        int offset = 0;
        int bitIndex = 0;
        int stringEndIndex = 0;
        StringBuilder binaryStringBuilder = new StringBuilder();
        stringEndIndex = offset + fieldLength - 1;
        // go through the bitset at the proper bit index based on the offset
        for (int j = bitIndex; j <= stringEndIndex; j++) {
            if (bitSet.get(j)) {
                binaryStringBuilder.append(1);
            } else {
                binaryStringBuilder.append(0);
            }
            bitIndex += 1;
        }
        return Integer.parseInt(binaryStringBuilder.toString(), 2);
    }

    private static String getQuestionType(int qTypeInt) {
        String questionType = null;
        switch (qTypeInt) {
            case 0:
                questionType = "A";
                break;
            case 1:
                questionType = "NS";
                break;
            case 2:
                questionType = "MX";
        }
        return questionType;
    }

    static ArrayList<String> readQuestionPayload(byte[] questionPayload) {
        ArrayList<String> qStringAndQtype = new ArrayList<String>();
        byte[] queryLength = Arrays.copyOfRange(questionPayload, 0, 1);
        BitSet headerBits = BitSet.valueOf(queryLength);
        int qLen = bitSetToInteger(headerBits, Q_LEN);
        String queryString = new String(Arrays.copyOfRange(questionPayload, 1, (1 + qLen)));
        BitSet qTypeBits = BitSet.valueOf(new byte[]{questionPayload[-1]});
        int qType = bitSetToInteger(qTypeBits, QTYPE_LEN);
        qStringAndQtype.add(queryString);
        qStringAndQtype.add(getQuestionType(qType));
        return qStringAndQtype;
    }

    static byte[] createResponseSegment(byte[] querySegment) {
        byte[] qHeaderArray = Arrays.copyOfRange(querySegment, 0, 12);
        int[] qHeaderFields = readHeaderFields(readSegmentHeader(querySegment));
        // todo: need to know the server--if root server, if auth server, etc.
        int[] rHeaderFields = new int[]{qHeaderFields[0], 1, qHeaderFields[2], 1, 1, 1, 1, 0};

        return qHeaderArray; // todo: need to finish and return new response segment
    }
}

    /*

    static ArrayList<String> readResponsePayload(byte[] responsePayload) {
        // get the resource record toString?
        ArrayList<String> qStringAndQtype = new ArrayList<String>();
        byte[] queryLength = Arrays.copyOfRange(responsePayload, 0, 1);
        BitSet headerBits = BitSet.valueOf (queryLength);
        int qLen = bitSetToInteger(headerBits, Q_LEN);
        String queryString = new String(Arrays.copyOfRange(questionPayload, 1, (1 + qLen)));
        BitSet qTypeBits = BitSet.valueOf(new byte[] {questionPayload[-1]});
        int qType = bitSetToInteger(qTypeBits, QTYPE_LEN);
        qStringAndQtype.add(queryString);
        qStringAndQtype.add(getQuestionType(qType));
        return qStringAndQtype;
    }
} */

/*
            int queryId, int authFlag, int recursionDesiredFlag,
                                     int num, String domainName) {

        HDR_QID_FLD_SIZE, HDR_QRFLAG_FLD_SIZE, HDR_AUTHFLAG_FLD_SIZE, HDR_REC_DESIRED_FLD_SIZE,
                HDR_REC_AVAIL_FLD_SIZE, HDR_NUMQS_FLD_SIZE, HDR_NUM_ANS_RRS_FLD_SIZE,
                HDR_NUM_AUTH_RRS_FLD_SIZE, HDR_NUM_ADD_RRS_FLD_SIZE

        int responseId = queryId;

        // int queryId = queryIDRandomizer.ints(0, 65536)
        //  .findFirst()
        //  .getAsInt();
        int queryId = 12345;
        int[] headerFields = new int[] {responseId, 1, authFlag, recursionDesiredFlag, 0,
                numQuestions, 0, 0, 0};

        DNSSegment querySegment = new DNSSegment(headerFields);

        byte[] payload = createQuestionPayload(questionType, domainName);
        byte[] qSegment = new byte[querySegment.getDnsSegmentHeader().length + payload.length];
        ByteBuffer qSegmentBuffer = ByteBuffer.wrap(qSegment);
        qSegmentBuffer.put(querySegment.getDnsSegmentHeader());
        qSegmentBuffer.put(payload);
        return qSegmentBuffer.array();
}

}

 */

