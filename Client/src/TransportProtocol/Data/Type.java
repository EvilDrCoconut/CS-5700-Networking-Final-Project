package TransportProtocol.Data;

import TransportProtocol.Utils;

/**
 * The type of message. 0x1 for data, 0x2 for ACK. 5 bits, so could store 2^5 message types.
 */
public enum Type implements BinaryRepresentable {
  DATA(0x1),
  ACK(0x2);

  private final boolean[] binaryValue;

  /**
   * Construct with hex value, and convert to binary representation.
   *
   * @param val the hex value
   */
  Type(int val) {
    // 5 since takes up 5 bits in header.
    this.binaryValue = Utils.numberToBinaryArray(val, 5);
  }

  @Override
  public boolean[] toBinary() {
    return binaryValue;
  }
}
