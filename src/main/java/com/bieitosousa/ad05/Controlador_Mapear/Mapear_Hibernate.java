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
package com.bieitosousa.ad05.Controlador_Mapear;

import com.bieitosousa.ad05.Controlador_Json.*;
import com.bieitosousa.ad05.Controlador_Mapear.*;
import com.bieitosousa.ad05.Controlador_Notificaciones.*;
import com.bieitosousa.ad05.Controlador_DB.*;
import com.bieitosousa.ad05.Modelos.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bieito
 */
public class Mapear_Hibernate {
//  ======================================================================
//                          =    ATRIBUTOS  =
//  ======================================================================
//  =>  driver      [INSTANCIAR EL OBJETO {HibernateUtil}] 
//  =>  ruta        [DIRECTORIO -> FILE]
//  =>  raiz        [DIRECTORIO -> STRING]
//  =>  directorio  [Cargar los datos de la DT en lista de Objeto]
//  =>  {List<String>}    [MOSTRAR EL RESULTADO DE LAS OPERACIONES]
//          =        =       REFERENCIAS  [Comentarios ]       =        =
//  *   Directorio --> Referenciar {fichero||String} directorio local  mapeado
//  *   this.directorio --> Referenciar lista de objetos DT
//  *   DT --> Referenciar los registros de la DT    
//      

    private HibernateUtil h = HibernateUtil.getInstance();
    private  String ruta = JSonMake.getApp().getDirectory();
    private File raiz = new File(ruta);
    private List<String> archNew = new ArrayList<>();
    private List<String> archUpdate = new ArrayList<>();
    private List<String> archDelete = new ArrayList<>();
    private List<MyFile_Hibernate> archivo = new ArrayList<>();
    private List<MyDirectori_Hibernate> directorio = new ArrayList<>();
    protected static int cont = -1;
    private Calendar c = Calendar.getInstance();
    private String dia = Integer.toString(c.get(Calendar.DATE));
    private String mes = Integer.toString(c.get(Calendar.MONTH));
    private String annio = Integer.toString(c.get(Calendar.YEAR));
    private String hora = Integer.toString(c.get(Calendar.HOUR));
    private String min = Integer.toString(c.get(Calendar.MINUTE));
    private String seg = Integer.toString(c.get(Calendar.SECOND));
    //  =========================================================
    //      =   =   =   =  =    CABECERAS  =   =   =   =  =   
    //  =========================================================

    private String cabecera = "[" + (cont) + "]"
            + "[Driver]"
            + "[" + dia + "/" + mes + "/" + annio
            + "::" + hora + ":" + min + ":" + seg + "]";
    private String rMapeado = cabecera
            + "=============================="
            + "=============================="
            + "==============================\n"
            + cabecera
            + "    =    OPERACIONES    DE MAPEADO "
            + "=  {mappDirectory}    =   \n"
            + cabecera
            + "=============================="
            + "=============================="
            + "==============================\n";
    private String rPreMapeado = cabecera
            + "=============================="
            + "=============================="
            + "==============================\n"
            + cabecera
            + "    =    OPERACIONES    {ANTES}  DE MAPEADO "
            + "=   DIRECTORIO  =   DB  =   \n"
            + cabecera
            + "=============================="
            + "=============================="
            + "==============================\n";
    private String rDesMapeado = cabecera
            + "=============================="
            + "=============================="
            + "==============================\n"
            + cabecera
            + "    =    OPERACIONES    {DESPUES}  DE MAPEADO "
            + "=   DIRECTORIO  =   DB  =   \n"
            + cabecera
            + "=============================="
            + "=============================="
            + "==============================\n";
    private String rResMapeado = cabecera
            + "=============================="
            + "=============================="
            + "==============================\n"
            + cabecera
            + "    =    OPERACIONES    {RESULTADO}  DE MAPEADO "
            + "=               =       =   \n"
            + cabecera
            + "=============================="
            + "=============================="
            + "==============================\n";

