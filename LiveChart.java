import java.util.ArrayList;
import java.util.Random;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import java.util.List;

/**
 * Creates a simple real-time chart
 */

public class LiveChart {

    private JLSA CHjlsa;
    int counter = 0;

    final XYChart chart;
    final XYChart chart2;
    final SwingWrapper<XYChart> sw;
    final SwingWrapper<XYChart> sw2;

    List<Double> time = new ArrayList<Double>();
    List<Double> output = new ArrayList<Double>();
    List<Integer> input = new ArrayList<Integer>();

    public LiveChart(JLSA jlsa) {
        CHjlsa = jlsa;

        SignalGenerator(CHjlsa);

        chart = QuickChart.getChart("RANDOM SIGNAL INPUT", "Time", "INPUT", "INPUT", time, input);
        chart2 = QuickChart.getChart("DISCRETIZED OUTPUT", "Time", "OUTPUT", "OUTPUT", time, output);
        sw = new SwingWrapper<XYChart>(chart);
        sw2 = new SwingWrapper<XYChart>(chart2);
        // Show it
        sw.displayChart();
        sw2.displayChart();
    }

    public void LiveChartDisplay() throws InterruptedException {
        while (true) {
            counter += 1;
            SignalGenerator(CHjlsa);

            Thread.sleep(20);

            chart.updateXYSeries("INPUT", time, input, null);
            chart2.updateXYSeries("OUTPUT", time, output, null);
            sw.repaintChart();
            sw2.repaintChart();
        }
    }

    private void SignalGenerator(JLSA jlsa) {

        int x_input = Randomizer();

        if (time.size() == 100) {
            time.remove(0);
            input.remove(0);
            output.remove(0);
        }

        time.add(new Double(counter * 0.1));
        input.add(x_input);
        output.add(10 * jlsa.LinearSimulationStepByStep(x_input, 0.1));

    }

    private int Randomizer() {
        Random rg = new Random();
        int result = (rg.nextInt(2) / 1);
        return result;
    }

}
