package com.keyboard.chay.keyboardwatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.keyboard.chay.watcherlibiary.KeyboardWatcher;
import com.keyboard.chay.watcherlibiary.OnKeyboardToggleListener;

public class MainActivity extends AppCompatActivity implements OnKeyboardToggleListener {

    private KeyboardWatcher keyboardWatcher;

    private EditText et_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_test = findViewById(R.id.et_test);
        keyboardWatcher = new KeyboardWatcher(this);
        keyboardWatcher.init(getWindow().getDecorView(), this);
    }

    @Override
    public void onKeyboardShown(int keyboardSize) {
        Log.e("Chay", "onKeyboardShown -- keyboardSize " + keyboardSize);
    }

    @Override
    public void onKeyboardClosed() {
        Log.e("Chay", "onKeyboardClosed");
    }
}
