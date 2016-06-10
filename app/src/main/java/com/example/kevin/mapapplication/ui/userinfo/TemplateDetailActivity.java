package com.example.kevin.mapapplication.ui.userinfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.kevin.mapapplication.R;
import com.example.kevin.mapapplication.model.ConnectionManager;
import com.example.kevin.mapapplication.ui.mainscreen.tag.BlueTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.GreenTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.RedTagInfoActivity;
import com.example.kevin.mapapplication.ui.mainscreen.tag.TagInfoActivity;
import com.example.kevin.mapapplication.utils.AsyncJSONHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TemplateDetailActivity extends OrderAndTemplateDetailActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        text_header.setText("Template Detail");
        button_create.setVisibility(View.VISIBLE);

        button_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupMenu == null){
                    popupMenu = new PopupMenu(TemplateDetailActivity.this, button_more);

                    menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.menu_more,popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.action_edit:
                                    editTemplate();
                                    break;
                                case R.id.action_delete:
                                    deleteTemplate();
                                    break;
                            }
                            return true;
                        }
                    });
                }
                popupMenu.show();
            }
        });

        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder();
            }
        });
    }

    @Override
    protected void refreshDetail() {
        super.refreshDetail();
        ConnectionManager.getInstance().GetTemplateDetail(bundle.getString("tid"), userinfo.getString("token", null), detailHandler);
    }

    private void createOrder() {
        Intent intent = new Intent(TemplateDetailActivity.this, tagClass);
        intent.putExtras(newBundle);
        intent.putExtra("class", "order");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    private void editTemplate() {
        Intent intent = new Intent(TemplateDetailActivity.this, tagClass);
        intent.putExtras(newBundle);
        intent.putExtra("class", "template");
        intent.putExtra("tid", bundle.getString("tid"));
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    private void deleteTemplate() {
        ConnectionManager.getInstance().DeleteTemplate(bundle.getString("tid"), userinfo.getString("token", null), new AsyncJSONHttpResponseHandler() {
            @Override
            public void onSuccessWithJSON(int statusCode, Header[] headers, JSONObject res) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                finish();
            }

            @Override
            public void onFailureWithJSON(int statusCode, Header[] headers, JSONObject res, String error) throws JSONException {
                loading.setVisibility(View.INVISIBLE);
                Toast.makeText(TemplateDetailActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
