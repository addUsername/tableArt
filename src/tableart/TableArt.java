
package tableart;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        private final int precision;
        private final BufferedImage[] files;
        public Imagen jpg;
        
        public hilo(int y1,int precision, BufferedImage [] jpg){
            
            this.y1=y1;
            this.precision=precision;
            this.files=jpg;
            
        }
        public String toString(String css){
            return jpg.stringCss;
        }
        @Override
        public String toString(){
            return jpg.string;
        }
        
        @Override
        public void run(){
            
            //System.out.println("INICIAMOS LOS RUNS " + currentThread().getName());
            jpg = new Imagen(files,precision);
            jpg.inicialize(y1);
        }   
    }
    
    public static void main(String[] args) {
        
        Long startTime = System.nanoTime();
        //ARG[0] y ARG[1]
        File [] file ={ new File ("primer nivel\\final\\ejemplo2PEQ10.jpg"),new File ("primer nivel\\final\\ejemplo2PEQ10Area.jpg")};       
        
        try {
            //quitar en en futuro
            BufferedImage [] jpg = {ImageIO.read(file[0]),ImageIO.read(file[1])};
            int y = (int) jpg[0].getHeight()/4;
            
            //iniciamos los hilos e intentamos acceder a sus .string
            
            Thread thread0 = new hilo(0,2,jpg);
            Thread thread1 = new hilo(y+1,2,jpg);
            Thread thread2 = new hilo(y*2+1,2,jpg);
            Thread thread3 = new hilo(y*3+1,2,jpg);
            
            thread0.setName("Q1-");
            thread1.setName("Q2-");
            thread2.setName("Q3-");
            thread3.setName("Q4-");
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
            
            //se puede crear esto dinamicamente, leyendo solo las keys del hasmap
            //ARG[2]
            String js = "<script type=\"text/javascript\">\n" +
                            "function is1(){alert('1 pulsado')};\n" +
                            "function is2(){alert('2 pulsado')};\n" +
                            "function is3(){alert('3 pulsado')};\n" +
                            "function is4(){alert('4 pulsado')};\n" +
                            "</script>";            
            
            String html ="<!doctype html >\n" +
                            "<html><head>\n" +
                            "<link rel='stylesheet' type='text/css' href='normalizeNew.css'>" +
                            "</head>\n<body>\n"+
                            js+
                            "\n<table id='mytable'>\n";
            
            String end = "\n</table></body></html>";
            
            
            String inicialCss = "#mytable{\n" +
                "	border: 0px;\n" +
                "	width: "+jpg[0].getWidth()+"px;\n" +
                "	height:"+jpg[0].getHeight()+"px;\n" +
                "	padding: 0px;\n" +
                "	border-spacing: 0px;\n}\n" +
                "tr{ \n" +
                "	height:1px;\n}"+
                "td{ \n" +
                "	width:1px;\n}";
            
            File output = new File("primer nivel\\final\\indexNew.html");
            File css = new File("primer nivel\\final\\normalizeNew.css");
            
            FileWriter fw = new FileWriter(output);
            FileWriter cssFw = new FileWriter(css);
            
            fw.write(html+thread0.toString()+"\n"+thread1.toString()+"\n"+thread2.toString()+"\n"+thread3.toString()+end);
            fw.close();
            
            cssFw.write(inicialCss+((hilo) thread0).toString("css")+"\n"+((hilo) thread1).toString("css")+"\n"+((hilo) thread2).toString("css")+"\n"+((hilo) thread3).toString("css"));
            cssFw.close();
            
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(TableArt.class.getName()).log(Level.SEVERE, null, ex);
        }
        Long totalTime = System.nanoTime();
        System.out.println("tardo en total: " + (totalTime-startTime)/1000000);
    }
    
}
