package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.*;

public class GreedySolver implements Solver {

    public enum Priority {SPT, LRPT, ESTSPT
    }

    private Priority priority;

    public GreedySolver(Priority priority) {
        this.priority = priority;
    }

    public Result solve(Instance instance, long deadline) {
        
        if(this.priority == Priority.ESTSPT){
            return solveESTSPT(instance, deadline);
        }else {

            ResourceOrder sol = new ResourceOrder(instance);
            int[] nextRealisableTasks = new int[instance.numJobs];
            Arrays.fill(nextRealisableTasks, 0);
            List<TaskDuration> todos = getRealisableTasks(instance, nextRealisableTasks, this.priority);

            while (!todos.isEmpty()) {
                Task to_place = Collections.min(todos).task;

                int machine = instance.machine(to_place);
                nextRealisableTasks[to_place.job] += 1;

                sol.tasksByMachine[machine][sol.nextFreeSlot[machine]] = to_place;
                sol.nextFreeSlot[machine] += 1;

                todos = getRealisableTasks(instance, nextRealisableTasks, this.priority);
            }

            return new Result(instance, sol.toSchedule(), Result.ExitCause.Timeout);
        }
    }

    private Result solveESTSPT(Instance instance, long deadline) {
        ResourceOrder sol = new ResourceOrder(instance);
        int[] nextRealisableTasks = new int[instance.numJobs];
        Arrays.fill(nextRealisableTasks, 0);
        List<TaskDuration> todos = getRealisableESTTasks(instance, nextRealisableTasks, this.priority);
        while (!todos.isEmpty()) {
            Task to_place = Collections.min(todos, new ComparatorEST()).task;
           // System.out.println(to_place);
            int machine = instance.machine(to_place);
            nextRealisableTasks[to_place.job] += 1;

            //System.out.println(Arrays.toString(nextRealisableTasks));
            sol.tasksByMachine[machine][sol.nextFreeSlot[machine]] = to_place;
            sol.nextFreeSlot[machine] += 1;

            todos = getRealisableESTTasks(instance, nextRealisableTasks, this.priority);
        }
        //System.out.println(sol);
        return new Result(instance, sol.toSchedule(), Result.ExitCause.Timeout);
    }

    private List<TaskDuration> getRealisableESTTasks(Instance instance, int[] nextRealisableTasks, Priority priority) {
        List<TaskDuration> todo = new ArrayList<>();

        int [][] startTimes = new int [instance.numJobs][instance.numTasks];
        int[] releaseTimeOfMachine = new int[instance.numMachines];

        for (int numJob = 0; numJob < nextRealisableTasks.length; numJob++) {
            if (nextRealisableTasks[numJob] >= instance.numTasks) {
                continue;
            }
            for (int tache = 0; tache < instance.numTasks; tache++) {


                //int tache = nextRealisableTasks[numJob];
                int machine = instance.machine(numJob, tache);
                int est = tache == 0 ? 0 : startTimes[numJob][tache - 1] + instance.duration(numJob, tache - 1);
                est = Math.max(est, releaseTimeOfMachine[machine]);
                startTimes[numJob][tache] = est;

                releaseTimeOfMachine[machine] = est + instance.duration(numJob, tache);

                if(tache == nextRealisableTasks[numJob]){
                    Task task = new Task(numJob, tache);

                    int duration = instance.duration(task);

                    todo.add(new TaskDuration(task, duration, est));
                }
            }
        }
        Collections.sort(todo, new ComparatorEST());
        return todo;
    }

    private List<TaskDuration> getRealisableTasks(Instance instance, int[] nextRealisableTasks, Priority priority) {
        List<TaskDuration> todo = new ArrayList<>();
        for (int numJob = 0; numJob < nextRealisableTasks.length; numJob++) {

            if (nextRealisableTasks[numJob] >= instance.numTasks) {
                continue;
            }

            Task task = new Task(numJob, nextRealisableTasks[numJob]);

            int duration = -1;
            if (priority == Priority.SPT) {
                duration = instance.duration(task);
            } else if (priority == Priority.LRPT) {
                duration = 0;
                for (int numTask = task.task; numTask < instance.numTasks; numTask++) {
                    duration += instance.duration(task.job, numTask);
                }
                duration = -duration;
            }
            todo.add(new TaskDuration(task, duration));
        }
        return todo;
    }
}





/*
    private Result solveLPT(Instance instance, long deadline) {
        ResourceOrder sol = new ResourceOrder(instance);
        int[] nextRealisableTasks  = new int[instance.numJobs];
        Arrays.fill(nextRealisableTasks, 0);
        List<TaskDuration> todos = getRealisableTasks(instance, nextRealisableTasks);

        while( ! todos.isEmpty() ){
            Task to_place = todos.remove(0).task;
            // System.out.println(to_place);
            int machine = instance.machine(to_place);
            nextRealisableTasks[to_place.job] += 1;
            todos = getRealisableTasks(instance, nextRealisableTasks, Priority.LPT);
            sol.tasksByMachine[machine][sol.nextFreeSlot[machine]] = to_place;
            sol.nextFreeSlot[machine] += 1;
        }
        //System.out.println(sol);
        return new Result(instance, sol.toSchedule(), Result.ExitCause.Timeout);

    }


    private Result solveSPT(Instance instance, long deadline) {
        ResourceOrder sol = new ResourceOrder(instance);
        int[] nextRealisableTasks  = new int[instance.numJobs];
        Arrays.fill(nextRealisableTasks, 0);
        List<TaskDuration> todos = getRealisableTasks(instance, nextRealisableTasks);

        while( ! todos.isEmpty() ){
            Task to_place = todos.remove(0).task;
           // System.out.println(to_place);
            int machine = instance.machine(to_place);
            nextRealisableTasks[to_place.job] += 1;
            todos = getRealisableTasks(instance, nextRealisableTasks);
            sol.tasksByMachine[machine][sol.nextFreeSlot[machine]] = to_place;
            sol.nextFreeSlot[machine] += 1;
        }
        //System.out.println(sol);
        return new Result(instance, sol.toSchedule(), Result.ExitCause.Timeout);

    }

}*/
