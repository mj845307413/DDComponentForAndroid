package com.example.appgradle;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (MyClass.class.isAssignableFrom(FatherClass.class)) {
            ((TextView) findViewById(R.id.text)).setText(MyClass.class.getSuperclass().getSimpleName());
//        }
    }
}
