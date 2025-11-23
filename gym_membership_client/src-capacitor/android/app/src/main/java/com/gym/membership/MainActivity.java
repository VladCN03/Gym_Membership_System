package com.gym.membership;

import com.getcapacitor.BridgeActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends BridgeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();   // 🔥 ascunde ActionBar
        }
    }
}

