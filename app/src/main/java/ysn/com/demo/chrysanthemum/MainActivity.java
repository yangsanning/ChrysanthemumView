package ysn.com.demo.chrysanthemum;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ysn.com.view.chrysanthemum.ChrysanthemumView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChrysanthemumView chrysanthemumView = findViewById(R.id.main_activity_chrysanthemum_view);
        chrysanthemumView.startAnimation(1500);
        ChrysanthemumView chrysanthemumView2 = findViewById(R.id.main_activity_chrysanthemum_view2);
        chrysanthemumView2.startAnimation(1500);
    }
}
