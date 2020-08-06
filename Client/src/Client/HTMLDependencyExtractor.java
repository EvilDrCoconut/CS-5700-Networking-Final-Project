package Client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Need to get things in <link> and begin with ./ in href Also need <script> and begin with ./ in
 * src <img> begin with ./ in src. Finds all, collapses to Set of elements so doesn't duplicate.
 */
public class HTMLDependencyExtractor {

  // Look behind for <link, then follow it by smallest number of characters to href
  // select the characters plus the href and then group 1, the characters within href quotes.
  Pattern link = Pattern.compile("(?<=<link).+?(?<=href=\")(\\./.+?)(?=\")");
  Pattern script = Pattern.compile("(?<=<script).+?(?<=src=\")(\\./.+?)(?=\")");
  Pattern img = Pattern.compile("(?<=<img).+?(?<=src=\")(\\./.+?)(?=\")");
  Matcher matcher;
  String content;
  Set<String> links = new HashSet<>();

  /**
   * Group lets you select the regex group. 0 is total, 1 is first. Matcher.find will move to next
   * instance.
   *
   * @param filePath
   * @throws IOException
   */
  HTMLDependencyExtractor(String filePath) throws IOException {
    StringBuilder contentBuilder = new StringBuilder();

    try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
      stream.forEach(s -> contentBuilder.append(s).append("\n"));
    }
    this.content = contentBuilder.toString();
    matcher = link.matcher(content);
    while (matcher.find()) {
      links.add(matcher.group(1));
    }
    matcher = script.matcher(content);
    while (matcher.find()) {
      links.add(matcher.group(1));
    }
    matcher = img.matcher(content);
    while (matcher.find()) {
      links.add(matcher.group(1));
    }
  }

  /**
   * Get the link without the first ./ that the relative links are matched on.
   *
   * @param baseUrl
   * @return
   */
  public List<String> getLinks(String baseUrl) {
    return new ArrayList<>(links).stream().map(x -> baseUrl + x.substring(2))
        .collect(Collectors.toList());

  }
}
