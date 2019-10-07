/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytusoftware.kurssatis;

/**
 *
 * @author tekin
 */
public class Satis {
    
    private String kursAdı;
    private int odenenMiktar;
    private String tarih;
    private String odemeTipi;
    
    

    public Satis(String kursAdı, int odenenMiktar, String tarih, String odemeTipi) {
        this.kursAdı = kursAdı;
        this.odenenMiktar = odenenMiktar;
        this.tarih = tarih;
        this.odemeTipi = odemeTipi;
    }
    
    
    
    public String getKursAdı() {
        return kursAdı;
    }

    public void setKursAdı(String kursAdı) {
        this.kursAdı = kursAdı;
    }

    public int getOdenenMiktar() {
        return odenenMiktar;
    }

    public void setOdenenMiktar(int odenenMiktar) {
        this.odenenMiktar = odenenMiktar;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getOdemeTipi() {
        return odemeTipi;
    }

    public void setOdemeTipi(String odemeTipi) {
        this.odemeTipi = odemeTipi;
    }
    
    
    
    
    
}
