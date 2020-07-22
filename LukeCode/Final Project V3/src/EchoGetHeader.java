import java.util.LinkedList;

public class EchoGetHeader {

  private String fileRequested;
  private LinkedList<String> fileRequestSegmented;
  private String[] specificFiles;
  private String company;
  private String folder;
  private String file;



  EchoGetHeader(String filesReq){
    fileRequested = filesReq;
    specificFiles = new String[2];
  }

  public void handle(){

    String[] first = fileRequested.split(".");
    String firstHelper = first[2];
    fileRequestSegmented.add(first[0]); fileRequestSegmented.add(first[1]);
    company = first[1];

    String[] second = firstHelper.split("/");
    fileRequestSegmented.add(second[0]); fileRequestSegmented.add(second[1]); fileRequestSegmented.add(second[2]);
    specificFiles[0] = second[1]; specificFiles[1] = second[2];

    folder = specificFiles[0];
    file = specificFiles[1];
  }

  public String retComp(){
    return company;
  }

  public String retFolder(){
    return folder;
  }

  public String retFile(){
    return file;
  }

}
