import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

import processing.opengl.*;

HE_Mesh mesh;
WB_Render render;
PImage img;
PImage[] imgs;
void setup() {
  size(800, 800, OPENGL);
  smooth(8);
textureMode(NORMAL);
  float[][] values=new float[21][21];
  for (int j = 0; j < 21; j++) {
    for (int i = 0; i < 21; i++) {
      values[i][j]=200*noise(0.35*i, 0.35*j);
    }
  }

  HEC_Grid creator=new HEC_Grid();
  creator.setU(20);
  creator.setV(20);
  creator.setUSize(500);
  creator.setVSize(500);
  creator.setWValues(values);
  mesh=new HE_Mesh(creator);
 /*
HEC_Cylinder creator=new HEC_Cylinder();
  creator.setRadius(150,150); // upper and lower radius. If one is 0, HEC_Cone is called. 
  creator.setHeight(400);
  creator.setFacets(14).setSteps(1);
  creator.setCap(true,true);// cap top, cap bottom?
  //Default axis of the cylinder is (0,1,0). To change this use the HEC_Creator method setZAxis(..).
  creator.setZAxis(0,0,1);
  mesh=new HE_Mesh(creator); 
 

  mesh=new HE_Mesh(new HEC_Torus(80,200,6,12).setTwist(4)); 

 mesh=new HE_Mesh(new HEC_Sphere().setRadius(200).setUFacets(16).setVFacets(8));

 HEC_UVParametric creator=new  HEC_UVParametric();
  creator.setUVSteps(40, 40);
  creator.setRadius(100); //scaling factor
  creator.setUWrap(true); // needs to be set manually
  creator.setVWrap(true); // needs to be set manually
  creator.setEvaluator(new UVFunction());// expects an implementation of the WB_Function2D<WB_Point3d> interface, taking u and v from 0 to 1
mesh=new HE_Mesh(creator); 
  HEC_SuperDuper creator=new HEC_SuperDuper();
   creator.setU(64);
   creator.setV(8);
   creator.setUWrap(true); // needs to be set manually
   creator.setVWrap(false); // needs to be set manually
   creator.setRadius(50);
    // creator.setGeneralParameters(0, 10, 0, 0,6, 10, 6, 10, 3, 0, 0, 0, 4, 0.5, 0.25);
creator.setDonutParameters(0, 10, 10, 10, 5, 6, 12, 12,  3, 1);
  mesh=new HE_Mesh(creator); 
  */
  //mesh.splitEdges();
//mesh.splitFacesQuad();
//mesh.splitFacesHybrid();
//mesh.splitFacesHybrid();
//mesh.splitFacesTri();
//mesh.splitFacesTri();
//mesh.smooth();
  //mesh.splitFacesMidEdge();
  //mesh.splitFacesMidEdgeHole();
 //mesh.subdivide(new HES_Planar().setKeepTriangles(false));
 //mesh.modify(new HEM_Lattice().setWidth(20).setDepth(-18));
 //mesh.modify(new HEM_Crocodile().setDistance(50));
//mesh.modify(new HEM_Slice().setPlane(0,0,0,1,0,1));

//mesh.smooth(2);
  img=loadImage("texture.jpg");
  render=new WB_Render(this);
  imgs=new PImage[]{img,img,img};
  mesh.validate();
  
  
}

void draw() {
  background(120);
  lights();
  translate(400, 400, 0);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  noStroke();
  render.drawFaces(mesh,img);
  stroke(0);
  render.drawEdges(mesh);
 
  }

class UVFunction implements WB_Function2D<WB_Point> {
  WB_Point f(double u, double v) {
    double pi23=2*Math.PI/3;
    double ua=Math.PI*2*u;
    double va=Math.PI*2*v;
    double sqrt2=Math.sqrt(2.0d);
    double px = Math.sin(ua) / Math.abs(sqrt2+ Math.cos(va));
    double py = Math.sin(ua+pi23) / Math.abs(sqrt2 +Math.cos(va + pi23));
    double pz = Math.cos(ua-pi23) / Math.abs(sqrt2 +Math.cos(va - pi23));
    return new WB_Point(px, py, pz);
  }
}

