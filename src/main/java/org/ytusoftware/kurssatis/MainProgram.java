/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ytusoftware.kurssatis;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author tekin
 */
public class MainProgram extends javax.swing.JPanel {

    /**
     * Creates new form MainProgram
     */
    public MainProgram() {
        initComponents();
    }
    
    
    
    private JFrame frame;
    public TableRowSorter<DefaultTableModel> Tablesorter;
    
    //Bu uye alanlari tablo ve liste modelleri icin kullanilmistir
    public DefaultListModel<String> dl = new DefaultListModel<>();
    public DefaultTableModel table_model = new DefaultTableModel(new Object[] {"Ders Adı", "Bedel"},0);
    public DefaultTableModel tm_eklenebilecek_dersler = new DefaultTableModel(new Object[] {"Ders Id", "Ders Adı","Ders Günü","Ders Saati", "Kapasite"},0);
    public DefaultTableModel tm_eklenen_dersler = new DefaultTableModel(new Object[] {"Ders Id", "Ders Adı","Ders Günü","Ders Saati", "Kapasite"},0);
    public DefaultTableModel tm_ogretmen_atama = new DefaultTableModel(new Object[] {"Ders Adı","Ders Saati","Ders Kapasitesi","Öğretmen Id","Öğretmen Adı","Öğretmen Soyadı", "Bedel (TL)"},0);
    public DefaultListModel<String> lm_yer_alacak_dersler = new DefaultListModel<>();
    public DefaultTableModel tm_secilebilecek_kurslar = new DefaultTableModel(new Object[] {"Kurs Adı","Kurs Tipi","Tarih","Kapasite","Doluluk","Ücret (TL)"},0);
    public DefaultTableModel tm_icerdigi_dersler = new DefaultTableModel(new Object[] {"Ders Id", "Ders Adı","Ders Günü","Ders Saati","Ders Sınıfı"},0);
    public DefaultTableModel tm_kayitli_kursiyerler = new DefaultTableModel(new Object[] {"Id","Ad", "Soyad", "Email" },0);
    public DefaultTableModel tm_kursiyer_bilgileri = new DefaultTableModel(new Object[] {"Id","Ad", "Soyad", "Ev Tel","Cep Tel","Adres", "Email"},0);
    public DefaultTableModel tm_katildigi_kurslar = new DefaultTableModel(new Object[] {"Kurs Adı", "Ödenen Miktar", "Satın Alım Tarihi", "Ödeme Tipi"},0);
    public DefaultTableModel tm_ogretmen_bilgileri = new DefaultTableModel(new Object[] {"Id","Ad", "Soyad", "Ev Tel","Cep Tel","Adres", "Email"},0);
    public DefaultListModel<String> lm_calisabildigi_saatler = new DefaultListModel<>();
    public DefaultTableModel tm_verebildigi_dersler = new DefaultTableModel(new Object[] {"Ders Adı","Bedel"},0);
    public DefaultTableModel tm_kurs_bilgileri = new DefaultTableModel(new Object[] {"Kurs Adı","Kurs Tipi","Tarih","Kapasite","Doluluk","Ücret (TL)"},0);
    
    
    
