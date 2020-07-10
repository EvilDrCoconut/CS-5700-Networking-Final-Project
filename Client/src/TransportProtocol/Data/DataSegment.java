package TransportProtocol.Data;

import TransportProtocol.Utils;
import java.util.Arrays;

/**
 * Class which represents a segment with a payload, not an ACK.
 */
public class DataSegment {

  private final Header header;
  private final byte[] data;
  public static final int MAX_PAYLOAD = 512;

  public DataSegment(Header header, byte[] data) {
    this.header = header;
    this.data = Arrays.copyOf(data, data.length);
  }

  public Header getHeader() {
    return header;
  }

  public byte[] getData() {
    return Arrays.copyOf(data, data.length);
  }

  /**
   * Combines header bytes with payload bytes to get full byte representation.
   *
   * @return byte representation
   */
  public byte[] toBytes() {
    return Utils.combineByte(Utils.bytesFromBinary(header.toBinary()), data);
  }
}
