package joeltio.pulse.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.github.lzyzsd.circleprogress.ArcProgress;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.Vector;

import joeltio.pulse.CalibrateActivity;
import joeltio.pulse.R;

public class CalibrateFragment extends OpenCVFragment {
    private CameraBridgeViewBase openCvCameraView;
    private Double meanValue;
    private int iteration;

    public CalibrateFragment() {
        super("OpenCV:CalibrateFragment");
    }

    @Override
    protected CameraBridgeViewBase getOpenCvCameraView() { return this.openCvCameraView; }

    @Override
    public void onCameraViewStarted(int width, int height) {}

    @Override
    public void onCameraViewStopped() {}

    private Double getRedMean(Mat rgbaFrame) {
        Vector<Mat> planes = new Vector<>();
        Core.split(rgbaFrame, planes);
        Mat redPlane = planes.get(0);
        return Core.mean(redPlane).val[0];
    }

    private void setProgress(int percent) {
        ((ArcProgress) getActivity().findViewById(R.id.calibrate_progress)).setProgress(percent);
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (meanValue == -1) {
            meanValue = getRedMean(inputFrame.rgba());
        } else {
            this.meanValue = (this.meanValue + getRedMean(inputFrame.rgba()))/2;
        }

        this.iteration += 1;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setProgress(iteration*5);
            }
        });

        if (this.iteration == 20) {
            ((CalibrateActivity) getActivity()).stopCalibration(this.meanValue);
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calibrate, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.iteration = 0;
        this.meanValue = -1.0;

        this.openCvCameraView = getActivity().findViewById(R.id.calibrate_camera_view);
        this.openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        this.openCvCameraView.setAlpha(0);
        this.openCvCameraView.setCvCameraViewListener(this);
    }
}
