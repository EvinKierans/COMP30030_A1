import java.io.IOException;
import java.util.Scanner;
import java.io.File;

/**
 *
 * @author nhurley
 */
public class PacMan {
    // class to do some simple queueing
    static class Queue {
	int front;
	int back;
	int[] queue;
		
	public Queue(int numjunctions)
	{
	    front=-1;
	    back=-1;
	    queue = new int[numjunctions];
	    for (int i=0;i<numjunctions;i++)
		queue[i]=-1;
	}
	public void enque(int junction)
	{
	    // Add junction to the back of the queue, 
	    // making sure to update front if the queue 
	    // was originally empty 
	    queue[junction]=-1;
	    if (back != -1)
		queue[back] = junction;
	    else
		front = junction;
	    back = junction;
	    return;
	}
	public int deque()
	{
			
	    int junction = front;
	    // Remove junction from the queue 
	    if (back == junction)
		back = -1;
	    front = queue[front];
	    queue[junction] = -1;
	    return junction;
	}
	public boolean empty()
	{
	    return front == -1;
	}
    }
    // class to store the map of the junctions and their connections
    static class Map
    {
	public int[] neighbourhood;
	public int numjunctions;

	public Map(int numj, int[] neigh)
	{
	    numjunctions = numj;
	    neighbourhood = neigh;
	}
    }

    public static void main(String[] args) {
        int i;
        int source, destination;
	Map map;
	Scanner reader = new Scanner(System.in);  // Reading from System.in
	String file;

	//Reading Map file
	System.out.println("Enter the Map File Name:");
	file = reader.next();

        try {
	    map = readMap(file);
        } catch(IOException e)
	    {
		System.out.println("Error in reading neighbourhood file");
		return;
	    }

	int[] parent = new int[map.numjunctions];
        int[] depth = new int[map.numjunctions];
        boolean[] visited = new boolean[map.numjunctions];


        //Reading input
        System.out.println("Enter the source:");
        source = reader.nextInt();
	System.out.println("Enter the destination:");
	destination = reader.nextInt();

        
        System.out.println("Route from "+source+" to "+destination);
		
	Queue theQueue = new Queue(map.numjunctions);

        for (i = 0; i < map.numjunctions; i++) {
            parent[i] = -1;
            visited[i] = false;
        }

	if (findRoute(map, source, destination, visited, theQueue, parent, depth)) {
            // Print the route backwards from destination to source 
            i = destination;
            do {
		System.out.println(i);
		i = parent[i];
	    } while (i != -1);
      	} else {
	    System.out.println("No route exists from source "+source+" to destination "+destination);
	}
        
 
    }

    static boolean findRoute(Map map, int source, 
			     int dest, boolean[] visited, Queue theQueue,
			     int[] parent, int[] depth)
    {
	int neighbour, j, curr_junction;
	boolean dest_found = false;
	int curr_depth;
		  
	// Add the source junction to the queue 
	theQueue.enque(source);
	// Record the source as visited 
	visited[source]=true;
	depth[source]=0;
	// Keep searching until dest is found or the queue is empty 
	while (!dest_found && !theQueue.empty()) {
	    /* Get current junction from queue */
	    curr_junction = theQueue.deque();
	    curr_depth = depth[curr_junction];
	    /* Check if it is the destination */
	    dest_found = (curr_junction == dest);
      
	    if (!dest_found)
		// Check four neighbours of current junction 
		for (j=0;j<4;j++) {
		    neighbour = map.neighbourhood[curr_junction*4+j];
		    if (neighbour != -1 && !visited[neighbour]) {
			//Set parent of neighbour
			parent[neighbour] = curr_junction;

			// Enqueue the neighbour 
			theQueue.enque(neighbour);
			// Record the neighbour as visited 
			visited[neighbour]=true;
			depth[neighbour] = curr_depth+1;
		    }
		}
	}
	return dest_found;
    }


    static Map readMap(String file) throws IOException
    {
	int numjunctions,i;
	// First get the number of junctions  and check
     	// that it is not greater than the maximum size
      	Scanner reader = new Scanner(new File(file));
	numjunctions = reader.nextInt();
	int[] neighbourhood = new int[4*numjunctions];
			  
	// Next read the map into the neighbourhood array 
	for (i=0;i<numjunctions*4;i++) {
	    neighbourhood[i] = reader.nextInt();
		  
	    if (neighbourhood[i]>=numjunctions || neighbourhood[i]<-1)
		throw new IOException();
    		
	}
	Map map = new Map(numjunctions, neighbourhood);
	return map;
    }
}