    private String cMapeado = "   =   [MAPEADO]   = ";
    private String cPreMapeado = "   =   [ANTES]  [MAPEADO]   = ";
    private String cDessMapeado = "   =   [DESPUES]  [MAPEADO]   = ";
    private String cResMapeado = "   =   [RESULTADO]  [MAPEADO]   = ";

    public Mapear_Hibernate() {
        this.cont++;
    }
    public Mapear_Hibernate(String ruta) {
        this.cont++;
        this.ruta=ruta;
    }
    public static void main(String[] args) throws Exception {
        Mapear_Hibernate m = new Mapear_Hibernate();
        m.start();
    }

//  ======================================================================
//    =    OPERACIONES    {ANTES}  DE MAPEADO =   DIRECTORIO  =   DB  =
//  ======================================================================
//
//  *   |-->    start    
//        [ Ejecuta el programa , en un orden determinado, filtrando]
//        
    public void start() {
//      HIBERNATE CREA AUTOMATICAMENTE LAS TABLAS
//        if (driver.createTabletMyFile()) { 
        //Creamos el directorio si no existe

        System.out.println(rPreMapeado);
        System.out.println(cabecera + cPreMapeado
                + "   ANALIZANDO DIRECTORIO [" + ruta + "]");
        if (createDirectory()) {
            System.out.println(cabecera + cPreMapeado
                    + "  Extrallendo datos de la DB ");
            if (cargarRegistrosDB() || directorio.size() == 0 || archivo.size() == 0) {
                if (rebootState() || directorio.size() == 0 || archivo.size() == 0) {
                    viewDBDirectories();
                    System.out.println(cabecera + cPreMapeado
                            + "  Preparando DIRECTORIO [" + ruta + "] para su analisis [OK]");
                    System.out.println(rMapeado);
                    //Mapeamos
                    mappDirectory(raiz);
                    System.out.println(rDesMapeado);
                    createDeleteFile();
                    System.out.println(rResMapeado);
                    resultado();
                } else {
                    System.err.println(cabecera
                            + " Fallo al reiniciar los marcadores");
                }
            } else {
                System.err.println(cabecera
                        + " Fallo al cargar lista directorios desde la DB");
            }
        } else {
            System.err.println(cabecera
                    + " Fallo al crear el directorio raiz ");
        }
//        } else {
//            System.out.println("Fallo al crear las tablas en la DB");
//        }
    }

//  ======================================================================
//    =    OPERACIONES    {ANTES}  DE MAPEADO =   DIRECTORIO  =   DB  =
//  ======================================================================
//
//  *   [H]|    -->    driver.createTabletMyFile() {Lo hace HIbernate}    
//        [Filtramos , Creamos las tablas en la DT]
//  *   [2]|-->    createDirectory     
//        [Filtramos , Creamos el directorio desde donde vamos a mapear]
//  *   [3]|-->    getDirectorio   
//  *     [ Cargamos los datos de la DB en --> this.directirio]
//  *   [4]|-->    rebootState   
//  *     [ reiniciamos el estado:: en this.Directorio ponemos {state = 0}] 
//  *   [5]|-->    viewDBDirectories   
//  *    [ Muestra por pantalla los registros de la DB]
//
    private boolean createDirectory() {
        if (!raiz.exists()) {
            return raiz.mkdirs();
        }
        return true;
    }

    private boolean cargarRegistrosDB() {
        return cargarDirectorio() && cargarArchivo();
    }

    private boolean rebootState() {
        boolean d = false, a = false;
        if (this.directorio != null) {
            for (MyDirectori_Hibernate md : this.directorio) {
                md.setState(0);
                d = true;
            }
        }
        if (this.archivo != null) {
            for (MyFile_Hibernate mf : this.archivo) {
                mf.setState(0);
                a = true;
            }
        }
        if (a && d) {
            return true;
        } else {
            return false;
        }
    }

