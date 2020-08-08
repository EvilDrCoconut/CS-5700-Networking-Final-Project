package DNS;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DNSResolver {

  static String URL_REGEX = "^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*." +
      "(com|net|edu|org)$";
  Registrar registrar = new Registrar();

  static boolean validateURL(String regex, String URL) {
    Pattern urlPattern = Pattern.compile(regex);
    Matcher urlMatcher = urlPattern.matcher(URL);
    return urlMatcher.matches();
  }

  public static String retrieveTLDFromURL(String URL) {
    int lastDotIndex = URL.lastIndexOf('.');
    return URL.substring(lastDotIndex + 1, URL.length());
  }

  static String retrieveHostNameFromURL(String URL) {
    int firstDotIndex = URL.indexOf('.');
    return URL.substring(firstDotIndex + 1, URL.length());
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

  public InetAddress dnsLookupByURL(String URL, DCache cache) throws UnknownHostException {
    ResourceRecord rr = (ResourceRecord) cache.get(URL);
    String ipString = rr.getValue();
    System.out.println(ipString);
    byte[] ipBytes;
    ipBytes = getIPV4Address(ipString);
    if (ipBytes != null) {
      return InetAddress.getByAddress(ipBytes);
    }
    return null;
  }

  public ResourceRecord aRecordLookupByURL(String URL, DCache cache) throws UnknownHostException {
    ResourceRecord rr = (ResourceRecord) cache.get(URL);
    return rr;
  }

  public ResourceRecord nsRecordLookupByTLD(String tldQuery, DCache cache)
      throws UnknownHostException {
    ResourceRecord rr = (ResourceRecord) cache.get(tldQuery);
    return rr;
  }

  public ResourceRecord tldRecordLookup(String tldQuery, DCache cache) throws UnknownHostException {
    ResourceRecord rr = (ResourceRecord) cache.get(tldQuery);
    return rr;
  }


  public ConcurrentHashMap<String, DCache.CachedObject> rrQuery(String query,
      String questionType,
      DCache cache) throws IOException {
    ConcurrentMap<String, DCache.CachedObject> queryResult = new ConcurrentHashMap<>();
    System.out.println("is cache not null? " + cache != null);
    System.out.println("is cachemap empty? " + !cache.getCacheMap().isEmpty());
    //ResourceRecord rr = registrar.createErrorRecord("NOT_IN_CACHE", query);
    if (cache != null && !cache.getCacheMap().isEmpty()) {
      System.out.println("in the stream");
      System.out.println("printing the key set" + cache.getCacheMap().keySet());
      if (questionType.equals("A")) {
        queryResult = cache.getCacheMap().entrySet()
            .stream()
            .filter(map -> questionType.equals(map.getValue().record.getType()))
            .filter(map -> query.equals(map.getKey()))
            .collect(Collectors.toConcurrentMap(Map.Entry::getKey,
                Map.Entry::getValue));
        System.out.println("query result " + queryResult.keySet());
      } else if (questionType.equals("G") || questionType.equals("TLD") || questionType
          .equals("NS")) {
        queryResult = cache.getCacheMap().entrySet()
            .stream()
            .filter(map -> questionType.equals(map.getValue().record.getType()))
            .filter(map -> retrieveTLDFromURL(query).equals(map.getKey().substring(0, 3)))
            .filter(map -> query.substring(0, 3).equals(map.getKey().substring(0, 3)))
            .collect(Collectors.toConcurrentMap(Map.Entry::getKey,
                Map.Entry::getValue));
      }

    }
    System.out.println("printing queryResult " + queryResult.entrySet());
    return (ConcurrentHashMap<String, DCache.CachedObject>) queryResult;

  }
}
