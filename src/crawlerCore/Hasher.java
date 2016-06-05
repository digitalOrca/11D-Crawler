package crawlerCore;

import java.security.MessageDigest;

public class Hasher {
    public static String toSha256(String str) throws Exception{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String text = str.toLowerCase();
        md.update(text.getBytes("ASCII"));
        byte[] hash = md.digest();
        
        StringBuilder sb = new StringBuilder();
        for(byte b:hash){
            sb.append(String.format("%02x", b));
        }
            
        return sb.toString().toUpperCase();
    }
}
