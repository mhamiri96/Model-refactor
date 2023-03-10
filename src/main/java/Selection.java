
import java.util.ArrayList;

public class Selection {

    ArrayList<Solution> selection;
    double fronts;
    double generations;

    Selection() {
        this.selection = new ArrayList<>();
        this.fronts = 0;
        this.generations = 0;
    }

    Selection(int generations) {
        this.selection = new ArrayList<>();
        this.fronts = 0;
        this.generations = generations;
    }

    void remove_duplicates() {
        ArrayList<Integer> duplicates = new ArrayList<>();
        for (int i = 0; i < this.selection.size(); i++) {
            double distance = selection.get(i).distance;
            for (int j = 0; j < this.selection.size(); j++) {
                if (j != i) {
                    if (distance == selection.get(j).distance)
                        duplicates.add(j);
                }
            }
        }
        for (int i = 0; i < duplicates.size(); i++) 
            selection.remove(duplicates.get(i));
    }

    double average_front_number() {
        return (double) (this.fronts / this.generations);
    }
}
