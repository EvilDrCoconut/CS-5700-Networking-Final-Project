import java.io.*;
import java.util.concurrent.*;

public class ResourceRecord implements Serializable {

    private String name;
    private String value;
    private String type;
    private int ttl;

    int getPortNum() {
        return this.portNum;
    }

    void setPortNum(final int portNum) {
        this.portNum = portNum;
    }

    private int portNum;
    private long lastAccessed = System.currentTimeMillis();

    long getLastAccessed() {
        return this.lastAccessed;
    }

    void setLastAccessed(final long lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    ResourceRecord(String name, String value, String type, int ttl) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.ttl = ttl;
    }

    String getName() {
        return this.name;
    }

    void setName(final String name) {
        this.name = name;
    }

    String getValue() {
        return this.value;
    }

    void setValue(final String value) {
        this.value = value;
    }

    String getType() {
        return this.type;
    }

    void setType(final String type) {
        this.type = type;
    }

    int getTtl() {
        return this.ttl;
    }

    void setTtl(final int ttl) {
        this.ttl = ttl;
    }


    @Override
    public String toString() {
        return "ResourceRecord{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                ", ttl=" + ttl +
                ", lastAccessed=" + lastAccessed +
                '}';
    }


}
