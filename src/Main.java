import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        for (int i=1; i<=15; i++){
            System.out.println("-----------"+i+"-----------");
            long start = System.nanoTime();
            Main.go(i);
            System.out.println("Completed in: "+(System.nanoTime()-start)/1000000000f);
        }
    }



    public static void go(int i){
        try {
            Solver mySolver = new Solver();

            String index = "";
            if (i<10){
                index = "0"+i;
            } else {
                index = ""+i;
            }
            String f = "data/220028896-clause-database-"+index+".cnf";

            int returnValue = 0;

            Path file = Paths.get(f);
            BufferedReader reader = Files.newBufferedReader(file);
            returnValue = mySolver.runSatSolver(reader);

            return;

        } catch (Exception e) {
            System.err.println("Solver failed :-(");
            e.printStackTrace(System.err);
            return;

        }
    }
}
