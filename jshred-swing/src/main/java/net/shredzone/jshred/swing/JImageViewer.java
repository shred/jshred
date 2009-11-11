/**
 * jshred - Shred's Toolbox
 *
 * Copyright (C) 2009 Richard "Shred" Körber
 *   http://jshred.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License / GNU Lesser
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
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
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

/**
 * An ImageViewer renders an image in the centre of the component. This image can be
 * scaled, and a transparency checkboard can also be drawn behind it.
 * <p>
 * As an extra feature, the image can be dragged with the mouse if the viewer is in a
 * JScrollPane.
 * <p>
 * This class also implements the {@link Printable} interface since R13.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JImageViewer.java 389 2009-11-11 23:47:30Z shred $
 * @since R9
 */
public class JImageViewer extends JComponent implements Printable {
    private static final long serialVersionUID = 3690198762949851445L;

    public enum Quality {
        /**
         * The default quality. Usually this is about the quality of QUALITY_FAST, but may
         * depend on the client's system and might change in future.
         */
        DEFAULT,

        /**
         * This quality is very fast, but will give a rather poor scaling result.
         */
        FAST,

        /**
         * This quality is reasonable in speed and quality.
         */
        SMOOTH,

        /**
         * This quality gives best scaling results, but it is rather slow.
         */
        BEST
    };

    public enum Autoscale {
        /**
         * No autoscaling. This is the default. Use this mode if you want to embed the
         * JImageViewer into a JScrollPane.
         */
        OFF,

        /**
         * Autoscaling is used. The image will only be scaled down to the size of the
         * component, keeping its aspect ratio. If the component is larger than the image
         * though, it will not be magnified.
         */
        REDUCE,

        /**
         * Autoscaling is used. The image will be scaled to always fill as much as
         * possible of the component, keeping its aspect ratio.
         */
        FULL
    }

    private static final int CBSIZE = 10; // Checkboard size in pixel

    private float zoom = 1.0f;
    private boolean checkboard = false;
    private Color cbcolor = null;
    private Autoscale autoscale = Autoscale.OFF;
    private Image image = null;
    private Quality quality = Quality.DEFAULT;
    private transient int rectX, rectY, mouseX, mouseY;
    private transient Cursor oldCursor;

    /**
     * Create an empty JImageViewer.
     */
    public JImageViewer() {
        init();
    }

    /**
     * Create a JImageViewer showing the given {@link Image}.
     * 
     * @param image
     *            {@link Image} to be shown.
     */
    public JImageViewer(Image image) {
        init();
        setImage(image);
    }

    /**
     * Create a JImageViewer showing the given {@link ImageIcon}.
     * 
     * @param icon
     *            {@link ImageIcon} to be shown.
     */
    public JImageViewer(ImageIcon icon) {
        init();
        setImage(icon);
    }

    /**
     * Create a JImageViewer showing an image that is read from the given
     * {@link InputStream}.
     * 
     * @param is
     *            {@link InputStream} to read the image data from.
     * @throws IOException
     *             if the stream could not be read.
     */
    public JImageViewer(InputStream is) throws IOException {
        init();
        setImage(is);
    }

    /**
     * Create a JImageViewer showing an image that is read from the given {@link URL}.
     * 
     * @param url
     *            {@link URL} to read the image data from.
     */
    public JImageViewer(URL url) {
        init();
        setImage(url);
    }

