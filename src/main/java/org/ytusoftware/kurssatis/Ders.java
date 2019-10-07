/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytusoftware.kurssatis;

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
    private String dersSinifi;
    
   
    
    
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

    public String getDersSinifi() {
        return dersSinifi;
    }

    public void setDersSinifi(String dersSinifi) {
        this.dersSinifi = dersSinifi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
    
    
    /* Ders kaydi bolumundeki bilgileri girilen dersi veri tabanina ekler.*/
    public void writeDB(){
                    
        String sql = "INSERT INTO Ders(ders_adi,gun,saat,kapasite,sinif) VALUES(?,?,?,?,?)";
        Connection conn = MainProgram.getDatabaseConnection();
 
        try ( PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, this.dersAdi);
                pstmt.setString(2, this.dersGunu);
                pstmt.setString(3, this.dersSaati);
                pstmt.setInt(4, this.dersKapasitesi);
                pstmt.setString(5, this.dersSinifi);
                pstmt.executeUpdate();
                conn.close();
                
                
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    /* Veri tabaninda dersi ogretmen ile baglar */
    public void ogretmenAta(int ogretmenId) {

        String sql = "UPDATE Ders SET ogretmen_id=? WHERE id=?";
        Connection conn = MainProgram.getDatabaseConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, ogretmenId);
            pstmt.setInt(2, this.id);

            pstmt.executeUpdate();
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    
    

  
    
    
}
