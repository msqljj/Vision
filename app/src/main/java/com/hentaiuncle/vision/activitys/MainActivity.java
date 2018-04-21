package com.hentaiuncle.vision.activitys;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hentaiuncle.vision.Global;
import com.hentaiuncle.vision.R;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Global.getPermissions(this);

    }

    public void imgpg(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    public void cnntest(View v){
        Intent intent = new Intent(this,NetPlaygroundActivity.class);
        //intent.putExtra("path","/sdcard/Data_My/my.txt");
        startActivity(intent);
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
