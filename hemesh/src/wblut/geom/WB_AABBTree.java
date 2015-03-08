/*
 * 
 */
package wblut.geom;

import java.util.List;
import javolution.util.FastTable;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;

/**
 * 
 */
public class WB_AABBTree {
    
    /**
     * 
     */
    private WB_AABBNode root;
    
    /**
     * 
     */
    private final int maxLevel;
    
    /**
     * 
     */
    private final int maxNumberOfFaces;

    /**
     * 
     *
     * @param mesh 
     * @param mnof 
     */
    public WB_AABBTree(final HE_Mesh mesh, final int mnof) {
	maxLevel = 2 * (int) Math.ceil(Math.log(mesh.getNumberOfFaces())
		/ Math.log(3.0));
	maxNumberOfFaces = Math.max(1, mnof);
	buildTree(mesh);
    }

    /**
     * 
     *
     * @param mesh 
     */
    private void buildTree(final HE_Mesh mesh) {
	root = new WB_AABBNode();
	final HE_Selection faces = new HE_Selection(mesh);
	faces.addFaces(mesh.getFacesAsList());
	buildNode(root, faces, mesh, 0);
    }

    /**
     * 
     *
     * @param node 
     * @param faces 
     * @param mesh 
     * @param level 
     */
    private void buildNode(final WB_AABBNode node, final HE_Selection faces,
	    final HE_Mesh mesh, final int level) {
	node.level = level;
	faces.collectVertices();
	node.aabb = faces.getAABB();
	if ((level == maxLevel)
		|| (faces.getNumberOfFaces() <= maxNumberOfFaces)) {
	    node.faces.addAll(faces.getFacesAsList());
	    node.isLeaf = true;
	} else {
	    final HE_Selection pos = new HE_Selection(mesh);
	    final HE_Selection neg = new HE_Selection(mesh);
	    final HE_Selection mid = new HE_Selection(mesh);
	    final WB_Vector dir = new WB_Vector();
	    if ((level % 3) == 0) {
		dir.set(0, 0, 1);
	    } else if ((level % 3) == 1) {
		dir.set(0, 1, 0);
	    } else {
		dir.set(1, 0, 0);
	    }
	    node.separator = new WB_Plane(new WB_Point(node.aabb.getCenter()),
		    dir);
	    for (final HE_Face face : faces.getFacesAsList()) {
		final WB_ClassificationGeometry cptp = WB_Classify
			.classifyPolygonToPlane3D(face.toPolygon(),
				node.separator);
		if (cptp == WB_ClassificationGeometry.CROSSING) {
		    mid.add(face);
		} else if (cptp == WB_ClassificationGeometry.BACK) {
		    neg.add(face);
		} else {
		    pos.add(face);
		}
	    }
	    node.isLeaf = true;
	    if (mid.getNumberOfFaces() > 0) {
		node.mid = new WB_AABBNode();
		buildNode(node.mid, mid, mesh, level + 1);
		node.isLeaf = false;
	    }
	    if (neg.getNumberOfFaces() > 0) {
		node.negative = new WB_AABBNode();
		buildNode(node.negative, neg, mesh, level + 1);
		node.isLeaf = false;
	    }
	    if (pos.getNumberOfFaces() > 0) {
		node.positive = new WB_AABBNode();
		buildNode(node.positive, pos, mesh, level + 1);
		node.isLeaf = false;
	    }
	}
    }

    /**
     * 
     *
     * @return 
     */
    public WB_AABBNode getRoot() {
	return root;
    }

    /**
     * 
     */
    public class WB_AABBNode {
	
	/**
	 * 
	 */
	protected int level;
	
	/**
	 * 
	 */
	protected WB_AABB aabb;
	
	/**
	 * 
	 */
	protected WB_AABBNode positive;
	
	/**
	 * 
	 */
	protected WB_AABBNode negative;
	
	/**
	 * 
	 */
	protected WB_AABBNode mid;
	
	/**
	 * 
	 */
	protected WB_Plane separator;
	
	/**
	 * 
	 */
	protected List<HE_Face> faces;
	
	/**
	 * 
	 */
	protected boolean isLeaf;

	/**
	 * 
	 */
	public WB_AABBNode() {
	    level = -1;
	    faces = new FastTable<HE_Face>();
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public WB_AABB getAABB() {
	    return aabb;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public WB_Plane getSeparator() {
	    return separator;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public int getLevel() {
	    return level;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public boolean isLeaf() {
	    return isLeaf;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public List<HE_Face> getFaces() {
	    return faces;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public WB_AABBNode getPosChild() {
	    return positive;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public WB_AABBNode getNegChild() {
	    return negative;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public WB_AABBNode getMidChild() {
	    return mid;
	}
    }
}
