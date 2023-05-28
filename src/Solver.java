
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


    public int findUnit(int[] partialAssignment, int[] clause){
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



}
