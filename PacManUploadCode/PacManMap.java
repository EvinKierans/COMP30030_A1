import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;

/**
 *
 * @author Neil Hurley
 * Code to generate a two-dimensional NxM grid of junctions. 
 * Each junction is surrounded by at most 4 other junctions, to the LEFT, 
 * RIGHT, ABOVE and BELOW it. 
 * A row of four numbers is output for each junction. These numbers correspond
 * to the junctions, LEFT, RIGHT, ABOVE and BELOW it.  If the junction does not
 * exist, then -1 is output.
 * A certain percentage of links between junctions are chosen at random and
 * blocked, so that the robot cannot travel directly between these junctions. 
 */
public class PacManMap {

    static int N = 20;
    static int M = 20;
    static double percentblocks = 0.1;//
    static boolean permute = false;

    // names for the four directions in the grid (with method to convert
    // to a value when necessary 
    enum Direction {
        LEFT(0), RIGHT(1), ABOVE(2), BELOW(3);
        private final int value;
        private Direction(int val) {
            value = val;
        }

        public int getValue() {
            return value;
        }
    }

    static class Gridlink {

        public int junc;
        public Direction dir;

        public Gridlink(int j, Direction d) {
            junc = j;
            dir = d;
        }

        // if this corresponds to the link joining junction A to B, then
        // this.symlink() is the link joining B to A
        public Gridlink symlink() {
            Direction symd = Direction.LEFT;
            int symi = -1, symj = -1;
            int i = junc / M;
            int j = junc % M;
            switch (dir) {
                case LEFT:
                    symd = Direction.RIGHT;
                    symi = i;
                    symj = (j - 1);
                    break;
                case RIGHT:
                    symd = Direction.LEFT;
                    symi = i;
                    symj = j + 1;
                    break;
                case ABOVE:
                    symd = Direction.BELOW;
                    symi = i - 1;
                    symj = j;
                    break;
                case BELOW:
                    symd = Direction.ABOVE;
                    symi = i + 1;
                    symj = j;
            }
            if (symi < 0 || symi >= N || symj < 0 || symj >= M) {
                return null;
            }
            return new Gridlink(M * symi + symj, symd);
        }

    }

    static public void randPerm(int[] P, int size) {
        int i, k;
        int tmp;

        for (i = 0; i < size; i++) {
            P[i] = i;
        }
        for (i = size - 1; i >= 0; i--) {
            k = (int) Math.floor(Math.random() * i);
            tmp = P[k]; P[k] = P[i]; P[i] = tmp;
        }
        return;
    }

    public static void main(String[] args) {

        int[][] D = new int[N][M];
        int[][] C = new int[N * M][Direction.values().length];
        int[] P = new int[N * M];
        int i, j, k, p;
        // set up a permutation array to permute the names of the
        // junctions if required
        if (permute) {
            randPerm(P, N * M);
        } else {
            for (i = 0; i < N * M; i++) {
                P[i] = i;
            }
        }
        // associate each (i,j) co-ordinate of the grid with a 
        // junction name
        for (i = 0; i < N; i++) {
            for (j = 0; j < M; j++) {
                D[i][j] = P[i * M + j];
            }
        }
        // initialise C[][], which will hold for each junction the set
        // of junctions that it links to, one junction in each of the link
        // directions
        for (i = 0; i < N * M; i++) {
            for (Direction d: Direction.values()) {
                C[i][d.getValue()] = 0;
            }
        }
        // loop through each junction and initialise its links to the junctions
        // surrounding it
        for (i = 0; i < N; i++) {
            for (j = 0; j < M; j++) {

                k = P[i * M + j];

                if (j == 0) {
                    C[k][Direction.LEFT.getValue()] = -1;
                } else {
                    C[k][Direction.LEFT.getValue()] = D[i][j - 1];
                }

                if (j == M - 1) {
                    C[k][Direction.RIGHT.getValue()] = -1;
                } else {
                    C[k][Direction.RIGHT.getValue()] = D[i][j + 1];
                }

                if (i == 0) {
                    C[k][Direction.ABOVE.getValue()] = -1;
                } else {
                    C[k][Direction.ABOVE.getValue()] = D[i - 1][j];
                }

                if (i == N - 1) {
                    C[k][Direction.BELOW.getValue()] = -1;
                } else {
                    C[k][Direction.BELOW.getValue()] = D[i + 1][j];
                }

            }
        }
        // Finally, randomly block some of the links. A link will be
        // blocked with probability percentblock.
        for (i = 0; i < N; i++) {
            for (j = 0; j < M; j++) {
                for (Direction d : Direction.values()) {
                    Gridlink g = new Gridlink(i * M + j, d);
                    // find the backward link from the neighbour back to the
                    // current node
                    Gridlink symg = g.symlink();
                    if (Math.random() < percentblocks) {
                        // block the link to the neighbouring junction
                        C[P[g.junc]][d.getValue()] = -1;
                        // also block the backward link,back to current junction
                        if (symg != null) {
                            C[P[symg.junc]][symg.dir.getValue()] = -1;
                        }
                    }
                }
            }
        }

        PrintStream fp;
        try {
            fp = new PrintStream(new FileOutputStream(new File("PacManMap.txt"), false));
        } catch (IOException e) {
            fp = System.out;

        }
        //PrintStream fp = System.out;
        fp.println(N * M);
        for (j = 0; j < N * M; j++) {
            for (Direction d : Direction.values()) {
                fp.print(C[j][d.getValue()] + " ");
            }
            fp.println();
        }        
        return;
    }
}
