import java.io.Serializable;

public class Segment implements Serializable {

  private String Type;
  private int Sequencenum;
  private int Window;
  private int length;
  private String Payload;
  private int ipAddress;


  public Segment(String type, int Seqnum, int window, int size, String payload){

    Type = type;
    Sequencenum = Seqnum;
    Window = window;
    length = size;
    Payload = payload;

  }

  public String retType(){
    return this.Type;
  }

  public int retSeqnum(){
    return this.Sequencenum;
  }

  public int retWindow(){
    return this.Window;
  }

  public int retLength(){
    return this.length;
  }

  public String retPayload(){
    return this.Payload;
  }

  public int retIP(){
    return this.ipAddress;
  }


}
