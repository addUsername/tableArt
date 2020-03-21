/*
A ver... este programa deberia pillar una imagen y :
1.- Obtener el array de colores por pixel
    En ppio aqui seria solo meterle el file a un objeto del tipo bufferedImage con imageIO.read(File.jpg);
    Lo hacemos y miramos lo metodos que tiene

    Ya tenemos el array, un array de Color, ahora miramos la distancia de uno a otro y ver ya como traducirlo a HTML
__________________________
    Nuevo array

2.- Simplificarlo.. esto es lo chungi, agrupar columnas de colores similares (colspan)
    primero simplificamos los numeros254 -> 255; 257 sea dividimos por /100 y 
3.- Crear el html
 */
package tableart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author SERGI
 */
public class TableArt {

    
    public static class hilo extends Thread {
        
        private final int y1;
        private final int y2;
        private final BufferedImage[] file;
        public Imagen jpg;
        
        public hilo(int y1,int y2, BufferedImage [] jpg){
            
            this.y1=y1;
            this.y2=y2;
            this.file=jpg;
            
        }
        @Override
        public String toString(){
            return jpg.string;
        }
        
        @Override
        public void run(){
            
            //System.out.println("INICIAMOS LOS RUNS " + currentThread().getName());
            jpg = new Imagen(file);
            jpg.inicialize(y1, y2);
        }   
    }
    
    public static void main(String[] args) {
        
        Long startTime = System.nanoTime();
        File [] file ={ new File ("primer nivel\\final\\ejemplo2PEQ11.jpg"),new File ("primer nivel\\final\\ejemplo2PEQ11WHITE.jpg")};
        
        
        try {
            //quitar en en futuro
            BufferedImage [] jpg = {ImageIO.read(file[0]),ImageIO.read(file[1])};
            int y = (int) jpg[0].getHeight()/4;
            
            //iniciamos los hilos e intentamos acceder a sus .string
            
            Thread thread0 = new hilo(0,y,jpg);
            Thread thread1 = new hilo(y+1,y*2,jpg);
            Thread thread2 = new hilo(y*2+1,y*3,jpg);
            Thread thread3 = new hilo(y*3+1,jpg[0].getHeight(),jpg);
            
            //System.out.println("INICIAMOS HILOS");
            thread0.start();
            thread1.start();
            thread2.start();
            thread3.start();
            //System.out.println("JOINS");
            thread0.join();
            thread1.join();
            thread2.join();
            thread3.join();
            
            //Vale ya solo me queda crear el html y dejar esto mas bonito
            String inicial ="<!doctype html >\n" +
                            "<html><body>\n" +
                            "<link href=\"normailize.css\" rel=\"stylesheet\" type=\"text/css\"/>\n" +
                            "<script type=\"text/javascript\">\n" +
                            "function is1(){alert('1 pulsado')};\n" +
                            "function is2(){alert('2 pulsado')};\n" +
                            "function is3(){alert('3 pulsado')};\n" +
                            "function is4(){alert('4 pulsado')};\n" +
                            "</script>\n<table border=0 cellpadding=0 cellspacing=0 >\n";
            String end = "\n</table></body></html>";
            //Esto es un fix rapido para que no modifique la anchura
            String firstRow = "\n<tr height=1>";
            for(int i = 0; i<jpg[0].getWidth() ;i++) firstRow+="<td width=1></td>";
            firstRow+="</tr>\n";
            
            File output = new File("output.html");            
            FileWriter fw = new FileWriter(output);
            
            fw.write(inicial+firstRow+thread0.toString()+"\n"+thread1.toString()+"\n"+thread2.toString()+"\n"+thread3.toString()+end);
            fw.close();
            
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(TableArt.class.getName()).log(Level.SEVERE, null, ex);
        }
        Long totalTime = System.nanoTime();
        System.out.println("tardo en total: " + (totalTime-startTime)/1000000);
    }
    
}
