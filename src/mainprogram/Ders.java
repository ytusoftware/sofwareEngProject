/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainprogram;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 *
 * @author tekin
 */
public class Ders {
    
    private int id;
    private String dersAdi;
    private String dersGunu;
    private String dersSaati;
    private int dersKapasitesi;
    
    //Singleton design pattern. Constructor private yapiliyor
    private Ders(){}
    
    public static Ders getInstance(){
        return new Ders();
    }

    public String getDersAdi() {
        return dersAdi;
    }

    public void setDersAdi(String dersAdi) {
        this.dersAdi = dersAdi;
    }

    public String getDersGunu() {
        return dersGunu;
    }

    public void setDersGunu(String dersGunu) {
        this.dersGunu = dersGunu;
    }

    public String getDersSaati() {
        return dersSaati;
    }

    public void setDersSaati(String dersSaati) {
        this.dersSaati = dersSaati;
    }

    public int getDersKapasitesi() {
        return dersKapasitesi;
    }

    public void setDersKapasitesi(int dersKapasitesi) {
        this.dersKapasitesi = dersKapasitesi;
    }
    
    
    /* Ders kaydi bolumundeki bilgileri girilen dersi veri tabanina ekler.*/
    public void writeDB(){
                    
        String sql = "INSERT INTO Ders(ders_adi,gun,saat,kapasite) VALUES(?,?,?,?)";
        Connection conn = MainProgram.getDatabaseConnection();
 
        try ( PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, this.dersAdi);
                pstmt.setString(2, this.dersGunu);
                pstmt.setString(3, this.dersSaati);
                pstmt.setInt(4, this.dersKapasitesi);
                pstmt.executeUpdate();
                conn.close();
                
                
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

  
    
    
}
