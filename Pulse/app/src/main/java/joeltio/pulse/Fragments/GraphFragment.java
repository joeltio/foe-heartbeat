package joeltio.pulse.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

import joeltio.pulse.R;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.openCvCameraView =
                (CameraBridgeViewBase) getActivity().findViewById(R.id.graph_camera_view);
        this.openCvCameraView.setVisibility(SurfaceView.VISIBLE);
        this.openCvCameraView.setCvCameraViewListener(this);
    }
}
