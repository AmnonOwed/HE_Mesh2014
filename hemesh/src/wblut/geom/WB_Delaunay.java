//
// Delaunay.java
//
/*
 VisAD system for interactive analysis and visualization of numerical
 data.  Copyright (C) 1996 - 2011 Bill Hibbard, Curtis Rueden, Tom
 Rink, Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and
 Tommy Jasmin.

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Library General Public
 License as published by the Free Software Foundation; either
 version 2 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Library General Public License for more details.

 You should have received a copy of the GNU Library General Public
 License along with this library; if not, write to the Free
 Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 MA 02111-1307, USA
 */
package wblut.geom;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Delaunay represents an abstract class for calculating an N-dimensional
 * Delaunay triangulation, that can be extended to allow for various
 * triangulation algorithms.
 * <P>
 */
public abstract class WB_Delaunay {
    /**
     * triangles/tetrahedra --> vertices.
     * <p>
     * Tri = new int[ntris][dim + 1]
     *
     * <p>
     * This is the key output, a list of triangles (in two dimensions,
     * tetrahedra in three dimensions, etc). ntris is the number of triangles.
     *
     * <p>
     * In 2-D, Tri[i] is an array of 3 integers, which are three indices into
     * the samples[0] and samples[1] arrays to get the x and y values of the
     * three vertices of the triangle.
     *
     * <p>
     * In 3-D, Tri[i] is an array of 4 integers, which are four indices into the
     * samples[0], samples[1] and samples[2] arrays to get the x, y and z values
     * of the four vertices of the tetrahedron.
     *
     * <p>
     * This pattern continues for higher dimensionalities.
     */
    public int[][] Tri;
    /**
     * vertices --> triangles/tetrahedra.
     * <p>
     * Vertices = new int[nrs][nverts[i]]
     *
     * <p>
     * nrs is the number of samples (the length of the samples[0] and samples[1]
     * arrays. For sample i, Vertices[i] is a (variable length) list of indices
     * into the Tri array above, giving the indices of the triangles that
     * include vertex i.
     *
     * <p>
     * nverts is an array as the second index of the Vertices array since
     * different vertices may be part of different numbers of triangles.
     *
     * <p>
     * You can use Tri and Vertices together to traverse the triangulation. If
     * you don't need to traverse, then you can probably ignore all arrays
     * except Tri.
     */
    public int[][] Vertices;
    
    /**
     * 
     */
    public int[][] Neighbors;
    /**
     * triangles/tetrahedra --> triangles/tetrahedra.
     * <p>
     * Walk = new int[ntris][dim + 1]
     *
     * <p>
     * Also useful for traversing the triangulation, in this case giving the
     * indices of triangles that share edges with the current triangle.
     */
    public int[][] Walk;
    /**
     * tri/tetra edges --> global edge number.
     * <p>
     * Edges = new int[ntris][3 * (dim - 1)];
     *
     * <p>
     * 'global edge number' is the number of an edge that is unique among the
     * whole triangulation. This number is not an index into any array, but will
     * match for a shared edge between two triangles.
     */
    public int[][] Edges;
    
    /**
     *  number of unique global edge numbers.
     */
    public int NumEdges;
    
    /**
     * 
     */
    public double[] circumradii;
    
    /**
     * 
     */
    public WB_Point[] circumcenters;

    /**
     * The abstract constructor initializes the class's data arrays.
     *
     */
    protected WB_Delaunay() {
	Tri = null;
	Vertices = null;
	Walk = null;
	Edges = null;
	NumEdges = 0;
    }

