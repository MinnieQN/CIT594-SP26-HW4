●	What is the purpose of RELAX-A-STAR described in the pseudo code for A* Search?
  
  The purpose is to update a vertex's shortest known distance if a shorter path to it is found.
  
●	Why does A* require a priority queue (or min-heap)? What would happen if it used a regular queue instead?
  
  Min-heap is needed to explore the vertex with the lowest estimated total cost (distance + heuristic) first. This greedy selection ensures the algorithm finds the optimal path by prioritizing the shorter path. If a regular queue were used, vertices would be explored in insertion order regardless of their cost. 
  
●	When translating your solution from Java to Python, how did your chosen data structures change? Give a specific example of a data structure you used in Java and its equivalent in Python.
  
  HashMap in Java was changed to dictionary in Python. They have the same mechanism of storing keys and values as a pair.
  
●	How long did it take you to complete this assignment (in hours)?
  
  10 hours
  
●	What parts of this assignment did you find most difficult?
  
  Switching programming language to Python
  
●	Did you use any outside resources to complete this assignment (worked with a friend, stack overflow, ChatGPT, etc.)? If so, what resources and in what way did you use them?
  
  No
  
●	Please write one or two sentences about something that you learned while completing this assignment.
  
  I learned implementing class/object with Python.
