package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.kevin.mapapplication.R;

public class HelpActivity extends AppCompatActivity {

    public static final int RESULT_CODE = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        ImageButton back = (ImageButton) findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        final ArrayAdapter adapter =new ArrayAdapter<String>(this, R.layout.help_listview_item,
                new String[]{"Can I hide my ID on my profile?",
                "How do I enable account protection?",
                "Why can't I access the Wallet feature?",
                "How do I backup my history?",
                "How do I upload my history to a new device?",
                "Why are you so diao?"});

        ListView listView = (ListView) findViewById(R.id.help_list_view);
        listView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //intent.putExtra("Choosed","Item id");
        setResult(RESULT_CODE, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

}
