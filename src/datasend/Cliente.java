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

            Socket cl = new Socket(InetAddress.getByName(host), port);

            cl.setTcpNoDelay(!nagle); //Activa o desactiva el algoritmo de Nagle

            byte[] b = new byte[buff_size];

            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

            dos.writeInt(buff_size);
            dos.flush();
            dos.writeInt(f.length);
            dos.flush();

            progreso = new Progreso();

            total = 0;
            for (File file : f) {
                progreso.appendRow(file.getName(), humanReadableByteCountSI(file.length()));
               
                total += file.length();
            }
            progreso.update(progreso.getGraphics());
            new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    enviados2 = 0;
                    for (File file : f) {
                        enviados = 0;

                        DataInputStream dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
                        byte[] nombreB = file.getName().getBytes(StandardCharsets.UTF_8);
                        String nombre = new String(nombreB,"UTF-8");
                        dos.writeUTF(nombre);
                        dos.flush();
                        
                        dos.writeLong(file.length());
                        dos.flush();

                        progreso.enviandoSetLabel("Enviando: " + file.getName());

                        while (enviados < file.length()) {
                            n = dis.read(b);
                            dos.write(b, 0, n);
                            dos.flush();

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

                    }
                    
                    
                    dos.close();
                    cl.close();
                    return null;

                }
            }.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