    //Bu uye alanlari eventler arasindaki geciste nesneleri global olarak saklamak icin kullanilmistir
    public ArrayList<Ders> eklenenDersler = new ArrayList<Ders>();
    public int maliyet;
    
    
    public void setFrame(JFrame frame){
        
        this.frame = frame;
        this.frame.getContentPane().add(this.anaEkran);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.pack();
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
       
    }
    
    
    public void switchPanel(JPanel panel){
        
        this.frame.getContentPane().removeAll();   
        this.frame.getContentPane().add(panel);
        this.frame.validate();
        this.frame.repaint();
        
        
    }
    
    
    public static Connection getDatabaseConnection(){
        
        Connection conn = null;
        try{
            String url = "jdbc:sqlite:sistem.db";   
        
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            
            
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
        return conn;
        
        
    }
    
    /*Search box filtering icin*/
    private void newFilter(JTextField initial_box) {
        RowFilter<DefaultTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(initial_box.getText(), 2);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        Tablesorter.setRowFilter(rf);
    }
    
    
        /*Search box filtering icin*/
    private void newFilterKurs(JTextField initial_box) {
        RowFilter<DefaultTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(initial_box.getText(), 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        Tablesorter.setRowFilter(rf);
    }


    //Ders bilgilerini dondurur. Kategorik ders bilgileri
    public static ArrayList<String> getDersBilgileri(){
        String sql = "SELECT ders_adi FROM Ders_Bilgileri";
        Connection conn = MainProgram.getDatabaseConnection();
        ArrayList<String> dersAdlari = new ArrayList<String>();
        
        
 
        try{
                Statement stmt  = conn.createStatement();
                
                //Ders adlari cekiliyor
                ResultSet rs = stmt.executeQuery(sql);
                while(rs.next()){
                    dersAdlari.add(rs.getString("ders_adi"));
                } 
                
                
                
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return dersAdlari;
    }
    
    
    //Veri tabanına kategorik ders bilgisi ekler
    public void addDersBilgisiVT(String ders){
        String sql = "INSERT INTO Ders_Bilgileri(ders_adi) VALUES (?)";
        Connection conn = MainProgram.getDatabaseConnection();
 
        try{
                PreparedStatement pstmt = conn.prepareStatement(sql);
                
               //Ders bilgisi vtye yaziliyor
                pstmt.setString(1, ders);
                
                pstmt.executeUpdate();     
                
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(),"Bu isimde ders zaten mevcuttur!","Hata",JOptionPane.ERROR_MESSAGE);
            System.out.println(e.getMessage());
            
        }
        
    }
  

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        dersKaydiEkrani = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        gun = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        saat = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        kapasite = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        dersAdi = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        sinif = new javax.swing.JComboBox();
        anaEkran = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 0), new java.awt.Dimension(2, 32767));
        jButton50 = new javax.swing.JButton();
        ogretmenKaydiEkrani = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        ogretmen_soyadi = new javax.swing.JTextField();
        ogretmen_adi = new javax.swing.JTextField();
        cep_tel = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        ev_tel = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        email = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        l_calisabildigiSaatler = new javax.swing.JList();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        t_verdigi_dersler = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        adres = new javax.swing.JTextField();
        dersEkleEkrani = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        dersAdi2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        KURS_HAZIRLAMA_EKRANI = new javax.swing.JPanel();
        kursHazirlamaEkrani = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        s_kurs_tipi = new javax.swing.JComboBox();
        b_ders_getir = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        t_eklenebilecek_dersler = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        t_eklenen_dersler = new javax.swing.JTable();
        kursHazirlamaEkrani2 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        t_ogretmen_atama = new javax.swing.JTable();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        kursHazirlamaEkrani3 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        kurs_adi = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        kurs_ucreti = new javax.swing.JTextField();
        jButton25 = new javax.swing.JButton();
        jLabel55 = new javax.swing.JLabel();
        kurs_tarihi = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        KURS_SATIS_EKRANI = new javax.swing.JPanel();
        kursSatisEkrani = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        l_yer_alacak_dersler = new javax.swing.JList();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        kurs_zamani_c = new javax.swing.JComboBox();
        jLabel32 = new javax.swing.JLabel();
        max_miktar = new javax.swing.JTextField();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        kursSatisEkrani2 = new javax.swing.JPanel();
        kursSatisEkrani2_1 = new javax.swing.JPanel();
        jButton34 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        t_secilebilecek_kurslar = new javax.swing.JTable();
        jLabel36 = new javax.swing.JLabel();
        jButton41 = new javax.swing.JButton();
        kursDersleriListele = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        t_icerdigi_dersler = new javax.swing.JTable();
        kursSatisEkrani3 = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        kurs_adi_teyit = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        kurs_tarihi_teyit = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        kurs_ucreti_teyit = new javax.swing.JLabel();
        jButton35 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jLabel51 = new javax.swing.JLabel();
        kursiyer_tipi_c = new javax.swing.JComboBox();
        kursSatisEkrani4 = new javax.swing.JPanel();
        jButton30 = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        kursiyer_soyadi = new javax.swing.JTextField();
        kursiyer_adi = new javax.swing.JTextField();
        cep_tel1 = new javax.swing.JTextField();
        jLabel47 = new javax.swing.JLabel();
        ev_tel1 = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        email1 = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jButton38 = new javax.swing.JButton();
        adres1 = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        odeme_yontemi_c = new javax.swing.JComboBox();
        jButton43 = new javax.swing.JButton();
        kursSatisEkrani4_2 = new javax.swing.JPanel();
        jButton32 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jLabel58 = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        t_kayitli_kursiyerler = new javax.swing.JTable();
        search_box = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        odeme_yontemi_2_c = new javax.swing.JComboBox();
        jButton40 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        KURSIYER_BILGILERINI_GORUNTULEME_EKRANI = new javax.swing.JPanel();
        kursiyerBilgileri1 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        t_kursiyer_bilgileri = new javax.swing.JTable();
        jButton44 = new javax.swing.JButton();
        search_box_2 = new javax.swing.JTextField();
        jButton45 = new javax.swing.JButton();
        kursiyerBilgileri2 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        t_katildigi_kurslar = new javax.swing.JTable();
        jButton49 = new javax.swing.JButton();
        OGRETMEN_BILGILERINI_GORUNTULE = new javax.swing.JPanel();
        ogretmenBilgileri1 = new javax.swing.JPanel();
        jLabel40 = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        t_ogretmen_bilgileri = new javax.swing.JTable();
        jButton46 = new javax.swing.JButton();
        search_box_3 = new javax.swing.JTextField();
        jButton47 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        calisabildigiSaatler = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        l_calisabildigi_saatler = new javax.swing.JList();
        verebildigiDersler = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        t_verebildigi_dersler = new javax.swing.JTable();
        KURS_BILGILERI_GORUNTULE = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jScrollPane13 = new javax.swing.JScrollPane();
        t_kurs_bilgileri = new javax.swing.JTable();
        jButton51 = new javax.swing.JButton();
        jButton52 = new javax.swing.JButton();
        search_box_4 = new javax.swing.JTextField();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        dersKaydiEkrani.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 255));
        jLabel4.setText("Ders Kayıt Ekranı");

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel5.setText("Ders Günü:");

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel6.setText("Ders Adı:");

        gun.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        gun.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar" }));
        gun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gunActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel7.setText("Ders Saati:");

        saat.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        saat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "9.00", "10.00", "11.00", "12.00", "13.00", "14.00", "15.00", "16.00", "17.00", "18.00" }));

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel8.setText("Ders Kapasitesi:");

        kapasite.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        kapasite.setText("20");
        kapasite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kapasiteActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(51, 153, 0));
        jButton1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Kaydet");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(153, 0, 0));
        jButton9.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("İptal");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        dersAdi.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel23.setText("Ders Sınıfı:");

        sinif.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        sinif.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "S-101", "S-102", "S-103", "S-104", "S-105", "S-106", "S-107", "S-108" }));
        sinif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sinifActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dersKaydiEkraniLayout = new javax.swing.GroupLayout(dersKaydiEkrani);
        dersKaydiEkrani.setLayout(dersKaydiEkraniLayout);
        dersKaydiEkraniLayout.setHorizontalGroup(
            dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dersKaydiEkraniLayout.createSequentialGroup()
                .addContainerGap(352, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(334, 334, 334))
            .addGroup(dersKaydiEkraniLayout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dersKaydiEkraniLayout.createSequentialGroup()
                        .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addGap(68, 68, 68)
                        .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(gun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dersAdi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(dersKaydiEkraniLayout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(dersKaydiEkraniLayout.createSequentialGroup()
                        .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(jLabel23))
                        .addGap(18, 18, 18)
                        .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sinif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(saat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(kapasite, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dersKaydiEkraniLayout.setVerticalGroup(
            dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dersKaydiEkraniLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102)
                .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(dersAdi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(gun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52)
                .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(saat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(kapasite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(sinif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                .addGroup(dersKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49))
        );

        anaEkran.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("EL SANATLARI KURSU PROGRAMI");
        jLabel1.setToolTipText("");

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/77.jpg"))); // NOI18N

        jButton2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 51, 51));
        jButton2.setText("Öğretmen Kaydı Gerçekleştir");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(51, 51, 255));
        jButton4.setText("Ders Grubu Aç");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("*İŞLEM MENÜSÜ*");

        jButton5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton5.setText("Kurs Programı Hazırla");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(204, 0, 204));
        jButton6.setText("Kurs Satışı Yap");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(0, 204, 0));
        jButton7.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("Kursiyer Bilgilerini Görüntüle");
        jButton7.setPreferredSize(new java.awt.Dimension(278, 33));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(0, 0, 204));
        jButton8.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("Öğretmen Bilgilerini Görüntüle");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton11.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton11.setForeground(new java.awt.Color(204, 102, 0));
        jButton11.setText("Ders Ekle");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton50.setBackground(new java.awt.Color(255, 102, 0));
        jButton50.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton50.setForeground(new java.awt.Color(255, 255, 255));
        jButton50.setText("Kurs Bilgilerini Görüntüle");
        jButton50.setPreferredSize(new java.awt.Dimension(278, 33));
        jButton50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton50ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout anaEkranLayout = new javax.swing.GroupLayout(anaEkran);
        anaEkran.setLayout(anaEkranLayout);
        anaEkranLayout.setHorizontalGroup(
            anaEkranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, anaEkranLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
            .addGroup(anaEkranLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(anaEkranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(anaEkranLayout.createSequentialGroup()
                .addGroup(anaEkranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(anaEkranLayout.createSequentialGroup()
                        .addGap(174, 174, 174)
                        .addGroup(anaEkranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(anaEkranLayout.createSequentialGroup()
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addComponent(jButton8))
                            .addGroup(anaEkranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
                                .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(anaEkranLayout.createSequentialGroup()
                        .addGap(318, 318, 318)
                        .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(172, Short.MAX_VALUE))
        );
        anaEkranLayout.setVerticalGroup(
            anaEkranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(anaEkranLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel3)
                .addGap(24, 24, 24)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(anaEkranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(anaEkranLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, anaEkranLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(jButton11)
                .addGap(12, 12, 12)
                .addComponent(jButton4)
                .addGap(12, 12, 12)
                .addComponent(jButton5)
                .addGap(12, 12, 12)
                .addComponent(jButton6)
                .addGap(37, 37, 37)
                .addGroup(anaEkranLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        ogretmenKaydiEkrani.setBackground(new java.awt.Color(255, 255, 255));
        ogretmenKaydiEkrani.setPreferredSize(new java.awt.Dimension(929, 742));

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(204, 0, 51));
        jLabel9.setText("Öğretmen Kaydı Ekranı");

        jButton10.setBackground(new java.awt.Color(153, 0, 0));
        jButton10.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("İptal");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel12.setText("Öğretmen Adı:");

        jLabel13.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel13.setText("Ev Telefonu:");

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel14.setText("Öğretmen Soyadı:");

        ogretmen_soyadi.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        ogretmen_soyadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ogretmen_soyadiActionPerformed(evt);
            }
        });

        ogretmen_adi.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        ogretmen_adi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ogretmen_adiActionPerformed(evt);
            }
        });

        cep_tel.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cep_tel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cep_telActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel15.setText("Cep Telefonu");

        ev_tel.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        ev_tel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ev_telActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel16.setText("Adres:");

        email.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(l_calisabildigiSaatler);

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel17.setText("E-mail:");

        jLabel18.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel18.setText("Verebildiği Dersler:");

        t_verdigi_dersler.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ders Adı", "Bedel"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(t_verdigi_dersler);

        jButton3.setBackground(new java.awt.Color(153, 102, 0));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Ekle");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton14.setBackground(new java.awt.Color(153, 102, 0));
        jButton14.setForeground(new java.awt.Color(255, 255, 255));
        jButton14.setText("Ekle");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton15.setBackground(new java.awt.Color(0, 102, 0));
        jButton15.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton15.setForeground(new java.awt.Color(255, 255, 255));
        jButton15.setText("Kaydet");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel19.setText("Çalışabildiği Saatler:");

        adres.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        adres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adresActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ogretmenKaydiEkraniLayout = new javax.swing.GroupLayout(ogretmenKaydiEkrani);
        ogretmenKaydiEkrani.setLayout(ogretmenKaydiEkraniLayout);
        ogretmenKaydiEkraniLayout.setHorizontalGroup(
            ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ogretmenKaydiEkraniLayout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel13)
                        .addComponent(jLabel12)
                        .addComponent(jLabel14)
                        .addComponent(jLabel15)
                        .addComponent(jLabel17)
                        .addComponent(jLabel16)
                        .addComponent(jLabel19)
                        .addComponent(jLabel18))
                    .addGroup(ogretmenKaydiEkraniLayout.createSequentialGroup()
                        .addComponent(jButton15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addGap(26, 26, 26)
                .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                        .addComponent(email)
                        .addComponent(cep_tel)
                        .addComponent(ev_tel)
                        .addComponent(ogretmen_soyadi)
                        .addComponent(ogretmen_adi)
                        .addComponent(adres))
                    .addComponent(jButton14)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(311, Short.MAX_VALUE))
        );
        ogretmenKaydiEkraniLayout.setVerticalGroup(
            ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ogretmenKaydiEkraniLayout.createSequentialGroup()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ogretmen_adi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(30, 30, 30)
                .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(ogretmen_soyadi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(ev_tel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(cep_tel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(adres, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton14)
                .addGap(38, 38, 38)
                .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ogretmenKaydiEkraniLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(ogretmenKaydiEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel18))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        dersEkleEkrani.setBackground(new java.awt.Color(255, 255, 255));
        dersEkleEkrani.setForeground(new java.awt.Color(0, 0, 0));
        dersEkleEkrani.setPreferredSize(new java.awt.Dimension(929, 742));

        jButton12.setBackground(new java.awt.Color(153, 0, 0));
        jButton12.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton12.setForeground(new java.awt.Color(255, 255, 255));
        jButton12.setText("İptal");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        dersAdi2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        dersAdi2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dersAdi2ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(204, 102, 0));
        jLabel10.setText("Ders Ekleme Ekranı");

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel11.setText("Ders Adı:");

        jButton13.setBackground(new java.awt.Color(0, 153, 0));
        jButton13.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton13.setForeground(new java.awt.Color(255, 255, 255));
        jButton13.setText("Ekle");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout dersEkleEkraniLayout = new javax.swing.GroupLayout(dersEkleEkrani);
        dersEkleEkrani.setLayout(dersEkleEkraniLayout);
        dersEkleEkraniLayout.setHorizontalGroup(
            dersEkleEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dersEkleEkraniLayout.createSequentialGroup()
                .addGroup(dersEkleEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dersEkleEkraniLayout.createSequentialGroup()
                        .addGap(324, 324, 324)
                        .addComponent(jLabel10))
                    .addGroup(dersEkleEkraniLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(dersEkleEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(dersEkleEkraniLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(32, 32, 32)
                                .addComponent(dersAdi2, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(345, Short.MAX_VALUE))
        );
        dersEkleEkraniLayout.setVerticalGroup(
            dersEkleEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dersEkleEkraniLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(101, 101, 101)
                .addGroup(dersEkleEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dersAdi2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(99, 99, 99)
                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(340, Short.MAX_VALUE))
        );

        kursHazirlamaEkrani.setBackground(new java.awt.Color(255, 255, 255));
        kursHazirlamaEkrani.setPreferredSize(new java.awt.Dimension(929, 742));

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(204, 0, 0));
        jLabel20.setText("Kurs Programı Hazırlama Ekranı");

        jLabel21.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel21.setText("Kurs tipi seçiniz:");

        s_kurs_tipi.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        s_kurs_tipi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hafta İçi", "Hafta Sonu" }));

        b_ders_getir.setBackground(new java.awt.Color(153, 102, 0));
        b_ders_getir.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        b_ders_getir.setForeground(new java.awt.Color(255, 255, 255));
        b_ders_getir.setText("Kursa Eklenebilecek Dersleri Listele");
        b_ders_getir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b_ders_getirActionPerformed(evt);
            }
        });

        jButton16.setBackground(new java.awt.Color(0, 0, 255));
        jButton16.setForeground(new java.awt.Color(255, 255, 255));
        jButton16.setText("Devam Et");
        jButton16.setEnabled(false);
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton17.setBackground(new java.awt.Color(255, 255, 255));
        jButton17.setForeground(new java.awt.Color(0, 153, 0));
        jButton17.setText("Seçilen Dersi Ekle");
        jButton17.setEnabled(false);
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton18.setBackground(new java.awt.Color(255, 255, 255));
        jButton18.setForeground(new java.awt.Color(204, 0, 0));
        jButton18.setText("Seçilen Dersi Sil");
        jButton18.setEnabled(false);
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setBackground(new java.awt.Color(153, 0, 0));
        jButton19.setForeground(new java.awt.Color(255, 255, 255));
        jButton19.setText("İptal");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        t_eklenebilecek_dersler.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        t_eklenebilecek_dersler.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane6.setViewportView(t_eklenebilecek_dersler);

        t_eklenen_dersler.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        t_eklenen_dersler.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(t_eklenen_dersler);

        javax.swing.GroupLayout kursHazirlamaEkraniLayout = new javax.swing.GroupLayout(kursHazirlamaEkrani);
        kursHazirlamaEkrani.setLayout(kursHazirlamaEkraniLayout);
        kursHazirlamaEkraniLayout.setHorizontalGroup(
            kursHazirlamaEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursHazirlamaEkraniLayout.createSequentialGroup()
                .addGap(256, 256, 256)
                .addComponent(jLabel20)
                .addContainerGap(257, Short.MAX_VALUE))
            .addGroup(kursHazirlamaEkraniLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(kursHazirlamaEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kursHazirlamaEkraniLayout.createSequentialGroup()
                        .addGroup(kursHazirlamaEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(b_ders_getir)
                            .addGroup(kursHazirlamaEkraniLayout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(s_kurs_tipi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton17))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(kursHazirlamaEkraniLayout.createSequentialGroup()
                        .addGroup(kursHazirlamaEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, kursHazirlamaEkraniLayout.createSequentialGroup()
                                .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton18, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        kursHazirlamaEkraniLayout.setVerticalGroup(
            kursHazirlamaEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursHazirlamaEkraniLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addGap(72, 72, 72)
                .addGroup(kursHazirlamaEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(s_kurs_tipi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(b_ders_getir)
                .addGap(66, 66, 66)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton17)
                .addGap(40, 40, 40)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                .addGroup(kursHazirlamaEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        kursHazirlamaEkrani2.setBackground(new java.awt.Color(255, 255, 255));
        kursHazirlamaEkrani2.setPreferredSize(new java.awt.Dimension(929, 742));

        jLabel22.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(204, 0, 0));
        jLabel22.setText("Kurs Programı Hazırlama Ekranı");

        t_ogretmen_atama.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        t_ogretmen_atama.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(t_ogretmen_atama);

        jButton21.setBackground(new java.awt.Color(255, 102, 0));
        jButton21.setForeground(new java.awt.Color(255, 255, 255));
        jButton21.setText("Seçilen Derse Öğretmen Ata");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jButton22.setBackground(new java.awt.Color(0, 0, 255));
        jButton22.setForeground(new java.awt.Color(255, 255, 255));
        jButton22.setText("Devam Et");
        jButton22.setEnabled(false);
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        jButton20.setBackground(new java.awt.Color(153, 0, 0));
        jButton20.setForeground(new java.awt.Color(255, 255, 255));
        jButton20.setText("Geri");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kursHazirlamaEkrani2Layout = new javax.swing.GroupLayout(kursHazirlamaEkrani2);
        kursHazirlamaEkrani2.setLayout(kursHazirlamaEkrani2Layout);
        kursHazirlamaEkrani2Layout.setHorizontalGroup(
            kursHazirlamaEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kursHazirlamaEkrani2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel22)
                .addGap(252, 252, 252))
            .addGroup(kursHazirlamaEkrani2Layout.createSequentialGroup()
                .addGroup(kursHazirlamaEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kursHazirlamaEkrani2Layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addGroup(kursHazirlamaEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 785, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton21)))
                    .addGroup(kursHazirlamaEkrani2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        kursHazirlamaEkrani2Layout.setVerticalGroup(
            kursHazirlamaEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursHazirlamaEkrani2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addGap(54, 54, 54)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 230, Short.MAX_VALUE)
                .addGroup(kursHazirlamaEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17))
        );

        kursHazirlamaEkrani3.setBackground(new java.awt.Color(255, 255, 255));
        kursHazirlamaEkrani3.setPreferredSize(new java.awt.Dimension(929, 742));

        jLabel24.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(204, 0, 0));
        jLabel24.setText("Kurs Programı Hazırlama Ekranı");

        jLabel25.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel25.setText("Toplam Kurs Maliyeti:");

        jLabel26.setFont(new java.awt.Font("Dialog", 3, 24)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setText("#MALİYET#");

        jButton23.setBackground(new java.awt.Color(204, 0, 0));
        jButton23.setForeground(new java.awt.Color(255, 255, 255));
        jButton23.setText("Geri");
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });

        jButton24.setBackground(new java.awt.Color(0, 102, 0));
        jButton24.setForeground(new java.awt.Color(255, 255, 255));
        jButton24.setText("Onayla");
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel27.setText("Kurs Ücreti:");

        kurs_adi.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jLabel28.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel28.setText("Kurs Adı:");

        kurs_ucreti.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        kurs_ucreti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kurs_ucretiActionPerformed(evt);
            }
        });

        jButton25.setBackground(new java.awt.Color(0, 0, 204));
        jButton25.setForeground(new java.awt.Color(255, 255, 255));
        jButton25.setText("Ana Ekran");
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });

        jLabel55.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel55.setText("Kurs Tarihi (\"YYYY-MM-dd\"):");

        kurs_tarihi.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        kurs_tarihi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kurs_tarihiActionPerformed(evt);
            }
        });

        jLabel56.setText("Örnek: 1997-06-22");

        javax.swing.GroupLayout kursHazirlamaEkrani3Layout = new javax.swing.GroupLayout(kursHazirlamaEkrani3);
        kursHazirlamaEkrani3.setLayout(kursHazirlamaEkrani3Layout);
        kursHazirlamaEkrani3Layout.setHorizontalGroup(
            kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kursHazirlamaEkrani3Layout.createSequentialGroup()
                .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(kursHazirlamaEkrani3Layout.createSequentialGroup()
                        .addContainerGap(259, Short.MAX_VALUE)
                        .addComponent(jLabel24))
                    .addGroup(kursHazirlamaEkrani3Layout.createSequentialGroup()
                        .addGap(217, 217, 217)
                        .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(254, 254, 254))
            .addGroup(kursHazirlamaEkrani3Layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel56)
                    .addGroup(kursHazirlamaEkrani3Layout.createSequentialGroup()
                        .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel25)
                            .addComponent(jLabel27)
                            .addComponent(jLabel28)
                            .addComponent(jLabel55))
                        .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(kursHazirlamaEkrani3Layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(jLabel26))
                            .addGroup(kursHazirlamaEkrani3Layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(kurs_ucreti, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(kurs_tarihi, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(kurs_adi, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(kursHazirlamaEkrani3Layout.createSequentialGroup()
                    .addGap(91, 91, 91)
                    .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(725, Short.MAX_VALUE)))
        );
        kursHazirlamaEkrani3Layout.setVerticalGroup(
            kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursHazirlamaEkrani3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addGap(121, 121, 121)
                .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(kursHazirlamaEkrani3Layout.createSequentialGroup()
                        .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(jLabel26))
                        .addGap(101, 101, 101))
                    .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28)
                        .addComponent(kurs_adi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(87, 87, 87)
                .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(kurs_ucreti, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(85, 85, 85)
                .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(kurs_tarihi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel56)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 114, Short.MAX_VALUE)
                .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton23, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton25, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36))
            .addGroup(kursHazirlamaEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kursHazirlamaEkrani3Layout.createSequentialGroup()
                    .addContainerGap(664, Short.MAX_VALUE)
                    .addComponent(jButton24, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(35, 35, 35)))
        );

        javax.swing.GroupLayout KURS_HAZIRLAMA_EKRANILayout = new javax.swing.GroupLayout(KURS_HAZIRLAMA_EKRANI);
        KURS_HAZIRLAMA_EKRANI.setLayout(KURS_HAZIRLAMA_EKRANILayout);
        KURS_HAZIRLAMA_EKRANILayout.setHorizontalGroup(
            KURS_HAZIRLAMA_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 929, Short.MAX_VALUE)
            .addGroup(KURS_HAZIRLAMA_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_HAZIRLAMA_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursHazirlamaEkrani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURS_HAZIRLAMA_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_HAZIRLAMA_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursHazirlamaEkrani2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURS_HAZIRLAMA_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_HAZIRLAMA_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursHazirlamaEkrani3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        KURS_HAZIRLAMA_EKRANILayout.setVerticalGroup(
            KURS_HAZIRLAMA_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 742, Short.MAX_VALUE)
            .addGroup(KURS_HAZIRLAMA_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_HAZIRLAMA_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursHazirlamaEkrani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURS_HAZIRLAMA_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_HAZIRLAMA_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursHazirlamaEkrani2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURS_HAZIRLAMA_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_HAZIRLAMA_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursHazirlamaEkrani3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        kursSatisEkrani.setBackground(new java.awt.Color(255, 255, 255));
        kursSatisEkrani.setPreferredSize(new java.awt.Dimension(929, 742));

        jLabel29.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(204, 0, 204));
        jLabel29.setText("Kurs Satış Ekranı");

        jLabel30.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel30.setText("Kurs içerisinde yer alacak dersler:");

        l_yer_alacak_dersler.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        l_yer_alacak_dersler.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane5.setViewportView(l_yer_alacak_dersler);

        jButton26.setBackground(new java.awt.Color(204, 0, 0));
        jButton26.setForeground(new java.awt.Color(255, 255, 255));
        jButton26.setText("Seçilen Dersi Sil");
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });

        jButton27.setBackground(new java.awt.Color(255, 102, 0));
        jButton27.setForeground(new java.awt.Color(255, 255, 255));
        jButton27.setText("Ekle");
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel31.setText("Kurs zamanı:");

        kurs_zamani_c.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        kurs_zamani_c.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hafta İçi", "Hafta Sonu" }));

        jLabel32.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel32.setText("Ödenebilecek maksimum miktar (TL):");

        max_miktar.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jButton28.setBackground(new java.awt.Color(204, 0, 0));
        jButton28.setForeground(new java.awt.Color(255, 255, 255));
        jButton28.setText("Geri");
        jButton28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton28ActionPerformed(evt);
            }
        });

        jButton29.setBackground(new java.awt.Color(0, 0, 204));
        jButton29.setForeground(new java.awt.Color(255, 255, 255));
        jButton29.setText("Devam Et");
        jButton29.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton29ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kursSatisEkraniLayout = new javax.swing.GroupLayout(kursSatisEkrani);
        kursSatisEkrani.setLayout(kursSatisEkraniLayout);
        kursSatisEkraniLayout.setHorizontalGroup(
            kursSatisEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursSatisEkraniLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(kursSatisEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kursSatisEkraniLayout.createSequentialGroup()
                        .addGroup(kursSatisEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel30)
                            .addComponent(jLabel31)
                            .addComponent(jLabel32))
                        .addGap(20, 20, 20)
                        .addGroup(kursSatisEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(max_miktar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(kurs_zamani_c, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(kursSatisEkraniLayout.createSequentialGroup()
                                .addComponent(jButton27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton26))))
                    .addGroup(kursSatisEkraniLayout.createSequentialGroup()
                        .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(237, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kursSatisEkraniLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel29)
                .addGap(350, 350, 350))
        );
        kursSatisEkraniLayout.setVerticalGroup(
            kursSatisEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursSatisEkraniLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addGap(74, 74, 74)
                .addGroup(kursSatisEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(kursSatisEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton26)
                    .addComponent(jButton27))
                .addGap(81, 81, 81)
                .addGroup(kursSatisEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(kurs_zamani_c, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(99, 99, 99)
                .addGroup(kursSatisEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(max_miktar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 227, Short.MAX_VALUE)
                .addGroup(kursSatisEkraniLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton28, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton29, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27))
        );

        kursSatisEkrani2.setBackground(new java.awt.Color(255, 255, 255));
        kursSatisEkrani2.setPreferredSize(new java.awt.Dimension(929, 742));

        kursSatisEkrani2_1.setBackground(new java.awt.Color(255, 255, 255));
        kursSatisEkrani2_1.setPreferredSize(new java.awt.Dimension(929, 742));

        jButton34.setBackground(new java.awt.Color(0, 0, 204));
        jButton34.setForeground(new java.awt.Color(255, 255, 255));
        jButton34.setText("Devam Et");
        jButton34.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton34ActionPerformed(evt);
            }
        });

        jButton31.setBackground(new java.awt.Color(0, 102, 102));
        jButton31.setForeground(new java.awt.Color(255, 255, 255));
        jButton31.setText("Seçilen Kursun İçerdiği Dersleri Görüntüle");
        jButton31.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton31ActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel34.setText("Lütfen kayıt olunacak kursu seçip \"Devam Et\" butonuna tıklayınız.");

        t_secilebilecek_kurslar.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        t_secilebilecek_kurslar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane9.setViewportView(t_secilebilecek_kurslar);

        jLabel36.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(204, 0, 204));
        jLabel36.setText("Kurs Satış Ekranı");

        jButton41.setBackground(new java.awt.Color(204, 0, 0));
        jButton41.setForeground(new java.awt.Color(255, 255, 255));
        jButton41.setText("Geri");
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kursSatisEkrani2_1Layout = new javax.swing.GroupLayout(kursSatisEkrani2_1);
        kursSatisEkrani2_1.setLayout(kursSatisEkrani2_1Layout);
        kursSatisEkrani2_1Layout.setHorizontalGroup(
            kursSatisEkrani2_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kursSatisEkrani2_1Layout.createSequentialGroup()
                .addContainerGap(356, Short.MAX_VALUE)
                .addComponent(jLabel36)
                .addGap(351, 351, 351))
            .addGroup(kursSatisEkrani2_1Layout.createSequentialGroup()
                .addGap(191, 191, 191)
                .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(kursSatisEkrani2_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(kursSatisEkrani2_1Layout.createSequentialGroup()
                    .addGap(73, 73, 73)
                    .addGroup(kursSatisEkrani2_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton34)
                        .addComponent(jLabel34)
                        .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 782, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton31))
                    .addContainerGap(74, Short.MAX_VALUE)))
        );
        kursSatisEkrani2_1Layout.setVerticalGroup(
            kursSatisEkrani2_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursSatisEkrani2_1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 632, Short.MAX_VALUE)
                .addComponent(jButton41, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
            .addGroup(kursSatisEkrani2_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(kursSatisEkrani2_1Layout.createSequentialGroup()
                    .addGap(138, 138, 138)
                    .addComponent(jLabel34)
                    .addGap(18, 18, 18)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jButton31)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 151, Short.MAX_VALUE)
                    .addComponent(jButton34, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(32, 32, 32)))
        );

        kursDersleriListele.setBackground(new java.awt.Color(255, 255, 255));
        kursDersleriListele.setPreferredSize(new java.awt.Dimension(490, 380));

        t_icerdigi_dersler.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane8.setViewportView(t_icerdigi_dersler);

        javax.swing.GroupLayout kursDersleriListeleLayout = new javax.swing.GroupLayout(kursDersleriListele);
        kursDersleriListele.setLayout(kursDersleriListeleLayout);
        kursDersleriListeleLayout.setHorizontalGroup(
            kursDersleriListeleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursDersleriListeleLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        kursDersleriListeleLayout.setVerticalGroup(
            kursDersleriListeleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursDersleriListeleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout kursSatisEkrani2Layout = new javax.swing.GroupLayout(kursSatisEkrani2);
        kursSatisEkrani2.setLayout(kursSatisEkrani2Layout);
        kursSatisEkrani2Layout.setHorizontalGroup(
            kursSatisEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 929, Short.MAX_VALUE)
            .addGroup(kursSatisEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(kursSatisEkrani2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursDersleriListele, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(kursSatisEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(kursSatisEkrani2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursSatisEkrani2_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        kursSatisEkrani2Layout.setVerticalGroup(
            kursSatisEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 742, Short.MAX_VALUE)
            .addGroup(kursSatisEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(kursSatisEkrani2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursDersleriListele, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(kursSatisEkrani2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(kursSatisEkrani2Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursSatisEkrani2_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        kursSatisEkrani3.setBackground(new java.awt.Color(255, 255, 255));
        kursSatisEkrani3.setPreferredSize(new java.awt.Dimension(929, 742));

        jLabel37.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(204, 0, 204));
        jLabel37.setText("Kurs Satış Ekranı");

        jLabel33.setFont(new java.awt.Font("Dialog", 3, 24)); // NOI18N
        jLabel33.setText("Seçilen Kurs Bilgileri");

        jLabel35.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel35.setText("Kurs Tarihi:");

        kurs_adi_teyit.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        kurs_adi_teyit.setText("#Kurs Adı#");

        jLabel39.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel39.setText("Kurs Adı:");

        kurs_tarihi_teyit.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        kurs_tarihi_teyit.setText("#Kurs Tarihi#");

        jLabel41.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel41.setText("Kurs Ücreti (TL):");

        kurs_ucreti_teyit.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        kurs_ucreti_teyit.setText("#Kurs Ücreti#");

        jButton35.setBackground(new java.awt.Color(204, 0, 0));
        jButton35.setForeground(new java.awt.Color(255, 255, 255));
        jButton35.setText("Ana Ekran");
        jButton35.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton35ActionPerformed(evt);
            }
        });

        jButton36.setBackground(new java.awt.Color(0, 0, 204));
        jButton36.setForeground(new java.awt.Color(255, 255, 255));
        jButton36.setText("Devam Et");
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jButton37.setBackground(new java.awt.Color(0, 0, 204));
        jButton37.setForeground(new java.awt.Color(255, 255, 255));
        jButton37.setText("Geri");
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jLabel51.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel51.setText("Kursiyer Tipi Seçiniz:");

        kursiyer_tipi_c.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        kursiyer_tipi_c.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seçiniz", "Yeni Kayıt", "Kayıtlı" }));

        javax.swing.GroupLayout kursSatisEkrani3Layout = new javax.swing.GroupLayout(kursSatisEkrani3);
        kursSatisEkrani3.setLayout(kursSatisEkrani3Layout);
        kursSatisEkrani3Layout.setHorizontalGroup(
            kursSatisEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, kursSatisEkrani3Layout.createSequentialGroup()
                .addGap(0, 359, Short.MAX_VALUE)
                .addComponent(jLabel37)
                .addGap(348, 348, 348))
            .addGroup(kursSatisEkrani3Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(kursSatisEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kursSatisEkrani3Layout.createSequentialGroup()
                        .addComponent(jLabel51)
                        .addGap(39, 39, 39)
                        .addComponent(kursiyer_tipi_c, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kursSatisEkrani3Layout.createSequentialGroup()
                        .addComponent(jButton36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel33)
                    .addGroup(kursSatisEkrani3Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(kursSatisEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35)
                            .addComponent(jLabel39)
                            .addComponent(jLabel41))
                        .addGap(114, 114, 114)
                        .addGroup(kursSatisEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(kurs_adi_teyit)
                            .addComponent(kurs_tarihi_teyit)
                            .addComponent(kurs_ucreti_teyit))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        kursSatisEkrani3Layout.setVerticalGroup(
            kursSatisEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursSatisEkrani3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel37)
                .addGap(57, 57, 57)
                .addComponent(jLabel33)
                .addGap(18, 18, 18)
                .addGroup(kursSatisEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kurs_adi_teyit)
                    .addComponent(jLabel39))
                .addGap(79, 79, 79)
                .addGroup(kursSatisEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kurs_tarihi_teyit)
                    .addComponent(jLabel35))
                .addGap(88, 88, 88)
                .addGroup(kursSatisEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(kurs_ucreti_teyit))
                .addGap(105, 105, 105)
                .addGroup(kursSatisEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(kursiyer_tipi_c, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 164, Short.MAX_VALUE)
                .addGroup(kursSatisEkrani3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton36, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton37, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton35, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29))
        );

        kursSatisEkrani4.setBackground(new java.awt.Color(255, 255, 255));
        kursSatisEkrani4.setPreferredSize(new java.awt.Dimension(929, 742));

        jButton30.setBackground(new java.awt.Color(153, 0, 0));
        jButton30.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton30.setForeground(new java.awt.Color(255, 255, 255));
        jButton30.setText("Geri");
        jButton30.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton30ActionPerformed(evt);
            }
        });

        jLabel44.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel44.setText("Kursiyer Adı:");

        jLabel45.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel45.setText("Ev Telefonu:");

        jLabel46.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel46.setText("Kursiyer Soyadı:");

        kursiyer_soyadi.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        kursiyer_soyadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kursiyer_soyadiActionPerformed(evt);
            }
        });

        kursiyer_adi.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        kursiyer_adi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kursiyer_adiActionPerformed(evt);
            }
        });

        cep_tel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        cep_tel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cep_tel1ActionPerformed(evt);
            }
        });

        jLabel47.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel47.setText("Cep Telefonu");

        ev_tel1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        ev_tel1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ev_tel1ActionPerformed(evt);
            }
        });

        jLabel48.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel48.setText("Adres:");

        email1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        email1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                email1ActionPerformed(evt);
            }
        });

        jLabel49.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel49.setText("E-mail:");

        jButton38.setBackground(new java.awt.Color(0, 102, 0));
        jButton38.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton38.setForeground(new java.awt.Color(255, 255, 255));
        jButton38.setText("Kurs Satış İşlemini Gerçekleştir");
        jButton38.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton38ActionPerformed(evt);
            }
        });

        adres1.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        adres1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adres1ActionPerformed(evt);
            }
        });

        jLabel43.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(204, 0, 204));
        jLabel43.setText("Kurs Satış Ekranı");

        jLabel53.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel53.setText("Lütfen kursiyer bilgilerini girip kursa satış işlemini tamamlayınız.");

        jLabel54.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel54.setText("Ödeme Yöntemi");

        odeme_yontemi_c.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        odeme_yontemi_c.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seçiniz", "Nakit", "Kredi Kartı" }));

        jButton43.setBackground(new java.awt.Color(153, 0, 0));
        jButton43.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton43.setForeground(new java.awt.Color(255, 255, 255));
        jButton43.setText("Ana Ekran");
        jButton43.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton43ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kursSatisEkrani4Layout = new javax.swing.GroupLayout(kursSatisEkrani4);
        kursSatisEkrani4.setLayout(kursSatisEkrani4Layout);
        kursSatisEkrani4Layout.setHorizontalGroup(
            kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                        .addGap(360, 360, 360)
                        .addComponent(jLabel43))
                    .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                        .addGap(229, 229, 229)
                        .addComponent(kursiyer_adi, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                        .addGap(229, 229, 229)
                        .addComponent(kursiyer_soyadi, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                        .addGap(229, 229, 229)
                        .addComponent(ev_tel1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                        .addGap(229, 229, 229)
                        .addComponent(cep_tel1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                        .addGap(229, 229, 229)
                        .addComponent(email1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                                .addComponent(jLabel54)
                                .addGap(35, 35, 35)
                                .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(adres1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(odeme_yontemi_c, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel48)
                            .addComponent(jLabel49)
                            .addComponent(jLabel47)
                            .addComponent(jLabel45)
                            .addComponent(jLabel46)
                            .addComponent(jLabel44)
                            .addComponent(jLabel53)
                            .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                                .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(237, 237, 237))
        );
        kursSatisEkrani4Layout.setVerticalGroup(
            kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursSatisEkrani4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel43)
                .addGap(55, 55, 55)
                .addComponent(jLabel53)
                .addGap(53, 53, 53)
                .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(kursiyer_adi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(kursiyer_soyadi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(ev_tel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(cep_tel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(45, 45, 45)
                .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(email1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(adres1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel54)
                    .addComponent(odeme_yontemi_c, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(kursSatisEkrani4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton38, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton30, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton43, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(246, 246, 246))
        );

        kursSatisEkrani4_2.setBackground(new java.awt.Color(255, 255, 255));
        kursSatisEkrani4_2.setPreferredSize(new java.awt.Dimension(929, 755));

        jButton32.setBackground(new java.awt.Color(153, 0, 0));
        jButton32.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton32.setForeground(new java.awt.Color(255, 255, 255));
        jButton32.setText("İptal");
        jButton32.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton32ActionPerformed(evt);
            }
        });

        jButton39.setBackground(new java.awt.Color(0, 102, 0));
        jButton39.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton39.setForeground(new java.awt.Color(255, 255, 255));
        jButton39.setText("Seçilen Kursiyer ile Devam Et");
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        jLabel58.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(204, 0, 204));
        jLabel58.setText("Kurs Satış Ekranı");

        t_kayitli_kursiyerler.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        t_kayitli_kursiyerler.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane10.setViewportView(t_kayitli_kursiyerler);

        search_box.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        search_box.setText("Soyisim ile arama...");

        jLabel50.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel50.setText("Lütfen bir kursiyer seçip kursa kayıt işlemini tamamlayınız.");

        jLabel52.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel52.setText("Ödeme Yöntemi:");

        odeme_yontemi_2_c.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        odeme_yontemi_2_c.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Seçiniz", "Nakit", "Kredi Kartı" }));

        jButton40.setBackground(new java.awt.Color(0, 102, 0));
        jButton40.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton40.setForeground(new java.awt.Color(255, 255, 255));
        jButton40.setText("Kurs Satış İşlemini Gerçekleştir");
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        jButton33.setBackground(new java.awt.Color(153, 0, 0));
        jButton33.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton33.setForeground(new java.awt.Color(255, 255, 255));
        jButton33.setText("Geri");
        jButton33.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton33ActionPerformed(evt);
            }
        });

        jButton42.setBackground(new java.awt.Color(153, 0, 0));
        jButton42.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jButton42.setForeground(new java.awt.Color(255, 255, 255));
        jButton42.setText("Ana Ekran");
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kursSatisEkrani4_2Layout = new javax.swing.GroupLayout(kursSatisEkrani4_2);
        kursSatisEkrani4_2.setLayout(kursSatisEkrani4_2Layout);
        kursSatisEkrani4_2Layout.setHorizontalGroup(
            kursSatisEkrani4_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursSatisEkrani4_2Layout.createSequentialGroup()
                .addGroup(kursSatisEkrani4_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kursSatisEkrani4_2Layout.createSequentialGroup()
                        .addGap(360, 360, 360)
                        .addComponent(jLabel58))
                    .addGroup(kursSatisEkrani4_2Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(kursSatisEkrani4_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel50)
                            .addGroup(kursSatisEkrani4_2Layout.createSequentialGroup()
                                .addComponent(jLabel52)
                                .addGap(37, 37, 37)
                                .addComponent(odeme_yontemi_2_c, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 818, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(search_box, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(kursSatisEkrani4_2Layout.createSequentialGroup()
                                .addComponent(jButton40, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton33, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton42, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(kursSatisEkrani4_2Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jButton39, javax.swing.GroupLayout.PREFERRED_SIZE, 345, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                .addComponent(jButton32, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(321, 321, 321))
        );
        kursSatisEkrani4_2Layout.setVerticalGroup(
            kursSatisEkrani4_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursSatisEkrani4_2Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel58)
                .addGap(70, 70, 70)
                .addComponent(jLabel50)
                .addGap(45, 45, 45)
                .addComponent(search_box, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79)
                .addGroup(kursSatisEkrani4_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(odeme_yontemi_2_c, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(kursSatisEkrani4_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton40)
                    .addComponent(jButton33)
                    .addComponent(jButton42))
                .addGap(139, 139, 139)
                .addGroup(kursSatisEkrani4_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton39)
                    .addComponent(jButton32))
                .addGap(34, 34, 34))
        );

        javax.swing.GroupLayout KURS_SATIS_EKRANILayout = new javax.swing.GroupLayout(KURS_SATIS_EKRANI);
        KURS_SATIS_EKRANI.setLayout(KURS_SATIS_EKRANILayout);
        KURS_SATIS_EKRANILayout.setHorizontalGroup(
            KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1370, Short.MAX_VALUE)
            .addGroup(KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_SATIS_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursSatisEkrani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_SATIS_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursSatisEkrani2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_SATIS_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursSatisEkrani3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_SATIS_EKRANILayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(kursSatisEkrani4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_SATIS_EKRANILayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(kursSatisEkrani4_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(429, Short.MAX_VALUE)))
        );
        KURS_SATIS_EKRANILayout.setVerticalGroup(
            KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 875, Short.MAX_VALUE)
            .addGroup(KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_SATIS_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursSatisEkrani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_SATIS_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursSatisEkrani2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_SATIS_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursSatisEkrani3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_SATIS_EKRANILayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(kursSatisEkrani4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(KURS_SATIS_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURS_SATIS_EKRANILayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(kursSatisEkrani4_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(108, Short.MAX_VALUE)))
        );

        kursiyerBilgileri1.setBackground(new java.awt.Color(255, 255, 255));
        kursiyerBilgileri1.setPreferredSize(new java.awt.Dimension(929, 742));

        jLabel38.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(0, 0, 204));
        jLabel38.setText("Kursiyer Bilgileri Görüntüleme Ekranı");

        t_kursiyer_bilgileri.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        t_kursiyer_bilgileri.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane7.setViewportView(t_kursiyer_bilgileri);

        jButton44.setBackground(new java.awt.Color(255, 102, 0));
        jButton44.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton44.setForeground(new java.awt.Color(255, 255, 255));
        jButton44.setText("Seçilen Kursiyerin Katıldığı Kursları Görüntüle");
        jButton44.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton44ActionPerformed(evt);
            }
        });

        search_box_2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        search_box_2.setText("Soyisim ile ara...");

        jButton45.setBackground(new java.awt.Color(204, 0, 0));
        jButton45.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton45.setForeground(new java.awt.Color(255, 255, 255));
        jButton45.setText("Geri");
        jButton45.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton45ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kursiyerBilgileri1Layout = new javax.swing.GroupLayout(kursiyerBilgileri1);
        kursiyerBilgileri1.setLayout(kursiyerBilgileri1Layout);
        kursiyerBilgileri1Layout.setHorizontalGroup(
            kursiyerBilgileri1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursiyerBilgileri1Layout.createSequentialGroup()
                .addGroup(kursiyerBilgileri1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(kursiyerBilgileri1Layout.createSequentialGroup()
                        .addGap(215, 215, 215)
                        .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(kursiyerBilgileri1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(kursiyerBilgileri1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(search_box_2, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(kursiyerBilgileri1Layout.createSequentialGroup()
                                .addComponent(jButton44)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton45, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 895, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        kursiyerBilgileri1Layout.setVerticalGroup(
            kursiyerBilgileri1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursiyerBilgileri1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(search_box_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(kursiyerBilgileri1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton44)
                    .addComponent(jButton45))
                .addContainerGap(122, Short.MAX_VALUE))
        );

        kursiyerBilgileri2.setBackground(new java.awt.Color(255, 255, 255));
        kursiyerBilgileri2.setPreferredSize(new java.awt.Dimension(600, 500));

        t_katildigi_kurslar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane11.setViewportView(t_katildigi_kurslar);

        jButton49.setBackground(new java.awt.Color(204, 0, 0));
        jButton49.setForeground(new java.awt.Color(255, 255, 255));
        jButton49.setText("Seçilen Kurs Kaydını İptal Et");
        jButton49.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton49ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kursiyerBilgileri2Layout = new javax.swing.GroupLayout(kursiyerBilgileri2);
        kursiyerBilgileri2.setLayout(kursiyerBilgileri2Layout);
        kursiyerBilgileri2Layout.setHorizontalGroup(
            kursiyerBilgileri2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursiyerBilgileri2Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addGroup(kursiyerBilgileri2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton49)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        kursiyerBilgileri2Layout.setVerticalGroup(
            kursiyerBilgileri2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kursiyerBilgileri2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton49)
                .addContainerGap(76, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout = new javax.swing.GroupLayout(KURSIYER_BILGILERINI_GORUNTULEME_EKRANI);
        KURSIYER_BILGILERINI_GORUNTULEME_EKRANI.setLayout(KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout);
        KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.setHorizontalGroup(
            KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 929, Short.MAX_VALUE)
            .addGroup(KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursiyerBilgileri1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursiyerBilgileri2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.setVerticalGroup(
            KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 742, Short.MAX_VALUE)
            .addGroup(KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursiyerBilgileri1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KURSIYER_BILGILERINI_GORUNTULEME_EKRANILayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(kursiyerBilgileri2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        ogretmenBilgileri1.setBackground(new java.awt.Color(255, 255, 255));
        ogretmenBilgileri1.setPreferredSize(new java.awt.Dimension(929, 742));

        jLabel40.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(0, 0, 204));
        jLabel40.setText("Öğretmen Bilgileri Görüntüleme Ekranı");

        t_ogretmen_bilgileri.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        t_ogretmen_bilgileri.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane12.setViewportView(t_ogretmen_bilgileri);

        jButton46.setBackground(new java.awt.Color(255, 102, 0));
        jButton46.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton46.setForeground(new java.awt.Color(255, 255, 255));
        jButton46.setText("Çalışabildiği Saatleri Görüntüle");
        jButton46.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton46ActionPerformed(evt);
            }
        });

        search_box_3.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        search_box_3.setText("Soyisim ile ara...");
        search_box_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_box_3ActionPerformed(evt);
            }
        });

        jButton47.setBackground(new java.awt.Color(204, 0, 0));
        jButton47.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton47.setForeground(new java.awt.Color(255, 255, 255));
        jButton47.setText("Geri");
        jButton47.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton47ActionPerformed(evt);
            }
        });

        jButton48.setBackground(new java.awt.Color(0, 0, 204));
        jButton48.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton48.setForeground(new java.awt.Color(255, 255, 255));
        jButton48.setText("Verebildiği Dersleri Görüntüle");
        jButton48.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton48ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ogretmenBilgileri1Layout = new javax.swing.GroupLayout(ogretmenBilgileri1);
        ogretmenBilgileri1.setLayout(ogretmenBilgileri1Layout);
        ogretmenBilgileri1Layout.setHorizontalGroup(
            ogretmenBilgileri1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ogretmenBilgileri1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 524, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(202, 202, 202))
            .addGroup(ogretmenBilgileri1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(ogretmenBilgileri1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(ogretmenBilgileri1Layout.createSequentialGroup()
                        .addComponent(jButton46)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton48)
                        .addGap(179, 179, 179)
                        .addComponent(jButton47, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 887, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, ogretmenBilgileri1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(search_box_3, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        ogretmenBilgileri1Layout.setVerticalGroup(
            ogretmenBilgileri1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ogretmenBilgileri1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46)
                .addComponent(search_box_3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ogretmenBilgileri1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton47)
                    .addComponent(jButton46)
                    .addComponent(jButton48))
                .addContainerGap(128, Short.MAX_VALUE))
        );

        calisabildigiSaatler.setBackground(new java.awt.Color(255, 255, 255));
        calisabildigiSaatler.setPreferredSize(new java.awt.Dimension(490, 380));

        jScrollPane14.setViewportView(l_calisabildigi_saatler);

        javax.swing.GroupLayout calisabildigiSaatlerLayout = new javax.swing.GroupLayout(calisabildigiSaatler);
        calisabildigiSaatler.setLayout(calisabildigiSaatlerLayout);
        calisabildigiSaatlerLayout.setHorizontalGroup(
            calisabildigiSaatlerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calisabildigiSaatlerLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );
        calisabildigiSaatlerLayout.setVerticalGroup(
            calisabildigiSaatlerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calisabildigiSaatlerLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        verebildigiDersler.setBackground(new java.awt.Color(255, 255, 255));
        verebildigiDersler.setPreferredSize(new java.awt.Dimension(490, 380));

        t_verebildigi_dersler.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane15.setViewportView(t_verebildigi_dersler);

        javax.swing.GroupLayout verebildigiDerslerLayout = new javax.swing.GroupLayout(verebildigiDersler);
        verebildigiDersler.setLayout(verebildigiDerslerLayout);
        verebildigiDerslerLayout.setHorizontalGroup(
            verebildigiDerslerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(verebildigiDerslerLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        verebildigiDerslerLayout.setVerticalGroup(
            verebildigiDerslerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(verebildigiDerslerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout OGRETMEN_BILGILERINI_GORUNTULELayout = new javax.swing.GroupLayout(OGRETMEN_BILGILERINI_GORUNTULE);
        OGRETMEN_BILGILERINI_GORUNTULE.setLayout(OGRETMEN_BILGILERINI_GORUNTULELayout);
        OGRETMEN_BILGILERINI_GORUNTULELayout.setHorizontalGroup(
            OGRETMEN_BILGILERINI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 929, Short.MAX_VALUE)
            .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(ogretmenBilgileri1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createSequentialGroup()
                    .addGap(219, 219, 219)
                    .addComponent(calisabildigiSaatler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(220, Short.MAX_VALUE)))
            .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createSequentialGroup()
                    .addGap(219, 219, 219)
                    .addComponent(verebildigiDersler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(220, Short.MAX_VALUE)))
        );
        OGRETMEN_BILGILERINI_GORUNTULELayout.setVerticalGroup(
            OGRETMEN_BILGILERINI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 742, Short.MAX_VALUE)
            .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(ogretmenBilgileri1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createSequentialGroup()
                    .addGap(181, 181, 181)
                    .addComponent(calisabildigiSaatler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(181, Short.MAX_VALUE)))
            .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(OGRETMEN_BILGILERINI_GORUNTULELayout.createSequentialGroup()
                    .addGap(181, 181, 181)
                    .addComponent(verebildigiDersler, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(181, Short.MAX_VALUE)))
        );

        KURS_BILGILERI_GORUNTULE.setBackground(new java.awt.Color(255, 255, 255));
        KURS_BILGILERI_GORUNTULE.setPreferredSize(new java.awt.Dimension(929, 742));

        jLabel42.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 102, 0));
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("Kurs Bilgileri Görüntüleme Ekranı");

        t_kurs_bilgileri.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        t_kurs_bilgileri.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane13.setViewportView(t_kurs_bilgileri);

        jButton51.setBackground(new java.awt.Color(0, 51, 255));
        jButton51.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton51.setForeground(new java.awt.Color(255, 255, 255));
        jButton51.setText("Seçilen Kursun Ücretini Güncelle");
        jButton51.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton51ActionPerformed(evt);
            }
        });

        jButton52.setBackground(new java.awt.Color(204, 0, 0));
        jButton52.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jButton52.setForeground(new java.awt.Color(255, 255, 255));
        jButton52.setText("Geri");
        jButton52.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton52ActionPerformed(evt);
            }
        });

        search_box_4.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        search_box_4.setText("Kurs adı ile ara...");

        javax.swing.GroupLayout KURS_BILGILERI_GORUNTULELayout = new javax.swing.GroupLayout(KURS_BILGILERI_GORUNTULE);
        KURS_BILGILERI_GORUNTULE.setLayout(KURS_BILGILERI_GORUNTULELayout);
        KURS_BILGILERI_GORUNTULELayout.setHorizontalGroup(
            KURS_BILGILERI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(KURS_BILGILERI_GORUNTULELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(KURS_BILGILERI_GORUNTULELayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(KURS_BILGILERI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(KURS_BILGILERI_GORUNTULELayout.createSequentialGroup()
                        .addComponent(jButton51)
                        .addGap(36, 36, 36)
                        .addComponent(jButton52, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 844, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(search_box_4, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        KURS_BILGILERI_GORUNTULELayout.setVerticalGroup(
            KURS_BILGILERI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(KURS_BILGILERI_GORUNTULELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(search_box_4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(KURS_BILGILERI_GORUNTULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton51)
                    .addComponent(jButton52))
                .addContainerGap(127, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(anaEkran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(812, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(751, 751, 751)
                    .addComponent(dersKaydiEkrani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(ogretmenKaydiEkrani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(dersEkleEkrani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(KURS_HAZIRLAMA_EKRANI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(KURS_SATIS_EKRANI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(KURSIYER_BILGILERINI_GORUNTULEME_EKRANI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(OGRETMEN_BILGILERINI_GORUNTULE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(KURS_BILGILERI_GORUNTULE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(anaEkran, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(523, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(447, 447, 447)
                    .addComponent(dersKaydiEkrani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(ogretmenKaydiEkrani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(dersEkleEkrani, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(KURS_HAZIRLAMA_EKRANI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(KURS_SATIS_EKRANI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(KURSIYER_BILGILERINI_GORUNTULEME_EKRANI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(OGRETMEN_BILGILERINI_GORUNTULE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(KURS_BILGILERI_GORUNTULE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void gunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gunActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_gunActionPerformed

    private void kapasiteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kapasiteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kapasiteActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String[] options = new String[2];
        options[0] = new String("Evet");
        options[1] = new String("Hayır");
        int output = JOptionPane.showOptionDialog(this.frame.getContentPane(),"Dersi eklemek istediğinize emin misiniz?","Ders Ekleme Onayı", 0,JOptionPane.QUESTION_MESSAGE,null,options,null);
        
        
        //Ders ekleme islemi onaylandi ise
        if (output == 0) {
            
            //Input kisimlari dogru girildi mi?
            if (Integer.parseInt(kapasite.getText()) < 1) {
                JOptionPane.showMessageDialog(this.frame.getContentPane(),"Kapasite sıfırdan büyük olmalıdır!","Hata",JOptionPane.ERROR_MESSAGE);
            }
            
            
            //Sorun yok ise
            else{                           
                            
                Ders ders = Ders.getInstance();
                ders.setDersAdi(dersAdi.getSelectedItem().toString());
                ders.setDersGunu(gun.getSelectedItem().toString());
                ders.setDersSaati(saat.getSelectedItem().toString());
                ders.setDersKapasitesi(Integer.parseInt(kapasite.getText()));
                ders.setDersSinifi(sinif.getSelectedItem().toString());
                
                
                ders.writeDB();
                
            }       

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
    
        this.switchPanel(anaEkran);
        
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void dersAdi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dersAdi2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dersAdi2ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        String[] options = new String[2];
        options[0] = new String("Evet");
        options[1] = new String("Hayır");
        int output = JOptionPane.showOptionDialog(this.frame.getContentPane(),"Dersi eklemek istediğinize emin misiniz?","Ders Ekleme Onayı", 0,JOptionPane.QUESTION_MESSAGE,null,options,null);
        
        
        //Ders ekleme islemi onaylandi ise
        if (output == 0) {
            
            //Input kisimlari dogru girildi mi?
            if (dersAdi2.getText().compareTo("") == 0) {
                JOptionPane.showMessageDialog(this.frame.getContentPane(),"Ders adı boş bırakılamaz!","Hata",JOptionPane.ERROR_MESSAGE);
            }
            
            else {
                this.addDersBilgisiVT(dersAdi2.getText().toString());
            }
        }    
    }//GEN-LAST:event_jButton13ActionPerformed

    private void adresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adresActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_adresActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        //Cesitli input kontrolleri yapiliyor
        if (ogretmen_adi.getText().compareTo("") == 0) {

            JOptionPane.showMessageDialog(this.frame.getContentPane(),"Öğretmen adı boş bırakılamaz!","Hata",JOptionPane.ERROR_MESSAGE);
        }

        else if (ogretmen_soyadi.getText().compareTo("") == 0) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(),"Öğretmen soyadı boş bırakılamaz!","Hata",JOptionPane.ERROR_MESSAGE);
        }

        else if (cep_tel.getText().compareTo("") == 0) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(),"Cep telefonu boş bırakılamaz!","Hata",JOptionPane.ERROR_MESSAGE);
        }

        else if (email.getText().compareTo("") == 0) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(),"E-mail boş bırakılamaz!","Hata",JOptionPane.ERROR_MESSAGE);
        }

        else if (this.dl.size() == 0) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(),"Lütfen öğretmenin çalışabildiği saat ekleyiniz!","Hata",JOptionPane.ERROR_MESSAGE);
        }

        else if (this.table_model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(),"Lütfen öğretmenin verebildiği ders ekleyiniz!","Hata",JOptionPane.ERROR_MESSAGE);
        }
        
        else {

            Ogretmen ogretmen = Ogretmen.getInstance();
            ogretmen.setAd(ogretmen_adi.getText());
            ogretmen.setSoyad(ogretmen_soyadi.getText());
            ogretmen.setEvTel(ev_tel.getText());
            ogretmen.setCepTel(cep_tel.getText());
            ogretmen.setEmail(email.getText());
            ogretmen.setAdres(adres.getText());

            ArrayList<String> saatler = new ArrayList<String>();
            HashMap<String, Integer> derslerBedeller = new HashMap<String, Integer>();

            for (int i = 0; i < table_model.getRowCount(); i++) {
                derslerBedeller.put(table_model.getValueAt(i, 0).toString(), new Integer(Integer.parseInt(table_model.getValueAt(i, 1).toString())));

            }

            //Calisabilgi saatler nesneye ekleniyor
            for (int i = 0; i < this.dl.size(); i++) {
                saatler.add(dl.getElementAt(i));
            }

            ogretmen.setDerslerBedeller(derslerBedeller);
            ogretmen.setCalisabildigiSaatler(saatler.toArray(new String[0]));

            //Kullanıcı onayı
            String[] options = new String[2];
            options[0] = new String("Evet");
            options[1] = new String("Hayır");
            int output = JOptionPane.showOptionDialog(this.frame.getContentPane(), "Öğretmen kaydını eklemek istediğinize emin misiniz?", "Öğretmen Ekleme Onayı", 0, JOptionPane.QUESTION_MESSAGE, null, options, null);

            if (output == 0) {
                ogretmen.writeDB();
            }

        }

 

        

    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed

        String[] saatler = { "9.00", "10.00", "11.00", "12.00", "13.00", "14.00", "15.00", "16.00", "17.00", "18.00",};

        String saat = (String) JOptionPane.showInputDialog(this.frame,
            "Saat seçiniz.",
            "Saat",
            JOptionPane.QUESTION_MESSAGE,
            null,
            saatler,
            saatler[0]);

        /* Secim listesine eleman ekleme */
        boolean flag = false;

        //Eklenen eleman listede var mi?
        for (int i = 0; i<this.dl.getSize();i++) {
            if (dl.getElementAt(i).compareTo(saat) == 0) {
                flag = true;

            }

        }
        //Listede varsa mesaj basiliyor
        if (flag) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(),"Seçilen saat listede bulunmaktadır!","Hata",JOptionPane.ERROR_MESSAGE);
        }

        //Listede yoksa ekleniyor
        else {
            this.dl.addElement(saat);
            l_calisabildigiSaatler.setModel(dl);

        }

    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        //this.table_model.addRow(new Object[]{"deneme", "deneme"});

        /* Oncelikle ders adi soruluyor*/

        //Veri tabanindan acilmis dersler cekiliyor
        ArrayList<String> dersAdlari = MainProgram.getDersBilgileri();
        String dersAdlari2[] = dersAdlari.toArray(new String[dersAdlari.size()]);

        String ders = (String) JOptionPane.showInputDialog(this.frame,
            "Ders seçiniz",
            "Ders",
            JOptionPane.QUESTION_MESSAGE,
            null,
            dersAdlari2,
            dersAdlari2[0]);

        //Ders seçildiyse devam ediliyor

        if (ders != null) {

            boolean flag = false;

            for (int i = 0; i<table_model.getRowCount();i++) {
                if (table_model.getValueAt(i, 0).toString().compareTo(ders) == 0) {

                    flag = true;

                }

            }

            if (flag) {

                JOptionPane.showMessageDialog(this.frame.getContentPane(),"Seçilen ders zaten eklenmiş!","Hata",JOptionPane.ERROR_MESSAGE);

            }

            else {

                String bedel = JOptionPane.showInputDialog(
                    frame,
                    "Ders için talep edilen bedeli giriniz.",
                    "Bedel",
                    JOptionPane.QUESTION_MESSAGE);

                //Kullanıcı bir şey girmezse
                if (bedel.compareTo("") == 0) {

                    JOptionPane.showMessageDialog(this.frame.getContentPane(),"Ders bedeli boş bırakılamaz!","Hata",JOptionPane.ERROR_MESSAGE);

                }

                //Tum kontrollerden gecti tabloya ekleniyor
                else {

                    table_model.addRow(new Object[]{ders, bedel});

                }

            }

        }

        t_verdigi_dersler.setModel(table_model);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailActionPerformed

    private void ev_telActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ev_telActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ev_telActionPerformed

    private void cep_telActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cep_telActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cep_telActionPerformed

    private void ogretmen_adiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ogretmen_adiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ogretmen_adiActionPerformed

    private void ogretmen_soyadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ogretmen_soyadiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ogretmen_soyadiActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void b_ders_getirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b_ders_getirActionPerformed
        /*Once eklenen ve eklenebilecek ders tabloları temizleniyor*/
        
        //Remove rows one by one from the end of the table (Eklenebilecek dersler)
        for (int i = tm_eklenebilecek_dersler.getRowCount() - 1; i >= 0; i--) {
            tm_eklenebilecek_dersler.removeRow(i);
        }
        
        //Remove rows one by one from the end of the table (Eklenen dersler)
        for (int i = tm_eklenen_dersler.getRowCount() - 1; i >= 0; i--) {
            tm_eklenen_dersler.removeRow(i);
        }
        
        t_eklenebilecek_dersler.setModel(tm_eklenebilecek_dersler);
        t_eklenen_dersler.setModel(tm_eklenen_dersler);
        eklenenDersler.clear();
        jButton17.setEnabled(false);
        jButton16.setEnabled(false);
        jButton18.setEnabled(false);
        
        
        /* Seçilen kurs tipine göre eklenebilecek dersleri lsteleme islemi*/
        
        
        String kurs_tipi = s_kurs_tipi.getSelectedItem().toString();
        
        
        
        Set<String> haftaciSet = new HashSet<String>(Arrays.asList("Pazartesi","Salı","Çarşamba","Perşembe","Cuma"));
        Set<String> haftasonuSet = new HashSet<String>(Arrays.asList("Cumartesi","Pazar"));
        Set<String> searchSet;
        
        
        if (kurs_tipi.compareTo("Hafta İçi") == 0) {
            
            searchSet = haftaciSet;
            
        }
        
        else {
            
            searchSet = haftasonuSet;
        }
        
        
        
        //Oncelikle vtden tum dersler cekiliyor(bir ogretmene atanmamis dersler)
        
        String sql = "SELECT * FROM Ders WHERE ogretmen_id IS NULL";
        Connection conn = MainProgram.getDatabaseConnection();
        ResultSet rs;
        boolean flag = false;

        try {
            Statement stmt = conn.createStatement();

            rs = stmt.executeQuery(sql);

            //Vtden gelen derslerden uygun turde olanlari secilip tabloya ekleniyor(hafta ici veya hafta sonu)
            while (rs.next()) {
                
                //Eger secilen tip ile uyusan bir ders ise tabloda gosteriliyor
                if (searchSet.contains(rs.getString("gun")) ) {
                    tm_eklenebilecek_dersler.addRow( new Object[]{rs.getInt("id"), rs.getString("ders_adi"), rs.getString("gun"), rs.getString("saat"), rs.getInt("kapasite")} );
                    flag=true;
                }
                

            }

            conn.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        
        
        //Uygun turde ders tabloya eklenebildiyse ders ekle butonu aktif ediliyor
        if (flag) {
            jButton17.setEnabled(true);
        }
        
        
        
    }//GEN-LAST:event_b_ders_getirActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        

        /* Eklenen dersler tek tek Ders nesnesi icinde bir listede saklaniyor*/
        int dersSayisi = t_eklenen_dersler.getRowCount();
        eklenenDersler.clear();
        
        
        Ders ders;
        
        for (int i = 0; i < dersSayisi; i++) {
            ders = new Ders();
            
            ders.setId(Integer.parseInt(tm_eklenen_dersler.getValueAt(i, 0).toString()));
            ders.setDersAdi(tm_eklenen_dersler.getValueAt(i, 1).toString());
            ders.setDersGunu(tm_eklenen_dersler.getValueAt(i, 2).toString());
            ders.setDersSaati(tm_eklenen_dersler.getValueAt(i, 3).toString());
            ders.setDersKapasitesi(Integer.parseInt(tm_eklenen_dersler.getValueAt(i, 4).toString()));
            
            eklenenDersler.add(ders);
        }
        
        
        /*Ogretmen atama tablosu onceden doldurulmasina istinaden bosaltiliyor*/
        
        //Remove rows one by one from the end of the table
        int rowCount = t_ogretmen_atama.getRowCount();
        
        for (int i = rowCount - 1; i >= 0; i--) {
            tm_ogretmen_atama.removeRow(i);
        }
        
        
        //Eklenen dersler basiliyor
        for (Ders eklenen_ders : eklenenDersler) {
            tm_ogretmen_atama.addRow(new Object[]{eklenen_ders.getDersAdi(), eklenen_ders.getDersSaati(), eklenen_ders.getDersKapasitesi()});
        }
        
        t_ogretmen_atama.setModel(tm_ogretmen_atama);

        this.switchPanel(kursHazirlamaEkrani2);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        this.switchPanel(kursHazirlamaEkrani);
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        
        /*Eklenen derslerin seçilen öğretmenlere göre bedelleri toplanıp maliyet hesaplanyor*/
        
        maliyet = 0;
        for (int i = 0; i < tm_ogretmen_atama.getRowCount(); i++) {
            maliyet += (int)tm_ogretmen_atama.getValueAt(i, 6);
        }
        
        
        
        jLabel26.setText(Integer.toString(maliyet) + " TL");
        
        kurs_adi.setText("");
                
        this.switchPanel(kursHazirlamaEkrani3);
    }//GEN-LAST:event_jButton22ActionPerformed

    private void sinifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sinifActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sinifActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        this.switchPanel(kursHazirlamaEkrani2);
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        
        
        if (kurs_adi.getText().toString().compareTo("") == 0) {
            
            JOptionPane.showMessageDialog(this.frame.getContentPane(),"Kurs adı boş bırakılamaz!","Hata",JOptionPane.ERROR_MESSAGE);
            
        }
        
        else if(kurs_ucreti.getText().toString().compareTo("") == 0) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(),"Kurs ücreti boş bırakılamaz!","Hata",JOptionPane.ERROR_MESSAGE);
        }
        
        //Tarih formatı düzgün bir şekilde girildi mi?
        else if( !Pattern.matches("^2[0-9][0-9][0-9]\\-((0[1-9])|(1[0-2]))\\-((0[1-9])|(1[0-9])|(2[0-9])|(3[0-1]))$", kurs_tarihi.getText().toString()) ) {
             JOptionPane.showMessageDialog(this.frame.getContentPane(),"Lütfen tarihi formatı düzgün giriniz!","Hata",JOptionPane.ERROR_MESSAGE);
        }
        
        else {

            String[] options = new String[2];
            options[0] = new String("Evet");
            options[1] = new String("Hayır");
            int output = JOptionPane.showOptionDialog(this.frame.getContentPane(), "Kursu sisteme kayıt etmek istediğinize emin misiniz?", "Kurs Ekleme Onayı", 0, JOptionPane.QUESTION_MESSAGE, null, options, null);

            if (output == 0) {
                
                
                //Derslere secilen ogretmenler ataniyor
                for (int i = 0; i < tm_ogretmen_atama.getRowCount(); i++) {  
                    eklenenDersler.get(i).ogretmenAta( Integer.parseInt(tm_ogretmen_atama.getValueAt(i, 2).toString()) );
                }
                
                
                

                
                //Kurs aciliyor
                Kurs kurs = new Kurs();

                kurs.setAd(kurs_adi.getText().toString());
                kurs.setDersler(eklenenDersler);
                kurs.setTip(s_kurs_tipi.getSelectedItem().toString());
                kurs.setUcret(Integer.parseInt(kurs_ucreti.getText().toString()));
                
                //Kursun baslayagi tarih

                kurs.setTarih(kurs_tarihi.getText().toString());
                
                //Kursun kapasitesi, eklenen derslerden kapasitesi en az olan dersin kapasitesine eşit olacaktır
                int min = eklenenDersler.get(0).getDersKapasitesi();
                
                for (Ders ders : eklenenDersler) {
                    
                    if (ders.getDersKapasitesi() < min) {
                        min = ders.getDersKapasitesi();
                    }
                }
                
                kurs.setKapasite(min);
                
                //Doluluk başlangıçta 0 olacaktır
                kurs.setDoluluk(0);
                

                boolean response = kurs.writeDB();
                
                if (response) {
                    JOptionPane.showMessageDialog(this.frame.getContentPane(),"Kurs sisteme başarılı bir şekilde kayıt edildi!","Başarı",JOptionPane.INFORMATION_MESSAGE);
                }
                
                

            }

        }
        
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        
        int selectedRowIndex = t_eklenebilecek_dersler.getSelectedRow();
        
        if (selectedRowIndex != -1) {
            
            String eklenecekGun = tm_eklenebilecek_dersler.getValueAt(selectedRowIndex, 2).toString();
            String eklenecekSaat = tm_eklenebilecek_dersler.getValueAt(selectedRowIndex, 3).toString();
            
            String eklenmisGun;
            String eklenmisSaat;
            
            boolean flag = false;
            
            //Eklenilecek dersin gun ve saatinde baska ders eklenmis mi?
            for (int i = 0; i < t_eklenen_dersler.getRowCount(); i++) {
                eklenmisGun = tm_eklenen_dersler.getValueAt(i, 2).toString();
                eklenmisSaat = tm_eklenen_dersler.getValueAt(i, 3).toString();
                
                if ( (eklenmisGun.compareTo(eklenecekGun) == 0) && (eklenmisSaat.compareTo(eklenecekSaat) == 0) ) {
                    flag = true;           
                }
                
            }
            
            if (flag) {
                JOptionPane.showMessageDialog(this.frame.getContentPane(),"Eklenmeye çalışılan dersin gün ve saatinde bir ders eklenen derslerde mevcuttur!","Hata",JOptionPane.ERROR_MESSAGE);
            }
            
            else {
                //Secilen ders bir asagidaki eklenen dersler tablosuna aliniyor
                tm_eklenen_dersler.addRow(new Object[]{tm_eklenebilecek_dersler.getValueAt(selectedRowIndex, 0), tm_eklenebilecek_dersler.getValueAt(selectedRowIndex, 1), tm_eklenebilecek_dersler.getValueAt(selectedRowIndex, 2), tm_eklenebilecek_dersler.getValueAt(selectedRowIndex, 3), tm_eklenebilecek_dersler.getValueAt(selectedRowIndex, 4)});

                //Ders sil ve Devam Et butonu aktif ediliyor
                jButton18.setEnabled(true);
                jButton16.setEnabled(true);
            }

        }

    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        
        int selectedRowIndex = t_eklenen_dersler.getSelectedRow();
        
        //Secilen satır varsa
        if (selectedRowIndex != -1) {
            tm_eklenen_dersler.removeRow(selectedRowIndex);
            
            //Eklenen dersler tablosu bosaldi mi? Bosaldiysa Devam Et ve Ders Sil butonları disable edilir
            if(t_eklenen_dersler.getRowCount() == 0) {
                jButton18.setEnabled(false);
                jButton16.setEnabled(false);
            }
            
        }
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed

        /* Tabloda secilen dersin nesnesine ulasilir*/
        int secilenSatir = t_ogretmen_atama.getSelectedRow();

        if (secilenSatir != -1) {

            Ders ders = eklenenDersler.get(secilenSatir);

            /* Veri tabanindan dersi vermeye her acidan musait ogretmenler cekilir*/
            String sql = "SELECT id,ad,soyad,bedel FROM Ogretmen,Calisabildigi_Saatler,Verebildigi_Dersler WHERE id=Calisabildigi_Saatler.ogretmen_id AND "
                    + "id=Verebildigi_Dersler.ogretmen_id AND ders_adi=? AND saat=?";
            Connection conn = MainProgram.getDatabaseConnection();

            ArrayList<Ogretmen> ogretmenler = new ArrayList<Ogretmen>(); //Bu liste ogr ad soyadiyla birlikte idyi de tutuyor
            ArrayList<String> ogretmenAdSoyadlari = new ArrayList<String>(); //Bu liste kullanıcıya ogretmen seciminde ogr ad soyadlarini gostermek icin
            ArrayList<Integer> dersBedelleri = new ArrayList<Integer>();

            try {

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, ders.getDersAdi());
                pstmt.setString(2, ders.getDersSaati());

                ResultSet rs = pstmt.executeQuery();
                Ogretmen ogretmen;

                //Veri tabanindan gelen her satir sirayla bir Ogretmen nesnesi icinde saklanip listeye ekleniyor
                while (rs.next()) {
                    ogretmen = new Ogretmen();

                    String ogrAd = rs.getString("ad");
                    String ogrSoyad = rs.getString("soyad");

                    ogretmen.setAd(ogrAd);
                    ogretmen.setSoyad(ogrSoyad);
                    ogretmen.setId(rs.getInt("id"));

                    ogretmenler.add(ogretmen);
                    ogretmenAdSoyadlari.add(ogrAd + " " + ogrSoyad);
                    dersBedelleri.add(new Integer(rs.getInt("bedel")));

                }

                conn.close();

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            //Dersi verebilecek ogretmen varsa
            if (ogretmenAdSoyadlari.size() != 0) {

                Object ogretmenAdSoyadlari2[] = ogretmenAdSoyadlari.toArray();

                Object secilenOgr = JOptionPane.showInputDialog(this.frame,
                        "Öğretmen seçiniz.",
                        "Öğretmen",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        ogretmenAdSoyadlari2,
                        ogretmenAdSoyadlari2[0]);

                //Listeden secilen ogretmenin indexi aliniyor. Bu index Ogretmen nesnelerinin tutuldugu ArrayList uzerinde kullanilacak.
                int index = 0;
                if (secilenOgr != null) {

                    for (Object o : ogretmenAdSoyadlari2) {
                        if (secilenOgr == o) {
                            break;
                        }
                        index++;
                    }

                    //Tabloda satir, eklenen ogretmen ile guncelleniyor
                    tm_ogretmen_atama.setValueAt(ogretmenler.get(index).getId(), secilenSatir, 3);
                    tm_ogretmen_atama.setValueAt(ogretmenler.get(index).getAd(), secilenSatir, 4);
                    tm_ogretmen_atama.setValueAt(ogretmenler.get(index).getSoyad(), secilenSatir, 5);
                    tm_ogretmen_atama.setValueAt(dersBedelleri.get(index).intValue(), secilenSatir, 6);

                    boolean flag = false;

                    //Tum derslere ogretmen atandiysa Devam Et butonu enable ediliyor
                    for (int i = 0; i < tm_ogretmen_atama.getRowCount(); i++) {
                        if (tm_ogretmen_atama.getValueAt(i, 2) == null) {
                            flag = true;
                        }
                    }

                    jButton22.setEnabled(!flag);
                }
            } else {

                JOptionPane.showMessageDialog(this.frame.getContentPane(), "Dersi verebilecek öğretmen bulunmamaktadır!", "Hata", JOptionPane.ERROR_MESSAGE);

            }

        }


    }//GEN-LAST:event_jButton21ActionPerformed

    private void kurs_ucretiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kurs_ucretiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kurs_ucretiActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton25ActionPerformed
    
    private void satisEkrani1(){
        //Secilebilecek kurs tablosu onceden doldurulduysa temizleniyor
        int rowCount = tm_secilebilecek_kurslar.getRowCount();
        
        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--) {
            tm_secilebilecek_kurslar.removeRow(i);
        }

        t_secilebilecek_kurslar.setModel(tm_secilebilecek_kurslar);
        
        Controller ct = new Controller();
        String mm = max_miktar.getText().toString();
        String message = ct.checkFields(lm_yer_alacak_dersler, mm);
        
        if(!message.isEmpty()){
            JOptionPane.showMessageDialog(this.frame.getContentPane(), message, "Hata", JOptionPane.ERROR_MESSAGE);
        }
        
        //Sorun yok devam ediliyor
        else {
            
            ArrayList<String> yerAlacakDersler = new ArrayList<>();
            
            //Bir onceki ekranda eklenen dersler String listesine aliniyor
            for (int i = 0; i < lm_yer_alacak_dersler.getSize(); i++) {
                yerAlacakDersler.add( lm_yer_alacak_dersler.getElementAt(i) );
            }
            
            //Uygun kurslar alinir
            ArrayList<Kurs> uygunKurslar = ct.getCourses(yerAlacakDersler, kurs_zamani_c, max_miktar);
            
            //Kurslar tabloda gosterilir
            for (Kurs kurs : uygunKurslar) {
                //{"Kurs Adı","Kurs Tipi","Tarih", "Ücret (TL)"}

                //Eger tarihi geçmemiş ise
                if (LocalDate.parse(kurs.getTarih()).compareTo(LocalDate.now()) > 0) {
                    tm_secilebilecek_kurslar.addRow(new Object[]{kurs.getAd(), kurs.getTip(), kurs.getTarih(), kurs.getKapasite(), kurs.getDoluluk(),kurs.getUcret()});
                }
            }
            this.switchPanel(kursSatisEkrani2_1);
        }
    }
    
    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton29ActionPerformed
        this.satisEkrani1();
    }//GEN-LAST:event_jButton29ActionPerformed

    private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton28ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton28ActionPerformed

    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton31ActionPerformed
        
        //Oncelikle tablo temizlenir
        int rowCount = tm_icerdigi_dersler.getRowCount();

        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--) {
            tm_icerdigi_dersler.removeRow(i);
        }
        
        //Secilen satırdaki kursun içerdigi dersler çekiliyor
        int rowIndex = t_secilebilecek_kurslar.getSelectedRow();
        System.out.println(rowIndex);
        if (rowIndex != -1) {

            String kursAdi = tm_secilebilecek_kurslar.getValueAt(rowIndex, 0).toString();

            Controller ct = new Controller();
            ArrayList<Ders> dersler = ct.getClasses(kursAdi);

            for (Ders ders : dersler) {
                //{"Ders Id", "Ders Adı","Ders Günü","Ders Saati","Ders Sınıfı"}
                tm_icerdigi_dersler.addRow(new Object[]{ders.getId(), ders.getDersAdi(), ders.getDersGunu(),ders.getDersSaati(), ders.getDersSinifi()});
            }

            t_icerdigi_dersler.setModel(tm_icerdigi_dersler);
            JOptionPane.showMessageDialog(frame, kursDersleriListele, "Kursta Bulunan Dersler", JOptionPane.PLAIN_MESSAGE);

        }

    }//GEN-LAST:event_jButton31ActionPerformed

    //Ders Ekle butonu
    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        this.switchPanel(dersEkleEkrani);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        
        //Oncelikle ogretmen tablosu temizleniyor
        for (int i = tm_ogretmen_bilgileri.getRowCount() - 1; i >= 0; i--) {
            tm_ogretmen_bilgileri.removeRow(i);
        }

        this.Tablesorter = new TableRowSorter<DefaultTableModel>(tm_ogretmen_bilgileri);
        t_ogretmen_bilgileri.setRowSorter(Tablesorter);
        t_ogretmen_bilgileri.setModel(tm_ogretmen_bilgileri);

        //Search box filtering
        search_box_3.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        newFilter(search_box_3);
                    }

                    public void insertUpdate(DocumentEvent e) {
                        newFilter(search_box_3);
                    }

                    public void removeUpdate(DocumentEvent e) {
                        newFilter(search_box_3);
                    }
                });

        //Tum kursiyerler cekiliyor
        ArrayList<Ogretmen> ogretmenler = Ogretmen.tumOgretmenleriGetir();

        //Tabloda gosteriliyor
        for (Ogretmen ogretmen : ogretmenler) {
            tm_ogretmen_bilgileri.addRow(new Object[]{ogretmen.getId(), ogretmen.getAd(), ogretmen.getSoyad(), ogretmen.getEvTel(), ogretmen.getCepTel(), ogretmen.getAdres(), ogretmen.getEmail()});
        }

        this.switchPanel(ogretmenBilgileri1);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        
        //Oncelikle kursiyer tablosu temizleniyor
        for (int i = tm_kursiyer_bilgileri.getRowCount() - 1; i >= 0; i--) {
            tm_kursiyer_bilgileri.removeRow(i);
        }

        this.Tablesorter = new TableRowSorter<DefaultTableModel>(tm_kursiyer_bilgileri);
        t_kursiyer_bilgileri.setRowSorter(Tablesorter);
        t_kursiyer_bilgileri.setModel(tm_kursiyer_bilgileri);

        //Search box filtering
        search_box_2.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        newFilter(search_box_2);
                    }

                    public void insertUpdate(DocumentEvent e) {
                        newFilter(search_box_2);
                    }

                    public void removeUpdate(DocumentEvent e) {
                        newFilter(search_box_2);
                    }
                });

        
        //Tum kursiyerler cekiliyor
        ArrayList<Kursiyer> kursiyerler = Kursiyer.tumKursiyerleriGetir();
        
        //Tabloda gosteriliyor
        for (Kursiyer kursiyer : kursiyerler) {
            tm_kursiyer_bilgileri.addRow(new Object[]{kursiyer.getId(),kursiyer.getAd(), kursiyer.getSoyad(), kursiyer.getEvTel(),kursiyer.getCepTel(),kursiyer.getAdres(), kursiyer.getEmail()});
        }
        
        this.switchPanel(kursiyerBilgileri1);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        lm_yer_alacak_dersler.clear();
        l_yer_alacak_dersler.setModel(lm_yer_alacak_dersler);
        this.switchPanel(kursSatisEkrani);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        /* Verilebilecek dersler temizleniyor */

        int rowCount = tm_eklenebilecek_dersler.getRowCount();

        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--) {
            tm_eklenebilecek_dersler.removeRow(i);
        }

        /*Verilen dersler temizleniyor */
        rowCount = tm_eklenen_dersler.getRowCount();

        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--) {
            tm_eklenen_dersler.removeRow(i);
        }

        t_eklenebilecek_dersler.setModel(tm_eklenebilecek_dersler);
        t_eklenen_dersler.setModel(tm_eklenen_dersler);
        t_ogretmen_atama.setModel(tm_ogretmen_atama);
        eklenenDersler.clear();

        //Butonlar disable ediliyor
        jButton17.setEnabled(false);
        jButton18.setEnabled(false);
        jButton16.setEnabled(false);
        jButton22.setEnabled(false);

        this.switchPanel(kursHazirlamaEkrani);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        //Veri tabanindan ders adlari cekiliyor

        this.dersAdi.removeAllItems();
        ArrayList<String> dersAdlari = MainProgram.getDersBilgileri();
        for (String ders : dersAdlari) {
            this.dersAdi.addItem(ders);
        }

        this.switchPanel(dersKaydiEkrani);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //Saat listesi temizleniyor
        dl.removeAllElements();

        //Verdigi dersler tablosu temizleniyor
        int rowCount = table_model.getRowCount();

        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--) {
            table_model.removeRow(i);
        }

        //Fieldlar temizleniyor
        this.ogretmen_adi.setText("");
        this.ogretmen_soyadi.setText("");
        this.ev_tel.setText("");
        this.cep_tel.setText("");
        this.email.setText("");
        this.adres.setText("");

        this.t_verdigi_dersler.setModel(table_model);
        this.l_calisabildigiSaatler.setModel(dl);

        this.switchPanel(ogretmenKaydiEkrani);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton34ActionPerformed
        
        //Kursiyer tipi seçimi sıfırlanıyor
        kursiyer_tipi_c.setSelectedIndex(0);
        
        int selectedRow = t_secilebilecek_kurslar.getSelectedRow();
        if (selectedRow == -1 ) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(), "Lütfen bir kurs seçiniz!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
        else {
            kurs_adi_teyit.setText(tm_secilebilecek_kurslar.getValueAt(selectedRow, 0).toString());
            kurs_tarihi_teyit.setText(tm_secilebilecek_kurslar.getValueAt(selectedRow, 2).toString());
            kurs_ucreti_teyit.setText(tm_secilebilecek_kurslar.getValueAt(selectedRow, 5).toString());
            this.switchPanel(kursSatisEkrani3);
        }
        
    }//GEN-LAST:event_jButton34ActionPerformed

    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton35ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton35ActionPerformed

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        
        if (kursiyer_tipi_c.getSelectedItem().toString().compareTo("Seçiniz") == 0) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(), "Lütfen kursiyer tipi seçiniz!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
        
        else if (kursiyer_tipi_c.getSelectedItem().toString().compareTo("Yeni Kayıt") == 0) {
            
            //Odeme yontemi sifirlaniyor
            odeme_yontemi_c.setSelectedIndex(0);
            
            this.switchPanel(kursSatisEkrani4);
        }
        
        else {

            //Önceki doldurulan tablo temizleniyor
            //Remove rows one by one from the end of the table (Eklenebilecek dersler)
            for (int i = tm_kayitli_kursiyerler.getRowCount() - 1; i >= 0; i--) {
                tm_kayitli_kursiyerler.removeRow(i);
            }

            //Odeme yontemi sıfirlaniyor
            odeme_yontemi_2_c.setSelectedIndex(0);

            this.Tablesorter = new TableRowSorter<DefaultTableModel>(tm_kayitli_kursiyerler);             
            t_kayitli_kursiyerler.setRowSorter(Tablesorter);
            t_kayitli_kursiyerler.setModel(tm_kayitli_kursiyerler);
            
            //Search box filtering
            search_box.getDocument().addDocumentListener(
                    new DocumentListener() {
                        public void changedUpdate(DocumentEvent e) {
                            newFilter(search_box);
                        }

                        public void insertUpdate(DocumentEvent e) {
                            newFilter(search_box);
                        }

                        public void removeUpdate(DocumentEvent e) {
                            newFilter(search_box);
                        }
                    });
            
            //Kursiyerler listeleniyor
            ArrayList<Kursiyer> kursiyerler = Kursiyer.tumKursiyerleriGetir();
            
            for (Kursiyer kursiyer : kursiyerler) {
                //{"Id","Ad", "Soyad", "Email" }
                tm_kayitli_kursiyerler.addRow(new Object[]{kursiyer.getId(),kursiyer.getAd(),kursiyer.getSoyad(), kursiyer.getEmail()});
                
            }

            this.switchPanel(kursSatisEkrani4_2);
        }
        
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        this.switchPanel(kursSatisEkrani2_1);
    }//GEN-LAST:event_jButton37ActionPerformed

    private void adres1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adres1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_adres1ActionPerformed

    private void jButton38ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton38ActionPerformed
        //Cesitli input kontrolleri yapiliyor
        Controller ct = new Controller();
        
        
        String message = ct.checkTextField(kursiyer_adi, kursiyer_soyadi, cep_tel1, email1, odeme_yontemi_c);
        if(!message.isEmpty()){
            JOptionPane.showMessageDialog(this.frame.getContentPane(), message, "Hata", JOptionPane.ERROR_MESSAGE);
        } else {
            Kursiyer kursiyer = new Kursiyer();
            ArrayList<Satis> satinAlim = new ArrayList<>();
            
            kursiyer.setAd(kursiyer_adi.getText().toString());
            kursiyer.setSoyad(kursiyer_soyadi.getText().toString());
            kursiyer.setEvTel(ev_tel1.getText().toString());
            kursiyer.setCepTel(cep_tel1.getText().toString());
            kursiyer.setEmail(email1.getText().toString());
            kursiyer.setAdres(adres1.getText().toString());
            
            satinAlim.add(new Satis(kurs_adi_teyit.getText(), Integer.parseInt(kurs_ucreti_teyit.getText()), LocalDate.now().toString(), odeme_yontemi_c.getSelectedItem().toString()) );
            
            kursiyer.setSatinAlimlar(satinAlim);
            
            //Nesne veri tabanina yaziliyor
            boolean response = kursiyer.writeDB();
            
            if (response) {
                
                JOptionPane.showMessageDialog(this.frame.getContentPane(), "Kursiyer ve satış bilgileri başarıyla kaydedildi!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                
            }
            
            else {
                JOptionPane.showMessageDialog(this.frame.getContentPane(), "Bir hata oldu, lütfen tekrar deneyiniz!", "Hata", JOptionPane.ERROR_MESSAGE);
            }
            
        }
       
        
        
    }//GEN-LAST:event_jButton38ActionPerformed

    private void email1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_email1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_email1ActionPerformed

    private void ev_tel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ev_tel1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ev_tel1ActionPerformed

    private void cep_tel1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cep_tel1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cep_tel1ActionPerformed

    private void kursiyer_adiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kursiyer_adiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kursiyer_adiActionPerformed

    private void kursiyer_soyadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kursiyer_soyadiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kursiyer_soyadiActionPerformed

    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton30ActionPerformed
        this.switchPanel(kursSatisEkrani3);
    }//GEN-LAST:event_jButton30ActionPerformed

    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton32ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton32ActionPerformed

    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton39ActionPerformed

    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        
        if (t_kayitli_kursiyerler.getSelectedRow() == -1) {
            
            JOptionPane.showMessageDialog(this.frame.getContentPane(), "Lütfen kursiyer seçiniz!", "Hata", JOptionPane.ERROR_MESSAGE);  
        }
        
        else if (odeme_yontemi_2_c.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this.frame.getContentPane(), "Lütfen ödeme yöntemi seçiniz!", "Hata", JOptionPane.ERROR_MESSAGE); 
        }
        
        else {
            Kursiyer kursiyer = new Kursiyer();
            kursiyer.setId(Integer.parseInt(tm_kayitli_kursiyerler.getValueAt(t_kayitli_kursiyerler.getRowSorter().convertRowIndexToModel(t_kayitli_kursiyerler.getSelectedRow()), 0).toString()));
            
            
            Satis satis = new Satis(kurs_adi_teyit.getText(), Integer.parseInt(kurs_ucreti_teyit.getText()), LocalDate.now().toString(), odeme_yontemi_2_c.getSelectedItem().toString());
            
            boolean response = kursiyer.kursSatinAlimiKaydet(satis);
            
            if (response) {
                
                JOptionPane.showMessageDialog(this.frame.getContentPane(), "Kurs satış işlemi başarıyla tamamlandı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);                
                
            }
            
        }
    }//GEN-LAST:event_jButton40ActionPerformed

    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton33ActionPerformed
        this.switchPanel(kursSatisEkrani3);
    }//GEN-LAST:event_jButton33ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
              
        //Veri tabanindan acilmis dersler cekiliyor
        ArrayList<String> dersAdlari = MainProgram.getDersBilgileri();
        String dersAdlari2[] = dersAdlari.toArray(new String[dersAdlari.size()]);

        String ders = (String) JOptionPane.showInputDialog(this.frame,
                "Ders seçiniz",
                "Ders",
                JOptionPane.QUESTION_MESSAGE,
                null,
                dersAdlari2,
                dersAdlari2[0]);

        //Ders seçildiyse devam ediliyor
        if (ders != null) {

            boolean flag = false;

            //Secilen ders zaten eklenmis mi?
            for (int i = 0; i < lm_yer_alacak_dersler.size(); i++) {
                if (lm_yer_alacak_dersler.getElementAt(i).toString().compareTo(ders) == 0) {

                    flag = true;

                }

            }

            if (flag) {

                JOptionPane.showMessageDialog(this.frame.getContentPane(), "Seçilen ders zaten eklenmiş!", "Hata", JOptionPane.ERROR_MESSAGE);

            }
            
            else {
                
                lm_yer_alacak_dersler.addElement(ders);
                
            }

        }
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        
        int index = l_yer_alacak_dersler.getSelectedIndex();
        
        if (index != -1) {
            
            lm_yer_alacak_dersler.removeElementAt(index);
            
        }
    }//GEN-LAST:event_jButton26ActionPerformed

    private void kurs_tarihiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kurs_tarihiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kurs_tarihiActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        this.switchPanel(kursSatisEkrani);
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton42ActionPerformed

    private void jButton43ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton43ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton43ActionPerformed

    private void jButton44ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton44ActionPerformed
        
        int selectedRowIndex = t_kursiyer_bilgileri.getSelectedRow();
        
        if (selectedRowIndex != -1) {

            for (int i = tm_katildigi_kurslar.getRowCount() - 1; i >= 0; i--) {
                tm_katildigi_kurslar.removeRow(i);
            }
            
            
            
            Kursiyer kursiyer = new Kursiyer();
            kursiyer.setId(Integer.parseInt(tm_kursiyer_bilgileri.getValueAt(t_kursiyer_bilgileri.getRowSorter().convertRowIndexToModel(selectedRowIndex), 0).toString()));
            
            ArrayList<Satis> satinAlimlar = kursiyer.kursSatinAlimlariniGetir();
            
            //Satın alımlar tabloya yaziliyor
            for (Satis satinAlim : satinAlimlar) {
                tm_katildigi_kurslar.addRow(new Object[]{satinAlim.getKursAdı(), satinAlim.getOdenenMiktar(), satinAlim.getTarih(), satinAlim.getOdemeTipi()});
            }
            
            t_katildigi_kurslar.setModel(tm_katildigi_kurslar);
            JOptionPane.showMessageDialog(frame, kursiyerBilgileri2, "Kursiyerin Satın Aldığı Kurslar", JOptionPane.PLAIN_MESSAGE);
            
        }
    }//GEN-LAST:event_jButton44ActionPerformed

    private void jButton45ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton45ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton45ActionPerformed

    private void jButton46ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton46ActionPerformed
        int selectedRowIndex = t_ogretmen_bilgileri.getSelectedRow();

        if (selectedRowIndex != -1) {

            //Oncelikle liste temizlenir
            lm_calisabildigi_saatler.clear();

            Ogretmen ogretmen = new Ogretmen();
            ogretmen.setId(Integer.parseInt(tm_ogretmen_bilgileri.getValueAt(t_ogretmen_bilgileri.getRowSorter().convertRowIndexToModel(selectedRowIndex), 0).toString()));

            ArrayList<String> saatler = ogretmen.calisabildigiSaatleriGetir();

            //Satın alımlar tabloya yaziliyor
            for (String saat : saatler) {
                lm_calisabildigi_saatler.addElement(saat);
            }

            l_calisabildigi_saatler.setModel(lm_calisabildigi_saatler);
            JOptionPane.showMessageDialog(frame, calisabildigiSaatler, "Öğretmenin Çalışabildiği Saatler", JOptionPane.PLAIN_MESSAGE);

        }
    }//GEN-LAST:event_jButton46ActionPerformed

    private void jButton47ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton47ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton47ActionPerformed

    private void jButton48ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton48ActionPerformed
        int selectedRowIndex = t_ogretmen_bilgileri.getSelectedRow();

        if (selectedRowIndex != -1) {

            //Oncelikle tablo temizlenir
            for (int i = tm_verebildigi_dersler.getRowCount() - 1; i >= 0; i--) {
                tm_verebildigi_dersler.removeRow(i);
            }

            Ogretmen ogretmen = new Ogretmen();
            ogretmen.setId(Integer.parseInt(tm_ogretmen_bilgileri.getValueAt(t_ogretmen_bilgileri.getRowSorter().convertRowIndexToModel(selectedRowIndex), 0).toString()));

            HashMap<String, Integer> verebildigiDersler1 = ogretmen.verebildigiDersleriGetir();

            //Satın alımlar tabloya yaziliyor
            for (String ders : verebildigiDersler1.keySet()) {
                tm_verebildigi_dersler.addRow(new Object[]{ders,verebildigiDersler1.get(ders).intValue()});
            }

            t_verebildigi_dersler.setModel(tm_verebildigi_dersler);
            JOptionPane.showMessageDialog(frame, verebildigiDersler, "Öğretmenin Verebildiği Dersler", JOptionPane.PLAIN_MESSAGE);

        }
    }//GEN-LAST:event_jButton48ActionPerformed

    private void search_box_3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_box_3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_search_box_3ActionPerformed

    private void jButton49ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton49ActionPerformed
        int selectedRowIndex = t_katildigi_kurslar.getSelectedRow();

        if (selectedRowIndex != -1) {

            Kursiyer kursiyer = new Kursiyer();
            kursiyer.setId(Integer.parseInt(tm_kursiyer_bilgileri.getValueAt(t_kursiyer_bilgileri.getRowSorter().convertRowIndexToModel(t_kursiyer_bilgileri.getSelectedRow()), 0).toString()));

            
            //Seçilen kursun tarihi ile şu anki tarih arasındaki gün farkı hesaplanıyor
            String kursBaslamaTarihi = Kurs.baslamaTarihiGetir(tm_katildigi_kurslar.getValueAt(t_katildigi_kurslar.getSelectedRow(), 0).toString());
            
            LocalDate kursBaslamaTarihiParsed = LocalDate.parse(kursBaslamaTarihi);
            if (kursBaslamaTarihiParsed.compareTo(LocalDate.now()) <= 0) {
                JOptionPane.showMessageDialog(this.frame.getContentPane(), "Seçilen kurs başladığı için kayıt silinemez!", "Hata", JOptionPane.ERROR_MESSAGE);
            } 
            else {
                String[] options = new String[2];
                options[0] = new String("Evet");
                options[1] = new String("Hayır");
                int output = JOptionPane.showOptionDialog(this.frame.getContentPane(), "Seçilen kursa olan kaydı silmek istediğinize emin misiniz?", "Kayıt Silme Onayı", 0, JOptionPane.QUESTION_MESSAGE, null, options, null);
                
                if (output == 0) {
                    
                    //Kursiyer kurstan çıkarılıyor.
                    boolean response = kursiyer.kurstanCikar(tm_katildigi_kurslar.getValueAt(t_katildigi_kurslar.getSelectedRow(), 0).toString());

                    if (response) {

                        JOptionPane.showMessageDialog(this.frame.getContentPane(), "Kursiyer, seçilen kurstan başarıyla çıkarıldı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                        
                        //Kurs satin alinan tarihin şimdiyle arasındaki gün farkı    
                        long daysBetween = DAYS.between(LocalDate.parse(tm_katildigi_kurslar.getValueAt(t_katildigi_kurslar.getSelectedRow(), 2).toString()), LocalDate.now());

                        //Eger aradaki gun farki 14'ten (iki hafta) küçük ise
                        if (daysBetween <= 14) {

                            JOptionPane.showMessageDialog(this.frame.getContentPane(), "İade edilecek tutar (TL): " + tm_katildigi_kurslar.getValueAt(t_katildigi_kurslar.getSelectedRow(), 1).toString(), "Bilgi", JOptionPane.INFORMATION_MESSAGE);

                        } else {
                            int ucret = Integer.parseInt(tm_katildigi_kurslar.getValueAt(t_katildigi_kurslar.getSelectedRow(), 1).toString());
                            ucret = ucret / 2;
                            Integer ucretINTEGER = new Integer(ucret);
                            String ucretStr = ucretINTEGER.toString();
                            JOptionPane.showMessageDialog(this.frame.getContentPane(), "İade edilecek tutar (TL): " + ucretStr, "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                        }

                        
                        //Kullanıcının gordugu tablodan secilen kurs çıkarılıyor
                        tm_katildigi_kurslar.removeRow(t_katildigi_kurslar.getSelectedRow());
                    }


                }
                
            }

        }
    }//GEN-LAST:event_jButton49ActionPerformed

    private void jButton50ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton50ActionPerformed
        //Oncelikle kurs tablosu temizleniyor
        for (int i = tm_kurs_bilgileri.getRowCount() - 1; i >= 0; i--) {
            tm_kurs_bilgileri.removeRow(i);
        }

        this.Tablesorter = new TableRowSorter<DefaultTableModel>(tm_kurs_bilgileri);
        t_kurs_bilgileri.setRowSorter(Tablesorter);
        t_kurs_bilgileri.setModel(tm_kurs_bilgileri);

        //Search box filtering
        search_box_4.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        newFilterKurs(search_box_4);
                    }

                    public void insertUpdate(DocumentEvent e) {
                        newFilterKurs(search_box_4);
                    }

                    public void removeUpdate(DocumentEvent e) {
                        newFilterKurs(search_box_4);
                    }
                });

        //Tum kursiyerler cekiliyor
        ArrayList<Kurs> kurslar = Kurs.tumKurslariGetir();

        //Tabloda gosteriliyor
        for (Kurs kurs : kurslar) {
            //{"Kurs Adı","Kurs Tipi","Tarih","Kapasite","Doluluk","Ücret (TL)"}
            tm_kurs_bilgileri.addRow(new Object[]{kurs.getAd(), kurs.getTip(), kurs.getTarih(), kurs.getKapasite(), kurs.getDoluluk(), kurs.getUcret()});
        }

        this.switchPanel(KURS_BILGILERI_GORUNTULE);
    }//GEN-LAST:event_jButton50ActionPerformed

    private void jButton52ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton52ActionPerformed
        this.switchPanel(anaEkran);
    }//GEN-LAST:event_jButton52ActionPerformed

    private void jButton51ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton51ActionPerformed
        int selectedRowIndex = t_kurs_bilgileri.getSelectedRow();

        if (selectedRowIndex != -1) {

            String ucret = JOptionPane.showInputDialog(
                    frame,
                    "Yeni ücreti giriniz.",
                    "Ücret",
                    JOptionPane.QUESTION_MESSAGE);
            
            
            if (ucret!=null && ucret.compareTo("") == 0) {
                JOptionPane.showMessageDialog(this.frame.getContentPane(),"Ücret boş bırakılamaz!","Hata",JOptionPane.ERROR_MESSAGE);
                
            }
            
            else if(ucret != null) {

                Kurs kurs = new Kurs();
                kurs.setAd(tm_kurs_bilgileri.getValueAt(t_kurs_bilgileri.getRowSorter().convertRowIndexToModel(selectedRowIndex), 0).toString());

                String[] options = new String[2];
                options[0] = new String("Evet");
                options[1] = new String("Hayır");
                int output = JOptionPane.showOptionDialog(this.frame.getContentPane(), "Ücreti güncellemek istediğinize emin misiniz?", "Ücret Güncelleme Onayı", 0, JOptionPane.QUESTION_MESSAGE, null, options, null);

                if (output == 0) {
                    
                    boolean response = kurs.ucretGuncelle(Integer.parseInt(ucret));
                    
                    if (response) {
                        JOptionPane.showMessageDialog(this.frame.getContentPane(),"Kurs ücreti başarılı bir şekilde güncellendi!","Bilgi",JOptionPane.INFORMATION_MESSAGE);
                        tm_kurs_bilgileri.setValueAt(ucret,t_kurs_bilgileri.getRowSorter().convertRowIndexToModel(selectedRowIndex), 5);
                    }
                    
                }

            }




        }
    }//GEN-LAST:event_jButton51ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel KURSIYER_BILGILERINI_GORUNTULEME_EKRANI;
    private javax.swing.JPanel KURS_BILGILERI_GORUNTULE;
    private javax.swing.JPanel KURS_HAZIRLAMA_EKRANI;
    private javax.swing.JPanel KURS_SATIS_EKRANI;
    private javax.swing.JPanel OGRETMEN_BILGILERINI_GORUNTULE;
    private javax.swing.JTextField adres;
    private javax.swing.JTextField adres1;
    private javax.swing.JPanel anaEkran;
    private javax.swing.JButton b_ders_getir;
    private javax.swing.JPanel calisabildigiSaatler;
    private javax.swing.JTextField cep_tel;
    private javax.swing.JTextField cep_tel1;
    private javax.swing.JComboBox dersAdi;
    private javax.swing.JTextField dersAdi2;
    private javax.swing.JPanel dersEkleEkrani;
    private javax.swing.JPanel dersKaydiEkrani;
    private javax.swing.JTextField email;
    private javax.swing.JTextField email1;
    private javax.swing.JTextField ev_tel;
    private javax.swing.JTextField ev_tel1;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JComboBox gun;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTextField kapasite;
    private javax.swing.JPanel kursDersleriListele;
    private javax.swing.JPanel kursHazirlamaEkrani;
    private javax.swing.JPanel kursHazirlamaEkrani2;
    private javax.swing.JPanel kursHazirlamaEkrani3;
    private javax.swing.JPanel kursSatisEkrani;
    private javax.swing.JPanel kursSatisEkrani2;
    private javax.swing.JPanel kursSatisEkrani2_1;
    private javax.swing.JPanel kursSatisEkrani3;
    private javax.swing.JPanel kursSatisEkrani4;
    private javax.swing.JPanel kursSatisEkrani4_2;
    private javax.swing.JTextField kurs_adi;
    private javax.swing.JLabel kurs_adi_teyit;
    private javax.swing.JTextField kurs_tarihi;
    private javax.swing.JLabel kurs_tarihi_teyit;
    private javax.swing.JTextField kurs_ucreti;
    private javax.swing.JLabel kurs_ucreti_teyit;
    private javax.swing.JComboBox kurs_zamani_c;
    private javax.swing.JPanel kursiyerBilgileri1;
    private javax.swing.JPanel kursiyerBilgileri2;
    private javax.swing.JTextField kursiyer_adi;
    private javax.swing.JTextField kursiyer_soyadi;
    private javax.swing.JComboBox kursiyer_tipi_c;
    private javax.swing.JList l_calisabildigiSaatler;
    private javax.swing.JList l_calisabildigi_saatler;
    private javax.swing.JList l_yer_alacak_dersler;
    private javax.swing.JTextField max_miktar;
    private javax.swing.JComboBox odeme_yontemi_2_c;
    private javax.swing.JComboBox odeme_yontemi_c;
    private javax.swing.JPanel ogretmenBilgileri1;
    private javax.swing.JPanel ogretmenKaydiEkrani;
    private javax.swing.JTextField ogretmen_adi;
    private javax.swing.JTextField ogretmen_soyadi;
    private javax.swing.JComboBox s_kurs_tipi;
    private javax.swing.JComboBox saat;
    private javax.swing.JTextField search_box;
    private javax.swing.JTextField search_box_2;
    private javax.swing.JTextField search_box_3;
    private javax.swing.JTextField search_box_4;
    private javax.swing.JComboBox sinif;
    private javax.swing.JTable t_eklenebilecek_dersler;
    private javax.swing.JTable t_eklenen_dersler;
    private javax.swing.JTable t_icerdigi_dersler;
    private javax.swing.JTable t_katildigi_kurslar;
    private javax.swing.JTable t_kayitli_kursiyerler;
    private javax.swing.JTable t_kurs_bilgileri;
    private javax.swing.JTable t_kursiyer_bilgileri;
    private javax.swing.JTable t_ogretmen_atama;
    private javax.swing.JTable t_ogretmen_bilgileri;
    private javax.swing.JTable t_secilebilecek_kurslar;
    private javax.swing.JTable t_verdigi_dersler;
    private javax.swing.JTable t_verebildigi_dersler;
    private javax.swing.JPanel verebildigiDersler;
    // End of variables declaration//GEN-END:variables


    public static void main(String[] args) {
          
        MainProgram mainProgram = new MainProgram();
        JFrame frame = new JFrame("El Sanatları Kursu Programı");
        mainProgram.setFrame(frame);

    }
    
}    