    private void viewDBDirectories() {
        System.out.println(cabecera + cPreMapeado
                + "          =   =   =   =   =   =   "
                + " [INICIO]   [ANALISIS]    [REGISTROS]      [DB]   =   ");
        if (directorio != null) {
            System.out.println(cabecera + cPreMapeado
                    + "           "
                    + "=___DIRECTORIOS____[DIR]___");
            for (MyDirectori_Hibernate md : this.directorio) {
                System.out.println(cabecera + cPreMapeado
                        + "  [REGISTROS] [DIRECTORIOS] "
                        + "[DB][DIR]    " + md);
            }
        }

        if (archivo != null) {
            System.out.println(cabecera
                    + cPreMapeado
                    + " [ANALISIS]    [REGISTROS]  "
                    + "___ARCHIVOS____[ARCH]___");
            for (MyFile_Hibernate mf : this.archivo) {
                System.out.println(cabecera + cPreMapeado
                        + "  [REGISTROS] [ARCH] "
                        + "[DB][ARCH]    " + mf);
            }
        }
        System.out.println(cabecera + cPreMapeado
                + "          =   =   =   =   =   =   "
                + "     =   [FIN]   [ANALISIS]    [REGISTROS]      [DB]   =   ");
    }
//  ======================================================================
//    =    OPERACIONES    DE MAPEADO =   DIRECTORIO  =   DB  =
//  ======================================================================
//
//  *   [6]|-->    mappDirectory   
//        [Mapea el directorio]
//  *   [6.1]|-->    saveFile     
//        [ Establece el estado {Archivo} -->Compara < Directorio Vs DB >]
//  *   [6.1.2]|-->    isDirectoryContainsKey   
//  *     [ Compara {DB (url) Directorio(url)} (==)tru (!=)false ]
//  *   [6.1.3]|-->    getDBMyFile   
//  *     [Obtiene un objeto de la lista this.directorio >> Directorio(url)] 
//
//  ====================================================
//    =    OPERACIONES    DE MAPEADO =  {mappDirectory}
//  ====================================================
// Input : ruta del fichero
// Ouput : 
//      si es un directorio : mira los ficheros que hay dentro.
//      si es un fichero lo analiza
//       1. no existe en la DB --> lo crea de nuevo
//       2. existeen la DB  pero tiene cambios --> lo modifica
//       3. exiete en el la DB pero no en el mapeado --> lo elimina de la DB
//

