package dev.eetusalli.offdays_ga.constraints;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.List;

/**
 * Solitary days off
 * Amount of off days that have a workday before and after (minimized)
 */

public class SODO extends Constraint{
    public SODO(ConstraintConfig cfg, Config appCfg) {
        super (cfg, appCfg);
    }

    @Override
    public void initializeCost(Chromosome chromosome) {
        List<List<Integer>> m = chromosome.getChromosome();
        int offense_count = 0;
        for (int i = 0; i < m.size(); i++){
            costPerRow.set(i, 0);
            for (int j = 1; j < m.get(i).size()-1; j++){
                boolean previous_isWorkDay = (m.get(i).get(j-1) == 1) ? true : false;
                boolean current_isWorkDay = (m.get(i).get(j) == 1) ? true : false;
                boolean next_isWorkDay = (m.get(i).get(j+1) == 1) ? true : false;
                if (previous_isWorkDay && !current_isWorkDay && next_isWorkDay){
                    offense_count++;
                    costPerRow.set(i, costPerRow.get(i)+1);
                }
            }
        }
        cost = offense_count;
    }

    @Override
    public void updateCost() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
