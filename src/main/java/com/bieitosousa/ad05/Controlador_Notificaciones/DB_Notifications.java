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
package com.bieitosousa.ad05.Controlador_Notificaciones;

import com.bieitosousa.ad05.Controlador_Json.*;
import com.bieitosousa.ad05.Controlador_Mapear.*;
import com.bieitosousa.ad05.Controlador_Notificaciones.*;
import com.bieitosousa.ad05.Controlador_DB.*;
import com.bieitosousa.ad05.Modelos.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

/**
 *
 * @author bieito
 */
public class DB_Notifications extends Thread {
    PrintStream originalPrintStream = System.out;
    private static File fileLog = null;
    private static PrintStream stream= null;
    PGConnection pgconn;
    DB_driver_Util driver = DB_driver_Util.getInstance();
    String raiz = "";
    String cabecera = "      [[[[ =  [[[[ NOTIFICACIONES ]]]]] "
            + "= [[[[" + raiz + "]]]]    ";
    private String sqlFunccion
            = "CREATE OR REPLACE FUNCTION notificar_mensaxe() "
            + "RETURNS trigger AS $$ "
            + "BEGIN "
            + "PERFORM pg_notify('nuevofichero',NEW.MyFile_Hibernate_id::text); "
            + "RETURN NEW; "
            + "END; "
            + "$$ LANGUAGE plpgsql;";

    private String sqlTrigger
            = "DROP TRIGGER IF EXISTS notif_nuevo_fichero ON MyFile__Hibernate; "
            + "CREATE TRIGGER notif_nuevo_fichero "
            + "AFTER INSERT "
            + "ON MyFile__Hibernate "
            + "FOR EACH ROW "
            + "EXECUTE PROCEDURE notificar_mensaxe(); ";

//  ======================================================================
//    =    Constructores  =
//  ======================================================================
//
    
    public DB_Notifications() {
        HibernateUtil.getInstance();
        addFileLog();

        if (driver.sqlFunccion(sqlFunccion)) {
            System.out.println(" SQLFunccion [CORRECTA]");
        } else {
            System.out.println(" SQLFunccion [ERROR]");
        }
        if (driver.sqlTrigger(sqlTrigger)) {
            System.out.println(" SQLTRIGGER [CORRECTA]");

        } else {
            System.out.println(" SQLTRIGGER [ERROR]");
        }
        openChanel();

    }

    public DB_Notifications(String raiz) {
        HibernateUtil.getInstance();
        addFileLog();
        this.raiz = raiz;
        File fraiz = null;
        if ((fraiz = new File(raiz)) != null) {
            if (!fraiz.exists()) {
                if (fraiz.mkdirs()) {
                    System.out.println(cabecera
                            + "[[[[ CREADO NUEVO DIRECTORIO " + fraiz.getPath() + " ]]]");
                }
            }
        }

        if (driver.sqlFunccion(sqlFunccion)) {
            System.out.println(cabecera + " SQLFunccion [CORRECTA]");
        } else {
            System.err.println(cabecera + " SQLFunccion [ERROR]");
        }
        if (driver.sqlTrigger(sqlTrigger)) {
            System.out.println(cabecera + " SQLTRIGGER [CORRECTA]");

        } else {
            System.err.println(cabecera + " SQLTRIGGER [ERROR]");
        }
        openChanel();

    }

    
//  ======================================================================
//    =    OPERACIONES   Crear  Archivo  =  {notificaciones.log}  =
//  ======================================================================
//
//  *   |-->    addFileLog    
//        [ Cambia System.out a una salida en el archivo notificaciones.log ]
//  *   |-->    getCreateFileLog    
//        [ Crea si no existe el archivo notificaciones.log ]
//  *   |-->   CreateSteamLog() {
//        [Crea el PrintStream que se le pasara a System.out  ]
//            
    private void addFileLog() {
        CreateSteamLog();
        System.setOut(stream);
    }

    private File getCreateFileLog() {
        if (fileLog == null) {
            DB_Notifications.fileLog = new File("notificaciones.log");
            try {
                fileLog.createNewFile();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(Mapear_Hibernate.class.getName())
                        .log(Level.SEVERE, null, ex);
                return null;
            } catch (IOException ex) {
                Logger.getLogger(Mapear_Hibernate.class.getName())
                        .log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return fileLog;
    }

    private PrintStream CreateSteamLog() {
        if (stream == null) {
            try {
               DB_Notifications.fileLog=getCreateFileLog();
               System.setOut(originalPrintStream);
               System.out.println(
                       "NOTIFICACIONES >>> salida redirigiada a archivo ["
                               +fileLog.getPath()+"]");
                DB_Notifications.stream = new PrintStream(
                        DB_Notifications.fileLog);
                 
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Mapear_Hibernate.class.getName())
                        .log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return DB_Notifications.stream;

    }

    private void openChanel() {
        try {
            pgconn = driver.getConn().unwrap(PGConnection.class);
            try (Statement stmt = driver.getConn().createStatement()) {
                stmt.execute("LISTEN nuevofichero");
            }
            System.out.println(cabecera + "Esperando mensaxes...");

        } catch (SQLException ex) {
            Logger.getLogger(DB_Notifications.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                PGNotification notifications[] = pgconn.getNotifications();
                if (notifications != null) {
                    System.out.println("hay notificaciones ... ");
                    for (int i = 0; i < notifications.length; i++) {
                        int id = Integer.parseInt(notifications[i]
                                .getParameter());
                        System.out.println(cabecera + "recibido el id = [" 
                                + id + "]");
                        MyDirectori_Hibernate dh = null;
                        MyFile_Hibernate fh = null;
                        if ((fh = (HibernateUtil.getInstance()
                                .getElement(Integer.parseInt(notifications[i]
                                        .getParameter()),
                                        MyFile_Hibernate.class))) != null) {
                            fh.getMyDictori().setRaiz(raiz);
                            new Mapear_Hibernate().createDeleteFile((fh));
                             System.out.println(cabecera + "creado = [" 
                                + fh+ "]");
                        }

                    }

                }
                Thread.sleep(500);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(DB_Notifications.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DB_Notifications.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

    }
}
