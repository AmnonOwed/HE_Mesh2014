package wblut.hemesh;

import processing.core.PApplet;

/**
 * HET_Selector is used for interactive selection of mesh elements.
 * 
 * Tweaked version of original code by
 * 
 * @author nicolas.clavaud <antiplastik@gmail.com>
 * 
 */
public class HET_Selector {

	/** calling applet. */
	protected PApplet home;

	/** Selection buffer. */
	private final HET_SelectionBuffer buffer;

	/** Last key returned. */
	private Long _lastKey;

	/**
	 * Instantiates a new HET_Selector.
	 * 
	 * @param home
	 *            calling applet, typically "this"
	 */
	public HET_Selector(final PApplet home) {
		this.home = home;
		buffer = (HET_SelectionBuffer) home.createGraphics(home.width,
				home.height, "wblut.hemesh.tools.HET_SelectionBuffer");
		buffer.callCheckSettings();
		if (home.recorder == null) {
			home.recorder = buffer;
		}
		buffer.background(0);
		home.registerPre(this);
		home.registerDraw(this);
		_lastKey = -1L;
	}

	/**
	 * Pre.
	 */
	public void pre() {
		buffer.beginDraw();
		if (home.recorder == null) {
			home.recorder = buffer;
		}
	}

	/**
	 * Draw.
	 */
	public void draw() {
		buffer.endDraw();
	}

	/**
	 * Clear recording buffer.
	 */
	public void clear() {
		buffer.clear();
	}

	/**
	 * Start recording.
	 * 
	 * @param key
	 *            the key
	 */
	public void start(final Long key) {
		if (key < 0 || key > 16777214) {
			PApplet.println("[HE_Selector error] start(): ID out of range");
			return;
		}
		if (home.recorder == null) {
			home.recorder = buffer;
		}
		buffer.setKey(key);
	}

	/**
	 * Stop recording.
	 */
	public void stop() {
		home.recorder = null;
	}

	/**
	 * Resume recording.
	 */
	public void resume() {
		if (home.recorder == null) {
			home.recorder = buffer;
		}
	}

	/**
	 * Reads the ID of the object at point (x, y) -1 means there is no object at
	 * this point.
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @return Object ID
	 */
	public Long get(final int x, final int y) {
		_lastKey = buffer.getKey(x, y);
		return _lastKey;
	}

	/**
	 * Last key.
	 * 
	 * @return key of the last object selected
	 */
	public Long lastKey() {
		return _lastKey;
	}

	/**
	 * Buffer size.
	 * 
	 * @return the int
	 */
	public int bufferSize() {
		return buffer.colorToObject.size();
	}

}
