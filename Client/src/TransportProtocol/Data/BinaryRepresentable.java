package TransportProtocol.Data;

/**
 * Interface for objects that can be represented by binary.
 */
public interface BinaryRepresentable {

  /**
   * Convert data into boolean array, binary representation.
   *
   * @return binary representation
   */
  boolean[] toBinary();

}
