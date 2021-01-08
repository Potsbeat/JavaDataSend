/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasend;

import java.net.*;
import java.io.*;

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
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                
                int buff_size = dis.readInt();
                int n_archivos = dis.readInt();
                System.out.println("Se recibirán " + n_archivos + " archivos con un buff de "+buff_size);
                byte[] b = new byte[buff_size];
                for (int i = 0; i < n_archivos; i++) {
                    String nombre;
                    nombre = dis.readUTF();
                    
                    System.out.println("Recibimos el archivo:" + nombre);
                    long tam = dis.readLong();
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));
                    long recibidos = 0;
                    int n, porcentaje;
                    while (recibidos < tam) {
                        n = dis.read(b);
                        dos.write(b, 0, n);
                        dos.flush();
                        recibidos = recibidos + n;
                        porcentaje = (int) (recibidos * 100 / tam);
                        System.out.print("Recibido: " + porcentaje + "%\r");
                    }//While
                    System.out.print("\n\nArchivo recibido.\n");
                    dos.close();
                }
                dis.close();
                cl.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }
}