    public void mappDirectory(File fraiz) {
        MyDirectori_Hibernate Draiz = null;
        if ((Draiz = saveDirectori(fraiz)) != null) {;
            File[] ficheros = fraiz.listFiles();
            for (File fichero : ficheros) {
                if (fichero.exists()) {
                    if (fichero.isDirectory()) {
                        mappDirectory(fichero);
                    } else if (fichero.isFile()) {
                        saveFile(fichero, Draiz);
                    } else {
                        System.err.println(cabecera
                                + "Error iniesperado");
                    }
                } else {
                    System.err.print(cabecera
                            + "El fichero no existe [" + fichero.getPath() + "]");
                }
            }
        } else {
            System.err.println(cabecera
                    + "No se a podido crear el directorio [" + fraiz.getPath() + "]");
        }
    }

//  ====================================================
//    =    OPERACIONES    DE MAPEADO =  {saveFile}
//  ====================================================
//  (i) Inicialmente el Estado se reinicia >> Estado[0]
//      Analiza el fichero
//       1. DB (NO) Directorio(SI) --> lo crea de nuevo >>Estado [3]
//       2. DB (!=) Directorio --> lo modifica >>Estado [2]
//       3. DB (==) Directorio --> Sin Cambios >>Estado [1]
//       4. DB (SI) Directorio(NO) --> No se trataron >>Estado [0]
//  
    private boolean saveFile(File f, MyDirectori_Hibernate Draiz) {
        if (f.exists() && Draiz != null) {
            MyFile_Hibernate readFile = null;
            if ((readFile = new MyFile_Hibernate(f, Draiz)) != null) {
                if (isArchivoContainsKey(readFile.getUrl())) {
                    // el archivo ya existe [1]
                    MyFile_Hibernate dbFile = getDBMyFile(readFile.getUrl());
                    if ((dbFile).equals(readFile)) {
                        // identificamos que esta vigente
                        if (updateThisArchivo(dbFile, 1)) {
                            System.out.print(cabecera + cMapeado
                                    + "= {FICHERO} = [" + f.getPath()
                                    + "] DB [=] DIR [=]");
                            System.out.print(" Estado [Sin Cambios] \n");
                            return true;
                        } else {
                            System.err.print(cabecera + cMapeado
                                    + "= {FICHERO} = [" + f.getPath()
                                    + "[ERROR] [Sin Cambios] ");
                            return false;
                        }
                        // el archivo es identico al guardado en la Db
                    } else {//modificado [2]
                        // el archivo contiene cambios con el guardado en la DB. 
                        // update en la DB
                        // identificamos que esta vigente
                        System.out.print(cabecera + cMapeado
                                + "= {FICHERO} = [" + f.getPath() + "] ");
                        if (h.update(readFile)) {
                            System.out.print(" DB [UPDATE] ");
                        } else {
                            System.err.print(cabecera + cMapeado
                                    + "[ERROR] = {FICHERO} = [" + f.getPath()
                                    + "[DB] [NO] UPDATE ");
                            return false;
                        }
                        if (updateThisArchivo(dbFile, readFile, 2)) {
                            System.out.print(" DIR [UPDATE] ");
                        } else {
                            System.err.print(cabecera + cMapeado
                                    + "[ERROR] = {FICHERO} = [" + f.getPath()
                                    + "[DIR] [ERROR] [UPDATE] ");
                            return false;
                        }
                        System.out.print(" Estado [Modificado] \n");
                        return true;
                    }
                } else { // nuevo archivo [3]
                    // identificamos que esta vigente

                    if (h.add(readFile)) {
                        System.out.print(" DB [NEW] ");
                    } else {
                        System.err.print(cabecera + cMapeado
                                + "[ERROR] = {FICHERO} = [" + f.getPath()
                                + "[DB] [ERROR] [NEW] ");
                        return false;
                    }
                    if (addThisArchivo(readFile, 3)) {
                        System.out.print(" DIR [NEW] ");
                    } else {
                        System.err.print(cabecera + cMapeado
                                + "[ERROR] = {FICHERO} = [" + f.getPath()
                                + "[DIR] [ERROR] [NEW] ");
                        return false;
                    }
                    System.out.print(" Estado [Nuevo] \n");
                    return true;
                }
                // Los archivos que no se trataron en this.directorio>>Estado [0] 
            } else {
                System.err.println(cabecera
                        + "No se a podido crear el archivo [" + f.getPath() + "]");
                return false;
            }
        } else {
            System.err.println(cabecera
                    + "No se a podido crear el archivo [" + f.getPath() + "]");
            return false;
        }
    }

