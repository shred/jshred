/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2004 Richard "Shred" Körber
 *   http://www.shredzone.net/go/jshred
 *
 *-----------------------------------------------------------------------
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is JSHRED.
 *
 * The Initial Developer of the Original Code is
 * Richard "Shred" Körber.
 * Portions created by the Initial Developer are Copyright (C) 2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK *****
 */

package net.shredzone.jshred.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

/**
 * An ImageViewer renders an image in the centre of the component. This image
 * can be scaled, and a transparency checkboard can also be drawn behind it.
 * <p>
 * As an extra feature, the image can be dragged with the mouse if JImage is
 * in a JScrollPane.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JImageViewer.java,v 1.4 2005/01/24 10:08:07 shred Exp $
 * @since   R9
 */
public class JImageViewer extends JComponent {
  private static final long serialVersionUID = 3690198762949851445L;

  /**
   * The default quality. Usually this is about the quality of QUALITY_FAST,
   * but may depend on the client's system and might change in future.
   */
  public static final int QUALITY_DEFAULT = 0;

  /**
   * This quality is very fast, but will give a rather poor scaling result.
   */
  public static final int QUALITY_FAST    = 1;

  /**
   * This quality is reasonable in speed and quality.
   */
  public static final int QUALITY_SMOOTH  = 2;
  
  /**
   * This quality gives best scaling results, but it is rather slow.
   */
  public static final int QUALITY_BEST    = 3;
  
  /**
   * No autoscaling. This is the default.
   * Use this mode if you want to embed the JImageViewer into a JScrollPane.
   */
  public static final int AUTOSCALE_OFF = 0;
  
  /**
   * Autoscaling is used. The image will only be scaled down to the size of
   * the component, keeping its aspect ratio. If the component is larger than
   * the image though, it will not be magnified.
   */
  public static final int AUTOSCALE_REDUCE = 1;
  
  /**
   * Autoscaling is used. The image will be scaled to always fill as much as
   * possible of the component, keeping its aspect ratio.
   */
  public static final int AUTOSCALE_FULL = 2;
  
  private static final int CBSIZE = 10;         // Checkboard size in pixel
  
  private float   zoom       = 1.0f;
  private boolean checkboard = false;
  private Color   cbcolor    = null;
  private int     autoscale  = AUTOSCALE_OFF;
  private Image   image      = null;
  private int     quality    = QUALITY_DEFAULT;
  private transient int rectX, rectY, mouseX, mouseY;
  private transient Cursor  oldCursor;

  /**
   * Create an empty JImageViewer.
   */
  public JImageViewer() {
    init();
  }

  /**
   * Create a JImageViewer showing the given Image.
   * 
   * @param   image       Image to be shown.
   */
  public JImageViewer( Image image ) {
    init();
    setImage( image );
  }
  
  /**
   * Create a JImageViewer showing the given ImageIcon.
   * 
   * @param   icon        ImageIcon to be shown.
   */
  public JImageViewer( ImageIcon icon ) {
    init();
    setImage( icon );
  }
  
  /**
   * Create a JImageViewer showing an image that is read from the given
   * InputStream.
   * 
   * @param   is          InputStream to read the image data from.
   * @throws IOException    if the stream could not be read.
   */
  public JImageViewer( InputStream is ) throws IOException {
    init();
    setImage( is );
  }
  
  /**
   * Create a JImageViewer showing an image that is read from the given
   * URL.
   * 
   * @param   url         URL to read the image data from.
   */
  public JImageViewer( URL url ) {
    init();
    setImage( url );
  }
  
  /**
   * Common initializer for an instance.
   */
  private void init() {
    MouseListener lMouse = new MouseAdapter() {
      public void mousePressed( MouseEvent e ) {
        final Rectangle rv = getVisibleRect();

        //--- Remember the rectangle when the mouse was pressed ---
        rectX  = rv.x;
        rectY  = rv.y;

        //--- Remember the mouse position relative to it ---
        mouseX = e.getX()-rv.x;
        mouseY = e.getY()-rv.y;

        //--- Set the cursor ---
        oldCursor = getCursor();
        if( rv.width<getWidth() || rv.height<getHeight() ) {
          setCursor( Cursor.getPredefinedCursor( Cursor.MOVE_CURSOR ) );
        }
      }
      
      public void mouseReleased( MouseEvent e ) {
        //--- Restore the cursor ---
        setCursor( oldCursor );
      }
    };
    
    MouseMotionListener lMotion = new MouseMotionAdapter() {
      public void mouseDragged( MouseEvent e ) {
        Rectangle rv = new Rectangle( getVisibleRect() );
        //--- Compute the mouse delta movement ---
        // This are the number of pixels the mouse has been moved
        // on the viewport.
        int dX = mouseX - (e.getX()-rv.x);
        int dY = mouseY - (e.getY()-rv.y);
        
        //--- Compute the new rectangle ---
        // This is the position of the rectangle when the mouse was
        // clicked, added by the delta mouse move since then.
        rv.x = rectX + dX;
        rv.y = rectY + dY;
        
        //--- Make visible ---
        ((JComponent) e.getSource()).scrollRectToVisible( rv );
      }
    };
    
    addMouseListener( lMouse );
    addMouseMotionListener( lMotion );
  }
  
