package jobshop.solvers;

import jobshop.encodings.Task;

import java.util.List;


    public class TaskDuration implements Comparable<TaskDuration>{
        public Task task;
        public int duration;
        public int startTime;

        public TaskDuration(Task task, int duration){
            this.task = task;
            this.duration = duration;
            this.startTime = 0;
        }

        public TaskDuration(Task task, int duration, int startTime){
            this.task = task;
            this.duration = duration;
            this.startTime = startTime;
        }

        @Override
        public int compareTo(TaskDuration other) {
            return this.duration - other.duration;
        }

        public String toString(){
            return task + "#" + duration + "ST:" + startTime;
        }

    }