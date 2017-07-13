package joeltio.pulse.Fragments;

import android.graphics.Color;
import android.os.Bundle;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import joeltio.pulse.FixedQueue;
import joeltio.pulse.R;

public class GraphFragment extends OpenCVFragment {
    private CameraBridgeViewBase openCvCameraView;

    private FixedQueue<Double> brightnessValues;

    private int beats;
    private int iteration;
    private double currentBpm;
    private long sampleStart;

    private XYPlot xyPlot;

    private Double getRedMean(Mat rgbaFrame) {
        Vector<Mat> planes = new Vector<>();
        Core.split(rgbaFrame, planes);
        Mat redPlane = planes.get(0);
        return Core.mean(redPlane).val[0];
    }

    private void redrawGraph(Double[] newVals) {
        final XYSeries series = new SimpleXYSeries(
                Arrays.asList(newVals),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Brightness Values");
        final LineAndPointFormatter formatter =
                new LineAndPointFormatter(Color.YELLOW, Color.YELLOW, null, null);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                xyPlot.clear();
                xyPlot.addSeries(series, formatter);
                xyPlot.redraw();
            }
        });
    }

    private void updateBpm(final double bpm) {
        getActivity().runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        TextView bpmTextView =
                                (TextView) getActivity().findViewById(R.id.bpm_num_textView);
                        bpmTextView.setText(String.format(Locale.ENGLISH, "%.1f", bpm));
                    }
                }
        );
    }

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
        if (this.iteration == 11) {
            this.iteration = 0;
        }

        this.brightnessValues.add(getRedMean(inputFrame.rgba()));
        this.iteration += 1;

        Double[] vals = new Double[this.brightnessValues.size()];
        this.brightnessValues.copyToArray(vals);

        if (this.iteration == 10 && this.brightnessValues.size() > 0) {
            List<Double> lastFiveVals =
                    Arrays.asList(Arrays.copyOfRange(vals, vals.length-10, vals.length));
            Double max = Collections.max(lastFiveVals);
            Double min = Collections.min(lastFiveVals);
            if ((max - min) > 60) {
                this.beats += 1;

                if (this.beats == 1 && this.sampleStart == -1) {
                    this.sampleStart = System.nanoTime();
                }

                if (this.beats == 4) {
                    long now = System.nanoTime();
                    Long timeElapsedMillis = (now - this.sampleStart)/(1000*1000);
                    double newBpm = (3*1000*60)/(timeElapsedMillis.doubleValue());

                    if (newBpm > 40 && newBpm < 140) {
                        if (this.currentBpm != 0) {
                            this.currentBpm = (this.currentBpm + newBpm)/2;
                        } else {
                            this.currentBpm = newBpm;
                        }
                        updateBpm(this.currentBpm);
                    }

                    this.beats = 0;
                    this.sampleStart = now;
                }
            }
        }

        redrawGraph(vals);
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
        this.brightnessValues = new FixedQueue<>(50);
        this.iteration = 0;

        this.currentBpm = 0;
        this.sampleStart = -1;

        this.openCvCameraView =
                (CameraBridgeViewBase) getActivity().findViewById(R.id.graph_camera_view);
        this.openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        this.openCvCameraView.setAlpha(0);
        this.openCvCameraView.setCvCameraViewListener(this);

        this.xyPlot = (XYPlot) getActivity().findViewById(R.id.graph_plot);
    }
}
