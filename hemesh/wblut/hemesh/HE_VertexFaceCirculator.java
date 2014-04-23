package wblut.hemesh;

import java.util.Iterator;


public class HE_VertexFaceCirculator<V extends HE_Vertex> implements Iterator<HE_Face>{

	private HE_Halfedge _start;
	private HE_Halfedge _current;
	
	public HE_VertexFaceCirculator(HE_Vertex v){
		_start=v.getHalfedge();
		_current=null;
		
	}
	
	@Override
	public boolean hasNext() {
		
		return (_current==null)||(_current.getNextInVertex()!=_start);
	}

	@Override
	public HE_Face next() {
		if(_current==null){
			_current=_start;
		}else{
		_current=_current.getNextInVertex();
		}
		return _current.getFace();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
		
	}
	
}