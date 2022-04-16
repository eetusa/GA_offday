package dev.eetusalli.offdays_ga.constraints;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.List;

/**
 * Sequential Days Off
 * How many sequential days off between workdays (max)
 */

public class SEDO extends Constraint {
    public SEDO(ConstraintConfig cfg, Config appCfg) { super (cfg, appCfg); }

    @Override
    public void initializeCost(Chromosome chromosome) {
        List<List<Integer>> m = chromosome.getChromosome();
        int offense_count = 0;
        for (int i = 0; i < m.size(); i++){
            int sedo = 0;
            costPerRow.set(i, 0);
            for (int j = 0; j < m.get(i).size(); j++){
                boolean isWorkDay = (m.get(i).get(j) == 1) ? true : false;
                if (!isWorkDay) sedo++;
                else sedo = 0;
                if (sedo > cfg.getValue()){
                    offense_count++;
                    costPerRow.set(i, costPerRow.get(i)+1);
                }
            }
        }
        cost = offense_count;    }

    @Override
    public void updateCost() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