    private MyDirectori_Hibernate saveDirectori(File f) {
        if (f.exists()) {
            MyDirectori_Hibernate readDictori = null;
            if ((readDictori = new MyDirectori_Hibernate(f)) != null) {
                if (isDirectoryContainsKey(f.getPath())) {
                    // identificamos que esta vigente
                    if (updateThisDirectori(readDictori, 1)) {
                        System.out.print(cabecera + cMapeado
                                + "= = {DIRECTORIO} = = [" + f.getPath()
                                + "] DB [=] DIR [=]");
                        System.out.print(" Estado [Sin Cambios] \n");
                        return readDictori;
                    } else {
                        System.err.print(cabecera + cMapeado
                                + "[ERROR] = = {DIRECTORIO} = =  [" + f.getPath()
                                + "[ERROR] [Sin Cambios] ");
                        return null;
                    }
                } else {// nuevo archivo [3]
                    // identificamos que esta vigente

                    if (h.add(readDictori)) {
                        System.out.print(" DB [NEW] ");
                    } else {
                        System.err.print(cabecera + cMapeado
                                + "[ERROR] = = {DIRECTORIO} = =  [" + f.getPath()
                                + "[DB] [ERROR] [NEW] ");
                        return null;
                    }
                    if (addDirectori(readDictori, 3)) {
                        System.out.print(" DIR [NEW] ");
                    } else {
                        System.err.print(cabecera + cMapeado
                                + "[ERROR] = = {DIRECTORIO} = =  [" + f.getPath()
                                + "[DIR] [ERROR] [NEW] ");
                        return null;
                    }
                    System.out.print(" Estado [Nuevo] \n");
                    return readDictori;
                }
            } else {
                System.err.println(cabecera
                        + "No se a podido crear el archivo [" + f.getPath() + "]");
                return null;
            }
        } else {
            System.err.println(cabecera
                    + "No se a podido crear el archivo [" + f.getPath() + "]");
            return null;
        }
    }

    // -------- COMPAR url --> (DT) VS (DIRECTORIO) -----------
    private boolean isDirectoryContainsKey(String url) {
        for (MyDirectori_Hibernate md : this.directorio) {
            if (md.getUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }

    private boolean isArchivoContainsKey(String url) {
        for (MyFile_Hibernate mf : this.archivo) {
            if (mf.getUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }

    // -------- COMPAR Object (MyFile) --> (DT) VS (DIRECTORIO) -----------
    private MyFile_Hibernate getDBMyFile(String url) {
        for (MyFile_Hibernate mf : this.archivo) {
            if (mf.getUrl().equals(url)) {
                return mf;
            }
        }
        return null;
    }

    private MyDirectori_Hibernate getDBMyDirectori(String url) {
        for (MyDirectori_Hibernate md : this.directorio) {
            if (md.getUrl().equals(url)) {
                return md;
            }
        }
        return null;
    }
//  ======================================================================
//    =    OPERACIONES    {DESPUES}  DE MAPEADO =   DIRECTORIO  =   DB  =
//  ======================================================================
//
//  *   |-->    deleteFile    
//        [Elimina el archivo en la DB  { Directorio (NO) DT (SI)}]
//  *   [7]|-->    createDeleteFile     
//        [Crea el archivo en el Directorio  { Directorio (NO) DT (SI)}]
//  *   [8]|-->    resultado   
//  *     [Muestra el resultado de las operaciones]
//   

    private void deleteFile() {
        for (MyFile_Hibernate mf : this.archivo) {
            // si el directorio no existe
            if (mf.getState() == 0) {
                // 1. Eliminalo de la DB
                h.delete(mf);
                // 2. Eliminalo de la Lista 
                directorio.remove(mf);
            };
        }
    }

    public void createDeleteFile(File f) {
        if (f.isFile()) {
            // si el archivo no existe
            MyFile_Hibernate mf = new MyFile_Hibernate(f, new MyDirectori_Hibernate(f.getParentFile()));
            try {
                File fnFile = new File(mf.getUrl());
                if (fnFile.getParentFile().mkdirs()) {
                    System.out.println(cabecera
                            + " Creando Directorio ...."
                            + fnFile.getParentFile().getPath());
                    updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()), 4);
                } else {
//                            System.out.println(this.cabecera
//                                    + " [NO] se ha Creando Directorio"
//                                    + "..." + fnFile.getParentFile().getPath());
//                             updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()),-1);
                }
                if (fnFile.createNewFile()) {
                    updateThisArchivo(new MyFile_Hibernate(fnFile, new MyDirectori_Hibernate(fnFile.getParentFile())), 4);
                    System.out.println(this.cabecera
                            + " Creando Archivo ...... "
                            + fnFile.getPath());
                } else {
                    System.out.println(this.cabecera
                            + "[NO] se ha Creando Archivo ...."
                            + ".. " + fnFile.getPath());
                    updateThisArchivo(new MyFile_Hibernate(fnFile, new MyDirectori_Hibernate(fnFile.getParentFile())), -1);
                }

            } catch (IOException ex) {
                Logger.getLogger(Mapear_Hibernate.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            mf.pasteData();
        }
        if (f.isDirectory()) {
            MyDirectori_Hibernate md;
            if ((md = new MyDirectori_Hibernate(f)) != null) {
                File fnFile = new File(md.getUrl());
                if (fnFile.mkdirs()) {
                    System.out.println(this.cabecera + cDessMapeado
                            + " Creando Directorio ...."
                            + fnFile.getParentFile().getPath());
                    updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()), 4);
                } else {
//                            System.out.println(this.cabecera
//                                    + " [NO] se ha Creando Directorio"
//                                    + "..." + fnFile.getParentFile().getPath());
//                             updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()),-1);
                }

            }
        }
    }
    public void createDeleteFile(MyFile_Hibernate mf) {
      
            // si el archivo no existe
            
            try {
                File fnFile = new File(mf.getUrl());
                if (fnFile.getParentFile().mkdirs()) {
                    System.out.println(cabecera
                            + " Creando Directorio ...."
                            + fnFile.getParentFile().getPath());
                    updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()), 4);
                } else {
//                            System.out.println(this.cabecera
//                                    + " [NO] se ha Creando Directorio"
//                                    + "..." + fnFile.getParentFile().getPath());
//                             updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()),-1);
                }
                if (fnFile.createNewFile()) {
                    updateThisArchivo(new MyFile_Hibernate(fnFile, new MyDirectori_Hibernate(fnFile.getParentFile())), 4);
                    System.out.println(this.cabecera
                            + " Creando Archivo ...... "
                            + fnFile.getPath());
                } else {
                    System.out.println(this.cabecera
                            + "[NO] se ha Creando Archivo ...."
                            + ".. " + fnFile.getPath());
                    updateThisArchivo(new MyFile_Hibernate(fnFile, new MyDirectori_Hibernate(fnFile.getParentFile())), -1);
                }

            } catch (IOException ex) {
                Logger.getLogger(Mapear_Hibernate.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
            mf.pasteData();
        }
        
           public void createDeleteFile(MyDirectori_Hibernate md) {

                File fnFile = new File(md.getUrl());
                if (fnFile.mkdirs()) {
                    System.out.println(this.cabecera + cDessMapeado
                            + " Creando Directorio ...."
                            + fnFile.getParentFile().getPath());
                    updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()), 4);
                } else {
//                            System.out.println(this.cabecera
//                                    + " [NO] se ha Creando Directorio"
//                                    + "..." + fnFile.getParentFile().getPath());
//                             updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()),-1);
                }

            
        }
    
