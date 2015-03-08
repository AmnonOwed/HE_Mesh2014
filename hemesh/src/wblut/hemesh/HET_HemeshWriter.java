/*
 * 
 */
package wblut.hemesh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * 
 */
public class HET_HemeshWriter {
    
    /**
     * 
     */
    protected OutputStream hemeshStream;
    
    /**
     * 
     */
    protected PrintWriter hemeshWriter;

    /**
     * 
     *
     * @param stream 
     */
    public void beginSave(final OutputStream stream) {
	try {
	    hemeshStream = stream;
	    handleBeginSave();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * 
     *
     * @param file 
     */
    static private void createDirectories(final File file) {
	try {
	    final String parentName = file.getParent();
	    if (parentName != null) {
		final File parent = new File(parentName);
		if (!parent.exists()) {
		    parent.mkdirs();
		}
	    }
	} catch (final SecurityException se) {
	    System.err.println("No permissions to create "
		    + file.getAbsolutePath());
	}
    }

    /**
     * 
     *
     * @param fn 
     * @param name 
     */
    public void beginSave(final String fn, final String name) {
	try {
	    final File file = new File(fn, name + ".hemesh");
	    createDirectories(file);
	    hemeshStream = new FileOutputStream(file);
	    handleBeginSave();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * 
     */
    public void endSave() {
	try {
	    hemeshWriter.flush();
	    hemeshWriter.close();
	    hemeshStream.flush();
	    hemeshStream.close();
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * 
     */
    protected void handleBeginSave() {
	hemeshWriter = new PrintWriter(hemeshStream);
    }

    /**
     * 
     *
     * @param v 
     * @param heid 
     */
    public void vertex(final HE_Vertex v, final int heid) {
	hemeshWriter.println(v.xd() + " " + v.yd() + " " + v.zd() + " " + heid);
    }

    /**
     * 
     *
     * @param vid 
     * @param henextid 
     * @param hepairid 
     * @param faceid 
     */
    public void halfedge(final int vid, final int henextid, final int hepairid,
	    final int faceid) {
	hemeshWriter.println(vid + " " + henextid + " " + hepairid + " "
		+ faceid);
    }

    /**
     * 
     *
     * @param heid 
     */
    public void edge(final int heid) {
	hemeshWriter.println(heid);
    }

    /**
     * 
     *
     * @param heid 
     */
    public void face(final int heid) {
	hemeshWriter.println(heid);
    }

    /**
     * 
     *
     * @param v1 
     * @param v2 
     * @param v3 
     */
    public void sizes(final int v1, final int v2, final int v3) {
	hemeshWriter.println(v1 + " " + v2 + " " + v3);
    }
}
