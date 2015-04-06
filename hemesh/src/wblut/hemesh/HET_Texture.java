package wblut.hemesh;

import wblut.geom.WB_Vector;
import colorlib.Palette;

public class HET_Texture {
    /**
     * Set vertex colors according to the vertex normal normal.x: -1 to 1, red
     * component from 0 to 255 normal.y: -1 to 1, green component from 0 to 255
     * normal.z: -1 to 1, blue component from 0 to 255
     *
     * @param mesh
     */
    public static void setVertexColorFromVertexNormal(final HE_Mesh mesh) {
	final HE_VertexIterator vitr = mesh.vItr();
	HE_Vertex v;
	WB_Vector n;
	int color;
	while (vitr.hasNext()) {
	    v = vitr.next();
	    n = v.getVertexNormal();
	    color = color((int) (128 * (n.xd() + 1)),
		    (int) (128 * (n.yd() + 1)), (int) (128 * (n.zd() + 1)));
	    v.setColor(color);
	}
    }

    /**
     * Set vertex colors by vertex.getLabel() from a palette created with the
     * colorLib library
     *
     * @param mesh
     * @param palette
     */
    public static void setVertexColorFromPalette(final HE_Mesh mesh,
	    final Palette palette) {
	final HE_VertexIterator vitr = mesh.vItr();
	HE_Vertex v;
	final int size = palette.numSwatches();
	int color;
	while (vitr.hasNext()) {
	    v = vitr.next();
	    final int choice = (Math.max(0, Math.min(size - 1, v.getLabel())));
	    color = palette.getColor(choice);
	    v.setColor(color);
	}
    }

    /**
     * Set vertex colors randomly chosen from a palette created with the
     * colorLib library
     *
     * @param mesh
     * @param palette
     */
    public static void setRandomVertexColorFromPalette(final HE_Mesh mesh,
	    final Palette palette) {
	final HE_VertexIterator vitr = mesh.vItr();
	HE_Vertex v;
	final int size = palette.numSwatches();
	int color;
	while (vitr.hasNext()) {
	    v = vitr.next();
	    final int choice = (int) (Math.min(size - 1, Math.random() * size));
	    ;
	    color = palette.getColor(choice);
	    v.setColor(color);
	}
    }

    /**
     * Set vertex colors according to the umbrella angle. The color scale used
     * is a perceptually linearized rainbow scale. Angle: 0 (infinite outward or
     * inward spike) to 2 Pi (flat).
     *
     * @param mesh
     * @param minrange
     * @param maxrange
     */
    public static void setVertexColorFromVertexUmbrella(final HE_Mesh mesh,
	    final double minrange, final double maxrange) {
	final HE_VertexIterator vitr = mesh.vItr();
	HE_Vertex v;
	int color;
	final double idenom = 128 / Math.PI;
	while (vitr.hasNext()) {
	    v = vitr.next();
	    color = (int) (idenom * (v.getUmbrellaAngle() - minrange) / (maxrange - minrange));
	    color = Math.max(0, Math.min(color, 255));
	    v.setColor(rainbow[255 - color]);
	}
    }

    /**
     * Set vertex colors according to the Gaussian curvature. The color scale
     * used is a perceptually linearized heat map.
     *
     * @param mesh
     * @param minrange
     * @param maxrange
     */
    public static void setVertexColorFromVertexCurvature(final HE_Mesh mesh,
	    final double minrange, final double maxrange) {
	final HE_VertexIterator vitr = mesh.vItr();
	HE_Vertex v;
	int color;
	final double idenom = 128 / Math.PI;
	while (vitr.hasNext()) {
	    v = vitr.next();
	    color = (int) (idenom * (v.getGaussianCurvature() - minrange) / (maxrange - minrange));
	    color = Math.max(0, Math.min(color, 255));
	    v.setColor(heat[color]);
	}
    }

