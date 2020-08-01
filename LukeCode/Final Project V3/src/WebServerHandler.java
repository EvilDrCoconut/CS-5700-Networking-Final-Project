

public class WebServerHandler {

  private String dataToParse;
  private String[] segmentedData;
  private String language = "";
  private String size = "";
  private String path;

  WebServerHandler(String obj){
    dataToParse = obj;
    segmentedData = new String[15];
  }

  public void handle() {

    segmentedData = dataToParse.split("-");

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
