package crawlerCore;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Page {
    
    private Domain domain;
    private String domainUrl;
    private Document document;
    
    public Document getDocument(){
        return this.document;
    }
    
    public Page(Domain domain){
        this.domain = domain;
        this.domainUrl = domain.getdomainUrl();
    }
    
    /*Use Jsoup to get HTML*/
    public void loadHTML(){
    	try{
            document = Jsoup.connect(domainUrl).get();
	}catch(IOException e){
            //Logger.getLogger(Page.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("Unable to connect to: " + domainUrl);
	}
    }
}
