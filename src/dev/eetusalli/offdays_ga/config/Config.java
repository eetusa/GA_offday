package dev.eetusalli.offdays_ga.config;

import dev.eetusalli.offdays_ga.model.Employee;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to read the config file and manage settings for the application
 */
public class Config {

    private String filepath_to_employees;
    private int employees_amount;
    private int weeks_in_period;
    private int periods_amount;
    private List<ConstraintConfig> constraintConfigList = new ArrayList<>();
    private boolean isConfigValid;
    private int total_days;
    private int population_size;
    private float cross_p;
    private float mut_p;
    private String end_type;
    private int end_value;

    private List<Employee> employees = new ArrayList<>();
    private List<Integer> employee_offdays = new ArrayList<>();

    public Config(String path){
        isConfigValid = setSettingsFromFile(path);
        initializeEmployees();
        if (!isConfigValid){
            System.out.println("Error initiating config.");
            System.exit(-1);
        }
    }

    /**
     * Setup config-object from txt-file from path
     * @param path
     * @return true / false if successful setup
     */
    private boolean setSettingsFromFile(String path){
        System.out.println("Reading config file from " + path + "..");
        boolean readingWorkersEachDay = false;

        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();

            StringBuilder helper_sb = new StringBuilder();

            while (line != null) {
                try{

                    if (line.length() != 0){
                        char firstChar = line.charAt(0);
                        if (firstChar == '#'){
                            readingWorkersEachDay = false;
                            String[] parameter_and_value = (line.substring(1)).split(" ");
                            String parameter = parameter_and_value[0];
                            String value = parameter_and_value.length>1 ? parameter_and_value[1] : "";
                            switch(parameter){
                                case "EMPLOYEES":
                                    this.employees_amount = Integer.parseInt(value);
                                    break;
                                case "PERIODS":
                                    this.periods_amount = Integer.parseInt(value);
                                    break;
                                case "WEEKS_IN_PERIOD":
                                    this.weeks_in_period = Integer.parseInt(value);
                                    break;
                                case "EMPLOYEE_FILE":
                                    this.filepath_to_employees = value;
                                    break;
                                case "WORKERS_EACH_DAY":
                                    readingWorkersEachDay = true;
                                    break;
                                case "POPULATION":
                                    this.population_size = Integer.parseInt(value);
                                    break;
                                case "CROSS_P":
                                    this.cross_p = Float.parseFloat(value);
                                    break;
                                case "MUT_P":
                                    this.mut_p = Float.parseFloat(value);
                                    break;
                                case "END_TYPE":
                                    this.end_type = parameter_and_value[1];
                                    if (parameter_and_value.length > 2){
                                        this.end_value = Integer.parseInt(parameter_and_value[2]);
                                    }
                                    break;
                                default: // constraints
                                    if (parameter_and_value.length == 3){
                                        String constraintName = parameter.split(":")[1];
                                        String constraintType = parameter_and_value[1];
                                        boolean isEnabled = parameter_and_value[2].equals("1") ? true : false;
                                        float multiplier = Float.parseFloat(parameter_and_value[2]);
                                        constraintConfigList.add(new ConstraintConfig(constraintName, constraintType, multiplier));
                                    } else if (parameter_and_value.length == 4){
                                        String constraintName = parameter.split(":")[1];
                                        String constraintType = parameter_and_value[1];
                                        boolean isEnabled = parameter_and_value[2].equals("1") ? true : false;
                                        float multiplier = Float.parseFloat(parameter_and_value[2]);
                                        int confValue = Integer.parseInt(parameter_and_value[3]);
                                        constraintConfigList.add(new ConstraintConfig(constraintName, constraintType, multiplier, confValue));
                                    }


                            }
                        } else if (readingWorkersEachDay){ // getting worker per day
                            helper_sb.append(" ");
                            helper_sb.append(line);
                        }
                    }

                    line = br.readLine();
                    // Add required per day requirements to WPD-constraint from the end of the file.
                    if (line == null){
                        List<Integer> values = new ArrayList<>();
                        String[] helper = helper_sb.toString().split(" ");
                        ConstraintConfig wpd_cfg = null;
                        for (ConstraintConfig cfg : constraintConfigList){
                            if (cfg.getConstraintName().equals("WPD")){
                                wpd_cfg = cfg;
                            }
                        }
                        if (wpd_cfg != null && wpd_cfg.isEnabled()){
                            for (int i = 0; i < periods_amount; i++){
                                for (String h : helper){
                                    try{
                                        values.add(Integer.parseInt(h));
                                    } catch (Exception e){

                                    }
                                }
                            }

                            if ((weeks_in_period*7)*periods_amount != values.size() ) {
                                System.err.println("Error matching given workdays and amount of weeks/periods");
                                return false;
                            }
                            wpd_cfg.setValues(values);
                        }
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        total_days = getPeriods_amount() * weeks_in_period * 7;
        System.out.println("Config read and initialized.");
        return true;
    }


    private void initializeEmployees(){
        System.out.println("Reading employee file from " + filepath_to_employees + "..");
        try(BufferedReader br = new BufferedReader(new FileReader(filepath_to_employees))) {
            String line = br.readLine();
            while (line != null) {
                try {
                    String[] split = line.split(" ");
                    int id = Integer.parseInt(split[0].trim());
                    int off_days = Integer.parseInt(split[split.length-1]);
                    employees.add(new Employee(id, off_days));
                    employee_offdays.add(new Integer(off_days));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                line = br.readLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (employees.size() != employees_amount){
            System.out.println("Mismatch on employees actual size vs given in config");
            System.exit(-1);
        }
        System.out.println("Employees initialized.");
        System.out.println("Employee amount: " + employees.size());
    }

    /**
     * Prints initialized configs to console
     */
    public void printConfig(){
        System.out.println("=== PRINTING CONFIG ===");
        System.out.println("Amount of employees:\t\t" + employees_amount);
        System.out.println("Amount of periods:\t\t\t" + periods_amount);
        System.out.println("Weeks per period:\t\t\t" + weeks_in_period);
        System.out.println("Filepath employees:\t\t\t" + filepath_to_employees);
        System.out.println("Population:\t\t\t\t\t" + population_size);
        System.out.println("Crossover probability:\t\t" + cross_p);
        System.out.println("Mutation probability:\t\t" + mut_p);
        System.out.println("End type and value:\t\t\t" + end_type + " " + end_value);
        for (ConstraintConfig cfg : constraintConfigList){
            System.out.println(cfg.toString());
        }
        System.out.println("=======================");
    }

    public String getFilepath_to_employees() {
        return filepath_to_employees;
    }

    private void setFilepath_to_employees(String filepath_to_employees) {
        this.filepath_to_employees = filepath_to_employees;
    }

    public int getEmployees_amount() {
        return employees_amount;
    }

    private void setEmployees_amount(int employees_amount) {
        this.employees_amount = employees_amount;
    }

    public int getWeeks_in_period() {
        return weeks_in_period;
    }

    private void setWeeks_in_period(int weeks_in_period) {
        this.weeks_in_period = weeks_in_period;
    }

    public int getPeriods_amount() {
        return periods_amount;
    }

    private void setPeriods_amount(int periods_amount) {
        this.periods_amount = periods_amount;
    }

    public List<ConstraintConfig> getConstraintConfigList() {
        return constraintConfigList;
    }

    private void setConstraintConfigList(List<ConstraintConfig> constraintConfigList) {
        this.constraintConfigList = constraintConfigList;
    }

    public int getEmployeeOffDays(int index){
        return employee_offdays.get(index);
    }

    public int getPopulation_size(){
        return this.population_size;
    }

    public float getMut_p() {
        return mut_p;
    }

    public int getTotal_days(){
        return this.total_days;
    }

}
