/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytusoftware.kurssatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


/**
 *
 * @author tekin
 */
public class Ogretmen extends Kisi{
    
    private String calisabildigiSaatler[];
    private HashMap<String, Integer> derslerBedeller;
    private int verdigiDersler[]; //Verdigi derslerin id'si tutuluyor

    
       
    
    
    public static Ogretmen getInstance(){
        return new Ogretmen();
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

    
    /* Ogretmen kaydi bolumundeki bilgileri girilen ogretmeni veri tabanina ekler.*/
    public void writeDB(){
                    
        String sql1 = "INSERT INTO Ogretmen(ad,soyad,ev_tel,cep_tel,adr,email) VALUES(?,?,?,?,?,?)";
        String sql2 = "INSERT INTO Verebildigi_Dersler(ogretmen_id,ders_adi,bedel) VALUES(?,?,?)";
        String sql3 = "SELECT MAX(id) FROM Ogretmen";
        String sql4 = "INSERT INTO Calisabildigi_Saatler(ogretmen_id,saat) VALUES(?,?)";
        Connection conn = MainProgram.getDatabaseConnection();
        
        
 
        try{
                PreparedStatement pstmt = conn.prepareStatement(sql1);
                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                PreparedStatement pstmt3 = conn.prepareStatement(sql4);
                Statement stmt  = conn.createStatement();
                
                pstmt.setString(1, this.ad);
                pstmt.setString(2, this.soyad);
                pstmt.setString(3, this.evTel);
                pstmt.setString(4, this.cepTel);
                pstmt.setString(5, this.adres);
                pstmt.setString(6, this.email); 
                
                pstmt.executeUpdate();
                
                
                
                ResultSet rs = stmt.executeQuery(sql3);
                this.id = rs.getInt(1);

                
                
                //Verebildigi dersler yaziliyor
                for (String dersAdi : derslerBedeller.keySet()) {
                    
                    pstmt2.setInt(1, this.id);
                    pstmt2.setString(2, dersAdi);
                    pstmt2.setInt(3, derslerBedeller.get(dersAdi).intValue() );
                    pstmt2.executeUpdate();
                }
                
                
                //Calisabildigi saatler yaziliyor
                for (String i : this.calisabildigiSaatler) {
                    
                    pstmt3.setInt(1, this.id);
                    pstmt3.setString(2, i);
                    pstmt3.executeUpdate();
                }

                
                conn.close();
                
                
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    
    //Veri tabanindaki tum ogretmenleri dondurur
    public static ArrayList<Ogretmen> tumOgretmenleriGetir() {

        String sql = "SELECT * FROM Ogretmen";

        Connection conn = MainProgram.getDatabaseConnection();
        ArrayList<Ogretmen> ogretmenler = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);

            Ogretmen ogretmen;

            while (rs.next()) {

                ogretmen = new Ogretmen();

                ogretmen.setId(rs.getInt("id"));
                ogretmen.setAd(rs.getString("ad"));
                ogretmen.setSoyad(rs.getString("soyad"));
                ogretmen.setEvTel(rs.getString("ev_tel"));
                ogretmen.setCepTel(rs.getString("cep_tel"));
                ogretmen.setAdres(rs.getString("adr"));
                ogretmen.setEmail(rs.getString("email"));

                ogretmenler.add(ogretmen);
            }

            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return ogretmenler;

    }
    
    
    
    //Veri tabanindan ogretmenin calisabildigi saatleri dondurur
    public ArrayList<String> calisabildigiSaatleriGetir() {

        String sql = "SELECT saat FROM Calisabildigi_Saatler WHERE ogretmen_id=?";

        Connection conn = MainProgram.getDatabaseConnection();
        ArrayList<String> saatler = new ArrayList<>();

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, this.id);
            
            ResultSet rs = pstmt.executeQuery();


            while (rs.next()) {
                
                saatler.add(rs.getString("saat"));

            }

            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return saatler;

    }
    
    
    
    //Veri tabanindan ogretmenin verebildigi dersleri dondurur
    public HashMap<String, Integer> verebildigiDersleriGetir() {

        String sql = "SELECT ders_adi,bedel FROM Verebildigi_Dersler WHERE ogretmen_id=?";

        Connection conn = MainProgram.getDatabaseConnection();
        HashMap<String, Integer> verebildigiDersler = new HashMap<String, Integer>();

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, this.id);
            
            ResultSet rs = pstmt.executeQuery();


            while (rs.next()) {
                
                verebildigiDersler.put(rs.getString("ders_adi"),new Integer(rs.getInt("bedel")));

            }

            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return verebildigiDersler;

    }
    
}
