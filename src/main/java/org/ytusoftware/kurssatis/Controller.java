/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytusoftware.kurssatis;

import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 *
 * @author baris
 */
public class Controller {
    public String checkFields(DefaultListModel lm, String mm){
        if (lm.isEmpty()) {
            return "Lütfen kursta yer alacak ders ekleyiniz!";
        }
        
        else if (mm.compareTo("") == 0){
            return "Lütfen ödenebilecek max miktar giriniz!";
        }
        
        return "";
    }
    
    public String checkTextField(JTextField ka, JTextField ksa, JTextField kct, JTextField ke, JComboBox oy){
        if (ka.getText().compareTo("") == 0) {
             return "Kursiyer adı boş bırakılamaz!";
        } else if (ksa.getText().compareTo("") == 0) {
            return "Kursiyer soyadı boş bırakılamaz!";
        } else if (kct.getText().compareTo("") == 0) {
            return "Cep telefonu boş bırakılamaz!";
        } else if (ke.getText().compareTo("") == 0) {
            return "E-mail boş bırakılamaz!";
        } else if(oy.getSelectedIndex() == 0) {
            return "Lütfen ödeme yöntemi seçiniz!";
        }
        
        return "";
    }
    
    public ArrayList<Kurs> getCourses(ArrayList<String> yerAlacakDersler, JComboBox jcb, JTextField jtf){
        String time = jcb.getSelectedItem().toString();
        int maxUcret = Integer.parseInt(jtf.getText().toString());
        return Kurs.getsecilebilecekKurslar(yerAlacakDersler, time, maxUcret);
    }
    
    public ArrayList<Ders> getClasses(String kursAdi){
        Kurs kurs = new Kurs();
        kurs.setAd(kursAdi);
        return kurs.getDerslerDB();
    }
}
