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

//    private List<Employee> employees = new ArrayList<>();
//    private int employee_size;
//    private int total_days;
    private List<Chromosome> population;
    private Chromosome best_result;
    private Fitness best_fitness;

    public GA(Config cfg){
        this.cfg = cfg;
        this.best_fitness = new Fitness(cfg.getEmployees_amount());
        initializePopulation(-1);
    }

    public void run(){
        int counter = 0;
        int combine_from_n_best = 6;

        while (true){



            Collections.sort(population);

            int A_int;
            int B_int;
            int C_int;
            int D_int;

            List<Integer> helper = new ArrayList<>();
            for (int i = 0; i < combine_from_n_best; i++){
                for (int j = 0; j < (combine_from_n_best-i); j++){
                    helper.add(i);
                }
            }

            while (true){
                A_int = ThreadLocalRandom.current().nextInt(0, helper.size());
                B_int = ThreadLocalRandom.current().nextInt(0, helper.size());
                C_int = ThreadLocalRandom.current().nextInt(0, helper.size());
                D_int = ThreadLocalRandom.current().nextInt(0, helper.size());

                if ((helper.get(A_int) != helper.get(B_int)) && (helper.get(C_int) != helper.get(D_int))) break;
            }
            Chromosome A = population.get(helper.get(A_int));
            Chromosome B = population.get(helper.get(B_int));
            Chromosome C = population.get(helper.get(C_int));
            Chromosome D = population.get(helper.get(D_int));

            if (counter % 1000000 == 0){
                System.out.println(counter + "..");
                A.printChromosome();

            }

            if (A.getFitness().compareTo(best_fitness) == -1){
                System.out.println("Best: " + A.fitnessToString() + " on round " + counter);
                best_fitness.soft_breaks = A.getFitness().soft_breaks;
                best_fitness.hard_breaks = A.getFitness().hard_breaks;
             //   A.printFitnessPerRow();
                best_result = new Chromosome(A);
                best_result.setFitness(A.getFitness());
            //    best_result.printFitnessPerRow();
              //  System.exit(12);
//                if (best_fitness.hard_breaks <= 1 && best_fitness.soft_breaks <= 120){
//                    A.printChromosome();
//                }
            }
           // Chromosome C = population.get(population.size()-1);
          //  System.out.println("C before: " + population.get(population.size()-1).fitnessToString());
            population.get(population.size()-1).makeAChildOf(A,B);
            population.get(population.size()-2).makeAChildOf(C,D);
       //     System.out.println("C after: " + population.get(population.size()-1).fitnessToString());
            if (ThreadLocalRandom.current().nextFloat() < cfg.getMut_p()){
               // System.out.println("Mutating1..");
                population.get(population.size()-1).mutate();
            }
            if (ThreadLocalRandom.current().nextFloat() < cfg.getMut_p()){
                // System.out.println("Mutating1..");
                population.get(population.size()-2).mutate();
            }
            for (int i = 0; i < population.size()-1; i++){
                if (ThreadLocalRandom.current().nextFloat() < cfg.getMut_p()){
                 //   System.out.println("Mutating2..");
                    population.get(i).mutate();
                }
            }
//            if (counter > 3000000){
//                System.out.println("Best: " + A.fitnessToString() + " on round " + counter);
//                A.printChromosome();
//                A.printFitnessPerRow();
//                break;
//            }

            counter++;
            if (counter >= 1000000){
                System.out.println("Best: Hard: " + best_fitness.hard_breaks + ", soft: " + best_fitness.soft_breaks + ". End round " + counter);
                best_result.printChromosome();
                System.out.println(best_result.fitnessToString());
                best_result.printFitnessPerRow();
                best_result.printConstraintCosts();
                break;
            }
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
        System.out.println("Initialized population of " + population.size());

    }

}
