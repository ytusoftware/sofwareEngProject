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
import java.util.ArrayList;
import static org.mockito.Mockito.*;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author baris
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({MainProgram.class, Kurs.class})
@SuppressStaticInitializationFor({"javax.swing.JComponent", "javax.swing.text.JTextComponent"})
public class kursSatisTest {
    @Mock DefaultListModel<String> lm;
    @Mock JComboBox jcb;
    @Mock JTextField jtf, jtf2;
    @Mock Kurs k;
    @Mock Connection conn;
    @Mock PreparedStatement ps;
    @Mock ResultSet rs;
    Controller c;
    
    @Before
    public void setup(){
        c = new Controller();
    }
    
    @Test
    public void checkFieldsTest_isEmpty(){
       when(lm.isEmpty()).thenReturn(true);
       String message = c.checkFields(lm, "");
       Assert.assertEquals(message, "Lütfen kursta yer alacak ders ekleyiniz!");
    }
    
    @Test
    public void checkFieldsTest_compareTo(){
       when(lm.isEmpty()).thenReturn(false);
       String message = c.checkFields(lm, "");
       Assert.assertEquals(message, "Lütfen ödenebilecek max miktar giriniz!");
    }
    
    @Test
    public void checkFieldsTest_success(){
       when(lm.isEmpty()).thenReturn(false);
       String message = c.checkFields(lm, "3000");
       Assert.assertEquals(message, "");
    }
    
    @Test
    public void getCourses_positiveNumber(){
        PowerMockito.mockStatic(Kurs.class);
        ArrayList<Kurs> kurslar = new ArrayList<>();
        
        kurslar.add(new Kurs());
        kurslar.add(new Kurs());
        kurslar.add(new Kurs());
        
        when(jcb.getSelectedItem()).thenReturn(new String("11.00"));
        when(jtf.getText()).thenReturn("30000");
        when(Kurs.getsecilebilecekKurslar(any(ArrayList.class), any(String.class),
                any(Integer.class))).thenReturn(kurslar);
        
        ArrayList<String> dersler = new ArrayList<>();
        
        ArrayList<Kurs> kurslar1 = c.getCourses(dersler, jcb, jtf);
        Assert.assertEquals(3, kurslar1.size());
    }
    
    @Test
    public void getCourses_negativeNumber(){
        PowerMockito.mockStatic(Kurs.class);
        ArrayList<Kurs> kurslar = new ArrayList<>();
        
        when(jcb.getSelectedItem()).thenReturn(new String("11.00"));
        when(jtf.getText()).thenReturn("-1000");
        when(Kurs.getsecilebilecekKurslar(any(ArrayList.class), any(String.class),
                any(Integer.class))).thenReturn(kurslar);
        
        ArrayList<String> dersler = new ArrayList<>();
        
        ArrayList<Kurs> kurslar1 = c.getCourses(dersler, jcb, jtf);
        Assert.assertEquals(0, kurslar1.size());
    }
    
    @Test(expected = NumberFormatException.class)
    public void getCourses_NaN(){
        PowerMockito.mockStatic(Kurs.class);
        ArrayList<Kurs> kurslar = new ArrayList<>();
        
        when(jcb.getSelectedItem()).thenReturn(new String("11.00"));
        when(jtf.getText()).thenReturn("asdqwe");
        when(Kurs.getsecilebilecekKurslar(any(ArrayList.class), any(String.class),
                any(Integer.class))).thenReturn(kurslar);
        
        ArrayList<String> dersler = new ArrayList<>();
        
        ArrayList<Kurs> kurslar1 = c.getCourses(dersler, jcb, jtf);
    }
    
    @Test
    public void checkTestField_kaEmpty(){
        when(jtf2.getText()).thenReturn("");
        when(jtf.getText()).thenReturn("asd");
        when(jcb.getSelectedIndex()).thenReturn(1);
        
        String mes = c.checkTextField(jtf2, jtf, jtf, jtf, jcb);
        Assert.assertEquals("Kursiyer adı boş bırakılamaz!", mes);
    }
    
    @Test
    public void checkTestField_ksaEmpty(){
        when(jtf2.getText()).thenReturn("");
        when(jtf.getText()).thenReturn("asd");
        when(jcb.getSelectedIndex()).thenReturn(1);
        
        String mes = c.checkTextField(jtf, jtf2, jtf, jtf, jcb);
        Assert.assertEquals("Kursiyer soyadı boş bırakılamaz!", mes);
    }
    
    @Test
    public void checkTestField_kctEmpty(){
        when(jtf2.getText()).thenReturn("");
        when(jtf.getText()).thenReturn("asd");
        when(jcb.getSelectedIndex()).thenReturn(1);
        
        String mes = c.checkTextField(jtf, jtf, jtf2, jtf, jcb);
        Assert.assertEquals("Cep telefonu boş bırakılamaz!", mes);
    }
    
    @Test
    public void checkTestField_keEmpty(){
        when(jtf2.getText()).thenReturn("");
        when(jtf.getText()).thenReturn("asd");
        when(jcb.getSelectedIndex()).thenReturn(1);
        
        String mes = c.checkTextField(jtf, jtf, jtf, jtf2, jcb);
        Assert.assertEquals("E-mail boş bırakılamaz!", mes);
    }
    
    @Test
    public void checkTestField_koyEmpty(){
        when(jtf.getText()).thenReturn("asd");
        when(jcb.getSelectedIndex()).thenReturn(0);
        
        String mes = c.checkTextField(jtf, jtf, jtf, jtf, jcb);
        Assert.assertEquals("Lütfen ödeme yöntemi seçiniz!", mes);
    }
    
    @Test
    public void checkTestField_success(){
        when(jtf.getText()).thenReturn("asd");
        when(jcb.getSelectedIndex()).thenReturn(1);
        
        String mes = c.checkTextField(jtf, jtf, jtf, jtf, jcb);
        Assert.assertEquals("", mes);
    }
}

/*







*/