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

    PGConnection pgconn;
    DB_driver_Util driver = DB_driver_Util.getInstance();
    private String sqlFunccion
            = "CREATE OR REPLACE FUNCTION notificar_mensaxe() "
            + "RETURNS trigger AS $$ "
            + "BEGIN "
            + "PERFORM pg_notify('nuevofichero',NEW.id::text); "
            + "RETURN NEW; "
            + "END; "
            + "$$ LANGUAGE plpgsql;";

    private String sqlTrigger
            = "DROP TRIGGER IF EXISTS notif_nuevo_fichero ON files; "
            + "CREATE TRIGGER notif_nuevo_fichero "
            + "AFTER INSERT "
            + "ON files "
            + "FOR EACH ROW "
            + "EXECUTE PROCEDURE notificar_mensaxe(); ";

    public DB_Notifications() {

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

    private void openChanel() {
        try {
            PGConnection pgconn = driver.getConn().unwrap(PGConnection.class);
            Statement stmt = driver.getConn().createStatement();

            stmt.execute("LISTEN novamensaxe");
            stmt.close();
            System.out.println("Esperando mensaxes...");

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
                    for (int i = 0; i < notifications.length; i++) {
                        MyDirectori_Hibernate dh = null;
                        MyFile_Hibernate fh = null;
                        if ((fh = (HibernateUtil.getInstance()
                                .getElement(Integer.parseInt(notifications[i]
                                        .getParameter()), MyFile_Hibernate.class))) != null) {
                            new Mapear_Hibernate().createDeleteFile(fh);
                        }
                        if ((dh = (HibernateUtil.getInstance()
                                .getElement(Integer.parseInt(notifications[i]
                                        .getParameter()), MyDirectori_Hibernate.class))) != null) {
                            new Mapear_Hibernate().createDeleteFile(dh);
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
