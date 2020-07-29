public class ResourceRecord {

    // name, value, type, ttl
    /* type: A, then Name is a hostname and Value is the IP address for the hostname (relay1.bar
    .foo.com, 145.37.93.126, A) is a type A record
    // type: NS (Name is a domain, such as foo.com, and value is the hostname of an authoritative
    // DNS server that knows how to obtain the IP addresses for hosts in the domain. E.g., {foo
    // .com, dns.foo.com, NS)
    type: CNAME, value is a canonical hostname for the alias hostname Name. This record can provide
    querying hosts the canonical name for a hostname. (foo.com, relay1.bar.foo.com, CNAME), is a
    CNAME record
    If type=MX, then value is the canonical name of a mail server that has an alias hostname name

    if DNS server is authoritative, the DNS server will contain a TypeA record for the hostname.
    (If not authoritative, it may contain a Type A record in its cache.) If a server is not
    authoritative for a hostname, then the server will contain a type NS record for the domain
    that includes the hostname; it will also contain a type A record that provides the IP address
     of the DNS server in the Value field of the NS record.

    { name, value, type, ttl }
     */
    private String name;
    private String value;
    private String type;
    private int ttl;
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




// need getters and setters



}