    private void createDeleteFile() {
        boolean arch = false;
        if (archivo.size() > 0) {
            for (MyFile_Hibernate mf : this.archivo) {
                // si el archivo no existe
                if (mf.getState() == 0) {
                    try {
                        File fnFile = new File(mf.getUrl());
                        if (fnFile.getParentFile().mkdirs()) {
                            System.out.println(cabecera
                                    + " Creando Directorio ...."
                                    + fnFile.getParentFile().getPath());
                            updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()), 4);
                        } else {
//                            System.out.println(this.cabecera
//                                    + " [NO] se ha Creando Directorio"
//                                    + "..." + fnFile.getParentFile().getPath());
//                             updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()),-1);
                        }
                        if (fnFile.createNewFile()) {
                            updateThisArchivo(new MyFile_Hibernate(fnFile, new MyDirectori_Hibernate(fnFile.getParentFile())), 4);
                            System.out.println(this.cabecera
                                    + " Creando Archivo ...... "
                                    + fnFile.getPath());
                        } else {
                            System.out.println(this.cabecera
                                    + "[NO] se ha Creando Archivo ...."
                                    + ".. " + fnFile.getPath());
                            updateThisArchivo(new MyFile_Hibernate(fnFile, new MyDirectori_Hibernate(fnFile.getParentFile())), -1);
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(Mapear_Hibernate.class.getName()).log(
                                Level.SEVERE, null, ex);
                    }
                    mf.pasteData();
                }
            }
        }
        if (directorio.size() > 0) {
            for (MyDirectori_Hibernate md : this.directorio) {
                // si el archivo no existe
                if (md.getState() == 0) {
                    File fnFile = new File(md.getUrl());
                    if (fnFile.mkdirs()) {
                        System.out.println(this.cabecera + cDessMapeado
                                + " Creando Directorio ...."
                                + fnFile.getParentFile().getPath());
                        updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()), 4);
                    } else {
//                            System.out.println(this.cabecera
//                                    + " [NO] se ha Creando Directorio"
//                                    + "..." + fnFile.getParentFile().getPath());
//                             updateThisDirectori(new MyDirectori_Hibernate(fnFile.getParentFile()),-1);
                    }

                }
            }
        }
    }

    private void resultado() {
        List<String> archNew = new ArrayList<>();
        List<String> archCreate = new ArrayList<>();
        List<String> archUpdate = new ArrayList<>();
        List<String> archDelete = new ArrayList<>();
        List<String> archNotChange = new ArrayList<>();
        List<String> archError = new ArrayList<>();
        List<String> dirNew = new ArrayList<>();
        List<String> dirNotChange = new ArrayList<>();
        List<String> dirDelete = new ArrayList<>();
        List<String> dirCreate = new ArrayList<>();

        for (MyFile_Hibernate mf : this.archivo) {
            switch (mf.getState()) {
                case -1:
                    archError.add(mf.getUrl());
                    break;
                case 0:
                    archDelete.add(mf.getUrl());
                    break;
                case 1:
                    archNotChange.add(mf.getUrl());
                    break;
                case 2:
                    archUpdate.add(mf.getUrl());
                    break;
                case 3:
                    archNew.add(mf.getUrl());
                    break;
                case 4:
                    archCreate.add(mf.getUrl());
                    break;
                default:
                    break;
            }

        }
        for (MyDirectori_Hibernate md : this.directorio) {
            switch (md.getState()) {
                case 0:
                    dirDelete.add(md.getUrl());
                    break;
                case 1:
                    dirNotChange.add(md.getUrl());
                    break;
                case 2:

                    break;
                case 3:
                    dirNew.add(md.getUrl());
                    break;
                case 4:
                    dirCreate.add(md.getUrl());
                    break;
                default:
                    break;
            }

        }
        if (archNew.size() > 0) {
            System.out.println(cabecera + cResMapeado
                    + "     " + "    __ Archivos nuevos ___");
            for (String fn : archNew) {
                System.out.println(cabecera + cResMapeado
                        + "             " + "  New --> " + fn);
            }
        }
        if (archUpdate.size() > 0) {

            System.out.println(cabecera + cResMapeado
                    + "     " + "    __ Archivos Modificados ___");
            for (String fm : archUpdate) {
                System.out.println(cabecera + cResMapeado
                        + "             " + "  Update --> " + fm);
            }
        }
        if (archNotChange.size() > 0) {
            System.out.println(cabecera + cResMapeado
                    + "     " + "    __ Archivos Sin Cambios ___");
            for (String fsc : archNotChange) {
                System.out.println(cabecera + cResMapeado
                        + "             " + "  Not Change --> " + fsc);
            }
        }
        if (archDelete.size() > 0) {
            System.out.println(cabecera + cResMapeado
                    + "     " + "    __ Archivos eliminados ___");
            for (String fe : archDelete) {
                System.out.println(cabecera + cResMapeado
                        + "             " + "  Delete --> " + fe);
            }
        }
        if (archCreate.size() > 0) {
            System.out.println(cabecera + cResMapeado
                    + "     " + "    __ Archivos creados ___");
            for (String fc : archCreate) {
                System.out.println(cabecera + cResMapeado
                        + "             " + "  Creados --> " + fc);
            }
        }
        if (dirNew.size() > 0) {
            System.out.println(cabecera + cResMapeado
                    + "     " + "    __ Directorios nuevos ___");
            for (String fn : dirNew) {
                System.out.println(cabecera + cResMapeado
                        + "             " + "  New --> " + fn);
            }
        }

        if (dirNotChange.size() > 0) {
            System.out.println(cabecera + cResMapeado
                    + "     " + "    __ Directorios Sin Cambios ___");
            for (String fsc : dirNotChange) {
                System.out.println(cabecera + cResMapeado
                        + "             " + "  Not Change --> " + fsc);
            }
        }
        if (dirDelete.size() > 0) {
            System.out.println(cabecera + cResMapeado
                    + "     " + "    __ Directorios eliminados ___");
            for (String fe : dirDelete) {
                System.out.println(cabecera + cResMapeado
                        + "             " + "  Delete --> " + fe);
            }
        }
        if (dirCreate.size() > 0) {
            System.out.println(cabecera + cResMapeado
                    + "     " + "    __ Directorios creados ___");
            for (String fc : dirCreate) {
                System.out.println(cabecera + cResMapeado
                        + "             " + "  Creados --> " + fc);
            }
        }
    }

