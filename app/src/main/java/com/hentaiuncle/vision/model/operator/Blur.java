package com.hentaiuncle.vision.model.operator;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import vision.image.VisionImage;

/**
 * 模糊
 *
 * Created by 饶翰新 on 2018/4/13.
 */
public class Blur extends ImageOperator {

    public int r;
    public int t;

    public Blur(){
        super();
        r = 2;
        t = 1;
        setArgText("r","模糊半径");
        setArgText("t","迭代次数");

    }

    @Override
    public void Operator(VisionImage img, Context context) {
        if(r <= 0)
            return;
        RenderScript script = RenderScript.create(context);
        Bitmap bm = Bitmap.createBitmap(img.getWidth(),img.getHeight(),Bitmap.Config.RGB_565);
        bm = bm.copy(Bitmap.Config.ARGB_8888,true);

        bm.setPixels(img.getRGB(),0,img.getWidth(),0,0,img.getWidth(),img.getHeight());
        ScriptIntrinsicBlur st = ScriptIntrinsicBlur.create(script,Element.U8_4(script));

        st.setRadius(r);

        for(int i = 0;i < t;i++) {
            Allocation input = Allocation.createFromBitmap(script,bm);
            Allocation output = Allocation.createTyped(script,input.getType());
            st.setInput(input);
            st.forEach(output);

            output.copyTo(bm);
        }

        bm = bm.copy(Bitmap.Config.RGB_565,false);
        bm.getPixels(img.getRGB(),0,img.getWidth(),0,0,img.getWidth(),img.getHeight());
        st.destroy();

    }

    @Override
    public String OperatorName() {
        return "模糊";
    }
}
