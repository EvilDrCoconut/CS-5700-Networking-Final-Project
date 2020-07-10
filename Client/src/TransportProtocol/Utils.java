package TransportProtocol;

/**
 * Utility class for dealing with binary data. Allows conversion from java integer representation to
 * appropriate sized binary and then bytes for transmission.
 */
public class Utils {

  /**
   * Converts an int to a binary array.
   *
   * @param num       int representation
   * @param bitLength how many bits to take up
   * @return converted form
   */
  public static boolean[] numberToBinaryArray(int num, int bitLength) {
    boolean[] binaryArray = new boolean[bitLength];
    String binary = numberToPaddedBinary(num, bitLength);
    for (int i = 0; i < binary.length(); i++) {
      if (binary.charAt(i) == '1') {
        binaryArray[i] = true;
      }
    }
    return binaryArray;
  }

  /**
   * Converts int to padded string binary representation.
   *
   * @param num    the int
   * @param length how many digits
   * @return string representation
   */
  private static String numberToPaddedBinary(int num, int length) {
    // Convert number to binary representation
    String binary = Integer.toBinaryString(num);
    // Pad binary with appropraite 0s. 16 zeroes for max length value.
    // binary.length = 2 for '10'
    int zeroesToPad = length - binary.length();
    StringBuilder sb = new StringBuilder();
    for (int i = zeroesToPad; i > 0; i--) {
      sb.append("0");
    }
    sb.append(binary);
    return sb.toString();
  }

  public static boolean[] combineBoolean(boolean[] a, boolean[] b) {
    boolean[] result = new boolean[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }

  public static byte[] combineByte(byte[] a, byte[] b) {
    byte[] result = new byte[a.length + b.length];
    System.arraycopy(a, 0, result, 0, a.length);
    System.arraycopy(b, 0, result, a.length, b.length);
    return result;
  }

  /**
   * Converts boolean array to equivalent byte array.
   *
   * @param b boolean array
   * @return byte array
   */
  public static byte[] bytesFromBinary(boolean[] b) {
    if (b.length % 8 != 0) {
      throw new IllegalArgumentException("Length must be a multiple of 8 to convert to bytes.");
    }
    // 1/8 the bytes as bits. Initiate to all 0
    byte[] bytes = new byte[b.length / 8];
    for (int i = 0; i < b.length; i++) {
      // shift left every iteration to make room for next bit. (except first)
      if (i % 8 != 0) {
        bytes[i / 8] <<= 1;
      }
      // If 1, add to end of array
      if (b[i]) {
        bytes[i / 8] += 1;
      }

    }
    return bytes;
  }
}
