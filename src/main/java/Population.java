
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Population {

    int population_size;
    ArrayList<Solution> solutions;
    double min_distance;
    double max_distance;
    ReferencePoint ref;
    Sigma s;
    Selection selection;
    int last_generation;

    Population() {
        this.solutions = new ArrayList<Solution>();
        this.population_size = 0;
        s = new Sigma();
        //ref = new ReferencePoint();
        selection = new Selection();
        last_generation = 0;
    }

    Population(int size, Sigma s, ReferencePoint ref, int generations) {
        this.solutions = new ArrayList<Solution>();
        this.population_size = size;
        this.s = new Sigma(s);
        this.ref = new ReferencePoint(ref);
        this.selection = new Selection(generations);
        last_generation = generations - 1;
    }

    public void update_sigma_value(int current_generation, int max_generations) {
        s.update_sigma(current_generation, max_generations);
    }

    // create population then evaluate it and then normalize its values
    public void create_poplulation() {
        this.solutions = new ArrayList<Solution>();
        for (int i = 0; i < population_size; i++) {
            Solution temp = new Solution();
            temp.create_solution();
            solutions.add(temp);
        }
    }

    // re-evaluate the current population, then normalize its values
    public void evaluate_poplulation() {
        for (int i = 0; i < this.solutions.size(); i++)
            this.solutions.get(i).evaluate_solution();
        this.normalize_metrics();
    }

    // called in evaluate population. To apply r-NSGA-II objective values must be normalized and dominance distance should be calculated
    public void normalize_metrics() {
        // preparing values for calculating dominance distance
        double min_objectives[] = new double[this.solutions.get(0).objectives.size()];
        double max_objectives[] = new double[this.solutions.get(0).objectives.size()];
        for (int i = 0; i < this.solutions.get(0).objectives.size(); i++) {
            min_objectives[i] = this.solutions.get(0).objectives.get(i);
            max_objectives[i] = this.solutions.get(0).objectives.get(i);
            for (int j = 1; j < solutions.size(); j++) {
                if (solutions.get(j).objectives.get(i) < min_objectives[i])
                    min_objectives[i] = solutions.get(j).objectives.get(i);
                if (solutions.get(j).objectives.get(i) > max_objectives[i])
                    max_objectives[i] = solutions.get(j).objectives.get(i);
            }
        }
        
        /*
         *  a + (x-A)*(b-a)/(B-A) where:
            A minimum of dataset
            B maximum of dataset
            a is from where you would like normalised data set to start
            b is where you would like normalised data set to end
            x is the value you are trying to normalise
         *
         * so in case of [0..1] then a = 0 and b = 1
         */

        for (int i = 0; i < solutions.size(); i++) {
            for (int j = 0; j < this.solutions.get(0).objectives.size(); j++)
                this.solutions.get(i).objectives.set(j, (double) (this.solutions.get(i).objectives.get(j) - min_objectives[j]) / (max_objectives[j] - min_objectives[j]));
        }
        int index = 0;
        if (solutions.get(0).objectives_names.contains("Cohesion")) {
            System.out.println("\n Reversing Cohesion");
            index = solutions.get(0).objectives_names.indexOf("Cohesion");
            for (int j = 0; j < solutions.size(); j++)
                solutions.get(j).objectives.set(index, 1 - solutions.get(j).objectives.get(index));
        }
        if (solutions.get(0).objectives_names.contains("Sability")) {
            System.out.println("\n Reversing Stability");
            index = solutions.get(0).objectives_names.indexOf("Sability");
            for (int j = 0; j < solutions.size(); j++)
                solutions.get(j).objectives.set(index, 1 - solutions.get(j).objectives.get(index));
        }
        if (solutions.get(0).objectives_names.contains("Interfacing")) {
            System.out.println("\n Reversing Interfacing");
            index = solutions.get(0).objectives_names.indexOf("Interfacing");
            for (int j = 0; j < solutions.size(); j++)
                solutions.get(j).objectives.set(index, 1 - solutions.get(j).objectives.get(index));
        }
        
        /* this part is no longer needed as it was made for r-NSGA-II
         *
        for(int i=0;i<solutions.size();i++)
        {
            solutions.get(i).distance = 0;
        }
        // determining min/max - complexity - quality - unprecision
        for(int i=0;i<solutions.size();i++)
        {
            double temporary_sum = 0;

            for(int j=0;j<this.solutions.get(0).objectives.size();j++)
            {
                temporary_sum += (Math.pow((solutions.get(i).objectives.get(j) - ref.objectives[j])/(max_objectives[j]-min_objectives[j]), 2));
            }
            solutions.get(i).distance = Math.sqrt(temporary_sum);
        }
        // determine min/max distance
        this.min_distance = solutions.get(0).distance ;
        this.max_distance = solutions.get(0).distance ;

        for(int i=1;i<solutions.size();i++)
        {
            if(solutions.get(i).distance < max_distance) min_distance = solutions.get(i).distance;
            if(solutions.get(i).distance > min_distance) max_distance = solutions.get(i).distance;
        }
        *
         */
    }

//    // used only in compare_dominance
//    int r_dominates(Solution x, Solution y, double dmin, double dmax, Sigma s) {
//        int result = 0;
//        double dist_between_individuals = 0;
//        dist_between_individuals = ((double) (x.distance - y.distance) / (double) (dmax - dmin));
//        if (dist_between_individuals < -s.current_value)
//            result = -1; 
//        else if (dist_between_individuals > s.current_value)
//            result = 1;
//        else
//            result = 0;
//        return result;
//    }

    // to be used on the Comparator class when checking which is the best solution between two candidates
    public int compare_dominance(Solution solution1, Solution solution2) {
        int dominate1; // dominate1 indicates if some objective of solution1
                       // dominates the same objective in solution2. dominate2
        int dominate2; // is the complementary of dominate1.
        dominate1 = 0;
        dominate2 = 0;
        int flag; //stores the result of the comparison
        // Applying a normal dominance Test then
        double value1, value2;
        for (int i = 0; i < solution1.objectives.size(); i++) {
            value1 = solution1.objectives.get(i);
            value2 = solution2.objectives.get(i);
            if (value1 < value2)
                flag = -1;
            else if (value1 > value2)
                flag = 1;
            else
                flag = 0;
            if (flag == -1)
                dominate1 = 1;
            if (flag == 1)
                dominate2 = 1;
        }
        if (dominate1 == dominate2)
            return 0; //No one dominate the other
        if (dominate1 == 1)
            return -1; // solution1 dominate
        return 1;    // solution2 dominate
    } // compare

    // used in generating next population
    public void crowdingDistanceAssignment(ArrayList<Solution> one_front) {
        int size = one_front.size();
        if (size == 0)
            return;
        if (size == 1) {
            one_front.get(0).crowding_distance = Double.POSITIVE_INFINITY;
            return;
        }
        if (size == 2) {
            one_front.get(0).crowding_distance = Double.POSITIVE_INFINITY;
            one_front.get(1).crowding_distance = Double.POSITIVE_INFINITY;
            return;
        }

        //Use a new SolutionSet to evite alter original solutionSet
        //ArrayList<Solution> front = new ArrayList<Solution>();
        /*
        for (int i = 0; i < size; i++)
            front.add(one_front.get(i));
         */
        for (int i = 0; i < size; i++) 
            one_front.get(i).crowding_distance = 0.0;
        double objetiveMaxn;
        double objetiveMinn;
        double distance;
        for (int i = 0; i < one_front.get(0).objectives.size(); i++) {
            // Sort the population by Obj n
            this.sorting_one_objective(one_front, i);
            objetiveMinn = one_front.get(0).objectives.get(i);
            objetiveMaxn = one_front.get(one_front.size() - 1).objectives.get(i);
            //Set de crowding distance
            one_front.get(0).crowding_distance = Double.POSITIVE_INFINITY;
            one_front.get(size - 1).crowding_distance = Double.POSITIVE_INFINITY;
            for (int j = 1; j < size - 1; j++) {
                distance = one_front.get(j + 1).objectives.get(i) - one_front.get(j - 1).objectives.get(i);
                distance = distance / (objetiveMaxn - objetiveMinn);
                distance += one_front.get(j).crowding_distance;
                one_front.get(j).crowding_distance = distance;
            }
        }
    }

    // used when calculating the crowding distance
    void sorting_one_objective(ArrayList<Solution> sol, int index) {
        int min_index = 0;
        for (int i = 0; i < (sol.size() - 1); i++) {
            min_index = i;
            for (int j = i + 1; j < sol.size(); j++) {
                if (sol.get(min_index).objectives.get(index) > sol.get(j).objectives.get(index))
                    min_index = j;
            }
            if (min_index != i) {
                Solution temp = new Solution();
                temp = sol.get(i);
                sol.set(i, sol.get(min_index));
                sol.set(min_index, temp);
            }
        }
    }

    // used in generating next population
    public void sorting_crowding_distance(ArrayList<Solution> sol) {
        int max_index = 0;
        for (int i = 0; i < (sol.size() - 1); i++) {
            max_index = i;
            for (int j = i + 1; j < sol.size(); j++) {
                if (sol.get(max_index).crowding_distance < sol.get(j).crowding_distance)
                    max_index = j;
            }
            if (max_index != i) {
                Solution temp = new Solution();
                temp = sol.get(i);
                sol.set(i, sol.get(max_index));
                sol.set(max_index, temp);
            }
        }
    }

    public void print_popluation() {
        for (int i = 0; i < solutions.size(); i++) {
            System.out.println("\n--- Solution number " + (i + 1) + "---");
            solutions.get(i).print_solution();
        }
    }

    public void print_popluation_metrics(int generation) {
        System.out.println("\n--------------- Population number " + generation + "--------------- ");
        for (int i = 0; i < solutions.size(); i++) {
            System.out.println("\n--- Solution number " + i + "---");
            solutions.get(i).print_metrics();
        }
    }

    public void print_popluation_associated_reference_points() {
        for (int i = 0; i < solutions.size(); i++) {
            System.out.println("\n Solution number " + i + " is associated to reference point number " + solutions.get(i).associated_ref_point_index);
            // print one of example of ref line
            if (i == 0)
                solutions.get(i).print_solution_reference_distances(i);
        }
    }

    // used only in tournament_selection2
    public void crossover(Solution a, Solution b) {
        ArrayList<Solution> result = new ArrayList<>();
        int minimum = Math.min(a.refectorings.size(), b.refectorings.size());
        int cut = Random.random(1, minimum - 2);
        result.add(new Solution());
        for (int i = 0; i < cut; i++)
            result.get(0).refectorings.add(a.refectorings.get(i));
        for (int i = cut; i < b.refectorings.size(); i++)
            result.get(0).refectorings.add(b.refectorings.get(i));
        result.add(new Solution());
        for (int i = 0; i < cut; i++)
            result.get(1).refectorings.add(b.refectorings.get(i));
        for (int i = cut; i < a.refectorings.size(); i++)
            result.get(1).refectorings.add(a.refectorings.get(i));
        // updating solutions
        a.refectorings = new ArrayList<>(result.get(0).refectorings);
        b.refectorings = new ArrayList<>(result.get(1).refectorings);
    }

    // used only in tournament_selection2
    public Solution mutation(Solution s) {
        Solution result = new Solution(s);
        int random = Random.random(0, 1);
        if (random == 0)
            result.mutation1();
        else 
            result.mutation2();
        return result;
    }

    // it is called to select randomly the best candidate solutions to be parents for the upcoming offsprings
    public ArrayList<Solution> tournament_selection2() {
        System.out.println("\n tournamenet selection started...");
        // evaluate the mix of current population and generated offsprings
        this.evaluate_poplulation();
        java.util.Random number_generator = new java.util.Random();
        int first_individual_index = 0;
        int second_individual_index = 0;
        int tournament_bound = this.solutions.size();
        ArrayList<Solution> parents = new ArrayList<Solution>();
        for (int i = 0; i < tournament_bound; i++) {
            number_generator = new java.util.Random();
            do {
                first_individual_index = number_generator.nextInt((this.solutions.size()));
                second_individual_index = number_generator.nextInt((this.solutions.size()));
            } 
            while ((first_individual_index == second_individual_index) || (first_individual_index < 0) || (second_individual_index < 0));
            if (solutions.get(first_individual_index).rank < solutions.get(second_individual_index).rank)
                parents.add(new Solution(solutions.get(first_individual_index)));
            else if (solutions.get(first_individual_index).rank >= solutions.get(second_individual_index).rank)
                parents.add(new Solution(solutions.get(second_individual_index)));
        }
        //crossover & mutation for parents to create offsprings
        for (int i = 0; i < parents.size() - 1; i += 2) {
            this.crossover(parents.get(i), parents.get(i + 1));
            parents.set(i, this.mutation(parents.get(i)));
            parents.set(i + 1, this.mutation(parents.get(i + 1)));
        }
        System.out.println("\n offsprings created... their number is : " + parents.size());
        return parents;
    }

    // it is called to select randomly the best candidate solutions to be parents for the upcoming offsprings
    public ArrayList<Solution> random_selection() {
        System.out.print("random selection started...");
        // evaluate the mix of current population and generated offsprings
        //this.evaluate_poplulation();
        java.util.Random number_generator = new java.util.Random();
        int first_individual_index = 0;
        int tournament_bound = this.solutions.size();
        ArrayList<Solution> parents = new ArrayList<>();
        for (int i = 0; i < tournament_bound; i++) {
            number_generator = new java.util.Random();
            do {
                first_individual_index = number_generator.nextInt((this.solutions.size()));
            } 
            while (first_individual_index < 0);
            parents.add(new Solution(solutions.get(first_individual_index)));
        }

        //crossover & mutation for parents to create offsprings
        for (int i = 0; i < parents.size() - 1; i += 2) {
            this.crossover(parents.get(i), parents.get(i + 1));
            parents.set(i, this.mutation(parents.get(i)));
            parents.set(i + 1, this.mutation(parents.get(i + 1)));
        }
        System.out.println(" and offsprings created... their number is : " + parents.size());
        return parents;
    }

    public void generate_next_popluation(ArrayList<Solution> offsprings, int current_generation) {
        System.out.println("\n evaluating current population + offsprings started with sigma current value : " + s.current_value);
        for (int i = 0; i < offsprings.size(); i++)
            this.solutions.add(new Solution(offsprings.get(i)));
        // re-evaluate the population with the offsprings
        this.evaluate_poplulation();
        System.out.print("\n creating fronts for current population and offsprings...");
        Comparator c = new Comparator(this);
        c.print_fronts();
        int remain = population_size;
        int front_index = 0;
        ArrayList<Solution> front = null;
        this.solutions.clear();

        // Obtain the next front
        front = c.getSubfront(front_index);
        while ((remain > 0) && (remain >= front.size())) {
            //Assign crowding distance to individuals
            this.crowdingDistanceAssignment(front);
            //Add the individuals of this front
            for (int k = 0; k < front.size(); k++)
                this.solutions.add(front.get(k));
            //Decrement remain
            remain = remain - front.size();
            //Obtain the next front
            front_index++;
            if (remain > 0) 
                front = c.getSubfront(front_index);
        }

        // Remain is less than front(index).size, insert only the best one
        if (remain > 0) {
            // front contains individuals to insert
            Hyperplane h = new Hyperplane(Execution.objectives_number, Execution.division);
            BigInteger exprected_results_size = h.binomial((Execution.objectives_number + (int) Execution.division - 1), (int) Execution.division);
            System.out.println("\n Creating a Hyperplane to choose " + remain + " solutions from the current front ");
            System.out.print("\n exprected reference points to create : " + exprected_results_size);
            h.create_hyperplane();
            System.out.println(" and the actual reference points number is : " + h.ref_points.size());
            ArrayList<Solution> candidate_solutions = new ArrayList<>();
            ArrayList<Solution> chosen_solutions = new ArrayList<>();
            candidate_solutions = h.calculate_normal_distance(front);
            chosen_solutions = h.niching(candidate_solutions, remain);
            for (int k = 0; k < remain; k++)
                this.solutions.add(chosen_solutions.get(k));
            this.crowdingDistanceAssignment(front);
            this.sorting_crowding_distance(front);
            for (int k = 0; k < remain; k++)
                this.solutions.add(front.get(k));
            remain = 0;
        }
        // getting the best solution:
        int min_distance_index = 0;
        for (int i = 1; i < this.solutions.size(); i++) {
            if (solutions.get(min_distance_index).distance > solutions.get(i).distance)
                min_distance_index = i;
        }
        selection.selection.add(solutions.get(min_distance_index));
        selection.fronts += c.getNumberOfSubfronts();
        if (current_generation == last_generation - 1) {
            System.out.println("\n last generation created! exporting results...");
            selection.remove_duplicates();
            this.export_selection();
            c = new Comparator(this);
            front = new ArrayList<Solution>();
            front = c.getSubfront(0);
            c.export_population();
            this.export_pareto(front);
            this.export_configuration(front.size());
        } 
        else 
            System.out.println("\n next generation ready...");
    }

    void export_selection() {
        Date dNow = new Date();
        SimpleDateFormat ft= new SimpleDateFormat("yyyy.MM.dd'-'hh.mm.ss");
        String file_name = "./output/result_selection_";
        file_name = file_name.concat(ft.format(dNow));
        file_name = file_name.concat(".csv");
        try {
            FileWriter writer = new FileWriter(file_name);
            for (int i = 0; i < selection.selection.get(0).objectives_names.size(); i++) {
                writer.append(selection.selection.get(0).objectives_names.get(i));
                if (i == (selection.selection.get(0).objectives_names.size() - 1))
                    writer.append('\n');
                else 
                    writer.append(',');
            }
            for (int i = 0; i < selection.selection.size(); i++) {
                for (int j = 0; j < selection.selection.get(i).objectives.size(); j++) {
                    writer.append(Double.toString(selection.selection.get(i).objectives.get(j)));
                    if (j == (selection.selection.get(i).objectives.size() - 1))
                        writer.append('\n');
                    else
                        writer.append(',');
                }
                writer.append("show applied refactoring");
                writer.append('\n');
                for (int k = 0; k < selection.selection.get(i).refectorings.size(); k++) {
                    writer.append(Refactorings.getRefactoring(selection.selection.get(i).refectorings.get(k)));
                    writer.append(',');
                }
                writer.append('\n');
            }
            for (int i = 0; i < ref.objectives.length; i++) {
                writer.append(Double.toString(ref.objectives[i]));
                if (i == (ref.objectives.length - 1))
                    writer.append('\n');
                else 
                    writer.append(',');
            }
            //generate whatever data you want
            writer.flush();
            writer.close();
        } 
        catch (IOException e){
            e.printStackTrace();
        }
    }

    void export_pareto(ArrayList<Solution> pareto) {
        Date dNow = new Date();
        SimpleDateFormat ft=new SimpleDateFormat("yyyy.MM.dd'-'hh.mm.ss");
        String file_name = "./output/result_pareto_";
        file_name = file_name.concat(ft.format(dNow));
        file_name = file_name.concat(".csv");
        try {
            FileWriter writer = new FileWriter(file_name);
            for (int i = 0; i < pareto.get(0).objectives_names.size(); i++) {
                writer.append(pareto.get(0).objectives_names.get(i));
                if (i == (pareto.get(0).objectives_names.size() - 1))
                    writer.append('\n');
                else
                    writer.append(',');
            }
            for (int i = 0; i < pareto.size(); i++) {
                for (int j = 0; j < pareto.get(i).objectives.size(); j++) {
                    writer.append(Double.toString(pareto.get(i).objectives.get(j)));
                    if (j == (pareto.get(i).objectives.size() - 1))
                        writer.append('\n');
                    else
                        writer.append(',');       
                }
            }
            for (int i = 0; i < ref.objectives.length; i++) {
                writer.append(Double.toString(ref.objectives[i]));
                if (i == (ref.objectives.length - 1))
                    writer.append('\n');
                else 
                    writer.append(',');
            }
            //generate whatever data you want
            writer.flush();
            writer.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void export_configuration(int pareto_size) {
        Date dNow = new Date();
        SimpleDateFormat ft=new SimpleDateFormat("yyyy.MM.dd'-'hh.mm.ss");
        String file_name = new String("./output/result_configuration_");
        file_name = file_name.concat(ft.format(dNow));
        file_name = file_name.concat(".csv");
        try {
            FileWriter writer = new FileWriter(file_name);
            writer.append("--- Execution Configuration ---\n\n");
            writer.append("Number of Objectives : " + Integer.toString(this.solutions.get(0).objectives_names.size()) + "\n\n");
            writer.append("Considered Objectives : " + this.solutions.get(0).objectives_names_to_string() + "\n\n");
            writer.append("Population size : " + Integer.toString(this.solutions.size()) + "\n\n");
            writer.append("Iterations : " + Double.toString(this.selection.generations) + "\n\n");
            writer.append("Sigma : " + Double.toString(s.user_value) + "\n\n");
            writer.append("Reference Point : " + ref.reference_point_to_string() + "\n\n");
            writer.append("Pareto size : " + Integer.toString(pareto_size) + "\n\n");
            writer.append("Average number of fronts : " + Double.toString(this.selection.average_front_number()) + "\n\n");
            writer.append("Selected solutions number (optional) : " + Double.toString(this.selection.selection.size()) + "\n\n");
            writer.append("Selected solutions are the set of best solution (minimal distance) in each iteration (duplicated solutions removed) \n\n");
            //generate whatever data you want
            writer.flush();
            writer.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args){
//        // just for testing case
//        Execution e = new Execution(); // important for parameters
//        double aspiration_values[] = {0.4,0.6};
//        ReferencePoint ref = new ReferencePoint(aspiration_values) ;
//        Sigma s = new Sigma(0.5);
//        Population p = new Population(5,s,ref,50);
//        p.create_poplulation();
//        p.print_popluation();
//    }
}