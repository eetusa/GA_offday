package dev.eetusalli.offdays_ga.constraints;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.List;

/**
 * Workers per day
 * How many workers required per day
 */

public class WPD  extends Constraint {
    private List<Integer> workers_per_day;
    public WPD(ConstraintConfig cfg, Config appCfg) {
        super (cfg, appCfg);
        workers_per_day = cfg.getValues();
    }

    @Override
    public void initializeCost(Chromosome chromosome) {
        int counter = 0;
        List<List<Integer>> m = chromosome.getChromosome();
        int total_days = m.get(0).size();
        for (int i = 0; i < total_days; i++){
            int worker_count_per_day = 0;
            for (int j = 0; j < m.size(); j++){
                worker_count_per_day += m.get(j).get(i);
            }
            counter += Math.abs(workers_per_day.get(i) - worker_count_per_day);
        }
        cost = counter;
    }

    @Override
    public void updateCost() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
