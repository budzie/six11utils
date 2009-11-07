package org.six11.util.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;

import org.six11.util.pen.Pt;

/**
 * Miscellaneous static functions for getting information about and
 * doing things tocomponents and the GUI environment.
 **/
public class Components {
  public static Pt getCenter(Component c) {
    Dimension d = c.getSize();
    return new Pt(d.getWidth() / 2.0, d.getHeight() / 2.0);
  }

  /**
   * @param g Sets KEY_ANTIALIASING to VALUE_ANTIALIAS_ON.
   */
  public static void antialias(Graphics2D g) {
    g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON));
  }

  public static void centerComponent(Component c) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = c.getSize();
    int x = (screenSize.width - frameSize.width) / 2;
    int y = (screenSize.height - frameSize.height) / 2;
    c.setLocation(x, y);
  }
  
}
