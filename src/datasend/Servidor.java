/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasend;
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
                //BufferedInputStream bis = new BufferedInputStream(cl.getInputStream());
                //DataInputStream dis = new DataInputStream(bis);
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                
                int buff_size = dis.readInt();
                System.out.print("lectura del primer entero\n");
                int n_archivos = dis.readInt();
                System.out.print("lectura del segundo entero\n");
                System.out.println("Se recibirán " + n_archivos + " archivos con un buff de "+buff_size);
                
                byte[] b = new byte[buff_size];
                
                for (int i = 0; i < n_archivos; i++) {
                    //int n_tamanioNombreFile = dis.readInt();
                    //System.out.print("El numero de bytes que ha de leer para sacar el nombre es: " + n_tamanioNombreFile);
                    /*if(i>0){
                        buff_size = dis.readInt();
                        System.out.print("lectura del primer entero\n");
                        n_archivos = dis.readInt();
                        System.out.print("lectura del segundo entero\n");
                        System.out.println("Se recibirán " + n_archivos + " archivos con un buff de "+buff_size);
                        n_tamanioNombreFile = dis.readInt();
                    }*/
                    String nombre;
                    //byte [] nombreb = new byte [n_tamanioNombreFile];
                    //dis.read(nombreb, 0, n_tamanioNombreFile);
                    try{
                        nombre = dis.readUTF();
                    }catch(EOFException e){
                        System.out.print("HEMOS LLEGADO AL FINAL SIN LEER TODOS LOS BYTES  ?????  \n ");
                        continue;
                    }
                    System.out.print("lectura en UTF-8 nombre de " + nombre + "\n");
                    //nombre = new String(nombreb, StandardCharsets.UTF_8);
                    System.out.println("Recibimos el archivo:" + nombre);
                    System.out.print("Con " + nombre.getBytes().length + " bytes en el nombre\n");
                    long tam = dis.readLong();
                    System.out.print("lectura de un long\n");
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));
                    if(dos != null){
                        System.out.print("Encontro el archivo\n");
                    }else{
                        System.out.print("NO encontro el archivo\n");
                    }
                    System.out.print("Despues de la creacion de dos\n");
                    long recibidos = 0;
                    int n, porcentaje;
                    while (recibidos < tam) {
                        n = dis.read(b);
                        System.out.print("lectura normal\n");
                        dos.write(b, 0, n);
                        dos.flush();
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
