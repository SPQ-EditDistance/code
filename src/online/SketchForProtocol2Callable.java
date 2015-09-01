package online;

import java.util.concurrent.Callable;

import sketches.SketchForProtocol2;

public class SketchForProtocol2Callable implements Callable<Object>{
	Object filename; 
	SketchForProtocol2 sketch;

	@Override
	public Object call() throws Exception {
		PrepareData.readFile((String)filename, sketch);
		sketch.finalizeSketch();
		return null;
	}
}
