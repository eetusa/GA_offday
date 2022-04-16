package dev.eetusalli.offdays_ga.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains configurations for a constraint
 */
public class ConstraintConfig {

    private String constraintName;
    private float multiplier;
    private String constraintType;
    int value = -1;

    List<Integer> values = new ArrayList<>();
    public List<Integer> getValues() {
        return values;
    }
    public void setValues(List<Integer> values) {
        this.values = new ArrayList<>(values);
    }

    public ConstraintConfig(String constraintName, String constraintType, float multiplier, int value){
        this.constraintName = constraintName;
        this.multiplier = multiplier;
        this.value = value;
        this.constraintType = constraintType;
        this.values = new ArrayList<>();
    }

    public ConstraintConfig(String constraintName, String constraintType, float multiplier){
        this.constraintName = constraintName;
        this.multiplier = multiplier;
        this.constraintType = constraintType;
        this.values = new ArrayList<>();
    }

    public String toString() {
        if (value != -1) {
            String str = String.format("Constraint: %s \ttype: %s\t val: %d\t multiplier: %f \t %s", constraintName, constraintType, value, multiplier, (isEnabled() ? "enabled" : "disabled"));
            return str;
        } else if (values.size() != 0){
            String vls = listToString(values);
            return String.format("Constraint: %s \ttype: %s\t\t\t multiplier: %f\t %s\t%s", constraintName, constraintType, multiplier, (isEnabled() ? "enabled" : "disabled"),vls);
        }else {
            return String.format("Constraint: %s \ttype: %s\t\t\t multiplier: %f\t %s", constraintName, constraintType, multiplier, (isEnabled() ? "enabled" : "disabled"));
        }
    }

    private String listToString(List<Integer> arr){
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < arr.size(); i++){
            if (i%7==0 && i != 0){
                temp.append("| ");
            }
            temp.append(arr.get(i));
            temp.append(" ");

        }
        return temp.toString();
    }

    public String getConstraintName() {
        return constraintName;
    }

    public boolean isEnabled() {
        return multiplier!=0;
    }

    public String getConstraintType() {
        return constraintType;
    }

    public int getValue() {
        return value;
    }

    public float getMultiplier() {
        return multiplier;
    }
}
