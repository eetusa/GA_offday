package dev.eetusalli.offdays_ga.constraints;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.List;

/**
 * Solitary Work Days
 * How many workdays that have an off day before and after (minimized)
 * Counts solitary days from beginning and end of the period as well.
 */

public class SOWD extends Constraint{
    public SOWD(ConstraintConfig cfg, Config appCfg) { super (cfg, appCfg); }

    @Override
    public void initializeCost(Chromosome chromosome) {
        List<List<Integer>> m = chromosome.getChromosome();
        int offense_count = 0;
        for (int i = 0; i < m.size(); i++){
            costPerRow.set(i, 0);
            boolean previous_isWorkDay;
            boolean current_isWorkDay;
            boolean next_isWorkDay;
            for (int j = 0; j < m.get(i).size(); j++){

                if (j == 0){
                    previous_isWorkDay = false;
                    current_isWorkDay = (m.get(i).get(j) == 1) ? true : false;
                    next_isWorkDay = (m.get(i).get(j+1) == 1) ? true : false;
                } else if (j == m.get(i).size()-1) {
                    previous_isWorkDay = (m.get(i).get(j-1) == 1) ? true : false;
                    current_isWorkDay = (m.get(i).get(j) == 1) ? true : false;
                    next_isWorkDay = false;
                }else{
                    previous_isWorkDay = (m.get(i).get(j-1) == 1) ? true : false;
                    current_isWorkDay = (m.get(i).get(j) == 1) ? true : false;
                    next_isWorkDay = (m.get(i).get(j+1) == 1) ? true : false;
                }

                if (!previous_isWorkDay && current_isWorkDay && !next_isWorkDay){
                    offense_count++;
                    costPerRow.set(i, costPerRow.get(i)+1);
                }
            }
        }
        cost = offense_count;
    }

    /**
     * Separately handles if the new value is a work day or and off day.
     * Checks neighbouring days as necessary in each case and updates the costs.
     * @param employee
     * @param day
     * @param chromosome
     */
    @Override
    public void updateCost(int employee, int day, List<List<Integer>> chromosome) {
        int row_cost = costPerRow.get(employee);
        int new_row_cost = row_cost;
        List<Integer> row = chromosome.get(employee);
        int new_day_value = row.get(day);

        if (new_day_value == 1){ // given row/column is now a work day
            if (isSolitaryWorkDay(row, day)){
                new_row_cost++;
            } else{
                if (isWorkDay(row, day - 1 ) && !isWorkDay(row, day - 2) ){
                    new_row_cost--;
                }
                if (isWorkDay(row, day + 1) && !isWorkDay(row, day + 2)){
                    new_row_cost--;
                }
            }
        } else { // given row/column is now an off day
            if (isSolitaryWorkDay(row, day - 1 )){
                new_row_cost++;
            }
            if (isSolitaryWorkDay(row, day + 1)){
                new_row_cost++;
            }
            if (!isWorkDay(row, day - 1) && !isWorkDay(row, day+1)){
                new_row_cost--;
            }
        }

        costPerRow.set(employee, new_row_cost);
        cost = cost + (new_row_cost - row_cost);
    }

    private boolean isWorkDay(List<Integer> row, int day){
        if (day <= -1 || day >= row.size()) return false;
        return row.get(day) == 1 ? true : false;
    }

    private boolean isSolitaryWorkDay(List<Integer> row, int day){
        if (day <= -1 || day >= row.size()) return false;

        boolean previous_isWorkDay;
        boolean current_isWorkDay;
        boolean next_isWorkDay;
        current_isWorkDay = (row.get(day) == 1) ? true : false;
        if (day == 0){
            previous_isWorkDay = false;
            next_isWorkDay = (row.get(day + 1) == 1) ? true : false;
        } else if (day == row.size()-1){
            previous_isWorkDay = (row.get(day - 1) == 1) ? true : false;
            next_isWorkDay = false;
        } else {
            previous_isWorkDay = (row.get(day - 1) == 1) ? true : false;
            next_isWorkDay = (row.get(day + 1) == 1) ? true : false;
        }

        if (!previous_isWorkDay && current_isWorkDay && !next_isWorkDay){
            return true;
        }

        return false;
    }
}
