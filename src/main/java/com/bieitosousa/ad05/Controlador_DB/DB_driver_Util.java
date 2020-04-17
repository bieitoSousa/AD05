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
package com.bieitosousa.ad05.Controlador_DB;

/**
 *
 * @author bieito
 */


import com.bieitosousa.ad05.Controlador_Json.*;
import com.bieitosousa.ad05.Controlador_Mapear.*;
import com.bieitosousa.ad05.Controlador_Notificaciones.*;
import com.bieitosousa.ad05.Modelos.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;


public class DB_driver_Util {

    private static String name = null, address = null, password = null, user = null;
    private final Connection conn = start();
    private static DB_driver_Util drive = null;

    protected DB_driver_Util() {
        start();
    }

   

   public static DB_driver_Util getInstance() {
        if (drive == null) {
            drive = new DB_driver_Util();
        }
        return drive;
    }

    public Connection getConn() {
        return conn;
    }

     protected static void finishDB() {
        drive = null;
    }

    private Connection start() {
        if (((DB_driver_Util.name = JSonMake.getDbConnection().getName()) != null)
                && ((DB_driver_Util.address = JSonMake.getDbConnection().getAddress()) != null)
                && ((DB_driver_Util.password = JSonMake.getDbConnection().getPassword()) != null)
                && ((DB_driver_Util.user = JSonMake.getDbConnection().getUser()) != null)) {
            try {

                //Indicamos as propiedades da conexión
                Properties props = new Properties();
                props.setProperty("user", user);
                props.setProperty("password", password);

                //Dirección de conexión a base de datos
                String postgres = "jdbc:postgresql://" + address + "/" + name;

                //Creamos la connexion
                System.out.println(" Enviamos la conexion");
                return DriverManager.getConnection(postgres, props);
            } catch (SQLException ex) {
                System.out.println("PARAMETROS DE CONEXION INCORRECTOS");
                System.err.println("Error: " + ex.toString());
            }
        } else {
            System.out.println("PARAMETROS DE CONEXION INCORRECTOS");
        }
        return null;
    }

    protected boolean stament(String sql) {
        try {
            /* String sql = "CREATE TABLE IF NOT EXISTS person (\n" +
                    "id integer PRIMARY KEY,\n"+
                    "nome text NOT NULL\n"+
                    ");";
             */
            Statement stmt = getConn().createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage() + " SQL FALLIDO {{______\n " + sql + "\n______}}");
            return false;
        } finally {
            DB_driver_Util.finishDB();
        }
        return true;
    }
    
    public boolean sqlFunccion(String sqlFucction) {
        try {
            //Creamos a sentencia SQL para crear unha función
            //NOTA: nón é moi lóxico crear funcións dende código. Só o fago para despois utilizala
//                String sqlCreateFucction = new String(
//                        "CREATE OR REPLACE FUNCTION inc(val integer) RETURNS integer AS $$ "
//                        + "BEGIN "
//                        + "RETURN val + 1; "
//                        + "END;"
//                        + "$$ LANGUAGE PLPGSQL;");

            //Executamos a sentencia SQL anterior
            CallableStatement createFunction = getConn().prepareCall(sqlFucction);
            createFunction.execute();
            createFunction.close();

//            //Creamos a chamada a función
//            String sqlCallFunction = new String("{? = call inc( ? ) }");
//            CallableStatement callFunction = getConn().prepareCall(sqlCallFunction);
//
//            //O primeiro parámetro indica o tipo de datos que devolve
//            callFunction.registerOutParameter(1, Types.INTEGER);
//
//            //O segundo parámetro indica o valor que lle pasamos a función, neste exemplo 5
//            //callFunction.setInt(2, 5);
//
//            //Executamos a función
//            callFunction.execute();
//
//            //Obtemos o valor resultante da función
//            int valorDevolto = callFunction.getInt(1);
//            callFunction.close();
//
//            //Mostramos o valor devolto
//            System.out.println("Valor devolto da función: " + valorDevolto);
//
//            //Cerramos a conexión coa base de datos
//            if (getConn() != null) {
//                getConn().close();
//            }

                  return true;

        } catch (SQLException ex) {
            System.err.println("Error: " + ex.toString());
            return false;
        }catch (Exception ee){
            System.err.println("Error: " + ee.toString());
            return false;
        }
    }
    public boolean sqlTrigger(String sqlTrigger) {
        try {
            //Creamos a sentencia SQL para crear unha función
            //NOTA: nón é moi lóxico crear funcións dende código. Só o fago para despois utilizala
//                String sqlCreateFucction = new String(
//                        "CREATE OR REPLACE FUNCTION inc(val integer) RETURNS integer AS $$ "
//                        + "BEGIN "
//                        + "RETURN val + 1; "
//                        + "END;"
//                        + "$$ LANGUAGE PLPGSQL;");

            //Executamos a sentencia SQL anterior
            CallableStatement createTrigger = getConn().prepareCall(sqlTrigger);
            createTrigger.execute();
            createTrigger.close();

//            //Creamos a chamada a función
//            String sqlCallFunction = new String("{? = call inc( ? ) }");
//            CallableStatement callFunction = getConn().prepareCall(sqlCallFunction);
//
//            //O primeiro parámetro indica o tipo de datos que devolve
//            callFunction.registerOutParameter(1, Types.INTEGER);
//
//            //O segundo parámetro indica o valor que lle pasamos a función, neste exemplo 5
//            callFunction.setInt(2, 5);
//
//            //Executamos a función
//            callFunction.execute();
//
//            //Obtemos o valor resultante da función
//            int valorDevolto = callFunction.getInt(1);
//            callFunction.close();
//
//            //Mostramos o valor devolto
//            System.out.println("Valor devolto da función: " + valorDevolto);
//
//            //Cerramos a conexión coa base de datos
//            if (getConn() != null) {
//                getConn().close();
//            }
            return true;

        } catch (SQLException ex) {
            System.err.println("Error: " + ex.toString());
            return false;
        }catch (Exception ee){
            System.err.println("Error: " + ee.toString());
            return false;
        }

    }
    
   

}//FIN DB_driver_Util
