/**
 *  class for a EchoGetHeader that helps to parse the url request
 */
public class EchoGetHeader implements HandlerInterface{

  private String fileRequested;
  private String folderString = "";
  private String endcap;
  private String file;

  /** constructor for the EchoGetHeader class
   *
   * @param filesReq the url path to parse
   */
  EchoGetHeader(String filesReq) {
    fileRequested = filesReq;
  }

  // method to parse the url request
  public void handle() {

    // helps to print url to server screen, then tokenize's on "/"
    System.out.println(fileRequested);
    String[] parts = fileRequested.split("/");
    // parse the first url bit on the "." to retrieve endcap "org", "edu", or "com"
    String[] helper = parts[0].split("\\.");
    endcap = helper[2];

    // creates the overall folder path to find requested file
    for(int i = 1; i < parts.length-1; i ++) {
      folderString = folderString + "/" + parts[i] ;
    }
    // variable for the requested file
    file = parts[parts.length-1];

  }

  // basic functions to return EchoGetHeader variables
  public String retEndCap() {
    return endcap;
  }

  public String retFolder() {
    return folderString;
  }

  public String retFile() {
    return file;
  }

}
