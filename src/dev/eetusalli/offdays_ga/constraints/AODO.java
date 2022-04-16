package dev.eetusalli.offdays_ga.constraints;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.List;

public class AODO extends Constraint {

    public AODO(ConstraintConfig cfg, Config appCfg) {
        super (cfg, appCfg);
    }

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
            costPerRow.set(i, Math.abs( appCfg.getEmployeeOffDays(i) - aodo ));
            offense_count += Math.abs( appCfg.getEmployeeOffDays(i) - aodo );
        }
        cost = offense_count;
    }

    @Override
    public void updateCost() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
