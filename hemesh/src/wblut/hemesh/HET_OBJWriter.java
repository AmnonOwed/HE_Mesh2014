package wblut.hemesh;

//Straight port from Karsten Schmidt's code
/*

 *   __               .__       .__  ._____.
 * _/  |_  _______  __|__| ____ |  | |__\_ |__   ______
 * \   __\/  _ \  \/  /  |/ ___\|  | |  || __ \ /  ___/
 *  |  | (  <_> >    <|  \  \___|  |_|  || \_\ \\___ \
 *  |__|  \____/__/\_ \__|\___  >____/__||___  /____  >
 *                   \/       \/             \/     \/
 *
 * Copyright (c) 2006-2011 Karsten Schmidt
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * http://creativecommons.org/licenses/LGPL/2.1/
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
 */
/**
 * Extremely bare bones Wavefront OBJ 3D format exporter. Purely handles the
 * writing of data to the .obj file, but does not have any form of mesh
 * management. See {@link TriangleMesh} for details.
 *
 * Needs to get some more TLC in future versions.
 *
 * @see TriangleMesh#saveAsOBJ(OBJWriter)
 */

import gnu.trove.map.TLongIntMap;
import gnu.trove.map.hash.TLongIntHashMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.zip.GZIPOutputStream;

import wblut.geom.WB_Coordinate;

/**
 * The Class HET_OBJWriter.
 */
public class HET_OBJWriter {

	protected static OutputStream objStream;

	protected static PrintWriter objWriter;

	protected static OutputStream mtlStream;

	protected static PrintWriter mtlWriter;

	protected static int numVerticesWritten = 0;

	protected static int numNormalsWritten = 0;

