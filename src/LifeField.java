public class LifeField {

    private int getNeighboursCount(int row, int col, int[][] matrix) {

        int res = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0)
                    continue;
                int row1 = (row + j + matrix.length) %  matrix.length;
                int col1 = (col + i + matrix[0].length) % matrix[0].length;
                if (matrix[row1][col1] == 1) {
                    res++;
                }
            }
        }
        return res;
    }



    public int[][] makeIter(int[][] matrix){
         int[][] field = new int[matrix.length][matrix[0].length];
         for (int row=0; row < field.length; ++row){
             for (int col=0; col < field[0].length; ++col) {
                 int s = getNeighboursCount(row, col, matrix);

                 //field[row][col] = (matrix[row][col] == 1) && (s == 2 || s == 3) ? 1 : 0;
                 //field[row][col] = (matrix[row][col] == 0) && (s == 3) ? 1 : 0;



                 if (matrix[row][col] == 1) {
                     if (s == 2 || s == 3) field[row][col] = 1;
                     else field[row][col] = 0;
                 }else if (field[row][col] == 0) {
                     if (s == 3) field[row][col]=1;
                     else field[row][col]=0;
                 }

             }
         }
         return field;
    }
}
