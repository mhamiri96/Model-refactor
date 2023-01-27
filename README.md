
![Generic badge](https://img.shields.io/badge/Language-JAVA-red.svg) ![Generic badge](https://img.shields.io/badge/Status-UP-green.svg) ![Generic badge](https://img.shields.io/badge/TeamMembers-3-blue.svg)

# Model Refactor
## What is this project?
  Model Refactor is an academic project which tries to do refactoring on a real project on two major design criterias (Class diagram and Activity diagram) using NSGA-II. It is worth mentioning that NSGA-II is an algorithm based on Genetic algorithm and is used extensively for multi-objective optimization problems.
  
## What was our motivation? 
Honestly it was a mandatory project of "Software Architecture" course of graduated students of Iran University of Sceince and Technology (IUST) which instructed by Dr. Mehrdad Ashtiani, but we can say that it was interesting enough to concentrate and worth it to spend considerable amount of time on it.

## What did we learn?
To do this project we must get familiar or even get competent in several fields. Software design and architecture concepts, Java programming and Optimization theories(especially multi-objective optimization) were main items. 

## How we did it?
However it's hard to say brief and concise in readme section, but the main procedure starts with defining the problem which is refactoring of class diagram and activity diagram. The next level is choosing a multi-objective optimization method to find best solution considering these two objectives. NSGA-II was the selected method which is a well-known algorithm based on Genetic algorithm. By using Java we set an initial population of solutions (here a solution is an array of several refactors) and go through steps of NSGA-II and made different generations to reach an elite solution for main problem which was finding the best refactor of class diagram while improve activity diagram. We constantly evaluate producing population with certain fitness functions.

## Challenges we faced:
Sometimes refactoring can be a nightmare for running softwares. Even worse if it has a huge users commmunity which are using the software or service continually. In this situation monitoring the system to find pitfalls and analyze potential sections for refactoring will be added to keeping the service up and running. Thus refactoring usually the last solution and option for software holders due to its load and difficulties. This issue shows the large amount of process and it's high risk. Elite algorithms such as NSGA-II finds a good solution but by it's main characteristic which is iteration we must find a good and reasonable number of iteration to get acceptable result regarding to limitation of time, space and equipment.

## How to use this project?
We have inputs called blocks which contain software classes. By running this project on them, and set starting parameters(such as metrics factors, numbers of generation, etc.) you can find candidate solutions and get final best solutions created by NSGA-II. 

## Credits:
With a special thanks to X/Y/Z, this project was done by @X, @Y and @Z.

## License: Open to all.


