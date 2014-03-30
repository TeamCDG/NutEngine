package cdg.nut.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public abstract class ImageUtils 
{
	 public static BufferedImage scaleExact(BufferedImage img, int width, int height, Object interpolation) 
	 {
	        //float factor = getFactor(img.getWidth(), img.getHeight(), d);
	        //int w = (int) (img.getWidth() * factor);
	        //int h = (int) (img.getHeight() * factor);
		 	if(width <= 0)
		 		width = 1;
		 	if(height <= 0)
		 		height = 1;
		 		
	        BufferedImage scaled = new BufferedImage(width, height,
	                BufferedImage.TYPE_INT_ARGB);

	        Graphics2D g = scaled.createGraphics();
	        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
	                interpolation);
	        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
	                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
	        g.drawImage(img, 0, 0, width, height, null);
	        g.dispose();
	        return scaled;
	 }
	 
	 public static int makeARGB(int i, int j, int k, int l)
	 {
		 return (l << 24) | (i << 16) | (j << 8) | k;		 
	 }
	 
	public static int[] getARGB(int rgb)
	{
		int a = (rgb >> 24) & 0xff;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
			
		return new int[]{a,r,g,b};
	}

	 float getBinFactor(int width, int height, Dimension dim) {
	        float factor = 1;
	        float target = getFactor(width, height, dim);
	        if (target <= 1) { while (factor / 2 > target) { factor /= 2; }
	        } else { while (factor * 2 < target) { factor *= 2; }         }
	        return factor;
	 }

	 static float getFactor(int width, int height, Dimension dim) {
	        float sx = dim.width / (float) width;
	        float sy = dim.height / (float) height;
	        return Math.min(sx, sy);
	 }
}
