
public class ReferencePoint {

    double objectives[];

    ReferencePoint(int objectives_number) {
        this.objectives = new double[objectives_number];
    }

    ReferencePoint(ReferencePoint ref) {
        this.objectives = new double[ref.objectives.length];
        System.arraycopy(ref.objectives, 0, this.objectives, 0, this.objectives.length);
    }

    ReferencePoint(double table[]) {
        this.objectives = new double[table.length];
        System.arraycopy(table, 0, this.objectives, 0, this.objectives.length);
    }

    String reference_point_to_string() {
        String result = " ";
        for (int i = 0; i < this.objectives.length; i++)
            result += Double.toString(objectives[i]) + " ";
        return result;
    }
}
