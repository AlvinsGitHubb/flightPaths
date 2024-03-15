
/*
 * Author: Alvin Mathew
 * Professor: Kamran Khan
 * Description: This program is built to determine the best flight path for the user. The flight
 * path can be filtered in terms of time or cost. The user specifies this in the reqFlights.txt file.
 * The flight data is in the flights.txt file. It contains the number of flights, and details of the
 * flights, including the source, destination, cost, and time respectively. The output of the best 
 * flight path goes onto an itinerary labeled as output.txt. If the flight path cannot be found, an 
 * error message will be printed onto the output.txt file. 
 */
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class Priority {

    private HashMap<String, Integer> optimalDistance;
    private HashMap<String, Integer> distance;
    private Set<String> setFlights;
    private List<Path1> visitedNodeList;
    private PriorityQueue<Node> queue;
    private int optionType, otherOption; // 2 = C, 3 = T
    private String timeCost[][], src, dest;
    private List<String> nodeList;

    // constructor
    public Priority(List<String> nodeList) {

        this.nodeList = nodeList;
        optimalDistance = new HashMap<String, Integer>();
        distance = new HashMap<String, Integer>();
        setFlights = new HashSet<String>();
        visitedNodeList = new ArrayList<Path1>();
        queue = new PriorityQueue<Node>(new Node());

    }

    // we will use dijkstra for this program
    public void dijkstras(String timeCost[][], String path_request[]) {

        String evalNode;
        src = path_request[0];
        dest = path_request[1];

        // If item at path_request=C
        if (path_request[2].equalsIgnoreCase("C")) {
            optionType = 2;
            otherOption = 3;

        } else { // item = T
            optionType = 3;
            otherOption = 2;
        }

        this.timeCost = timeCost;
        // traverse node list
        for (String item : nodeList) {
            // stick item from list of nodes into hash map labeled optimalDistance
            optimalDistance.put(item, Integer.MAX_VALUE);

            // Stick it to the distance hash map
            distance.put(item, Integer.MAX_VALUE);

        }

        // Add new node into queue
        queue.add(new Node(src, 0));

        // src = 0 in optimalDistance
        optimalDistance.replace(src, 0);

        // src = 0 in distance
        distance.replace(src, 0);

        // Traverse priority queue till empty
        while (!queue.isEmpty()) {

            // assign the node to be evaluated from the minimum distance node in queue
            evalNode = minDistQueueNode();

            // Create new path of nodes
            Path1 evalList = new Path1();

            // set the evaluated node into the evaluated list
            evalList.setNewNode(evalNode);

            // Add the evaluated node to the hash set of settled flights
            setFlights.add(evalNode);

            // evaluate the neighbors of the evaluated node from the list of evaluated nodes
            evalNeighbors(evalNode, evalList);

            // If evaluated node doesnt exist in list of visited nodes
            // then add it to the list of visited nodes
            if (!nodeExists(visitedNodeList, evalNode))
                visitedNodeList.add(evalList);

        }

    }

    // determine if node exists or not
    private boolean nodeExists(List<Path1> visitedNodeList, String evalNode) {

        // Traverse list of visited nodes
        for (Path1 p : visitedNodeList) {
            if (p.getNewNode().equals(evalNode))
                return true;
        }
        return false;

    }

    // find node with smallest distance from queue
    private String minDistQueueNode() {
        // store recent node from queue into string
        String recentNode = queue.remove().node;

        // return the removed node
        return recentNode;

    }

    // look at chosen node and its neighbors
    private void evalNeighbors(String evalNode, Path1 evalList) {

        int dist_edge = -1;
        int newDist = -1;

        // traverse 2d time array
        // i = row num, j = contents
        for (int i = 0; i < timeCost.length; i++) {

            // If current element of the first column is equal to the evaluated node keep
            // going
            if (!timeCost[i][0].equals(evalNode))
                continue;

            String destNode;

            // traverse list of nodes
            for (int j = 0; j < nodeList.size(); j++) {

                // Store current value of list of nodes into destNode
                destNode = nodeList.get(j);

                // If current element of the second column is equal to destNode keep going
                if (!timeCost[i][1].equals(destNode))
                    continue;

                // If list of settled flights does not contain the destNode
                if (!setFlights.contains(destNode)) {

                    // Get the distance of an edge
                    dist_edge = Integer.parseInt(timeCost[i][optionType]);

                    // Get new distance
                    newDist = optimalDistance.get(evalNode) + dist_edge;

                    // If new distance is less than the optimalDistance distance of destination node
                    if (newDist < optimalDistance.get(destNode)) {

                        // Replace the new distance with the destination node in optimal hash map
                        optimalDistance.replace(destNode, newDist);

                        // Replace the node at the distance of
                        // (evaluated node + time of cost of a path with the destination node)
                        // in the distance hash map

                        distance.replace(destNode, distance.get(evalNode) + Integer.parseInt(timeCost[i][otherOption]));

                        // Traverse list of visited nodes
                        for (Path1 p : visitedNodeList) {
                            // If the path from the destination node exists delete the destination node from
                            // the list of paths
                            if (p.Path1Exists(destNode))
                                p.delNode(destNode);

                            break;
                        }

                        // Add destination node to the list of evaluated nodes
                        evalList.addNode(destNode);

                    }

                    // Add a new node to the priority queue
                    queue.add(new Node(destNode, optimalDistance.get(destNode)));

                }

            }

        }

    }

    public static void main(String[] arg) throws FileNotFoundException {

        String timeCost[][], reqList[][];
        BufferedReader buffFligtData, buffReqData;
        List<String> mainNodeList;

        // open output file
        PrintWriter outfile = new PrintWriter("src/output.txt");
        // try opening the files and performing the main execution
        try {

            // read from flights.txt
            buffFligtData = new BufferedReader(new FileReader("src/flights.txt"));

            // read from reqFlights.txt
            buffReqData = new BufferedReader(new FileReader("src/reqFlights.txt"));

            String line; // used to store lines in files

            // Create an array list of nodes
            mainNodeList = new ArrayList<String>();

            // Create a string array to store cityA, cityB, cost, time
            // 0 = city A, 1 = city B, 2 = cost, 3 = time
            timeCost = new String[Integer.parseInt(buffFligtData.readLine())][4];

            // Create a string array to store the list of requested paths
            // 0 = city A, 1 = city B, 2 = C or T
            reqList = new String[Integer.parseInt(buffReqData.readLine())][3];

            String currNode;
            int i = 0;
            int j;

            // read lines of input file until there are no more lines left to read
            while ((line = buffFligtData.readLine()) != null) {

                j = 0;

                StringTokenizer fileData = new StringTokenizer(line, "|");
                int k = 1; // k is used to make sure the if else statement doesnt get compromised
                while (k <= 2) {

                    // if list of nodes doesnt contain the data in file
                    if (!mainNodeList.contains(currNode = fileData.nextToken())) {

                        // insert node into timecost array
                        timeCost[i][j++] = currNode;

                        // add node to list of nodes
                        mainNodeList.add(currNode);

                    } else
                        // otherwise still add the node to the array but not the node list
                        timeCost[i][j++] = currNode;

                    k++; // no more nodes to look at
                }

                while (fileData.hasMoreTokens()) // store all the times to respective arrays
                {
                    // Store the next token of the tokenize string into the array timecost_of_oath
                    // at (i, j++)
                    timeCost[i][j++] = fileData.nextToken();

                }

                i++;

            }

            i = 0; // i = # of rows for requested flights

            // read the requested flights
            while ((line = buffReqData.readLine()) != null) {

                j = 0;

                StringTokenizer tokenizedData = new StringTokenizer(line, "|");

                // keep this going as long as there are tokens to read
                while (tokenizedData.hasMoreTokens())
                    reqList[i][j++] = tokenizedData.nextToken();

                i++;

            }

            i = 1;

            // traverse the array reqList[][].
            for (String req_path[] : reqList) {

                // If the list of nodes does not contain the required items
                if (!(mainNodeList.contains(req_path[0]) && mainNodeList.contains(req_path[1]))) {
                    outfile.println("Path cannot be found");
                    continue;
                }

                String type, otherType;

                // If element at index 2 of the req_path[] = T
                if (req_path[2].equals("T")) {
                    type = "Time";
                    otherType = "Cost";
                } else { // Now we are looking for cost

                    type = "Cost";
                    otherType = "Time";
                }

                // call constructor
                Priority dij = new Priority(mainNodeList);

                // durn dijkstras for the requested path
                dij.dijkstras(timeCost, req_path);

                // Display the flight route
                outfile.println("Flight " + i + ": " + dij.src + ", " + dij.dest + " (" + type + ")");

                // Traverse the node's list
                for (String node : mainNodeList) {

                    // If the node = destination node of the class
                    if (!node.equals(dij.dest))
                        continue;

                    // list of complete paths
                    List<String> completePath = getPath(dij.visitedNodeList, dij.dest);

                    // Traverse the list of complete paths
                    for (int k = 0; k < completePath.size(); k++) {

                        // If the k = last index of the list of complete paths
                        if (k == completePath.size() - 1) {
                            // display element at index k
                            outfile.print(completePath.get(k) + ". ");

                        } else // Otherwise, display the element at the index k of this list
                            outfile.print(completePath.get(k) + " --> ");

                    }

                    // Display the type and its values to the output file
                    outfile.println(type + ": " + dij.optimalDistance.get(node) + " " + otherType + ": "
                            + dij.distance.get(node));

                    break;

                }

                i++;

            }

        } catch (Exception e) {
            // display error message if something doesn't work out in above code
            System.out.println("Exception:" + e.toString());
        }

        outfile.close();

    }

    // find complete path
    private static List<String> getPath(List<Path1> visitedNodeList, String dest) {
        // create another list of complete paths
        List<String> completePath = new ArrayList<String>();

        // traverse list of visited nodes
        for (Path1 p : visitedNodeList) {
            // If the required destination node does not exist in the path keep going
            if (!p.Path1Exists(dest))
                continue;

            // to get complete path, recursively call this function
            completePath = getPath(visitedNodeList, p.getNewNode());

            // add destination node to the complete path
            completePath.add(dest);

            // return complete path list
            return completePath;

        }

        completePath.add(dest);
        return completePath;

    }

}