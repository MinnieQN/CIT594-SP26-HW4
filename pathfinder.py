import sys
import math
import heapq

class PathFinder:
    class Stop:
        def __init__(self, id, name, lat, lon):
            self.id = id
            self.name = name
            self.lat = lat
            self.lon = lon
    
    def __init__(self):
        self.stops = {}     # {stop_id: Stop}
        self.map = {}       # {stop_id: {neighbor_id: distance}}

    def haversine(self, source, target):
        s = self.stops[source]
        t = self.stops[target]
        lat_s = math.radians(s.lat)
        lon_s = math.radians(s.lon)
        lat_t = math.radians(t.lat)
        lon_t = math.radians(t.lon)
        
        return 6371 * 2 * math.asin(
            math.sqrt(
                math.sin((lat_s - lat_t) / 2) ** 2 +
                math.cos(lat_s) * math.cos(lat_t) * math.sin((lon_s - lon_t) / 2) ** 2
            )
        )
    
    def build_graph(self, nodes_file, edges_file):
        with open(nodes_file, 'r') as f:
            next(f) # skip header
            for line in f:
                line = line.strip()
                if not line:
                    continue
                strings = line.split(',')
                stop_id = strings[0]
                name = strings[1]
                lat = float(strings[2])
                lon = float(strings[3])
                self.stops[stop_id] = self.Stop(stop_id, name, lat, lon)
        
        with open(edges_file, 'r') as f:
            next(f) # skip header
            for line in f:
                line = line.strip()
                if not line:
                    continue
                strings = line.split(',')
                source_id = strings[0].strip()
                target_id = strings[1].strip()

                distance = self.harversine(source_id, target_id)

                self.map.setdefault(source_id, {})[target_id] = distance
                self.map.setdefault(target_id, {})[source_id] = distance
    
    def a_star(self, start, target):
        shortest = {}   # actual distance from start
        estimate = {}   # f-value = dist + heuristic
        pred = {}   # pred
        visited = set()

        shortest[start] = 0
        estimate[start] = self.harversine(start, target)
        pred[start] = None

        # use tuple in headq to compares tuples one by one
        pq = [(estimate[start], start)]

        while pq:
            _, cur = heapq.heappop(pq)

            if cur == target:
                path = []
                curr = target
                while curr is not None:
                    path.append(curr)
                    curr = pred.get(curr)
                path.reverse()
                print('->'.join(path))
                print(f'{shortest[target]:.2f}')
                return
        
            visited.add(cur)

            for neighbor, weight in self.map.get(cur, {}).items():
                if neighbor in visited:
                    continue
                
                new_dist = shortest[cur] + weight
                
                if new_dist < shortest.get(neighbor, float('inf')):
                    shortes[neighbor] = new_dist
                    estimate[neighbor] = new_dist + self.haversine(neighbor, target)
                    pred[neighbor] = cur
                    heapq.heappush(pq, (estimate[neighbor], neighbor))

        print None
    
    if __name__ == '__main__':
        if len(sys.argv) != 6:
            sys.exit(1)
        
        pf = PathFinder()
        pf.build_graph(sys.argv[1], sys.argv[2])

        if sys.argv[3] == 'astar':
            pf.a_star(sys.arv[4], sys.argv[5])