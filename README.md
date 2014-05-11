# HE_Mesh2014
===========

HE_Mesh 2014, a Java library for creating and manipulating polygonal meshes. Aimed primarily at [Processing 2](http://processing.org/).

## Building HE_Mesh 2014 from source.

The first thing you need to do is download or fork this repository and import the code in Eclipse.
You need to add some other external *.jar files to the properties of your Eclipse project to compile HE_Mesh.

* `core.jar`: This is the core Processing API from Processing 2.1.1. Download the latest version of Processing here: http://processing.org/
* `javolution.jar`: Javolution is used to make HE_Mesh faster. You'll need version 5.5.1, download here: http://download.java.net/maven/2/javolution/javolution/5.5.1/
* `jts.jar`: The JTS Topology Suite is an API of spatial predicates and functions for processing geometry. You need version 1.13. Download here: http://tsusiatsoftware.net/jts/main.html