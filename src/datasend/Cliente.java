/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
            for(File file : f){
                System.out.print("Se ha seleccionado : " + file.getName());
            }
            

//********************
            /*
            Socket cl = new Socket(InetAddress.getByName(host), port);
            cl.setTcpNoDelay(!nagle); //Activa o desactiva el algoritmo de Nagle
            byte[] b = new byte[buff_size];
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            FileInputStream fis = new FileInputStream(file.getName());
            dos.writeInt(buff_size); // tamaño del buffer
            System.out.print("hemos escrito 4 bytes en el flujo de salida - tamaño del buffer\n");
            dos.flush();
            //dos.flush();
            dos.writeInt(f.length); // numero de archivos 
            System.out.print("hemos escrito 4 bytes mas en el flujo de salida - numero de archivos\n");
            dos.flush();
            //dos.flush();
            */
//************************
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
                       
                        //**************************
                        Socket cl = new Socket(InetAddress.getByName(host), port);
                        cl.setTcpNoDelay(!nagle); //Activa o desactiva el algoritmo de Nagle
                        byte[] b = new byte[buff_size];
                        DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                        
                        //dos.writeInt(f.length); // numero de archivos 
                        //System.out.print("hemos escrito 4 bytes mas en el flujo de salida - numero de archivos\n");
                        //dos.flush();
                        
                        dos.writeUTF(file.getName());
                        dos.flush();
                        
                        dos.writeInt(buff_size); // tamaño del buffer
                        System.out.print("hemos escrito 4 bytes en el flujo de salida - tamaño del buffer\n");
                        dos.flush();
                        //dos.flush();
                        
                        //dos.flush();
                        System.out.print("Archivo " + file.getName() + "\n");
                        System.out.print("De tamaño  " + file.length() + "\n");
                        enviados = 0;
                        
 
                        dos.writeLong(file.length());
                        dos.flush();
                        
                        //DataInputStream dis = new DataInputStream(new FileInputStream(file.getAbsolutePath()));
                        //dos.flush();
                        //**************************
                        progreso.enviandoSetLabel("Enviando: " + file.getName());
                        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
                        //while (fis.read(b)>0) {
                        while (enviados < file.length()) {
                            n = fis.read(b);
                            dos.write(b, 0, n);
                            dos.flush();
                            //dos.flush();
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
                        if(enviados != file.length()){
                            System.out.println("o faltan o sobran bytes por mandar");
                        }else{
                            System.out.println("se han enviado todos los bytes");
                        }
                        System.out.println("");
                        System.out.print("\n\nArchivo enviado");
                        fis.close();
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
