package offline;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;

import online.Protocol1;

public class DataPreprocessor {

	
	public static int getPos(long location, String value, int op, long[] res) {
		char[] charlist = value.toCharArray();
		for(int i = 0; i < charlist.length; ++i) {
			res[i] = i;
			res[i] <<=2;
			res[i] = res[i] | op;
			res[i] <<=2;
			res[i] = res[i] | toInt(charlist[i]);	
			res[i] <<=40;
			res[i] = res[i] | location;	
		}
		return charlist.length;
	}
	
	public static int DNAtoInt(String a) {
		if(a.length() == 1) {
			if(a.charAt(0) == 'X') return 23;
			if(a.charAt(0) == 'Y') return 24;
			if(a.charAt(0) == 'M') return 25;
		}
		return Integer.parseInt(a);
	}

	public static int opToInt(String a) {
		if(a.equals("SUB"))return 0;
		else if(a.equals("SNP")) return 1;
		else if(a.equals("DEL")) return 2;
		else if(a.equals("INS")) return 3;
		else{
			try {
				throw new Exception("unsupported op type!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//				System.exit(1);
			}
			return -1;
		}
	}

	public static int toInt(char a) {
		if(a == 'A')
			return 0;
		if(a == 'T')
			return 1;
		if(a == 'C')
			return 2;
		if(a == 'G')
			return 3;
		else{
			try {
				throw new Exception("unsupported format!");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//				System.exit(1);
			}
			return -1;
		}
	}

	public static void readFile(String filename, String outputdir) {
		try {
			MessageDigest sha = MessageDigest.getInstance("MD5");
			long[] res = new long[10000];
			String value;
			int a, op; 
			long index, loc;
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String sCurrentLine;
			String [] t = filename.split("/");
			DataOutputStream out = new DataOutputStream(new FileOutputStream(outputdir+t[t.length-1]));
			while((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.charAt(0) == '#')
					continue;

				String[] s = sCurrentLine.split("\t");
				a  = DNAtoInt(s[0]);
				op = opToInt(s[s.length-1].substring(7, 10));
				index = Long.parseLong(s[1]);
				loc = (index<<5) | a;


				if(op == 3) {//insert 
					value = s[4];
				}
				else if(op == 2) {//delete
					value = s[3];//does not matter what value it is
				}
				else {
					value = s[4];
				}
				int l = getPos(loc, value, op, res);

				for(int i = 0; i < l; ++i) {
					sha.update(ByteBuffer.allocate(8).putLong(res[i]).array());
					long a1 = ByteBuffer.wrap(sha.digest()).getLong();
					out.writeLong(a1);
				}
			}
			br.close();
			System.out.println("Processed: "+filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		ArrayList<String> f = Protocol1.getListofFiles(args[0]);
		for(String s:f)
			readFile(s,args[1]);
	}
}