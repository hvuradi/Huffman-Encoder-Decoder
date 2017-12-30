
import java.io.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;


public class encoder {

    String fileName;
    String codeFileName;

    public static void main(String[] args) throws IOException {
	    Map<Integer, Node> map = new HashMap<>();
	    ArrayList<Integer> encode = new ArrayList<Integer>();
        String fileName = args[0];
	    Scanner sc = new Scanner(new File(fileName));
	    while(sc.hasNext()){
            int i = sc.nextInt();
	    	encode.add(i);
	    	if (map.containsKey(i)) {
	    		Node n = map.get(i);
	    		n.frequency += 1;
	    	} else {
	    		Node nNode = new Node(i, 1);
	    		map.put(i, nNode);
	    	}
	    }sc.close();
	    
	    Node [] inputArr = new Node[map.size()+3];
		inputArr[0] = null;
		inputArr[1] = null;
		inputArr[2] = null;
    	int mCntr = 3;
    	for (Node nd: map.values()) {
    		inputArr[mCntr++] = nd;
    	}
    	Huffman.huffman_build(inputArr,encode);
	}
    
    encoder(String fileName, String codeFileName) {
        this.fileName = fileName;
        this.codeFileName = codeFileName;
    }

    void encode(List<Integer> encode, Map<Integer, String> codeTable) throws IOException {
        writeCodeTable(codeTable);
        encodeData(encode, codeTable);
    }

    void writeCodeTable(Map<Integer, String> codeTable) throws IOException {
        FileWriter fw = new FileWriter(codeFileName);
        BufferedWriter bw = new BufferedWriter(fw);

        for (Map.Entry<Integer, String> entry: codeTable.entrySet()) {
            String val = entry.getKey() + " " + entry.getValue();
            bw.write(val);
            bw.newLine();
        }
        bw.close();
        fw.close();
    }

