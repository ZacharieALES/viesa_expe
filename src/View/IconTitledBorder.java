package View;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.border.TitledBorder;

public class IconTitledBorder extends TitledBorder{
	
	Image icon;
	
	 public IconTitledBorder(String title, String iconPath) {
		super(title);
		icon = new ImageIcon(this.getClass().getResource(iconPath)).getImage();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	 public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) 
	 {
	     super.paintBorder(c, g, x, y, width, height);

	     ImageObserver obs = null;
	     // Now use the graphics context to draw whatever needed
	     if(icon != null)
	    	 g.drawImage(icon, 0, 0, 15, 15, obs);
	 }

}
