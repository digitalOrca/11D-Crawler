package crawlerCore;

public class Domain {
    private String domainHash;  //Database Hash key
    private String domainUrl;   //webpage url
    
    public String getdomainHash(){
        return this.domainHash;
    }
    
    public String getdomainUrl(){
        return this.domainUrl;
    } 
    
    public Domain() throws Exception{    //default constructor
        String defaultDomain = "http://www.jsoup.org";
        this.domainHash = Hasher.toSha256(defaultDomain);
        this.domainUrl = defaultDomain; //set a default starting URL if not specified
    }
    
    public Domain(String domainUrl) throws Exception{
        this.domainUrl = domainUrl;
        this.domainHash = Hasher.toSha256(domainUrl);
    }
}
