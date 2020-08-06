
// class to help parse the request and grab the needed url and requests headers
public class WebServerHandler implements HandlerInterface{

  private String dataToParse;
  private String[] segmentedData;
  private String language = "";
  private String size = "";
  private String path;

  /** Constructor for the WebServerHandler
   *
   * @param obj string that is the overall request from the client
   */
  WebServerHandler(String obj){
    dataToParse = obj;
    segmentedData = new String[15];
  }

  // method to parse the request
  public void handle() {

    // splits on "-" to tokenize the request
    segmentedData = dataToParse.split("-");

    // if a token equals the case, sets the needed variable to the value following the token
    int n = 0;
    for (String each : segmentedData) {
      switch (each) {
        case ("path:"):
          path = segmentedData[n + 1];
          break;
        case ("language:"):
          language = segmentedData[n + 1];
          break;
        case ("size:"):
          size = segmentedData[n + 1];
      }
      n++;
    }
  }

    // basic methods to return url path and optional request headers
    public String retPath(){
      return path;
    }

    public String retLang(){
      return language;
    }

    public String retSize(){
      return size;
    }
}
