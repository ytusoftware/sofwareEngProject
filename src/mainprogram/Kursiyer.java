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
import java.util.ArrayList;

/**
 *
 * @author tekin
 */
public class Kursiyer extends Kisi{
    
    private ArrayList<Satis> satinAlimlar = new ArrayList<Satis>();
    
    

    public ArrayList<Satis> getSatinAlimlar() {
        return satinAlimlar;
    }

    public void setSatinAlimlar(ArrayList<Satis> satinAlimlar) {
        this.satinAlimlar = satinAlimlar;
    }
    
    
    
    
    //Nesneyi veri tabanina yazar
    public boolean writeDB() {

        String sql = "INSERT INTO Kursiyer(ad,soyad,ev_tel,cep_tel,adr,email) VALUES(?,?,?,?,?,?)";
        String sql2 = "SELECT MAX(id) FROM Kursiyer";
        String sql3 = "INSERT INTO Kursiyer_Kurs(kursiyer_id,kurs_adi,ucret,tarih,odeme_tipi) VALUES(?,?,?,?,?)";
        String sql4 = "UPDATE Kurs SET doluluk=doluluk+1 WHERE kurs_adi=?";

        Connection conn = MainProgram.getDatabaseConnection();

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            Statement stmt  = conn.createStatement();
            PreparedStatement pstmt3 = conn.prepareStatement(sql3);
            PreparedStatement pstmt4 = conn.prepareStatement(sql4);

            pstmt.setString(1, this.ad);
            pstmt.setString(2, this.soyad);
            pstmt.setString(3, this.evTel);
            pstmt.setString(4, this.cepTel);
            pstmt.setString(5, this.adres);
            pstmt.setString(6, this.email);

            pstmt.executeUpdate();

            ResultSet rs = stmt.executeQuery(sql2);
            this.id = rs.getInt(1);


            //Satis bilgisi yaziliyor
            for (Satis satis : this.satinAlimlar) {

                pstmt3.setInt(1, this.id);
                pstmt3.setString(2, satis.getKursAdı());
                pstmt3.setInt(3, satis.getOdenenMiktar());
                pstmt3.setString(4, satis.getTarih());
                pstmt3.setString(5, satis.getOdemeTipi());
                
                pstmt3.executeUpdate();
            }
            
            
            //Doluluk guncelleniyor
            pstmt4.setString(1, this.satinAlimlar.get(0).getKursAdı());
            pstmt4.executeUpdate();

            conn.close();
            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return false;
    }
    
    
    //Veri tabanindaki tum kursiyerleri dondurur
    public static ArrayList<Kursiyer> tumKursiyerleriGetir() {

        String sql = "SELECT * FROM Kursiyer";

        Connection conn = MainProgram.getDatabaseConnection();
        ArrayList<Kursiyer> kursiyerler = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();


            ResultSet rs = stmt.executeQuery(sql);
            
            Kursiyer kursiyer;
            
            while (rs.next()) {
                
                kursiyer = new Kursiyer();
                
                kursiyer.setId(rs.getInt("id"));
                kursiyer.setAd(rs.getString("ad"));
                kursiyer.setSoyad(rs.getString("soyad"));
                kursiyer.setEvTel(rs.getString("ev_tel"));
                kursiyer.setCepTel(rs.getString("cep_tel"));
                kursiyer.setAdres(rs.getString("adr"));
                kursiyer.setEmail(rs.getString("email"));
                
                kursiyerler.add(kursiyer);
            }
            
            
            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return kursiyerler;

    }
    
    
    //Kayıtlı kursiyerin bir kurs satın alması sonrası satış bilgisini kaydeder
    public boolean kursSatinAlimiKaydet(Satis satis) {

        String sql = "INSERT INTO Kursiyer_Kurs(kursiyer_id,kurs_adi,ucret,tarih,odeme_tipi) VALUES(?,?,?,?,?)";
        String sql2 = "UPDATE Kurs SET doluluk=doluluk+1 WHERE kurs_adi=?";

        Connection conn = MainProgram.getDatabaseConnection();

        try {
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            
            //Satis bilgisi kaydediliyor
            pstmt.setInt(1, this.id);
            pstmt.setString(2, satis.getKursAdı());
            pstmt.setInt(3, satis.getOdenenMiktar());
            pstmt.setString(4, satis.getTarih());
            pstmt.setString(5, satis.getOdemeTipi());

            pstmt.executeUpdate();
            
            //Kursun dolulugu guncelleniyor
            pstmt2.setString(1, satis.getKursAdı());
            
            pstmt2.executeUpdate();
            
            conn.close();
            return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return false;

    }
    
    
    //Kursiyerin kayıt yaptırdığı tüm kursların satış bilgilerini döndürür
    public ArrayList<Satis> kursSatinAlimlariniGetir() {

        String sql = "SELECT * FROM Kursiyer_Kurs WHERE kursiyer_id=?";

        Connection conn = MainProgram.getDatabaseConnection();
        ArrayList<Satis> satinAlimlar = new ArrayList<>();

        try {

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, this.id);
            
            ResultSet rs = pstmt.executeQuery();
 
            
            while (rs.next()) {
                
                satinAlimlar.add( new Satis(rs.getString("kurs_adi"), rs.getInt("ucret"), rs.getString("tarih"), rs.getString("odeme_tipi")) );
                
            }


            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return satinAlimlar;

    }
    
    
    
    //Kursiyeri parametre olarak verilen kurstan çıkarır
    public boolean kurstanCikar(String kursAdi) {

        String sql = "DELETE FROM Kursiyer_Kurs WHERE kursiyer_id=? AND kurs_adi=?";
        String sql2 = "UPDATE Kurs SET doluluk=doluluk-1 WHERE kurs_adi=?";

        Connection conn = MainProgram.getDatabaseConnection();

        try {

            PreparedStatement pstmt = conn.prepareStatement(sql);
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            
            pstmt.setInt(1, this.id);
            pstmt.setString(2, kursAdi);
            
            
            pstmt.executeUpdate();
            
            pstmt2.setString(1, kursAdi);
            pstmt2.executeUpdate();
 
            conn.close();
            return true;
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return false;
                

    }
    
    
}
