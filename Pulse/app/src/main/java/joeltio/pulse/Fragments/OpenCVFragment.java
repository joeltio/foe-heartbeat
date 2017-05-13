package joeltio.pulse.Fragments;

import android.app.Fragment;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public abstract class OpenCVFragment extends Fragment
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    protected abstract CameraBridgeViewBase getOpenCvCameraView();
    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(getActivity()) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(debugTag, "OpenCV Loaded Successfully.");
                    getOpenCvCameraView().enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private String debugTag;

    public OpenCVFragment(String debugTag) {
        this.debugTag = debugTag;
    }

    @Override
    public abstract void onCameraViewStarted(int width, int height);

    @Override
    public abstract void onCameraViewStopped();

    @Override
    public abstract Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame);

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, getActivity(), loaderCallback);
            Log.e(getTag(), "Internal OpenCV Lib not found.");
        } else {
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getOpenCvCameraView() != null) {
            getOpenCvCameraView().disableView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getOpenCvCameraView() != null) {
            getOpenCvCameraView().disableView();
        }
    }
}
