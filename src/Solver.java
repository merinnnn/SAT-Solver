
public class Solver {
    private int [][] clauseDatabase = null;
    private int numberOfVariables = 0;

    public boolean checkClauseDatabase (int[] assignment, int[][] clauseDatabase){
        for (int[] clause : clauseDatabase){
            boolean endLoop = true;

            for (int i=0; i< clause.length; i++){
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


    public int checkClausePartial (int[] partialAssignment, int[] clause) {
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
        int[] assignment = new int[numberOfVariables +1];

        if (clauseDatabase.length == 0)
            return assignment;

        for (int[] clause : clauseDatabase){
            if (clause.length == 0){
                return null;
            }
        }

        return null;
    }


    public boolean DPLL (int[] assignment){

        if (checkClauseDatabase(assignment, clauseDatabase)){
            return true;
        }

        for (int[] clause : clauseDatabase){
            if (checkClausePartial(assignment, clause) == -1){
                return false;
            }
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

                for (int[] clause : clauseDatabase){
                    if (checkClausePartial(assignment, clause) == -1){
                        return false;
                    }
                }
            }
        }

        

    }


}
