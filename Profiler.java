import java.security.Timestamp;
import java.sql.Time;
import java.util.Date;
import java.util.function.Function;

public class Profiler {

    static long TotalTime;
    static int CallCount;
    static long MaxTime;
    static long MinTime;

    public static String[] analyse(Function<Character, String[]> oneMethod, char p) {
        long debut = timestamp();
        String[] res = oneMethod.apply(p);
        long fin = timestamp();
        CallCount++;
        if (MaxTime < (fin - debut)) {
            MaxTime = (fin - debut);
        }
        if (MinTime > (fin - debut)) {
            MinTime = (fin - debut);
        }
        TotalTime = TotalTime + (fin - debut);

        return res;

    }

    public static void init() {
        CallCount = 0;
        TotalTime = 0;
        MaxTime = 0;
        MinTime = 1000000000000000000L;
    }

    public static String getTotalTime() {

        double elapsed = (TotalTime) / 1e9;

        return " temps de calcul Total : " + elapsed + " s";

    }

    public static String getMoyenneTime() {

        double elapsed = (TotalTime) / 1e9;

        elapsed = elapsed / CallCount;
        return "temps de calcul Moyen : " + elapsed + " s";

    }

    public static String getMaxTime() {

        double elapsed = (MaxTime) / 1e9;

        return "temps de calcul Max : " + elapsed + " s";

    }

    public static String getMinTime() {

        double elapsed = (MinTime) / 1e9;

        return "temps de calcul Min : " + elapsed + " s";

    }

    public static int getCallCount() {
        return CallCount;
    }

    /**
     * Si clock0 est >0, retourne une chaîne de caractères
     * représentant la différence de temps depuis clock0.
     * 
     * @param clock0 instant initial
     * @return expression du temps écoulé depuis clock0
     */
    public static String timestamp(long clock0) {
        String result = null;

        if (clock0 > 0) {
            double elapsed = (System.nanoTime() - clock0) / 1e9;
            String unit = "s";
            if (elapsed < 1.0) {
                elapsed *= 1000.0;
                unit = "ms";
            }
            result = String.format("%.4g%s elapsed", elapsed, unit);
        }
        return result;
    }

    /**
     * retourne l'heure courante en ns.
     * 
     * @return
     */
    public static long timestamp() {
        return System.nanoTime();
    }
}