import wblut.math.*;
import wblut.processing.*;
import wblut.core.*;
import wblut.hemesh.*;
import wblut.geom.*;

HE_Mesh mesh;
WB_Render render;

void setup() {
  size(800, 800, OPENGL);
  smooth(8);
  HEC_Cylinder creator=new HEC_Cylinder();
  creator.setRadius(100,100); // upper and lower radius. If one is 0, HEC_Cone is called. 
  creator.setHeight(400);
  creator.setFacets(32).setSteps(24);
  creator.setCap(false,false);// cap top, cap bottom?
  //Default axis of the cylinder is (0,1,0). To change this use the HEC_Creator method setZAxis(..).
  creator.setZAxis(0,0,1);
  mesh=new HE_Mesh(creator);
  mesh.setFaceColor(color(255));
 //mesh.modify(new HEM_Shell().setThickness(10)); 
 //mesh=new HE_Mesh(new HEC_Dual(mesh));
 mesh.modify(new HEM_Noise().setDistance(2));
 HEM_Extrude ext=new HEM_Extrude().setDistance(10).setChamfer(0.6);
 mesh.modify(ext);
 HE_Selection sel=ext.extruded;
 ext=new HEM_Extrude().setDistance(10).setChamfer(0.4);
  mesh.modify(new HEM_Noise().setDistance(2));
 mesh.modifySelected(ext,sel);
 sel=ext.extruded;
 ext=new HEM_Extrude().setDistance(20).setChamfer(0.4);
  mesh.modify(new HEM_Noise().setDistance(2));
 mesh.modifySelected(ext,sel);
 sel=ext.extruded;
 ext=new HEM_Extrude().setDistance(2).setChamfer(-5.0);
  mesh.modify(new HEM_Noise().setDistance(2));
 mesh.modifySelected(ext,sel);
 sel=ext.extruded;
 ext=new HEM_Extrude().setDistance(7).setChamfer(0);
  mesh.modify(new HEM_Noise().setDistance(2));
 mesh.modifySelected(ext,sel);
 sel=ext.extruded;
 ext=new HEM_Extrude().setDistance(2).setChamfer(0.6);
 mesh.modifySelected(ext,sel);
 //mesh.modify(new HEM_Noise().setDistance(20));
 mesh.smooth();
  //mesh.modify(new HEM_Smooth().setIterations(10));
  HET_Diagnosis.validate(mesh);
  render=new WB_Render(this);
}



void draw() {
  background(255);
  directionalLight(255, 255, 255, 1, 1, -1);
  directionalLight(127, 127, 127, -1, -1, 1);
  translate(400, 400, 100);
  rotateY(mouseX*1.0f/width*TWO_PI);
  rotateX(mouseY*1.0f/height*TWO_PI);
  stroke(0);
  //render.drawEdges(mesh);
  noStroke();
  render.drawFacesFC(mesh);
}

