/*
 * 
 */
package wblut.hemesh;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

/**
 * Helper class for HE_Export.saveToBinaryHemesh.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HET_BinaryHemeshWriter {
    /** The hemesh stream. */
    protected FileOutputStream hemeshStream;
    /** The hemesh writer. */
    protected DataOutputStream hemeshWriter;

    /**
     * Begin save.
     *
     * @param stream
     *            the stream
     */
    public void beginSave(final FileOutputStream stream) {
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
     * Begin save.
     *
     * @param fn            the fn
     * @param name 
     */
    public void beginSave(final String fn, final String name) {
	try {
	    final File file = new File(fn, name + ".binhemesh");
	    createDirectories(file);
	    hemeshStream = new FileOutputStream(file);
	    handleBeginSave();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    /**
     * End save.
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
     * Handle begin save.
     */
    protected void handleBeginSave() {
	hemeshWriter = new DataOutputStream(new DeflaterOutputStream(
		hemeshStream));
    }

    /**
     * Vertex.
     *
     * @param v
     *            the v
     * @param heid
     *            the heid
     */
    public void vertex(final HE_Vertex v, final int heid) {
	try {
	    hemeshWriter.writeDouble(v.xd());
	    hemeshWriter.writeDouble(v.yd());
	    hemeshWriter.writeDouble(v.zd());
	    hemeshWriter.writeInt(heid);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Halfedge.
     *
     * @param vid
     *            the vid
     * @param henextid
     *            the henextid
     * @param hepairid
     *            the hepairid
     * @param faceid
     *            the faceid
     */
    public void halfedge(final int vid, final int henextid, final int hepairid,
	    final int faceid) {
	try {
	    hemeshWriter.writeInt(vid);
	    hemeshWriter.writeInt(henextid);
	    hemeshWriter.writeInt(hepairid);
	    hemeshWriter.writeInt(faceid);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Edge.
     *
     * @param heid
     *            the heid
     */
    public void edge(final int heid) {
	try {
	    hemeshWriter.writeInt(heid);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Face.
     *
     * @param heid
     *            the heid
     */
    public void face(final int heid) {
	try {
	    hemeshWriter.writeInt(heid);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Sizes.
     *
     * @param v1            the v1
     * @param v2            the v2
     * @param v3            the v3
     */
    public void sizes(final int v1, final int v2, final int v3) {
	try {
	    hemeshWriter.writeInt(v1);
	    hemeshWriter.writeInt(v2);
	    hemeshWriter.writeInt(v3);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }
}
