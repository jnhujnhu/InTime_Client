package com.example.kevin.mapapplication.ui.mainscreen;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.kevin.mapapplication.R;

public class SearchResultActivity extends AppCompatActivity {

    public final static int RESULT_CODE = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        ImageButton back = (ImageButton) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        SendBackMessage();
        overridePendingTransition(R.anim.anim_empty, R.anim.slide_out_to_bottom);
    }

    private void SendBackMessage() {
        Intent intent = new Intent();
        setResult(RESULT_CODE, intent);
        super.onBackPressed();
    }
}
