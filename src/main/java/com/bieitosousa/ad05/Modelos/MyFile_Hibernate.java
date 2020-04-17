/*
 * The MIT License
 *
 * Copyright 2020 bieito.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bieitosousa.ad05.Modelos;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.*;

/**
 *
 * @author bieito
 */
@Entity
@Table(name = "MyFile__Hibernate")
public class MyFile_Hibernate implements Serializable {

    public static MyFile_Hibernate getElement(int parseInt, Class<MyFile_Hibernate> aClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Id
    @Column(name = "MyFile_Hibernate_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "MyFile_Hibernate_name", nullable = false)
    private String name;
    @ManyToOne (cascade = {CascadeType.ALL})
    @JoinColumn(name="MyDirectori__Hibernatee_id")
     private MyDirectori_Hibernate myDictori;
    @Column(name = "MyFile_Hibernate_datos", nullable = false)
    private byte[] datos;
    @Transient
    private int state;// [0] {Directorio (No) DB (SI)} [1] Archivo sin Cambios    [2]Archivo Modificado  [3] Archivo Nuevo [-1] error [4]creado
    public MyFile_Hibernate(){}
  
    public MyFile_Hibernate( String name, MyDirectori_Hibernate myDictori, byte[] datos) {
        this.name = name;
        this.myDictori = myDictori;
        this.datos = datos;
    }
    
    public MyFile_Hibernate( File f , MyDirectori_Hibernate directorio ) {
        this.name = f.getName();
        this.myDictori = directorio;
        this.datos = copyBytes(f);
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
    
    public String getUrl() {
     return this.myDictori.getUrl()+File.separator+name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MyDirectori_Hibernate getMyDictori() {
        return myDictori;
    }

    public void setMyDictori(MyDirectori_Hibernate myDictori) {
        this.myDictori = myDictori;
    }

    public byte[] getDatos() {
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.myDictori);
        hash = 53 * hash + Arrays.hashCode(this.datos);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MyFile_Hibernate other = (MyFile_Hibernate) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.myDictori, other.myDictori)) {
            return false;
        }
        if (!Arrays.equals(this.datos, other.datos)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "MyFile_Hibernate{" + "name=" + name + ", myDictori=" + myDictori + ", datos=" + datos + '}';
    }

   
    

//  ======================================================================
//    =    OPERACIONES    {Filtro URL}   =
//  ======================================================================
//
//  *   |-->    filterUrl    
//        [sustitulle "/" y "\" por File.separator]
//
    private String filterUrl(String url) {
//    url.replaceAll("\\", File.separator);
//    url.replaceAll("/", File.separator); 
        return url;
    }

//  ======================================================================
//    =    OPERACIONES    {DATA}   =
//  ======================================================================
//
//  *   |-->    copyBytes    
//        [Copiar datos del archivo]
//  *   |-->    pasteData     
//        [Pegar datos del archivo]
//
    //Copiar datos del archivo
    
    public byte[] copyBytes(){
    return copyBytes(this);
    }
    
    private byte[] copyBytes(MyFile_Hibernate fh) {
        byte contenido[] = null;
        FileInputStream ficheroStream = null;
        try {
            File fichero = new File(fh.myDictori.getUrl()+File.separator+fh.getName());
            ficheroStream = new FileInputStream(fichero);
            contenido = new byte[(int) fichero.length()];
            ficheroStream.read(contenido);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyFile_Hibernate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyFile_Hibernate.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ficheroStream.close();
            } catch (IOException ex) {
                Logger.getLogger(MyFile_Hibernate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return contenido;
    }
    private byte[] copyBytes(File fichero ) {
        byte contenido[] = null;
        FileInputStream ficheroStream = null;
        try {
            ficheroStream = new FileInputStream(fichero);
            contenido = new byte[(int) fichero.length()];
            ficheroStream.read(contenido);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(MyFile_Hibernate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyFile_Hibernate.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                ficheroStream.close();
            } catch (IOException ex) {
                Logger.getLogger(MyFile_Hibernate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return contenido;
    }

    //Pegar datos del archivo
    public boolean pasteData(){
    return pasteData(this);
    }
    
    private boolean pasteData(MyFile_Hibernate fh) {
        File destino = new File(fh.myDictori.getUrl()+File.separator+fh.getName());
        try {
            OutputStream out = new FileOutputStream(destino);
            out.write(datos);
            out.close();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

   
}// Fin MyFile_Hibernate