  /**
   * Set a new image to be shown.
   * 
   * @param   img     New Image to be shown.
   */
  public void setImage( Image img ) {
    firePropertyChange( "image", this.image, img );
    this.image = img;
    revalidate();
    repaint();
  }
  
  /**
   * Set a new ImageIcon to be shown. This is a convenience method that will
   * just invoke ImageIcon.getImage().
   * 
   * @param   icon    New ImageIcon to be shown.
   */
  public void setImage( ImageIcon icon ) {
    setImage( icon.getImage() );
  }
  
  /**
   * Set a new image that is to be read from the InputStream. Only valid
   * JDK image formats (JPEG, PNG, GIF) can be used here.
   * 
   * @param   in      InputStream providing the image data.
   * @throws IOException    if the image could not be read.
   */
  public void setImage( InputStream in ) throws IOException {
    setImage( ImageIO.read( in ) );
  }
  
  /**
   * Set a new image that is to be read from the URL. Only valid
   * JDK image formats (JPEG, PNG, GIF) can be used here.
   * 
   * @param   url     URL to read the image from.
   */
  public void setImage( URL url ) {
    setImage( Toolkit.getDefaultToolkit().createImage( url ) );
  }

  /**
   * Get the Image that is currently set.
   * 
   * @return    Current Image, or null if none was set.
   */
  public Image getImage() {
    return image;
  }
  
  /**
   * Draw the transparency checkboard. The checkboard is drawn behind the
   * image and can be seen through the transparent parts of it.
   * <p>
   * By default, no transparency checkboard is shown since it slows down
   * the performance and might disturb the user.
   * 
   * @param   checkboard      true: show transparency checkboard.
   */
  public void setCheckboard( boolean checkboard ) {
    firePropertyChange( "checkboard", this.checkboard, checkboard );
    this.checkboard = checkboard;
    repaint();
  }
  
  /**
   * Check if the transparency checkboard is to be drawn.
   * 
   * @return    true: show transparency checkboard.
   */
  public boolean isCheckboard() {
    return checkboard;
  }
  
  /**
   * Set the transparency checkboard color. By default (or if null is passed
   * in here), getBackground().brighter() is used for the bright fields. For
   * the dark fields, getBackground() is always used unless this component
   * is not opaque.
   * 
   * @param color   Checkboard color, null means default color.
   */
  public void setCheckboardColor( Color color ) {
    firePropertyChange( "checkboardColor", this.cbcolor, color );
    this.cbcolor = color;
    repaint();
  }
  
  /**
   * Get the transparency checkboard color.
   * 
   * @return  Checkboard color, or null if the default color is used.
   */
  public Color getCheckboardColor() {
    return cbcolor;
  }
  
  /**
   * Set the autoscale mode. The default is <code>AUTOSCALE_OFF</code>, so you
   * can embed a JImageViewer into a JScrollPane.
   * 
   * @param autoscale   The new AUTOSCALE mode.
   */
  public void setAutoscale( int autoscale ) {
    if( this.autoscale!=autoscale ) {
      firePropertyChange( "autoscale", this.autoscale, autoscale );
      this.autoscale = autoscale;
      revalidate();
      repaint();
    }
  }
  
  /**
   * Get the current autoscale mode.
   * 
   * @return    Current autoscale mode.
   */
  public int getAutoscale() {
    return autoscale;
  }
  
  /**
   * Set the zoom factor for the shown image. A factor of 1.0f means that the
   * image is shown in the original size. A factor &lt;1.0f will reduce the
   * image, while a factor &gt;1.0f will magnify it.
   * <p>
   * <em>NOTE</em> that <code>AUTOSCALE_OFF</code> must be set in order to
   * use zooming. This might change in future releases though.
   * 
   * @param zoom    Zoom factor. Default is 1.0f.
   */
  public void setZoomFactor( float zoom ) {
    if( this.zoom!=zoom ) {
      firePropertyChange( "zoom", this.zoom, zoom );
      this.zoom = zoom;
      revalidate();
      repaint();
    }
  }
  
  /**
   * Get the current zoom factor.
   * 
   * @return    Current zoom factor.
   */
  public float getZoomFactor() {
    return zoom;
  }
  
  /**
   * Set the scaling quality.
   * 
   * @param quality   New scaling quality.
   */
  public void setQuality( int quality ) {
    firePropertyChange( "quality", this.quality, quality );
    this.quality = quality;
    repaint();
  }
  
