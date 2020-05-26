import java.util.ArrayList;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;

/**
 *
 * @author Zeta
 */
public class JLSA {

    //      [MATH EXTENDED METHODS]
    //          [X To the power of Y]
    private double ToThePower(double base, double exp) {
        double pow = Math.pow(base, exp);
        return pow;
    }

    //          [SQUARE ROOT (SQRT)]
    private double Sqrt(double value) {
        double sq_rt = Math.sqrt(value);
        return sq_rt;
    }
//  ----------------------------------------------------------------------------

    //  [CONSTANTS]
    //  NOMBER e
    private final double e = Math.E;

    //  [VARIABLES]
    //      [TRANSFER FUNCTION PARAMETERS]
    //          SYSTEM NATURAL FREQUENCY
    private double sys_Wn = 0;
    //          VISCOSE DAMPING COEFFICIENT
    private double sys_Zeta = 0;
    //          INPUT SYSTEM GAIN
    private double sys_K = 0;

    //      [isVALID - TRANSFER FUNCTION] -> FOR CHECKING IF THE SIMULATION MAY
    //                                       GO THROUGH
    private boolean isValidTF = false;

    //      [TIMEN DELTA]
    private double delta_t = 0;

    //      [SIMULATION PARAETERS]
    private double sim_time[];
    private double sim_discretized_u_signal[];
    private double[] discretized_y_response;
    private ArrayList step_input;
    //  ************************************************************************

    //  [FUNCTIONS]
    //      [TRANSFER FUNCTIONS]
    //          [ESTANDARD TRANSFER FUNCTION]
    public void TransferFunction_Standard(double Wn, double Zeta, double K) {
        if (Wn <= 0 || K <= 0 || Zeta >= 1 || Zeta <= 0) {
            sys_Wn = 0;
            sys_Zeta = 0;
            sys_K = 0;
            isValidTF = false;
//            ("Parámetros estándar incorrectos",
//                    "Parámetros estándar incorrectos",
//                    "danger");
        } else {
            sys_Wn = Wn;
            sys_Zeta = Zeta;
            sys_K = K;
            isValidTF = true;
        }
    }

    //          [COEFFICIENTS TRANSFER FUNCTION]
    //  TRANSFER FUNCTION MADE BY COEFFS.
    public void TransferFunction_ByCoeffs(double num, double[] den) {
        //  CHECKING THE NUMBER OF DENOMINATOR COEFFS.
        if (den.length != 3) {
//            MiscFunctions.PromptMessage("Coeficientes incorrectos", "La función"
//                    + " de transferencia por método de coeficientes es:\n"
//                    + "              A\n"
//                    + "--------------------------- \n"
//                    + "  B s^2   +   C s   +   D", "danger");
            sys_Wn = 0;
            sys_Zeta = 0;
            sys_K = 0;
            isValidTF = false;
            //  CHECKING NON-ZERO S^2 VALUE
        } else if (den[0] != 0) {
            double S2_gain = den[0];
            sys_Wn = Sqrt(den[2]) / S2_gain;
            sys_Zeta = den[1] / (2 * sys_Wn * S2_gain);
            sys_K = num / (ToThePower(sys_Wn, 2) * S2_gain);
        } else {
//            MiscFunctions.PromptMessage("Coeficientes incorrectos", "El "
//                    + " coeficiente B no puede ser 0"
//                    + " en un sistema de Segundo Orden", "danger");
            sys_Wn = 0;
            sys_Zeta = 0;
            sys_K = 0;
            isValidTF = false;
        }
        isValidTF = true;
    }

    //      [OUTPUT RESPONSE FROM U VALUE INPUT]
    //  TF RESPONSE (STEP INPUT)
    public double SecondOrderStepResponse(double u, double t) {
        //  DAMPING VALUE DEFINITION
        double damping = Sqrt(1 - ToThePower(sys_Zeta, 2));
        //  RESPONSE
        double so_step_response = u - u * ((ToThePower(e, -sys_Zeta * sys_Wn
                * t)) * (Math.cos(sys_Wn * damping * t) + (sys_Zeta / damping)
                * Math.sin(sys_Wn * damping * t)));
        return so_step_response;
    }

    //      [RESPONSE LINEAR SIMULATION FROM INPUT SIGNALS]
    public void LinearSimulation(double[] input, double[] time,
            double delta_time) {
        if (isValidTF == true) {
            //  CHECKING IF TIME IS IN POSITIVE RANK (DELTA t > 0) 
            if (!(time[0] >= time[1])) {
                sim_time = time;
                sim_discretized_u_signal = input;
                delta_t = delta_time;
                InputConvolution();
            } else {
//                MiscFunctions.PromptMessage("Valores temporales incorrectos",
//                        "t0 no puede ser nunca mayor o igual que t1",
//                        "danger");
            }
        } else {
//            MiscFunctions.PromptMessage("Función de Transferencia incorrecta",
//                    "No se ha definido una función de transferencia correcta",
//                    "danger");
        }
    }

