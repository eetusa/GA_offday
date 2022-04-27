package dev.eetusalli.offdays_ga.constraints;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.List;

/**
 * Amount of Days Off per worker
 */
public class AODO extends Constraint {

    public AODO(ConstraintConfig cfg, Config appCfg) {
        super (cfg, appCfg);
    }

    /**
     * Cost comes from how many days off (per row) are off from the desired amount (always positive, no matter if
     * there are too many or too little days off).
     * Cost per row are saved in a value that can be negative or positive, negative telling there fewer days than wanted
     * and positive telling there are too many off days.
     */
    @Override
    public void initializeCost(Chromosome chromosome) {
        List<List<Integer>> m = chromosome.getChromosome();
        int offense_count = 0;
        for (int i = 0; i < m.size(); i++){
            int aodo = 0;
            for (int j = 0; j < m.get(i).size(); j++){
                boolean isWorkDay = (m.get(i).get(j) == 1) ? true : false;
                if (!isWorkDay) aodo++;
            }
            costPerRow.set(i, appCfg.getEmployeeOffDays(i) - aodo );
            offense_count += Math.abs( appCfg.getEmployeeOffDays(i) - aodo );
        }
        cost = offense_count;
    }


    /**
     * Determine if the new cost per row value (employee = row) is better than the old one
     * and update it and the total cost accordingly
     */
    @Override
    public void updateCost(int employee, int day, List<List<Integer>> chromosome) {
        int row_cost = costPerRow.get(employee);
        int new_day_value = chromosome.get(employee).get(day);
        int new_row_cost = (new_day_value == 0 ? row_cost - 1 : row_cost + 1);
        costPerRow.set(employee, new_row_cost);
        if (Math.abs(new_row_cost) < Math.abs(row_cost)) {
            cost = cost - 1;
        } else {
            cost = cost + 1;
        }
    }
}
