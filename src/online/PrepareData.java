package online;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;

import sketches.SketchForProtocol1;
import sketches.SketchForProtocol2;

public class PrepareData {

	public static void readFile(String filename, SketchForProtocol1 sketch) {
		try {
			FileChannel fc = new FileInputStream(filename).getChannel();
			MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			LongBuffer longBuffer = buffer.order(ByteOrder.nativeOrder()).asLongBuffer();
			byte[] data = new byte[8];
			while (longBuffer.remaining() > 0) {
				sketch.insert(longBuffer.get());
			}
			fc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static int setDiff(String s1, String s2) {
		int res = 0;
		try {
			HashSet<Long> sketch = new HashSet<Long>();
			FileChannel fc = new FileInputStream(s1).getChannel();
			MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			ByteBuffer longBuffer = buffer.order(ByteOrder.nativeOrder()).asReadOnlyBuffer();
			while (longBuffer.remaining() > 0) {
				sketch.add(longBuffer.getLong());
			}
			fc.close();
			res = sketch.size();
			fc = new FileInputStream(s2).getChannel();
			buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			longBuffer = buffer.order(ByteOrder.nativeOrder()).asReadOnlyBuffer();
			while (longBuffer.remaining() > 0) {
				long l = longBuffer.getLong();
				if(sketch.contains(l))
					res--;
				else res++;
			}
			fc.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public static double setDiffApprox(String s1, String s2, int size, int l) {
		SketchForProtocol1 sa = new SketchForProtocol1(size, l);
		SketchForProtocol1 sb = new SketchForProtocol1(size, l);
		sb.sks = sa.sks;
		readFile(s1, sa);
		readFile(s2, sb);
		for(int i = 0; i < 100000; ++i)
			sa.insert(i);

		System.out.println("set a size: "+sa.size());
		System.out.println("set b size: "+sb.size());
		sa.set_diff(sb);
		return sa.size();
	}

	public static void main(String[] args) {
		//		double res1 = setDiff(args[0], args[1]);
		//		System.out.println(res1);
		double res2 = setDiffApprox(args[0], args[1], new Integer(args[2]), new Integer(args[3]));
		//		System.out.println(res2);
		//		System.out.println((res1-res2)/res1);
	}

	public static void readFile(String filename, SketchForProtocol2 sketch) {
		try {
			FileChannel fc = new FileInputStream(filename).getChannel();
			MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			LongBuffer longBuffer = buffer.order(ByteOrder.nativeOrder()).asLongBuffer();
			byte[] data = new byte[8];
			while (longBuffer.remaining() > 0) {
				sketch.insert(longBuffer.get());
			}
			fc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
