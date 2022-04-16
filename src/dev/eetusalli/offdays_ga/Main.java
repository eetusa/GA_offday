package dev.eetusalli.offdays_ga;

import dev.eetusalli.offdays_ga.config.Config;
import dev.eetusalli.offdays_ga.model.GA;

public class Main {

    /**
     * Always make a method for initializing the constraint. That method should
     * initialize the cost from scratch, i.e. calculate everything from start
     * to end.
     *
     * When you make the updater you should run it for some time. If it seems to
     * work ok, you should use the initializer to check that it produces the same
     * result as the updater. This is the most important thing to do.
     *
     * The above is because the updater can be very hard to validate. It can also
     * be very tricky to code. In my experience it is the most difficult part of
     * making the program.
     */
    public static void main(String[] args) {
        Config config = new Config("conf2.txt");
        config.printConfig();
        GA ga = new GA(config);
        ga.run();
    }
}
