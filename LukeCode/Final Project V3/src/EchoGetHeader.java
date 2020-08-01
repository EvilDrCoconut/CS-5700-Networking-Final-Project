import java.io.File;
import java.util.LinkedList;

public class EchoGetHeader {

  private String fileRequested;
  private String folderString = "";
  private String endcap;
  private String[] folder = new String[5];
  private String file;

  EchoGetHeader(String filesReq) {
    fileRequested = filesReq;
  }

  public void handle() {

    System.out.println(fileRequested);
    String[] parts = fileRequested.split("/");
    String[] helper = parts[0].split("\\.");
    endcap = helper[2];


    for(int i = 1; i < parts.length-1; i ++) {
      folderString = folderString + "/" + parts[i] ;
    }
    file = parts[parts.length-1];

  }

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
