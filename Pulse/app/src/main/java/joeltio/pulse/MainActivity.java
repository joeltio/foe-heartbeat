package joeltio.pulse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import joeltio.pulse.Fragments.CameraFragment;
import joeltio.pulse.Fragments.GraphFragment;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CALIBRATE = 2000;

    private BottomNavigationView bottomNavigationView;
    private Double meanValue;

    private void startCalibrateActivity() {
        Intent intent = new Intent(this, CalibrateActivity.class);
        startActivityForResult(intent, REQUEST_CALIBRATE);
    }

    private void startGraphFragment(Double meanValue) {
        Bundle bundle = new Bundle();
        bundle.putDouble(GraphFragment.ARGUMENT_MEAN_VALUE, meanValue);

        GraphFragment graphFragment = new GraphFragment();
        graphFragment.setArguments(bundle);

        getFragmentManager().beginTransaction().replace(
                R.id.content,
                graphFragment,
                graphFragment.getTag()
        ).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        this.meanValue = -1.0;

        this.bottomNavigationView = findViewById(R.id.navigation);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            this.bottomNavigationView.setSelectedItemId(R.id.navigation_camera);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Double meanValue = data.getDoubleExtra(CalibrateActivity.EXTRA_MEAN_VALUE, -1);
            if (meanValue != -1) {
                this.meanValue = meanValue;
                this.bottomNavigationView.setSelectedItemId(R.id.navigation_graph);
            }
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
                if (this.meanValue != -1) {
                    startGraphFragment(this.meanValue);
                } else {
                    this.bottomNavigationView.setSelectedItemId(R.id.navigation_calibrate);
                }
                return true;
            case R.id.navigation_calibrate:
                startCalibrateActivity();
                return true;
        }

        return false;
    }
}
