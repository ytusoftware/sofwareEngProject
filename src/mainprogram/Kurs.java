/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainprogram;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author tekin
 */
public class Kurs {
    
    private String ad;
    private String tip;
    private int maliyet;
    private ArrayList<Ders> dersler;

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

    public int getMaliyet() {
        return maliyet;
    }

    public void setMaliyet(int maliyet) {
        this.maliyet = maliyet;
    }

    public ArrayList<Ders> getDersler() {
        return dersler;
    }

    public void setDersler(ArrayList<Ders> dersler) {
        this.dersler = dersler;
    }
    
    
    public boolean writeDB(){
        
        String sql = "INSERT INTO Kurs(kurs_adi,kurs_tipi,maliyet) VALUES(?,?,?)";
        String sql2 = "UPDATE Ders SET kurs_adi=? WHERE id=?";

        Connection conn = MainProgram.getDatabaseConnection();
        

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);

            pstmt.setString(1, this.ad);
            pstmt.setString(2, this.tip);
            pstmt.setInt(3, this.maliyet);

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
    
}
