# Artificial intelligence course final project
The final project of this course was to implement othello game with minmax AI.
The GUI has been written using java Swing game and we used MVC design pattern that helped a lot in keeping our project clean and readable.
The game logic has been written inside the board class and can viewed there. 

For the main part of our project that is the creation of minmax tree and pruning using alpha beta and implementing a heuristic for depth limited min max.
Further improvments were then added to reduce the width of our search tree by only picking 3 nodes from the generated moves the chance of getting picked being dependent on the heuristic function. 

For the game to be playable we also added a new feature to include time limited iterative deepening to make sure we always had an answer ready at the given time limit.
The heuristic were learned using a Evolutionary Algorithm that pited randomly generated Agents against each other and the 10 most superior were chosen to advance and to crossover. 

To mitigate problems with lack of diversity we also included mutation with reasonably high chance on our population and also introduced a randomly generated Agent in every iteration of our population cycle.
