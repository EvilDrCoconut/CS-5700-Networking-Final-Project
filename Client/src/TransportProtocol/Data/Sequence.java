package TransportProtocol.Data;

import TransportProtocol.Utils;
import java.util.Objects;

/**
 * Represents the sequence number in the header. From 0 to 255, represented by 1 byte.
 */
public class Sequence implements BinaryRepresentable {

  private final int num;

  public Sequence() {
    this.num = 0;
  }

  /**
   * Instantiate the sequence. Must have value of [0,255] to be valid.
   *
   * @param num the sequence number
   */
  public Sequence(int num) {
    if (num > 255 || num < 0) {
      throw new IllegalArgumentException("Invalid Sequence Position.");
    }
    this.num = num;
  }

  public int getNum() {
    return num;
  }

  /**
   * Wrap around incrementation of sequence.
   *
   * @return the new sequence.
   */
  public Sequence increment() {
    if (num == 255) {
      return new Sequence(0);
    } else {
      return new Sequence(num + 1);
    }
  }

  @Override
  public String toString() {
    return "Sequence{" +
        "curr=" + num +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Sequence sequence = (Sequence) o;
    return num == sequence.num;
  }

  @Override
  public int hashCode() {
    return Objects.hash(num);
  }

  @Override
  public boolean[] toBinary() {
    return Utils.numberToBinaryArray(num, 8);
  }
}
