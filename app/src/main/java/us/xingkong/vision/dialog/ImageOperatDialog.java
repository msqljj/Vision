package us.xingkong.vision.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.xingkong.vision.R;
import us.xingkong.vision.model.operator.ImageOperator;
import vision.image.VisionImage;

/**
 * 图像操作对话框
 *
 * Created by 饶翰新 on 2018/4/13.
 */
public class ImageOperatDialog extends AppCompatDialog {

    public static final int RESULT_REMOVE = 2;
    public static final int RESULT_OK = 1;
    public static final int RESULT_CANCLE = 0;
    public static final int RESULT_UNKNOW = -1;
    public static final int RESULT_EXCEPTION = 2;

    private VisionImage img,tmp;

    private ImageOperator operator;
    private ImageOperatDialogRsult result;
    private boolean showRemoveButton;

    private List<String> argsName;
    private List<Class> argsClass;
    private Dialog prossessDialog;

    private TextView title;
    private ImageView imageView;
    private RecyclerView list;
    private Button ok, update, cancle;
    private Button remove;
    private Handler handler;

    /**
     * 对话框相对屏幕的横向缩放
     */
    public static float scaleX = 0.95f;
    /**
     * 对话框相对屏幕的纵向缩放
     */
    public static float scaleY = 0.85f;

    public static void Show(Context context,ImageOperator operator,VisionImage img,ImageOperatDialogRsult resultListener) {
        ImageOperatDialog d = new ImageOperatDialog(context);
        try {
            d.setOperator(operator);
        } catch (IllegalAccessException e) {
            resultListener.onDone(RESULT_EXCEPTION,operator,img,e);
            return;
        }
        d.setImg(img);
        d.setResult(resultListener);
        d.show();
    }

    public static void ShowWithRemoveButton(Context context,ImageOperator operator,VisionImage img,ImageOperatDialogRsult resultListener) {
        ImageOperatDialog d = new ImageOperatDialog(context);
        try {
            d.setOperator(operator);
        } catch (IllegalAccessException e) {
            resultListener.onDone(RESULT_EXCEPTION,operator,img,e);
            return;
        }
        d.setImg(img);
        d.setResult(resultListener);
        d.showRemoveButton = true;
        d.show();
    }

    public ImageOperatDialog(Context context) {
        super(context);
    }

    public ImageOperatDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ImageOperatDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image_operator);

        //设置窗口大小
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = this.getContext().getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * scaleX);
        lp.height = (int) (d.heightPixels * scaleY);
        dialogWindow.setAttributes(lp);

        //初始化控件
        title = (TextView) findViewById(R.id.operator_name);
        imageView = (ImageView) findViewById(R.id.operator_image);
        list = (RecyclerView) findViewById(R.id.operator_list);
        update = (Button) findViewById(R.id.operator_bt_update);
        ok = (Button) findViewById(R.id.operator_bt_ok);
        cancle = (Button) findViewById(R.id.operator_bt_cancle);
        remove = (Button) findViewById(R.id.operator_bt_rm);
        if(!showRemoveButton)
            remove.setVisibility(View.INVISIBLE);
        prossessDialog = new ProgressDialog(this.getContext());
        prossessDialog.setTitle(R.string.doing);

        handler = new Handler();

        //给控件赋值
        title.setText(operator.OperatorName());
        Bitmap b = Bitmap.createBitmap(img.getRGB(), img.getWidth(), img.getHeight(), Bitmap.Config.RGB_565);
        imageView.setImageBitmap(b);

        list.setAdapter(new ItemAdapter());
        list.setLayoutManager(new LinearLayoutManager(ImageOperatDialog.this.getContext(), LinearLayoutManager.VERTICAL, false));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        double scale = (double) imageView.getWidth() / img.getWidth();
                        int h = (int)(scale * img.getHeight());
                        tmp = img.Zoom(imageView.getWidth(),h);
                        //tmp = img.Copy();
                        operator.Operator(tmp,ImageOperatDialog.this.getContext());
                        if(!this.isInterrupted())
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap b = Bitmap.createBitmap(tmp.getRGB(), tmp.getWidth(), tmp.getHeight(), Bitmap.Config.RGB_565);
                                    imageView.setImageBitmap(b);
                                    prossessDialog.dismiss();
                                }
                            });

                    }
                };

                prossessDialog.setCancelable(true);
                prossessDialog.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Toast.makeText(prossessDialog.getContext(),R.string.tips_cancle,Toast.LENGTH_SHORT).show();
                        t.interrupt();
                        prossessDialog.dismiss();
                    }
                });
                prossessDialog.show();
                t.start();

            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tmp == null)
                    tmp = img.Copy();
                result.onDone(RESULT_OK,operator,tmp,null);
                ImageOperatDialog.this.dismiss();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.onDone(RESULT_CANCLE,operator,img,null);
                ImageOperatDialog.this.dismiss();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tmp == null)
                    tmp = img.Copy();
                result.onDone(RESULT_REMOVE,operator,tmp,null);
                ImageOperatDialog.this.dismiss();
            }
        });
    }

    public void setImg(VisionImage img) {
        this.img = img;
    }

    public void setOperator(ImageOperator operator) throws IllegalAccessException {
        this.operator = operator;
        Map<String, Class> args = operator.getArgs();
        argsName = new ArrayList<>();
        argsClass = new ArrayList<>();
        Set<Map.Entry<String, Class>> set = args.entrySet();
        for (Map.Entry<String, Class> kv : set) {
            argsName.add(kv.getKey());
            argsClass.add(kv.getValue());
        }
    }

    public void setResult(ImageOperatDialogRsult result) {
        this.result = result;
    }

    class ItemAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ImageOperatDialog.this.getContext()).inflate(R.layout.item_dialog_operator_list, parent, false);
            return new ItemHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemHolder h = (ItemHolder) holder;
            String name = argsName.get(position);
            h.name.setText(operator.getArgText(name));
            Object value = null;
            try {
                value = operator.getArg(argsName.get(position));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                result.onDone(RESULT_EXCEPTION,operator,img,e);
                ImageOperatDialog.this.dismiss();
            }

            if (argsClass.get(position).equals(Integer.class)) {
                SeekBar s = new SeekBar(h.controler.getContext());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
                if (value != null) {
                    int v = (int) value;
                    s.setProgress(v);
                    s.setMax(4 * v);
                }
                s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        h.value.setText(String.valueOf(i));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        try {
                            operator.setArg(name, seekBar.getProgress());
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            result.onDone(RESULT_EXCEPTION,operator,img,e);
                            ImageOperatDialog.this.dismiss();
                        }
                    }
                });
                h.value.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            int i = Integer.valueOf(h.value.getText().toString());
                            if(i > s.getMax()){
                                s.setMax(4 * i);
                            }
                            s.setProgress(i);
                            operator.setArg(name, i);
                        } catch (Exception e) {
//                            result.onDone(RESULT_EXCEPTION,operator,img,e);
//                            ImageOperatDialog.this.dismiss();
                        }
                    }
                });
                h.controler.addView(s, lp);
            } else {

            }
            if (value != null) {
                h.value.setText(value.toString());
            }
        }

        @Override
        public int getItemCount() {
            return argsName.size();
        }
    }



    class ItemHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected LinearLayout controler;
        protected EditText value;

        public ItemHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.operator_list_item_name);
            controler = itemView.findViewById(R.id.operator_list_item_controler);
            value = itemView.findViewById(R.id.operator_list_item_value);
        }

    }

    public interface ImageOperatDialogRsult {

        void onDone(int status, ImageOperator op, VisionImage img, Exception e);
    }
}
