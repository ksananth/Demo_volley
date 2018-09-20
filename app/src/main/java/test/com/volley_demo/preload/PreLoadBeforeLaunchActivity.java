package test.com.volley_demo.preload;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.billy.android.preloader.PreLoader;
import com.billy.android.preloader.interfaces.DataListener;

import test.com.volley_demo.R;

/**
 * pre-load data before launch the activity
 *
 * @author billy.qi
 */
public class PreLoadBeforeLaunchActivity extends AppCompatActivity {

    TimeWatcher allTime;
    private TextView logTextView;
    private TextView readmeTextView;
    private int preLoaderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        allTime = TimeWatcher.obtainAndStart("total");
        TimeWatcher timeWatcher = TimeWatcher.obtainAndStart("init layout");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_load);
        setTitle(R.string.pre_loader_before_page_title);
        try {
            Thread.sleep(200);
        } catch(Exception e) {
            e.printStackTrace();
        }
        int option = getIntent().getIntExtra("option", 0);
        readmeTextView = (TextView)findViewById(R.id.readme);
        readmeTextView.setText(getString(R.string.pre_loader_before_open_page_readme, option));
        logTextView = (TextView)findViewById(R.id.log);

        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allTime.start();
                readmeTextView.setText(R.string.pre_loader_refresh);
                logTextView.setText("");
                if (preLoaderId > 0) {
                    PreLoader.refresh(preLoaderId);
                } else {
                    Toast.makeText(PreLoadBeforeLaunchActivity.this, R.string.error_no_pre_loader_id, Toast.LENGTH_SHORT).show();
                }
            }
        });
        String s = timeWatcher.stopAndPrint();
        logTextView.append(s + "\n");
        preLoaderId = getIntent().getIntExtra("preLoaderId", -1);
        if (preLoaderId > 0) {
            PreLoader.listenData(preLoaderId, new DataHolder());
        }
    }

    class DataHolder implements DataListener<String> {
        @Override
        public void onDataArrived(String data) {
            String s = allTime.stopAndPrint();
            logTextView.append(data + "\n" + s + "\n");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preLoaderId > 0) {
            PreLoader.destroy(preLoaderId);
        }
    }
}
