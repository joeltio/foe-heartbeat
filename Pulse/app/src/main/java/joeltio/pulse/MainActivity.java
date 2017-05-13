package joeltio.pulse;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import joeltio.pulse.Fragments.CameraFragment;
import joeltio.pulse.Fragments.GraphFragment;
import joeltio.pulse.Fragments.StatisticsFragment;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_camera);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.navigation_camera:
                CameraFragment cameraFragment = new CameraFragment();
                getFragmentManager().beginTransaction().replace(
                        R.id.content,
                        cameraFragment,
                        cameraFragment.getTag()
                ).commit();
                return true;
            case R.id.navigation_graph:
                GraphFragment graphFragment = new GraphFragment();
                getFragmentManager().beginTransaction().replace(
                        R.id.content,
                        graphFragment,
                        graphFragment.getTag()
                ).commit();
                return true;
            case R.id.navigation_statistics:
                StatisticsFragment statisticsFragment = new StatisticsFragment();
                getFragmentManager().beginTransaction().replace(
                        R.id.content,
                        statisticsFragment,
                        statisticsFragment.getTag()
                ).commit();
                return true;
        }

        return false;
    }
}
