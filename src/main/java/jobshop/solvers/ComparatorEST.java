package jobshop.solvers;

import java.util.Comparator;

public class ComparatorEST implements Comparator<TaskDuration> {

    @Override
    public int compare(TaskDuration t1, TaskDuration t2) {
        if(t1.startTime == t2.startTime){
            return t1.duration - t2.duration;
        }
        return t1.startTime - t2.startTime;
    }
}
