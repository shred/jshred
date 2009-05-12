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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A Swing component that renders printable pages for a printer preview. The view can be
 * zoomed, and it is possible to turn over the pages of the printout.
 * <p>
 * The pages are rendered into an internal image buffer, for best performance. The
 * tradeoff is that a lot of memory is required for large zoom factors, so you should
 * limit it within a reasonable range.
 * <p>
 * The given {@link Printable} must be able to render the same page several times, but
 * does not need to be able to give random access to all the pages.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JPrintPreview.java 302 2009-05-12 22:19:11Z shred $
 * @since R8
 */
public class JPrintPreview extends JPanel {
    private static final long serialVersionUID = 3256723966038390833L;

    private final static int SHADOW_X = 4;      // Shadow X size
    private final static int SHADOW_Y = 4;      // Shadow Y size
    private final static int BORDER_SIZE = 3;   // Size of the border around the
    // page

    private int currentPage;
    private double currentZoom;
    private Pageable pageable = null;
    private Printable printable = null;
    private PageFormat format = null;
    private final JLabel jlContent;
    private boolean lowMem = false;

    private transient int rectX, rectY, mouseX, mouseY;
    private transient Cursor oldCursor;

    /**
     * Create an empty JPrintPreview pane. It will show nothing.
     */
    public JPrintPreview() {
        setLayout(new BorderLayout());
        jlContent = new JLabel();
        jlContent.setHorizontalAlignment(JLabel.CENTER);
        add(jlContent, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));

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

