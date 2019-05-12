
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class ServerMainClass 
{
    private static Object clientLock = new Object();
    private static Database db = new Database("./server.db");
    private static Hashtable<Long, Connection> clientList = new Hashtable<>();
    
    public static void main (String [] args) throws IOException
    {
        //Önce bi database denemesi yapalım
//        Database db = new Database("./deneme.db");
//        Client yeniClient = new Client(-1L, "ahmet", "Ahmet Aydın");
//        try {
//            Long id = db.addClient(yeniClient);
//            yeniClient.setId(id);
//            if(id != -1L)
//                System.out.println(db.getClient(id).toString());
//            else
//                System.out.println(db.getClientByUsername(yeniClient.getUsername()).toString());
//            db.close();
//            
        ServerSocket server=null; 

        try{
            server=new ServerSocket(1234);
        
            while(true){
                Socket clientSocket=server.accept();
                
                Connection yeniConnection = new Connection(clientSocket);
                clientList.put(yeniConnection.client.getId(), yeniConnection);
                yeniConnection.start();
                System.out.println(clientList.get(yeniConnection.client.getId()).client.getAdSoyad());
            }
        }catch (IOException e){
            try{
                server.close();
            }catch(IOException e1)
            {
             e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (ParseException ex) {
            Logger.getLogger(ServerMainClass.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServerMainClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Connection getClient(Long id) {
        return clientList.containsKey(id)?clientList.get(id):null;
    }
    
    static class Connection extends Thread {
        Socket socket;
        DataInputStream reader;
        DataOutputStream writter;
        Client client = null;

        public Connection(Socket socket) throws IOException, ParseException, SQLException{
            this.socket=socket;
            this.reader=new DataInputStream(socket.getInputStream());
            this.writter=new DataOutputStream(socket.getOutputStream());

            String girisString = this.reader.readUTF();
            JSONObject decodedMesaj = Message.mesajParse(girisString);
            Long fromID = (Long) decodedMesaj.get("from");
            Long toID = (Long) decodedMesaj.get("to");
            String mesaj = (String) decodedMesaj.get("mesaj");

            if(toID == -1L) {
                String[] mesajParts = mesaj.split("¿");
                if(!db.girisKontrol(mesajParts[0], mesajParts[1])) {
                    writter.writeUTF(Message.mesajOlustur(-1L, fromID, "Yanlış kullanıcı adı/şifre!"));
                    closeConnection();
                } else {
                    this.client = db.getClientByUsername(mesajParts[0]);
                    writter.writeUTF(Message.mesajOlustur(-1L, this.client.getId(), "OK"));
                }
            } else {
                writter.writeUTF(Message.mesajOlustur(-1L, fromID, "Yanlış kullanım!"));
                closeConnection();
            }
        }

        @Override
        public void run() {
            while(true)
            {
                String line;
                try{
                    line=reader.readUTF();
                    JSONObject decodedMesaj = Message.mesajParse(line);
                    Long fromID = (Long) decodedMesaj.get("from");
                    Long toID = (Long) decodedMesaj.get("to");
                    String mesaj = (String) decodedMesaj.get("mesaj");
                    
                    if(toID == -1L) {
                        
                    } else {
                        Connection toClient = getClient(toID);
                        if(toClient != null)
                            toClient.writter.writeUTF(line);
                        else
                            this.writter.writeUTF(Message.mesajOlustur(-1L, fromID, "Kullanıcı çevrimdışı!"));
                    }

                    if(line.equals(("q")))
                        break;

                    System.out.println(this.client.getUsername() + " - Client Mesaj:" + line);
                }catch(IOException e){
                    try{
                        closeConnection();
                    }catch(IOException e1){
                        break;
                    }
                    break;
                } catch (ParseException ex) {
                    Logger.getLogger(ServerMainClass.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            try{
                closeConnection();
            }catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        public void closeConnection() throws IOException{
            reader.close();
            writter.close();
            socket.close();
            System.out.println("Client Kapandı: " + socket.toString());
            this.interrupt();
        }
    }
}

