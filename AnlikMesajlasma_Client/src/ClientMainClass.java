
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class ClientMainClass {
    private static Long id = 0L;
    private static String username;
    
    public static void main(String[] args) throws ParseException
    {
        
        
        try{
            Socket socket=new Socket("127.0.0.1",1234);
            DataOutputStream writter= new DataOutputStream(socket.getOutputStream());
            DataInputStream reader=new DataInputStream(socket.getInputStream());
                        
            String mesaj="ahmet¿ahmet123";
            writter.writeUTF(Message.mesajOlustur(0L, -1L, mesaj));
            
            JSONObject girisSonuc = Message.mesajParse(reader.readUTF());
            String girisMesaj = (String) girisSonuc.get("mesaj");
            Long fromId = (Long) girisSonuc.get("from");
            Long toId = (Long) girisSonuc.get("to");
            if(fromId == -1L && "OK".equals(girisMesaj)) {
                id = toId;
            } else {
                System.out.println("SERVER: " + girisMesaj);
                writter.close();
                reader.close();
                socket.close();
                System.exit(0);
            }
            
            Scanner scanner= new Scanner(System.in);
            while(true){
                String kMesaj=scanner.nextLine();
                writter.writeUTF(Message.mesajOlustur(0L, -1L, kMesaj));
                
                if(kMesaj.equals("q"))
                    break;
            }
            
            writter.close();
            reader.close();
            socket.close();
        } catch(UnknownHostException e)
        {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
