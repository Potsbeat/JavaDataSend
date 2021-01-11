/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Servidor {
   
    public static void main(String[] args) {
        try {
            // Creamos el socket
            ServerSocket s = new ServerSocket(7000);
            // Iniciamos el ciclo infinito del servidor
            for (;;) {
                // Esperamos una conexiÃ³n 
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde" + cl.getInetAddress() + ":" + cl.getPort());
                /*
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                int flujo = dis.available();
                
                System.out.print(" flujo: " + flujo + "\n");
                int buff_size = dis.readInt();
                int n_archivos = dis.readInt();
                byte[] b = new byte[buff_size];
                */
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                //int n_archivos = 1;
                
                //for(i =0; i<n_archivos; i++){
                //while(true) { // se va a tronar cuando ya no hayan archivos
                    //**********************************************************************
                    //DataInputStream dis = new DataInputStream(cl.getInputStream());
                    String nombre = "cualquier cosa";
                    try{
                        nombre  = dis.readUTF();
                    }catch(UTFDataFormatException e){
                        System.out.print("que cachamos con 1.UTF " + nombre + "\n");
                    }
                    FileOutputStream fos = new FileOutputStream(nombre);
                    
                    int buff_size = dis.readInt();
                    //int n_archivos = dis.readInt();
                    
                    byte[] b = new byte[buff_size];
                    //**********************************************************************
                    long tam = 0;
                    System.out.println("Recibimos el archivo:" + nombre);
                    tam = dis.readLong();// lectura del archivo // tamaño del archivo
                    
                    //DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));
                    long recibidos = 0;
                    int n, porcentaje;
                    while ((n = dis.read(b, 0, Math.min(b.length, (int)tam)))>0) {
                        //n = dis.read(b);
                        fos.write(b, 0, n);
                        fos.flush();
                        fos.flush();
                        recibidos = recibidos + n;
                        porcentaje = (int) (recibidos * 100 / tam);
                        System.out.print("Recibido: " + porcentaje + "%\r");
                    }//While
                    System.out.print("\n\nArchivo recibido.\n");
                    fos.close();
                    dis.close();
                //}//For
                cl.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }
}
