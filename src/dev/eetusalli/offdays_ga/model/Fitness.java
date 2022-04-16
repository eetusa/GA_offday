package dev.eetusalli.offdays_ga.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fitness implements Comparable<Fitness>{
    float hard_breaks = 99999;
    float soft_breaks = 99999;
    public List<Float> per_row;

    public Fitness(int employee_amount){
        per_row = new ArrayList<Float>(Collections.nCopies(employee_amount, 9999f));
    }

    @Override
    public int compareTo(Fitness other){
        if (this.hard_breaks > other.hard_breaks) return 1;
        if (this.hard_breaks == other.hard_breaks){
            if (this.soft_breaks > other.soft_breaks) return 1;
            if (this.soft_breaks == other.soft_breaks) return 0;
            if (this.soft_breaks < other.soft_breaks) return -1;
        }
        return -1;
    }

}
