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
import java.util.ArrayList;

/**
 *
 * @author tekin
 */
public class Kurs {
    
    private String ad;
    private String tip;
    private int ucret;
    private ArrayList<Ders> dersler;
    private String tarih;
    private int kapasite;
    private int doluluk;

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public int getUcret() {
        return ucret;
    }

    public void setUcret(int ucret) {
        this.ucret = ucret;
    }

    public ArrayList<Ders> getDersler() {
        return dersler;
    }

    public void setDersler(ArrayList<Ders> dersler) {
        this.dersler = dersler;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public int getKapasite() {
        return kapasite;
    }

    public void setKapasite(int kapasite) {
        this.kapasite = kapasite;
    }

    public int getDoluluk() {
        return doluluk;
    }

    public void setDoluluk(int doluluk) {
        this.doluluk = doluluk;
    }
    
    
    
    
    
    /*Kurs nesnesini veri tabanina yazar*/
    public boolean writeDB(){
        
        String sql = "INSERT INTO Kurs(kurs_adi,kurs_tipi,ucret,tarih,kapasite,doluluk) VALUES(?,?,?,?,?,?)";
        String sql2 = "UPDATE Ders SET kurs_adi=? WHERE id=?";

        Connection conn = MainProgram.getDatabaseConnection();
        

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);

            pstmt.setString(1, this.ad);
            pstmt.setString(2, this.tip);
            pstmt.setInt(3, this.ucret);
            pstmt.setString(4, this.tarih);
            pstmt.setInt(5, this.kapasite);
            pstmt.setInt(6, this.doluluk);

            pstmt.executeUpdate();


            //Icerdigi dersler yaziliyor
            for (Ders ders : dersler) {

                pstmt2.setString(1, this.ad);
                pstmt2.setInt(2, ders.getId());
                
                pstmt2.executeUpdate();
            }

            conn.close();
            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return false;
    }
    
    
    
    /*
    Kullanıcı talebine göre kursta yer alacak dersleri, kurs zamanını ve odenebilecek max miktarı parametre olarak alıp bu kriterlere uyan kursları liste 
    olarak dondurur
    */
    public static ArrayList<Kurs> getsecilebilecekKurslar(ArrayList<String> dersler, String kursZamani, int maxUcret) {
        
        String sql = "";
        Kurs kurs;
        ArrayList<Kurs> secilebilecekKurslar = new ArrayList<Kurs>();
        
        
        
        //Tum dersleri aynı anda iceren dersleri bulmak icin INTERSECT ile sql sorgusu yazilmali
        for (int i = 0; i < dersler.size(); i++) {
            
            sql += "SELECT * "
                    + "FROM Kurs "
                    + "WHERE kurs_tipi=? AND ucret<=? AND kapasite<>doluluk AND kurs_adi IN("
                    + "SELECT kurs_adi "
                    + "FROM Ders "
                    + "WHERE ders_adi=?)";
            
            
            if (i != dersler.size()-1) {
                sql += " INTERSECT ";
            }
            
        }
        
        
        
        Connection conn = MainProgram.getDatabaseConnection();
        
        
        try {
            
            PreparedStatement pstmt = conn.prepareStatement(sql);

            int k = 1;
            
            //Place holderlar set ediliyor
            for (int i = 0; i < dersler.size(); i++) {
                pstmt.setString(k, kursZamani);
                pstmt.setInt(k+1, maxUcret);
                pstmt.setString(k+2, dersler.get(i));
                k += 3;
            }   
            


            ResultSet rs = pstmt.executeQuery();
            
            //Veri tabanindan gelen kurslarin nesnesi olusturulup tek tek listeye ekleniyor
            while (rs.next()) {
                
                
                kurs = new Kurs();
                
                kurs.setAd( rs.getString("kurs_adi") );
                kurs.setTip( rs.getString("kurs_tipi") );
                kurs.setUcret( rs.getInt("ucret") );
                kurs.setTarih( rs.getString("tarih") );
                kurs.setKapasite( rs.getInt("kapasite") );
                kurs.setDoluluk( rs.getInt("doluluk") );
                
                secilebilecekKurslar.add(kurs);
            }

            
            conn.close();      

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return secilebilecekKurslar;
        
    }
    
    
    public ArrayList<Ders> getDerslerDB() {

        String sql = "SELECT id,ders_adi,gun,saat,sinif FROM Ders WHERE kurs_adi=?";
        Connection conn = MainProgram.getDatabaseConnection();
        ArrayList<Ders> icerdigiDersler = new ArrayList<>();
        

        try {

            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, this.ad);
            
            ResultSet rs = pstmt.executeQuery();
            Ders ders;
            
            
            //Veri tabanindan gelen kurslarin nesnesi olusturulup tek tek listeye ekleniyor
            while (rs.next()) {

                ders = new Ders();

                ders.setId(rs.getInt("id"));
                ders.setDersAdi(rs.getString("ders_adi"));
                ders.setDersGunu(rs.getString("gun"));
                ders.setDersSaati(rs.getString("saat"));
                ders.setDersSinifi(rs.getString("sinif"));

                icerdigiDersler.add(ders);
            }

            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return icerdigiDersler;

    }
    
}