    void encodeData(List<Integer> encode, Map<Integer, String> codeTable) throws IOException {
     //   long startTime = System.currentTimeMillis();
        FileOutputStream fos = new FileOutputStream(new File(fileName));
        OutputStream out = new BufferedOutputStream(fos);
        StringBuilder sb1 = new StringBuilder();
        for(int i =0; i < encode.size(); i++) {
            sb1.append(codeTable.get(encode.get(i)));
            
            while ((sb1.length() >= 8) || (sb1.length()!=0 && i == encode.size()-1) ) {
                int end = 8;
                if (sb1.length() < 8) {
                    end = sb1.length();
                }
                String leftS = sb1.substring(end, sb1.length());
                byte bbb = (byte) Integer.parseInt(sb1.substring(0, end),2);
                try {
                    out.write(bbb);
                    sb1.setLength(0);
                    sb1.append(leftS);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        out.close();
        fos.close();

      //  long stopTime = System.currentTimeMillis();
     //   System.out.println("Encoding: " + (stopTime-startTime));
    }
}


class Node {
    
    boolean isLeaf;
	int key;
	int frequency = 0;
	Node left = null;
	Node right = null;
	public Node(int item, int freq)
	{
		key=item;
		frequency = freq;
		
	}		
	@Override
	public String toString(){
		return "(key:"+key+",freq:" +frequency+")";
	}
}


 class Huffman {
	public static void huffman_build(Node[] ndArray, ArrayList<Integer> inputList) throws IOException {
      //  long t2 = System.currentTimeMillis();
        Node root = new FourwayCacheHeap().generate_huffman_tree_using_fourway_heap(ndArray);
      //  long t3 = System.currentTimeMillis();
	    
	 //   System.out.println("Four Way Heap " + (t3-t2)); 
	    
	    HashMap<Integer, String> h = new HashMap<>();
		FourwayCacheHeap.code_table(h,root,"");
	//	long t4 = System.currentTimeMillis();
	//	System.out.println("Code table:" + (t4-t3));
       
		String binFileName = "encoded.bin";
		String codeFileName = "code_table.txt";
		encoder enc = new encoder(binFileName,codeFileName);
		enc.encode(inputList, h);
   }	
}

 class FourwayCacheHeap {
	 int heap_size=0;
	  public void heapify(Node[] Arr1, int i){
		 int l = 4*(i-2);
		 if (l >= heap_size) {
			 return;
		 }
		 int minimum = l;
		 Node tmp;
		 for(int j=l+1; j<=l+3;j++){
			 if(j < heap_size && Arr1[j].frequency < Arr1[minimum].frequency){
				 minimum = j;
			 }
		 }
		 if(minimum < heap_size && Arr1[i].frequency < Arr1[minimum].frequency ){
			 minimum = i;
			 }
		 if(minimum != i){
			 tmp = Arr1[i];
			 Arr1[i] = Arr1[minimum];
			 Arr1[minimum] = tmp;
			 heapify(Arr1,minimum);
		 }

		 }
	  public void build_min_heap(Node[] Arr1){
		  heap_size = Arr1.length;
		  for(int i=Arr1.length/4 +2; i>=3; i--){
			 heapify(Arr1,i);
		  }
	  }
	  public Node remove_min(Node[] Arr1){
		  if(heap_size < 4){
			  return null;
		  }
		  Node min = Arr1[3];
		  Arr1[3] = Arr1[heap_size-1]; 
		  heap_size = heap_size -1;
		  heapify(Arr1,3);
		  return min;
	  }
	  public void min_heap_insert(Node[] Arr1, Node new_Node){
		  Node tmp;
		  heap_size = heap_size + 1;
		  Arr1[heap_size-1] = new_Node;
		  int i = heap_size-1;
		  while(i > 2 && parent(i) > 2 && Arr1[parent(i)].frequency > Arr1[i].frequency){
			  tmp = Arr1[i];
			  Arr1[i] = Arr1[parent(i)]; 
			  Arr1[parent(i)] = tmp;
			  i = parent(i);
		  }
	  }
	  public int parent (int j){
		  return j/4 + 2;
	  }
	  public Node generate_huffman_tree_using_fourway_heap(Node[] Arr1){
		  build_min_heap(Arr1);
		  while(heap_size>4){
			  Node m = remove_min(Arr1);
			  Node m1 = remove_min(Arr1);
			  Node m2 = new Node(-1,m.frequency + m1.frequency);
			  m2.left = m;
			  m2.right = m1;
			  min_heap_insert(Arr1, m2);
		  }
		 
		  return Arr1[3];
	  }
	  public static void code_table(HashMap<Integer, String> h, Node root, String s){
		   if(root.left == null && root.right == null){
			   h.put(root.key, s);
		   }
		   if(root.left != null){
			   code_table(h,root.left,s+'0');
		   }
		   if(root.right != null){
			  code_table(h,root.right,s+'1');
		   }
		   
  }
}
 

 class BinaryHeap {
 	 int heap_size=0;
  public void binary_heapify(Node[] Arr, int i){
	 int l = 2*i + 1;
	 int r = 2*i + 2;
	 int minimum = 0;
	 Node tmp;
	 if(l < heap_size && Arr[l].frequency < Arr[i].frequency){
		 minimum = l;
		}
	 else{
		 minimum = i;
	 } if(r < heap_size && Arr[r].frequency < Arr[minimum].frequency){
		 minimum = r;
	 }
	 if(minimum != i){
		 tmp = Arr[i];
		 Arr[i] = Arr[minimum];
		 Arr[minimum] = tmp;
		 binary_heapify(Arr,minimum);
	 }
	 }
  public void build_min_heap(Node[] Arr){
	  heap_size = Arr.length;
	  for(int i=Arr.length/2-1; i>=0; i--){
		 binary_heapify(Arr,i);
	  }
  }
  public Node remove_min1(Node[] Arr){
	  if(heap_size < 1){
		  return null;
	  }
	  Node min = Arr[0];
	  Arr[0] = Arr[heap_size-1]; 
	  heap_size--;
	  binary_heapify(Arr,0);
	  return min;
  }
  public void min_binary_heap_insert(Node[] Arr, Node new_node){
	  Node tmp;
	  heap_size = heap_size + 1;
	  Arr[heap_size-1] = new_node;
	  int i = heap_size-1;
	  while(i > 1 && Arr[parent(i)].frequency > Arr[i].frequency){
		  tmp = Arr[i];
		  Arr[i] = Arr[parent(i)]; 
		  Arr[parent(i)] = tmp;
		  i = parent(i);
	  }
  }  
  public int parent(int i) {
	  return (i-1)/2;
  }
  
  public void generate_huffman_tree_using_binary_heap(Node[] Arr){
	  build_min_heap(Arr);
	  while(heap_size>1){
		  Node m = remove_min1(Arr);
		  Node m1 = remove_min1(Arr);
		  Node m2 = new Node(-1,m.frequency + m1.frequency);
		  m2.left = m;
		  m2.right = m1;
		  min_binary_heap_insert(Arr, m2);
	  }
  }
}

class Node1 {
	    
	    boolean isLeaf;
		int key;
		int frequency;
		ArrayList<Node1> child = new ArrayList<Node1>();
		Node1 left = null;
		Node1 right = null;
		
		public Node1(int item, int freq)
		{
			key=item;
			frequency = freq;
			
		}
}
 
class PairingHeap {
		Node1 root = null;
	 
	    public Node1 meld(Node1 n, Node1 n1){
	    	if(n == null){
	    		return n1;
	    	}
	    	if(n1 == null){
	    		return n;
	    	}
	    	if(n.frequency < n1.frequency){
	    	   n.child.add(n1);
	    	   return n;
	    	}else{
	    		n1.child.add(n);
	    		return n1;
	    	}
	    	
	    }
	    public void insert(Node1 n){
	     root = meld(root, n);
	    }
	    public Node1 meld_pair(ArrayList<Node1> an){
	    	if(an.size() == 1){
	    		return an.get(0);
	    	}
	    	ArrayList<Node1> an1 = new ArrayList<Node1>();
	    	for(int j=0; j<an.size(); j+=2){
	    		if(j == an.size()-1){
	    			an1.add(an.get(j));
	    		}else{
	    		an1.add(meld(an.get(j), an.get(j+1)));	
	    		}
	    	}
	    	return meld_pair(an1);
	    }

	    public Node1 remove_min(){
	    	if(root == null){
	    		return null;
	    	}
	    	Node1 a = new Node1(root.key,root.frequency);
	       	root = meld_pair(root.child);
	    	return a;
	    }
	    public void generate_huffman_tree_using_pairing_heap(Node1[] Arr){
	    	for(int i=0; i<Arr.length; i++){
	    		insert(Arr[i]);
	    	}
	    	while(root != null){
				  Node1 m = remove_min();
				  Node1 m1 = remove_min();
				  Node1 m2 = new Node1(-1,m.frequency + m1.frequency);
				  m2.left = m;
				  m2.right = m1;
				  insert(m2);
			  }
	    }
	}

