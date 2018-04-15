package us.xingkong.vision.activitys;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.xingkong.vision.R;
import us.xingkong.vision.dialog.ImageOperatDialog;
import us.xingkong.vision.dialog.ImageOperatSelector;
import us.xingkong.vision.model.operator.Blur;
import us.xingkong.vision.model.operator.ImageOperator;
import us.xingkong.vision.other.NoScrollViewPager;
import vision.core.Core;
import vision.image.VisionImage;

import static us.xingkong.vision.Global.PERMISSIONS;

public class ImagePlaygroundActivity extends AppCompatActivity {


    private ImageView imageView, imageView_input, imageView_output;
    private TextView info_input, info_output;
    private FloatingActionButton fab_pick_img, fab_op_add;//, fab2;
    private NoScrollViewPager vp;
    private Button bt_output_update, bt_output_save;

    private TabLayout tb;

    private RecyclerView opList;
    private List<ImageOperator> opdata;

    private List<View> viewList;
    private VisionImage img;
    private Bitmap img_bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_playground);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        opdata = new ArrayList<>();
        viewList = new ArrayList<>();
        viewList.add(getLayoutInflater().inflate(R.layout.img_pg_frag_input, null));
        viewList.add(getLayoutInflater().inflate(R.layout.img_pg_frag_oper, null));
        viewList.add(getLayoutInflater().inflate(R.layout.img_pg_frag_output, null));


        imageView = (ImageView) findViewById(R.id.imageView);
        fab_pick_img = viewList.get(0).findViewById(R.id.fab_pick_img);
        imageView_input = viewList.get(0).findViewById(R.id.imageView_input);
        info_input = viewList.get(0).findViewById(R.id.info_input);
        fab_op_add = viewList.get(1).findViewById(R.id.img_pg_op_add);
        opList = viewList.get(1).findViewById(R.id.img_pg_oplist);
        imageView_output = viewList.get(2).findViewById(R.id.imageView_output);
        bt_output_update = viewList.get(2).findViewById(R.id.fab_output_update);
        bt_output_save = viewList.get(2).findViewById(R.id.fab_output_save);
        info_output = viewList.get(2).findViewById(R.id.info_output);


        tb = (TabLayout) findViewById(R.id.tab);
        vp = (NoScrollViewPager) findViewById(R.id.img_pg_vp);
        vp.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                View v = viewList.get(position);
                container.addView(v);
                return v;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                View v = viewList.get(position);
                container.removeView(v);
            }
        });

        //opList = (RecyclerView) findViewById(R.id.img_pg_op_list);


        fab_op_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageOperatSelector.show(ImagePlaygroundActivity.this, img, new ImageOperatSelector.ImageOperatSelectorResult() {
                    @Override
                    public void onDone(int status, ImageOperator operator, Exception e) {
                        if (status == ImageOperatSelector.STATUS_OK) {
                            opdata.add(operator);
                            opList.getAdapter().notifyDataSetChanged();
                        } else if (status == ImageOperatSelector.STATUS_EXCEPTION) {
                            Snackbar.make(view, e.toString(), Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        fab_pick_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });

        vp.setNoScroll(true);
        tb.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp.setCurrentItem(tb.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        opList.setAdapter(new OpAdapter());
        opList.setLayoutManager(new LinearLayoutManager(ImagePlaygroundActivity.this, LinearLayoutManager.VERTICAL, false));

        bt_output_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOutput();
            }
        });
        bt_output_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String result = exportOutput();
                    if (result != null)
                        Snackbar.make(view, "Success\n   " + result, Snackbar.LENGTH_SHORT).show();
                    else
                        Snackbar.make(view, "Fail", Snackbar.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });

        try {
            updateImg(getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新输入图
     *
     * @param uri
     * @throws IOException
     */
    protected void updateImg(Uri uri) throws IOException {

        Bitmap bitmap = null;

        img_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        imageView_input.setImageBitmap(img_bitmap);
        info_input.setText("Width:\t" + img_bitmap.getWidth() + "\n" + "Height:\t" + img_bitmap.getHeight());
        img = new VisionImage(img_bitmap.getWidth(), img_bitmap.getHeight());
        img_bitmap.getPixels(img.getRGB(), 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        int w = 200;
        float scale = (float) w / img.getWidth();

        if (scale > 1)
            scale = 1;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        bitmap = Bitmap.createBitmap(img_bitmap, 0, 0, img_bitmap.getWidth(), img_bitmap.getHeight(), matrix, true);
        VisionImage tmp = new VisionImage(bitmap.getWidth(), bitmap.getHeight());
        bitmap.getPixels(tmp.getRGB(), 0, tmp.getWidth(), 0, 0, tmp.getWidth(), tmp.getHeight());

        Blur b = new Blur();
        b.r = 2;
        b.t = 2;
        b.Operator(tmp, this);
        Core.Light(tmp, 0.5f);
        imageView.setImageBitmap(Bitmap.createBitmap(tmp.getRGB(), tmp.getWidth(), tmp.getHeight(), Bitmap.Config.RGB_565));
        tmp = null;
        b = null;
        //updateOutput();
        System.gc();
    }

    protected void updateOutput() {
        double scale = (double) imageView_output.getWidth() / img.getWidth();
        int h = (int) (scale * img.getHeight());
        VisionImage tmp;
        if (scale <= 1)
            tmp = img.Zoom(imageView_output.getWidth(), h);
        else
            tmp = img.Copy();
        for (ImageOperator op : opdata) {
            op.Operator(tmp, ImagePlaygroundActivity.this.getApplicationContext());
            op = null;
            System.gc();
        }
        Bitmap bitmap = Bitmap.createBitmap(tmp.getRGB(), tmp.getWidth(), tmp.getHeight(), Bitmap.Config.RGB_565);
        imageView_output.setImageBitmap(bitmap);
        info_output.setText("Width:\t" + bitmap.getWidth() + "\nHeight:\t" + bitmap.getHeight());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (data != null && data.getData() != null) {
                try {
                    updateImg(data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class OpAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ImagePlaygroundActivity.this).inflate(R.layout.item_img_operator_list, parent, false);
            return new OperatorHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OperatorHolder h = (OperatorHolder) holder;
            ImageOperator op = opdata.get(position);

            h.name.setText((position + 1) + ":\t\t" + op.toString());
            h.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ImageOperatDialog.ShowWithRemoveButton(ImagePlaygroundActivity.this, opdata.get(position), img, new ImageOperatDialog.ImageOperatDialogRsult() {
                        @Override
                        public void onDone(int status, ImageOperator op, VisionImage img, Exception e) {
                            if (status == ImageOperatDialog.RESULT_OK)
                                h.name.setText((position + 1) + ":\t\t" + op.toString());
                            else if (status == ImageOperatDialog.RESULT_REMOVE) {
                                opdata.remove(op);
                                OpAdapter.this.notifyDataSetChanged();
                            }
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return opdata.size();
        }
    }


    class OperatorHolder extends RecyclerView.ViewHolder {

        protected TextView name;

        public OperatorHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.selector_item_name);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(item);
    }

    protected String exportOutput() throws Exception {
        int permission = ContextCompat.checkSelfPermission(this,
                "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
            return null;
        }
        String path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera";
        File root = new File(path);
        root.mkdirs();

        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileanme = "Vison - " + format.format(new Date()) + ".jpg";

        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(path + "/" + fileanme));
        DisplayMetrics d = this.getResources().getDisplayMetrics();

        double scale = (double) d.widthPixels / img.getWidth();
        int h = (int) (scale * img.getHeight());
        VisionImage tmp;
        if (scale <= 1)
            tmp = img.Zoom(d.widthPixels, h);
        else
            tmp = img.Copy();
        for (ImageOperator op : opdata) {
            op.Operator(tmp, ImagePlaygroundActivity.this.getApplicationContext());
            op = null;
            System.gc();
        }
        Bitmap bitmap = Bitmap.createBitmap(tmp.getRGB(), tmp.getWidth(), tmp.getHeight(), Bitmap.Config.RGB_565);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        out.flush();
        out.close();
        bitmap = null;
        tmp = null;
        System.gc();
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path + "/" + fileanme)));
        return fileanme;
    }
}
