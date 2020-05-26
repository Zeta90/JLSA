/**
 *
 * @author Zeta
 */

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RUN {

    public static void main(String[] args) {
        JLSA jlsa = new JLSA();
        double[] u = SignalGenerator();
        double[] t = TimeGenerator();
        
        System.out.println(Arrays.toString(t));
        System.out.println(Arrays.toString(u));

        double[] den = {1, 1, 3};
        jlsa.TransferFunction_ByCoeffs(1, den);
        jlsa.LinearSimulation(u, t, 0.1);
        jlsa.GetOutputResponse();
        jlsa.PlotResponse();
        
        LiveChart live = new LiveChart(jlsa);
        try {
            live.LiveChartDisplay();
        } catch (InterruptedException ex) {
            Logger.getLogger(RUN.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static double[] TimeGenerator() {
        Random rg = new Random();
        double[] result = new double[100];
        for (int j = 0; j < 100; j++) {
            result[j] = new Double(j) / 10;
        }
        return result;
    }

    private static double[] SignalGenerator() {
        Random rg = new Random();
        double[] result = new double[100];
        for (int i = 0; i < 100; i++) {
            result[i] = (new Double(rg.nextInt(20))/10);
        }
        return result;
    }
}
