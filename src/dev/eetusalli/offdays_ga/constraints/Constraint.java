package dev.eetusalli.offdays_ga.constraints;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This is the base class for constraints. It is quite simple
 * because the constraints are simple :).
 *
 * Think carefully what kind of data structure you should use.
 * That data structure should be used in all the constraints.
 * Remember that the data structure can also be an abstract class
 * (or interface).
 *
 * Also remember that you do not have to follow my instructions.
 * You can always choose some other way to solve the problem but you
 * must explain your solution to me.
 */
public abstract class Constraint {
    String name;
    int cost;
    ConstraintConfig cfg;
    Config appCfg;
    public ArrayList<Integer> costPerRow;
    int[][] lol;

    public Constraint(ConstraintConfig cfg, Config appCfg) {
        name = cfg.getConstraintName();
        this.cfg = cfg;
        this.appCfg = appCfg;
        init();
        lol = new int[appCfg.getEmployees_amount()][appCfg.getTotal_days()];
    }

    // These two methods should have parameters that the calculations are made from.
    // But how would the parameters look like?
    abstract public void initializeCost (Chromosome chromosome);
    abstract public void updateCost ();

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
