package com.bieitosousa.ad05.Controlador_Json;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.File;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map.Entry;

import java.io.FileReader;
import java.io.UnsupportedEncodingException;

import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.nio.cs.StandardCharsets;

public class JSonMake {

//    public static void main(String[] args) throws Exception {
//    ReadObjJsonInFileProvincias();
//    }
    private static DbConnection connConf = null;
    private static Hibernate hConf = null;
    private static App appConf = null;
    private static ConfigDB configDB = null;
    static final File fconfig = new File("." + File.separator + "config.json");

    public static File getFconfig() {
        return fconfig;
    }

    public static DbConnection getDbConnection() {
        if (connConf != null) {
            return connConf;
        } else {
            ReadConfigDBJsonInFile(fconfig);
            return connConf;
        }
    }

    public static Hibernate getHibernate() {

        if (hConf != null) {
            return hConf;
        } else {
            ReadConfigDBJsonInFile(fconfig);
            return hConf;
        }

    }

    public static App getApp() {

        if (appConf != null) {
            return appConf;
        } else {
            ReadConfigDBJsonInFile(fconfig);
            return appConf;
        }

    }

    public static ConfigDB getConfigDB() {

        if (configDB != null) {
            return configDB;
        } else {
            ReadConfigDBJsonInFile(fconfig);
            return configDB;
        }

    }

    /**
     *
     * @param objectFile
     * @return
     */
    public static Object ReadConfigDBJsonInFile(File objectFile) {
        Gson gson = new Gson();
        ConfigDB objaux = null;
        System.out.println("mirando la configuracion");
        try (Reader reader = new FileReader(objectFile)) {
            System.out.println("extrallendo configuracion");
            // RECUPERO un objeto de tipo Config
            objaux = gson.fromJson(reader, ConfigDB.class);
            System.out.println("mostrando objeto auxiliar");
            System.out.println(objaux);
            // RECUPERO los objetos dentro de Tipo ConfigDB
            connConf = objaux.getDbConnection();
            hConf = objaux.getHibernate();
            appConf = objaux.getApp();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return objaux;
    }

    public static void main(String[] arg) {
       System.out.println(" ------- CONEXION --------------");
        System.out.println(getDbConnection().getName());
        System.out.println(getDbConnection().getAddress());
        System.out.println(getDbConnection().getPort());
        System.out.println(getDbConnection().getPassword());;
        System.out.println(getDbConnection().getUser());
       System.out.println(" ------- HIBERNATE --------------");  
        System.out.println(getHibernate().getDialect());
        System.out.println(getHibernate().getDriver());
        System.out.println(getHibernate().getHBM2DDL_AUTO());
        System.out.println(getHibernate().getSHOW_SQL());
       System.out.println(" ------- APP --------------");  
        System.out.println(getApp().getDirectory());

    }
    
    public static class ConfigDB {

        private DbConnection dbConnection;
        private Hibernate hibernate;
        private App app;

        public ConfigDB(DbConnection dbConnection, Hibernate hibernate, App app) {
            this.dbConnection = dbConnection;
            this.hibernate = hibernate;
            this.app = app;

        }

        public DbConnection getDbConnection() {
            return dbConnection;
        }

        public void setDbConnection(DbConnection dbConnection) {
            this.dbConnection = dbConnection;
        }

        public Hibernate getHibernate() {
            return hibernate;
        }

        public void setHibernate(Hibernate hibernate) {
            this.hibernate = hibernate;
        }

        public App getApp() {
            return app;
        }

        public void setApp(App app) {
            this.app = app;
        }

        @Override
        public String toString() {
            return "ConfigDB{" + "dbConnection=" + dbConnection + ", hibernate=" + hibernate + ", app=" + app + '}';
        }

    }

    public static class DbConnection {

        private String address;
        private String port;
        private String name;
        private String timezone;
        private String user;
        private String password;

        public DbConnection(String address, String port, String name, String timezone, String user, String password) {
            this.address = address;
            this.port = port;
            this.name = name;
            this.timezone = timezone;
            this.user = user;
            this.password = password;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "DbConnection{" + "address=" + address + ", port=" + port + ", name=" + name + ", timezone=" + timezone + ", user=" + user + ", password=" + password + '}';
        }

    }

    public static class Hibernate {

        private String driver;
        private String dialect;
        private String HBM2DDL_AUTO;
        private boolean SHOW_SQL;

        public Hibernate(String driver, String dialect, String HBM2DDL_AUTO, boolean SHOW_SQL) {
            this.driver = driver;
            this.dialect = dialect;
            this.HBM2DDL_AUTO = HBM2DDL_AUTO;
            this.SHOW_SQL = SHOW_SQL;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getDialect() {
            return dialect;
        }

        public void setDialect(String dialect) {
            this.dialect = dialect;
        }

        public String getHBM2DDL_AUTO() {
            return HBM2DDL_AUTO;
        }

        public void setHBM2DDL_AUTO(String HBM2DDL_AUTO) {
            this.HBM2DDL_AUTO = HBM2DDL_AUTO;
        }

        public String getSHOW_SQL() {
            return String.valueOf(SHOW_SQL);
        }

        public boolean isSHOW_SQL() {
            return SHOW_SQL;
        }

        public void setSHOW_SQL(boolean SHOW_SQL) {
            this.SHOW_SQL = SHOW_SQL;
        }

        @Override
        public String toString() {
            return "Hibernate{" + "driver=" + driver + ", dialect=" + dialect + ", HBM2DDL_AUTO=" + HBM2DDL_AUTO + ", SHOW_SQL=" + SHOW_SQL + '}';
        }

    }

    public static class App {

        private String directory;

        public App(String directory) {
            this.directory = directory;
        }

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        @Override
        public String toString() {
            return "App{" + "directory=" + directory + '}';
        }
    }

}
