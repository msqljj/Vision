package com.hentaiuncle.vision.activitys;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hentaiuncle.vision.R;


public class MainActivity extends AppCompatActivity {

    private Button bt_test;
    private ImageView img_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        img_test = (ImageView) findViewById(R.id.img_test);

    }

    public void imgpg(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 2) {
            if(data != null){
                Intent intent = new Intent(this,ImagePlaygroundActivity.class);
                intent.setData(data.getData());
                startActivity(intent);
            }
        }
    }
}