  /**
   * Get the current scaling quality.
   * 
   * @return    Current scaling quality.
   */
  public int getQuality() {
    return quality;
  }
  
  /**
   * Get the final Dimension of the image after proper scaling was applied.
   * 
   * @return  Dimension of the image to be drawn. The returned Dimension
   *  object is a copy that can be manipulated by the caller.
   */
  protected Dimension getScaledDimension() {
    //--- Return null if there is no image ---
    if( image==null ) return null;
    
    //--- Get the default dimensions ---
    Dimension dim = new Dimension(
        image.getWidth(this),
        image.getHeight(this)
    );
    
    if( autoscale!=AUTOSCALE_OFF ) {
      //--- Automatically scale the image ---
      final Dimension imgDim = new Dimension( image.getWidth(this), image.getHeight(this) ); 
      final Dimension cmpDim = new Dimension( getSize() );
      final Insets    insets = getInsets();
      cmpDim.width  -= insets.left + insets.right;
      cmpDim.height -= insets.top + insets.bottom;
      
      if( autoscale==AUTOSCALE_REDUCE ) {
        dim = SwingUtils.scaleAspect( imgDim, cmpDim );
      }else {
        dim = SwingUtils.scaleAspectMax( imgDim, cmpDim );
      }
      
    }else if( zoom!=1.0f ) {
      //--- Scale the picture according to the zoom ---

      dim.width  = Math.round( dim.width  * zoom );
      dim.height = Math.round( dim.height * zoom );
    }
    
    return dim;
  }
  
  /**
   * Get this component's minimum size. If <code>AUTOSCALE_OFF</code> is set,
   * it equals to the image size.
   * 
   * @return  Minimum dimensions
   */
  public Dimension getMinimumSize() {
    if( image!=null && autoscale==AUTOSCALE_OFF ) {
      final Dimension dim = getScaledDimension();
      final Insets insets = getInsets();
      dim.width += insets.left + insets.right;
      dim.height += insets.top + insets.bottom;
      return dim;
    }else {
      return super.getMinimumSize();
    }
  }
  
  /**
   * Get the component's preferred size. This is always equal to the minimum
   * size unless no Image has been set.
   * 
   * @return  Preferred dimensions
   */
  public Dimension getPreferredSize() {
    if( image!=null ) {
      return getMinimumSize();
    }else {
      return super.getPreferredSize();
    }
  }
  
  /**
   * Paint the component. It will paint the checkerboard (if enabled) and the
   * Image on top of it.
   * 
   * @param   g       Graphics context.
   */
  protected void paintComponent( Graphics g ) {
    super.paintComponent( g );
    
    if( image!=null ) {
      final Graphics2D g2d = (Graphics2D) g.create();
      final Dimension dim = getScaledDimension();
      final Insets insets = getInsets();
      
      //--- Translate to the centre ---
      g2d.translate(
          ((getWidth()-insets.left-insets.right - dim.width ) / 2) + insets.left,
          ((getHeight()-insets.top-insets.bottom - dim.height) / 2) + insets.top
      );
      g2d.clipRect( 0,0, dim.width,dim.height );
      
      //--- Draw the cbcolor ---
      if( checkboard ) {
        g2d.setColor( cbcolor!=null ? cbcolor : getBackground().brighter() );
        for( int x=0; x<=(dim.width/CBSIZE); x++ ) {
          for( int y=0; y<=(dim.height/CBSIZE); y++ ) {
            if( x%2 == y%2 ) {
              g2d.fillRect( x*CBSIZE,y*CBSIZE, CBSIZE-1,CBSIZE-1 );
            }
          }
        }
      }

      //--- Set scaling quality ---
      switch( quality ) {
        case QUALITY_FAST:
          g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_INTERPOLATION      , RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR ) );
          g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED      ) );
          g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_RENDERING          , RenderingHints.VALUE_RENDER_SPEED                   ) );
          break;

        case QUALITY_SMOOTH:
          g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_INTERPOLATION      , RenderingHints.VALUE_INTERPOLATION_BILINEAR         ) );
          g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED      ) );
          g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_RENDERING          , RenderingHints.VALUE_RENDER_QUALITY                 ) );
          break;

        case QUALITY_BEST:
          g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_INTERPOLATION      , RenderingHints.VALUE_INTERPOLATION_BICUBIC          ) );
          g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY    ) );
          g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_RENDERING          , RenderingHints.VALUE_RENDER_QUALITY                 ) );
          g2d.addRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING       , RenderingHints.VALUE_ANTIALIAS_ON                   ) );
          break;
      }
      
      //--- Draw the image ---
      g2d.drawImage( image, 0,0, dim.width,dim.height, this );
      
      //--- We're done ---
      g2d.dispose();
    }
  }

}
