package dev.eetusalli.offdays_ga.constraints;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.ArrayList;
import java.util.List;

/**
 * Workers per day
 * How many workers required per day
 */

public class WPD  extends Constraint {
    private List<Integer> workers_required_per_day;
    private List<Integer> workers_per_day;
    public WPD(ConstraintConfig cfg, Config appCfg) {
        super (cfg, appCfg);
        workers_required_per_day = cfg.getValues();
        workers_per_day = new ArrayList<>();
    }

    @Override
    public void initializeCost(Chromosome chromosome) {
        int counter = 0;
        List<List<Integer>> m = chromosome.getChromosome();
        int total_days = m.get(0).size();
        workers_per_day = new ArrayList<>();
        for (int i = 0; i < total_days; i++){
            int worker_count_per_day = 0;
            for (int j = 0; j < m.size(); j++){
                worker_count_per_day += m.get(j).get(i);
            }
            workers_per_day.add(worker_count_per_day);
            counter += Math.abs(workers_required_per_day.get(i) - worker_count_per_day);
        }
        cost = counter;
    }

    /**
     * Checks the old amount of workers per day and updates it accordingly.
     * As all updates, implicitly assumes the value has changed on the given row/column.
     * @param employee
     * @param day
     * @param chromosome
     */
    @Override
    public void updateCost(int employee, int day, List<List<Integer>> chromosome) {
        int required_amount  = workers_required_per_day.get(day);
        int old_amount = workers_per_day.get(day);

        int new_amount;
        int new_day_value = chromosome.get(employee).get(day);

        if (new_day_value == 1){
            new_amount = old_amount + 1;
        } else {
            new_amount = old_amount - 1;
        }
        workers_per_day.set(day, new_amount);
        if (Math.abs(required_amount-new_amount) < Math.abs(required_amount-old_amount)){
            cost--;
        } else {
            cost++;
        }
    }
}
