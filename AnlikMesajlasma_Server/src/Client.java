
public class Client {

    private Long id;
    private String username;
    private String adSoyad;
    
    public Client(Long id, String username, String adSoyad) {
        this.id = id;
        this.username = username;
        this.adSoyad = adSoyad;
    }
    
    @Override
    public String toString() {
        String clientString = "ID: " + id + "\n"
                + "Username: " + username + "\n"
                + "Ad Soyad: " + adSoyad;
        return clientString;
    }
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the adSoyad
     */
    public String getAdSoyad() {
        return adSoyad;
    }

    /**
     * @param adSoyad the adSoyad to set
     */
    public void setAdSoyad(String adSoyad) {
        this.adSoyad = adSoyad;
    }
    
}
