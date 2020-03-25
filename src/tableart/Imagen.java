package tableart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import static java.lang.Thread.currentThread;
import java.util.HashMap;
import java.util.Map;

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
    //para crear ids
    private int index = 0;
    //para la precision de issimilar()
    private final int precision;
    //superString.. n se si es muy eficiente esto.. un poco si, con stringBuilder mejora algo
    public String string = "";
    public String stringCss = "";
    
    
    public Imagen(BufferedImage[] jpg, int precision){
        //cargamos la imagen completa
        this.jpg = jpg;
        pixels = new Color[(int) jpg[0].getHeight()/4][jpg[0].getWidth()];
        pixelsClick = new Color[(int) jpg[1].getHeight()/4][jpg[1].getWidth()];
        this.precision=precision;
    }
    
    private String onClick(int i, int j){
        
        //defiimos aqui el map por conveiecia, en un futuro, si hay gui tirariamos de un file.. txt, json.. y por conveniencia
        Map<String,String> funJS = new HashMap<String,String>();
        funJS.put("rgb(0,0,0)", " onclick='is1();'");
        funJS.put("rgb(200,200,200)", " onclick='is2();'" );
        funJS.put("rgb(200,100,50)", " onclick='is3();'" );
        funJS.put("rgb(100,120,140)", " onclick='is4();'" );
        funJS.put("rgb(80,100,120)", " onclick='is5();'" );
        
        return (funJS.get(areaClick[i][j]) != null)? funJS.get(areaClick[i][j]) : "";
    }
    private Color isSimilar(Color one, Color two){
        //De aqui lo impportante es la condicion, cuando mayor sea el numero, mas colores seran similares entre si
        //Si queremos que sea modificable, va a ser un poco movida hacerlo desde fuera..., no tanto
        int red = Math.abs(one.getRed() - two.getRed());
        int green = Math.abs(one.getGreen() - two.getGreen());
        int blue = Math.abs(one.getBlue() - two.getBlue());
        
        return (red+green+blue<precision )? one : two;
    }    
    private String getId(String color){
                
        //return id
        if(ids.get(color)==null) ids.put(color, currentThread().getName()+(++index));
        return ids.get(color);
    }
    
    private void setColors(int y1){
        //System.out.println("SET COLORS " + currentThread().getName());
        try{
        for(int i=0;i<pixels.length;i++){
            for (int j=0;j<pixels[0].length;j++){
                //comprobar esto
                pixels[i][j] = new Color(jpg[0].getRGB(j, y1+i));
                pixelsClick[i][j] = new Color(jpg[1].getRGB(j, y1+i));
            }
        }}catch(ArrayIndexOutOfBoundsException ex){
            System.out.println(ex);
        }
    }
    private void setTable(){
        //System.out.println("SET TABLE " + currentThread().getName());
        areaClick = new String[pixelsClick.length][pixelsClick[0].length];
        cssColor =  new String[pixelsClick.length][pixelsClick[0].length];
        
        //POSIBLEMENTE IMGTABLE YA NO HACE NADA si sacamos el onClick
        for(int i=0;i<pixels.length-1;i++){
            for (int j=0;j<pixels[0].length;j++){                
                
                //System.out.println(areaClick[i][j]);
                if(j==0){
                    
                    areaClick[i][j]=("rgb("+pixelsClick[i][j].getRed()+","+pixelsClick[i][j].getGreen()+","+pixelsClick[i][j].getBlue()+")");
                    cssColor[i][j]=(Integer.toHexString(pixels[i][j].getRGB() & 0x00FFFFFF)+";");
                    
                }else{
                    Color colores = isSimilar(pixels[i][j-1],pixels[i][j]);
                    Color coloresArea = isSimilar(pixelsClick[i][j-1],pixelsClick[i][j]);
                    //MIRAR ESTO
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
                        //String id =currentThread().getName()+ i+"i"+j+"j";
                        String id = getId(cssColor[i][j]);
                        //vale hay que calcular el colspan antes, o no.., si hay que hacer que compare colores, no columnas xq seran iguales aunq tengan otro color :(
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
        setColors(y1);
        setTable();
        setString();
    }    
    private void setString2(){
        //System.out.println("SET ESTIRNG " + currentThread().getName());
        //creo que esto deberia funcionar tal cual, porque tb valorara automaticamente el onclick? o no lo hace
        /*
        StringBuffer sb = new StringBuffer();
        String anteriorCol;
        int colspan =1;
        sb.append("<tr>");
        for(String[] row:imgTable){
            anteriorCol=row[0];
            colspan=1;
            for(String color:row){
                if((anteriorCol != null) && (anteriorCol.equals(color))){
                       colspan++;
                       anteriorCol=color;
                }else{
                    if(color!=null) {
                        //vale hay que calcular el colspan antes, o no.., si hay que hacer que compare colores, no columnas xq seran iguales aunq tengan otro color :(
                        sb.append("\n<td ").append(color).append(" colspan=").append(colspan).append(" ></td>");
                        colspan=-1;
                    }
                colspan=1;
                anteriorCol=color;
                }
            }
            if(colspan!=-1)sb.append("\n</tr>\n").append("<tr height=1>");
        }
        
        //LOOK AT THIS NUMBER, thx spagetti
        this.string = sb.toString().substring(0,sb.length()-18); */
    }
    
}
