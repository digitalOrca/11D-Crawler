package crawlerDB;

import crawlerCore.Domain;
import crawlerCore.Hasher;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBConnect {
    private Connection connection;
    private Statement statement;
    private ResultSet result;
    
    public DBConnect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/11dtech", "root", "");
            statement = connection.createStatement();   
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }
    
    public Connection getConnection(){
        return connection;
    }
    
    public void createTable() throws Exception{
        if(connection != null){
            String tableCreate = "CREATE TABLE IF NOT EXISTS `11dtech`.`domain` ( `id` INT NOT NULL AUTO_INCREMENT , `domainHash` CHAR(64) NOT NULL , `domainUrl` VARCHAR(500) NOT NULL , `activated` INT NOT NULL , PRIMARY KEY (`domainHash`), INDEX (`id`), UNIQUE (`domainUrl`)) ENGINE = InnoDB;";
            PreparedStatement create = connection.prepareStatement(tableCreate);
            create.executeUpdate();
        }else{
            System.out.println("Not connected to a database!");
        }
    }
    
    public void pendDomainExecute(PreparedStatement pend){
        try {
            pend.executeBatch();
        } catch (SQLException ex) {
            System.out.println("Error executing batch domain pending...");
        }
    }
    
    public void postDomain(Domain domain) throws SQLException{ //post the analyzed domain
        if(connection != null){
            String domainHash = domain.getdomainHash();
            String domainUrl = domain.getdomainUrl();
            int activated = 1;
            String insertion = "INSERT IGNORE `domain` (`id`,`domainHash`,`domainUrl`,`activated`) VALUES (NULL, '" + domainHash + "','"+domainUrl+"','"+activated+"')";
            PreparedStatement insert = connection.prepareStatement(insertion);
            insert.execute();
        }else{
            System.out.println("Not connected to a database!");
        }
    }
    
    public void activateDomainExecute(PreparedStatement active){
        try {
            active.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String[] getPendingDomain() throws SQLException{
        //only return no more than 1000 pending links each time to prevent the list become too big
        List pendingList = new ArrayList();
        if(connection != null){
            PreparedStatement getPend = connection.prepareStatement("SELECT `domainUrl` FROM `domain` WHERE `activated`=0 LIMIT 100");
            result = getPend.executeQuery();
            // put query results into a list
            while(result.next()){ 
                pendingList.add(result.getString("domainUrl"));
            }  
        }else{
            System.out.println("Not connected to a database!");
        }
        //convert list to array
        String[] pendingArray = new String[pendingList.size()];
        pendingList.toArray(pendingArray);
        return pendingArray;
    }
    
    
    public int countCrawled() throws SQLException{

        PreparedStatement  countVisited = connection.prepareStatement("SELECT COUNT(activated) FROM `domain` WHERE `activated` = 1 ");
        ResultSet crawled = countVisited.executeQuery();
        crawled.next(); //No idea why adding this line would make getInt to work
        return crawled.getInt(1);
    }
    
    public int countDetected() throws SQLException{

        PreparedStatement  countFound = connection.prepareStatement("SELECT COUNT(activated) FROM `domain` WHERE `activated` = 0 ");
        ResultSet found = countFound.executeQuery();
        found.next();
        return found.getInt(1);
    }
}
