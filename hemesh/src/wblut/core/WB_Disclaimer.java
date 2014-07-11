package wblut.core;

public class WB_Disclaimer {

	public static final WB_Disclaimer CURRENT_DISCLAIMER = new WB_Disclaimer();

	public String toString() {
		String dis = "Copyright (c) 2014 Frederik Vanhoutte (W:Blut)";
		dis += System.getProperty("line.separator");
		dis += System.getProperty("line.separator");
		dis += "Permission is hereby granted, free of charge, to any person obtaining a copy";
		dis += System.getProperty("line.separator");
		dis += "of this software and associated documentation files (the \"Software\"), to deal";
		dis += System.getProperty("line.separator");
		dis += "in the Software without restriction, including without limitation the rights";
		dis += System.getProperty("line.separator");
		dis += "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell";
		dis += System.getProperty("line.separator");
		dis += "copies of the Software, and to permit persons to whom the Software is";
		dis += System.getProperty("line.separator");
		dis += "furnished to do so, subject to the following conditions:";
		dis += System.getProperty("line.separator");
		dis += System.getProperty("line.separator");
		dis += "The above copyright notice and this permission notice shall be included in";
		dis += System.getProperty("line.separator");
		dis += "all copies or substantial portions of the Software.";
		dis += System.getProperty("line.separator");
		dis += System.getProperty("line.separator");
		dis += "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR";
		dis += System.getProperty("line.separator");
		dis += "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,";
		dis += System.getProperty("line.separator");
		dis += "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE";
		dis += System.getProperty("line.separator");
		dis += "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER";
		dis += System.getProperty("line.separator");
		dis += "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,";
		dis += System.getProperty("line.separator");
		dis += "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN";
		dis += System.getProperty("line.separator");
		dis += "THE SOFTWARE.";
		dis += System.getProperty("line.separator");
		dis += System.getProperty("line.separator");
		dis += "Cheers!";
		dis += System.getProperty("line.separator");
		dis += "Frederik";
		dis += System.getProperty("line.separator");
		return dis;
	}

	public static String disclaimer() {
		return CURRENT_DISCLAIMER.toString();
	}
}