    /**
     * Common initializer for an instance.
     */
    private void init() {
        MouseListener lMouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                final Rectangle rv = getVisibleRect();

                // --- Remember the rectangle when the mouse was pressed ---
                rectX = rv.x;
                rectY = rv.y;

                // --- Remember the mouse position relative to it ---
                mouseX = e.getX() - rv.x;
                mouseY = e.getY() - rv.y;

                // --- Set the cursor ---
                oldCursor = getCursor();
                if (rv.width < getWidth() || rv.height < getHeight()) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // --- Restore the cursor ---
                setCursor(oldCursor);
            }
        };

        MouseMotionListener lMotion = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Rectangle rv = new Rectangle(getVisibleRect());
                // --- Compute the mouse delta movement ---
                // This are the number of pixels the mouse has been moved
                // on the viewport.
                int dX = mouseX - (e.getX() - rv.x);
                int dY = mouseY - (e.getY() - rv.y);

                // --- Compute the new rectangle ---
                // This is the position of the rectangle when the mouse was
                // clicked, added by the delta mouse move since then.
                rv.x = rectX + dX;
                rv.y = rectY + dY;

                // --- Make visible ---
                ((JComponent) e.getSource()).scrollRectToVisible(rv);
            }
        };

        addMouseListener(lMouse);
        addMouseMotionListener(lMotion);
    }

    /**
     * Set a new {@link Image} to be shown.
     * 
     * @param img
     *            New {@link Image} to be shown.
     */
    public void setImage(Image img) {
        firePropertyChange("image", this.image, img);
        this.image = img;
        revalidate();
        repaint();
    }

    /**
     * Set a new {@link ImageIcon} to be shown. This is a convenience method that will
     * just invoke {@link ImageIcon#getImage()}.
     * 
     * @param icon
     *            New {@link ImageIcon} to be shown.
     */
    public void setImage(ImageIcon icon) {
        setImage(icon.getImage());
    }

    /**
     * Set a new image that is to be read from the {@link InputStream}. No fancy image
     * formats are supported, just the one that are supported by {@link ImageIO}.
     * 
     * @param in
     *            {@link InputStream} providing the image data.
     * @throws IOException
     *             if the image could not be read.
     */
    public void setImage(InputStream in) throws IOException {
        setImage(ImageIO.read(in));
    }

    /**
     * Set a new image that is to be read from the {@link URL}. No fancy image formats are
     * supported, just the one that are supported by {@link ImageIO}.
     * 
     * @param url
     *            {@link URL} to read the image from.
     */
    public void setImage(URL url) {
        setImage(Toolkit.getDefaultToolkit().createImage(url));
    }

    /**
     * Get the {@link Image} that is currently set.
     * 
     * @return Current {@link Image}, or <code>null</code> if none was set.
     */
    public Image getImage() {
        return image;
    }

    /**
     * Draw the transparency checkboard. The checkboard is drawn behind the image and can
     * be seen through the transparent parts of it.
     * <p>
     * By default, no transparency checkboard is shown since it slows down the performance
     * and might confuse the user.
     * 
     * @param checkboard
     *            true: show transparency checkboard.
     */
    public void setCheckboard(boolean checkboard) {
        firePropertyChange("checkboard", this.checkboard, checkboard);
        this.checkboard = checkboard;
        repaint();
    }

    /**
     * Check if the transparency checkboard is to be drawn.
     * 
     * @return true: show transparency checkboard.
     */
    public boolean isCheckboard() {
        return checkboard;
    }

    /**
     * Set the transparency checkboard color. By default (or if <code>null</code> is
     * passed in here), the background color's {@link Color#brighter()} is used for the
     * bright fields. For the dark fields, {@link #getBackground()} is always used if this
     * component is opaque.
     * 
     * @param color
     *            Checkboard {@link Color}, <code>null</code> means default color.
     */
    public void setCheckboardColor(Color color) {
        firePropertyChange("checkboardColor", this.cbcolor, color);
        this.cbcolor = color;
        repaint();
    }

    /**
     * Get the transparency checkboard color.
     * 
     * @return Checkboard {@link Color}, or <code>null</code> if the default color is
     *         used.
     */
    public Color getCheckboardColor() {
        return cbcolor;
    }

    /**
     * Set the autoscale mode. The default is {@link Autoscale#OFF}, so you can embed a
     * JImageViewer into a {@link JScrollPane}.
     * 
     * @param autoscale
     *            The new {@link Autoscale} mode.
     */
    public void setAutoscale(Autoscale autoscale) {
        if (this.autoscale != autoscale) {
            firePropertyChange("autoscale", this.autoscale, autoscale);
            this.autoscale = autoscale;
            revalidate();
            repaint();
        }
    }

    /**
     * Get the current autoscale mode.
     * 
     * @return Current autoscale mode.
     */
    public Autoscale getAutoscale() {
        return autoscale;
    }

    /**
     * Set the zoom factor for the shown image. A factor of 1.0f means that the image is
     * shown in the original size. A factor &lt;1.0f will reduce the image, while a factor
     * &gt;1.0f will magnify it.
     * <p>
     * <em>NOTE</em> that {@link Autoscale#OFF} must be set in order to use zooming. This
     * might change in future releases though.
     * 
     * @param zoom
     *            Zoom factor. Default is 1.0f.
     */
    public void setZoomFactor(float zoom) {
        if (this.zoom != zoom) {
            firePropertyChange("zoom", new Float(this.zoom), new Float(zoom));
            this.zoom = zoom;
            revalidate();
            repaint();
        }
    }

    /**
     * Get the current zoom factor.
     * 
     * @return Current zoom factor.
     */
    public float getZoomFactor() {
        return zoom;
    }

    /**
     * Set the scaling quality.
     * 
     * @param quality
     *            New scaling quality.
     */
    public void setQuality(Quality quality) {
        firePropertyChange("quality", this.quality, quality);
        this.quality = quality;
        repaint();
    }

    /**
     * Get the current scaling quality.
     * 
     * @return Current scaling quality.
     */
    public Quality getQuality() {
        return quality;
    }

    /**
     * Get the final {@link Dimension} of the image after proper scaling was applied.
     * 
     * @return {@link Dimension} of the image to be drawn. The returned {@link Dimension}
     *         object is a copy that can be manipulated by the caller.
     */
    protected Dimension getScaledDimension() {
        // --- Return null if there is no image ---
        if (image == null) return null;

        // --- Get the default dimensions ---
        Dimension dim = new Dimension(image.getWidth(this), image.getHeight(this));

        if (autoscale != Autoscale.OFF) {
            // --- Automatically scale the image ---
            final Dimension imgDim = new Dimension(image.getWidth(this), image.getHeight(this));
            final Dimension cmpDim = new Dimension(getSize());
            final Insets insets = getInsets();
            cmpDim.width -= insets.left + insets.right;
            cmpDim.height -= insets.top + insets.bottom;

            if (autoscale == Autoscale.REDUCE) {
                dim = SwingUtils.scaleAspect(imgDim, cmpDim);
            } else {
                dim = SwingUtils.scaleAspectMax(imgDim, cmpDim);
            }

        } else if (zoom != 1.0f) {
            // --- Scale the picture according to the zoom ---

            dim.width = Math.round(dim.width * zoom);
            dim.height = Math.round(dim.height * zoom);
        }

        return dim;
    }

    /**
     * Get this component's minimum size. If {@link Autoscale#OFF} is set, it equals to
     * the image size.
     * 
     * @return Minimum {@link Dimension}
     */
    @Override
    public Dimension getMinimumSize() {
        if (image != null && autoscale == Autoscale.OFF) {
            Dimension dim = getScaledDimension();
            Insets insets = getInsets();
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;
            return dim;
        } else {
            return super.getMinimumSize();
        }
    }

    /**
     * Get the component's preferred size. This is always equal to the minimum size unless
     * no image has been set.
     * 
     * @return Preferred dimensions
     */
    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            return getMinimumSize();
        } else {
            return super.getPreferredSize();
        }
    }

    /**
     * Paint the component. It will paint the checkerboard (if enabled) and the Image on
     * top of it.
     * 
     * @param g
     *            {@link Graphics} context.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            final Graphics2D g2d = (Graphics2D) g.create();
            final Dimension dim = getScaledDimension();
            final Insets insets = getInsets();

            // --- Translate to the centre ---
            g2d.translate(
                ((getWidth() - insets.left - insets.right - dim.width) / 2) + insets.left,
                ((getHeight() - insets.top - insets.bottom - dim.height) / 2) + insets.top);
            g2d.clipRect(0, 0, dim.width, dim.height);

            // --- Draw the cbcolor ---
            if (checkboard) {
                g2d.setColor(cbcolor != null ? cbcolor : getBackground().brighter());
                for (int x = 0; x <= (dim.width / CBSIZE); x++) {
                    for (int y = 0; y <= (dim.height / CBSIZE); y++) {
                        if (x % 2 == y % 2) {
                            g2d.fillRect(x * CBSIZE, y * CBSIZE, CBSIZE - 1, CBSIZE - 1);
                        }
                    }
                }
            }

            // --- Set scaling quality ---
            setQuality(g2d, quality);

            // --- Draw the image ---
            g2d.drawImage(image, 0, 0, dim.width, dim.height, this);

            // --- We're done ---
            g2d.dispose();
        }
    }

    /**
     * Print this image. It will be printed on a single page, scaled to consume the entire
     * paper (keeping the border).
     * 
     * @param graphics
     *            Graphics context
     * @param pageFormat
     *            Page format to be used
     * @param pageIndex
     *            Page index.
     * @return {@link Printable#PAGE_EXISTS} or {@link Printable#NO_SUCH_PAGE}
     * @since R13
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
        final Graphics2D g2d = (Graphics2D) graphics;

        if (pageIndex != 0) {
            return Printable.NO_SUCH_PAGE;
        }

        // --- Do nothing if there is no image ---
        if (image == null) {
            return Printable.PAGE_EXISTS;
        }

        int imgW = image.getWidth(this);
        int imgH = image.getHeight(this);
        if (imgW == 0 || imgH == 0) {
            return Printable.PAGE_EXISTS;
        }

        // --- Set Scaling and Clipping ---
        double scaleW = pageFormat.getImageableWidth() / imgW;
        double scaleH = pageFormat.getImageableHeight() / imgH;
        double scale = Math.min(scaleW, scaleH);
        g2d.scale(scale, scale);
        g2d.setClip(
            (int) (pageFormat.getImageableX() / scale),
            (int) (pageFormat.getImageableY() / scale),
            (int) (pageFormat.getImageableWidth() / scale),
            (int) (pageFormat.getImageableHeight() / scale));

        // --- Translate ---
        g2d.translate(g2d.getClipBounds().getX(), g2d.getClipBounds().getY());

        // --- Set scaling quality ---
        setQuality(g2d, quality);

        // --- Draw the image ---
        g2d.drawImage(image, 0, 0, this);

        return Printable.PAGE_EXISTS;
    }

    /**
     * Set the rendering hints according to the quality.
     * 
     * @param g2d
     *            {@link Graphics2D} to set the rendering hints.
     * @param quality
     *            {@link Quality} desired.
     * @since R13
     */
    protected void setQuality(Graphics2D g2d, Quality quality) {
        switch (quality) {
        case FAST:
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED));
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED));
            break;

        case SMOOTH:
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR));
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED));
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
            break;

        case BEST:
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
            g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
            break;
        }
    }

}
