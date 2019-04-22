/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainprogram;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


/**
 *
 * @author tekin
 */
public class Ogretmen extends Kisi{
    
    private String calisabildigiSaatler[];
    private HashMap<String, Integer> derslerBedeller;
    private int verdigiDersler[]; //Verdigi derslerin id'si tutuluyor

    
       
    
    //Contructor private yapiliyor. Singleton design pattern.
    private Ogretmen(){}
    
    
    
    public static Ogretmen getInstance(){
        return new Ogretmen();
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getEvTel() {
        return evTel;
    }

    public void setEvTel(String evTel) {
        this.evTel = evTel;
    }

    public String getCepTel() {
        return cepTel;
    }

    public void setCepTel(String cepTel) {
        this.cepTel = cepTel;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getCalisabildigiSaatler() {
        return calisabildigiSaatler;
    }

    public void setCalisabildigiSaatler(String[] calisabildigiSaatler) {
        this.calisabildigiSaatler = calisabildigiSaatler;
    }

    public HashMap<String, Integer> getDerslerBedeller() {
        return derslerBedeller;
    }

    public void setDerslerBedeller(HashMap<String, Integer> derslerBedeller) {
        this.derslerBedeller = derslerBedeller;
    }

    public int[] getVerdigiDersler() {
        return verdigiDersler;
    }

    public void setVerdigiDersler(int[] verdigiDersler) {
        this.verdigiDersler = verdigiDersler;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    
    
    
    /* Ogretmen kaydi bolumundeki bilgileri girilen ogretmeni veri tabanina ekler.*/
    public void writeDB(){
                    
        String sql1 = "INSERT INTO Ogretmen(ad,soyad,evTel,cepTel,adres,email) VALUES(?,?,?,?,?,?)";
        String sql2 = "INSERT INTO Verebildigi_Dersler(ogretmen_id,ders_adi,bedel) VALUES(?,?,?)";
        String sql3 = "SELECT MAX(id) FROM Ogretmen";
        Connection conn = MainProgram.getDatabaseConnection();
        
        
 
        try{
                PreparedStatement pstmt = conn.prepareStatement(sql1);
                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                Statement stmt  = conn.createStatement();
                
                pstmt.setString(1, this.ad);
                pstmt.setString(2, this.soyad);
                pstmt.setString(3, this.evTel);
                pstmt.setString(4, this.cepTel);
                pstmt.setString(5, this.adres);
                pstmt.setString(6, this.email); 
                
                pstmt.executeUpdate();
                
                
                //Ogretmen icin otomatik olusturulmus id veri tabanindan cekiliyor
                ResultSet rs = stmt.executeQuery(sql3);
                this.id = rs.getInt("id");
                
                
                //Verebildigi dersler yaziliyor
                for (String dersAdi : derslerBedeller.keySet()) {
                    
                    pstmt2.setInt(1, this.id);
                    pstmt2.setString(2, dersAdi);
                    pstmt2.setInt(2, derslerBedeller.get(dersAdi).intValue() );
                    pstmt2.executeUpdate();
                }

                
                
                
                
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    
    
    
    
}
