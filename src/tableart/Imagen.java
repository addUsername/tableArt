package tableart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Thread.currentThread;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author SERGI
 */
public class Imagen {
    
    //la imagen
    private BufferedImage[] jpg = new BufferedImage[2];
    //el numero de filas sera de 1/4 de la imagen, xq seran 4 hilos
    private final Color[][] pixels;
    private final Color[][] pixelsClick;
    //este guardara los colores como texto
    private String[][] areaClick;
    private String[][] cssColor;
    //para no repetir estilos en css
    private Map<String,String>ids=new HashMap();
    //map color - onclick
    Map<String,String> funJS = new HashMap<String,String>();
    //para crear ids
    private int index = 0;
    //para la precision de issimilar()
    private int precision;
    //superString.. n se si es muy eficiente esto.. un poco si, con stringBuilder mejora algo
    public String string = "";
    public String stringCss = "";
    
    
    public Imagen(BufferedImage[] jpg, int precision){
        //we load the whole image in each thread
        this.jpg = jpg;
        pixels = new Color[(int) jpg[0].getHeight()/4][jpg[0].getWidth()];
        pixelsClick = new Color[(int) jpg[1].getHeight()/4][jpg[1].getWidth()];
        this.precision=precision;
    }
    
    private String onClick(int i, int j){
        //this function will tell if there should be an event in that especific cell or not
        return (funJS.get(areaClick[i][j]) != null)? funJS.get(areaClick[i][j]) : "";
    }
    
    private void funJSinit(){
        //here we seek to parse app.txt which contains color:envents pairs
        File json = new File(System.getProperty("user.dir")+"\\app.txt");
        json.deleteOnExit();
        Scanner sc;
        String[] s;
        try {
            sc = new Scanner(json);
            while(sc.hasNextLine()){
                s=sc.nextLine().split("\\$");
                funJS.put(s[0],s[1]);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR - app.txt file not found, it should be generated by this software");
        }
        
    }
    
    private void makeJSfile(){
        //here we want to create the .js file
        File js = new File(System.getProperty("user.dir")+"\\output\\myScript.js");
        String jscript="";
        try {
            FileWriter fw = new FileWriter(js);
             for(String val: funJS.values()){
                String[] function = val.split("'");
                jscript += ("\nfunction "+function[1].substring(0,function[1].length()-1 )+"{\n};");
                
        }   fw.write(jscript);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(Imagen.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private Color isSimilar(Color one, Color two){
        
        int red = Math.abs(one.getRed() - two.getRed());
        int green = Math.abs(one.getGreen() - two.getGreen());
        int blue = Math.abs(one.getBlue() - two.getBlue());
        
        return (red+green+blue<precision )? one : two;
    }    
    private String getId(String color){
        
        if(ids.get(color)==null) ids.put(color, currentThread().getName()+(++index));
        return ids.get(color);
    }
    
    private void setColors(int y1){
        //System.out.println("SET COLORS " + currentThread().getName());
        try{
        for(int i=0;i<pixels.length;i++){
            for (int j=0;j<pixels[0].length;j++){
                
                pixels[i][j] = new Color(jpg[0].getRGB(j, y1+i));
                pixelsClick[i][j] = new Color(jpg[1].getRGB(j, y1+i));
            }
        }}catch(ArrayIndexOutOfBoundsException ex){}
    }
    private void setTable(){
        //System.out.println("SET TABLE " + currentThread().getName());
        areaClick = new String[pixelsClick.length][pixelsClick[0].length];
        cssColor =  new String[pixelsClick.length][pixelsClick[0].length];
        
        for(int i=0;i<pixels.length-1;i++){
            for (int j=0;j<pixels[0].length;j++){                
                
                
                if(j==0){                    
                    areaClick[i][j]=("rgb("+pixelsClick[i][j].getRed()+","+pixelsClick[i][j].getGreen()+","+pixelsClick[i][j].getBlue()+")");
                    cssColor[i][j]=(Integer.toHexString(pixels[i][j].getRGB() & 0x00FFFFFF)+";");
                    
                }else{
                    Color colores = isSimilar(pixels[i][j-1],pixels[i][j]);
                    Color coloresArea = isSimilar(pixelsClick[i][j-1],pixelsClick[i][j]);
                    
                    areaClick[i][j]=("rgb("+coloresArea.getRed()+","+coloresArea.getGreen()+","+coloresArea.getBlue()+")");
                    cssColor[i][j]=(Integer.toHexString(colores.getRGB() & 0x00FFFFFF)+";");
                }
            }
        }
    }
    private void setString(){
        
        StringBuffer sb = new StringBuffer();
        StringBuffer cssSb = new StringBuffer();
        String anteriorCol;
        
        //Evitara que s repitan clases del mismo color en el css
        int colspan=1;
        sb.append("<tr>");
        
        for(int i=0;i<cssColor.length;i++){
            anteriorCol=cssColor[i][0];
            colspan=1;
            
            for(int j=0;j<cssColor[0].length;j++){
                
                if((anteriorCol != null) && ((anteriorCol+" "+onClick(i,Math.abs(j-1))).equals(cssColor[i][j]+" "+onClick(i,j)))){
                       colspan++;
                       anteriorCol=cssColor[i][j];
                }else{
                    if(cssColor[i][j]!=null) {
                        
                        String id = getId(cssColor[i][j]);
                        sb.append("\n<td class='").append(id).append("' colspan=").append(colspan).append(onClick(i,j)).append(" ></td>");
                        
                        if(id.equals(currentThread().getName()+(index))){
                            cssSb.append("\n.").append(id).append("{\nbackground-color: #").append(cssColor[i][j]).append("\n}");
                        }
                        colspan=-1;
                    }
                colspan=1;
                anteriorCol=cssColor[i][j];
                }
            }
            if(colspan!=-1)sb.append("\n</tr>\n").append("<tr>");
        }
        //LOOK AT THIS NUMBER, thx spagetti
        this.string = sb.toString().substring(0,sb.length()-16);
        this.stringCss = cssSb.toString();
    }    
   
    public void inicialize(int y1){
        //System.out.println("INICIALIZE " + currentThread().getName());
        funJSinit();        
        makeJSfile();
        setColors(y1);
        setTable();
        setString();
    }
}