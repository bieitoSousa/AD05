/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bieitosousa.ad05;

import com.bieitosousa.ad05.Controlador_Mapear.*;
import com.bieitosousa.ad05.Controlador_Notificaciones.*;



/**
 *
 * @author bieito
 */
public class Main {
    public static void main(String[] args) throws Exception {
      ViewRecordsDB vr = new ViewRecordsDB();
      DB_Notifications nt = new DB_Notifications("raiz_notificaciones");
     vr.start();
      nt.start();
}
}