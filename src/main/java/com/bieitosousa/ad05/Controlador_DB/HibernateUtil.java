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
//import  com.bieitosousa.ad03_db.Json.JSonMake;


import com.bieitosousa.ad05.Controlador_Json.*;
import com.bieitosousa.ad05.Controlador_Mapear.*;
import com.bieitosousa.ad05.Controlador_Notificaciones.*;
import com.bieitosousa.ad05.Modelos.*;
import java.io.File;
import java.util.List;
//import com.bieitosousa.ad03_db.Json.Provincia;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil  {

    static SessionFactory sessionFact = null;
    
    static Transaction tran = null;
    private static HibernateUtil hu;
    private static final SessionFactory sessionFactoryBuid = startSessionFactory();
    private static final Session session = sessionFactoryBuid.openSession();

    public static File getElement(int parseInt, Class<File> aClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private HibernateUtil() {

    }

    public static HibernateUtil getInstance() {
        if (hu == null) {
            hu = new HibernateUtil();
            
        }
        return hu;
    }

    public static SessionFactory getSessionFactory() {
        return getInstance().sessionFactoryBuid;
    }

    
    
    //Este método devolve a sesión para poder facer operacións coa base de datos
    private static SessionFactory startSessionFactory() {
        SessionFactory sessionFactory = null;
        //Se a sesion non se configurou, creamolo
        if (sessionFactoryBuid == null) {
            try {
                Configuration conf = new Configuration();

                //Engadimos as propiedades
                Properties settings = new Properties();

                //Indicamos o conector da base de datos que vamos a usar
//                settings.put(Environment.DRIVER,"com.mysql.cj.jdbc.Driver");
                Object put = settings.put(Environment.DRIVER, JSonMake.getHibernate().getDriver());

                //Indicamos a localización da base de datos que vamos a utilizar
//                settings.put(Environment.URL,"jdbc:mysql://192.168.56.101:3306/hibernate");
                String timezone = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
                settings.put(Environment.URL, "jdbc:postgresql://" + JSonMake.getDbConnection().getAddress() + ":" + JSonMake.getDbConnection().getPort() + "/" + JSonMake.getDbConnection().getName()+timezone);
 //             settings.put(Environment.URL,"jdbc:mysql://192.168.56.101:3306/hibernate?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
                //Indicamos o usuario da base de datos con cal nos vamos conectar e o seu contrasinal
//                settings.put(Environment.USER,"userhibernate");
                settings.put(Environment.USER, JSonMake.getDbConnection().getUser());
//                settings.put(Environment.PASS,"abc123.");
                settings.put(Environment.PASS, JSonMake.getDbConnection().getPassword());

                //Indicamos o dialecto que ten que usar Hibernate 
//                settings.put(Environment.DIALECT,"org.hibernate.dialect.MySQL5Dialect");
                settings.put(Environment.DIALECT, JSonMake.getHibernate().getDialect());

                //Indicamos que se as táboas todas se borren e se volvan crear
                settings.put(Environment.HBM2DDL_AUTO, JSonMake.getHibernate().getHBM2DDL_AUTO());

                //Indicamos que se mostre as operacións SQL que Hibernate leva a cabo
//                settings.put(Environment.SHOW_SQL, "true");
                settings.put(Environment.SHOW_SQL, JSonMake.getHibernate().getSHOW_SQL());
                conf.setProperties(settings);

                //Engaidmos aquelas clases nas que queremos facer persistencia
                conf.addAnnotatedClass(MyFile_Hibernate.class);
                conf.addAnnotatedClass(MyDirectori_Hibernate.class);
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(conf.getProperties()).build();
                sessionFactory = conf.buildSessionFactory(serviceRegistry);

            } catch (ConstraintViolationException ex) {
                System.out.println("valores introducidos incorectamente " + ex.toString());

            } catch (HibernateException e) {
                e.printStackTrace();

            }
        }
        return sessionFactory;

    }

    public <T> boolean view(String consulta, Class<T> c) {
        try {

//            if ((session = getSession()) != null) {
                List<T> objList = (List< T>) session.createQuery(consulta, c).getResultList();
                        
                for (Object aux : objList) {
                    System.out.println(aux.toString());
                }
                
//                session.close();
//            }

        } catch (Exception e) {
            System.out.println("Error Global: VIEW{" + c.toString() + "} Mensaje de Error : [  " + e.toString() + "]");
            return false;
        } finally {
//            session.close();
        }
        return false;
    }

    public <T> List<T> get(String consulta, Class<T> c) {
        List<T> objList = null;
        try {
//            if ((session = getSession()) != null) {
                objList = (List< T>) session.createQuery(consulta, c).getResultList();
//            }
        } catch (Exception e) {
            System.out.println("Error Global: GET{" + c.toString() + "} Mensaje de Error : [  " + e.toString() + "]");

        } finally {
//            session.close();
        }
        return objList;
    }

    public boolean add(Object obj) {
        try {
            //Collemos a sesión de Hibernate

//            if ((session = getSession()) != null) {
                //Comenzamos unha transacción
                if ((tran = session.beginTransaction()) != null) {
                    //Gardamos o equipo
                    session.save(obj);
                    //Facemos un commit da transacción
                    tran.commit();

                    return true;
                }
//            }
        } catch (Exception e) {
            if (tran != null) {
                tran.rollback();
            }
            System.err.println("Error Global: ADD{" + obj.toString() + "} Mensaje de Error : [  " + e.toString() + "]");
            return false;
        } finally {
//            session.close();
        }
        return false;
    }

    public boolean delete(Object obj) {
        try {
            //Collemos a sesión de Hibernate

//            if ((session = getSession()) != null) {
                //Comenzamos unha transacción
                if ((tran = session.beginTransaction()) != null) {
                    //Gardamos o equipo
                    session.delete(obj);
                    //Facemos un commit da transacción
                    tran.commit();
//                    session.close();
                    return true;
                }
//            }
        } catch (Exception e) {
            if (tran != null) {
                tran.rollback();
            }
            System.out.println("Error Global: DELETE{" + obj.toString() + "} Mensaje de Error : [  " + e.toString() + "]");
            return false;
        } finally {
//            session.close();
        }
        return false;
    }

    public boolean update(Object obj) {
        try {
            //Collemos a sesión de Hibernate
//            if ((session = getSession()) != null) {
                //Comenzamos unha transacción
                if ((tran = session.beginTransaction()) != null) {
                    //Gardamos o equipo
                    session.update(obj);
                    //Facemos un commit da transacción
                    tran.commit();
//                    session.close();
                    return true;
//                }
            }

        } catch (Exception e) {
            if (tran != null) {
                tran.rollback();
            }
            System.out.println("Error Global: UPDATE{" + obj.toString() + "} Mensaje de Error : [  " + e.toString() + "]");
            return false;
        } finally {
//            session.close();
        }
        return false;
    }

       public <T, U> T getElement(U id, Class<T> type) {
        if (id != null) {
            return session.find(type, id);
        } else {
            return null;
        }
       }

 


}
