package dev.eetusalli.offdays_ga.model;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.config.ConstraintConfig;
import dev.eetusalli.offdays_ga.constraints.*;
import dev.eetusalli.offdays_ga.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Chromosome implements Comparable<Chromosome>{

    private Config cfg;
    private List<Constraint> constraints = new ArrayList<>();
    private List<List<Integer>> chromosome = new ArrayList<>();
    private int employee_size;
    private int total_days;
    private Fitness fitness;

    public List<List<Integer>> getChromosome(){
        return this.chromosome;
    }

    /**
     * Constructor for a random chromosome
     * @param cfg
     */
    public Chromosome(Config cfg){
        this.cfg = cfg;
        this.employee_size = cfg.getEmployees_amount();
        this.total_days = cfg.getTotal_days();
        fitness = new Fitness(employee_size);
        initializeConstraints();
        initializeRandomChromosome(-1);
        setFitness();
    }

    /**
     * Constructor for a chromosome generated from a seed number
     * @param cfg
     * @param seed
     */
    public Chromosome(Config cfg, int seed){
        this.cfg = cfg;
        this.employee_size = cfg.getEmployees_amount();
        initializeConstraints();
        initializeRandomChromosome(seed);
        setFitness();
    }

    /**
     * Poor man's copy constructor
     * Doesn't actually work since constraints aren't copied or initialized, only used to track best chromosome
     */
    public Chromosome(Chromosome other){
        this.cfg = other.cfg;
        this.chromosome = new ArrayList<>();
        this.employee_size = cfg.getEmployees_amount();
        this.total_days = cfg.getTotal_days();
        fitness = new Fitness(cfg.getEmployees_amount());
        for (List<Integer> row : other.getChromosome()){
            this.chromosome.add(row);
        }
    }



    /**
     * Method to call when 'repurposing' an old chromosome to make a child of two parents
     * @param A Parent_a
     * @param B Barent_b
     */
    public void makeAChildOf(Chromosome A, Chromosome B){
        if (ThreadLocalRandom.current().nextFloat() < 0.05){
            this.chromosome = combineParents(A,B);  // random combination (vaiko day combine tähän?
        } else {
            this.chromosome = combineParentsPerBestRow(A, B);   // weighted combination to value fitness per row
        }
        setFitness();  // re-establish fitness
    }

    /**
     * Create a matrix (chromosome) out of two parents.
     * Combines employees (rows) from best fitness rows from each
     * @param A Parent_a
     * @param B Parent_b
     * @return Resulting child chromosome
     */
    private List<List<Integer>> combineParentsPerBestRow(Chromosome A, Chromosome B){
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> fitnessRowCompare = Util.getFitRowOrder(A,B);
        for (int i = 0; i < employee_size; i++){
            if (fitnessRowCompare.get(i) == 0){
                result.add(new ArrayList<>());
                for (int j = 0; j < total_days; j++){
                    result.get(i).add(new Integer(A.getChromosome().get(i).get(j)));
                }
            } else{
                result.add(new ArrayList<>());
                for (int j = 0; j < total_days; j++){
                    result.get(i).add(new Integer(B.getChromosome().get(i).get(j)));
                }
            }
        }
        return result;
    }

    /**
     * Create a matrix (chromosome) out of two parents.
     * Combines employees (rows) randomly half and half
     * @param A Parent_a
     * @param B Parent_b
     * @return Resulting child chromosome
     */
    private List<List<Integer>> combineParents(Chromosome A, Chromosome B){
        List<List<Integer>> result = new ArrayList<>();

        int parent_A_counter = 0;
        int parent_B_counter = 0;

        for (int i = 0; i < employee_size; i++){
            boolean addToParentA = ThreadLocalRandom.current().nextBoolean();
            Chromosome current_parent;
            if (addToParentA){
                if (parent_A_counter < employee_size / 2){
                    current_parent = A;
                    parent_A_counter++;
                } else {
                    current_parent = B;
                    parent_B_counter++;
                }
            }  else{
                if (parent_B_counter < employee_size / 2 +  1){
                    current_parent = B;
                    parent_B_counter++;
                } else{
                    current_parent = A;
                    parent_A_counter++;
                }
            }
            result.add(new ArrayList<>());
            for (int j = 0; j < total_days; j++){
                result.get(i).add(new Integer(current_parent.getChromosome().get(i).get(j)));
            }
        }
        return result;
    }

    private List<List<Integer>> combineParentsDays(Chromosome A, Chromosome B){
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < employee_size; i++){
            result.add(new ArrayList<Integer>(Collections.nCopies(total_days, 0)));
        }

        int parent_A_counter = 0;
        int parent_B_counter = 0;

        for (int i = 0; i < total_days; i++){
            boolean addToParentA = ThreadLocalRandom.current().nextBoolean();
            Chromosome current_parent;
            if (addToParentA){
                if (parent_A_counter < total_days / 2){
                    current_parent = A;
                    parent_A_counter++;
                } else {
                    current_parent = B;
                    parent_B_counter++;
                }
            }  else{
                if (parent_B_counter < total_days / 2 +  1){
                    current_parent = B;
                    parent_B_counter++;
                } else{
                    current_parent = A;
                    parent_A_counter++;
                }
            }
            for (int j = 0; j < employee_size; j++){
                chromosome.get(j).set(i, new Integer(current_parent.getChromosome().get(j).get(i)));
            }

        }
        return result;
    }


    // not ready
    public void updateFitness(){
        for (Constraint c : constraints){
            c.updateCost();
            float total = c.getConstraintConfig().getMultiplier() * c.getCost();
            System.out.println("Constraint " + c.getConstraintConfig().getConstraintName() + " cost: " + c.getCost() + " and total cost: " + total);
            if (c.getConstraintConfig().getConstraintType().equals("H")) fitness.hard_breaks += total;
            else fitness.soft_breaks += total;
        }
        System.out.println("Fitness set. Hard breaks: " + fitness.hard_breaks + ", soft breaks: " + fitness.soft_breaks);
    }


    /**
     * Swap work and off days between two random employees and days
     */
    public void mutate(){
        if (ThreadLocalRandom.current().nextFloat() < 0.01){
            // find random employee and day


            // swap it with random employee and day (that isn't the same) that has opposite value of isWorkday

            int e;
            int o_e;
            int d = ThreadLocalRandom.current().nextInt(0, total_days);

            while (true){
                e = ThreadLocalRandom.current().nextInt(0, employee_size);
                o_e = ThreadLocalRandom.current().nextInt(0, employee_size);
                if (e != o_e) break;
            }


            int ed_value = chromosome.get(e).get(d);
            int oed_value = chromosome.get(o_e).get(d);



            chromosome.get(e).set(d, oed_value);
            chromosome.get(o_e).set(d, ed_value);


        } else {
            int e = ThreadLocalRandom.current().nextInt(0, employee_size);
            int d = ThreadLocalRandom.current().nextInt(0, total_days);
            int ed_value = chromosome.get(e).get(d);

            chromosome.get(e).set(d, Math.abs(ed_value - 1));

            if (ThreadLocalRandom.current().nextFloat() < 0.05){
                e = ThreadLocalRandom.current().nextInt(0, employee_size);
                d = ThreadLocalRandom.current().nextInt(0, total_days);
                ed_value = chromosome.get(e).get(d);

                chromosome.get(e).set(d, Math.abs(ed_value - 1));
            }
        }

        setFitness();





    }

    public void setFitness(Fitness fit){
        this.fitness.soft_breaks = fit.soft_breaks;
        this.fitness.hard_breaks = fit.hard_breaks;
        this.fitness.per_row.clear();
        this.fitness.per_row.addAll(fit.per_row);
    }


    public void setFitness(){
        fitness.soft_breaks = 0;
        fitness.hard_breaks = 0;
        //   result.add(new ArrayList<Integer>(Collections.nCopies(total_days, 0)));
        fitness.per_row = new ArrayList<Float>(Collections.nCopies(employee_size,0f));
        for (Constraint c : constraints){
            c.initializeCost(this);
            float total = c.getConstraintConfig().getMultiplier() * c.getCost();

            // Tracking fitness per row. Cheated by just using a multiplier of 10 to weight up hard constraint costs.
            if (c.getConstraintConfig().getConstraintType().equals("H")){
                fitness.hard_breaks += total;
                for (int i = 0; i < c.costPerRow.size(); i++){
                    fitness.per_row.set(i, new Float(fitness.per_row.get(i) + c.costPerRow.get(i) * c.getConstraintConfig().getMultiplier() * 10));
                }

            }
            else{
                fitness.soft_breaks += total;
                for (int i = 0; i < c.costPerRow.size(); i++){
                    fitness.per_row.set(i, new Float(fitness.per_row.get(i) + c.costPerRow.get(i) * c.getConstraintConfig().getMultiplier()));
                }
            }
        }
    }

    private void initializeRandomChromosome(long seed){
        Random rand = new Random();
        if (seed != -1) rand.setSeed(seed);

        List<Integer> values = new ArrayList<>();
        for (Constraint c : constraints){
            if (c.getConstraintConfig().getConstraintName().equals("WPD")) values = c.getConstraintConfig().getValues();
        }
        for (int i = 0; i < employee_size; i++){
            chromosome.add(new ArrayList<>());
            for (int j = 0; j < total_days; j++){
                chromosome.get(i).add(0);
            }
        }
        for (int i = 0; i < total_days;){
            if (currentlyPerDay(i) == values.get( i )){
                i++;
            } else{
                int e;
                if (seed != -1){
                    e = rand.nextInt(employee_size);
                } else {
                    e = ThreadLocalRandom.current().nextInt(0, employee_size); // ThreadLocalRandom better for parallel computation
                }
                chromosome.get(e).set(i, 1);
               // System.out.println("Value on day " +i + " on employee " + e +": " + chromosome.get(e).get(i));
              //  printChromosome();
            }
        }

    }

    public void printConstraintCosts(){
        System.out.println();
        for (Constraint c : constraints){
            System.out.print(c.getConstraintConfig().getConstraintName() + ": " + c.getCost() + ", ");
        }
        System.out.println();
    }

    private int currentlyPerDay(int day){
        int result = 0;
        for (int i = 0; i < chromosome.size(); i++){
            if (day < chromosome.get(i).size()){
                result += chromosome.get(i).get(day);
            }
        }
        return result;
    }

    public void printChromosome(){
        for (int i = 0; i < employee_size; i++){
            System.out.print("ID " + i +"\t ");
            for (int j = 0; j < total_days; j++){
                System.out.print(chromosome.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }

    private void initializeConstraints(){
        for (ConstraintConfig constraint_cfg : cfg.getConstraintConfigList()){
            if (constraint_cfg.isEnabled()){
                switch(constraint_cfg.getConstraintName()){
                    case "WPD":
                        constraints.add(new WPD(constraint_cfg, cfg));
                        break;
                    case "DBDO":
                        constraints.add(new DBDO(constraint_cfg, cfg));
                        break;
                    case "SEDO":
                        constraints.add(new SEDO(constraint_cfg, cfg));
                        break;
                    case "SODO":
                        constraints.add(new SODO(constraint_cfg, cfg));
                        break;
                    case "SOWD":
                        constraints.add(new SOWD(constraint_cfg, cfg));
                        break;
                    case "AODO":
                        constraints.add(new AODO(constraint_cfg, cfg));
                        break;
                }
            }
        }

    }

    @Override
    public int compareTo(Chromosome other){
        return this.fitness.compareTo(other.fitness);
    }

    /**
     * For testing mutate etc.
     */
    public void printTotalWorkdays(){
        int total = 0;
        for (List<Integer> list : chromosome){
            for (Integer value : list){
                if (value == 1) total++;
            }
        }
        System.out.println("Total workdays: " + total);
    }

    public String fitnessToString(){
        return "Hard: " + fitness.hard_breaks + ", soft: " + fitness.soft_breaks +".";
    }

    public Fitness getFitness(){
        return this.fitness;
    }

    public void printFitnessPerRow(){
        for (int i = 0; i < fitness.per_row.size(); i++){
            System.out.println(i + ": " + fitness.per_row.get(i));
        }
    }

}