        jlContent.addMouseListener(lMouse);
        jlContent.addMouseMotionListener(lMotion);
    }

    /**
     * Create a JPrintPreview pane and initialize it with a {@link Printable} object,
     * which is to be shown in the given {@link PageFormat}.
     * 
     * @param printable
     *            {@link Printable} to be shown
     * @param format
     *            {@link PageFormat} to be used
     */
    public JPrintPreview(Printable printable, PageFormat format) {
        this();
        setPrintable(printable, format);
    }

    /**
     * Create a JPrintPreview pane and initialize it with a {@link Pageable} object.
     * 
     * @param pageable
     *            {@link Pageable} to be shown
     */
    public JPrintPreview(Pageable pageable) {
        this();
        setPageable(pageable);
    }

    /**
     * Set a {@link Printable} to be shown in the given {@link PageFormat}. The first page
     * of the {@link Printable} will be shown with a standard zoom factor.
     * <p>
     * You can always change to other {@link Printable} and {@link Pageable} objects.
     * 
     * @param printable
     *            {@link Printable} to be shown
     * @param format
     *            {@link PageFormat} to be used
     */
    public void setPrintable(Printable printable, PageFormat format) {
        this.pageable = null;
        this.printable = printable;
        this.format = format;
        currentPage = 0;
        initZoom(format);
    }

    /**
     * Set a {@link Pageable} to be shown. The first page of the {@link Pageable} will be
     * shown with a standard zoom factor.
     * <p>
     * You can always change to other {@link Printable} and {@link Pageable} objects.
     * 
     * @param pageable
     *            {@link Pageable} to be shown
     */
    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
        this.printable = null;
        this.format = null;
        currentPage = 0;
        initZoom(pageable.getPageFormat(currentPage));
    }

    /**
     * Sets if a low memory footprint is required. JPrintPreview will then try to reduce
     * memory consumption. For example, the printout will be grayscaled, which reduces
     * memory by about two third.
     * 
     * @param val
     *            true: low memory
     */
    public void setLowMem(boolean val) {
        lowMem = val;
    }

    /**
     * Returns if a low memory footprint is required.
     * 
     * @return true: low memory
     */
    public boolean isLowMem() {
        return lowMem;
    }

    /**
     * Initializes the zoom factor. A default factor is chosen that the resulting image
     * will fit to the current size of this {@link Component}. If the {@link Component}
     * has currently no size (i.e. is not shown), a default size of 500x500 pixels will be
     * used. You can change the default behaviour by overriding this method.
     * 
     * @param format
     *            {@link PageFormat} to be used.
     */
    protected void initZoom(PageFormat format) {
        double zoom = 1.0;
        if (format.getOrientation() == PageFormat.PORTRAIT) {
            int height = getHeight();
            if (height == 0) height = 500;
            zoom = height / format.getHeight();
        } else {
            int width = getWidth();
            if (width == 0) width = 500;
            zoom = width / format.getWidth();
        }
        setZoomFactor(zoom);
    }

    /**
     * Draw the paper. The printout will be printed on this graphics. The default
     * behaviour is to print a white sheet (size: width x height) and a darker shadow at
     * the right and bottom side. You can change this behaviour by overriding this method.
     * <p>
     * The paper must start at the coordinates (0,0). There is no scaling set yet, and
     * antialiasing is disabled here.
     * 
     * @param g
     *            {@link Graphics}. It is safe to cast it to {@link Graphics2D}.
     * @param width
     *            Width of the paper. The actual image is at least three pixels broader,
     *            to give space for the shadow.
     * @param height
     *            Height of the paper. The actual image is at least three pixels taller,
     *            to give space for the shadow.
     */
    protected void drawPaper(Graphics g, int width, int height) {
        // --- Draw the shadow ---
        g.setColor(this.getBackground().darker());
        g.fillRect(SHADOW_X, height, width, SHADOW_Y); // bottom side
        g.fillRect(width, SHADOW_Y, SHADOW_X, height); // right side

        // --- Draw the paper ---
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
    }

    /**
     * Recreates the internal image cache. This tries to print the currently selected
     * page. If something goes wrong while printing, nothing will be shown instead.
     * <p>
     * Note that this method may require some time and memory.
     */
    protected void recreate() throws PrinterException {
        try {
            jlContent.setIcon(new ImageIcon(createImage()));
        } catch (PrinterException e) {
            jlContent.setIcon(null);
            throw e;
        }
    }

    /**
     * Creates an {@link Image} of the page that is currently to be printed. The image
     * will show the page content, scaled to the current zoom factor.
     * <p>
     * Note that this method may require some time and memory.
     * 
     * @return {@link Image} containing the printed and scaled page.
     * @throws PrinterException
     *             Could not print to this image, e.g. if the current page does not exist.
     */
    protected Image createImage() throws PrinterException {
        PageFormat cFormat;
        Printable cPrintable;

        // --- Get the printable and format to be used ---
        if (printable != null && format != null) {
            cPrintable = printable;
            cFormat = format;
        } else {
            cPrintable = pageable.getPrintable(currentPage);
            cFormat = pageable.getPageFormat(currentPage);
        }

        // --- Compute pane size ---
        int width = (int) Math.ceil(cFormat.getWidth() * currentZoom);
        int height = (int) Math.ceil(cFormat.getHeight() * currentZoom);

        // --- Create the image buffer ---
        BufferedImage img = new BufferedImage(
                width + SHADOW_X,
                height + SHADOW_Y,
                (isLowMem()
                        ? BufferedImage.TYPE_BYTE_GRAY
                        : BufferedImage.TYPE_4BYTE_ABGR));
        Graphics2D g2d = (Graphics2D) img.getGraphics();

        // --- Draw the paper ---
        drawPaper(g2d, width, height);

        // --- Turn on antialiasing ---
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

        // --- Scale to the Zoom factor ---
        g2d.scale(currentZoom, currentZoom);

        // --- Draw the content ---
        int stat = cPrintable.print(g2d, cFormat, currentPage);
        g2d.dispose();

        if (stat == Printable.NO_SUCH_PAGE) throw new PrinterException("Page "
                                                                       + currentPage
                                                                       + ": NO_SUCH_PAGE");

        // --- Return the image ---
        return img;
    }

    /**
     * Get the page number of the currently shown page.
     * 
     * @return Page number
     */
    public int getPage() {
        return currentPage;
    }

    /**
     * Set the page number of the page to be shown. If the page does not exist, an empty
     * image will be shown instead.
     * <p>
     * <em>NOTE:</em> some {@link Printables} are unable to give random access to the
     * printed document. You cannot go back to a page that has been printed already, or go
     * forward by more than one page.
     * 
     * @param page
     *            New page index (starting from 0).
     */
    public void setPage(int page) throws PrinterException {
        try {
            currentPage = page;
            recreate();
        } catch (IndexOutOfBoundsException e) {}
    }

    /**
     * Turn forward one page. If the page does not exist, nothing will happen (except of
     * some wasted CPU time ;-) ).
     */
    public void turnForward() throws PrinterException {
        final int oldPage = currentPage;
        try {
            setPage(currentPage + 1);
        } catch (PrinterException e) {
            currentPage = oldPage;
            recreate();
            throw e;
        }
    }

    /**
     * Turn back one page. If the page does not exist, nothing will happen. If the
     * {@link Printable} is unable to go backward, weird things may happen.
     */
    public void turnBack() throws PrinterException {
        final int oldPage = currentPage;
        if (currentPage > 0) {
            try {
                setPage(currentPage - 1);
            } catch (PrinterException e) {
                currentPage = oldPage;
                recreate();
                throw e;
            }
        }
    }

    /**
     * Get the current zoom factor. Values smaller than 1 reduces the page, while values
     * larger than 1 magnifies it.
     * 
     * @return The current zoom factor
     */
    public double getZoomFactor() {
        return currentZoom;
    }

    /**
     * Set the current zoom factor. Values smaller than 1 reduces the page, while values
     * larger than 1 magnifies it. The smallest possible zoom factor is 0.01.
     * <p>
     * Note that this class creates an internal {@link Image} representation of the
     * current page, for performance reasons. It will consume a lot of memory on large
     * zoom factors! Setting {@link #setLowMem(boolean)} to <code>true</code> could help
     * you save some memory.
     * 
     * @param zoom
     *            The new zoom factor.
     */
    public void setZoomFactor(double zoom) {
        final double oldZoom = currentZoom;
        try {
            currentZoom = Math.max(0.01, zoom);
            recreate();
        } catch (PrinterException e) {
            currentZoom = oldZoom;
            try {
                recreate();
            } catch (PrinterException e2) {
                // we're boned! :(
            }
        }
    }

}
