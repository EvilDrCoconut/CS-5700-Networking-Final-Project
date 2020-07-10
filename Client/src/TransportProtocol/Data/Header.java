package TransportProtocol.Data;

import TransportProtocol.Utils;
import java.util.Arrays;

/**
 * Class for transport protocol header. Contains the sequence number, the length of payload, the
 * type of message, and the window size.
 */
public class Header implements BinaryRepresentable {

  public static final int SIZE = 4;
  private final Sequence sequence;
  private final Window window;
  private final Length length;
  private final Type type;

  public Header(int windowSize, int length, Type type, Sequence sequence) {
    this.window = new Window(windowSize);
    this.length = new Length(length);
    this.type = type;
    this.sequence = sequence;
  }


  /**
   * Get header info from byte representation using bit shifts and conversion.
   */
  public Header(byte[] headerByte) {

    // Shift 3 to right, truncate last 3 bits, to get 5 bit type
    int type = Byte.toUnsignedInt((byte) (headerByte[0] >> 3));
    // mod 8 to remove all bits more significant than the third
    int wind = Byte.toUnsignedInt((byte) (headerByte[0] % 8));
    // Simple, just the second byte
    int lastSequence = Byte.toUnsignedInt(headerByte[1]);
    // Third byte * 256 + last byte to get int value of last two bytes.
    int length = Byte.toUnsignedInt(headerByte[2]) * 256 + Byte.toUnsignedInt(headerByte[3]);

    this.sequence = new Sequence(lastSequence);
    this.length = new Length(length);
    this.window = new Window(wind);
    if (type == 0x1) {
      this.type = Type.DATA;
    } else if (type == 0x2) {
      this.type = Type.ACK;
    } else {
      throw new IllegalArgumentException("Unknown Type Byte.");
    }
  }

  public Sequence getSequence() {
    return sequence;
  }

  public Window getWindow() {
    return window;
  }

  public Length getLength() {
    return length;
  }

  public Type getType() {
    return type;
  }

  /**
   * Assemble new boolean array from each part. Header is 4 bytes, 2 bytes for length, 1 byte for
   * sequence, 3 bits for win, 5 bits for type.
   *
   * @return combined binary representation
   */
  @Override
  public boolean[] toBinary() {
    return Utils.combineBoolean(
        Utils.combineBoolean(
            Utils.combineBoolean(type.toBinary(), window.toBinary()), sequence.toBinary()),
        length.toBinary());
  }

  @Override
  public String toString() {
    return "Header{" +
        "sequence=" + sequence +
        ", window=" + window +
        ", length=" + length +
        ", type=" + type +
        '}';
  }
}
