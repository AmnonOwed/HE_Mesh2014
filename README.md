# HE_Mesh2014
===========

HE_Mesh 2014, a Java library for creating and manipulating polygonal meshes. Aimed primarily at [Processing 2](http://processing.org/).

## Building HE_Mesh 2014 from source.

The first thing you need to do is download or fork this repository and import the code in Eclipse.
You need to add some other external *.jar files to the properties of your Eclipse project to compile HE_Mesh.

* `core.jar`: This is the core Processing API from Processing 2.1.1. Download the latest version of Processing here: http://processing.org/
* `javolution-6.1.0.jar`: Javolution is used to make HE_Mesh faster. You'll need version 6.1.0, download here: http://hemesh.wblut.com/javolution-6.1.0.zip (http://javolution.org/)
* `jts.jar`: The JTS Topology Suite is an API of spatial predicates and functions for processing planar geometry. You need version 1.13. Download here: http://hemesh.wblut.com/jts.zip (http://tsusiatsoftware.net/jts/main.html)
* `trove-3.1a1.jar`: A collection of high speed primitive based collections. Download here: http://hemesh.wblut.com/trove-3.1a1.zip
  (http://trove.starlight-systems.com/)
* `hemesh-external.jar`: HE_MESH 2014 contains source code from other authors that was converted to use my geometry classes for convenience. To protect the rights of the original authors whose work is not in the public domain, the source code is only available on request. The required JAR can be downloaded here: http://hemesh.wblut.com/hemesh-external.zip

## Build-of-the-day HE_Mesh 2014 (10/12/2014)

Download a recent built here: http://hemesh.wblut.com/hemesh.zip.

## License

HE_Mesh 2014, with the below exceptions, is dedicated to the public domain. 
To the extent possible under law, I, Frederik Vanhoutte, have waived all copyright and related or neighboring rights to HE_Mesh 2014. This work is published from België.
(http://creativecommons.org/publicdomain/zero/1.0/)

The following classes are subject to the license agreement of their original authors, included in the source file:

* wblut.geom.WB_Delaunay
* wblut.geom.WB_ShapeReader
* wblut.math.WB_MTRandom

The following packages are part of hemesh-external.jar and are subject to the license agreement of their original authors:

* wblut.external.ProGAL http://www.diku.dk/~rfonseca/ProGAL/
* wblut.external.straightskeleton https://code.google.com/p/campskeleton/
* wblut.external.QuickHull3D https://www.cs.ubc.ca/~lloyd/java/quickhull3d.html

The modified code is available on request.
