package joeltio.pulse.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

import joeltio.pulse.FixedQueue;
import joeltio.pulse.R;

public class GraphFragment extends OpenCVFragment {
    public static final String ARGUMENT_MEAN_VALUE = "joeltio.pulse.Fragments.GraphFragment.ARGUMENT_MEAN_VALUE";

    private CameraBridgeViewBase openCvCameraView;
    private ImageView heartImage;

    private FixedQueue<Double> brightnessValues;

    private int beats;
    private boolean upBeat;
    private double currentBpm;
    private double meanValue;
    private Long sampleStart;

    private XYPlot xyPlot;

    private Double getRedMean(Mat rgbaFrame) {
        Vector<Mat> planes = new Vector<>();
        Core.split(rgbaFrame, planes);
        Mat redPlane = planes.get(0);
        return Core.mean(redPlane).val[0];
    }

    private void bounceImage(ImageView imageView) {
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(imageView, "scaleX", 1.1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(imageView, "scaleY", 1.1f);
        scaleUpX.setDuration(80);
        scaleUpY.setDuration(80);

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imageView, "scaleX", 1.0f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imageView, "scaleY", 1.0f);
        scaleDownX.setDuration(80);
        scaleDownY.setDuration(80);

        AnimatorSet bounceOut = new AnimatorSet();
        AnimatorSet bounceDown = new AnimatorSet();

        bounceOut.play(scaleUpX).with(scaleUpY);
        bounceDown.play(scaleDownX).with(scaleDownY).after(bounceOut);

        bounceDown.start();
    }

    private void redrawGraph(Double[] newVals) {
        final XYSeries series = new SimpleXYSeries(
                Arrays.asList(newVals),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Brightness Values");
        final LineAndPointFormatter formatter =
                new LineAndPointFormatter(getResources().getColor(R.color.colorGraphLine), null, null, null);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                xyPlot.clear();
                xyPlot.getGraph().setDomainGridLinePaint(null);
                xyPlot.getGraph().setDomainOriginLinePaint(null);
                xyPlot.getGraph().setRangeGridLinePaint(null);
                xyPlot.getGraph().setRangeOriginLinePaint(null);
                xyPlot.getLegend().setVisible(false);
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
                        TextView bpmTextView = getActivity().findViewById(R.id.bpm_num_textView);
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
        Double frameValue = getRedMean(inputFrame.rgba());
        this.brightnessValues.add(frameValue);

        Double[] vals = new Double[this.brightnessValues.size()];
        this.brightnessValues.copyToArray(vals);
        redrawGraph(vals);

        if (frameValue > (20 + this.meanValue)) {
            this.upBeat = true;
        } else if (this.upBeat && (frameValue < (2 + this.meanValue))) {
            this.beats += 1;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bounceImage(heartImage);
                }
            });

            if (this.beats == 1) {
                this.sampleStart = System.nanoTime();
            } else if (this.beats == 4) {
                double timeTaken = (System.nanoTime()-this.sampleStart)/3;
                double newBpm = (60*Math.pow(10, 9))/timeTaken;

                if (this.currentBpm == 0) {
                    this.currentBpm = newBpm;
                } else {
                    this.currentBpm = (this.currentBpm + newBpm)/2;
                }

                this.beats = 0;
                updateBpm(this.currentBpm);
            }

            this.upBeat = false;
        }

        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        this.meanValue = getArguments().getDouble(ARGUMENT_MEAN_VALUE);
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.brightnessValues = new FixedQueue<>(50);

        this.upBeat = false;
        this.currentBpm = 0;
        this.sampleStart = null;

        this.heartImage = getActivity().findViewById(R.id.imageView_heart);

        this.openCvCameraView = getActivity().findViewById(R.id.graph_camera_view);
        this.openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        this.openCvCameraView.setAlpha(0);
        this.openCvCameraView.setCvCameraViewListener(this);

        this.xyPlot = getActivity().findViewById(R.id.graph_plot);
        this.xyPlot.setBackgroundPaint(null);
        this.xyPlot.getGraph().setBackgroundPaint(null);
        this.xyPlot.getGraph().setGridBackgroundPaint(null);
        this.xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM)
                .getPaint().setColor(Color.TRANSPARENT);
        this.xyPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT)
                .getPaint().setColor(Color.TRANSPARENT);
    }
}
