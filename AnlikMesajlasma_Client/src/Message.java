
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Message {
    
    public static String mesajOlustur(Long fromID, Long toID, String mesaj) {
        JSONObject mesajJSON = new JSONObject();
        mesajJSON.put("from", fromID);
        mesajJSON.put("to", toID);
        mesajJSON.put("mesaj", mesaj);
        return mesajJSON.toJSONString();
    }
    
    public static JSONObject mesajParse(String mesaj) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(mesaj);
    }
}
