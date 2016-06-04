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


public class DBConnect {
    private Connection connection;
    private Statement statement;
    private ResultSet result;
    
    public DBConnect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawl_footprint", "root", "");
            statement = connection.createStatement();   
        }catch(Exception ex){
            System.out.println("Error: " + ex);
        }
    }
    
    public void createTable() throws Exception{
        if(connection != null){
            String tableCreate = "CREATE TABLE IF NOT EXISTS `crawl_footprint`.`domain` ( `id` INT NOT NULL AUTO_INCREMENT , `domainHash` VARCHAR(64) NOT NULL , `domainUrl` VARCHAR(255) NOT NULL , `activated` INT NOT NULL , PRIMARY KEY (`domainHash`), INDEX (`id`), UNIQUE (`domainUrl`)) ENGINE = InnoDB;";
            PreparedStatement create = connection.prepareStatement(tableCreate);
            create.executeUpdate();
        }else{
            System.out.println("Not connected to a database!");
        }
    }
    
    public void pendDomain(String domainUrl) throws SQLException, Exception{ //post the dtected domain
        if(connection != null){
            String domainHash = Hasher.toSha256(domainUrl);
            int activated = 0;
            String insertion = "INSERT IGNORE `domain` (`id`,`domainHash`,`domainUrl`,`activated`) VALUES (NULL, '" + domainHash + "','"+domainUrl+"','"+activated+"')";
            PreparedStatement insert = connection.prepareStatement(insertion);
            insert.execute();
        }else{
            System.out.println("Not connected to a database!");
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
    
    public void activate(Domain domain) throws SQLException{
        if(connection != null){
            String domainHash = domain.getdomainHash();
            String update = "UPDATE `domain` SET `activated` = '1' WHERE `domain`.`domainHash` = '"+domainHash+"'";
            PreparedStatement activate = connection.prepareStatement(update);
            activate.executeUpdate();
        }else{
            System.out.println("Not connected to a database!");
        }
    }
    
    public String[] getPendingDomain() throws SQLException{
        
        List pendingList = new ArrayList();
        if(connection != null){
            PreparedStatement getPend = connection.prepareStatement("SELECT `domainUrl` FROM `domain` WHERE `activated`=0");
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
    
}
