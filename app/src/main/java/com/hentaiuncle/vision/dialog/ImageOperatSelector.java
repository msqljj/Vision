package com.hentaiuncle.vision.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import com.hentaiuncle.vision.Global;
import com.hentaiuncle.vision.R;
import com.hentaiuncle.vision.model.operator.ImageOperator;
import vision.image.VisionImage;

/**
 * Created by 饶翰新 on 2018/4/15.
 */

public class ImageOperatSelector extends AppCompatDialog {

    public static int STATUS_OK = 1;
    public static int STATUS_CANCLE = 0;
    public static int STATUS_EXCEPTION = -1;

    private VisionImage img;
    private ImageOperator operator;
    private ImageOperatSelectorResult resultListener;

    private List<ImageOperator> opdata;
    private RecyclerView opList;

    /**
     * 对话框相对屏幕的横向缩放
     */
    public static float scaleX = 0.95f;
    /**
     * 对话框相对屏幕的纵向缩放
     */
    public static float scaleY = 0.85f;

    public static void show(Context context, VisionImage img,ImageOperatSelectorResult resultListener) {

        ImageOperatSelector selector = new ImageOperatSelector(context);
        selector.setImg(img);
        selector.setResultListener(resultListener);
        selector.show();

    }

    public ImageOperatSelector(Context context) {
        super(context);
    }

    public ImageOperatSelector(Context context, int theme) {
        super(context, theme);
    }

    protected ImageOperatSelector(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image_operator_selector);


        opdata = new ArrayList<>();


        //设置窗口大小
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = this.getContext().getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * scaleX);
        lp.height = (int) (d.heightPixels * scaleY);
        dialogWindow.setAttributes(lp);

        opList = (RecyclerView) findViewById(R.id.op_selector_list);

        for (Class c : Global.OPERATORS) {
            try {
                if(c == null){
                    opdata.add(null);
                    continue;
                }
                ImageOperator op = (ImageOperator) c.getConstructor().newInstance();
                opdata.add(op);
            } catch (Exception e) {
                resultListener.onDone(STATUS_EXCEPTION,null,e);
                this.dismiss();
            }
        }

        opList.setAdapter(new OpAdapter());
        opList.setLayoutManager(new LinearLayoutManager(ImageOperatSelector.this.getContext(), LinearLayoutManager.VERTICAL, false));

    }

    public void setOperator(ImageOperator operator) {
        this.operator = operator;
    }

    public void setImg(VisionImage img) {
        this.img = img;
    }

    public void setResultListener(ImageOperatSelectorResult resultListener) {
        this.resultListener = resultListener;
    }

    public ImageOperator getOperator() {
        return operator;
    }

    class OpAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ImageOperatSelector.this.getContext()).inflate(R.layout.item_img_operator_list, parent, false);
            return new OpHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            OpHolder h = (OpHolder) holder;
            ImageOperator op = opdata.get(position);
            if(op == null){
                h.bt.setText("");
                return;
            }
            h.bt.setText(op.OperatorName());
            h.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageOperatDialog.Show(ImageOperatSelector.this.getContext(), opdata.get(position), img, new ImageOperatDialog.ImageOperatDialogRsult() {
                        @Override
                        public void onDone(int status, ImageOperator op, VisionImage img, Exception e) {
                            if(status == ImageOperatDialog.RESULT_OK) {
                                resultListener.onDone(STATUS_OK,op,e);
                                ImageOperatSelector.this.dismiss();
                            }else if(status == ImageOperatDialog.RESULT_CANCLE){
                                //resultListener.onDone(STATUS_CANCLE,op,e);
                            }else {
                                resultListener.onDone(STATUS_EXCEPTION,op,e);
                                ImageOperatSelector.this.dismiss();
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
    class OpHolder extends RecyclerView.ViewHolder {

        protected Button bt;

        public OpHolder(View itemView) {
            super(itemView);

            bt = (Button) itemView.findViewById(R.id.selector_item_name);

        }
    }

    public interface ImageOperatSelectorResult {
        void onDone(int status,ImageOperator operator,Exception e);
    }
}
