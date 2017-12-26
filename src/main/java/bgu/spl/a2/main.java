package bgu.spl.a2;

import bgu.spl.a2.sim.Simulator;
import bgu.spl.a2.sim.Warehouse;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class main {
    public static void main(String[] args) {
        // THIS TEST WILL RUN THE TEST FOR 100 TIMES IN A ROW
        for(int i = 0; i < 200; i++){
            String[] a = new String[0];
            try {
                IterativeTest.main(a);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
