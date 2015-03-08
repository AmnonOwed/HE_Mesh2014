/*
 * 
 */
package wblut.hemesh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import wblut.geom.WB_Point;

/**
 * 
 */
public class HEC_Beethoven extends HEC_Creator {
    
    /* (non-Javadoc)
     * @see wblut.hemesh.HEC_Creator#createBase()
     */
    @Override
    protected HE_Mesh createBase() {
	final InputStream is = this.getClass().getClassLoader()
		.getResourceAsStream("resources/beethoven.obj");
	if (is == null) {
	    return new HE_Mesh();
	}
	final ArrayList<WB_Point> vertexList = new ArrayList<WB_Point>();
	final ArrayList<int[]> faceList = new ArrayList<int[]>();
	int faceCount = 0;
	// load OBJ file as an array of strings
	final String objStrings[] = loadStrings(is);
	for (int i = 0; i < objStrings.length; i++) {
	    // split every line in parts divided by spaces
	    final String[] parts = objStrings[i].split("\\s+");
	    // the first part indicates the kind of data that is in that line
	    // v stands for vertex data
	    if (parts[0].equals("v")) {
		final double x1 = Double.parseDouble(parts[1]);
		final double y1 = Double.parseDouble(parts[2]);
		final double z1 = Double.parseDouble(parts[3]);
		final WB_Point pointLoc = new WB_Point(x1, y1, z1);
		vertexList.add(pointLoc);
	    }
	    // f stands for facelist data
	    // should work for non triangular faces
	    if (parts[0].equals("f")) {
		final int[] tempFace = new int[parts.length - 1];
		for (int j = 0; j < (parts.length - 1); j++) {
		    final String[] num = parts[j + 1].split("/");
		    tempFace[j] = Integer.parseInt(num[0]) - 1;
		}
		faceList.add(tempFace);
		faceCount++;
	    }
	}
	// the HEC_FromFacelist wants the face data as int[][]
	final int[][] faceArray = new int[faceCount][];
	for (int i = 0; i < faceCount; i++) {
	    final int[] tempFace = faceList.get(i);
	    faceArray[i] = tempFace;
	}
	// et voila... add to the creator
	final HEC_FromFacelist creator = new HEC_FromFacelist();
	creator.setVertices(vertexList);
	creator.setFaces(faceArray);
	creator.setDuplicate(true);
	return new HE_Mesh(creator);
    }

    /**
     * 
     *
     * @param input 
     * @return 
     */
    private String[] loadStrings(final InputStream input) {
	try {
	    final BufferedReader reader = new BufferedReader(
		    new InputStreamReader(input, "UTF-8"));
	    return loadStrings(reader);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * 
     *
     * @param reader 
     * @return 
     */
    private String[] loadStrings(final BufferedReader reader) {
	try {
	    String lines[] = new String[100];
	    int lineCount = 0;
	    String line = null;
	    while ((line = reader.readLine()) != null) {
		if (lineCount == lines.length) {
		    final String temp[] = new String[lineCount << 1];
		    System.arraycopy(lines, 0, temp, 0, lineCount);
		    lines = temp;
		}
		lines[lineCount++] = line;
	    }
	    reader.close();
	    if (lineCount == lines.length) {
		return lines;
	    }
	    final String output[] = new String[lineCount];
	    System.arraycopy(lines, 0, output, 0, lineCount);
	    return output;
	} catch (final IOException e) {
	    e.printStackTrace();
	}
	return null;
    }
}
