package iessanclemente.PRO.settings;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import iessanclemente.PRO.R;

public class SettingActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ImageView ivSplash = findViewById(R.id.ivSplash);
        ivSplash.setImageDrawable(getDrawable(R.drawable.logo));
    }
}
