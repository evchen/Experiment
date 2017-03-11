
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Exp{

    /*
      HashMap (NodeID, Neighbours(Neighbour ID, Distance))
     */
    static HashMap<Integer, HashMap<Integer,Integer>> graph = new  HashMap<Integer, HashMap<Integer,Integer>>();
    static int size = 0;
    static int START_NODE = 1;
    static HashMap<Integer,Integer> shortest_dist =new HashMap<Integer,Integer>();


    public static void stream_input(){


        String filename = "/home/eve/Downloads/moreno_health_health.txt";
        try{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            int i = 0;
            while((line = br.readLine()) != null){
                //ArrayList <Integer> temp = new ArrayList();
                String [] stemp = line.split(" ");
                int[] temp = Arrays.asList(stemp).stream().mapToInt(Integer::parseInt).toArray();

                if(graph.containsKey(temp[0])){
                    HashMap <Integer,Integer> neighbors = new HashMap<>();
                    neighbors = graph.get(temp[0]);
                    //System.out.print(neighbors);
                    neighbors.put(temp[1], temp[2]);
                    graph.put(temp[0], neighbors);
		    shortest_dist.put(temp[0],Integer.MAX_VALUE);
		    shortest_dist.put(temp[1],Integer.MAX_VALUE);
				    
		    
                }else{
                    HashMap <Integer,Integer> neighbors = new HashMap<>();
                    neighbors.put(temp[1], temp[2]);
                    graph.put(temp[0], neighbors);
		    shortest_dist.put(temp[0],Integer.MAX_VALUE);
		    shortest_dist.put(temp[1],Integer.MAX_VALUE);
                }

            }

	    //            System.out.println(nodesets);
        } catch (FileNotFoundException e) {
	    e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	size = shortest_dist.size();
    }
    
    // TODO insert normal queue
    // TODO test bench mark    
    
    public static void main(String[] args){
	

	stream_input();
	long start = System.nanoTime();
	runPQ();
	long end = System.nanoTime();
	
	System.out.println("execution time of Priority QUEUE: "+ (end-start));
	System.out.println(shortest_dist);

	start = System.nanoTime();
	runQ();
	end = System.nanoTime();

	System.out.println(shortest_dist);
	System.out.println("execution time of Normal QUEUE: "+ (end-start));
	
    }

    public static void runQ(){
	// making a queue
	HashMap<Integer, Integer> queue = new HashMap<Integer,Integer>(size);

	for(HashMap.Entry<Integer,Integer> entry : shortest_dist.entrySet()){
	   
	    queue.put(entry.getKey(),Integer.MAX_VALUE);
	}
	queue.put(START_NODE,0);
	while(!queue.isEmpty()){
	    /*
	      get smallest item in the map
	     */
	    int min_value =Integer.MAX_VALUE;
	    int min_key = 0;
	    for(HashMap.Entry<Integer,Integer> entry : queue.entrySet()){
		if(entry.getValue() <= min_value){
		    min_value = entry.getValue();
		    min_key = entry.getKey();
		}		
	    }
	    shortest_dist.put(min_key,queue.remove(min_key));
	    if(min_value!=Integer.MAX_VALUE){
		HashMap<Integer,Integer> neighbours = graph.get(min_key);
		if(neighbours!=null){
		    for(HashMap.Entry<Integer, Integer> entry : neighbours.entrySet()){
			if(queue.containsKey(entry.getKey()) && (entry.getValue() + min_value)< queue.get(entry.getKey()))
			    queue.put(entry.getKey(), min_value+entry.getValue());
		    }
		}
	    }

	}
    }

	    
	

    public static void runPQ(){
	
	MyPQ pq = new MyPQ(size);
	
	for(HashMap.Entry<Integer,Integer> entry : shortest_dist.entrySet()){
	   
	    pq.insert(entry.getKey(),Integer.MAX_VALUE);
	}

	pq.edit(START_NODE,0);
	
	
	while(!pq.isEmpty()){
	    
	    MyPQ.Node_value_pair pair = pq.pull();
	    
	    shortest_dist.put(pair.node, pair.value);

	    if(pair.value!=Integer.MAX_VALUE){
	    
		HashMap<Integer, Integer> neighbours = graph.get(pair.node);
		if(neighbours!=null){
		    
		    for(HashMap.Entry<Integer,Integer> entry : neighbours.entrySet()){
			if (pq.contain(entry.getKey())&&(pair.value + entry.getValue() )< pq.getValue(entry.getKey()))
			    pq.edit(entry.getKey(),pair.value+entry.getValue());
			
		    }
		}
	    }
	    //pq.print();
	}


    }

    public static class MyPQ{

	// index_number ->{node_number, priority}
	Node_value_pair[] binary_heap;
	// {node_number, index_number in heap}
	HashMap<Integer, Integer> map = new HashMap<Integer,Integer>();
	
	public int no_of_element = 0;

	
	
	public MyPQ(int number_of_nodes){
	    binary_heap = new Node_value_pair[number_of_nodes];
	}

	public boolean isEmpty(){
	    if(no_of_element == 0)
		return true;
	    return false;
	}

	public boolean contain(int node){
	    return map.containsKey(node);
	}
	    
	public void print(){

	    for(Node_value_pair pair : binary_heap){
		System.out.print(pair);
	    }
	}

	/*
	         0 
	      1      2 
	    3   4   5 6
	   7 8 9 10
	  chilren of 1 is 3 and 4
	  chilren of n is 2*n+1 and 2*n+2
	  parent of 7 is (n-1)/2
	 */

	public void edit(int node, int value){
	    int index = map.get(node);
	    binary_heap[index].value = value;
	    swim(binary_heap[index]);
	}

	public int getValue(int node){
;
	    int index = map.get(node);
	    return binary_heap[index].value;
	}

	public Node_value_pair pull(){
	    if(no_of_element == 1){
		no_of_element =0;
		return binary_heap[0];
	    }

	    swap(binary_heap[0],binary_heap[no_of_element-1]);

	    Node_value_pair pair = binary_heap[no_of_element-1];
	    binary_heap[no_of_element-1] = null;
	    no_of_element --;
	    map.remove(pair.node);
	    sink(binary_heap[0]);
	    return pair;
	}
	
	
	public void insert(int node, int value){
	    binary_heap[no_of_element] = new Node_value_pair(node, value);
	    map.put(node, no_of_element);
	    swim(binary_heap[no_of_element]);
	    no_of_element++;
	}
	
	public void sink(Node_value_pair node){
	   
	    int index = map.get(node.node);
	    int index_children1 = 2 * index + 1;
	    int index_children2 = 2 * index + 2;
	    int partner;
	    if (index_children2 < no_of_element){
		if (binary_heap[index_children1].value>= binary_heap[index_children2].value )
		    partner = index_children2;
		else partner = index_children1;
	    }
	    else if(index_children1 < no_of_element){
		partner = index_children1;
	    }
	    else return;
	    if (binary_heap[index].value > binary_heap[partner].value ){
		
		swap(binary_heap[index], binary_heap[partner]);
		sink(binary_heap[partner]);
	    }
	}

	    

	    
	
	public void swim(Node_value_pair node){
	    int index = map.get(node.node);
	    if (index!=0){
		int parent_index = (index-1)/2;
		if (binary_heap[parent_index].value > binary_heap[index].value){
			
		    swap(binary_heap[parent_index],binary_heap[index]);
		    swim(binary_heap[parent_index]);
		}
		    
	    }	
	}

	

	/*
	  sink, find the smaller of children, swap
	 */

	

	public void swap(Node_value_pair node1, Node_value_pair node2){
	    int index1 = map.get(node1.node);
	    int index2 = map.get(node2.node);
	    map.put(node1.node,index2);
	    map.put(node2.node,index1);

	    Node_value_pair temp = binary_heap[index1];
	    binary_heap[index1] = binary_heap[index2];
	    binary_heap[index2] = temp;
	}



	
	public class Node_value_pair{
	    public int node;
	    public int value;
	    
	    public Node_value_pair(int node, int value){
		this.node = node;
		this.value = value;
	    }

	    public String toString(){
		return "Node: " + node +" Value: " + value + "\n";
	    }
	}
	
    }

}
