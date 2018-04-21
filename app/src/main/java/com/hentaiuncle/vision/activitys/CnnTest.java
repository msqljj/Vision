package com.hentaiuncle.vision.activitys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.renderscript.RenderScript;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hentaiuncle.cnndroid.messagepack.ParamUnpacker;
import com.hentaiuncle.cnndroid.network.CNNdroid;
import com.hentaiuncle.vision.R;

import org.msgpack.MessagePack;
import org.msgpack.packer.MessagePackPacker;
import org.msgpack.type.MapValue;
import org.msgpack.type.Value;
import org.msgpack.unpacker.BufferUnpacker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;

public class CnnTest extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_FLAG_TAKEPHOTO = 1;

    private Button bt_takePhoto;
    private ImageView mv;
    private TextView tv;
    Bitmap mean;
    private RenderScript rs;
    private CNNdroid cnn;
    int inputW, inputH;
    List<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnn_test);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bt_takePhoto = (Button) findViewById(R.id.cnntest_takePhoto);
        mv = (ImageView) findViewById(R.id.cnntest_img);
        tv = (TextView) findViewById(R.id.cnntest_result);

        rs = RenderScript.create(this);
        labels = new ArrayList<>();

        if(getIntent().getStringExtra("path") != null)
            new prepareModel().execute(new args(rs, getIntent().getStringExtra("path")));
        else
            onException(new Exception("未指定神经网络模型文件！"));

        bt_takePhoto.setOnClickListener(this);
    }

    private String accuracy(float[] input_matrix, int topk) {
        String result = "";
        int[] max_num = {-1, -1, -1, -1, -1};
        float[] max = new float[topk];
        int size = labels.size();
        if (topk > size)
            topk = size;
        for (int k = 0; k < topk; ++k) {
            for (int i = 0; i < size; ++i) {
                if (input_matrix[i] > max[k]) {
                    boolean newVal = true;
                    for (int j = 0; j < topk; ++j)
                        if (i == max_num[j])
                            newVal = false;
                    if (newVal) {
                        max[k] = input_matrix[i];
                        max_num[k] = i;
                    }
                }
            }
        }

        for (int i = 0; i < topk; i++)
            result += labels.get(max_num[i]) + "的概率是" + Math.round(max[i] * 100 )+ " %\n\n";
        return result;
    }

    private void compute(Bitmap bitmap) throws Exception {
        if (cnn == null)
            throw new Exception("神经网络模型未加载");

        float[][][][] inputBatch = new float[1][3][inputW][inputH];
        //Bitmap bmp1 = Bitmap.createScaledBitmap(bitmap, mv.getWidth(), mv.getHeight(), true);


        Bitmap bmp2 = Bitmap.createScaledBitmap(bitmap, inputW, inputH, false);
        mv.setImageBitmap(bmp2);

        for (int j = 0; j < inputW; ++j) {
            for (int k = 0; k < inputH; ++k) {
                int color = bmp2.getPixel(j, k),color2 = mean.getPixel(j,k);
                inputBatch[0][0][k][j] = (float) (blue(color)) - (float) (blue(color2));
                inputBatch[0][1][k][j] = (float) (green(color)) - (float) (green(color2));
                inputBatch[0][2][k][j] = (float) (red(color)) - (float) (red(color2));
            }
        }
        float[][] output = (float[][]) cnn.compute(inputBatch);
        String result = accuracy(output[0], 1);
        tv.setText(result);
    }

    private void onException(Exception e) {
        e.printStackTrace();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(out));
        String str = new String(out.toByteArray());
        Snackbar.make(getWindow().getDecorView(), "发生异常：\n" + str, Snackbar.LENGTH_SHORT).show();
        tv.setText(str);

    }

    @Override
    public void onClick(View view) {
        if (cnn == null) {
            Snackbar.make(getWindow().getDecorView(), "神经网络模型未加载", Snackbar.LENGTH_SHORT).show();
            return;
        }
        // 拍照
        if (view.equals(bt_takePhoto)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, RESULT_FLAG_TAKEPHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FLAG_TAKEPHOTO && data != null) {
            try {
                compute((Bitmap) data.getExtras().get("data"));
            } catch (Exception e) {
                onException(e);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * 读取输入尺寸与输出标签
     *
     * @param file
     * @throws Exception
     */
    private void loadLabels(String file) throws Exception {
        Scanner scanner = new Scanner(new File(file));

        try {
            inputW = Integer.parseInt(scanner.nextLine().trim());
            inputH = Integer.parseInt(scanner.nextLine().trim());
        } catch (Throwable e) {
            throw new Exception("labels input information do not exist!");
        }
        while (scanner.hasNext())
            labels.add(scanner.nextLine().trim());
        System.out.println(labels.size());
        for (String s : labels)
            System.err.println(s);
    }

    private class args {
        protected RenderScript renderScript;
        protected String modelFile;

        public args(RenderScript renderScript, String modelFile) {
            this.renderScript = renderScript;
            this.modelFile = modelFile;
        }
    }

    private class prepareModel extends AsyncTask<args, Void, CNNdroid> {

        ProgressDialog progDailog;

        @Override
        protected void onPreExecute() {
            progDailog = new ProgressDialog(CnnTest.this);
            progDailog.setTitle(R.string.cnn_wait);
            progDailog.setMessage(CnnTest.this.getResources().getString(R.string.cnn_loading));
            progDailog.setCancelable(false);
            progDailog.show();
        }

        @Override
        protected CNNdroid doInBackground(args... argses) {
            if (argses.length == 0)
                onException(new IllegalArgumentException("CNN load Fail,arg is null!"));

            try {
                File f = new File(argses[0].modelFile);

                loadLabels(f.getParent() + "/labels.txt");

                mean = BitmapFactory.decodeFile(f.getParent() + "/mean.png");
                cnn = new CNNdroid(argses[0].renderScript, argses[0].modelFile);


            } catch (Exception e) {
                onException(e);
            }
            return cnn;
        }

        @Override
        protected void onPostExecute(CNNdroid cnNdroid) {
            progDailog.dismiss();
        }

        @Override
        protected void onCancelled() {
            progDailog.dismiss();
        }
    }

}
