import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class PathFinder {
    /**
     * Construct a Stop class for each node
     */
    private class Stop {
        String id;
        String name;
        double lat;
        double lon;

        Stop(String id, String name, double lat, double lon) {
            this.id = id;
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }
    }

    // list of all stops {stop_id : Stop}
    private HashMap<String, Stop> stops = new HashMap<>();
    // adjacency list for all routes and weights {stop_id, map{neighbor, weight}}
    private HashMap<String, HashMap<String, Double>> map = new HashMap<>();
    
    /**
     * Helper function to calculate Haversine distance between two stops
     * @param source: starting stop
     * @param target: goal stop
     * @return distance between start and target
     */
    public double haversine(String source, String target) {
        // calculate Haversine Distance
        double lat_s = Math.toRadians(stops.get(source).lat);
        double lon_s = Math.toRadians(stops.get(source).lon);
        double lat_t = Math.toRadians(stops.get(target).lat);
        double lon_t = Math.toRadians(stops.get(target).lon);
        double distance = 6371 * 2 * Math.asin(
            Math.sqrt(
                Math.pow(Math.sin((lat_s - lat_t) / 2), 2) + 
                Math.cos(lat_s) * Math.cos(lat_t) * Math.pow(Math.sin((lon_s - lon_t) / 2), 2)
            )
        );
        return distance;
    }

    /**
     * Function to parse csv files and build the entire graph for septa routes
     * @param nodesFile: nodes filename
     * @param edgesFile: edges filename
     */
    public void buildGraph(String nodesFile, String edgesFile) {
        // parse nodes csv file 
        try (BufferedReader br = new BufferedReader(new FileReader(nodesFile))) {
            // read the first line and discard 
            String line = br.readLine();

            // compute stops graph
            while((line = br.readLine()) != null) {
                String[] str = line.trim().split(",");
                String id = str[0];
                String name = str[1];
                double lat = Double.parseDouble(str[2]);
                double lon = Double.parseDouble(str[3]);

                Stop node = new Stop(id, name, lat, lon);
                stops.put(id, node);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        // parse edges csv file
        try (BufferedReader br = new BufferedReader(new FileReader(edgesFile))) {
            // read the first line and discard 
            String line = br.readLine();

            // compute map for all routes
            while((line = br.readLine()) != null) {
                String[] str = line.trim().split(",");
                String source_id = str[0].trim();
                String target_id = str[1].trim();

                double distance = haversine(source_id, target_id);
        
                map.computeIfAbsent(source_id, k -> new HashMap<>()).put(target_id, distance);
                map.computeIfAbsent(target_id, k -> new HashMap<>()).put(source_id, distance);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to for A* search algo and print the path and distance
     * @param start: starting stop
     * @param target: goal stop
     */
    public void aStar(String start, String target) {
        // initialize the shortest path map from start stop to each other stop
        HashMap<String, Double> shortest = new HashMap<>();
        // initialize estimated distance: dist + heuristic
        HashMap<String, Double> estimate = new HashMap<>();
        // initialize predecessor
        HashMap<String, String> pred = new HashMap<>();
        // initialize visited
        HashSet<String> visited = new HashSet<>();

        // use Min-Heap to find the min cost of distance
        PriorityQueue<String> pq = new PriorityQueue<>(
            (a, b) -> {
                double fa = estimate.getOrDefault(a, Double.MAX_VALUE);
                double fb = estimate.getOrDefault(b, Double.MAX_VALUE);
                if (fa != fb) return Double.compare(fa, fb);
                return a.compareTo(b);
            }
        );

        pq.offer(start);
        shortest.put(start, 0.0);
        estimate.put(start, haversine(start, target));
        pred.put(start, null);

        while (!pq.isEmpty()) {
            String cur = pq.poll();

            if (visited.contains(cur)) {
                continue;
            }

            if (cur.equals(target)) {
                // print the path
                List<String> path = new ArrayList<>();
                String curr = target;
                while (curr != null) {
                    path.add(curr);
                    curr = pred.get(curr);
                }
                Collections.reverse(path);
                System.out.println(String.join("->", path));
                System.out.printf("%.2f%n", shortest.get(target));
                return;
            }

            visited.add(cur);

            // get its neighbors
            HashMap<String, Double> neighbors = map.get(cur);
            // traverse its neighbors
            for (Map.Entry<String, Double> entry : neighbors.entrySet()) {
                String neighbor = entry.getKey();
                double weight = entry.getValue();

                if (visited.contains(neighbor)) {
                    continue;
                }

                double newDist = shortest.get(cur) + weight;
                
                if (newDist < shortest.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    // update shortest distance
                    shortest.put(neighbor, newDist); 
                    // update estimate
                    estimate.put(neighbor, newDist + haversine(neighbor, target));
                    // update pred
                    pred.put(neighbor, cur);

                    pq.offer(neighbor);
                }
            } 
        }
        // no path
        System.out.println("NONE");
    }

    /**
     * Main function to run the program
     * @param args: commands array
     */
    public static void main(String[] args) {
        // handle errors
        if (args.length != 5) {
            return;
        }
        // initialize PathFinder
        PathFinder pf = new PathFinder();

        // construct graphs
        pf.buildGraph(args[0], args[1]);

        if (args[2].equals("astar")) {
            // run aStar
            pf.aStar(args[3], args[4]);
        }
    }
}