package online;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.cli.CommandLine;

import sketches.SketchForProtocol2;
import util.EvaRunnable;
import util.GenRunnable;
import util.Utils;
import circuits.BitonicSortLib;
import circuits.arithmetic.IntegerLib;
import flexsc.CompEnv;

public class Protocol2 {

	public static<T> T[] add(IntegerLib<T> lib , T[][] x) {
		if(x.length == 1) return x[0];
		else if(x.length == 2) return lib.addFull(x[0], x[1], false);
		T[][] res = null;
		if(x.length%2 == 1)
			res = lib.getEnv().newTArray(x.length/2+1, 0);
		else 
			res = lib.getEnv().newTArray(x.length/2, 0);
		for(int i = 0; i < x.length/2*2; i+=2) {
			res[i/2] = lib.addFull(x[i], x[i+1], false);
		}
		if(x.length%2 == 1) res[res.length-1] = lib.padSignal(x[x.length-1], res[0].length);
		return add(lib, res);
	}	

	public static<T> T[] compute(CompEnv<T> gen, T[][] aliceBF, T[][] bobBF) {
		IntegerLib<T> lib = new IntegerLib<>(gen);
		T[][] res = gen.newTArray(aliceBF.length, 0);
		for(int i = 0; i < aliceBF.length; ++i) {
			T[] tmp = lib.add(aliceBF[i], bobBF[i]);
			res[i] = lib.absolute(tmp);
		}
		return add(lib, res);
	}

	public static class Generator<T> extends GenRunnable<T> {
		T[][] aliceBF;
		T[][][] bobBF;
		ExecutorService executor;
		T[][] res;
		SketchForProtocol2[] sketch;
		int numberOfFile;
		ArrayList<String> list;
		double t1;
		
		public int SIZE, MEDIAN, bitL;
		@Override
		public void prepareInput(CompEnv<T> gen) throws Exception {
			SIZE = config.getInt("m");
			MEDIAN = config.getInt("l");
			
			t1 = System.currentTimeMillis()/1000.0;
			list = getListofFiles(config.getString("GenFile"));
			numberOfFile = list.size();
			sketch = new SketchForProtocol2[list.size()];
			sketch[0] = new SketchForProtocol2(SIZE, MEDIAN);
			for(int i = 1; i < list.size(); ++i) {
				sketch[i] = new SketchForProtocol2(SIZE, MEDIAN);
				sketch[i].sks = sketch[0].sks;
			}
			gen.channel.writeInt(numberOfFile);
			for(int i = 0; i < sketch[0].sks.length; ++i)
				for(int j = 0; j < sketch[0].sks[0].length; ++j)
				gen.channel.writeLong(sketch[0].sks[i][j]);
			gen.channel.flush();

	        executor = Executors.newFixedThreadPool(config.getInt("NumThreads"));
			ArrayList<Future<Object> > list2 = new ArrayList<Future<Object>>();
			for(int listindex = 0; listindex < list.size(); ++listindex) {
				SketchForProtocol2Callable scon = new SketchForProtocol2Callable();
				scon.filename = list.get(listindex);
				scon.sketch = sketch[listindex];
				Future<Object> future = executor.submit(scon);
				list2.add(future);
			}
			for(Future<Object> future: list2) {
				future.get();
			}

			System.out.println("Time to construct sketches: "+(System.currentTimeMillis()/1000.0-t1));
			t1 = System.currentTimeMillis()/1000.0;
			executor.shutdown();
			
			bitL = gen.channel.readInt();
			boolean[][] data1 = new boolean[SIZE][bitL];
			bobBF = gen.newTArray(MEDIAN, SIZE, 0);
			for(int i = 0; i < MEDIAN; ++i) {
				bobBF[i] =  gen.inputOfBob(data1);
				gen.channel.flush();
			}
			System.out.println("Time to do OT: "+(System.currentTimeMillis()/1000.0-t1));
			t1 = System.currentTimeMillis()/1000.0;
		}

