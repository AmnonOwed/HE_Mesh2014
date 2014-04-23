package wblut.external.ProGAL;

/**
 * Part of ProGAL: http://www.diku.dk/~rfonseca/ProGAL/
 * 
 * Original copyright notice:
 * 
 * Copyright (c) 2013, Dept. of Computer Science - Univ. of Copenhagen. All
 * rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * An interface for 3d volumes such as spheres and boxes. Implementing classes
 * should consider a volume as all points within and on the boundary of the
 * volume. This implies, for instance, that two spheres overlap when the
 * distance between their center-points is <b>equal to or less</b> than their
 * combined radii.
 * 
 * If only the shell should be considered use the VolumeShell3d interface.
 */
public interface Volume extends Shape {

	/**
	 * Determine if this volume overlaps vol. Two volumes overlap if their
	 * surfaces touch or if the union of interiors is non-empty.
	 */
	public boolean overlaps(Volume vol);

	/** Get the volume. */
	public double getVolume();

	/** Make a deep clone this volume. */
	public Volume clone();
}
