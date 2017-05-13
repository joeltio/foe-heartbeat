package joeltio.pulse.Fragments;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public class GraphFragment extends OpenCVFragment {
    private CameraBridgeViewBase openCvCameraView;

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
        return null;
    }
}
