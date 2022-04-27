package dev.eetusalli.offdays_ga.util;

/**
 * Supporting class to help the ordering of the rows (employees) of two parents by fitness
 * while keeping track of which row belongs to which parent. Convoluted.
 */
public class FitRow implements Comparable<FitRow>{
    float fit;
    boolean isA;
    int index;

    public FitRow(float fit, boolean isA, int index){
        this.fit = fit;
        this.isA = isA;
        this.index = index;
    }

    public int compareTo(FitRow other){
        if (this.fit > other.fit) return 1;
        if (other.fit > this.fit) return -1;
        return 0;
    }
}