    //          [LINEAR SIMULATION FROM A STEP-BY-STEP INPUT]
    public double LinearSimulationStepByStep(double input, double delta_time) {
        if (step_input instanceof ArrayList == false) {
            step_input = new ArrayList();
        }
        double u1;
        double u0;
        double u;

        step_input.add(input);
        int input_n = step_input.size();
        double step_output = 0;

        for (int i = 0; i < input_n; i++) {
            u1 = (double) step_input.get(i);
            u = u1;
            if (i > 0) {
                u0 = (double) step_input.get(i - 1);

                if (u1 != u0) {
                    u = (u1 - u0);
                    step_output += SecondOrderStepResponse(u, (input_n - i) * delta_time);
                } else {
                    step_output += 0;
                }
            } else {
                step_output += SecondOrderStepResponse(u, (input_n - i) * delta_time);
            }

        }
        return step_output;
    }

    //      [MATHEMATICAL RESOURCES]
    //          [CONVOLUTION THEOREM]    
    private void InputConvolution() {
        double simulationTime = sim_time[1] - sim_time[0];
        int n_iterations = 100;
        double y0[][] = new double[n_iterations][n_iterations];

        for (int i = 0; i < n_iterations; i++) {
            double u = 0;
            double u1 = sim_discretized_u_signal[i];

            u = u1;
            if (i > 0) {
                double u0 = sim_discretized_u_signal[i - 1];
                if (u1 != u0) {
                    u = u1 - u0;
                    for (int j = 0; j < n_iterations; j++) {
                        if (j < i) {
                            y0[i][j] = 0;
                        } else {
                            double t = (j - i) * delta_t;
                            y0[i][j] = SecondOrderStepResponse(u, t);
                        }
                    }
                } else {
                    for (int j = i; j < n_iterations; j++) {
                        y0[i][j] = 0;
                    }
                }
            } else {
                for (int j = 0; j < n_iterations; j++) {
                    double t = (j - i) * delta_t;
                    y0[i][j] = SecondOrderStepResponse(u, t);
                }
            }
        }
        discretized_y_response = ColumnOutputReader(y0, n_iterations);
    }

    //          [DATA]    
    //  CONVOLUTION INTERATIONS
    private double[] ColumnOutputReader(double y0[][], int n_iterations) {
        double[] y = new double[n_iterations];
        double col_sum = 0;
        for (int i = 0; i < n_iterations; i++) {
            for (int j = 0; j < n_iterations; j++) {
                col_sum += y0[j][i];
            }
            y[i] = col_sum;
            col_sum = 0;
        }
        y0 = null;
        return y;
    }

    //      [RESULT PLOTTING]
    //          [OUTPUT SIGNAL IN DISCRETIZED FULL INPUT]
    public double[] GetOutputResponse() {
        return discretized_y_response;
    }

    public void PlotResponse() {
        int time_positions = discretized_y_response.length;
        double[] x = new double[time_positions];
        double[] x2 = new double[time_positions];
        double simulationTime = sim_time[1] - sim_time[0];
        for (int i = 0; i < discretized_y_response.length; i++) {
            x[i] = sim_time[0] + (i / simulationTime);
            x2[i] = sim_discretized_u_signal[i];
        }
        Plotter plotter = new Plotter();
        XYChart chart = plotter.getChartStatic(x, x2, discretized_y_response);
        new SwingWrapper<XYChart>(chart).displayChart();
    }
    //  ************************************************************************
}

class Plotter {

    private XYChart chart;

    public Plotter() {
        // Create Chart
        chart = new XYChartBuilder().width(800).height(600).title(getClass().getSimpleName()).xAxisTitle("X").yAxisTitle("Y").build();
        // Customize Chart
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setYAxisLabelAlignment(Styler.TextAlignment.Right);
        chart.getStyler().setYAxisDecimalPattern("#,###.##");
        chart.getStyler().setPlotMargin(0);
        chart.getStyler().setPlotContentSize(.95);
    }

    public XYChart getChartStatic(double[] x, double[] y_input, double[] y_output) {
        chart.addSeries("INPUT", x, y_input);
        chart.addSeries("OUTPUT", x, y_output);
        return chart;
    }

    public XYChart getChartStepByStep(double[] x, double[] y_input, double[] y_output) {
        chart.addSeries("INPUT", x, y_input);
        chart.addSeries("OUTPUT", x, y_output);
        return chart;
    }
}
