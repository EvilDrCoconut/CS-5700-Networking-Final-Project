package TransportProtocol.Data;

import TransportProtocol.Utils;

/**
 * Represents the window size of the host. 3 bits, so [0,7].
 */
public class Window implements BinaryRepresentable {

  private final int size;

  /**
   * Construct window. Size must be [0,7].
   * @param size window size
   */
  public Window(int size) {
    if (size > 7 || size < 0) {
      throw new IllegalArgumentException("Invalid Window Size.");
    }
    this.size = size;
  }

  public int getSize() {
    return size;
  }

  @Override
  public String toString() {
    return "Window{" +
        "size=" + size +
        '}';
  }

  @Override
  public boolean[] toBinary() {
    return Utils.numberToBinaryArray(size, 3);
  }
}
