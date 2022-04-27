package dev.eetusalli.offdays_ga.constraints;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Constraints listed for convenience
 * AODO = Amount of days off total.
 * DBDO = Days between days off.
 * WPD  = Workers per day.
 * SEDO = Sequential  days off.
 * SODO = Solitary days off.
 * SOWD = Solitary work days.
 */
public abstract class Constraint {
    String name;
    int cost;
    ConstraintConfig cfg;
    Config appCfg;
    public ArrayList<Integer> costPerRow;

    public Constraint(ConstraintConfig cfg, Config appCfg) {
        name = cfg.getConstraintName();
        this.cfg = cfg;
        this.appCfg = appCfg;
        init();
    }


    abstract public void initializeCost (Chromosome chromosome);
    abstract public void updateCost (int employee, int day, List<List<Integer>> chromosome);

    public void setCost(int cost){this.cost = cost;}
    public int getCost () {
        return cost;
    }
    public String toString () {
        return name + " " + cost;
    }
    public ConstraintConfig getConstraintConfig(){
        return this.cfg;
    }
    private void init(){
        costPerRow = new ArrayList<>(Collections.nCopies(appCfg.getEmployees_amount(),0));
    }
}
