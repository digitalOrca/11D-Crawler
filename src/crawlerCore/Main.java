package crawlerCore;

import crawlerCore.Domain;
import crawlerCore.Page;
import crawlerDB.BatchStatement;
import crawlerDB.DBConnect;
import crawlerUI.Window;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Main {
    
    public static void main(String[] args) throws SQLException {
        
        //create control interface----------------------------------------------
        Window control = new Window();
        control.createWindow();
        control.addLog("**************************************\n" +
                       "  __ __ _____    _______        _     \n" +
                       " /_ /_ |  __ \\  |__   __|      | |    \n" +
                       "  | || | |  | |    | | ___  ___| |__  \n" +
                       "  | || | |  | |    | |/ _ \\/ __| '_ \\\n" +
                       "  | || | |__| |    | |  __/ (__| | | |\n" +
                       "  |_||_|_____/     |_|\\___|\\___|_| |_|\n"+
                       "**************************************\n");
        control.addLog("This is a web crawler created by Lake Chen. The requirements of this program include following:\n"
                    + "XAMPP(https://www.apachefriends.org/download.html)\n"
                    + "A database named '11dtech' setup in XAMPP\n"
                    + "Available internet connection\n\n");
        
        control.addLog("Crawlers standby and ready --->>>");
        control.addLog("[don't be worried if some crawlers encounter difficulty]");
        control.update();
        
        
        while(!control.getStatus()){ 
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //create connection object----------------------------------------------
        DBConnect db = new DBConnect();
        BatchStatement bs = new BatchStatement();
        
        try {
            db.createTable();   //create domain registery table
            control.addLog("Database established...");
            control.update();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //crawl the starting page and setup database----------------------------
        Domain domain = null;
        Page page = null;

        do{
            while(!control.getStatus()){ //pause the check loop
                try {
                    TimeUnit.SECONDS.sleep(1);
                }catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
            try {
                domain = new Domain(control.getOrigin());  //use default constructor
                db.postDomain(domain); //insert first activated domain into database
                page = new Page(domain);
                page.loadHTML(); //extract html from the webpage
                System.out.println(1);
            } catch (Exception ex) {
                control.setStatus(false);
                control.addLog("Invalid origin domain, please verify your input.");
                control.update();
            }
        }while(!control.getStatus());
        control.addLog("Valid origin domain...");
        control.update();
        //load find links in the starting page----------------------------------
        Document doc = page.getDocument();
        Elements links = doc.select("a[href]");
        PreparedStatement pendtemp = db.getConnection().prepareStatement("");
        for(Element e:links){
            try {
                bs.pendDomain(pendtemp, e.attr("abs:href")); //pend unactivated domain to the database
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        db.pendDomainExecute(pendtemp);
        
        int Count = 0;
        while(control.getStatus()){
            Count++;
            control.addLog("Starting wave " + Count + ">>>>>>>>>>>");
            control.update();
            try {
                String[] pendingArray = db.getPendingDomain(); //get inactive domain from database
                PreparedStatement pend = db.getConnection().prepareStatement(""); //statement for batch pending unexplored links
                PreparedStatement active = db.getConnection().prepareStatement(""); //statement for batch updating explored links
                if(!control.getStatus()){break;}
                for(String d:pendingArray){
                    try {
                        Domain newDomain = new Domain(d);
                        bs.activate(active, newDomain);
                        Page newPage = new Page(newDomain);
                        newPage.loadHTML();
                        Document newDoc = newPage.getDocument();
                        if (newDoc.hasText()){
                            Elements newLinks = newDoc.select("a[href]");
                            for (Element ne:newLinks){
                                try{
                                    bs.pendDomain(pend, ne.attr("abs:href"));
                                }catch(Exception ex){
                                    control.addLog("A Crawlers hit a bump, but everything is alright...");
                                    control.update();
                                    //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                      } catch (Exception ex) {
                        control.addLog("A crawlers hit a wall, it'll go around...");
                        control.update();
                        //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                db.pendDomainExecute(pend); //pend all the detected domain to database
                db.activateDomainExecute(active);   //activate all the crawled domain
                control.setCrawled(db.countCrawled());
                control.setDetected(db.countDetected());
                control.update();
            }catch (SQLException ex) {
                control.addLog("Crawler got confused... it happens");
                control.update();
                //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        control.addLog("Crawling stopped");
        control.update();
    }//end of main
}