
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

    





}