	/**
	 * Begin save.
	 *
	 * @param fn
	 *            the fn
	 */
	public static void beginSave(final String fn, final String name) {
		try {
			objStream = createOutputStream(new File(fn, name + ".obj"));
			mtlStream = createOutputStream(new File(fn, name + ".mtl"));
			handleBeginSave();
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	static public OutputStream createOutputStream(final File file)
			throws IOException {
		if (file == null) {
			throw new IllegalArgumentException("file can't be null");
		}
		createDirectories(file);
		OutputStream stream = new FileOutputStream(file);
		if (file.getName().toLowerCase().endsWith(".gz")) {
			stream = new GZIPOutputStream(stream);
		}
		return stream;
	}

	static public void createDirectories(final File file) {
		try {
			final String parentName = file.getParent();
			if (parentName != null) {
				final File parent = new File(parentName);
				if (!parent.exists()) {
					parent.mkdirs();
				}
			}
		}
		catch (final SecurityException se) {
			System.err.println("No permissions to create "
					+ file.getAbsolutePath());
		}
	}

	/**
	 * End save.
	 */
	public static void endSave() {
		try {
			objWriter.flush();
			objWriter.close();
			objStream.flush();
			objStream.close();
			mtlWriter.flush();
			mtlWriter.close();
			mtlStream.flush();
			mtlStream.close();
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Face.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @param c
	 *            the c
	 */
	public static void face(final int a, final int b, final int c) {
		objWriter.println("f " + a + " " + b + " " + c);
	}

	public static void facecolor(final int i, final int c) {
		mtlWriter.println("newmtl f" + i);
		mtlWriter.println("Kd " + red(c) + " " + green(c) + " " + blue(c));
		mtlWriter.println("illum 0");
	}

	public static final float red(final int what) {
		return ((what >> 16) & 0xff) / 255.0f;

	}

	public static final float green(final int what) {
		return ((what >> 8) & 0xff) / 255.0f;

	}

	public static final float blue(final int what) {
		return ((what) & 0xff) / 255.0f;
	}

	/**
	 * Face list.
	 */
	public static void faceList() {
		objWriter.println("s off");
	}

	/**
	 * Face with normals.
	 *
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @param c
	 *            the c
	 * @param na
	 *            the na
	 * @param nb
	 *            the nb
	 * @param nc
	 *            the nc
	 */
	public static void faceWithNormals(final int a, final int b, final int c,
			final int na, final int nb, final int nc) {
		objWriter.println("f " + a + "//" + na + " " + b + "//" + nb + " " + c
				+ "//" + nc);
	}

	/**
	 * Gets the curr normal offset.
	 *
	 * @return the curr normal offset
	 */
	public static int getCurrNormalOffset() {
		return numNormalsWritten;
	}

	/**
	 * Gets the curr vertex offset.
	 *
	 * @return the curr vertex offset
	 */
	public static int getCurrVertexOffset() {
		return numVerticesWritten;
	}

	/**
	 * Handle begin save.
	 */
	protected static void handleBeginSave() {
		objWriter = new PrintWriter(objStream);
		objWriter.println("# generated by HET_OBJWriter");
		mtlWriter = new PrintWriter(mtlStream);
		mtlWriter.println("# generated by HET_OBJWriter");
		numVerticesWritten = 0;
		numNormalsWritten = 0;
	}

	/**
	 * New object.
	 *
	 * @param name
	 *            the name
	 */
	public static void newObject(final String name) {
		objWriter.println("o " + name);
	}

	/**
	 * Normal.
	 *
	 * @param n
	 *            the n
	 */
	public static void normal(final WB_Coordinate n) {
		objWriter.println("vn " + n.xd() + " " + n.yd() + " " + n.zd());
		numNormalsWritten++;
	}

	/**
	 * Vertex.
	 *
	 * @param v
	 *            the v
	 */
	public static void vertex(final WB_Coordinate v) {
		objWriter.println("v " + v.xd() + " " + v.yd() + " " + v.zd());
		numVerticesWritten++;
	}

	public static void vertexcolor(final int i, final int c) {
		mtlWriter.println("newmtl v" + i);
		mtlWriter.println("Kd " + red(c) + " " + green(c) + " " + blue(c));
		mtlWriter.println("illum 0");
	}

	/*
	 * Copyright (c) 2006-2011 Karsten Schmidt This library is free software;
	 * you can redistribute it and/or modify it under the terms of the GNU
	 * Lesser General Public License as published by the Free Software
	 * Foundation; either version 2.1 of the License, or (at your option) any
	 * later version. http://creativecommons.org/licenses/LGPL/2.1/ This library
	 * is distributed in the hope that it will be useful, but WITHOUT ANY
	 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
	 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
	 * more details. You should have received a copy of the GNU Lesser General
	 * Public License along with this library; if not, write to the Free
	 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
	 * 02110-1301, USA
	 */

	public static void saveMesh(final HE_Mesh mesh, final String path,
			final String name) {
		beginSave(path, name);
		final int vOffset = getCurrVertexOffset() + 1;
		final int nOffset = getCurrNormalOffset() + 1;
		newObject(new Long(mesh.getKey()).toString());
		// vertices
		final TLongIntMap keyToIndex = new TLongIntHashMap(10, 0.5f, -1L, -1);
		Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		int i = 0;
		while (vItr.hasNext()) {
			v = vItr.next();
			keyToIndex.put(v.key(), i);
			vertex(v);
			i++;
		}

		vItr = mesh.vItr();
		while (vItr.hasNext()) {
			normal(vItr.next().getVertexNormal());
		}

		// faces
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			faceWithNormals(keyToIndex.get(he.getVertex().key()) + vOffset,
					keyToIndex.get(he.getNextInFace().getVertex().key())
					+ vOffset,
					keyToIndex.get(he.getPrevInFace().getVertex().key())
							+ vOffset, keyToIndex.get(he.getVertex().key())
					+ nOffset,
					keyToIndex.get(he.getNextInFace().getVertex().key())
							+ nOffset,
					keyToIndex.get(he.getPrevInFace().getVertex().key())
							+ nOffset);
		}
		endSave();
	}

	public static void saveMeshWithFaceColor(final HE_Mesh mesh,
			final String path, final String name) {
		beginSave(path, name);
		final int vOffset = getCurrVertexOffset() + 1;
		final int nOffset = getCurrNormalOffset() + 1;
		objWriter.println("mtllib " + name + ".mtl");
		newObject(new Long(mesh.getKey()).toString());
		// vertices
		final TLongIntMap keyToIndex = new TLongIntHashMap(10, 0.5f, -1L, -1);
		Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		int i = 0;
		while (vItr.hasNext()) {
			v = vItr.next();
			keyToIndex.put(v.key(), i);
			vertex(v);
			i++;
		}

		vItr = mesh.vItr();
		while (vItr.hasNext()) {
			normal(vItr.next().getVertexNormal());
		}

		// faces
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		HE_Halfedge he;
		int fi = 0;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			facecolor(fi, f.getColor());
			objWriter.println("usemtl f" + (fi++));
			faceWithNormals(keyToIndex.get(he.getVertex().key()) + vOffset,
					keyToIndex.get(he.getNextInFace().getVertex().key())
					+ vOffset,
					keyToIndex.get(he.getPrevInFace().getVertex().key())
							+ vOffset, keyToIndex.get(he.getVertex().key())
					+ nOffset,
					keyToIndex.get(he.getNextInFace().getVertex().key())
							+ nOffset,
					keyToIndex.get(he.getPrevInFace().getVertex().key())
							+ nOffset);
		}
		endSave();
	}

	public static void saveMeshWithVertexColor(final HE_Mesh mesh,
			final String path, final String name) {
		beginSave(path, name);
		final int vOffset = getCurrVertexOffset() + 1;
		final int nOffset = getCurrNormalOffset() + 1;
		objWriter.println("mtllib " + name + ".mtl");
		newObject(new Long(mesh.getKey()).toString());
		// vertices
		final TLongIntMap keyToIndex = new TLongIntHashMap(10, 0.5f, -1L, -1);
		Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		int i = 0;
		while (vItr.hasNext()) {
			v = vItr.next();
			vertexcolor(i, v.getColor());
			objWriter.println("usemtl v" + (i));
			keyToIndex.put(v.key(), i);
			vertex(v);
			i++;
		}

		vItr = mesh.vItr();
		while (vItr.hasNext()) {
			normal(vItr.next().getVertexNormal());
		}

		// faces
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		HE_Halfedge he;

		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			faceWithNormals(keyToIndex.get(he.getVertex().key()) + vOffset,
					keyToIndex.get(he.getNextInFace().getVertex().key())
							+ vOffset,
					keyToIndex.get(he.getPrevInFace().getVertex().key())
					+ vOffset, keyToIndex.get(he.getVertex().key())
							+ nOffset,
					keyToIndex.get(he.getNextInFace().getVertex().key())
					+ nOffset,
					keyToIndex.get(he.getPrevInFace().getVertex().key())
					+ nOffset);
		}
		endSave();
	}

}