    /**
     * Set face colors according to the face normal normal.x: -1 to 1, red
     * component from 0 to 255 normal.y: -1 to 1, green component from 0 to 255
     * normal.z: -1 to 1, blue component from 0 to 255
     *
     * @param mesh
     */
    public static void setFaceColorFromFaceNormal(final HE_Mesh mesh) {
	final HE_FaceIterator fitr = mesh.fItr();
	HE_Face f;
	WB_Vector n;
	int color;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    n = f.getFaceNormal();
	    color = color((int) (128 * (n.xd() + 1)),
		    (int) (128 * (n.yd() + 1)), (int) (128 * (n.zd() + 1)));
	    f.setColor(color);
	}
    }

    /**
     * Set face colors randomly chosen from a palette created with the colorLib
     * library
     *
     * @param mesh
     * @param palette
     */
    public static void setRandomFaceColorFromPalette(final HE_Mesh mesh,
	    final Palette palette) {
	final HE_FaceIterator fitr = mesh.fItr();
	HE_Face f;
	final int size = palette.numSwatches();
	int color;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    final int choice = (int) (Math.min(size - 1, Math.random() * size));
	    color = palette.getColor(choice);
	    f.setColor(color);
	}
    }

    /**
     * Set face colors by face.getLabel() from a palette created with the
     * colorLib library
     *
     * @param mesh
     * @param palette
     */
    public static void setFaceColorFromPalette(final HE_Mesh mesh,
	    final Palette palette) {
	final HE_FaceIterator fitr = mesh.fItr();
	HE_Face f;
	final int size = palette.numSwatches();
	int color;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    final int choice = (Math.max(0, Math.min(size - 1, f.getLabel())));
	    color = palette.getColor(choice);
	    f.setColor(color);
	}
    }

    private static int color(int v1, int v2, int v3) {
	if (v1 > 255) {
	    v1 = 255;
	} else if (v1 < 0) {
	    v1 = 0;
	}
	if (v2 > 255) {
	    v2 = 255;
	} else if (v2 < 0) {
	    v2 = 0;
	}
	if (v3 > 255) {
	    v3 = 255;
	} else if (v3 < 0) {
	    v3 = 0;
	}
	return 0xff000000 | (v1 << 16) | (v2 << 8) | v3;
    }

    // http://www.cs.uml.edu/~haim/ColorCenter/ColorCenter.htm
    // ==============================================================================
    // HeatedObject ColorRGB SCALE Class
    //
    // Steve Pizer, UNC Chapel Hill (perceptually linearized)
    //
    // AGG - Alexander Gee
    //
    // 041497 - created
    // ==============================================================================
    static public int[] heat = new int[] { color(0, 0, 0), color(35, 0, 0),
	    color(52, 0, 0), color(60, 0, 0), color(63, 1, 0), color(64, 2, 0),
	    color(68, 5, 0), color(69, 6, 0), color(72, 8, 0),
	    color(74, 10, 0), color(77, 12, 0), color(78, 14, 0),
	    color(81, 16, 0), color(83, 17, 0), color(85, 19, 0),
	    color(86, 20, 0), color(89, 22, 0), color(91, 24, 0),
	    color(92, 25, 0), color(94, 26, 0), color(95, 28, 0),
	    color(98, 30, 0), color(100, 31, 0), color(102, 33, 0),
	    color(103, 34, 0), color(105, 35, 0), color(106, 36, 0),
	    color(108, 38, 0), color(109, 39, 0), color(111, 40, 0),
	    color(112, 42, 0), color(114, 43, 0), color(115, 44, 0),
	    color(117, 45, 0), color(119, 47, 0), color(119, 47, 0),
	    color(120, 48, 0), color(122, 49, 0), color(123, 51, 0),
	    color(125, 52, 0), color(125, 52, 0), color(126, 53, 0),
	    color(128, 54, 0), color(129, 56, 0), color(129, 56, 0),
	    color(131, 57, 0), color(132, 58, 0), color(134, 59, 0),
	    color(134, 59, 0), color(136, 61, 0), color(137, 62, 0),
	    color(137, 62, 0), color(139, 63, 0), color(139, 63, 0),
	    color(140, 65, 0), color(142, 66, 0), color(142, 66, 0),
	    color(143, 67, 0), color(143, 67, 0), color(145, 68, 0),
	    color(145, 68, 0), color(146, 70, 0), color(146, 70, 0),
	    color(148, 71, 0), color(148, 71, 0), color(149, 72, 0),
	    color(149, 72, 0), color(151, 73, 0), color(151, 73, 0),
	    color(153, 75, 0), color(153, 75, 0), color(154, 76, 0),
	    color(154, 76, 0), color(154, 76, 0), color(156, 77, 0),
	    color(156, 77, 0), color(157, 79, 0), color(157, 79, 0),
	    color(159, 80, 0), color(159, 80, 0), color(159, 80, 0),
	    color(160, 81, 0), color(160, 81, 0), color(162, 82, 0),
	    color(162, 82, 0), color(163, 84, 0), color(163, 84, 0),
	    color(165, 85, 0), color(165, 85, 0), color(166, 86, 0),
	    color(166, 86, 0), color(166, 86, 0), color(168, 87, 0),
	    color(168, 87, 0), color(170, 89, 0), color(170, 89, 0),
	    color(171, 90, 0), color(171, 90, 0), color(173, 91, 0),
	    color(173, 91, 0), color(174, 93, 0), color(174, 93, 0),
	    color(176, 94, 0), color(176, 94, 0), color(177, 95, 0),
	    color(177, 95, 0), color(179, 96, 0), color(179, 96, 0),
	    color(180, 98, 0), color(182, 99, 0), color(182, 99, 0),
	    color(183, 100, 0), color(183, 100, 0), color(185, 102, 0),
	    color(185, 102, 0), color(187, 103, 0), color(187, 103, 0),
	    color(188, 104, 0), color(188, 104, 0), color(190, 105, 0),
	    color(191, 107, 0), color(191, 107, 0), color(193, 108, 0),
	    color(193, 108, 0), color(194, 109, 0), color(196, 110, 0),
	    color(196, 110, 0), color(197, 112, 0), color(197, 112, 0),
	    color(199, 113, 0), color(200, 114, 0), color(200, 114, 0),
	    color(202, 116, 0), color(202, 116, 0), color(204, 117, 0),
	    color(205, 118, 0), color(205, 118, 0), color(207, 119, 0),
	    color(208, 121, 0), color(208, 121, 0), color(210, 122, 0),
	    color(211, 123, 0), color(211, 123, 0), color(213, 124, 0),
	    color(214, 126, 0), color(214, 126, 0), color(216, 127, 0),
	    color(217, 128, 0), color(217, 128, 0), color(219, 130, 0),
	    color(221, 131, 0), color(221, 131, 0), color(222, 132, 0),
	    color(224, 133, 0), color(224, 133, 0), color(225, 135, 0),
	    color(227, 136, 0), color(227, 136, 0), color(228, 137, 0),
	    color(230, 138, 0), color(230, 138, 0), color(231, 140, 0),
	    color(233, 141, 0), color(233, 141, 0), color(234, 142, 0),
	    color(236, 144, 0), color(236, 144, 0), color(238, 145, 0),
	    color(239, 146, 0), color(241, 147, 0), color(241, 147, 0),
	    color(242, 149, 0), color(244, 150, 0), color(244, 150, 0),
	    color(245, 151, 0), color(247, 153, 0), color(247, 153, 0),
	    color(248, 154, 0), color(250, 155, 0), color(251, 156, 0),
	    color(251, 156, 0), color(253, 158, 0), color(255, 159, 0),
	    color(255, 159, 0), color(255, 160, 0), color(255, 161, 0),
	    color(255, 163, 0), color(255, 163, 0), color(255, 164, 0),
	    color(255, 165, 0), color(255, 167, 0), color(255, 167, 0),
	    color(255, 168, 0), color(255, 169, 0), color(255, 169, 0),
	    color(255, 170, 0), color(255, 172, 0), color(255, 173, 0),
	    color(255, 173, 0), color(255, 174, 0), color(255, 175, 0),
	    color(255, 177, 0), color(255, 178, 0), color(255, 179, 0),
	    color(255, 181, 0), color(255, 181, 0), color(255, 182, 0),
	    color(255, 183, 0), color(255, 184, 0), color(255, 187, 7),
	    color(255, 188, 10), color(255, 189, 14), color(255, 191, 18),
	    color(255, 192, 21), color(255, 193, 25), color(255, 195, 29),
	    color(255, 197, 36), color(255, 198, 40), color(255, 200, 43),
	    color(255, 202, 51), color(255, 204, 54), color(255, 206, 61),
	    color(255, 207, 65), color(255, 210, 72), color(255, 211, 76),
	    color(255, 214, 83), color(255, 216, 91), color(255, 219, 98),
	    color(255, 221, 105), color(255, 223, 109), color(255, 225, 116),
	    color(255, 228, 123), color(255, 232, 134), color(255, 234, 142),
	    color(255, 237, 149), color(255, 239, 156), color(255, 240, 160),
	    color(255, 243, 167), color(255, 246, 174), color(255, 248, 182),
	    color(255, 249, 185), color(255, 252, 193), color(255, 253, 196),
	    color(255, 255, 204), color(255, 255, 207), color(255, 255, 211),
	    color(255, 255, 218), color(255, 255, 222), color(255, 255, 225),
	    color(255, 255, 229), color(255, 255, 233), color(255, 255, 236),
	    color(255, 255, 240), color(255, 255, 244), color(255, 255, 247),
	    color(255, 255, 255) };
    // ==============================================================================
    // Rainbow ColorRGB SCALE Class
    //
    // Steve Pizer, UNC Chapel Hill (perceptually linearized)
    //
    // AGG - Alexander Gee
    //
    // 041497 - created
    // ==============================================================================
    static public int[] rainbow = new int[] { color(0, 0, 0), color(45, 0, 36),
	    color(56, 0, 46), color(60, 0, 49), color(67, 0, 54),
	    color(70, 0, 59), color(71, 0, 61), color(75, 0, 68),
	    color(74, 0, 73), color(74, 0, 77), color(73, 0, 81),
	    color(71, 0, 87), color(69, 1, 90), color(68, 2, 94),
	    color(66, 3, 97), color(63, 6, 102), color(61, 7, 106),
	    color(58, 10, 109), color(56, 12, 113), color(53, 15, 116),
	    color(48, 18, 119), color(47, 20, 121), color(44, 23, 124),
	    color(41, 27, 128), color(40, 28, 129), color(37, 32, 132),
	    color(34, 36, 134), color(29, 43, 137), color(25, 52, 138),
	    color(24, 57, 139), color(24, 62, 141), color(24, 64, 142),
	    color(23, 65, 142), color(23, 69, 143), color(23, 71, 142),
	    color(23, 71, 142), color(23, 73, 142), color(23, 75, 142),
	    color(23, 75, 142), color(23, 78, 142), color(23, 80, 142),
	    color(23, 80, 142), color(23, 82, 141), color(23, 85, 141),
	    color(23, 85, 141), color(23, 87, 140), color(23, 87, 140),
	    color(24, 90, 140), color(24, 90, 140), color(24, 93, 139),
	    color(24, 93, 139), color(24, 93, 139), color(24, 93, 139),
	    color(24, 97, 139), color(24, 97, 139), color(25, 101, 138),
	    color(25, 101, 138), color(25, 104, 137), color(25, 104, 137),
	    color(25, 104, 137), color(26, 108, 137), color(26, 108, 137),
	    color(27, 111, 136), color(27, 111, 136), color(27, 111, 136),
	    color(27, 115, 135), color(27, 115, 135), color(28, 118, 134),
	    color(28, 118, 134), color(29, 122, 133), color(29, 122, 133),
	    color(29, 122, 133), color(29, 122, 133), color(29, 125, 132),
	    color(29, 125, 132), color(30, 128, 131), color(30, 128, 131),
	    color(31, 131, 130), color(31, 131, 130), color(31, 131, 130),
	    color(32, 134, 128), color(32, 134, 128), color(33, 137, 127),
	    color(33, 137, 127), color(33, 137, 127), color(34, 140, 125),
	    color(34, 140, 125), color(35, 142, 123), color(35, 142, 123),
	    color(36, 145, 121), color(36, 145, 121), color(36, 145, 121),
	    color(37, 147, 118), color(37, 147, 118), color(38, 150, 116),
	    color(38, 150, 116), color(40, 152, 113), color(40, 152, 113),
	    color(41, 154, 111), color(41, 154, 111), color(42, 156, 108),
	    color(42, 156, 108), color(43, 158, 106), color(43, 158, 106),
	    color(43, 158, 106), color(45, 160, 104), color(45, 160, 104),
	    color(46, 162, 101), color(46, 162, 101), color(48, 164, 99),
	    color(48, 164, 99), color(50, 166, 97), color(50, 166, 97),
	    color(51, 168, 95), color(53, 170, 93), color(53, 170, 93),
	    color(53, 170, 93), color(55, 172, 91), color(55, 172, 91),
	    color(57, 174, 88), color(57, 174, 88), color(59, 175, 86),
	    color(62, 177, 84), color(64, 178, 82), color(64, 178, 82),
	    color(67, 180, 80), color(67, 180, 80), color(69, 181, 79),
	    color(72, 183, 77), color(72, 183, 77), color(72, 183, 77),
	    color(75, 184, 76), color(77, 186, 74), color(80, 187, 73),
	    color(83, 189, 72), color(87, 190, 72), color(91, 191, 71),
	    color(95, 192, 70), color(99, 193, 70), color(103, 194, 70),
	    color(107, 195, 70), color(111, 196, 70), color(111, 196, 70),
	    color(115, 196, 70), color(119, 197, 70), color(123, 197, 70),
	    color(130, 198, 71), color(133, 199, 71), color(137, 199, 72),
	    color(140, 199, 72), color(143, 199, 73), color(143, 199, 73),
	    color(147, 199, 73), color(150, 199, 74), color(153, 199, 74),
	    color(156, 199, 75), color(160, 200, 76), color(167, 200, 78),
	    color(170, 200, 79), color(173, 200, 79), color(173, 200, 79),
	    color(177, 200, 80), color(180, 200, 81), color(183, 199, 82),
	    color(186, 199, 82), color(190, 199, 83), color(196, 199, 85),
	    color(199, 198, 85), color(199, 198, 85), color(203, 198, 86),
	    color(206, 197, 87), color(212, 197, 89), color(215, 196, 90),
	    color(218, 195, 91), color(224, 194, 94), color(224, 194, 94),
	    color(230, 193, 96), color(233, 192, 98), color(236, 190, 100),
	    color(238, 189, 104), color(240, 188, 106), color(240, 188, 106),
	    color(242, 187, 110), color(244, 185, 114), color(245, 184, 116),
	    color(247, 183, 120), color(248, 182, 123), color(248, 182, 123),
	    color(250, 181, 125), color(251, 180, 128), color(252, 180, 130),
	    color(253, 180, 133), color(253, 180, 133), color(254, 180, 134),
	    color(254, 179, 138), color(255, 179, 142), color(255, 179, 145),
	    color(255, 179, 145), color(255, 179, 152), color(255, 180, 161),
	    color(255, 180, 164), color(255, 180, 167), color(255, 180, 167),
	    color(255, 181, 169), color(255, 181, 170), color(255, 182, 173),
	    color(255, 183, 176), color(255, 183, 176), color(255, 184, 179),
	    color(255, 185, 179), color(255, 185, 182), color(255, 186, 182),
	    color(255, 186, 182), color(255, 187, 185), color(255, 188, 185),
	    color(255, 189, 188), color(255, 189, 188), color(255, 190, 188),
	    color(255, 191, 191), color(255, 192, 191), color(255, 194, 194),
	    color(255, 194, 194), color(255, 197, 197), color(255, 198, 198),
	    color(255, 200, 200), color(255, 201, 201), color(255, 201, 201),
	    color(255, 202, 202), color(255, 203, 203), color(255, 205, 205),
	    color(255, 206, 206), color(255, 206, 206), color(255, 208, 208),
	    color(255, 209, 209), color(255, 211, 211), color(255, 215, 215),
	    color(255, 216, 216), color(255, 216, 216), color(255, 218, 218),
	    color(255, 219, 219), color(255, 221, 221), color(255, 223, 223),
	    color(255, 226, 226), color(255, 228, 228), color(255, 230, 230),
	    color(255, 230, 230), color(255, 232, 232), color(255, 235, 235),
	    color(255, 237, 237), color(255, 240, 240), color(255, 243, 243),
	    color(255, 246, 246), color(255, 249, 249), color(255, 251, 251),
	    color(255, 253, 253), color(255, 255, 255) };
    // ==============================================================================
    // LOCS ColorRGB SCALE Class
    //
    // Dr. Haim Levkowitz, UMass Lowell (perceptually linearized)
    //
    // AGG - Alexander Gee
    //
    // 041497 - created
    // ==============================================================================
    static public int[] optimal = new int[] { color(0, 0, 0), color(0, 0, 0),
	    color(0, 0, 0), color(1, 0, 0), color(2, 0, 0), color(2, 0, 0),
	    color(3, 0, 0), color(3, 0, 0), color(4, 0, 0), color(5, 0, 0),
	    color(5, 0, 0), color(6, 0, 0), color(7, 0, 0), color(7, 0, 0),
	    color(8, 0, 0), color(9, 0, 0), color(9, 0, 0), color(10, 0, 0),
	    color(11, 0, 0), color(12, 0, 0), color(13, 0, 0), color(14, 0, 0),
	    color(15, 0, 0), color(16, 0, 0), color(17, 0, 0), color(18, 0, 0),
	    color(19, 0, 0), color(20, 0, 0), color(21, 0, 0), color(22, 0, 0),
	    color(23, 0, 0), color(25, 0, 0), color(26, 0, 0), color(27, 0, 0),
	    color(28, 0, 0), color(30, 0, 0), color(31, 0, 0), color(33, 0, 0),
	    color(34, 0, 0), color(35, 0, 0), color(37, 0, 0), color(39, 0, 0),
	    color(40, 0, 0), color(43, 0, 0), color(45, 0, 0), color(46, 0, 0),
	    color(49, 0, 0), color(51, 0, 0), color(53, 0, 0), color(54, 0, 0),
	    color(56, 0, 0), color(58, 0, 0), color(60, 0, 0), color(62, 0, 0),
	    color(64, 0, 0), color(67, 0, 0), color(69, 0, 0), color(71, 0, 0),
	    color(74, 0, 0), color(76, 0, 0), color(80, 0, 0), color(81, 0, 0),
	    color(84, 0, 0), color(86, 0, 0), color(89, 0, 0), color(92, 0, 0),
	    color(94, 0, 0), color(97, 0, 0), color(100, 0, 0),
	    color(103, 0, 0), color(106, 0, 0), color(109, 0, 0),
	    color(112, 0, 0), color(115, 0, 0), color(117, 0, 0),
	    color(122, 0, 0), color(126, 0, 0), color(128, 0, 0),
	    color(131, 0, 0), color(135, 0, 0), color(135, 0, 0),
	    color(135, 1, 0), color(135, 2, 0), color(135, 3, 0),
	    color(135, 4, 0), color(135, 6, 0), color(135, 6, 0),
	    color(135, 8, 0), color(135, 9, 0), color(135, 10, 0),
	    color(135, 11, 0), color(135, 13, 0), color(135, 13, 0),
	    color(135, 15, 0), color(135, 17, 0), color(135, 17, 0),
	    color(135, 19, 0), color(135, 21, 0), color(135, 22, 0),
	    color(135, 23, 0), color(135, 25, 0), color(135, 26, 0),
	    color(135, 27, 0), color(135, 29, 0), color(135, 31, 0),
	    color(135, 32, 0), color(135, 33, 0), color(135, 35, 0),
	    color(135, 36, 0), color(135, 38, 0), color(135, 40, 0),
	    color(135, 42, 0), color(135, 44, 0), color(135, 46, 0),
	    color(135, 47, 0), color(135, 49, 0), color(135, 51, 0),
	    color(135, 52, 0), color(135, 54, 0), color(135, 56, 0),
	    color(135, 57, 0), color(135, 59, 0), color(135, 62, 0),
	    color(135, 63, 0), color(135, 65, 0), color(135, 67, 0),
	    color(135, 69, 0), color(135, 72, 0), color(135, 73, 0),
	    color(135, 76, 0), color(135, 78, 0), color(135, 80, 0),
	    color(135, 82, 0), color(135, 84, 0), color(135, 87, 0),
	    color(135, 88, 0), color(135, 90, 0), color(135, 93, 0),
	    color(135, 95, 0), color(135, 98, 0), color(135, 101, 0),
	    color(135, 103, 0), color(135, 106, 0), color(135, 107, 0),
	    color(135, 110, 0), color(135, 113, 0), color(135, 115, 0),
	    color(135, 118, 0), color(135, 121, 0), color(135, 124, 0),
	    color(135, 127, 0), color(135, 129, 0), color(135, 133, 0),
	    color(135, 135, 0), color(135, 138, 0), color(135, 141, 0),
	    color(135, 144, 0), color(135, 148, 0), color(135, 150, 0),
	    color(135, 155, 0), color(135, 157, 0), color(135, 160, 0),
	    color(135, 163, 0), color(135, 166, 0), color(135, 170, 0),
	    color(135, 174, 0), color(135, 177, 0), color(135, 180, 0),
	    color(135, 184, 0), color(135, 188, 0), color(135, 192, 0),
	    color(135, 195, 0), color(135, 200, 0), color(135, 203, 0),
	    color(135, 205, 0), color(135, 210, 0), color(135, 214, 0),
	    color(135, 218, 0), color(135, 222, 0), color(135, 226, 0),
	    color(135, 231, 0), color(135, 236, 0), color(135, 239, 0),
	    color(135, 244, 0), color(135, 249, 0), color(135, 254, 0),
	    color(135, 255, 1), color(135, 255, 5), color(135, 255, 10),
	    color(135, 255, 15), color(135, 255, 20), color(135, 255, 23),
	    color(135, 255, 28), color(135, 255, 33), color(135, 255, 38),
	    color(135, 255, 43), color(135, 255, 45), color(135, 255, 49),
	    color(135, 255, 54), color(135, 255, 59), color(135, 255, 65),
	    color(135, 255, 70), color(135, 255, 74), color(135, 255, 80),
	    color(135, 255, 84), color(135, 255, 90), color(135, 255, 95),
	    color(135, 255, 98), color(135, 255, 104), color(135, 255, 110),
	    color(135, 255, 116), color(135, 255, 120), color(135, 255, 125),
	    color(135, 255, 131), color(135, 255, 137), color(135, 255, 144),
	    color(135, 255, 149), color(135, 255, 154), color(135, 255, 158),
	    color(135, 255, 165), color(135, 255, 172), color(135, 255, 179),
	    color(135, 255, 186), color(135, 255, 191), color(135, 255, 198),
	    color(135, 255, 203), color(135, 255, 211), color(135, 255, 216),
	    color(135, 255, 224), color(135, 255, 232), color(135, 255, 240),
	    color(135, 255, 248), color(135, 255, 254), color(135, 255, 255),
	    color(140, 255, 255), color(146, 255, 255), color(153, 255, 255),
	    color(156, 255, 255), color(161, 255, 255), color(168, 255, 255),
	    color(172, 255, 255), color(177, 255, 255), color(182, 255, 255),
	    color(189, 255, 255), color(192, 255, 255), color(199, 255, 255),
	    color(204, 255, 255), color(210, 255, 255), color(215, 255, 255),
	    color(220, 255, 255), color(225, 255, 255), color(232, 255, 255),
	    color(236, 255, 255), color(240, 255, 255), color(248, 255, 255),
	    color(255, 255, 255) };
}
