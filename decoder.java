
import java.io.*;
public class decoder {

    private String codeFileName;
    private String binFileName;
    private String outputFile;

    public static void main(String[] args) throws IOException {
        String outputFileName = "decoded.txt";
        String codeFileName = args[1];
        String binFileName = args[0];
        decoder dec = new decoder(binFileName, codeFileName , outputFileName);
        dec.decode();
    }

    decoder( String binFileName,String codeFileName, String outputFile) {
        this.codeFileName = codeFileName;
        this.binFileName = binFileName;
        this.outputFile = outputFile;
    }

    private class hnode {
        int val;
        hnode right;
        hnode left;
        hnode () {
            val = -1;
            right = null;
            left = null;
        }
    }

    public void decode() throws IOException {
        decodeValues(constructDecodeTree());
    }

    hnode constructDecodeTree() throws IOException {
       // long startTime = System.currentTimeMillis();
        FileReader fr = new FileReader(codeFileName);
        BufferedReader br = new BufferedReader(fr);
        hnode root = new hnode();

        String s;
        StringBuilder sb = new StringBuilder();
        while((s=br.readLine()) != null) {
            String [] sts = s.split(" ");
            int key = Integer.valueOf(sts[0]);
            String binval = sb.toString() + sts[1];
            hnode curr = root;
            for (int i =0; i < binval.length(); i++) {
                int pos = Integer.parseInt(binval.substring(i, i +1));
                hnode hn;
                if (pos == 1) {
                    if (curr.right != null) {
                        hn = curr.right;
                    } else {
                        hn = new hnode();
                        curr.right = hn;
                    }
                } else {
                    if (curr.left != null) {
                        hn = curr.left;
                    } else {
                        hn = new hnode();
                        curr.left = hn;
                    }
                }
                curr = hn;
                if (i == binval.length()-1) {
                    curr.val = key;
                    curr = root;
                }
            }
        }
     //   long stopTime = System.currentTimeMillis();
        br.close();
        fr.close();
     //   System.out.println("Tree Build: " + (stopTime-startTime));
        return root;
    }

    void decodeValues(hnode root) throws IOException {
      //  long startTime = System.currentTimeMillis();
        InputStream stream = new FileInputStream(binFileName);

        FileWriter fw = new FileWriter(outputFile);
        BufferedWriter bw = new BufferedWriter(fw);

        int inval;
        hnode curr = root;
        while((inval = stream.read()) != -1) {
            String s = Integer.toBinaryString(inval);
            while(s.length() != 8) {
            	s = "0" + s;
            }
            for (int i = 0; i < s.length(); i++) {
                int j = Integer.parseInt(String.valueOf(s.charAt(i)));
                if (j == 1) {
                    if (curr.right == null) {
                        bw.write(String.valueOf(curr.val)  + '\n');
                        
                        curr = root;
                    }
                    curr = curr.right;
                } else {
                    if (curr.left == null) {
                        bw.write(String.valueOf(curr.val)  + '\n');
                       
                        curr = root;
                    }
                    curr = curr.left;
                }
            }
        }
        bw.write(String.valueOf(curr.val)  + '\n');
        bw.close();
        fw.close();
       // long stopTime = System.currentTimeMillis();
      //  System.out.println("Decoded: " + (stopTime-startTime));
    }
}