    /**
     * 
     *
     * @param points 
     * @param exact 
     * @return 
     */
    public static WB_Delaunay getTriangulation2D(final WB_Coordinate[] points,
	    final boolean exact) {
	final double[][] samples = new double[2][points.length];
	for (int i = 0; i < points.length; i++) {
	    samples[0][i] = points[i].xd();
	    samples[1][i] = points[i].yd();
	}
	return getTriangulation(samples, 1, exact);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public static WB_Delaunay getTriangulation2D(final WB_Coordinate[] points) {
	final double[][] samples = new double[2][points.length];
	for (int i = 0; i < points.length; i++) {
	    samples[0][i] = points[i].xd();
	    samples[1][i] = points[i].yd();
	}
	return getTriangulation(samples, 1, true);
    }

    /**
     * 
     *
     * @param points 
     * @param epsilon 
     * @param exact 
     * @return 
     */
    public static WB_Delaunay getTriangulation2D(final WB_Coordinate[] points,
	    final double epsilon, final boolean exact) {
	final double[][] samples = new double[2][points.length];
	for (int i = 0; i < points.length; i++) {
	    samples[0][i] = points[i].xd()
		    + (2 * epsilon * (Math.random() - 0.5));
	    samples[1][i] = points[i].yd()
		    + (2 * epsilon * (Math.random() - 0.5));
	}
	return getTriangulation(samples, 1, true);
    }

    /**
     * 
     *
     * @param points 
     * @param epsilon 
     * @return 
     */
    public static WB_Delaunay getTriangulation2D(final WB_Coordinate[] points,
	    final double epsilon) {
	final double[][] samples = new double[2][points.length];
	for (int i = 0; i < points.length; i++) {
	    samples[0][i] = points[i].xd()
		    + (2 * epsilon * (Math.random() - 0.5));
	    samples[1][i] = points[i].yd()
		    + (2 * epsilon * (Math.random() - 0.5));
	}
	return getTriangulation(samples, 1, true);
    }

    /**
     * 
     *
     * @param points 
     * @param closest 
     * @return 
     */
    public static WB_Delaunay getTriangulation3D(final WB_Coordinate[] points,
	    final double closest) {
	final double[][] samples = new double[3][points.length];
	for (int i = 0; i < points.length; i++) {
	    samples[0][i] = points[i].xd();
	    samples[1][i] = points[i].yd();
	    samples[2][i] = points[i].zd();
	}
	return getTriangulation(samples, closest, true);
    }

    /**
     * 
     *
     * @param points 
     * @param closest 
     * @param epsilon 
     * @return 
     */
    public static WB_Delaunay getTriangulation3D(final WB_Coordinate[] points,
	    final double closest, final double epsilon) {
	final double[][] samples = new double[3][points.length];
	for (int i = 0; i < points.length; i++) {
	    samples[0][i] = points[i].xd()
		    + (2 * epsilon * (Math.random() - 0.5));
	    samples[1][i] = points[i].yd()
		    + (2 * epsilon * (Math.random() - 0.5));
	    samples[2][i] = points[i].zd()
		    + (2 * epsilon * (Math.random() - 0.5));
	}
	return getTriangulation(samples, closest, true);
    }

    /**
     * 
     *
     * @param points 
     * @param closest 
     * @return 
     */
    public static WB_Delaunay getTriangulation3D(
	    final WB_CoordinateSequence points, final double closest) {
	final double[][] samples = new double[3][points.size()];
	int id = 0;
	for (int i = 0; i < points.size(); i++) {
	    samples[0][i] = points.getRaw(id++);
	    samples[1][i] = points.getRaw(id++);
	    samples[2][i] = points.getRaw(id++);
	    id++;
	}
	return getTriangulation(samples, closest, true);
    }

    /**
     * 
     *
     * @param points 
     * @param closest 
     * @param epsilon 
     * @return 
     */
    public static WB_Delaunay getTriangulation3D(
	    final WB_CoordinateSequence points, final double closest,
	    final double epsilon) {
	final double[][] samples = new double[3][points.size()];
	int id = 0;
	for (int i = 0; i < points.size(); i++) {
	    samples[0][i] = points.getRaw(id++)
		    + (2 * epsilon * (Math.random() - 0.5));
	    samples[1][i] = points.getRaw(id++)
		    + (2 * epsilon * (Math.random() - 0.5));
	    samples[2][i] = points.getRaw(id++)
		    + (2 * epsilon * (Math.random() - 0.5));
	    id++;
	}
	return getTriangulation(samples, closest, true);
    }

    /**
     * 
     *
     * @param points 
     * @param closest 
     * @return 
     */
    public static WB_Delaunay getTriangulation4D(final WB_Coordinate[] points,
	    final double closest) {
	final double[][] samples = new double[4][points.length];
	for (int i = 0; i < points.length; i++) {
	    samples[0][i] = points[i].xd();
	    samples[1][i] = points[i].yd();
	    samples[2][i] = points[i].zd();
	    samples[3][i] = points[i].wd();
	}
	return getTriangulation(samples, closest, true);
    }

    /**
     * 
     *
     * @param points 
     * @param closest 
     * @param epsilon 
     * @return 
     */
    public static WB_Delaunay getTriangulation4D(final WB_Coordinate[] points,
	    final double closest, final double epsilon) {
	final double[][] samples = new double[4][points.length];
	for (int i = 0; i < points.length; i++) {
	    samples[0][i] = points[i].xd()
		    + (2 * epsilon * (Math.random() - 0.5));
	    samples[1][i] = points[i].yd()
		    + (2 * epsilon * (Math.random() - 0.5));
	    samples[2][i] = points[i].zd()
		    + (2 * epsilon * (Math.random() - 0.5));
	    samples[3][i] = points[i].wd()
		    + (2 * epsilon * (Math.random() - 0.5));
	}
	return getTriangulation(samples, closest, true);
    }

    //
    // DelaunayClarkson.java
    //
    /*
     * VisAD system for interactive analysis and visualization of numerical
     * data. Copyright (C) 1996 - 2011 Bill Hibbard, Curtis Rueden, Tom Rink,
     * Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and Tommy
     * Jasmin.
     * 
     * This library is free software; you can redistribute it and/or modify it
     * under the terms of the GNU Library General Public License as published by
     * the Free Software Foundation; either version 2 of the License, or (at
     * your option) any later version.
     * 
     * This library is distributed in the hope that it will be useful, but
     * WITHOUT ANY WARRANTY; without even the implied warranty of
     * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library
     * General Public License for more details.
     * 
     * You should have received a copy of the GNU Library General Public License
     * along with this library; if not, write to the Free Software Foundation,
     * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
     */
    /*
     * The Delaunay triangulation algorithm in this class is originally from
     * hull by Ken Clarkson:
     * 
     * Ken Clarkson wrote this. Copyright (c) 1995 by AT&T.. Permission to use,
     * copy, modify, and distribute this software for any purpose without fee is
     * hereby granted, provided that this entire notice is included in all
     * copies of any software which is or includes a copy or modification of
     * this software and in all copies of the supporting documentation for such
     * software. THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR
     * IMPLIED WARRANTY. IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
     * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY OF
     * THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
     */
    /**
     * 
     *
     * @param points 
     * @param exact 
     * @return 
     */
    public static WB_Delaunay getTriangulation2D(
	    final List<? extends WB_Coordinate> points, final boolean exact) {
	final double[][] samples = new double[2][points.size()];
	WB_Coordinate point;
	for (int i = 0; i < points.size(); i++) {
	    point = points.get(i);
	    samples[0][i] = point.xd();
	    samples[1][i] = point.yd();
	}
	return getTriangulation(samples, 1, exact);
    }

    /**
     * 
     *
     * @param points 
     * @param epsilon 
     * @param exact 
     * @return 
     */
    public static WB_Delaunay getTriangulation2D(
	    final List<? extends WB_Coordinate> points, final double epsilon,
		    final boolean exact) {
	final double[][] samples = new double[2][points.size()];
	WB_Coordinate point;
	for (int i = 0; i < points.size(); i++) {
	    point = points.get(i);
	    samples[0][i] = point.xd() + (2 * epsilon * (Math.random() - 0.5));
	    samples[1][i] = point.yd() + (2 * epsilon * (Math.random() - 0.5));
	}
	return getTriangulation(samples, 1, exact);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public static WB_Delaunay getTriangulation2D(
	    final List<? extends WB_Coordinate> points) {
	final double[][] samples = new double[2][points.size()];
	WB_Coordinate point;
	for (int i = 0; i < points.size(); i++) {
	    point = points.get(i);
	    samples[0][i] = point.xd();
	    samples[1][i] = point.yd();
	}
	return getTriangulation(samples, 1, true);
    }

    /**
     * 
     *
     * @param points 
     * @param epsilon 
     * @return 
     */
    public static WB_Delaunay getTriangulation2D(
	    final List<? extends WB_Coordinate> points, final double epsilon) {
	final double[][] samples = new double[2][points.size()];
	WB_Coordinate point;
	for (int i = 0; i < points.size(); i++) {
	    point = points.get(i);
	    samples[0][i] = point.xd() + (2 * epsilon * (Math.random() - 0.5));
	    samples[1][i] = point.yd() + (2 * epsilon * (Math.random() - 0.5));
	}
	return getTriangulation(samples, 1, true);
    }

    /**
     * 
     *
     * @param points 
     * @param closest 
     * @return 
     */
    public static WB_Delaunay getTriangulation3D(
	    final List<? extends WB_Coordinate> points, final double closest) {
	final double[][] samples = new double[3][points.size()];
	WB_Coordinate point;
	for (int i = 0; i < points.size(); i++) {
	    point = points.get(i);
	    samples[0][i] = point.xd();
	    samples[1][i] = point.yd();
	    samples[2][i] = point.zd();
	}
	return getTriangulation(samples, closest, true);
    }

    /**
     * 
     *
     * @param points 
     * @param closest 
     * @param epsilon 
     * @return 
     */
    public static WB_Delaunay getTriangulation3D(
	    final List<? extends WB_Coordinate> points, final double closest,
		    final double epsilon) {
	final double[][] samples = new double[3][points.size()];
	WB_Coordinate point;
	for (int i = 0; i < points.size(); i++) {
	    point = points.get(i);
	    samples[0][i] = point.xd() + (2 * epsilon * (Math.random() - 0.5));
	    samples[1][i] = point.yd() + (2 * epsilon * (Math.random() - 0.5));
	    samples[2][i] = point.zd() + (2 * epsilon * (Math.random() - 0.5));
	}
	return getTriangulation(samples, closest, true);
    }

    /**
     * 
     *
     * @param points 
     * @param closest 
     * @return 
     */
    public static WB_Delaunay getTriangulation4D(
	    final List<? extends WB_Coordinate> points, final double closest) {
	final double[][] samples = new double[4][points.size()];
	WB_Coordinate point;
	for (int i = 0; i < points.size(); i++) {
	    point = points.get(i);
	    samples[0][i] = point.xd();
	    samples[1][i] = point.yd();
	    samples[2][i] = point.zd();
	    samples[3][i] = point.wd();
	}
	return getTriangulation(samples, closest, true);
    }

    /**
     * 
     *
     * @param points 
     * @param closest 
     * @param epsilon 
     * @return 
     */
    public static WB_Delaunay getTriangulation4D(
	    final List<? extends WB_Coordinate> points, final double closest,
		    final double epsilon) {
	final double[][] samples = new double[4][points.size()];
	WB_Coordinate point;
	for (int i = 0; i < points.size(); i++) {
	    point = points.get(i);
	    samples[0][i] = point.xd() + (2 * epsilon * (Math.random() - 0.5));
	    samples[1][i] = point.yd() + (2 * epsilon * (Math.random() - 0.5));
	    samples[2][i] = point.zd() + (2 * epsilon * (Math.random() - 0.5));
	    samples[3][i] = point.wd() + (2 * epsilon * (Math.random() - 0.5));
	}
	return getTriangulation(samples, closest, true);
    }

    /**
     * The factory class method heuristically decides which extension to the
     * Delaunay abstract class to use in order to construct the fastest
     * triangulation, and calls that extension, returning the finished
     * triangulation. The method chooses from among the Fast, Clarkson, and
     * Watson methods.
     *
     * @param samples
     *            locations of points for topology - dimensioned
     *            double[dimension][number_of_points]
     * @param closest
     * 
     * @param exact
     *            flag indicating need for exact Delaunay triangulation
     * @return a topology using an appropriate sub-class of Delaunay
     */
    protected static WB_Delaunay getTriangulation(final double[][] samples,
	    final double closest, final boolean exact) {
	/*
	 * Note: Clarkson doesn't work well for very closely clumped site
	 * values, since the algorithm rounds each value to the nearest integer
	 * before computing the triangulation. This fact should probably be
	 * taken into account in this factory algorithm, but as of yet is not.
	 * In other words, if you need an exact triangulation and have more than
	 * 3000 data sites, and they have closely clumped values, be sure to
	 * scale them up before calling the factory method.
	 */
	/*
	 * Note: The factory method will not take new Delaunay extensions into
	 * account unless it is extended as well.
	 */
	int choice;
	final int FAST = 0;
	final int CLARKSON = 1;
	final int WATSON = 2;
	final int dim = samples.length;
	if (dim < 2) {
	    throw new IllegalArgumentException();
	}
	// only Clarkson can handle triangulations in high dimensions
	if (dim > 3) {
	    choice = CLARKSON;
	} else {
	    int nrs = samples[0].length;
	    for (int i = 1; i < dim; i++) {
		nrs = Math.min(nrs, samples[i].length);
	    }
	    if ((dim == 2) && !exact && (nrs > 10000)) {
		// use fast in 2-D with a very large set and exact not required
		choice = FAST;
	    } else if (nrs > 0) {
		// use Clarkson for large sets
		choice = CLARKSON;
	    } else {
		choice = WATSON;
	    }
	}
	try {
	    if (choice == FAST) {
		// triangulate with the Fast method and one improvement pass
		final DelaunayFast delan = new DelaunayFast(samples);
		delan.improve(samples, 1);
		return delan;
	    }
	    if (choice == CLARKSON) {
		// triangulate with the Clarkson method
		final DelaunayClarkson delan = new DelaunayClarkson(samples,
			closest);
		return delan;
	    }
	    if (choice == WATSON) {
		// triangulate with the Watson method
		final DelaunayWatson delan = new DelaunayWatson(samples);
		return delan;
	    }
	} catch (final Exception e) {
	    if (choice != CLARKSON) {
		try {
		    // triangulate with the Clarkson method
		    final DelaunayClarkson delan = new DelaunayClarkson(
			    samples, closest);
		    return delan;
		} catch (final Exception ee) {
		}
	    }
	}
	return null;
    }

    /**
     * alters the values of the samples by multiplying them by the mult factor.
     *
     * @param samples            locations of points for topology - dimensioned
     *            double[dimension][number_of_points]
     * @param mult            multiplication factor
     * @param copy            specifies whether scale should modify and return the argument
     *            samples array or a copy
     * @return array of scaled values
     */
    public static double[][] scale(final double[][] samples, final double mult,
	    final boolean copy) {
	final int dim = samples.length;
	int nrs = samples[0].length;
	for (int i = 1; i < dim; i++) {
	    if (samples[i].length < nrs) {
		nrs = samples[i].length;
	    }
	}
	// make a copy if needed
	final double[][] samp = copy ? new double[dim][] : samples;
	if (copy) {
	    for (int j = 0; j < dim; j++) {
		final int len = samples[j].length;
		samp[j] = new double[len];
		System.arraycopy(samples[j], 0, samp[j], 0, len);
	    }
	}
	// scale points
	for (int i = 0; i < dim; i++) {
	    for (int j = 0; j < nrs; j++) {
		samp[i][j] *= mult;
	    }
	}
	return samp;
    }

    /**
     * increments samples coordinates by random numbers between -epsilon and
     * epsilon, in order to eliminate triangulation problems such as co-linear
     * and co-located points.
     *
     * @param samples            locations of points for topology - dimensioned
     *            double[dimension][number_of_points]
     * @param epsilon            size limit on random perturbations
     * @param copy            specifies whether perturb should modify and return the
     *            argument samples array or a copy
     * @return array of perturbed values
     */
    public static double[][] perturb(final double[][] samples,
	    final double epsilon, final boolean copy) {
	final int dim = samples.length;
	int nrs = samples[0].length;
	for (int i = 1; i < dim; i++) {
	    if (samples[i].length < nrs) {
		nrs = samples[i].length;
	    }
	}
	// make a copy if needed
	final double[][] samp = copy ? new double[dim][] : samples;
	if (copy) {
	    for (int j = 0; j < dim; j++) {
		final int len = samples[j].length;
		samp[j] = new double[len];
		System.arraycopy(samples[j], 0, samp[j], 0, len);
	    }
	}
	// perturb points
	for (int i = 0; i < dim; i++) {
	    for (int j = 0; j < nrs; j++) {
		samp[i][j] += 2 * epsilon * (Math.random() - 0.5);
	    }
	}
	return samp;
    }

    /**
     * check this triangulation in various ways to make sure it is constructed
     * correctly. This method is expensive, provided mainly for debugging
     * purposes.
     *
     * @param samples
     *            locations of points for topology - dimensioned
     *            double[dimension][number_of_points]
     * @return flag that is false to indicate there are problems with the
     *         triangulation
     */
    public boolean test(final double[][] samples) {
	return test(samples, false);
    }

    /**
     * 
     *
     * @param samples 
     * @param printErrors 
     * @return 
     */
    public boolean test(final double[][] samples, final boolean printErrors) {
	final int dim = samples.length;
	final int dim1 = dim + 1;
	final int ntris = Tri.length;
	int nrs = samples[0].length;
	for (int i = 1; i < dim; i++) {
	    nrs = Math.min(nrs, samples[i].length);
	}
	// verify triangulation dimension
	for (int i = 0; i < ntris; i++) {
	    if (Tri[i].length < dim1) {
		if (printErrors) {
		    System.err.println("Delaunay.test: invalid triangulation "
			    + "dimension (Tri[" + i + "].length="
			    + Tri[i].length + "; dim1=" + dim1 + ")");
		}
		return false;
	    }
	}
	// verify no illegal triangle vertices
	for (int i = 0; i < ntris; i++) {
	    for (int j = 0; j < dim1; j++) {
		if ((Tri[i][j] < 0) || (Tri[i][j] >= nrs)) {
		    if (printErrors) {
			System.err
			.println("Delaunay.test: illegal triangle vertex ("
				+ "Tri["
				+ i
				+ "]["
				+ j
				+ "]="
				+ Tri[i][j] + "; nrs=" + nrs + ")");
		    }
		    return false;
		}
	    }
	}
	// verify that all points are in at least one triangle
	final int[] nverts = new int[nrs];
	for (int i = 0; i < nrs; i++) {
	    nverts[i] = 0;
	}
	for (int i = 0; i < ntris; i++) {
	    for (int j = 0; j < dim1; j++) {
		nverts[Tri[i][j]]++;
	    }
	}
	for (int i = 0; i < nrs; i++) {
	    if (nverts[i] == 0) {
		if (printErrors) {
		    System.err.println("Delaunay.test: point not in triangle ("
			    + "nverts[" + i + "]=0)");
		}
		return false;
	    }
	}
	// test for duplicate triangles
	for (int i = 0; i < ntris; i++) {
	    for (int j = i + 1; j < ntris; j++) {
		final boolean[] m = new boolean[dim1];
		for (int mi = 0; mi < dim1; mi++) {
		    m[mi] = false;
		}
		for (int k = 0; k < dim1; k++) {
		    for (int l = 0; l < dim1; l++) {
			if ((Tri[i][k] == Tri[j][l]) && !m[l]) {
			    m[l] = true;
			}
		    }
		}
		boolean mtot = true;
		for (int k = 0; k < dim1; k++) {
		    if (!m[k]) {
			mtot = false;
		    }
		}
		if (mtot) {
		    if (printErrors) {
			System.err
			.println("Delaunay.test: duplicate triangles (i="
				+ i + "; j=" + j + ")");
		    }
		    return false;
		}
	    }
	}
	// test for errors in Walk array
	for (int i = 0; i < ntris; i++) {
	    for (int j = 0; j < dim1; j++) {
		if (Walk[i][j] != -1) {
		    boolean found = false;
		    for (int k = 0; k < dim1; k++) {
			if (Walk[Walk[i][j]][k] == i) {
			    found = true;
			}
		    }
		    if (!found) {
			if (printErrors) {
			    System.err
			    .println("Delaunay.test: error in Walk array (i="
				    + i + "; j=" + j + ")");
			}
			return false;
		    }
		    // make sure two walk'ed triangles share dim vertices
		    int sb = 0;
		    for (int k = 0; k < dim1; k++) {
			for (int l = 0; l < dim1; l++) {
			    if (Tri[i][k] == Tri[Walk[i][j]][l]) {
				sb++;
			    }
			}
		    }
		    if (sb != dim) {
			if (printErrors) {
			    System.err
			    .println("Delaunay.test: error in Walk array (i="
				    + i
				    + "; j="
				    + j
				    + "; sb="
				    + sb
				    + "; dim=" + dim + ")");
			}
			return false;
		    }
		}
	    }
	}
	// Note: Another test that could be performed is one that
	// makes sure, given a triangle T, all points in the
	// triangulation that are not part of T are located
	// outside the bounds of T. This test would verify
	// that there are no overlapping triangles.
	// all tests passed
	return true;
    }

    /**
     * use edge-flipping to bring the current triangulation closer to the true
     * Delaunay triangulation.
     *
     * @param samples
     *            locations of points for topology - dimensioned
     *            double[dimension][number_of_points]
     * @param pass
     *            the number of passes the algorithm should take over all edges
     *            (however, the algorithm terminates if no edges are flipped for
     *            an entire pass).
     */
    public void improve(final double[][] samples, final int pass) {
	final int dim = samples.length;
	final int dim1 = dim + 1;
	if (Tri[0].length != dim1) {
	    throw new IllegalArgumentException();
	}
	// only 2-D triangulations supported for now
	if (dim > 2) {
	    throw new IllegalArgumentException();
	}
	final int ntris = Tri.length;
	int nrs = samples[0].length;
	for (int i = 1; i < dim; i++) {
	    nrs = Math.min(nrs, samples[i].length);
	}
	final double[] samp0 = samples[0];
	final double[] samp1 = samples[1];
	// go through entire triangulation pass times
	boolean eflipped = false;
	for (int p = 0; p < pass; p++) {
	    eflipped = false;
	    // edge keeps track of which edges have been checked
	    final boolean[] edge = new boolean[NumEdges];
	    for (int i = 0; i < NumEdges; i++) {
		edge[i] = true;
	    }
	    // check every edge of every triangle
	    for (int t = 0; t < ntris; t++) {
		final int[] trit = Tri[t];
		final int[] walkt = Walk[t];
		final int[] edgest = Edges[t];
		for (int e = 0; e < 2; e++) {
		    final int curedge = edgest[e];
		    // only check the edge if it hasn't been checked yet
		    if (edge[curedge]) {
			final int t2 = walkt[e];
			// only check edge if it is not part of the outer hull
			if (t2 >= 0) {
			    final int[] trit2 = Tri[t2];
			    final int[] walkt2 = Walk[t2];
			    final int[] edgest2 = Edges[t2];
			    // check if the diagonal needs to be flipped
			    final int f = (walkt2[0] == t) ? 0
				    : (walkt2[1] == t) ? 1 : 2;
			    final int A = (e + 2) % 3;
			    final int B = (A + 1) % 3;
			    final int C = (B + 1) % 3;
			    final int D = (f + 2) % 3;
			    final double ax = samp0[trit[A]];
			    final double ay = samp1[trit[A]];
			    final double bx = samp0[trit[B]];
			    final double by = samp1[trit[B]];
			    final double cx = samp0[trit[C]];
			    final double cy = samp1[trit[C]];
			    final double dx = samp0[trit2[D]];
			    final double dy = samp1[trit2[D]];
			    final double abx = ax - bx;
			    final double aby = ay - by;
			    final double acx = ax - cx;
			    final double acy = ay - cy;
			    final double dbx = dx - bx;
			    final double dby = dy - by;
			    final double dcx = dx - cx;
			    final double dcy = dy - cy;
			    final double Q = (abx * acx) + (aby * acy);
			    final double R = (dbx * abx) + (dby * aby);
			    final double S = (acx * dcx) + (acy * dcy);
			    final double T = (dbx * dcx) + (dby * dcy);
			    final boolean QD = ((abx * acy) - (aby * acx)) >= 0;
			    final boolean RD = ((dbx * aby) - (dby * abx)) >= 0;
			    final boolean SD = ((acx * dcy) - (acy * dcx)) >= 0;
			    final boolean TD = ((dcx * dby) - (dcy * dbx)) >= 0;
			    final boolean sig = ((QD ? 1 : 0) + (RD ? 1 : 0)
				    + (SD ? 1 : 0) + (TD ? 1 : 0)) < 2;
			    boolean d;
			    if (QD == sig) {
				d = true;
			    } else if (RD == sig) {
				d = false;
			    } else if (SD == sig) {
				d = false;
			    } else if (TD == sig) {
				d = true;
			    } else if (((Q < 0) && (T < 0))
				    || ((R > 0) && (S > 0))) {
				d = true;
			    } else if (((R < 0) && (S < 0))
				    || ((Q > 0) && (T > 0))) {
				d = false;
			    } else if ((Q < 0 ? Q : T) < (R < 0 ? R : S)) {
				d = true;
			    } else {
				d = false;
			    }
			    if (d) {
				// diagonal needs to be swapped
				eflipped = true;
				final int n1 = trit[A];
				final int n2 = trit[B];
				final int n3 = trit[C];
				final int n4 = trit2[D];
				final int w1 = walkt[A];
				final int w2 = walkt[C];
				final int e1 = edgest[A];
				final int e2 = edgest[C];
				int w3, w4, e3, e4;
				if (trit2[(D + 1) % 3] == trit[C]) {
				    w3 = walkt2[D];
				    w4 = walkt2[(D + 2) % 3];
				    e3 = edgest2[D];
				    e4 = edgest2[(D + 2) % 3];
				} else {
				    w3 = walkt2[(D + 2) % 3];
				    w4 = walkt2[D];
				    e3 = edgest2[(D + 2) % 3];
				    e4 = edgest2[D];
				}
				// update Tri array
				trit[0] = n1;
				trit[1] = n2;
				trit[2] = n4;
				trit2[0] = n1;
				trit2[1] = n4;
				trit2[2] = n3;
				// update Walk array
				walkt[0] = w1;
				walkt[1] = w4;
				walkt[2] = t2;
				walkt2[0] = t;
				walkt2[1] = w3;
				walkt2[2] = w2;
				if (w2 >= 0) {
				    final int val = (Walk[w2][0] == t) ? 0
					    : (Walk[w2][1] == t) ? 1 : 2;
				    Walk[w2][val] = t2;
				}
				if (w4 >= 0) {
				    final int val = (Walk[w4][0] == t2) ? 0
					    : (Walk[w4][1] == t2) ? 1 : 2;
				    Walk[w4][val] = t;
				}
				// update Edges array
				edgest[0] = e1;
				edgest[1] = e4;
				// Edges[t][2] and Edges[t2][0] stay the same
				edgest2[1] = e3;
				edgest2[2] = e2;
				// update Vertices array
				final int[] vertn1 = Vertices[n1];
				final int[] vertn2 = Vertices[n2];
				final int[] vertn3 = Vertices[n3];
				final int[] vertn4 = Vertices[n4];
				final int ln1 = vertn1.length;
				final int ln2 = vertn2.length;
				final int ln3 = vertn3.length;
				final int ln4 = vertn4.length;
				final int[] tn1 = new int[ln1 + 1]; // Vertices[n1]
				// adds t2
				final int[] tn2 = new int[ln2 - 1]; // Vertices[n2]
				// loses t2
				final int[] tn3 = new int[ln3 - 1]; // Vertices[n3]
				// loses t
				final int[] tn4 = new int[ln4 + 1]; // Vertices[n4]
				// adds t
				System.arraycopy(vertn1, 0, tn1, 0, ln1);
				tn1[ln1] = t2;
				int c = 0;
				for (int i = 0; i < ln2; i++) {
				    if (vertn2[i] != t2) {
					tn2[c++] = vertn2[i];
				    }
				}
				c = 0;
				for (int i = 0; i < ln3; i++) {
				    if (vertn3[i] != t) {
					tn3[c++] = vertn3[i];
				    }
				}
				System.arraycopy(vertn4, 0, tn4, 0, ln4);
				tn4[ln4] = t;
				Vertices[n1] = tn1;
				Vertices[n2] = tn2;
				Vertices[n3] = tn3;
				Vertices[n4] = tn4;
			    }
			}
			// the edge has now been checked
			edge[curedge] = false;
		    }
		}
	    }
	    // if no edges have been flipped this pass, then stop
	    if (!eflipped) {
		break;
	    }
	}
    }

    /**
     * calculate a triangulation's helper arrays, Walk and Edges, if the
     * triangulation algorithm hasn't calculated them already. Any extension to
     * the Delaunay class should call finish_triang() at the end of its
     * triangulation constructor.
     *
     * @param samples
     *            locations of points for topology - dimensioned
     *            double[dimension][number_of_points]
     */
    public void finish_triang(final double[][] samples) {
	final int mdim = Tri[0].length - 1;
	final int mdim1 = mdim + 1;
	final int dim = samples.length;
	final int ntris = Tri.length;
	int nrs = samples[0].length;
	for (int i = 1; i < dim; i++) {
	    nrs = Math.min(nrs, samples[i].length);
	}
	if (Vertices == null) {
	    // build Vertices component
	    Vertices = new int[nrs][];
	    final int[] nverts = new int[nrs];
	    for (int i = 0; i < ntris; i++) {
		for (int j = 0; j < mdim1; j++) {
		    nverts[Tri[i][j]]++;
		}
	    }
	    for (int i = 0; i < nrs; i++) {
		Vertices[i] = new int[nverts[i]];
		nverts[i] = 0;
	    }
	    for (int i = 0; i < ntris; i++) {
		for (int j = 0; j < mdim1; j++) {
		    Vertices[Tri[i][j]][nverts[Tri[i][j]]++] = i;
		}
	    }
	}
	if ((Walk == null) && (mdim <= 3)) {
	    // build Walk component
	    Walk = new int[ntris][mdim1];
	    for (int i = 0; i < ntris; i++) {
		WalkDim: for (int j = 0; j < mdim1; j++) {
		    final int v1 = j;
		    final int v2 = (v1 + 1) % mdim1;
		    Walk[i][j] = -1;
		    for (int k = 0; k < Vertices[Tri[i][v1]].length; k++) {
			final int temp = Vertices[Tri[i][v1]][k];
			if (temp != i) {
			    for (int l = 0; l < Vertices[Tri[i][v2]].length; l++) {
				if (mdim == 2) {
				    if (temp == Vertices[Tri[i][v2]][l]) {
					Walk[i][j] = temp;
					continue WalkDim;
				    }
				} else { // mdim == 3
				    final int temp2 = Vertices[Tri[i][v2]][l];
				    final int v3 = (v2 + 1) % mdim1;
				    if (temp == temp2) {
					for (int m = 0; m < Vertices[Tri[i][v3]].length; m++) {
					    if (temp == Vertices[Tri[i][v3]][m]) {
						Walk[i][j] = temp;
						continue WalkDim;
					    }
					}
				    }
				} // end if (mdim == 3)
			    } // end for (int l=0;
			    // l<Vertices[Tri[i][v2]].length; l++)
			} // end if (temp != i)
		    } // end for (int k=0; k<Vertices[Tri[i][v1]].length; k++)
		} // end for (int j=0; j<mdim1; j++)
	    } // end for (int i=0; i<Tri.length; i++)
	} // end if (Walk == null && mdim <= 3)
	if ((Edges == null) && (mdim <= 3)) {
	    // build Edges component
	    // initialize all edges to "not yet found"
	    final int edim = 3 * (mdim - 1);
	    Edges = new int[ntris][edim];
	    for (int i = 0; i < ntris; i++) {
		for (int j = 0; j < edim; j++) {
		    Edges[i][j] = -1;
		}
	    }
	    // calculate global edge values
	    NumEdges = 0;
	    if (mdim == 2) {
		for (int i = 0; i < ntris; i++) {
		    for (int j = 0; j < 3; j++) {
			if (Edges[i][j] < 0) {
			    // this edge doesn't have a "global edge number" yet
			    final int othtri = Walk[i][j];
			    if (othtri >= 0) {
				int cside = -1;
				for (int k = 0; k < 3; k++) {
				    if (Walk[othtri][k] == i) {
					cside = k;
				    }
				}
				if (cside != -1) {
				    Edges[othtri][cside] = NumEdges;
				} else {
				    // ;
				}
			    }
			    Edges[i][j] = NumEdges++;
			}
		    }
		}
	    } else { // mdim == 3
		final int[] ptlook1 = { 0, 0, 0, 1, 1, 2 };
		final int[] ptlook2 = { 1, 2, 3, 2, 3, 3 };
		for (int i = 0; i < ntris; i++) {
		    for (int j = 0; j < 6; j++) {
			if (Edges[i][j] < 0) {
			    // this edge doesn't have a "global edge number" yet
			    // search through the edge's two end points
			    final int endpt1 = Tri[i][ptlook1[j]];
			    final int endpt2 = Tri[i][ptlook2[j]];
			    // create an intersection of two sets
			    final int[] set = new int[Vertices[endpt1].length];
			    int setlen = 0;
			    for (int p1 = 0; p1 < Vertices[endpt1].length; p1++) {
				final int temp = Vertices[endpt1][p1];
				for (int p2 = 0; p2 < Vertices[endpt2].length; p2++) {
				    if (temp == Vertices[endpt2][p2]) {
					set[setlen++] = temp;
					break;
				    }
				}
			    }
			    // assign global edge number to all members of set
			    for (int kk = 0; kk < setlen; kk++) {
				final int k = set[kk];
				for (int l = 0; l < edim; l++) {
				    if (((Tri[k][ptlook1[l]] == endpt1) && (Tri[k][ptlook2[l]] == endpt2))
					    || ((Tri[k][ptlook1[l]] == endpt2) && (Tri[k][ptlook2[l]] == endpt1))) {
					Edges[k][l] = NumEdges;
				    }
				}
			    }
			    Edges[i][j] = NumEdges++;
			} // end if (Edges[i][j] < 0)
		    } // end for (int j=0; j<6; j++)
		} // end for (int i=0; i<ntris; i++)
	    } // end if (mdim == 3)
	} // end if (Edges == null && mdim <= 3)
	if ((Neighbors == null) && (mdim <= 3)) {
	    Neighbors = new int[nrs][];
	    final Set<Integer> temp = new HashSet<Integer>();
	    for (int i = 0; i < nrs; i++) {
		final int[] tetras = Vertices[i];
		for (int j = 0; j < tetras.length; j++) {
		    for (int k = 0; k < mdim1; k++) {
			temp.add(Tri[tetras[j]][k]);
		    }
		}
		temp.remove(i);
		Neighbors[i] = new int[temp.size()];
		int j = 0;
		for (final Integer s : temp) {
		    Neighbors[i][j++] = s;
		}
	    }
	}
	if ((circumcenters == null) && (mdim == 3)) {
	    circumcenters = new WB_Point[ntris];
	    circumradii = new double[ntris];
	    final WB_Point p0 = new WB_Point();
	    final WB_Point p1 = new WB_Point();
	    final WB_Point p2 = new WB_Point();
	    final WB_Point p3 = new WB_Point();
	    final WB_Predicates pred = new WB_Predicates();
	    WB_Sphere CS;
	    int[] tetra;
	    for (int i = 0; i < ntris; i++) {
		tetra = Tri[i];
		p0.set(samples[0][tetra[0]], samples[1][tetra[0]],
			samples[2][tetra[0]]);
		p1.set(samples[0][tetra[1]], samples[1][tetra[1]],
			samples[2][tetra[1]]);
		p2.set(samples[0][tetra[2]], samples[1][tetra[2]],
			samples[2][tetra[2]]);
		p3.set(samples[0][tetra[3]], samples[1][tetra[3]],
			samples[2][tetra[3]]);
		CS = pred.circumsphereTetra(p0, p1, p2, p3);
		circumcenters[i] = CS.getCenter();
		circumradii[i] = CS.getRadius();
	    }
	} else if ((circumcenters == null) && (mdim == 2)) {
	    circumcenters = new WB_Point[ntris];
	    circumradii = new double[ntris];
	    final WB_Point p0 = new WB_Point();
	    final WB_Point p1 = new WB_Point();
	    final WB_Point p2 = new WB_Point();
	    final WB_Predicates pred = new WB_Predicates();
	    WB_Sphere CS;
	    int[] tri;
	    for (int i = 0; i < ntris; i++) {
		tri = Tri[i];
		p0.set(samples[0][tri[0]], samples[1][tri[0]],
			samples[2][tri[0]]);
		p1.set(samples[0][tri[1]], samples[1][tri[1]],
			samples[2][tri[1]]);
		p2.set(samples[0][tri[2]], samples[1][tri[2]],
			samples[2][tri[2]]);
		CS = pred.circumsphereTri(p0, p1, p2);
		circumcenters[i] = CS.getCenter();
		circumradii[i] = CS.getRadius();
	    }
	}
    }

    /**
     * @return a String representation of this
     */
    @Override
    public String toString() {
	return sampleString(null);
    }

    /**
     * @param samples
     *            locations of points for topology - dimensioned
     *            double[dimension][number_of_points] - may be null
     * @return a String representation of this, including samples if it is
     *         non-null
     */
    public String sampleString(final double[][] samples) {
	final StringBuffer s = new StringBuffer("");
	if (samples != null) {
	    s.append("\nsamples " + samples[0].length + "\n");
	    for (int i = 0; i < samples[0].length; i++) {
		s.append("  " + i + " -> " + samples[0][i] + " "
			+ samples[1][i] + " " + samples[2][i] + "\n");
	    }
	    s.append("\n");
	}
	s.append("\nTri (triangles -> vertices) " + Tri.length + "\n");
	for (int i = 0; i < Tri.length; i++) {
	    s.append("  " + i + " -> ");
	    for (int j = 0; j < Tri[i].length; j++) {
		s.append(" " + Tri[i][j]);
	    }
	    s.append("\n");
	}
	s.append("\nVertices (vertices -> triangles) " + Vertices.length + "\n");
	for (int i = 0; i < Vertices.length; i++) {
	    s.append("  " + i + " -> ");
	    for (int j = 0; j < Vertices[i].length; j++) {
		s.append(" " + Vertices[i][j]);
	    }
	    s.append("\n");
	}
	s.append("\nWalk (triangles -> triangles) " + Walk.length + "\n");
	for (int i = 0; i < Walk.length; i++) {
	    s.append("  " + i + " -> ");
	    for (int j = 0; j < Walk[i].length; j++) {
		s.append(" " + Walk[i][j]);
	    }
	    s.append("\n");
	}
	s.append("\nEdges (triangles -> global edges) " + Edges.length + "\n");
	for (int i = 0; i < Edges.length; i++) {
	    s.append("  " + i + " -> ");
	    for (int j = 0; j < Edges[i].length; j++) {
		s.append(" " + Edges[i][j]);
	    }
	    s.append("\n");
	}
	return s.toString();
    }

    //
    // DelaunayClarkson.java
    //
    /*
     * VisAD system for interactive analysis and visualization of numerical
     * data. Copyright (C) 1996 - 2011 Bill Hibbard, Curtis Rueden, Tom Rink,
     * Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and Tommy
     * Jasmin.
     * 
     * This library is free software; you can redistribute it and/or modify it
     * under the terms of the GNU Library General Public License as published by
     * the Free Software Foundation; either version 2 of the License, or (at
     * your option) any later version.
     * 
     * This library is distributed in the hope that it will be useful, but
     * WITHOUT ANY WARRANTY; without even the implied warranty of
     * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library
     * General Public License for more details.
     * 
     * You should have received a copy of the GNU Library General Public License
     * along with this library; if not, write to the Free Software Foundation,
     * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
     */
    /*
     * The Delaunay triangulation algorithm in this class is originally from
     * hull by Ken Clarkson:
     * 
     * Ken Clarkson wrote this. Copyright (c) 1995 by AT&T.. Permission to use,
     * copy, modify, and distribute this software for any purpose without fee is
     * hereby granted, provided that this entire notice is included in all
     * copies of any software which is or includes a copy or modification of
     * this software and in all copies of the supporting documentation for such
     * software. THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR
     * IMPLIED WARRANTY. IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
     * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY OF
     * THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
     */
    /**
     * DelaunayClarkson represents an O(N*logN) method with high overhead to
     * find the Delaunay triangulation of a set of samples of R^DomainDimension.
     * <P>
     */
    static class DelaunayClarkson extends WB_Delaunay {
	/* ******* BEGINNING OF CONVERTED HULL CODE ******* */
	// <<<< Constants >>>>
	/**
	 * 
	 */
	private static final double DBL_MANT_DIG = 53;
	
	/**
	 * 
	 */
	private static final double FLT_RADIX = 2;
	
	/**
	 * 
	 */
	private static final double DBL_EPSILON = 2.2204460492503131E-16;
	
	/**
	 * 
	 */
	private static final double ln2 = Math.log(2);
	// <<<< Variables >>>>
	/*
	 * we need to have two indices for every pointer into basis_s and
	 * simplex arrays, because they are two-dimensional arrays of blocks. (
	 * _bn = block number )
	 */
	// for the pseudo-pointers
	/**
	 * 
	 */
	private static final int INFINITY = -2; // replaces infinity
	
	/**
	 * 
	 */
	private static final int NOVAL = -1; // replaces null
	
	/**
	 * 
	 */
	private double[][] site_blocks; // copy of samples array
	
	/**
	 * 
	 */
	private int[][] a3s; // output array
	
	/**
	 * 
	 */
	private int a3size; // output array maximum size
	
	/**
	 * 
	 */
	private int nts = 0; // # output objects
	
	/**
	 * 
	 */
	private static final int max_blocks = 10000; // max # basis/simplex
	// blocks
	/**
	 * 
	 */
	private static final int Nobj = 10000;
	
	/**
	 * 
	 */
	private static final int MAXDIM = 8; // max dimension
	
	/**
	 * 
	 */
	private final int dim;
	
	/**
	 * 
	 */
	private int p;
	
	/**
	 * 
	 */
	private long pnum;
	
	/**
	 * 
	 */
	private final int rdim; // # sites currently specifying region
	
	/**
	 * 
	 */
	private int cdim;
	
	/**
	 * 
	 */
	private final int exact_bits;
	
	/**
	 * 
	 */
	private final double b_err_min, b_err_min_sq;
	
	/**
	 * 
	 */
	private double ldetbound = 0;
	
	/**
	 * 
	 */
	private int failcount = 0; // static: reduce_inner
	
	/**
	 * 
	 */
	private int lscale; // static: reduce_inner
	
	/**
	 * 
	 */
	private double max_scale; // static: reduce_inner
	
	/**
	 * 
	 */
	private int nsb = 0; // # simplex blocks
	
	/**
	 * 
	 */
	private int nbb = 0; // # basis_s blocks
	
	/**
	 * 
	 */
	private int ss = MAXDIM; // static: search
	
	/**
	 * 
	 */
	private int ss2 = 2000; // static: visit_triang
	
	/**
	 * 
	 */
	private long vnum = -1; // static: visit_triang
	// "void stuff" -- dummy variables to hold unused return information
	/**
	 * 
	 */
	private final int[] voidp = new int[1];
	
	/**
	 * 
	 */
	private final int[] voidp_bn = new int[1];
	// basis_s stuff
	/**
	 * 
	 */
	private int[][] bbt_next = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] bbt_next_bn = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] bbt_ref_count = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] bbt_lscale = new int[max_blocks][];
	
