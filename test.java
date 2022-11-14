public class test {
    public static void main(String[] args)
    {
        int n = 7;
        int nombreCroix = n;
        int nombreEspace = 0;
        
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= nombreCroix; j++) {
                System.out.print(' ');
            }
            for (int k = 1; k <= nombreEspace; k++) {
                System.out.print('X');
            }

            for (int l = 1; l <= nombreCroix; l++) {
                System.out.print(' ');
            }
            nombreCroix--;
            System.out.println();

            nombreEspace += 2;
        }

        int nombreCroix2 = 1;
        int nombreEspace2 = (n * 2) - 2;
        for (int z = 1; z <= n; z++) {

            for (int m = 1; m <= nombreCroix2; m++) {
                System.out.print(' ');
            }
            for (int p = 1; p <= nombreEspace2; p++) {
                System.out.print('X');
            }
            for (int e = 1; e <= nombreCroix2; e++) {
                System.out.print(' ');
            }
            nombreCroix2++;
            System.out.println();
            nombreEspace2 -= 2;
        }
    }
}