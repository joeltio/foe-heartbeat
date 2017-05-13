package joeltio.pulse.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import joeltio.pulse.R;

public class CameraFragment extends OpenCVFragment {
    private CameraBridgeViewBase openCvCameraView;
    private TextView brightnessMean;

    public CameraFragment() {
        super("OpenCV:CameraFragment");
    }

    @Override
    protected CameraBridgeViewBase getOpenCvCameraView() {
        return openCvCameraView;
    }

    Mat mRgba;
    Mat mRgbaF;
    Mat redChannel;

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC3);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Imgproc.resize(mRgba.t(), mRgbaF, mRgba.size());
        Core.flip(mRgbaF, mRgba, 1);

        List<Mat> planes = new Vector<>();
        Core.split(mRgba, planes);
        redChannel = planes.get(0);

        List<Mat> greyScale = Arrays.asList(redChannel, redChannel, redChannel);
        Core.merge(greyScale, mRgba);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                brightnessMean.setText(Double.toString(Core.mean(redChannel).val[0]));
            }
        });

        return mRgba;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.brightnessMean = (TextView) getActivity().findViewById(R.id.brightness_mean);

        this.openCvCameraView =
                (CameraBridgeViewBase) getActivity().findViewById(R.id.camera_camera_view);
        this.openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        this.openCvCameraView.setCvCameraViewListener(this);
    }
}
