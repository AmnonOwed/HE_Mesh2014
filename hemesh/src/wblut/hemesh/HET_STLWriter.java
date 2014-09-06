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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import wblut.geom.WB_Coordinate;
import wblut.geom.WB_Vector;

/**
 * A simple, but flexible and memory efficient exporter for binary STL files.
 * Custom color support is implemented via the STLcolorModel interface and the
 * exporter comes with the 2 most common format variations defined by the
 * DEFAULT and MATERIALISE constants.
 *
 * The minimal design of this exporter means it does not build an extra list of
 * faces in RAM and so is able to easily export models with millions of faces.
 *
 * http://en.wikipedia.org/wiki/STL_(file_format)
 */
public class HET_STLWriter {

	public static final int DEFAULT_RGB = -1;

	public static final STLColorModel NONE = new NoColorModel();

	public static final STLColorModel DEFAULT = new DefaultSTLColorModel();

	public static final STLColorModel MATERIALISE = new MaterialiseSTLColorModel(
			0xffffffff);

	public static final int DEFAULT_BUFFER = 0x10000;

	protected OutputStream ds;
	protected byte[] buf = new byte[4];
	protected int bufferSize;

	protected WB_Vector scale = new WB_Vector(1, 1, 1);
	protected boolean useInvertedNormals = false;

	protected STLColorModel colorModel;

	public HET_STLWriter() {
		this(DEFAULT, DEFAULT_BUFFER);
	}

	public HET_STLWriter(final STLColorModel cm, final int bufSize) {
		colorModel = cm;
		this.bufferSize = bufSize;
	}

	public void beginSave(final OutputStream stream, final int numFaces) {

		try {
			ds = new BufferedOutputStream(new DataOutputStream(stream),
					bufferSize);
			writeHeader(numFaces);
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

	public void beginSave(final String fn, final String name, final int numFaces) {

		try {
			beginSave(createOutputStream(new File(fn, name + ".stl")), numFaces);
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void endSave() {
		try {
			ds.flush();
			ds.close();
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void face(final WB_Coordinate a, final WB_Coordinate b,
			final WB_Coordinate c, final WB_Coordinate normal) {
		face(a, b, c, normal, DEFAULT_RGB);
	}

	public void face(final WB_Coordinate a, final WB_Coordinate b,
			final WB_Coordinate c, final WB_Coordinate normal, final int rgb) {
		try {
			writeVector(normal);
			// vertices
			writeScaledVector(a);
			writeScaledVector(b);
			writeScaledVector(c);
			// vertex attrib (color)
			if (rgb != DEFAULT_RGB) {
				writeShort(colorModel.formatRGB(rgb));
			}
			else {
				writeShort(colorModel.getDefaultRGB());
			}
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private final void prepareBuffer(final int a) {
		buf[3] = (byte) (a >>> 24);
		buf[2] = (byte) (a >> 16 & 0xff);
		buf[1] = (byte) (a >> 8 & 0xff);
		buf[0] = (byte) (a & 0xff);
	}

	public void setScale(final float s) {
		scale.set(s, s, s);
	}

	public void setScale(final WB_Coordinate s) {
		scale.set(s);
	}

	public void useInvertedNormals(final boolean state) {
		useInvertedNormals = state;
	}

	protected void writeFloat(final float a) throws IOException {
		prepareBuffer(Float.floatToRawIntBits(a));
		ds.write(buf, 0, 4);
	}

	protected void writeHeader(final int num) throws IOException {
		final byte[] header = new byte[80];
		colorModel.formatHeader(header);
		ds.write(header, 0, 80);
		writeInt(num);
	}

	protected void writeInt(final int a) throws IOException {
		prepareBuffer(a);
		ds.write(buf, 0, 4);
	}

	protected void writeScaledVector(final WB_Coordinate v) {
		try {
			writeFloat(v.xf() * scale.xf());
			writeFloat(v.yf() * scale.yf());
			writeFloat(v.zf() * scale.zf());
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	protected void writeShort(final int a) throws IOException {
		buf[0] = (byte) (a & 0xff);
		buf[1] = (byte) (a >> 8 & 0xff);
		ds.write(buf, 0, 2);
	}

	protected void writeVector(final WB_Coordinate v) {
		try {
			writeFloat(v.xf());
			writeFloat(v.yf());
			writeFloat(v.zf());
		}
		catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static interface STLColorModel {

		void formatHeader(byte[] header);

		int formatRGB(int rgb);

		int getDefaultRGB();
	}

	public static class NoColorModel implements STLColorModel {

		@Override
		public void formatHeader(final byte[] header) {
		}

		@Override
		public int formatRGB(final int rgb) {

			return 0;
		}

		@Override
		public int getDefaultRGB() {
			return 0;
		}
	}

	public static class DefaultSTLColorModel implements STLColorModel {

		@Override
		public void formatHeader(final byte[] header) {
		}

		@Override
		public int formatRGB(final int rgb) {
			int col15bits = (rgb >> 3 & 0x1f);
			col15bits |= (rgb >> 11 & 0x1f) << 5;
			col15bits |= (rgb >> 19 & 0x1f) << 10;
			col15bits |= 0x8000;
			return col15bits;
		}

		@Override
		public int getDefaultRGB() {
			return 0;
		}
	}

	public static class MaterialiseSTLColorModel implements STLColorModel {

		protected int baseColor;
		protected boolean useFacetColors;

		public MaterialiseSTLColorModel(final int rgb) {
			this(rgb, false);
		}

		public MaterialiseSTLColorModel(final int rgb,
				final boolean enableFacets) {
			baseColor = rgb;
			useFacetColors = enableFacets;
		}

		/**
		 * @param enabled
		 *            the useFacetColors to set
		 */
		public void enableFacetColors(final boolean enabled) {
			this.useFacetColors = enabled;
		}

		/**
		 * @return the useFacetColors
		 */
		public boolean facetColorsEnabled() {
			return useFacetColors;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see toxi.geom.mesh.STLColorModel#formatHeader(byte[])
		 */
		@Override
		public void formatHeader(final byte[] header) {
			final char[] col = new char[] { 'C', 'O', 'L', 'O', 'R', '=' };
			for (int i = 0; i < col.length; i++) {
				header[i] = (byte) col[i];
			}
			header[6] = (byte) (baseColor >> 16 & 0xff);
			header[7] = (byte) (baseColor >> 8 & 0xff);
			header[8] = (byte) (baseColor & 0xff);
			header[9] = (byte) (baseColor >>> 24);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see toxi.geom.mesh.STLColorModel#formatRGB(int)
		 */
		@Override
		public int formatRGB(final int rgb) {
			int col15bits = (rgb >> 19 & 0x1f);
			col15bits |= (rgb >> 11 & 0x1f) << 5;
			col15bits |= (rgb >> 3 & 0x1f) << 10;
			if (!useFacetColors) {
				// set bit 15 to indicate use of base color
				col15bits |= 0x8000;
			}
			return col15bits;
		}

		/**
		 * @return the baseColor
		 */
		public int getBaseColor() {
			return baseColor;
		}

		@Override
		public int getDefaultRGB() {
			// set bit 15 to indicate use of base color
			return 0x8000;
		}

		/**
		 * @param baseColor
		 *            the baseColor to set
		 */
		public void setBaseColor(final int baseColor) {
			this.baseColor = baseColor;
		}
	}
}
