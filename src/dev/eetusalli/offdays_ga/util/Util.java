package dev.eetusalli.offdays_ga.util;

import dev.eetusalli.offdays_ga.model.Chromosome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Util {

    public static List<Integer> getFitRowOrder(Chromosome A, Chromosome B){

        List<Float> a_rows = A.getFitness().per_row;
        List<Float> b_rows = B.getFitness().per_row;
        List<FitRow> fitrows = new ArrayList<>();

        List<Integer> result  = new ArrayList<>(Collections.nCopies(a_rows.size(),-1));


        for (int i = 0; i < a_rows.size(); i++){
            fitrows.add(new FitRow(a_rows.get(i), true, i));
            fitrows.add(new FitRow(b_rows.get(i), false, i));
        }

        Collections.sort(fitrows);
//        System.out.println("Fit sorted");
//        for (int i = 0; i < fitrows.size(); i++){
//            System.out.println(i + ": " + fitrows.get(i).fit);
//        }

        int a_counter = 0;
        int b_counter = 0;
        for (int i = 0; i < fitrows.size(); i++){
            FitRow row = fitrows.get(i);
            int index = row.index;
            if (result.get(index) != -1) continue;

            if (row.isA){
                if (a_counter < a_rows.size() / 2){
                    result.set(index, 0);
                    a_counter++;
                } else {
                    result.set(index, 1);
                    b_counter++;
                }
            } else{
                if (b_counter < b_rows.size() / 2){
                    result.set(index, 1);
                    b_counter++;
                } else {
                    result.set(index, 0);
                    a_counter++;
                }
            }
        }

        return result;

    }
}
