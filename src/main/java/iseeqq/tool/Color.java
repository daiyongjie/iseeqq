package iseeqq.tool;


/**
 * 
 * 
 * @author 戴永杰
 *
 * @date 2017年11月14日 下午5:37:22 
 * @version V1.0   
 *
 */
public class Color {
	public static int red(int color) {
		return (color >> 16) & 0xFF;
	}
	
    public static int green(int color) {
        return (color >> 8) & 0xFF;
    }
    
    public static int blue(int color) {
        return color & 0xFF;
    }
    
    public static int[] rgb(int color){
    	return new int[]{red(color),green(color),blue(color)};
    }
    
    public static  int toInt( int[] rgb){
//    	return  ((rgb[0]&0x0ff)<<16)|((rgb[1]&0x0ff)<<8)|(rgb[2]&0x0ff);
    	return new java.awt.Color(rgb[0], rgb[1], rgb[2]).getRGB();
    }
    public static String toHex(int r, int g, int b) {  
        return "#" + toBrowserHexValue(r) + toBrowserHexValue(g)  
                + toBrowserHexValue(b);  
    }  
  
    private static String toBrowserHexValue(int number) {  
        StringBuilder builder = new StringBuilder(  
                Integer.toHexString(number & 0xff));  
        while (builder.length() < 2) {  
            builder.append("0");  
        }  
        return builder.toString().toUpperCase();  
    }  
}
