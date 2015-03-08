/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import processing.core.PApplet;

/**
 * HET_Recorder is used to record meshes drawn in Processing.
 */
public class HET_Recorder {
    /** Mesh buffer. */
    private HET_MeshBuffer meshBuffer;
    /** Calling applet. */
    private final PApplet home;
    /** Recorded meshes. */
    public ArrayList<HE_Mesh> meshes;
    /** Number of meshes. */
    public int numberOfMeshes;

    /**
     * Instantiates a new HET_Recorder.
     * 
     * @param home
     *            calling applet, typically "this"
     */
    public HET_Recorder(final PApplet home) {
	this.home = home;
    }

    /**
     * Start recorder.
     */
    public void start() {
	meshes = new ArrayList<HE_Mesh>();
	meshBuffer = (HET_MeshBuffer) home.createGraphics(home.width,
		home.height, "wblut.hemesh.tools.HET_MeshBuffer");
	meshBuffer.home = home;
	home.beginRecord(meshBuffer);
    }

    /**
     * Start next mesh.
     */
    public void nextMesh() {
	meshBuffer.nextMesh();
    }

    /**
     * Stop recorder.
     */
    public void stop() {
	meshBuffer.nextMesh();
	meshes = meshBuffer.meshes;
	home.endRecord();
	meshBuffer = null;
    }
}
