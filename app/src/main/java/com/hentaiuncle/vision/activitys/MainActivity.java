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

    /*
 * 剪切图片
 */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        // 裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", 250);
//        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        //intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 2) {
            if(data != null){

                Intent intent = new Intent(this,ImagePlaygroundActivity.class);
                intent.setData(data.getData());
                startActivity(intent);

//                //构建VisionImage
//                VisionImage img = new VisionImage(bitmap.getWidth(),bitmap.getHeight());
//                bitmap.getPixels(img.getRGB(),0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());

//
//                ImageOperatDialog.Show(MainActivity.this, new Sobel(), img, new ImageOperatDialog.ImageOperatDialogRsult() {
//                    @Override
//                    public void onDone(int status, ImageOperator op, VisionImage img, Exception e) {
//                        if(status == 2)
//                            e.printStackTrace();
//                        if(status == 1){
//                            Bitmap bm = Bitmap.createBitmap(img.getRGB(),img.getWidth(),img.getHeight(),Bitmap.Config.RGB_565);
//
//                            img_test.setImageBitmap(bm);
//                        }
//                    }
//                });

            }
        }else if(requestCode == 3){
            if (data != null) {

            }
        }
    }
}
