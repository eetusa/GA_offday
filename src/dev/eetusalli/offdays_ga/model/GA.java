package dev.eetusalli.offdays_ga.model;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.constraints.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GA {

    private Config cfg;
    private List<Chromosome> population;
    private Chromosome best_result;
    private Fitness best_fitness;

    public GA(Config cfg){
        this.cfg = cfg;
        this.best_fitness = new Fitness(cfg.getEmployees_amount());
        initializePopulation(-1);   // -1 for random seed
    }

    public void run(){
        int counter = 0;

        while (true){
            // Sort by fitness
            Collections.sort(population);
            Chromosome best = population.get(0);

            // if new best found
            if (best.getFitness().compareTo(best_fitness) == -1){
                System.out.println("Best: " + best.fitnessToString() + " on round " + counter);
                best_fitness.soft_breaks = best.getFitness().soft_breaks;
                best_fitness.hard_breaks = best.getFitness().hard_breaks;
                best_result.copyChromosome(best); // Create a 'fake' copy of the best found
            }

            // Cross-over **********************************************************************
            // Combine from top N chromosomes with weighted probability favoring better specimen
            // Hard coded to create two children.
            List<Integer> helper = new ArrayList<>();
            for (int i = 0; i < cfg.getCombine_from_best_of_n(); i++){
                for (int j = i; j < cfg.getCombine_from_best_of_n(); j++){
                    helper.add(i);
                }
            }
            int a;
            int b;
            int c;
            int d;
            while(true){
                a = ThreadLocalRandom.current().nextInt(0, helper.size());
                b = ThreadLocalRandom.current().nextInt(0, helper.size());
                c = ThreadLocalRandom.current().nextInt(0, helper.size());
                d = ThreadLocalRandom.current().nextInt(0, helper.size());
                if ( (helper.get(a) != helper.get(b)) && (helper.get(c) != helper.get(d)))  break;
            }

            if (ThreadLocalRandom.current().nextFloat() < cfg.getCross_p()){
                Chromosome A = population.get(helper.get(a));
                Chromosome B = population.get(helper.get(b));
                population.get(population.size()-1).makeAChildOf(A,B);
            }
            if (ThreadLocalRandom.current().nextFloat() < cfg.getCross_p()){

                Chromosome C = population.get(helper.get(c));
                Chromosome D = population.get(helper.get(d));
                population.get(population.size()-2).makeAChildOf(C,D);
            }
            // ********************************************************************************

            // Mutate *************************************
            if (cfg.isDo_mutate_whole_population()){
                for (int i = 0; i < population.size(); i++){
                    if (ThreadLocalRandom.current().nextFloat() < cfg.getMut_p()){
                        population.get(i).mutate();
                    }
                }
            } else{
                for (int i = 0; i < cfg.getCombine_n_children(); i++){
                    if (ThreadLocalRandom.current().nextFloat() < cfg.getMut_p()){
                        population.get(population.size()-1-i).mutate();
                    }
                }
            }
            // ********************************************


            // end condition (#END_TYPE second parameter (int) in configuration)
            if (counter >= cfg.getEnd_value()){
                System.out.println("Best: Hard: " + best_fitness.hard_breaks + ", soft: " + best_fitness.soft_breaks + ". End round " + counter);
                best_result.printChromosome();
                System.out.println(best_result.fitnessToString());
                best_result.setFitness();
                best_result.printConstraintCosts();
                break;
            }

            if (counter % 1000000 == 0){
                System.out.println(counter + "..");
                best.printConstraintCosts();
            }
            counter++;
        }
    }

    private void initializePopulation(long seed){
        System.out.println("Initializing population with seed " + seed + "..");
        population = new ArrayList<>();
        if (seed == -1){
            for (int i = 0; i < cfg.getPopulation_size(); i++){
                population.add(new Chromosome(cfg));
            }
        } else{
            for (int i = 0; i < cfg.getPopulation_size(); i++){
                population.add(new Chromosome(cfg, (int) Math.pow(seed, i)));
                System.out.println("Fitness: " + population.get(population.size()-1).fitnessToString());
            }
        }
        best_result = new Chromosome(cfg);
        System.out.println("Initialized population of " + population.size());

    }

}
