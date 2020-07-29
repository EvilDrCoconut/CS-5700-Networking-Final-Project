import sun.security.x509.*;

import java.net.*;
import java.util.regex.*;

public class DNSResolver {

    String urlRegex = "^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*." +
            "(com|net|edu)$";

    public boolean validateURL(String regex, String URL) {
        Pattern urlPattern = Pattern.compile(regex);
        Matcher urlMatcher = urlPattern.matcher(URL);
        return urlMatcher.matches();
    }

    public String retrieveTLDFromURL(String URL) {
        int lastDotIndex = URL.lastIndexOf('.');
        return URL.substring(lastDotIndex, URL.length());
    }

    public String retrieveHostNameFromURL(String URL) {
        int firstDotIndex = URL.indexOf('.');
        return URL.substring(firstDotIndex + 1, -1);
    }

    private static byte[] getIPV4Address(String IPString) {
        int numDigits;
        int currentOctet;
        byte[] values = new byte[4];
        int currentValue;
        int length = IPString.length();

        currentOctet = 0;
        currentValue = 0;
        numDigits = 0;
        for (int i = 0; i < length; i++) {
            char c = IPString.charAt(i);
            if (c >= '0' && c <= '9') {
                /* Can't have more than 3 digits per octet. */

                if (numDigits == 3) {
                    return null;
                }
                /* Octets shouldn't start with 0, unless they are 0. */

                if (numDigits > 0 && currentValue == 0) {
                    return null;
                }
                numDigits++;
                currentValue *= 10;
                currentValue += c - '0';
                /* 255 is the maximum value for an octet. */

                if (currentValue > 255) {
                    return null;
                }
            } else if (c == '.') {
                /* Can't have more than 3 dots. */

                if (currentOctet == 3) {
                    return null;
                }
                /* Two consecutive dots are bad. */

                if (numDigits == 0) {
                    return null;
                }
                values[currentOctet++] = (byte) currentValue;
                currentValue = 0;
                numDigits = 0;
            } else {
                return null;
            }
        }

        /* Must have 4 octets. */

        if (currentOctet != 3) {
            return null;
        }
        /* The fourth octet can't be empty. */

        if (numDigits == 0) {
            return null;
        }
        values[currentOctet] = (byte) currentValue;
        return values;
    }

    public InetAddress dnsLookupByURL(String URL, DNSCache cache) throws UnknownHostException {
        ResourceRecord rr = cache.getDnsCacheMap().get(URL);
        String ipString = rr.getValue();
        byte[] ipBytes;
        ipBytes = getIPV4Address(ipString);
        if (ipBytes != null) {
            return InetAddress.getByAddress(ipBytes);
        }
        return null;
    }
}

     /*  73.169.179.107
        25.177.38.10
        121.30.157.72
        154.27.33.71
        200.255.154.207
        197.11.44.132
        125.183.231.8
        149.253.249.247
        0.23.179.75
        182.80.70.29




*/
