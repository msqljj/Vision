package com.hentaiuncle.vision.model.operator;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.hentaiuncle.vision.ScriptC_grey;
import vision.image.VisionImage;

/**
 * 灰化
 *
 * Created by 饶翰新 on 2018/4/13.
 */

public class Grey extends ImageOperator {

    public Grey(){
        super();

    }

    @Override
    public void Operator(VisionImage img, Context context) {
        RenderScript script = RenderScript.create(context);
        Bitmap bm = Bitmap.createBitmap(img.getWidth(),img.getHeight(),Bitmap.Config.RGB_565);
        bm = bm.copy(Bitmap.Config.ARGB_8888,true);

        bm.setPixels(img.getRGB(),0,img.getWidth(),0,0,img.getWidth(),img.getHeight());

        ScriptC_grey st = new ScriptC_grey(script);
        Allocation input = Allocation.createFromBitmap(script,bm);
        Allocation output = Allocation.createTyped(script,input.getType());
        st.forEach_root(input,output);
        output.copyTo(bm);

        bm = bm.copy(Bitmap.Config.RGB_565,false);
        bm.getPixels(img.getRGB(),0,img.getWidth(),0,0,img.getWidth(),img.getHeight());
        st.destroy();

    }

    @Override
    public String OperatorName() {
        return "灰化";
    }
}
