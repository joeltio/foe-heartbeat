package joeltio.pulse.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Vector;

import joeltio.pulse.R;

public class GraphFragment extends OpenCVFragment {
    private CameraBridgeViewBase openCvCameraView;

    private Deque<Double> brightnessValues;
    private int numValuesShown;

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
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Vector<Mat> planes = new Vector<>();
        Core.split(inputFrame.rgba(), planes);

        if (this.brightnessValues.size() == numValuesShown) {
            this.brightnessValues.remove();
        }
        this.brightnessValues.add(Core.mean(planes.get(0)).val[0]);

        Number[] vals = this.brightnessValues.toArray(new Number[this.brightnessValues.size()]);

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
        this.numValuesShown = 50;
        this.brightnessValues = new ArrayDeque<>();

        this.openCvCameraView =
                (CameraBridgeViewBase) getActivity().findViewById(R.id.graph_camera_view);
        this.openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        this.openCvCameraView.setAlpha(0);
        this.openCvCameraView.setCvCameraViewListener(this);

        this.xyPlot = (XYPlot) getActivity().findViewById(R.id.graph_plot);
        this.formatter = new LineAndPointFormatter(Color.YELLOW, Color.YELLOW, null, null);
    }
}