		@Override
		public void secureCompute(CompEnv<T> gen) throws Exception {
			res = gen.newTArray(numberOfFile, 0);
			T[][] med = gen.newTArray(MEDIAN, 0);
			boolean[][] data = new boolean[SIZE][bitL];
			for(int listindex = 0; listindex < numberOfFile; listindex++) {
				BitonicSortLib<T> lib = new BitonicSortLib<T>(gen);
				for(int i = 0; i < MEDIAN; ++i) {
					for(int j = 0; j < sketch[listindex].bs[i].length; ++j)
						data[j] = Utils.fromInt(sketch[listindex].bs[i][j], bitL);
					aliceBF = gen.inputOfAlice(data);
					med[i] = compute(gen, aliceBF, bobBF[i]);
				}

				lib.sort(med, gen.ZERO());
				res[listindex] = med[MEDIAN/2];

				System.out.println("Time for paitent "+(1+listindex)+": "+(System.currentTimeMillis()/1000.0-t1));
				t1 = System.currentTimeMillis()/1000.0;
			}
		}

		@Override
		public void prepareOutput(CompEnv<T> gen) {
			String f = new String(gen.channel.readBytes());
			for(int i = 0; i < numberOfFile; ++i){
				double r = Utils.toInt(gen.outputToAlice(res[i]));
				r = r / SIZE;
				r = r * r * Math.PI/2;
				System.out.print("Distance: "+r);
				if(config.getBool("Test")) {
					double result = PrepareData.setDiff(f, list.get(i).trim());
					System.out.print("\t real result:"+result);
					System.out.println("\t relative error: "+(r-result)/result);
				}
			}
			gen.channel.writeBoolean(true);//for sync
			gen.channel.flush();
			executor.shutdown();
		}
	}

	public static class Evaluator<T> extends EvaRunnable<T> {
		T[][] aliceBF;
		T[][][] bobBF;
		T[][] res;
		CommandLine cmd;
		SketchForProtocol2 sketch;
		int numberOfFile;
		public int SIZE, MEDIAN, bitL;
		@Override
		public void prepareInput(CompEnv<T> gen) throws Exception {
			SIZE = config.getInt("m");
			MEDIAN = config.getInt("l");
			
			sketch = new SketchForProtocol2(SIZE, MEDIAN);
			numberOfFile = gen.channel.readInt();
			for(int i = 0; i < sketch.sks.length; ++i)
				for(int j = 0; j < sketch.sks[0].length; ++j)
				sketch.sks[i][j] = gen.channel.readLong();

			PrepareData.readFile(config.getString("EvaFile"), sketch);
			sketch.finalizeSketch();
			res = gen.newTArray(numberOfFile, 0);
			
			for(int j = 0; j < MEDIAN; ++j)
				for(int i = 0; i < sketch.bs[j].length; ++i)
					sketch.bs[j][i]*=-1;

			bitL = sketch.MaxInt()*2;
			gen.channel.writeInt(bitL);gen.channel.flush();
			boolean[][] data1 = new boolean[sketch.bs[0].length][bitL];
			bobBF = gen.newTArray(MEDIAN, SIZE, 0);
			for(int i = 0; i < MEDIAN; ++i) {
				for(int j = 0; j < sketch.bs[i].length; ++j) {
					data1[j] = Utils.fromInt(sketch.bs[i][j], bitL);
				}
				bobBF[i] =  gen.inputOfBob(data1);
				gen.channel.flush();
			}
		}

		@Override
		public void secureCompute(CompEnv<T> gen) throws Exception {
			T[][] med = gen.newTArray(MEDIAN, 0);
			boolean[][] data = new boolean[SIZE][bitL];
			for(int listindex = 0; listindex < numberOfFile; listindex++) {
				BitonicSortLib<T> lib = new BitonicSortLib<T>(gen);
				for(int i = 0; i < MEDIAN; ++i) {
					aliceBF = gen.inputOfAlice(data);
					med[i] = compute(gen, aliceBF, bobBF[i]);
				}
				lib.sort(med, gen.ZERO());
				res[listindex] = med[MEDIAN/2];
			}
		}

		@Override
		public void prepareOutput(CompEnv<T> gen) {
			gen.channel.writeByte(config.getString("EvaFile").getBytes());
			gen.channel.flush();
			for(int i = 0; i < numberOfFile; ++i)
				gen.outputToAlice(res[i]);
			gen.channel.readBoolean();
		}
	}

	public static ArrayList<String> getListofFiles(String filename) {
		Scanner scanner;
		ArrayList<String> list = new ArrayList<String>();
		try {
			File file = new File(filename);
			scanner = new Scanner(file);
			String a = scanner.nextLine();
			int lines = new Integer(a);
			for(int i = 0; i < lines; ++i) {
				String tmp = scanner.nextLine();
				list.add(tmp.trim());
			}
			scanner.close();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return list;
	}
}
