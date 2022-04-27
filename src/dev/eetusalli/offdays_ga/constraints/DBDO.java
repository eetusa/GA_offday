package dev.eetusalli.offdays_ga.constraints;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.List;

/**
 * Days Between Days off
 * How many workdays between days off (max)
 */
public class DBDO extends Constraint {

    public DBDO(ConstraintConfig cfg, Config appCfg) {
        super (cfg, appCfg);
    }

    @Override
    public void initializeCost(Chromosome chromosome) {
        List<List<Integer>> m = chromosome.getChromosome();
        int offense_count = 0;
        for (int i = 0; i < m.size(); i++){
            int dbdo = 0;
            costPerRow.set(i, 0);
            for (int j = 0; j < m.get(i).size(); j++){
                boolean isWorkDay = (m.get(i).get(j) == 1) ? true : false;
                if (isWorkDay) dbdo++;
                else dbdo = 0;
                if (dbdo > cfg.getValue()){
                    costPerRow.set(i, costPerRow.get(i)+1);
                    offense_count++;
                }
            }
        }
        cost = offense_count;
    }

    /**
     * Simply recounts the cost of the whole row.
     * @param employee
     * @param day
     * @param chromosome
     */
    @Override
    public void updateCost(int employee, int day, List<List<Integer>> chromosome) {
        int row_cost = costPerRow.get(employee);
        int dbdo = 0;
        int new_row_cost = 0;
        for (int j = 0; j < appCfg.getTotal_days(); j++){
            boolean isWorkDay = (chromosome.get(employee).get(j) == 1) ? true : false;
            if (isWorkDay) dbdo++;
            else dbdo = 0;
            if (dbdo > cfg.getValue()){
                new_row_cost++;
            }
        }
        costPerRow.set(employee, new_row_cost);
        cost = cost + (new_row_cost - row_cost);

    }
}
