/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;

/**
 * 
 */
public class HE_DynamicMesh extends HE_Mesh {
    
    /**
     * 
     */
    private final ArrayList<HE_Machine> modifierStack;
    
    /**
     * 
     */
    private HE_Mesh bkp;

    /**
     * 
     *
     * @param baseMesh 
     */
    public HE_DynamicMesh(final HE_Mesh baseMesh) {
	this.set(baseMesh);
	bkp = get();
	modifierStack = new ArrayList<HE_Machine>();
    }

    /**
     * 
     */
    public void update() {
	this.set(bkp);
	applyStack();
    }

    /**
     * 
     */
    private void applyStack() {
	for (int i = 0; i < modifierStack.size(); i++) {
	    modifierStack.get(i).apply(this);
	}
    }

    /**
     * 
     *
     * @param mod 
     */
    public void add(final HE_Machine mod) {
	modifierStack.add(mod);
    }

    /**
     * 
     *
     * @param mod 
     */
    public void remove(final HE_Machine mod) {
	modifierStack.remove(mod);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_MeshStructure#clear()
     */
    @Override
    public void clear() {
	modifierStack.clear();
	set(bkp);
    }

    /**
     * 
     *
     * @param baseMesh 
     * @return 
     */
    public HE_DynamicMesh setBaseMesh(final HE_Mesh baseMesh) {
	set(baseMesh);
	bkp = get();
	applyStack();
	return this;
    }
}
