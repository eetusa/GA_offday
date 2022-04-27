package dev.eetusalli.offdays_ga;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.model.GA;

public class Main {

    public static void main(String[] args) {
        Config config = new Config("conf2.txt");
        config.printConfig();
        GA ga = new GA(config);
        ga.run();
    }
}
