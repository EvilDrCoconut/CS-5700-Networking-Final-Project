package TransportProtocol.Data;

import TransportProtocol.Utils;

/**
 * Represents the number of bytes in the payload, from 0 to 512. Represented with 2 bytes of data in
 * header.
 */
public class Length implements BinaryRepresentable {

  private final int size;

  /**
   * Constructor. Size must be [0,512].
   *
   * @param size payload size
   */
  public Length(int size) {
    if (size > 512 || size < 0) {
      throw new IllegalArgumentException("Invalid length value.");
    }
    this.size = size;
  }

  public int getSize() {
    return size;
  }

  /**
   * Last segment if payload size less than 512.
   *
   * @return is last segment?
   */
  public boolean lastSegment() {
    return size < 512;
  }

  @Override
  public boolean[] toBinary() {
    return Utils.numberToBinaryArray(size, 16);
  }

  @Override
  public String toString() {
    return "Length{" +
        "length=" + size +
        '}';
  }
}
