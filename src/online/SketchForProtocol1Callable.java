package online;

import java.util.concurrent.Callable;

import sketches.SketchForProtocol1;

public class SketchForProtocol1Callable implements Callable<Object>{
	Object filename; 
	SketchForProtocol1 sketch;

	@Override
	public Object call() throws Exception {
		PrepareData.readFile((String)filename, sketch);
		return null;
	}

}
