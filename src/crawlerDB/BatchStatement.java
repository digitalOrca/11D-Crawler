package crawlerDB;

import crawlerCore.Domain;
import crawlerCore.Hasher;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BatchStatement {
    
    public BatchStatement(){
    }
    
    public PreparedStatement activate(PreparedStatement active, Domain domain) throws SQLException{
        String domainHash = domain.getdomainHash();
        String update = "UPDATE `domain` SET `activated` = '1' WHERE `domain`.`domainHash` = '"+domainHash+"'";
        active.addBatch(update);
        return active;
    }
    
    public PreparedStatement pendDomain(PreparedStatement pend, String domainUrl) throws SQLException, Exception{ //post the dtected domain
        String domainHash = Hasher.toSha256(domainUrl);
        int activated = 0;
        String insertion = "INSERT IGNORE `domain` (`id`,`domainHash`,`domainUrl`,`activated`) VALUES (NULL, '" + domainHash + "','"+domainUrl+"','"+activated+"')";
        pend.addBatch(insertion);
        return pend;
    }
    
}