	/**
	 * 
	 */
	private double[][] bbt_sqa = new double[max_blocks][];
	
	/**
	 * 
	 */
	private double[][] bbt_sqb = new double[max_blocks][];
	
	/**
	 * 
	 */
	private double[][][] bbt_vecs = new double[max_blocks][][];
	
	/**
	 * 
	 */
	private final int ttbp;
	
	/**
	 * 
	 */
	private final int ttbp_bn;
	
	/**
	 * 
	 */
	private final int ib;
	
	/**
	 * 
	 */
	private final int ib_bn;
	
	/**
	 * 
	 */
	private int basis_s_list = NOVAL;
	
	/**
	 * 
	 */
	private int basis_s_list_bn;
	
	/**
	 * 
	 */
	private int pnb = NOVAL;
	
	/**
	 * 
	 */
	private int pnb_bn;
	
	/**
	 * 
	 */
	private int b = NOVAL; // static: sees
	
	/**
	 * 
	 */
	private int b_bn;
	// simplex stuff
	/**
	 * 
	 */
	private int[][] sbt_next = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] sbt_next_bn = new int[max_blocks][];
	
	/**
	 * 
	 */
	private long[][] sbt_visit = new long[max_blocks][];
	
	/**
	 * 
	 */
	private short[][] sbt_mark = new short[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] sbt_normal = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] sbt_normal_bn = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] sbt_peak_vert = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] sbt_peak_simp = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] sbt_peak_simp_bn = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] sbt_peak_basis = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][] sbt_peak_basis_bn = new int[max_blocks][];
	
	/**
	 * 
	 */
	private int[][][] sbt_neigh_vert = new int[max_blocks][][];
	
	/**
	 * 
	 */
	private int[][][] sbt_neigh_simp = new int[max_blocks][][];
	
	/**
	 * 
	 */
	private int[][][] sbt_neigh_simp_bn = new int[max_blocks][][];
	
	/**
	 * 
	 */
	private int[][][] sbt_neigh_basis = new int[max_blocks][][];
	
	/**
	 * 
	 */
	private int[][][] sbt_neigh_basis_bn = new int[max_blocks][][];
	
	/**
	 * 
	 */
	private int simplex_list = NOVAL;
	
	/**
	 * 
	 */
	private int simplex_list_bn;
	
	/**
	 * 
	 */
	private final int ch_root;
	
	/**
	 * 
	 */
	private final int ch_root_bn;
	
	/**
	 * 
	 */
	private int ns; // static: make_facets
	
	/**
	 * 
	 */
	private int ns_bn;
	
	/**
	 * 
	 */
	private int[] st = new int[ss + MAXDIM + 1]; // static: search
	
	/**
	 * 
	 */
	private int[] st_bn = new int[ss + MAXDIM + 1];
	
	/**
	 * 
	 */
	private int[] st2 = new int[ss2 + MAXDIM + 1]; // static: visit_triang
	
	/**
	 * 
	 */
	private int[] st2_bn = new int[ss2 + MAXDIM + 1];

	// <<<< Functions >>>>
	/**
	 * 
	 *
	 * @return 
	 */
	private int new_block_basis_s() {
	    bbt_next[nbb] = new int[Nobj];
	    bbt_next_bn[nbb] = new int[Nobj];
	    bbt_ref_count[nbb] = new int[Nobj];
	    bbt_lscale[nbb] = new int[Nobj];
	    bbt_sqa[nbb] = new double[Nobj];
	    bbt_sqb[nbb] = new double[Nobj];
	    bbt_vecs[nbb] = new double[2 * rdim][];
	    for (int i = 0; i < (2 * rdim); i++) {
		bbt_vecs[nbb][i] = new double[Nobj];
	    }
	    for (int i = 0; i < Nobj; i++) {
		bbt_next[nbb][i] = i + 1;
		bbt_next_bn[nbb][i] = nbb;
		bbt_ref_count[nbb][i] = 0;
		bbt_lscale[nbb][i] = 0;
		bbt_sqa[nbb][i] = 0;
		bbt_sqb[nbb][i] = 0;
		for (int j = 0; j < (2 * rdim); j++) {
		    bbt_vecs[nbb][j][i] = 0;
		}
	    }
	    bbt_next[nbb][Nobj - 1] = NOVAL;
	    basis_s_list = 0;
	    basis_s_list_bn = nbb;
	    nbb++;
	    return basis_s_list;
	}

	/**
	 * 
	 *
	 * @param v 
	 * @param v_bn 
	 * @param s 
	 * @param s_bn 
	 * @param k 
	 * @return 
	 */
	private int reduce_inner(final int v, final int v_bn, final int s,
		final int s_bn, final int k) {
	    int q, q_bn;
	    double dd, Sb = 0;
	    double scale;
	    bbt_sqa[v_bn][v] = 0;
	    for (int i = 0; i < rdim; i++) {
		bbt_sqa[v_bn][v] += bbt_vecs[v_bn][i][v] * bbt_vecs[v_bn][i][v];
	    }
	    bbt_sqb[v_bn][v] = bbt_sqa[v_bn][v];
	    if (k <= 1) {
		for (int i = 0; i < rdim; i++) {
		    bbt_vecs[v_bn][i][v] = bbt_vecs[v_bn][rdim + i][v];
		}
		return 1;
	    }
	    for (int j = 0; j < 250; j++) {
		int xx = rdim;
		double labound;
		for (int i = 0; i < rdim; i++) {
		    bbt_vecs[v_bn][i][v] = bbt_vecs[v_bn][rdim + i][v];
		}
		for (int i = k - 1; i > 0; i--) {
		    q = sbt_neigh_basis[s_bn][i][s];
		    q_bn = sbt_neigh_basis_bn[s_bn][i][s];
		    dd = 0;
		    for (int l = 0; l < rdim; l++) {
			dd -= bbt_vecs[q_bn][l][q] * bbt_vecs[v_bn][l][v];
		    }
		    dd /= bbt_sqb[q_bn][q];
		    for (int l = 0; l < rdim; l++) {
			bbt_vecs[v_bn][l][v] += dd
				* bbt_vecs[q_bn][rdim + l][q];
		    }
		}
		bbt_sqb[v_bn][v] = 0;
		for (int i = 0; i < rdim; i++) {
		    bbt_sqb[v_bn][v] += bbt_vecs[v_bn][i][v]
			    * bbt_vecs[v_bn][i][v];
		}
		bbt_sqa[v_bn][v] = 0;
		for (int i = 0; i < rdim; i++) {
		    bbt_sqa[v_bn][v] += bbt_vecs[v_bn][rdim + i][v]
			    * bbt_vecs[v_bn][rdim + i][v];
		}
		if ((2 * bbt_sqb[v_bn][v]) >= bbt_sqa[v_bn][v]) {
		    return 1;
		}
		// scale up vector
		if (j < 10) {
		    labound = Math.floor(Math.log(bbt_sqa[v_bn][v]) / ln2) / 2;
		    max_scale = exact_bits - labound - (0.66 * (k - 2)) - 1;
		    if (max_scale < 1) {
			max_scale = 1;
		    }
		    if (j == 0) {
			ldetbound = 0;
			Sb = 0;
			for (int l = k - 1; l > 0; l--) {
			    q = sbt_neigh_basis[s_bn][l][s];
			    q_bn = sbt_neigh_basis_bn[s_bn][l][s];
			    Sb += bbt_sqb[q_bn][q];
			    ldetbound += (Math.floor(Math.log(bbt_sqb[q_bn][q])
				    / ln2) / 2) + 1;
			    ldetbound -= bbt_lscale[q_bn][q];
			}
		    }
		}
		if (((ldetbound - bbt_lscale[v_bn][v])
			+ (Math.floor(Math.log(bbt_sqb[v_bn][v]) / ln2) / 2) + 1) < 0) {
		    scale = 0;
		} else {
		    lscale = (int) (Math
			    .log((2 * Sb)
				    / (bbt_sqb[v_bn][v] + (bbt_sqa[v_bn][v] * b_err_min))) / ln2) / 2;
		    if (lscale > max_scale) {
			lscale = (int) max_scale;
		    } else if (lscale < 0) {
			lscale = 0;
		    }
		    bbt_lscale[v_bn][v] += lscale;
		    scale = (lscale < 20) ? 1 << lscale : Math.pow(2, lscale);
		}
		while (xx < (2 * rdim)) {
		    bbt_vecs[v_bn][xx++][v] *= scale;
		}
		for (int i = k - 1; i > 0; i--) {
		    q = sbt_neigh_basis[s_bn][i][s];
		    q_bn = sbt_neigh_basis_bn[s_bn][i][s];
		    dd = 0;
		    for (int l = 0; l < rdim; l++) {
			dd -= bbt_vecs[q_bn][l][q]
				* bbt_vecs[v_bn][rdim + l][v];
		    }
		    dd /= bbt_sqb[q_bn][q];
		    dd = Math.floor(dd + 0.5);
		    for (int l = 0; l < rdim; l++) {
			bbt_vecs[v_bn][rdim + l][v] += dd
				* bbt_vecs[q_bn][rdim + l][q];
		    }
		}
	    }
	    if (failcount++ < 10) {
		System.out.println("reduce_inner failed!");
	    }
	    return 0;
	}

	/**
	 * 
	 *
	 * @param v 
	 * @param v_bn 
	 * @param rp 
	 * @param s 
	 * @param s_bn 
	 * @param k 
	 * @return 
	 */
	private int reduce(final int[] v, final int[] v_bn, final int rp,
		final int s, final int s_bn, final int k) {
	    if (v[0] == NOVAL) {
		v[0] = basis_s_list != NOVAL ? basis_s_list
			: new_block_basis_s();
		v_bn[0] = basis_s_list_bn;
		basis_s_list = bbt_next[v_bn[0]][v[0]];
		basis_s_list_bn = bbt_next_bn[v_bn[0]][v[0]];
		bbt_ref_count[v_bn[0]][v[0]] = 1;
	    } else {
		bbt_lscale[v_bn[0]][v[0]] = 0;
	    }
	    if (rp == INFINITY) {
		bbt_next[v_bn[0]][v[0]] = bbt_next[ib_bn][ib];
		bbt_next_bn[v_bn[0]][v[0]] = bbt_next_bn[ib_bn][ib];
		bbt_ref_count[v_bn[0]][v[0]] = bbt_ref_count[ib_bn][ib];
		bbt_lscale[v_bn[0]][v[0]] = bbt_lscale[ib_bn][ib];
		bbt_sqa[v_bn[0]][v[0]] = bbt_sqa[ib_bn][ib];
		bbt_sqb[v_bn[0]][v[0]] = bbt_sqb[ib_bn][ib];
		for (int i = 0; i < (2 * rdim); i++) {
		    bbt_vecs[v_bn[0]][i][v[0]] = bbt_vecs[ib_bn][i][ib];
		}
	    } else {
		double sum = 0;
		final int sbt_nv = sbt_neigh_vert[s_bn][0][s];
		if (sbt_nv == INFINITY) {
		    for (int i = 0; i < dim; i++) {
			bbt_vecs[v_bn[0]][i + rdim][v[0]] = bbt_vecs[v_bn[0]][i][v[0]] = site_blocks[i][rp];
		    }
		} else {
		    for (int i = 0; i < dim; i++) {
			bbt_vecs[v_bn[0]][i + rdim][v[0]] = bbt_vecs[v_bn[0]][i][v[0]] = site_blocks[i][rp]
				- site_blocks[i][sbt_nv];
		    }
		}
		for (int i = 0; i < dim; i++) {
		    sum += bbt_vecs[v_bn[0]][i][v[0]]
			    * bbt_vecs[v_bn[0]][i][v[0]];
		}
		bbt_vecs[v_bn[0]][(2 * rdim) - 1][v[0]] = sum;
		bbt_vecs[v_bn[0]][rdim - 1][v[0]] = sum;
	    }
	    return reduce_inner(v[0], v_bn[0], s, s_bn, k);
	}

	/**
	 * 
	 *
	 * @param s 
	 * @param s_bn 
	 */
	private void get_basis_sede(final int s, final int s_bn) {
	    int k = 1;
	    int q, q_bn;
	    final int[] curt = new int[1];
	    final int[] curt_bn = new int[1];
	    if ((sbt_neigh_vert[s_bn][0][s] == INFINITY) && (cdim > 1)) {
		int t_vert, t_simp, t_simp_bn, t_basis, t_basis_bn;
		t_vert = sbt_neigh_vert[s_bn][0][s];
		t_simp = sbt_neigh_simp[s_bn][0][s];
		t_simp_bn = sbt_neigh_simp_bn[s_bn][0][s];
		t_basis = sbt_neigh_basis[s_bn][0][s];
		t_basis_bn = sbt_neigh_basis_bn[s_bn][0][s];
		sbt_neigh_vert[s_bn][0][s] = sbt_neigh_vert[s_bn][k][s];
		sbt_neigh_simp[s_bn][0][s] = sbt_neigh_simp[s_bn][k][s];
		sbt_neigh_simp_bn[s_bn][0][s] = sbt_neigh_simp_bn[s_bn][k][s];
		sbt_neigh_basis[s_bn][0][s] = sbt_neigh_basis[s_bn][k][s];
		sbt_neigh_basis_bn[s_bn][0][s] = sbt_neigh_basis_bn[s_bn][k][s];
		sbt_neigh_vert[s_bn][k][s] = t_vert;
		sbt_neigh_simp[s_bn][k][s] = t_simp;
		sbt_neigh_simp_bn[s_bn][k][s] = t_simp_bn;
		sbt_neigh_basis[s_bn][k][s] = t_basis;
		sbt_neigh_basis_bn[s_bn][k][s] = t_basis_bn;
		q = sbt_neigh_basis[s_bn][0][s];
		q_bn = sbt_neigh_basis_bn[s_bn][0][s];
		if ((q != NOVAL) && (--bbt_ref_count[q_bn][q] == 0)) {
		    bbt_next[q_bn][q] = basis_s_list;
		    bbt_next_bn[q_bn][q] = basis_s_list_bn;
		    bbt_ref_count[q_bn][q] = 0;
		    bbt_lscale[q_bn][q] = 0;
		    bbt_sqa[q_bn][q] = 0;
		    bbt_sqb[q_bn][q] = 0;
		    for (int j = 0; j < (2 * rdim); j++) {
			bbt_vecs[q_bn][j][q] = 0;
		    }
		    basis_s_list = q;
		    basis_s_list_bn = q_bn;
		}
		sbt_neigh_basis[s_bn][0][s] = ttbp;
		sbt_neigh_basis_bn[s_bn][0][s] = ttbp_bn;
		bbt_ref_count[ttbp_bn][ttbp]++;
	    } else {
		if (sbt_neigh_basis[s_bn][0][s] == NOVAL) {
		    sbt_neigh_basis[s_bn][0][s] = ttbp;
		    sbt_neigh_basis_bn[s_bn][0][s] = ttbp_bn;
		    bbt_ref_count[ttbp_bn][ttbp]++;
		} else {
		    while ((k < cdim) && (sbt_neigh_basis[s_bn][k][s] != NOVAL)) {
			k++;
		    }
		}
	    }
	    while (k < cdim) {
		q = sbt_neigh_basis[s_bn][k][s];
		q_bn = sbt_neigh_basis_bn[s_bn][k][s];
		if ((q != NOVAL) && (--bbt_ref_count[q_bn][q] == 0)) {
		    bbt_next[q_bn][q] = basis_s_list;
		    bbt_next_bn[q_bn][q] = basis_s_list_bn;
		    bbt_ref_count[q_bn][q] = 0;
		    bbt_lscale[q_bn][q] = 0;
		    bbt_sqa[q_bn][q] = 0;
		    bbt_sqb[q_bn][q] = 0;
		    for (int j = 0; j < (2 * rdim); j++) {
			bbt_vecs[q_bn][j][q] = 0;
		    }
		    basis_s_list = q;
		    basis_s_list_bn = q_bn;
		}
		sbt_neigh_basis[s_bn][k][s] = NOVAL;
		curt[0] = sbt_neigh_basis[s_bn][k][s];
		curt_bn[0] = sbt_neigh_basis_bn[s_bn][k][s];
		reduce(curt, curt_bn, sbt_neigh_vert[s_bn][k][s], s, s_bn, k);
		sbt_neigh_basis[s_bn][k][s] = curt[0];
		sbt_neigh_basis_bn[s_bn][k][s] = curt_bn[0];
		k++;
	    }
	}

	/**
	 * 
	 *
	 * @param rp 
	 * @param s 
	 * @param s_bn 
	 * @return 
	 */
	private int sees(final int rp, final int s, final int s_bn) {
	    double dd, dds;
	    int q, q_bn, q1, q1_bn, q2, q2_bn;
	    final int[] curt = new int[1];
	    final int[] curt_bn = new int[1];
	    if (b == NOVAL) {
		b = (basis_s_list != NOVAL) ? basis_s_list
			: new_block_basis_s();
		b_bn = basis_s_list_bn;
		basis_s_list = bbt_next[b_bn][b];
		basis_s_list_bn = bbt_next_bn[b_bn][b];
	    } else {
		bbt_lscale[b_bn][b] = 0;
	    }
	    if (cdim == 0) {
		return 0;
	    }
	    if (sbt_normal[s_bn][s] == NOVAL) {
		get_basis_sede(s, s_bn);
		if ((rdim == 3) && (cdim == 3)) {
		    sbt_normal[s_bn][s] = basis_s_list != NOVAL ? basis_s_list
			    : new_block_basis_s();
		    sbt_normal_bn[s_bn][s] = basis_s_list_bn;
		    q = sbt_normal[s_bn][s];
		    q_bn = sbt_normal_bn[s_bn][s];
		    basis_s_list = bbt_next[q_bn][q];
		    basis_s_list_bn = bbt_next_bn[q_bn][q];
		    q1 = sbt_neigh_basis[s_bn][1][s];
		    q1_bn = sbt_neigh_basis_bn[s_bn][1][s];
		    q2 = sbt_neigh_basis[s_bn][2][s];
		    q2_bn = sbt_neigh_basis_bn[s_bn][2][s];
		    bbt_ref_count[q_bn][q] = 1;
		    bbt_vecs[q_bn][0][q] = (bbt_vecs[q1_bn][1][q1] * bbt_vecs[q2_bn][2][q2])
			    - (bbt_vecs[q1_bn][2][q1] * bbt_vecs[q2_bn][1][q2]);
		    bbt_vecs[q_bn][1][q] = (bbt_vecs[q1_bn][2][q1] * bbt_vecs[q2_bn][0][q2])
			    - (bbt_vecs[q1_bn][0][q1] * bbt_vecs[q2_bn][2][q2]);
		    bbt_vecs[q_bn][2][q] = (bbt_vecs[q1_bn][0][q1] * bbt_vecs[q2_bn][1][q2])
			    - (bbt_vecs[q1_bn][1][q1] * bbt_vecs[q2_bn][0][q2]);
		    bbt_sqb[q_bn][q] = 0;
		    for (int i = 0; i < rdim; i++) {
			bbt_sqb[q_bn][q] += bbt_vecs[q_bn][i][q]
				* bbt_vecs[q_bn][i][q];
		    }
		    for (int i = cdim + 1; i > 0; i--) {
			final int m = (i > 1) ? sbt_neigh_vert[ch_root_bn][i - 2][ch_root]
				: INFINITY;
			int j;
			for (j = 0; (j < cdim)
				&& (m != sbt_neigh_vert[s_bn][j][s]); j++) {
			    ;
			}
			if (j < cdim) {
			    continue;
			}
			if (m == INFINITY) {
			    if (bbt_vecs[q_bn][2][q] > -b_err_min) {
				continue;
			    }
			} else {
			    if (sees(m, s, s_bn) == 0) {
				continue;
			    }
			}
			bbt_vecs[q_bn][0][q] = -bbt_vecs[q_bn][0][q];
			bbt_vecs[q_bn][1][q] = -bbt_vecs[q_bn][1][q];
			bbt_vecs[q_bn][2][q] = -bbt_vecs[q_bn][2][q];
			break;
		    }
		} else {
		    for (int i = cdim + 1; i > 0; i--) {
			final int m = (i > 1) ? sbt_neigh_vert[ch_root_bn][i - 2][ch_root]
				: INFINITY;
			int j;
			for (j = 0; (j < cdim)
				&& (m != sbt_neigh_vert[s_bn][j][s]); j++) {
			    ;
			}
			if (j < cdim) {
			    continue;
			}
			curt[0] = sbt_normal[s_bn][s];
			curt_bn[0] = sbt_normal_bn[s_bn][s];
			reduce(curt, curt_bn, m, s, s_bn, cdim);
			q = sbt_normal[s_bn][s] = curt[0];
			q_bn = sbt_normal_bn[s_bn][s] = curt_bn[0];
			if (bbt_sqb[q_bn][q] != 0) {
			    break;
			}
		    }
		}
		for (int i = 0; i < cdim; i++) {
		    q = sbt_neigh_basis[s_bn][i][s];
		    q_bn = sbt_neigh_basis_bn[s_bn][i][s];
		    if ((q != NOVAL) && (--bbt_ref_count[q_bn][q] == 0)) {
			bbt_next[q_bn][q] = basis_s_list;
			bbt_next_bn[q_bn][q] = basis_s_list_bn;
			bbt_ref_count[q_bn][q] = 0;
			bbt_lscale[q_bn][q] = 0;
			bbt_sqa[q_bn][q] = 0;
			bbt_sqb[q_bn][q] = 0;
			for (int l = 0; l < (2 * rdim); l++) {
			    bbt_vecs[q_bn][l][q] = 0;
			}
			basis_s_list = q;
			basis_s_list_bn = q_bn;
		    }
		    sbt_neigh_basis[s_bn][i][s] = NOVAL;
		}
	    }
	    if (rp == INFINITY) {
		bbt_next[b_bn][b] = bbt_next[ib_bn][ib];
		bbt_next_bn[b_bn][b] = bbt_next_bn[ib_bn][ib];
		bbt_ref_count[b_bn][b] = bbt_ref_count[ib_bn][ib];
		bbt_lscale[b_bn][b] = bbt_lscale[ib_bn][ib];
		bbt_sqa[b_bn][b] = bbt_sqa[ib_bn][ib];
		bbt_sqb[b_bn][b] = bbt_sqb[ib_bn][ib];
		for (int i = 0; i < (2 * rdim); i++) {
		    bbt_vecs[b_bn][i][b] = bbt_vecs[ib_bn][i][ib];
		}
	    } else {
		double sum = 0;
		final int sbt_nv = sbt_neigh_vert[s_bn][0][s];
		if (sbt_nv == INFINITY) {
		    for (int l = 0; l < dim; l++) {
			bbt_vecs[b_bn][l + rdim][b] = bbt_vecs[b_bn][l][b] = site_blocks[l][rp];
		    }
		} else {
		    for (int l = 0; l < dim; l++) {
			bbt_vecs[b_bn][l + rdim][b] = bbt_vecs[b_bn][l][b] = site_blocks[l][rp]
				- site_blocks[l][sbt_nv];
		    }
		}
		for (int l = 0; l < dim; l++) {
		    sum += bbt_vecs[b_bn][l][b] * bbt_vecs[b_bn][l][b];
		}
		bbt_vecs[b_bn][(2 * rdim) - 1][b] = bbt_vecs[b_bn][rdim - 1][b] = sum;
	    }
	    q = sbt_normal[s_bn][s];
	    q_bn = sbt_normal_bn[s_bn][s];
	    for (int i = 0; i < 3; i++) {
		double sum = 0;
		dd = 0;
		for (int l = 0; l < rdim; l++) {
		    dd += bbt_vecs[b_bn][l][b] * bbt_vecs[q_bn][l][q];
		}
		if (dd == 0.0) {
		    return 0;
		}
		for (int l = 0; l < rdim; l++) {
		    sum += bbt_vecs[b_bn][l][b] * bbt_vecs[b_bn][l][b];
		}
		dds = (dd * dd) / bbt_sqb[q_bn][q] / sum;
		if (dds > b_err_min_sq) {
		    return (dd < 0 ? 1 : 0);
		}
		get_basis_sede(s, s_bn);
		reduce_inner(b, b_bn, s, s_bn, cdim);
	    }
	    return 0;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	private int new_block_simplex() {
	    sbt_next[nsb] = new int[Nobj];
	    sbt_next_bn[nsb] = new int[Nobj];
	    sbt_visit[nsb] = new long[Nobj];
	    sbt_mark[nsb] = new short[Nobj];
	    sbt_normal[nsb] = new int[Nobj];
	    sbt_normal_bn[nsb] = new int[Nobj];
	    sbt_peak_vert[nsb] = new int[Nobj];
	    sbt_peak_simp[nsb] = new int[Nobj];
	    sbt_peak_simp_bn[nsb] = new int[Nobj];
	    sbt_peak_basis[nsb] = new int[Nobj];
	    sbt_peak_basis_bn[nsb] = new int[Nobj];
	    sbt_neigh_vert[nsb] = new int[rdim][];
	    sbt_neigh_simp[nsb] = new int[rdim][];
	    sbt_neigh_simp_bn[nsb] = new int[rdim][];
	    sbt_neigh_basis[nsb] = new int[rdim][];
	    sbt_neigh_basis_bn[nsb] = new int[rdim][];
	    for (int i = 0; i < rdim; i++) {
		sbt_neigh_vert[nsb][i] = new int[Nobj];
		sbt_neigh_simp[nsb][i] = new int[Nobj];
		sbt_neigh_simp_bn[nsb][i] = new int[Nobj];
		sbt_neigh_basis[nsb][i] = new int[Nobj];
		sbt_neigh_basis_bn[nsb][i] = new int[Nobj];
	    }
	    for (int i = 0; i < Nobj; i++) {
		sbt_next[nsb][i] = i + 1;
		sbt_next_bn[nsb][i] = nsb;
		sbt_visit[nsb][i] = 0;
		sbt_mark[nsb][i] = 0;
		sbt_normal[nsb][i] = NOVAL;
		sbt_peak_vert[nsb][i] = NOVAL;
		sbt_peak_simp[nsb][i] = NOVAL;
		sbt_peak_basis[nsb][i] = NOVAL;
		for (int j = 0; j < rdim; j++) {
		    sbt_neigh_vert[nsb][j][i] = NOVAL;
		    sbt_neigh_simp[nsb][j][i] = NOVAL;
		    sbt_neigh_basis[nsb][j][i] = NOVAL;
		}
	    }
	    sbt_next[nsb][Nobj - 1] = NOVAL;
	    simplex_list = 0;
	    simplex_list_bn = nsb;
	    nsb++;
	    return simplex_list;
	}

	/**
	 * starting at s, visit simplices t such that test(s,i,0) is true, and t
	 * is the i'th neighbor of s; apply visit function to all visited
	 * simplices; when visit returns nonnull, exit and return its value.
	 *
	 * @param s 
	 * @param s_bn 
	 * @param whichfunc 
	 * @param ret 
	 * @param ret_bn 
	 */
	private void visit_triang_gen(final int s, final int s_bn,
		final int whichfunc, final int[] ret, final int[] ret_bn) {
	    int v;
	    int v_bn;
	    int t;
	    int t_bn;
	    int tms = 0;
	    vnum--;
	    if (s != NOVAL) {
		st2[tms] = s;
		st2_bn[tms] = s_bn;
		tms++;
	    }
	    while (tms != 0) {
		if (tms > ss2) {
		    // JAVA: efficiency issue: how much is this stack hammered?
		    ss2 += ss2;
		    final int[] newst2 = new int[ss2 + MAXDIM + 1];
		    final int[] newst2_bn = new int[ss2 + MAXDIM + 1];
		    System.arraycopy(st2, 0, newst2, 0, st2.length);
		    System.arraycopy(st2_bn, 0, newst2_bn, 0, st2_bn.length);
		    st2 = newst2;
		    st2_bn = newst2_bn;
		}
		tms--;
		t = st2[tms];
		t_bn = st2_bn[tms];
		if ((t == NOVAL) || (sbt_visit[t_bn][t] == vnum)) {
		    continue;
		}
		sbt_visit[t_bn][t] = vnum;
		if (whichfunc == 1) {
		    if (sbt_peak_vert[t_bn][t] == NOVAL) {
			v = t;
			v_bn = t_bn;
		    } else {
			v = NOVAL;
			v_bn = NOVAL;
		    }
		    if (v != NOVAL) {
			ret[0] = v;
			ret_bn[0] = v_bn;
			return;
		    }
		} else {
		    final int[] vfp = new int[cdim];
		    if (t != NOVAL) {
			for (int j = 0; j < cdim; j++) {
			    vfp[j] = sbt_neigh_vert[t_bn][j][t];
			}
			for (int j = 0; j < cdim; j++) {
			    a3s[j][nts] = (vfp[j] == INFINITY) ? -1 : vfp[j];
			}
			nts++;
			if (nts > a3size) {
			    // JAVA: efficiency issue, hammering an array
			    a3size += a3size;
			    final int[][] newa3s = new int[rdim][a3size
			                                         + MAXDIM + 1];
			    for (int i = 0; i < rdim; i++) {
				System.arraycopy(a3s[i], 0, newa3s[i], 0,
					a3s[i].length);
			    }
			    a3s = newa3s;
			}
		    }
		}
		for (int i = 0; i < cdim; i++) {
		    final int j = sbt_neigh_simp[t_bn][i][t];
		    final int j_bn = sbt_neigh_simp_bn[t_bn][i][t];
		    if ((j != NOVAL) && (sbt_visit[j_bn][j] != vnum)) {
			st2[tms] = j;
			st2_bn[tms] = j_bn;
			tms++;
		    }
		}
	    }
	    ret[0] = NOVAL;
	}

	/**
	 * make neighbor connections between newly created simplices incident to
	 * p.
	 *
	 * @param s 
	 * @param s_bn 
	 */
	private void connect(final int s, final int s_bn) {
	    int xb, xf;
	    int sb, sb_bn;
	    int sf, sf_bn;
	    int tf, tf_bn;
	    int ccj, ccj_bn;
	    int xfi;
	    if (s == NOVAL) {
		return;
	    }
	    for (int i = 0; (sbt_neigh_vert[s_bn][i][s] != p) && (i < cdim); i++) {
		;
	    }
	    if (sbt_visit[s_bn][s] == pnum) {
		return;
	    }
	    sbt_visit[s_bn][s] = pnum;
	    ccj = sbt_peak_simp[s_bn][s];
	    ccj_bn = sbt_peak_simp_bn[s_bn][s];
	    for (xfi = 0; ((sbt_neigh_simp[ccj_bn][xfi][ccj] != s) || (sbt_neigh_simp_bn[ccj_bn][xfi][ccj] != s_bn))
		    && (xfi < cdim); xfi++) {
		;
	    }
	    for (int i = 0; i < cdim; i++) {
		int l;
		if (p == sbt_neigh_vert[s_bn][i][s]) {
		    continue;
		}
		sb = sbt_peak_simp[s_bn][s];
		sb_bn = sbt_peak_simp_bn[s_bn][s];
		sf = sbt_neigh_simp[s_bn][i][s];
		sf_bn = sbt_neigh_simp_bn[s_bn][i][s];
		xf = sbt_neigh_vert[ccj_bn][xfi][ccj];
		if (sbt_peak_vert[sf_bn][sf] == NOVAL) { // are we done already?
		    for (l = 0; (sbt_neigh_vert[ccj_bn][l][ccj] != sbt_neigh_vert[s_bn][i][s])
			    && (l < cdim); l++) {
			;
		    }
		    sf = sbt_neigh_simp[ccj_bn][l][ccj];
		    sf_bn = sbt_neigh_simp_bn[ccj_bn][l][ccj];
		    if (sbt_peak_vert[sf_bn][sf] != NOVAL) {
			continue;
		    }
		} else {
		    do {
			xb = xf;
			for (l = 0; ((sbt_neigh_simp[sf_bn][l][sf] != sb) || (sbt_neigh_simp_bn[sf_bn][l][sf] != sb_bn))
				&& (l < cdim); l++) {
			    ;
			}
			xf = sbt_neigh_vert[sf_bn][l][sf];
			sb = sf;
			sb_bn = sf_bn;
			for (l = 0; (sbt_neigh_vert[sb_bn][l][sb] != xb)
				&& (l < cdim); l++) {
			    ;
			}
			tf = sbt_neigh_simp[sf_bn][l][sf];
			tf_bn = sbt_neigh_simp_bn[sf_bn][l][sf];
			sf = tf;
			sf_bn = tf_bn;
		    } while (sbt_peak_vert[sf_bn][sf] != NOVAL);
		}
		sbt_neigh_simp[s_bn][i][s] = sf;
		sbt_neigh_simp_bn[s_bn][i][s] = sf_bn;
		for (l = 0; (sbt_neigh_vert[sf_bn][l][sf] != xf) && (l < cdim); l++) {
		    ;
		}
		sbt_neigh_simp[sf_bn][l][sf] = s;
		sbt_neigh_simp_bn[sf_bn][l][sf] = s_bn;
		connect(sf, sf_bn);
	    }
	}

	/**
	 * visit simplices s with sees(p,s), and make a facet for every neighbor
	 * of s not seen by p.
	 *
	 * @param seen 
	 * @param seen_bn 
	 * @param ret 
	 * @param ret_bn 
	 */
	private void make_facets(final int seen, final int seen_bn,
		final int[] ret, final int[] ret_bn) {
	    int n, n_bn;
	    int q, q_bn;
	    int j;
	    if (seen == NOVAL) {
		ret[0] = NOVAL;
		return;
	    }
	    sbt_peak_vert[seen_bn][seen] = p;
	    for (int i = 0; i < cdim; i++) {
		n = sbt_neigh_simp[seen_bn][i][seen];
		n_bn = sbt_neigh_simp_bn[seen_bn][i][seen];
		if (pnum != sbt_visit[n_bn][n]) {
		    sbt_visit[n_bn][n] = pnum;
		    if (sees(p, n, n_bn) != 0) {
			make_facets(n, n_bn, voidp, voidp_bn);
		    }
		}
		if (sbt_peak_vert[n_bn][n] != NOVAL) {
		    continue;
		}
		ns = (simplex_list != NOVAL) ? simplex_list
			: new_block_simplex();
		ns_bn = simplex_list_bn;
		simplex_list = sbt_next[ns_bn][ns];
		simplex_list_bn = sbt_next_bn[ns_bn][ns];
		sbt_next[ns_bn][ns] = sbt_next[seen_bn][seen];
		sbt_next_bn[ns_bn][ns] = sbt_next_bn[seen_bn][seen];
		sbt_visit[ns_bn][ns] = sbt_visit[seen_bn][seen];
		sbt_mark[ns_bn][ns] = sbt_mark[seen_bn][seen];
		sbt_normal[ns_bn][ns] = sbt_normal[seen_bn][seen];
		sbt_normal_bn[ns_bn][ns] = sbt_normal_bn[seen_bn][seen];
		sbt_peak_vert[ns_bn][ns] = sbt_peak_vert[seen_bn][seen];
		sbt_peak_simp[ns_bn][ns] = sbt_peak_simp[seen_bn][seen];
		sbt_peak_simp_bn[ns_bn][ns] = sbt_peak_simp_bn[seen_bn][seen];
		sbt_peak_basis[ns_bn][ns] = sbt_peak_basis[seen_bn][seen];
		sbt_peak_basis_bn[ns_bn][ns] = sbt_peak_basis_bn[seen_bn][seen];
		for (j = 0; j < rdim; j++) {
		    sbt_neigh_vert[ns_bn][j][ns] = sbt_neigh_vert[seen_bn][j][seen];
		    sbt_neigh_simp[ns_bn][j][ns] = sbt_neigh_simp[seen_bn][j][seen];
		    sbt_neigh_simp_bn[ns_bn][j][ns] = sbt_neigh_simp_bn[seen_bn][j][seen];
		    sbt_neigh_basis[ns_bn][j][ns] = sbt_neigh_basis[seen_bn][j][seen];
		    sbt_neigh_basis_bn[ns_bn][j][ns] = sbt_neigh_basis_bn[seen_bn][j][seen];
		}
		for (j = 0; j < cdim; j++) {
		    q = sbt_neigh_basis[seen_bn][j][seen];
		    q_bn = sbt_neigh_basis_bn[seen_bn][j][seen];
		    if (q != NOVAL) {
			bbt_ref_count[q_bn][q]++;
		    }
		}
		sbt_visit[ns_bn][ns] = 0;
		sbt_peak_vert[ns_bn][ns] = NOVAL;
		sbt_normal[ns_bn][ns] = NOVAL;
		sbt_peak_simp[ns_bn][ns] = seen;
		sbt_peak_simp_bn[ns_bn][ns] = seen_bn;
		q = sbt_neigh_basis[ns_bn][i][ns];
		q_bn = sbt_neigh_basis_bn[ns_bn][i][ns];
		if ((q != NOVAL) && (--bbt_ref_count[q_bn][q] == 0)) {
		    bbt_next[q_bn][q] = basis_s_list;
		    bbt_next_bn[q_bn][q] = basis_s_list_bn;
		    bbt_ref_count[q_bn][q] = 0;
		    bbt_lscale[q_bn][q] = 0;
		    bbt_sqa[q_bn][q] = 0;
		    bbt_sqb[q_bn][q] = 0;
		    for (int l = 0; l < (2 * rdim); l++) {
			bbt_vecs[q_bn][l][q] = 0;
		    }
		    basis_s_list = q;
		    basis_s_list_bn = q_bn;
		}
		sbt_neigh_basis[ns_bn][i][ns] = NOVAL;
		sbt_neigh_vert[ns_bn][i][ns] = p;
		for (j = 0; ((sbt_neigh_simp[n_bn][j][n] != seen) || (sbt_neigh_simp_bn[n_bn][j][n] != seen_bn))
			&& (j < cdim); j++) {
		    ;
		}
		sbt_neigh_simp[seen_bn][i][seen] = sbt_neigh_simp[n_bn][j][n] = ns;
		sbt_neigh_simp_bn[seen_bn][i][seen] = ns_bn;
		sbt_neigh_simp_bn[n_bn][j][n] = ns_bn;
	    }
	    ret[0] = ns;
	    ret_bn[0] = ns_bn;
	}

	/**
	 * p lies outside flat containing previous sites; make p a vertex of
	 * every current simplex, and create some new simplices.
	 *
	 * @param s 
	 * @param s_bn 
	 * @param ret 
	 * @param ret_bn 
	 */
	private void extend_simplices(final int s, final int s_bn,
		final int[] ret, final int[] ret_bn) {
	    int q, q_bn;
	    int ns, ns_bn;
	    if (sbt_visit[s_bn][s] == pnum) {
		if (sbt_peak_vert[s_bn][s] != NOVAL) {
		    ret[0] = sbt_neigh_simp[s_bn][cdim - 1][s];
		    ret_bn[0] = sbt_neigh_simp_bn[s_bn][cdim - 1][s];
		} else {
		    ret[0] = s;
		    ret_bn[0] = s_bn;
		}
		return;
	    }
	    sbt_visit[s_bn][s] = pnum;
	    sbt_neigh_vert[s_bn][cdim - 1][s] = p;
	    q = sbt_normal[s_bn][s];
	    q_bn = sbt_normal_bn[s_bn][s];
	    if ((q != NOVAL) && (--bbt_ref_count[q_bn][q] == 0)) {
		bbt_next[q_bn][q] = basis_s_list;
		bbt_next_bn[q_bn][q] = basis_s_list_bn;
		bbt_ref_count[q_bn][q] = 0;
		bbt_lscale[q_bn][q] = 0;
		bbt_sqa[q_bn][q] = 0;
		bbt_sqb[q_bn][q] = 0;
		for (int j = 0; j < (2 * rdim); j++) {
		    bbt_vecs[q_bn][j][q] = 0;
		}
		basis_s_list = q;
		basis_s_list_bn = q_bn;
	    }
	    sbt_normal[s_bn][s] = NOVAL;
	    q = sbt_neigh_basis[s_bn][0][s];
	    q_bn = sbt_neigh_basis_bn[s_bn][0][s];
	    if ((q != NOVAL) && (--bbt_ref_count[q_bn][q] == 0)) {
		bbt_next[q_bn][q] = basis_s_list;
		bbt_ref_count[q_bn][q] = 0;
		bbt_lscale[q_bn][q] = 0;
		bbt_sqa[q_bn][q] = 0;
		bbt_sqb[q_bn][q] = 0;
		for (int j = 0; j < (2 * rdim); j++) {
		    bbt_vecs[q_bn][j][q] = 0;
		}
		basis_s_list = q;
		basis_s_list_bn = q_bn;
	    }
	    sbt_neigh_basis[s_bn][0][s] = NOVAL;
	    if (sbt_peak_vert[s_bn][s] == NOVAL) {
		final int[] esretp = new int[1];
		final int[] esretp_bn = new int[1];
		extend_simplices(sbt_peak_simp[s_bn][s],
			sbt_peak_simp_bn[s_bn][s], esretp, esretp_bn);
		sbt_neigh_simp[s_bn][cdim - 1][s] = esretp[0];
		sbt_neigh_simp_bn[s_bn][cdim - 1][s] = esretp_bn[0];
		ret[0] = s;
		ret_bn[0] = s_bn;
		return;
	    } else {
		ns = (simplex_list != NOVAL) ? simplex_list
			: new_block_simplex();
		ns_bn = simplex_list_bn;
		simplex_list = sbt_next[ns_bn][ns];
		simplex_list_bn = sbt_next_bn[ns_bn][ns];
		sbt_next[ns_bn][ns] = sbt_next[s_bn][s];
		sbt_next_bn[ns_bn][ns] = sbt_next_bn[s_bn][s];
		sbt_visit[ns_bn][ns] = sbt_visit[s_bn][s];
		sbt_mark[ns_bn][ns] = sbt_mark[s_bn][s];
		sbt_normal[ns_bn][ns] = sbt_normal[s_bn][s];
		sbt_normal_bn[ns_bn][ns] = sbt_normal_bn[s_bn][s];
		sbt_peak_vert[ns_bn][ns] = sbt_peak_vert[s_bn][s];
		sbt_peak_simp[ns_bn][ns] = sbt_peak_simp[s_bn][s];
		sbt_peak_simp_bn[ns_bn][ns] = sbt_peak_simp_bn[s_bn][s];
		sbt_peak_basis[ns_bn][ns] = sbt_peak_basis[s_bn][s];
		sbt_peak_basis_bn[ns_bn][ns] = sbt_peak_basis_bn[s_bn][s];
		for (int j = 0; j < rdim; j++) {
		    sbt_neigh_vert[ns_bn][j][ns] = sbt_neigh_vert[s_bn][j][s];
		    sbt_neigh_simp[ns_bn][j][ns] = sbt_neigh_simp[s_bn][j][s];
		    sbt_neigh_simp_bn[ns_bn][j][ns] = sbt_neigh_simp_bn[s_bn][j][s];
		    sbt_neigh_basis[ns_bn][j][ns] = sbt_neigh_basis[s_bn][j][s];
		    sbt_neigh_basis_bn[ns_bn][j][ns] = sbt_neigh_basis_bn[s_bn][j][s];
		}
		for (int j = 0; j < cdim; j++) {
		    q = sbt_neigh_basis[s_bn][j][s];
		    q_bn = sbt_neigh_basis_bn[s_bn][j][s];
		    if (q != NOVAL) {
			bbt_ref_count[q_bn][q]++;
		    }
		}
		sbt_neigh_simp[s_bn][cdim - 1][s] = ns;
		sbt_neigh_simp_bn[s_bn][cdim - 1][s] = ns_bn;
		sbt_peak_vert[ns_bn][ns] = NOVAL;
		sbt_peak_simp[ns_bn][ns] = s;
		sbt_peak_simp_bn[ns_bn][ns] = s_bn;
		sbt_neigh_vert[ns_bn][cdim - 1][ns] = sbt_peak_vert[s_bn][s];
		sbt_neigh_simp[ns_bn][cdim - 1][ns] = sbt_peak_simp[s_bn][s];
		sbt_neigh_simp_bn[ns_bn][cdim - 1][ns] = sbt_peak_simp_bn[s_bn][s];
		sbt_neigh_basis[ns_bn][cdim - 1][ns] = sbt_peak_basis[s_bn][s];
		sbt_neigh_basis_bn[ns_bn][cdim - 1][ns] = sbt_peak_basis_bn[s_bn][s];
		q = sbt_peak_basis[s_bn][s];
		q_bn = sbt_peak_basis_bn[s_bn][s];
		if (q != NOVAL) {
		    bbt_ref_count[q_bn][q]++;
		}
		for (int i = 0; i < cdim; i++) {
		    final int[] esretp = new int[1];
		    final int[] esretp_bn = new int[1];
		    extend_simplices(sbt_neigh_simp[ns_bn][i][ns],
			    sbt_neigh_simp_bn[ns_bn][i][ns], esretp, esretp_bn);
		    sbt_neigh_simp[ns_bn][i][ns] = esretp[0];
		    sbt_neigh_simp_bn[ns_bn][i][ns] = esretp_bn[0];
		}
	    }
	    ret[0] = ns;
	    ret_bn[0] = ns_bn;
	    return;
	}

	/**
	 * return a simplex s that corresponds to a facet of the current hull,
	 * and sees(p, s).
	 *
	 * @param root 
	 * @param root_bn 
	 * @param ret 
	 * @param ret_bn 
	 */
	private void search(final int root, final int root_bn, final int[] ret,
		final int[] ret_bn) {
	    int s, s_bn;
	    int tms = 0;
	    st[tms] = sbt_peak_simp[root_bn][root];
	    st_bn[tms] = sbt_peak_simp_bn[root_bn][root];
	    tms++;
	    sbt_visit[root_bn][root] = pnum;
	    if (sees(p, root, root_bn) == 0) {
		for (int i = 0; i < cdim; i++) {
		    st[tms] = sbt_neigh_simp[root_bn][i][root];
		    st_bn[tms] = sbt_neigh_simp_bn[root_bn][i][root];
		    tms++;
		}
	    }
	    while (tms != 0) {
		if (tms > ss) {
		    // JAVA: efficiency issue: how much is this stack hammered?
		    ss += ss;
		    final int[] newst = new int[ss + MAXDIM + 1];
		    final int[] newst_bn = new int[ss + MAXDIM + 1];
		    System.arraycopy(st, 0, newst, 0, st.length);
		    System.arraycopy(st_bn, 0, newst_bn, 0, st_bn.length);
		    st = newst;
		    st_bn = newst_bn;
		}
		tms--;
		s = st[tms];
		s_bn = st_bn[tms];
		if (sbt_visit[s_bn][s] == pnum) {
		    continue;
		}
		sbt_visit[s_bn][s] = pnum;
		if (sees(p, s, s_bn) == 0) {
		    continue;
		}
		if (sbt_peak_vert[s_bn][s] == NOVAL) {
		    ret[0] = s;
		    ret_bn[0] = s_bn;
		    return;
		}
		for (int i = 0; i < cdim; i++) {
		    st[tms] = sbt_neigh_simp[s_bn][i][s];
		    st_bn[tms] = sbt_neigh_simp_bn[s_bn][i][s];
		    tms++;
		}
	    }
	    ret[0] = NOVAL;
	    return;
	}

	/**
	 * construct a Delaunay triangulation of the points in the samples array
	 * using Clarkson's algorithm.
	 *
	 * @param samples            locations of points for topology - dimensioned
	 *            double[dimension][number_of_points]
	 * @param closest 
	 */
	public DelaunayClarkson(final double[][] samples, final double closest) {
	    int s, s_bn, q, q_bn;
	    int root, root_bn;
	    final int[] retp = new int[1];
	    final int[] retp_bn = new int[1];
	    final int[] ret2p = new int[1];
	    final int[] ret2p_bn = new int[1];
	    final int[] curt = new int[1];
	    final int[] curt_bn = new int[1];
	    int s_num = 0;
	    int nrs;
	    // Start of main hull triangulation algorithm
	    dim = samples.length;
	    nrs = samples[0].length;
	    for (int i = 1; i < dim; i++) {
		nrs = Math.min(nrs, samples[i].length);
	    }
	    if (nrs <= dim) {
		throw new IllegalArgumentException();
	    }
	    if (dim > MAXDIM) {
		throw new IllegalArgumentException();
	    }
	    // copy samples
	    site_blocks = new double[dim][nrs];
	    for (int j = 0; j < dim; j++) {
		System.arraycopy(samples[j], 0, site_blocks[j], 0, nrs);
	    }
	    final double expansion = (closest < 1) ? ((closest > 0) ? 1.0 / closest
		    : 0.001)
		    : 1.0;
	    for (int j = 0; j < dim; j++) {
		for (int kk = 0; kk < nrs; kk++) {
		    site_blocks[j][kk] = expansion * samples[j][kk];
		}
	    }
	    exact_bits = (int) ((DBL_MANT_DIG * Math.log(FLT_RADIX)) / ln2);
	    b_err_min = DBL_EPSILON * MAXDIM * (1 << MAXDIM) * MAXDIM * 3.01;
	    b_err_min_sq = b_err_min * b_err_min;
	    cdim = 0;
	    rdim = dim + 1;
	    if (rdim > MAXDIM) {
		throw new IllegalArgumentException(
			"dimension bound MAXDIM exceeded; rdim=" + rdim
			+ "; dim=" + dim);
	    }
	    pnb = basis_s_list != NOVAL ? basis_s_list : new_block_basis_s();
	    pnb_bn = basis_s_list_bn;
	    basis_s_list = bbt_next[pnb_bn][pnb];
	    basis_s_list_bn = bbt_next_bn[pnb_bn][pnb];
	    bbt_next[pnb_bn][pnb] = NOVAL;
	    ttbp = basis_s_list != NOVAL ? basis_s_list : new_block_basis_s();
	    ttbp_bn = basis_s_list_bn;
	    basis_s_list = bbt_next[ttbp_bn][ttbp];
	    basis_s_list_bn = bbt_next_bn[ttbp_bn][ttbp];
	    bbt_next[ttbp_bn][ttbp] = NOVAL;
	    bbt_ref_count[ttbp_bn][ttbp] = 1;
	    bbt_lscale[ttbp_bn][ttbp] = -1;
	    bbt_sqa[ttbp_bn][ttbp] = 0;
	    bbt_sqb[ttbp_bn][ttbp] = 0;
	    for (int j = 0; j < (2 * rdim); j++) {
		bbt_vecs[ttbp_bn][j][ttbp] = 0;
	    }
	    root = NOVAL;
	    p = INFINITY;
	    ib = (basis_s_list != NOVAL) ? basis_s_list : new_block_basis_s();
	    ib_bn = basis_s_list_bn;
	    basis_s_list = bbt_next[ib_bn][ib];
	    basis_s_list_bn = bbt_next_bn[ib_bn][ib];
	    bbt_ref_count[ib_bn][ib] = 1;
	    bbt_vecs[ib_bn][(2 * rdim) - 1][ib] = bbt_vecs[ib_bn][rdim - 1][ib] = 1;
	    bbt_sqa[ib_bn][ib] = bbt_sqb[ib_bn][ib] = 1;
	    root = (simplex_list != NOVAL) ? simplex_list : new_block_simplex();
	    root_bn = simplex_list_bn;
	    simplex_list = sbt_next[root_bn][root];
	    simplex_list_bn = sbt_next_bn[root_bn][root];
	    ch_root = root;
	    ch_root_bn = root_bn;
	    s = (simplex_list != NOVAL) ? simplex_list : new_block_simplex();
	    s_bn = simplex_list_bn;
	    simplex_list = sbt_next[s_bn][s];
	    simplex_list_bn = sbt_next_bn[s_bn][s];
	    sbt_next[s_bn][s] = sbt_next[root_bn][root];
	    sbt_next_bn[s_bn][s] = sbt_next_bn[root_bn][root];
	    sbt_visit[s_bn][s] = sbt_visit[root_bn][root];
	    sbt_mark[s_bn][s] = sbt_mark[root_bn][root];
	    sbt_normal[s_bn][s] = sbt_normal[root_bn][root];
	    sbt_normal_bn[s_bn][s] = sbt_normal_bn[root_bn][root];
	    sbt_peak_vert[s_bn][s] = sbt_peak_vert[root_bn][root];
	    sbt_peak_simp[s_bn][s] = sbt_peak_simp[root_bn][root];
	    sbt_peak_simp_bn[s_bn][s] = sbt_peak_simp_bn[root_bn][root];
	    sbt_peak_basis[s_bn][s] = sbt_peak_basis[root_bn][root];
	    sbt_peak_basis_bn[s_bn][s] = sbt_peak_basis_bn[root_bn][root];
	    for (int i = 0; i < rdim; i++) {
		sbt_neigh_vert[s_bn][i][s] = sbt_neigh_vert[root_bn][i][root];
		sbt_neigh_simp[s_bn][i][s] = sbt_neigh_simp[root_bn][i][root];
		sbt_neigh_simp_bn[s_bn][i][s] = sbt_neigh_simp_bn[root_bn][i][root];
		sbt_neigh_basis[s_bn][i][s] = sbt_neigh_basis[root_bn][i][root];
		sbt_neigh_basis_bn[s_bn][i][s] = sbt_neigh_basis_bn[root_bn][i][root];
	    }
	    for (int i = 0; i < cdim; i++) {
		q = sbt_neigh_basis[root_bn][i][root];
		q_bn = sbt_neigh_basis_bn[root_bn][i][root];
		if (q != NOVAL) {
		    bbt_ref_count[q_bn][q]++;
		}
	    }
	    sbt_peak_vert[root_bn][root] = p;
	    sbt_peak_simp[root_bn][root] = s;
	    sbt_peak_simp_bn[root_bn][root] = s_bn;
	    sbt_peak_simp[s_bn][s] = root;
	    sbt_peak_simp_bn[s_bn][s] = root_bn;
	    while (cdim < rdim) {
		int oof = 0;
		if (s_num == 0) {
		    p = 0;
		} else {
		    p++;
		}
		for (int i = 0; i < dim; i++) {
		    site_blocks[i][p] = Math.floor(site_blocks[i][p] + 0.5);
		}
		s_num++;
		pnum = (((s_num * dim) - 1) / dim) + 2;
		cdim++;
		sbt_neigh_vert[root_bn][cdim - 1][root] = sbt_peak_vert[root_bn][root];
		q = sbt_neigh_basis[root_bn][cdim - 1][root];
		q_bn = sbt_neigh_basis_bn[root_bn][cdim - 1][root];
		if ((q != NOVAL) && (--bbt_ref_count[q_bn][q] == 0)) {
		    bbt_next[q_bn][q] = basis_s_list;
		    bbt_next_bn[q_bn][q] = basis_s_list_bn;
		    bbt_ref_count[q_bn][q] = 0;
		    bbt_lscale[q_bn][q] = 0;
		    bbt_sqa[q_bn][q] = 0;
		    bbt_sqb[q_bn][q] = 0;
		    for (int l = 0; l < (2 * rdim); l++) {
			bbt_vecs[q_bn][l][q] = 0;
		    }
		    basis_s_list = q;
		    basis_s_list_bn = q_bn;
		}
		sbt_neigh_basis[root_bn][cdim - 1][root] = NOVAL;
		get_basis_sede(root, root_bn);
		if (sbt_neigh_vert[root_bn][0][root] == INFINITY) {
		    oof = 1;
		} else {
		    curt[0] = pnb;
		    curt_bn[0] = pnb_bn;
		    reduce(curt, curt_bn, p, root, root_bn, cdim);
		    pnb = curt[0];
		    pnb_bn = curt_bn[0];
		    if (bbt_sqa[pnb_bn][pnb] != 0) {
			oof = 1;
		    } else {
			cdim--;
		    }
		}
		if (oof != 0) {
		    extend_simplices(root, root_bn, voidp, voidp_bn);
		} else {
		    search(root, root_bn, retp, retp_bn);
		    make_facets(retp[0], retp_bn[0], ret2p, ret2p_bn);
		    connect(ret2p[0], ret2p_bn[0]);
		}
	    }
	    for (int i = s_num; i < nrs; i++) {
		p++;
		s_num++;
		for (int j = 0; j < dim; j++) {
		    site_blocks[j][p] = Math.floor(site_blocks[j][p] + 0.5);
		}
		pnum = (((s_num * dim) - 1) / dim) + 2;
		search(root, root_bn, retp, retp_bn);
		make_facets(retp[0], retp_bn[0], ret2p, ret2p_bn);
		connect(ret2p[0], ret2p_bn[0]);
	    }
	    a3size = rdim * nrs;
	    a3s = new int[rdim][a3size + MAXDIM + 1];
	    visit_triang_gen(root, root_bn, 1, retp, retp_bn);
	    visit_triang_gen(retp[0], retp_bn[0], 0, voidp, voidp_bn);
	    // deallocate memory
	    /*
	     * NOTE: If this deallocation is not performed, more points could
	     * theoretically be added to the triangulation later
	     */
	    site_blocks = null;
	    st = null;
	    st_bn = null;
	    st2 = null;
	    st2_bn = null;
	    sbt_next = null;
	    sbt_next_bn = null;
	    sbt_visit = null;
	    sbt_mark = null;
	    sbt_normal = null;
	    sbt_normal_bn = null;
	    sbt_peak_vert = null;
	    sbt_peak_simp = null;
	    sbt_peak_simp_bn = null;
	    sbt_peak_basis = null;
	    sbt_peak_basis_bn = null;
	    sbt_neigh_vert = null;
	    sbt_neigh_simp = null;
	    sbt_neigh_simp_bn = null;
	    sbt_neigh_basis = null;
	    sbt_neigh_basis_bn = null;
	    bbt_next = null;
	    bbt_next_bn = null;
	    bbt_ref_count = null;
	    bbt_lscale = null;
	    bbt_sqa = null;
	    bbt_sqb = null;
	    bbt_vecs = null;
	    /* ********** END OF CONVERTED HULL CODE ********** */
	    /* (but still inside constructor) */
	    // compute number of triangles or tetrahedra
	    final int[] nverts = new int[nrs];
	    for (int i = 0; i < nrs; i++) {
		nverts[i] = 0;
	    }
	    int ntris = 0;
	    boolean positive;
	    for (int i = 0; i < nts; i++) {
		positive = true;
		for (int j = 0; j < rdim; j++) {
		    if (a3s[j][i] < 0) {
			positive = false;
		    }
		}
		if (positive) {
		    ntris++;
		    for (int j = 0; j < rdim; j++) {
			nverts[a3s[j][i]]++;
		    }
		}
	    }
	    Vertices = new int[nrs][];
	    for (int i = 0; i < nrs; i++) {
		Vertices[i] = new int[nverts[i]];
	    }
	    for (int i = 0; i < nrs; i++) {
		nverts[i] = 0;
	    }
	    // build Tri & Vertices components
	    Tri = new int[ntris][rdim];
	    int itri = 0;
	    for (int i = 0; i < nts; i++) {
		positive = true;
		for (int j = 0; j < rdim; j++) {
		    if (a3s[j][i] < 0) {
			positive = false;
		    }
		}
		if (positive) {
		    for (int j = 0; j < rdim; j++) {
			Vertices[a3s[j][i]][nverts[a3s[j][i]]++] = itri;
			Tri[itri][j] = a3s[j][i];
		    }
		    itri++;
		}
	    }
	    // Deallocate remaining helper information
	    a3s = null;
	    // call more generic method for constructing Walk and Edges arrays
	    finish_triang(samples);
	}
    }

    //
    // DelaunayWatson.java
    //
    /*
     * VisAD system for interactive analysis and visualization of numerical
     * data. Copyright (C) 1996 - 2011 Bill Hibbard, Curtis Rueden, Tom Rink,
     * Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and Tommy
     * Jasmin.
     * 
     * This library is free software; you can redistribute it and/or modify it
     * under the terms of the GNU Library General Public License as published by
     * the Free Software Foundation; either version 2 of the License, or (at
     * your option) any later version.
     * 
     * This library is distributed in the hope that it will be useful, but
     * WITHOUT ANY WARRANTY; without even the implied warranty of
     * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library
     * General Public License for more details.
     * 
     * You should have received a copy of the GNU Library General Public License
     * along with this library; if not, write to the Free Software Foundation,
     * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
     */
    /*
     * The Delaunay triangulation/tetrahedralization algorithm in this class is
     * originally from nnsort.c by David F. Watson:
     * 
     * nnsort() finds the Delaunay triangulation of the two- or three-component
     * vectors in 'data_list' and returns a list of simplex vertices in
     * 'vertices' with the corresponding circumcentre and squared radius in the
     * rows of 'circentres'. nnsort() also can be used to find the ordered
     * convex hull of the two- or three-component vectors in 'data_list' and
     * returns a list of (d-1)-facet vertices in 'vertices' (dummy filename for
     * 'circentres' must be used). nnsort() was written by Dave Watson and uses
     * the algorithm described in - Watson, D.F., 1981, Computing the
     * n-dimensional Delaunay tessellation with application to Voronoi
     * polytopes: The Computer J., 24(2), p. 167-172.
     * 
     * additional information about this algorithm can be found in - CONTOURING:
     * A guide to the analysis and display of spatial data, by David F. Watson,
     * Pergamon Press, 1992, ISBN 0 08 040286 0
     */
    /**
     * DelaunayWatson represents an O(N^2) method with low overhead to find the
     * Delaunay triangulation or tetrahedralization of a set of samples of R^2
     * or R^3.
     * <P>
     */
    static class DelaunayWatson extends WB_Delaunay {
	
	/**
	 * 
	 */
	private static final double BIGNUM = 1E37;
	
	/**
	 * 
	 */
	private static final double EPSILON = 0.00001f;
	// temporary storage size factor
	/**
	 * 
	 */
	private static final int TSIZE = 75;
	// factor (>=1) for radius of control points
	/**
	 * 
	 */
	private static final double RANGE = 10.0f;

	/**
	 * construct a Delaunay triangulation of the points in the samples array
	 * using Watson's algorithm.
	 *
	 * @param samples            locations of points for topology - dimensioned
	 *            double[dimension][number_of_points]
	 */
	public DelaunayWatson(final double[][] samples) {
	    final int dim = samples.length;
	    final int nrs = samples[0].length;
	    double xx, bgs;
	    int i0, i1, i2, i3, i4, i5, i6, i7, i8, i9, i11;
	    final int[] ii = new int[3];
	    int dm, dim1, nts, tsz;
	    final double[][] mxy = new double[2][dim];
	    for (i0 = 0; i0 < dim; i0++) {
		mxy[0][i0] = -(mxy[1][i0] = BIGNUM);
	    }
	    dim1 = dim + 1;
	    final double[][] wrk = new double[dim][dim1];
	    for (i0 = 0; i0 < dim; i0++) {
		for (i1 = 0; i1 < dim1; i1++) {
		    wrk[i0][i1] = -RANGE;
		}
	    }
	    for (i0 = 0; i0 < dim; i0++) {
		wrk[i0][i0] = RANGE * ((3 * dim) - 1);
	    }
	    final double[][] pts = new double[nrs + dim1][dim];
	    for (i0 = 0; i0 < nrs; i0++) {
		if (dim < 3) {
		    pts[i0][0] = samples[0][i0];
		    pts[i0][1] = samples[1][i0];
		} else {
		    pts[i0][0] = samples[0][i0];
		    pts[i0][1] = samples[1][i0];
		    pts[i0][2] = samples[2][i0];
		}
		// compute bounding box
		for (i1 = 0; i1 < dim; i1++) {
		    if (mxy[0][i1] < pts[i0][i1]) {
			mxy[0][i1] = pts[i0][i1]; // max
		    }
		    if (mxy[1][i1] > pts[i0][i1]) {
			mxy[1][i1] = pts[i0][i1]; // min
		    }
		}
	    }
	    for (bgs = 0, i0 = 0; i0 < dim; i0++) {
		mxy[0][i0] -= mxy[1][i0];
		if (bgs < mxy[0][i0]) {
		    bgs = mxy[0][i0];
		}
	    }
	    // now bgs = largest range
	    // add random perturbations to points
	    bgs *= EPSILON;
	    final Random rand = new Random(367);
	    for (i0 = 0; i0 < nrs; i0++) {
		for (i1 = 0; i1 < dim; i1++) {
		    // random numbers [0, 1]
		    pts[i0][i1] += bgs * (0.5 - rand.nextDouble());
		}
	    }
	    for (i0 = 0; i0 < dim1; i0++) {
		for (i1 = 0; i1 < dim; i1++) {
		    pts[nrs + i0][i1] = mxy[1][i1] + (wrk[i1][i0] * mxy[0][i1]);
		}
	    }
	    for (i1 = 1, i0 = 2; i0 < dim1; i0++) {
		i1 *= i0;
	    }
	    tsz = TSIZE * i1;
	    int[][] tmp = new int[tsz + 1][dim];
	    // storage allocation - increase value of `i1' for 3D if necessary
	    i1 *= (nrs + (50 * i1));
	    /* WLH 4 Nov 97 */
	    if (dim == 3) {
		i1 *= 10;
	    }
	    /* end WLH 4 Nov 97 */
	    final int[] id = new int[i1];
	    for (i0 = 0; i0 < i1; i0++) {
		id[i0] = i0;
	    }
	    final int[][] a3s = new int[i1][dim1];
	    final double[][] ccr = new double[i1][dim1];
	    for (a3s[0][0] = nrs, i0 = 1; i0 < dim1; i0++) {
		a3s[0][i0] = a3s[0][i0 - 1] + 1;
	    }
	    for (ccr[0][dim] = BIGNUM, i0 = 0; i0 < dim; i0++) {
		ccr[0][i0] = 0;
	    }
	    nts = i4 = 1;
	    dm = dim - 1;
	    for (i0 = 0; i0 < nrs; i0++) {
		i1 = i7 = -1;
		i9 = 0;
		Loop3: for (i11 = 0; i11 < nts; i11++) {
		    i1++;
		    while (a3s[i1][0] < 0) {
			i1++;
		    }
		    xx = ccr[i1][dim];
		    for (i2 = 0; i2 < dim; i2++) {
			xx -= (pts[i0][i2] - ccr[i1][i2])
				* (pts[i0][i2] - ccr[i1][i2]);
			if (xx < 0) {
			    continue Loop3;
			}
		    }
		    i9--;
		    i4--;
		    id[i4] = i1;
		    Loop2: for (i2 = 0; i2 < dim1; i2++) {
			ii[0] = 0;
			if (ii[0] == i2) {
			    ii[0]++;
			}
			for (i3 = 1; i3 < dim; i3++) {
			    ii[i3] = ii[i3 - 1] + 1;
			    if (ii[i3] == i2) {
				ii[i3]++;
			    }
			}
			if (i7 > dm) {
			    i8 = i7;
			    Loop1: for (i3 = 0; i3 <= i8; i3++) {
				for (i5 = 0; i5 < dim; i5++) {
				    if (a3s[i1][ii[i5]] != tmp[i3][i5]) {
					continue Loop1;
				    }
				}
				for (i6 = 0; i6 < dim; i6++) {
				    tmp[i3][i6] = tmp[i8][i6];
				}
				i7--;
				continue Loop2;
			    }
			}
			if (++i7 > tsz) {
			    final int newtsz = 2 * tsz;
			    final int[][] newtmp = new int[newtsz + 1][dim];
			    System.arraycopy(tmp, 0, newtmp, 0, tsz);
			    tsz = newtsz;
			    tmp = newtmp;
			    // WLH 23 july 97
			    // throw new VisADException(
			    // "DelaunayWatson: Temporary storage exceeded");
			}
			for (i3 = 0; i3 < dim; i3++) {
			    tmp[i7][i3] = a3s[i1][ii[i3]];
			}
		    }
		    a3s[i1][0] = -1;
		}
		for (i1 = 0; i1 <= i7; i1++) {
		    for (i2 = 0; i2 < dim; i2++) {
			for (wrk[i2][dim] = 0, i3 = 0; i3 < dim; i3++) {
			    wrk[i2][i3] = pts[tmp[i1][i2]][i3] - pts[i0][i3];
			    wrk[i2][dim] += (wrk[i2][i3] * (pts[tmp[i1][i2]][i3] + pts[i0][i3])) / 2;
			}
		    }
		    if (dim < 3) {
			xx = (wrk[0][0] * wrk[1][1]) - (wrk[1][0] * wrk[0][1]);
			ccr[id[i4]][0] = ((wrk[0][2] * wrk[1][1]) - (wrk[1][2] * wrk[0][1]))
				/ xx;
			ccr[id[i4]][1] = ((wrk[0][0] * wrk[1][2]) - (wrk[1][0] * wrk[0][2]))
				/ xx;
		    } else {
			xx = ((wrk[0][0] * ((wrk[1][1] * wrk[2][2]) - (wrk[2][1] * wrk[1][2]))) - (wrk[0][1] * ((wrk[1][0] * wrk[2][2]) - (wrk[2][0] * wrk[1][2]))))
					+ (wrk[0][2] * ((wrk[1][0] * wrk[2][1]) - (wrk[2][0] * wrk[1][1])));
			ccr[id[i4]][0] = (((wrk[0][3] * ((wrk[1][1] * wrk[2][2]) - (wrk[2][1] * wrk[1][2]))) - (wrk[0][1] * ((wrk[1][3] * wrk[2][2]) - (wrk[2][3] * wrk[1][2])))) + (wrk[0][2] * ((wrk[1][3] * wrk[2][1]) - (wrk[2][3] * wrk[1][1]))))
						/ xx;
			ccr[id[i4]][1] = (((wrk[0][0] * ((wrk[1][3] * wrk[2][2]) - (wrk[2][3] * wrk[1][2]))) - (wrk[0][3] * ((wrk[1][0] * wrk[2][2]) - (wrk[2][0] * wrk[1][2])))) + (wrk[0][2] * ((wrk[1][0] * wrk[2][3]) - (wrk[2][0] * wrk[1][3]))))
						/ xx;
			ccr[id[i4]][2] = (((wrk[0][0] * ((wrk[1][1] * wrk[2][3]) - (wrk[2][1] * wrk[1][3]))) - (wrk[0][1] * ((wrk[1][0] * wrk[2][3]) - (wrk[2][0] * wrk[1][3])))) + (wrk[0][3] * ((wrk[1][0] * wrk[2][1]) - (wrk[2][0] * wrk[1][1]))))
						/ xx;
		    }
		    for (ccr[id[i4]][dim] = 0, i2 = 0; i2 < dim; i2++) {
			ccr[id[i4]][dim] += (pts[i0][i2] - ccr[id[i4]][i2])
				* (pts[i0][i2] - ccr[id[i4]][i2]);
			a3s[id[i4]][i2] = tmp[i1][i2];
		    }
		    a3s[id[i4]][dim] = i0;
		    i4++;
		    i9++;
		}
		nts += i9;
	    }
	    /*
	     * OUTPUT is in a3s ARRAY needed output is: Tri - array of pointers
	     * from triangles or tetrahedra to their corresponding vertices
	     * Vertices - array of pointers from vertices to their corresponding
	     * triangles or tetrahedra Walk - array of pointers from triangles
	     * or tetrahedra to neighboring triangles or tetrahedra Edges -
	     * array of pointers from each triangle or tetrahedron's edges to
	     * their corresponding triangles or tetrahedra
	     * 
	     * helpers: nverts - number of triangles or tetrahedra per vertex
	     */
	    // compute number of triangles or tetrahedra
	    final int[] nverts = new int[nrs];
	    for (int i = 0; i < nrs; i++) {
		nverts[i] = 0;
	    }
	    int ntris = 0;
	    i0 = -1;
	    for (i11 = 0; i11 < nts; i11++) {
		i0++;
		while (a3s[i0][0] < 0) {
		    i0++;
		}
		if (a3s[i0][0] < nrs) {
		    ntris++;
		    if (dim < 3) {
			nverts[a3s[i0][0]]++;
			nverts[a3s[i0][1]]++;
			nverts[a3s[i0][2]]++;
		    } else {
			nverts[a3s[i0][0]]++;
			nverts[a3s[i0][1]]++;
			nverts[a3s[i0][2]]++;
			nverts[a3s[i0][3]]++;
		    }
		}
	    }
	    Vertices = new int[nrs][];
	    for (int i = 0; i < nrs; i++) {
		Vertices[i] = new int[nverts[i]];
	    }
	    for (int i = 0; i < nrs; i++) {
		nverts[i] = 0;
	    }
	    // build Tri & Vertices components
	    Tri = new int[ntris][dim1];
	    int a, b, c, d;
	    int itri = 0;
	    i0 = -1;
	    for (i11 = 0; i11 < nts; i11++) {
		i0++;
		while (a3s[i0][0] < 0) {
		    i0++;
		}
		if (a3s[i0][0] < nrs) {
		    if (dim < 3) {
			a = a3s[i0][0];
			b = a3s[i0][1];
			c = a3s[i0][2];
			Vertices[a][nverts[a]] = itri;
			nverts[a]++;
			Vertices[b][nverts[b]] = itri;
			nverts[b]++;
			Vertices[c][nverts[c]] = itri;
			nverts[c]++;
			Tri[itri][0] = a;
			Tri[itri][1] = b;
			Tri[itri][2] = c;
		    } else {
			a = a3s[i0][0];
			b = a3s[i0][1];
			c = a3s[i0][2];
			d = a3s[i0][3];
			Vertices[a][nverts[a]] = itri;
			nverts[a]++;
			Vertices[b][nverts[b]] = itri;
			nverts[b]++;
			Vertices[c][nverts[c]] = itri;
			nverts[c]++;
			Vertices[d][nverts[d]] = itri;
			nverts[d]++;
			Tri[itri][0] = a;
			Tri[itri][1] = b;
			Tri[itri][2] = c;
			Tri[itri][3] = d;
		    }
		    itri++;
		}
	    }
	    finish_triang(samples);
	}
    }

    //
    // DelaunayFast.java
    //
    /*
     * VisAD system for interactive analysis and visualization of numerical
     * data. Copyright (C) 1996 - 2011 Bill Hibbard, Curtis Rueden, Tom Rink,
     * Dave Glowacki, Steve Emmerson, Tom Whittaker, Don Murray, and Tommy
     * Jasmin.
     * 
     * This library is free software; you can redistribute it and/or modify it
     * under the terms of the GNU Library General Public License as published by
     * the Free Software Foundation; either version 2 of the License, or (at
     * your option) any later version.
     * 
     * This library is distributed in the hope that it will be useful, but
     * WITHOUT ANY WARRANTY; without even the implied warranty of
     * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Library
     * General Public License for more details.
     * 
     * You should have received a copy of the GNU Library General Public License
     * along with this library; if not, write to the Free Software Foundation,
     * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
     */
    /**
     * DelaunayFast is a method of finding an imperfect triangulation or
     * tetrahedralization of a set of samples of R^2 or R^3. It provides a
     * substantial speed increase over the true Delaunay triangulation
     * algorithms.
     * <P>
     */
    static class DelaunayFast extends WB_Delaunay {
	// <<< Modified quick sort routine >>>
	/**
	 * 
	 *
	 * @param array 
	 * @param samples 
	 * @param sIndex 
	 * @param lo 
	 * @param hi 
	 */
	private final void qsort(final int[] array, final double[][] samples,
		final int sIndex, final int lo, final int hi) {
	    if (lo < hi) {
		int pivot = (lo + hi) / 2;
		int swap = array[lo];
		array[lo] = array[pivot];
		array[pivot] = swap;
		pivot = lo;
		for (int i = lo + 1; i <= hi; i++) {
		    if (samples[sIndex][array[i]] < samples[sIndex][array[lo]]) {
			swap = array[i];
			array[i] = array[++pivot];
			array[pivot] = swap;
		    }
		}
		swap = array[lo];
		array[lo] = array[pivot];
		array[pivot] = swap;
		if (lo < (pivot - 1)) {
		    qsort(array, samples, sIndex, lo, pivot - 1);
		}
		if ((pivot + 1) < hi) {
		    qsort(array, samples, sIndex, pivot + 1, hi);
		}
	    }
	}

	/**
	 *  Number of radians to rotate points before triangulating.
	 */
	public static final double ROTATE = Math.PI / 18; // (10 degrees)

	/**
	 * construct an approximate Delaunay triangulation of the points in the
	 * samples array using Curtis Rueden's algorithm.
	 *
	 * @param samples            locations of points for topology - dimensioned
	 *            double[dimension][number_of_points]
	 */
	public DelaunayFast(final double[][] samples) {
	    if ((samples.length < 2) || (samples.length > 3)) {
		throw new IllegalArgumentException();
	    }
	    if (samples.length == 3) {
		throw new IllegalArgumentException();
	    }
	    final int numpts = Math.min(samples[0].length, samples[1].length);
	    if (numpts < 3) {
		throw new IllegalArgumentException();
	    }
	    final double[][] samp = new double[2][numpts];
	    System.arraycopy(samples[0], 0, samp[0], 0, numpts);
	    System.arraycopy(samples[1], 0, samp[1], 0, numpts);
	    final double[] samp0 = samp[0];
	    final double[] samp1 = samp[1];
	    // rotate samples by ROTATE radians to avoid colinear axis-parallel
	    // points
	    final double cosrot = Math.cos(ROTATE);
	    final double sinrot = Math.sin(ROTATE);
	    for (int i = 0; i < numpts; i++) {
		final double x = samp0[i];
		final double y = samp1[i];
		samp0[i] = (x * cosrot) - (y * sinrot);
		samp1[i] = (y * cosrot) + (x * sinrot);
	    }
	    // misc. variables
	    int ntris = 0;
	    final int tsize = (int) ((2f / 3f) * numpts) + 10;
	    final int[][][] tris = new int[tsize][3][];
	    int tp = 0;
	    final int[] nverts = new int[numpts];
	    for (int i = 0; i < numpts; i++) {
		nverts[i] = 0;
	    }
	    // set up the stack
	    int ssize = 20; // "stack size"
	    int[] ss = new int[ssize + 2]; // "stack start"
	    int[] se = new int[ssize + 2]; // "stack end"
	    boolean[] vh = new boolean[ssize + 2]; // "vertical/horizontal"
	    boolean[] mp = new boolean[ssize + 2]; // "merge points"
	    int sp = 0; // "stack pointer"
	    int hsize = 10; // "hull stack size"
	    int[][] hs = new int[hsize + 2][]; // "hull stack"
	    int hsp = 0; // "hull stack pointer"
	    // set up initial conditions
	    final int[] indices = new int[numpts];
	    for (int i = 0; i < numpts; i++) {
		indices[i] = i;
	    }
	    // add initial conditions to stack
	    sp++;
	    ss[0] = 0;
	    se[0] = numpts - 1;
	    vh[0] = false;
	    mp[0] = false;
	    // stack loop variables
	    int css;
	    int cse;
	    boolean cvh;
	    boolean cmp;
	    // stack loop
	    while (sp != 0) {
		if (hsp > hsize) {
		    // expand hull stack if necessary
		    hsize += hsize;
		    final int newhs[][] = new int[hsize + 2][];
		    System.arraycopy(hs, 0, newhs, 0, hs.length);
		    hs = newhs;
		}
		if (sp > ssize) {
		    // expand stack if necessary
		    ssize += ssize;
		    final int[] newss = new int[ssize + 2];
		    final int[] newse = new int[ssize + 2];
		    final boolean[] newvh = new boolean[ssize + 2];
		    final boolean[] newmp = new boolean[ssize + 2];
		    System.arraycopy(ss, 0, newss, 0, ss.length);
		    System.arraycopy(se, 0, newse, 0, se.length);
		    System.arraycopy(vh, 0, newvh, 0, vh.length);
		    System.arraycopy(mp, 0, newmp, 0, mp.length);
		    ss = newss;
		    se = newse;
		    vh = newvh;
		    mp = newmp;
		}
		// pop action from stack
		sp--;
		css = ss[sp];
		cse = se[sp];
		cvh = vh[sp];
		cmp = mp[sp];
		if (!cmp) {
		    // division step
		    if ((cse - css) >= 3) {
			// sort step
			qsort(indices, samp, cvh ? 0 : 1, css, cse);
			// push merge action onto stack
			ss[sp] = css;
			se[sp] = cse;
			vh[sp] = cvh;
			mp[sp] = true;
			sp++;
			// divide, and push two halves onto stack
			final int mid = (css + cse) / 2;
			ss[sp] = css;
			se[sp] = mid;
			vh[sp] = !cvh;
			mp[sp] = false;
			sp++;
			ss[sp] = mid + 1;
			se[sp] = cse;
			vh[sp] = !cvh;
			mp[sp] = false;
			sp++;
		    } else {
			// connect step, also push hulls onto hull stack
			int[] hull;
			if (((cse - css) + 1) == 3) {
			    hull = new int[3];
			    hull[0] = indices[css];
			    hull[1] = indices[css + 1];
			    hull[2] = indices[cse];
			    final double a0x = samp0[hull[0]];
			    final double a0y = samp1[hull[0]];
			    if ((((samp0[hull[1]] - a0x) * (samp1[hull[2]] - a0y)) - ((samp1[hull[1]] - a0y) * (samp0[hull[2]] - a0x))) > 0) {
				// adjust step, hull must remain clockwise
				hull[1] = indices[cse];
				hull[2] = indices[css + 1];
			    }
			    tris[tp][0] = new int[1];
			    tris[tp][1] = new int[1];
			    tris[tp][2] = new int[1];
			    tris[tp][0][0] = hull[0];
			    tris[tp][1][0] = hull[1];
			    tris[tp][2][0] = hull[2];
			    tp++;
			    ntris++;
			    nverts[indices[css]]++;
			    nverts[indices[cse]]++;
			    nverts[indices[css + 1]]++;
			} else {
			    hull = new int[2];
			    hull[0] = indices[css];
			    hull[1] = indices[cse];
			}
			hs[hsp++] = hull;
		    }
		} else {
		    // merge step
		    final int coord = cvh ? 1 : 0;
		    // pop hull arrays from stack
		    int[] hull1, hull2;
		    hsp -= 2;
		    hull2 = cvh ? hs[hsp + 1] : hs[hsp];
		    hull1 = cvh ? hs[hsp] : hs[hsp + 1];
		    hs[hsp + 1] = null;
		    hs[hsp] = null;
		    // find upper and lower convex hull additions
		    int upp1 = 0;
		    int upp2 = 0;
		    int low1 = 0;
		    int low2 = 0;
		    // find initial upper and lower hull indices for later
		    // optimization
		    for (int i = 1; i < hull1.length; i++) {
			if (samp[coord][hull1[i]] > samp[coord][hull1[upp1]]) {
			    upp1 = i;
			}
			if (samp[coord][hull1[i]] < samp[coord][hull1[low1]]) {
			    low1 = i;
			}
		    }
		    for (int i = 1; i < hull2.length; i++) {
			if (samp[coord][hull2[i]] > samp[coord][hull2[upp2]]) {
			    upp2 = i;
			}
			if (samp[coord][hull2[i]] < samp[coord][hull2[low2]]) {
			    low2 = i;
			}
		    }
		    // hull sweep must be performed thrice to ensure correctness
		    for (int t = 0; t < 3; t++) {
			// optimize upp1
			int bob = (upp1 + 1) % hull1.length;
			double ax = samp0[hull2[upp2]];
			double ay = samp1[hull2[upp2]];
			double bamx = samp0[hull1[bob]] - ax;
			double bamy = samp1[hull1[bob]] - ay;
			double camx = samp0[hull1[upp1]] - ax;
			double camy = samp1[hull1[upp1]] - ay;
			double u = (cvh) ? (double) (bamy / Math
				.sqrt((bamx * bamx) + (bamy * bamy)))
				: (double) (bamx / Math.sqrt((bamx * bamx)
					+ (bamy * bamy)));
			double v = (cvh) ? (double) (camy / Math
				.sqrt((camx * camx) + (camy * camy)))
				: (double) (camx / Math.sqrt((camx * camx)
					+ (camy * camy)));
			boolean plus_dir = (u < v);
			if (!plus_dir) {
			    bob = upp1;
			    u = 0;
			    v = 1;
			}
			while (u < v) {
			    upp1 = bob;
			    bob = plus_dir ? (upp1 + 1) % hull1.length
				    : ((upp1 + hull1.length) - 1)
					    % hull1.length;
			    bamx = samp0[hull1[bob]] - ax;
			    bamy = samp1[hull1[bob]] - ay;
			    camx = samp0[hull1[upp1]] - ax;
			    camy = samp1[hull1[upp1]] - ay;
			    u = (cvh) ? (double) (bamy / Math
				    .sqrt((bamx * bamx) + (bamy * bamy)))
				    : (double) (bamx / Math.sqrt((bamx * bamx)
					    + (bamy * bamy)));
			    v = (cvh) ? (double) (camy / Math
				    .sqrt((camx * camx) + (camy * camy)))
				    : (double) (camx / Math.sqrt((camx * camx)
					    + (camy * camy)));
			}
			// optimize upp2
			bob = (upp2 + 1) % hull2.length;
			ax = samp0[hull1[upp1]];
			ay = samp1[hull1[upp1]];
			bamx = samp0[hull2[bob]] - ax;
			bamy = samp1[hull2[bob]] - ay;
			camx = samp0[hull2[upp2]] - ax;
			camy = samp1[hull2[upp2]] - ay;
			u = (cvh) ? (double) (bamy / Math.sqrt((bamx * bamx)
				+ (bamy * bamy))) : (double) (bamx / Math
					.sqrt((bamx * bamx) + (bamy * bamy)));
			v = (cvh) ? (double) (camy / Math.sqrt((camx * camx)
				+ (camy * camy))) : (double) (camx / Math
					.sqrt((camx * camx) + (camy * camy)));
			plus_dir = (u < v);
			if (!plus_dir) {
			    bob = upp2;
			    u = 0;
			    v = 1;
			}
			while (u < v) {
			    upp2 = bob;
			    bob = plus_dir ? (upp2 + 1) % hull2.length
				    : ((upp2 + hull2.length) - 1)
					    % hull2.length;
			    bamx = samp0[hull2[bob]] - ax;
			    bamy = samp1[hull2[bob]] - ay;
			    camx = samp0[hull2[upp2]] - ax;
			    camy = samp1[hull2[upp2]] - ay;
			    u = (cvh) ? (double) (bamy / Math
				    .sqrt((bamx * bamx) + (bamy * bamy)))
				    : (double) (bamx / Math.sqrt((bamx * bamx)
					    + (bamy * bamy)));
			    v = (cvh) ? (double) (camy / Math
				    .sqrt((camx * camx) + (camy * camy)))
				    : (double) (camx / Math.sqrt((camx * camx)
					    + (camy * camy)));
			}
			// optimize low1
			bob = (low1 + 1) % hull1.length;
			ax = samp0[hull2[low2]];
			ay = samp1[hull2[low2]];
			bamx = samp0[hull1[bob]] - ax;
			bamy = samp1[hull1[bob]] - ay;
			camx = samp0[hull1[low1]] - ax;
			camy = samp1[hull1[low1]] - ay;
			u = (cvh) ? (double) (bamy / Math.sqrt((bamx * bamx)
				+ (bamy * bamy))) : (double) (bamx / Math
					.sqrt((bamx * bamx) + (bamy * bamy)));
			v = (cvh) ? (double) (camy / Math.sqrt((camx * camx)
				+ (camy * camy))) : (double) (camx / Math
					.sqrt((camx * camx) + (camy * camy)));
			plus_dir = (u > v);
			if (!plus_dir) {
			    bob = low1;
			    u = 1;
			    v = 0;
			}
			while (u > v) {
			    low1 = bob;
			    bob = plus_dir ? (low1 + 1) % hull1.length
				    : ((low1 + hull1.length) - 1)
					    % hull1.length;
			    bamx = samp0[hull1[bob]] - ax;
			    bamy = samp1[hull1[bob]] - ay;
			    camx = samp0[hull1[low1]] - ax;
			    camy = samp1[hull1[low1]] - ay;
			    u = (cvh) ? (double) (bamy / Math
				    .sqrt((bamx * bamx) + (bamy * bamy)))
				    : (double) (bamx / Math.sqrt((bamx * bamx)
					    + (bamy * bamy)));
			    v = (cvh) ? (double) (camy / Math
				    .sqrt((camx * camx) + (camy * camy)))
				    : (double) (camx / Math.sqrt((camx * camx)
					    + (camy * camy)));
			}
			// optimize low2
			bob = (low2 + 1) % hull2.length;
			ax = samp0[hull1[low1]];
			ay = samp1[hull1[low1]];
			bamx = samp0[hull2[bob]] - ax;
			bamy = samp1[hull2[bob]] - ay;
			camx = samp0[hull2[low2]] - ax;
			camy = samp1[hull2[low2]] - ay;
			u = (cvh) ? (double) (bamy / Math.sqrt((bamx * bamx)
				+ (bamy * bamy))) : (double) (bamx / Math
					.sqrt((bamx * bamx) + (bamy * bamy)));
			v = (cvh) ? (double) (camy / Math.sqrt((camx * camx)
				+ (camy * camy))) : (double) (camx / Math
					.sqrt((camx * camx) + (camy * camy)));
			plus_dir = (u > v);
			if (!plus_dir) {
			    bob = low2;
			    u = 1;
			    v = 0;
			}
			while (u > v) {
			    low2 = bob;
			    bob = plus_dir ? (low2 + 1) % hull2.length
				    : ((low2 + hull2.length) - 1)
					    % hull2.length;
			    bamx = samp0[hull2[bob]] - ax;
			    bamy = samp1[hull2[bob]] - ay;
			    camx = samp0[hull2[low2]] - ax;
			    camy = samp1[hull2[low2]] - ay;
			    u = (cvh) ? (double) (bamy / Math
				    .sqrt((bamx * bamx) + (bamy * bamy)))
				    : (double) (bamx / Math.sqrt((bamx * bamx)
					    + (bamy * bamy)));
			    v = (cvh) ? (double) (camy / Math
				    .sqrt((camx * camx) + (camy * camy)))
				    : (double) (camx / Math.sqrt((camx * camx)
					    + (camy * camy)));
			}
		    }
		    // calculate number of points in inner hull
		    int nih1, nih2;
		    int noh1, noh2;
		    int h1ups, h2ups;
		    if (low1 == upp1) {
			nih1 = hull1.length;
			noh1 = 1;
			h1ups = 0;
		    } else {
			nih1 = (low1 - upp1) + 1;
			if (nih1 <= 0) {
			    nih1 += hull1.length;
			}
			noh1 = (hull1.length - nih1) + 2;
			h1ups = 1;
		    }
		    if (low2 == upp2) {
			nih2 = hull2.length;
			noh2 = 1;
			h2ups = 0;
		    } else {
			nih2 = (upp2 - low2) + 1;
			if (nih2 <= 0) {
			    nih2 += hull2.length;
			}
			noh2 = (hull2.length - nih2) + 2;
			h2ups = 1;
		    }
		    // copy hull1 & hull2 info into merged hull array
		    final int[] hull = new int[noh1 + noh2];
		    int hullnum = 0;
		    int spot;
		    // go clockwise until upp1 is reached
		    for (spot = low1; spot != upp1; hullnum++, spot = (spot + 1)
			    % hull1.length) {
			hull[hullnum] = hull1[spot];
		    }
		    // append upp1
		    hull[hullnum++] = hull1[upp1];
		    // go clockwise until low2 is reached
		    for (spot = upp2; spot != low2; hullnum++, spot = (spot + 1)
			    % hull2.length) {
			hull[hullnum] = hull2[spot];
		    }
		    // append low2
		    hull[hullnum++] = hull2[low2];
		    // now push the new, completed hull onto the hull stack
		    hs[hsp++] = hull;
		    // stitch a connection between the two triangulations
		    int base1 = low1;
		    int base2 = low2;
		    int oneUp1 = ((base1 + hull1.length) - 1) % hull1.length;
		    int oneUp2 = (base2 + 1) % hull2.length;
		    // when both sides reach the top the merge is complete
		    final int ntd = ((noh1 == 1) || (noh2 == 1)) ? (nih1 + nih2) - 1
			    : (nih1 + nih2) - 2;
		    tris[tp][0] = new int[ntd];
		    tris[tp][1] = new int[ntd];
		    tris[tp][2] = new int[ntd];
		    for (int t = 0; t < ntd; t++) {
			// special case if side 1 has reached the top
			if (h1ups == nih1) {
			    oneUp2 = (base2 + 1) % hull2.length;
			    tris[tp][0][t] = hull2[base2];
			    tris[tp][1][t] = hull1[base1];
			    tris[tp][2][t] = hull2[oneUp2];
			    ntris++;
			    nverts[hull1[base1]]++;
			    nverts[hull2[base2]]++;
			    nverts[hull2[oneUp2]]++;
			    base2 = oneUp2;
			    h2ups++;
			}
			// special case if side 2 has reached the top
			else if (h2ups == nih2) {
			    oneUp1 = ((base1 + hull1.length) - 1)
				    % hull1.length;
			    tris[tp][0][t] = hull2[base2];
			    tris[tp][1][t] = hull1[base1];
			    tris[tp][2][t] = hull1[oneUp1];
			    ntris++;
			    nverts[hull1[base1]]++;
			    nverts[hull2[base2]]++;
			    nverts[hull1[oneUp1]]++;
			    base1 = oneUp1;
			    h1ups++;
			}
			// neither side has reached the top yet
			else {
			    boolean d;
			    final int hb1 = hull1[base1];
			    final int ho1 = hull1[oneUp1];
			    final int hb2 = hull2[base2];
			    final int ho2 = hull2[oneUp2];
			    final double ax = samp0[ho2];
			    final double ay = samp1[ho2];
			    final double bx = samp0[hb2];
			    final double by = samp1[hb2];
			    final double cx = samp0[ho1];
			    final double cy = samp1[ho1];
			    final double dx = samp0[hb1];
			    final double dy = samp1[hb1];
			    final double abx = ax - bx;
			    final double aby = ay - by;
			    final double acx = ax - cx;
			    final double acy = ay - cy;
			    final double dbx = dx - bx;
			    final double dby = dy - by;
			    final double dcx = dx - cx;
			    final double dcy = dy - cy;
			    final double Q = (abx * acx) + (aby * acy);
			    final double R = (dbx * abx) + (dby * aby);
			    final double S = (acx * dcx) + (acy * dcy);
			    final double T = (dbx * dcx) + (dby * dcy);
			    final boolean QD = ((abx * acy) - (aby * acx)) >= 0;
			    final boolean RD = ((dbx * aby) - (dby * abx)) >= 0;
			    final boolean SD = ((acx * dcy) - (acy * dcx)) >= 0;
			    final boolean TD = ((dcx * dby) - (dcy * dbx)) >= 0;
			    final boolean sig = ((QD ? 1 : 0) + (RD ? 1 : 0)
				    + (SD ? 1 : 0) + (TD ? 1 : 0)) < 2;
			    if (QD == sig) {
				d = true;
			    } else if (RD == sig) {
				d = false;
			    } else if (SD == sig) {
				d = false;
			    } else if (TD == sig) {
				d = true;
			    } else if (((Q < 0) && (T < 0))
				    || ((R > 0) && (S > 0))) {
				d = true;
			    } else if (((R < 0) && (S < 0))
				    || ((Q > 0) && (T > 0))) {
				d = false;
			    } else if ((Q < 0 ? Q : T) < (R < 0 ? R : S)) {
				d = true;
			    } else {
				d = false;
			    }
			    if (d) {
				tris[tp][0][t] = hull2[base2];
				tris[tp][1][t] = hull1[base1];
				tris[tp][2][t] = hull2[oneUp2];
				ntris++;
				nverts[hull1[base1]]++;
				nverts[hull2[base2]]++;
				nverts[hull2[oneUp2]]++;
				// use diagonal (base1, oneUp2) as new base
				base2 = oneUp2;
				h2ups++;
				oneUp2 = (base2 + 1) % hull2.length;
			    } else {
				tris[tp][0][t] = hull2[base2];
				tris[tp][1][t] = hull1[base1];
				tris[tp][2][t] = hull1[oneUp1];
				ntris++;
				nverts[hull1[base1]]++;
				nverts[hull2[base2]]++;
				nverts[hull1[oneUp1]]++;
				// use diagonal (base2, oneUp1) as new base
				base1 = oneUp1;
				h1ups++;
				oneUp1 = ((base1 + hull1.length) - 1)
					% hull1.length;
			    }
			}
		    }
		    tp++;
		}
	    }
	    // build Tri component
	    Tri = new int[ntris][3];
	    int tr = 0;
	    for (int i = 0; i < tp; i++) {
		for (int j = 0; j < tris[i][0].length; j++) {
		    Tri[tr][0] = tris[i][0][j];
		    Tri[tr][1] = tris[i][1][j];
		    Tri[tr][2] = tris[i][2][j];
		    tr++;
		}
	    }
	    // build Vertices component
	    Vertices = new int[numpts][];
	    for (int i = 0; i < numpts; i++) {
		Vertices[i] = new int[nverts[i]];
		nverts[i] = 0;
	    }
	    int a, b, c;
	    for (int i = 0; i < ntris; i++) {
		a = Tri[i][0];
		b = Tri[i][1];
		c = Tri[i][2];
		Vertices[a][nverts[a]++] = i;
		Vertices[b][nverts[b]++] = i;
		Vertices[c][nverts[c]++] = i;
	    }
	    // call more generic method for constructing Walk and Edges arrays
	    finish_triang(samples);
	}
    }
}
