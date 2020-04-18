/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bieitosousa.ad05;

import com.bieitosousa.ad05.Controlador_DB.*;
import com.bieitosousa.ad05.Controlador_Mapear.*;
import com.bieitosousa.ad05.Controlador_Notificaciones.*;



/**
 *
 * @author bieito
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println(
                "\n"
                + " ========================================== \n"
                + " =    =   INSTRUCIIONES   =   =   \n"
                +" ========================================== \n"
                        + "[1]  =  Antes de ejecutar el Programa \n"
                        + "     1.1 Inicie una maquina virtual \n"
                        + "     1.2 http://192.168.56.102/phppgadmin \n"
                        + "     1.3 cree la db \"minidriver\" \n"
                        + "[2]  = Inicie el Programa \n"
                        + "     2.1 Creara los siguientes directorios \n"
                        + "         2.1.1 [raiz_mapear] \n"
                        + "         2.1.2 [raiz_notificaciones] \n"
                        + "      2.1 Creara los siguientes archivos \n"
                        + "         2.1.1 [mapear.log] -> Registra los mensajes de Mapeado \n"
                        + "         2.1.2 [notificaciones.log]  -> Registra los mensajes de Notificaciones \n"
                        +"                 despues de la primera vez se registra en [Mapeado.log] {cntr+f 'notificaciones'}\n"
                        + "[3] = Crea carapetas y archivos dentro del directorio [raiz_mapear] \n"
                        + "    = Estos se replicaran en  [raiz_notificaciones] \n"
                        + "[4] = consulta [mapear.log] y [notificaciones.log] para ver las operaciones realizadas por el programa \n"
                        + "\n"
                        + "\n"
                        + "\n"
        );
        
      ViewRecordsDB vr = new ViewRecordsDB();
      DB_Notifications nt = new DB_Notifications("raiz_notificaciones");
      HibernateUtil.getInstance();
      nt.start();
      vr.start();

}
}