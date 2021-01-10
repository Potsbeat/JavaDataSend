/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasend;

import javax.swing.JFileChooser;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import javax.swing.SwingWorker;

public class Cliente {

    boolean done = false;
    Progreso progreso;
    long enviados, enviados2, total;
    int n;
    int porcentaje, porcentaje2;

    public Cliente(String host, int port, int buff_size, boolean nagle) {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        int r = chooser.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            conectar(host, port, buff_size, nagle, chooser.getSelectedFiles());
        }

    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public void conectar(String host, int port, int buff_size, boolean nagle, File[] f) {
        try {
            /*
            Socket cl = new Socket(InetAddress.getByName(host), port);

            cl.setTcpNoDelay(!nagle); //Activa o desactiva el algoritmo de Nagle

            byte[] b = new byte[buff_size];

            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            System.out.print("Vamos a volver a abrir dataOutoutstream en cliente\n");
            dos.writeInt(buff_size);
            System.out.print("Antes del flush - Ver cuantos bytes lleva el flujo de salida del cliente: " + dos.size());
            dos.flush();
            dos.flush();
            System.out.print("Despues del flush - Ver cuantos bytes lleva el flujo de salida del cliente: " + dos.size());
            dos.writeInt(f.length);
            dos.flush();
            dos.flush();

            progreso = new Progreso();

            total = 0;
            for (File file : f) {
                progreso.appendRow(file.getName(), humanReadableByteCountSI(file.length()));
               
                total += file.length();
            }
            progreso.update(progreso.getGraphics());
            */
            new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    enviados2 = 0;
                    for (File file : f) {
                        ///*******************************************************
                        Socket cl = new Socket(InetAddress.getByName(host), port);

                        cl.setTcpNoDelay(!nagle); //Activa o desactiva el algoritmo de Nagle

                        byte[] b = new byte[buff_size];

                        DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                        System.out.print("Vamos a volver a abrir dataOutoutstream en cliente\n");
                        dos.writeInt(buff_size);
                        System.out.print("Antes del flush - Ver cuantos bytes lleva el flujo de salida del cliente: " + dos.size());
                        dos.flush();
                        dos.flush();
                        System.out.print("Despues del flush - Ver cuantos bytes lleva el flujo de salida del cliente: " + dos.size());
                        dos.writeInt(f.length);
                        dos.flush();
                        dos.flush();

                        progreso = new Progreso();

                        total = 0;
                        for (File fi : f) {
                            progreso.appendRow(fi.getName(), humanReadableByteCountSI(fi.length()));

                            total += fi.length();
                        }
                        progreso.update(progreso.getGraphics());
                        ///*******************************************
                        System.out.print("archivo " + file.getName());
                        System.out.print("vamos por el envio de un archivo\n");
                        enviados = 0;

                        DataInputStream dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
                        //byte[] nombreB = file.getName().getBytes(StandardCharsets.UTF_8);
                        //String nombre = new String(nombreB,"UTF-8");
                        //dos.writeInt(file.getName().getBytes(StandardCharsets.UTF_8).length);//cuenta el numero de bytes del nombre
                        dos.writeUTF(file.getName());//escribirmos el nombre como bytes
                        System.out.print("Antes del flush - Ver cuantos bytes lleva el flujo de salida del cliente: " + dos.size());
                        dos.flush();
                        dos.flush();
                        System.out.print("Despues del flush - Ver cuantos bytes lleva el flujo de salida del cliente: " + dos.size());
                        dos.writeLong(file.length());
                        System.out.print("Antes del flush - Ver cuantos bytes lleva el flujo de salida del cliente: " + dos.size());
                        dos.flush();
                        dos.flush();
                        System.out.print("Despues del flush - Ver cuantos bytes lleva el flujo de salida del cliente: " + dos.size());
                        progreso.enviandoSetLabel("Enviando: " + file.getName());

                        while (enviados < file.length()) {
                            n = dis.read(b);
                            dos.write(b, 0, n);
                            System.out.print("Antes del flush - Ver cuantos bytes lleva el flujo de salida del cliente: " + dos.size());
                            dos.flush();
                            dos.flush();
                            System.out.print("Despues del flush - Ver cuantos bytes lleva el flujo de salida del cliente: " + dos.size());
                            enviados = enviados + n;
                            enviados2 = enviados2 + n;
                            porcentaje = (int) (enviados * 100 / file.length());
                            System.out.print("Enviado: " + porcentaje + "%\r");

                            progreso.setIndBarValue(porcentaje);
                            progreso.getProgreso_individual_bar().setString(porcentaje + "%");

                            porcentaje2 = (int) (enviados2 * 100 / total);
                            progreso.setGenBarValue(porcentaje2);
                            progreso.getProgreso_general_bar().setString(porcentaje2 + "%");

                        }//While

                        System.out.print("\n\nArchivo enviado");
                        dis.close();
                        dos.close();
                        cl.close();
                    }
                    //dos.close();
                    //cl.close();
                    return null;

                }
            }.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
