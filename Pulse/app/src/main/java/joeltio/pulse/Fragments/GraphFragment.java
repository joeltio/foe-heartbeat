package joeltio.pulse.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import joeltio.pulse.R;

public class GraphFragment extends OpenCVFragment {
    private CameraBridgeViewBase openCvCameraView;

    private Deque<Double> brightnessValues;
    private int numValuesShown;

    private int beats;
    private int iteration;

    private Handler handler;
    private Timer timer;

    private XYPlot xyPlot;
    private XYSeries series;
    private LineAndPointFormatter formatter;

    public GraphFragment() {
        super("OpenCV:GraphFragment");
    }

    @Override
    protected CameraBridgeViewBase getOpenCvCameraView() {
        return openCvCameraView;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {}

    @Override
    public void onCameraViewStopped() {}

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (this.brightnessValues.size() == this.numValuesShown) {
            this.brightnessValues.remove();
        }
        if (this.iteration == 11) {
            this.iteration = 0;
        }

        Vector<Mat> planes = new Vector<>();
        Core.split(inputFrame.rgba(), planes);
        this.brightnessValues.add(Core.mean(planes.get(0)).val[0]);
        this.iteration += 1;

        Double[] vals = this.brightnessValues.toArray(new Double[this.brightnessValues.size()]);

        if (this.iteration == 10 && this.brightnessValues.size() > 0) {
            List<Double> lastFiveVals =
                    Arrays.asList(Arrays.copyOfRange(vals, vals.length-10, vals.length));
            Double max = Collections.max(lastFiveVals);
            Double min = Collections.min(lastFiveVals);
            if ((max - min) > 60) {
                this.beats += 1;
            }
        }

        series = new SimpleXYSeries(
                Arrays.asList(vals),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Brightness Values");

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                xyPlot.clear();
                xyPlot.addSeries(series, formatter);
                xyPlot.redraw();
            }
        });

        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.brightnessValues = new ArrayDeque<>();
        this.iteration = 0;

        this.openCvCameraView =
                (CameraBridgeViewBase) getActivity().findViewById(R.id.graph_camera_view);
        this.openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        this.openCvCameraView.setAlpha(0);
        this.openCvCameraView.setCvCameraViewListener(this);

        this.numValuesShown = 50;
        this.xyPlot = (XYPlot) getActivity().findViewById(R.id.graph_plot);
        this.formatter = new LineAndPointFormatter(Color.YELLOW, Color.YELLOW, null, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.handler = new Handler();
        this.timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        TextView bpmValue =
                                (TextView) getActivity().findViewById(R.id.bpm_num_textView);
                        bpmValue.setText(Integer.toString(beats));
                        beats = 0;
                    }
                });
            }
        };

        timer.schedule(task, 0, 60*1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }
}
