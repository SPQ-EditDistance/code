package sketches;

import java.security.SecureRandom;
import java.util.Arrays;

public class SketchForProtocol1 {
	public int l,k;
	public int[][] bs;
	public long[]sks;
	SecureRandom rnd = new SecureRandom();
	
	public SketchForProtocol1(int m, int l) {
		this.l = m;
		this.k = l;
		bs  = new int[l][m];
		sks = new long[l];
		for(int i = 0; i < sks.length ; ++i)
			sks[i] = rnd.nextInt();
	}

	public void insert(long s) {
		int i = k;
		while(i-- > 0) {
			long h = Hasher.fasthash64(s, sks[i]);
			int pos = (int) ((h & 0xFFFFFF) % l);
			bs[i][pos] += (((h>>29)&1) == 1) ? 1:-1;
		}
	}

	public double size() {
		double[] res = new double[k];
		for(int i = 0; i < k; ++i) {
			for(int j = 0; j < l; ++j) {
				res[i] += ((bs[i][j]*bs[i][j]));
			}
		}
		Arrays.sort(res);
		return res[k/2];
	}

	public void set_diff(SketchForProtocol1 a) {
		for(int j = 0; j < k; ++j)
			for(int i = 0; i < l; ++i) 
				bs[j][i]-=a.bs[j][i];
	}
	
	public int MaxInt() {
		int res = -1;
		for(int i = 0; i < bs.length; ++i)
			for(int j = 0; j < bs[0].length; ++j)
				res = Math.max(res, Math.abs(bs[i][j]));
		return (int)(Math.ceil( Math.log(res)/Math.log(2)));
	}
}