//  ======================================================================
//    =    OPERACIONES     DB  =
//  ======================================================================
//  *   |-->    cargarDirectorio    
//        [Cargamos los datos de la DB en --> this.directirio]  
    private boolean cargarDirectorio() {
        directorio.clear();
        List<MyDirectori_Hibernate> listMyDirectori_Hibernate
                = h.get("from MyDirectori_Hibernate", MyDirectori_Hibernate.class);
        if (listMyDirectori_Hibernate.size() > 0) {
            for (MyDirectori_Hibernate md : listMyDirectori_Hibernate) {
                directorio.add(md);
            }
            if (directorio.size() > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean cargarArchivo() {
        archivo.clear();
        List<MyFile_Hibernate> listMyFile_Hibernate
                = h.get("from MyFile_Hibernate", MyFile_Hibernate.class);
        if (listMyFile_Hibernate.size() > 0) {
            for (MyFile_Hibernate md : listMyFile_Hibernate) {
                archivo.add(md);
            }
            if (archivo.size() > 0) {
                return true;
            }
        }
        return false;

    }

    private boolean updateThisDirectori(MyDirectori_Hibernate oldFile, int state) {
        if (directorio != null) {
            for (MyDirectori_Hibernate mf : this.directorio) {
                // si el archivo no existe
                if (mf.getUrl().equals(oldFile.getUrl())) {
                    mf.setState(state);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean addDirectori(MyDirectori_Hibernate oldFile, int state) {
        if (directorio != null) {
            oldFile.setState(state);
            return directorio.add(oldFile);
        }
        return false;
    }

    private boolean updateThisArchivo(MyFile_Hibernate oldFile, int state) {
        if (archivo != null) {
            for (MyFile_Hibernate mf : this.archivo) {
                // si el archivo no existe
                if (mf.getUrl().equals(oldFile.getUrl())) {
                    mf.setState(state);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean addThisArchivo(MyFile_Hibernate oldFile, int state) {
        if (archivo != null) {
            oldFile.setState(state);
            return archivo.add(oldFile);
        }
        return false;
    }

    private boolean updateThisArchivo(
            MyFile_Hibernate oldFile, MyFile_Hibernate newFile, int state) {
        if (archivo != null) {
            for (MyFile_Hibernate mf : this.archivo) {
                // si el archivo no existe
                if (mf.getUrl().equals(oldFile.getUrl())) {
                    mf.setName(newFile.getName());
                    mf.setMyDictori(newFile.getMyDictori());
                    mf.setDatos(newFile.getDatos());
                    mf.setState(state);
                    return true;
                }
            }
        }
        return false;
    }

}// FIN Mapear_driver
