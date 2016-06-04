package crawlerCore;

import crawlerCore.Domain;
import crawlerCore.Page;
import crawlerDB.DBConnect;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
    public static void main(String[] args) throws SQLException {
        //create connection object
        DBConnect db = new DBConnect();
        try {
            db.createTable();   //create domain registery table
            System.out.println("Database established...");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //crawl the first page and setup database
        Domain domain = null;
        Page page = null;
        try {
            domain = new Domain();  //use default constructor
            db.postDomain(domain); //insert first activated domain into database
            page = new Page(domain);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        page.loadHTML(); //extract html from the webpage
        Document doc = page.getDocument();
        Elements links = doc.select("a[href]");
        
        for(Element e:links){
            try {
                db.pendDomain(e.attr("abs:href")); //pend unactivated domain to the database
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            //System.out.println(e.attr("abs:href")); 
        }
        
        int level = 2;  //crawl three level of connections
        for(int i = 0; i <level; i++){
            try {
                String[] pendingArray = db.getPendingDomain();
                for(String d:pendingArray){
                    try {
                        Domain newDomain = new Domain(d);
                        db.activate(newDomain);
                        Page newPage = new Page(newDomain);
                        newPage.loadHTML();
                        Document newDoc = newPage.getDocument();
                        if (newDoc.hasText()){
                            Elements newLinks = newDoc.select("a[href]");
                            for (Element ne:newLinks){
                                try{
                                    db.pendDomain(ne.attr("abs:href"));
                                }catch(Exception ex){
                                    System.out.println("Cannot not pend  this domain to database...");
                                    //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                      } catch (Exception ex) {
                        System.out.println("Failed to create Domain object using Url...");
                        //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }catch (SQLException ex) {
                System.out.println("Bad domain...");
                //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } 
    }//end of main
}
