package bgu.spl.a2;

import bgu.spl.a2.sim.Simulator;
import bgu.spl.a2.sim.Warehouse;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class main {
    public static void main(String[] args) {
        // THIS TEST WILL RUN THE SIMULATOR FOR 100 TIMES IN A ROW
        for(int i = 0; i < 100; i++){
            String[] a = {"src/main/java/bgu/spl/a2/sim/input.txt"};
            try {
                IterativeTest.main(a);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
