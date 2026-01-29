package com.pierfrancescosoffritti.androidyoutubeplayer.core.sampleapp;

import android.content.Intent;
import android.os.Bundle;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.sampleapp.examples.SCMPExampleActivity;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, SCMPExampleActivity.class);
        startActivity(intent);
        finish();
    }
}
