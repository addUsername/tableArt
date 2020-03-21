package tableart;

import java.awt.Color;
import java.awt.image.BufferedImage;
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
    private String[][] imgTable;
    private String[][] areaClick;
    //superString.. n se si es muy eficiente esto.. un poco si, con stringBuilder mejora algo
    public String string = "";
    
    
    public Imagen(BufferedImage[] jpg){
        
        //cargamos la imagen completa
        this.jpg = jpg;
        pixels = new Color[(int) jpg[0].getHeight()/4][jpg[0].getWidth()];
        pixelsClick = new Color[(int) jpg[1].getHeight()/4][jpg[1].getWidth()];
    }
    
    private void setColors(int y1, int y2){
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
    private String onClick(int i, int j){
        
        //defiimos aqui el map por conveiecia, en un futuro, si hay gui tirariamos de un file.. txt, json.. y por conveniencia
        Map<String,String> funJS = new HashMap<String,String>();
        funJS.put("rgb(120,120,120)", " onclick='is1();'");
        funJS.put("rgb(200,200,200)", " onclick='is2();'" );
        funJS.put("rgb(200,100,50)", " onclick='is3();'" );
        funJS.put("rgb(100,120,140)", " onclick='is4();'" );
        funJS.put("rgb(80,100,120)", " onclick='is5();'" );
        
        return (funJS.get(areaClick[i][j]) != null)? funJS.get(areaClick[i][j]) : "";
    }
    
    private void setTable(){
        //creo que no hay que cambiar nada aqui
        //en un futuro aqui se escribira el css
        //System.out.println("SET TABLE " + currentThread().getName());
        imgTable = new String[pixels.length][pixels[0].length];
        areaClick = new String[pixelsClick.length][pixelsClick[0].length];
        
        for(int i=0;i<imgTable.length-1;i++){
            for (int j=0;j<imgTable[0].length;j++){                
                
                //System.out.println(areaClick[i][j]);
                if(j==0){
                    //imgTable[i][j]=("rgb("+pixels[i][j].getRed()+","+pixels[i][j].getGreen()+","+pixels[i][j].getBlue()+")");
                    areaClick[i][j]=("rgb("+pixelsClick[i][j].getRed()+","+pixelsClick[i][j].getGreen()+","+pixelsClick[i][j].getBlue()+")");
                    imgTable[i][j]=(" bgcolor="+Integer.toHexString(pixels[i][j].getRGB() & 0x00FFFFFF)+" "+onClick(i,j));
                    
                }else{
                    Color colores = isSimilar(pixels[i][j-1],pixels[i][j]);
                    Color coloresArea = isSimilar(pixelsClick[i][j-1],pixelsClick[i][j]);
                    //MIRAR ESTO
                    //imgTable[i][j]=(" bgcolor='rgb("+colores[0]+","+colores[1]+","+colores[2]+")"+onClick(i,j)+"'");
                    areaClick[i][j]=("rgb("+coloresArea.getRed()+","+coloresArea.getGreen()+","+coloresArea.getBlue()+")");
                    imgTable[i][j]=(" bgcolor="+Integer.toHexString(colores.getRGB() & 0x00FFFFFF))+" "+onClick(i,j);
                }
            }
        }
    }
    
    private Color isSimilar(Color one, Color two){
        //De aqui lo impportante es la condicion, cuando mayor sea el numero, mas colores seran similares entre si
        int red = Math.abs(one.getRed() - two.getRed());
        int green = Math.abs(one.getGreen() - two.getGreen());
        int blue = Math.abs(one.getBlue() - two.getBlue());
        
        if(red+green+blue<6 ) {
            int [] color = {one.getRed(),one.getGreen(),one.getBlue()};
            return one;
        }
        //if not similar..
        int [] color = {two.getRed(),two.getGreen(),two.getBlue()};
        return two; 
    }
    
    private void setString(){
        //System.out.println("SET ESTIRNG " + currentThread().getName());
        //creo que esto deberia funcionar tal cual, porque tb valorara automaticamente el onclick? no lo hace
        StringBuffer sb = new StringBuffer();
        String anteriorCol;
        int colspan =1;
        sb.append("<tr height=1>");
        for(String[] row:imgTable){
            anteriorCol=row[0];
            colspan=1;
            for(String color:row){
                if((anteriorCol != null) && (anteriorCol.equals(color))){
                       colspan++;
                       anteriorCol=color;
                }else{
                    if(color!=null) {
                        sb.append("\n<td width=1").append(color).append(" colspan=").append(colspan).append(" ></td>");
                        colspan=-1;
                    }
                colspan=1;
                anteriorCol=color;
                }
            }
            if(colspan!=-1)sb.append("\n</tr>\n").append("<tr height=1>");
        }
        
        this.string = sb.toString().substring(0,sb.length()-33);
    }
    
    public void inicialize(int y1, int y2){
        
        //System.out.println("INICIALIZE " + currentThread().getName());
        setColors(y1,y2);
        setTable();
        setString();
        
    }
}
