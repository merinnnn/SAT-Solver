import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Solver {
    private int [][] clauseDatabase = null;
    private int numberOfVariables = 0;

    public boolean checkClauseDatabase (int[] assignment, int[][] clauseDatabase){
        for (int[] clause : clauseDatabase){
            boolean endLoop = true;

            for (int i=0; i<clause.length; i++){
                int literal = Math.abs(clause[i]);
                if (assignment[literal] == Integer.signum(clause[i])){
                    endLoop = false;
                    break;
                }
            }

            if (endLoop) {
                return false;
            }
        }

        return true;
    }


    public int checkUNSAT(int[] partialAssignment, int[] clause) {
        boolean unknown = false;

        for (int i=0; i<clause.length; i++){
            int literal = Math.abs(clause[i]);
            if (partialAssignment[literal] == Integer.signum(clause[i])){
                return 1;
            } else if (partialAssignment[literal] == 0) {
                unknown = true;
            }
        }

        return unknown ? 0 : -1;
    }



    public boolean checkUNSAT(int[] partialAssignment, int[][] clauseDatabase) {
        for (int[] clause : clauseDatabase){
            if (checkUNSAT(partialAssignment, clause) == -1){
                return false;
            }
        }

        return true;
    }


    public int findUnit (int[] partialAssignment, int[] clause){
        int count = 0;
        int unknownLiteral = 0;

        for (int i=0; i<clause.length; i++){
            int literal = Math.abs(clause[i]);
            if (partialAssignment[literal] == Integer.signum(clause[i])){
                return 0;
            } else if (partialAssignment[literal] == 0) {
                unknownLiteral = clause[i];
                count++;

                if (count > 1){
                    return 0;
                }
            }
        }

        return count == 1 ? unknownLiteral : 0;
    }


    public boolean containsLiteral (int[] clause, int literal){
        for (int i=0; i<clause.length; i++){
            if (clause[i] == literal || clause[i] == -literal){
                return true;
            }
        }

        return false;
    }

    public void assignTrue (int[] assignment){
        for (int i=1; i<assignment.length; i++){
            if (assignment[i] == 0){
                assignment[i] = 1;
            }
        }
    }

    public int[] SATSolver (int[][] clauseDatabase){
        int[] assignment = new int[numberOfVariables + 1];

        if (clauseDatabase.length == 0)
            return assignment;

        if (!checkUNSAT(assignment, clauseDatabase) )
            return null;

        return DPLL(assignment) ? assignment : null;
    }


    public boolean DPLL (int[] assignment){

        if (checkClauseDatabase(assignment, clauseDatabase)){
            return true;
        }

        if (!checkUNSAT(assignment, clauseDatabase)){
                return false;
        }

        boolean assignmentModified = true;
        while (assignmentModified){
            int literal;
            assignmentModified = false;

            for (int[] clause : clauseDatabase){
                literal = findUnit(assignment, clause);
                if (literal != 0 && assignment[Math.abs(literal)] == 0){
                    assignment[Math.abs(literal)] = Integer.signum(literal);
                    assignmentModified = true;
                } else if (literal != 0 && assignment[Math.abs(literal)] != 0) {
                    return false;
                }
            }

            if (assignmentModified){
                if (checkClauseDatabase(assignment, clauseDatabase)) {
                    assignTrue(assignment);
                    return true;
                }

                if (!checkUNSAT(assignment, clauseDatabase)){
                    return false;
                }

            }
        }


        int index = 0;
        int frequent = 0;
        for (int i=1; i<assignment.length; i++){
            if (assignment[i] == 0){
                int count = 0;
                for (int[] clause : clauseDatabase){
                    if (checkUNSAT(assignment, clause) == 0 && containsLiteral(clause, i)){
                        count++;
                    }
                }

                if (count > frequent){
                    index = i;
                    frequent = count;
                }
            }
        }

        int[] assignmentTrue = assignment.clone();
        assignmentTrue[index] = 1;

        int[] assignmentFalse = assignment.clone();
        assignmentFalse[index] = -1;

        boolean dpllResult = DPLL(assignmentTrue);
        if (dpllResult){
            for (int i=1; i<assignment.length; i++){
                assignment[i] = assignmentTrue[i];
            }
            assignTrue(assignment);
            return true;
        }


        dpllResult = DPLL(assignmentFalse);
        if (dpllResult){
            for (int i=1; i<assignment.length; i++){
                assignment[i] = assignmentFalse[i];
            }
            assignTrue(assignment);
            return true;
        }

        return false;

    }


    public static void main(String[] args) {
        try {
            Solver mySolver = new Solver();

            System.out.println("Enter the file to check");

            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            String fileName = br.readLine();

            int returnValue = 0;

            Path file = Paths.get(fileName);
            BufferedReader reader = Files.newBufferedReader(file);
            returnValue = mySolver.runSatSolver(reader);

            return;

        } catch (Exception e){
            System.err.println("Solver failed");
            e.printStackTrace(System.err);
            return;
        }
    }


    public int runSatSolver (BufferedReader reader) throws Exception, IOException {

        loadDimacs(reader);

        int[] assignment = SATSolver(clauseDatabase);

        if (assignment == null) {
            System.out.println("UNSATISFIABLE");
            return 20;
        } else {
            boolean checkResult = checkClauseDatabase(assignment, clauseDatabase);

            if (!checkResult) {
                throw new Exception("Assignment is not correct");
            }

            System.out.println("SATISFIABLE");

            if (assignment.length != numberOfVariables + 1) {
                throw new Exception("Assignment should have one element per variable.");
            }
            if (assignment[0] != 0) {
                throw new Exception("The first element of an assignment must be zero.");
            }
            for (int i = 1; i <= numberOfVariables; ++i) {
                if (assignment[i] == 1 || assignment[i] == -1) {
                    System.out.println(i * assignment[i]);
                } else {
                    throw new Exception("assignment[" + i + "] should be 1 or -1, is " + assignment[i]);
                }
            }

            return 10;
        }

    }



    public void loadDimacs (BufferedReader reader) throws Exception, IOException{
        int numberOfClauses = 0;

        do {
            String line = reader.readLine();

            if (line == null){
                throw new Exception("Found end of file before a header?");
            } else if (line.startsWith("c")) {
                continue;
            } else if (line.startsWith("p cnf")) {
                String counters = line.substring(6);
                int split = counters.indexOf(" ");
                numberOfVariables = Integer.parseInt(counters.substring(0, split));
                numberOfClauses = Integer.parseInt(counters.substring(split + 1));

                if (numberOfVariables <= 0){
                    throw new Exception("Variables should be positive");
                }

                if (numberOfClauses < 0) {
                    throw new Exception("Negative number of clauses?");
                }

                break;

            } else {
                throw new Exception("Unexpected line");
            }
        } while (true);

        clauseDatabase = new int[numberOfClauses][];

        for (int i=0; i < numberOfClauses; ++i) {
            String line = reader.readLine();

            if (line == null) {
                throw new Exception("Unexpected end of file before clauses have been parsed");
            } else if (line.startsWith("c")) {
                // Comment; skip
                --i;
                continue;
            } else {
                // Try to parse as a clause
                ArrayList<Integer> tmp = new ArrayList<Integer>();
                String working = line;

                do {
                    int split = working.indexOf(" ");

                    if (split == -1) {
                        // No space found so working should just be
                        // the final "0"
                        if (!working.equals("0")) {
                            throw new Exception("Unexpected end of clause string : \"" + working + "\"");
                        } else {
                            // Clause is correct and complete
                            break;
                        }
                    } else {
                        int var = Integer.parseInt(working.substring(0,split));

                        if (var == 0) {
                            throw new Exception("Unexpected 0 in the middle of a clause");
                        } else {
                            tmp.add(var);
                        }

                        working = working.substring(split + 1);
                    }
                } while (true);

                // Add to the clause database
                clauseDatabase[i] = new int[tmp.size()];
                for (int j = 0; j < tmp.size(); ++j) {
                    clauseDatabase[i][j] = tmp.get(j);
                }
            }
        }

        // All clauses loaded successfully!
        return;
    }

}



