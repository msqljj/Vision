package com.hentaiuncle.vision.model.operator;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.hentaiuncle.vision.ScriptC_inverse;
import vision.image.VisionImage;

/**
 * 反色操作
 *
 * Created by 饶翰新 on 2018/4/13.
 */

public class Inverse extends ImageOperator {

    public Inverse(){
        super();
    }

    @Override
    public void Operator(VisionImage img, Context context) {
        RenderScript script = RenderScript.create(context);

        Bitmap bm = Bitmap.createBitmap(img.getRGB(),img.getWidth(),img.getHeight(),Bitmap.Config.ARGB_8888);

        ScriptC_inverse st = new ScriptC_inverse(script);
        Allocation input = Allocation.createFromBitmap(script,bm);
        Allocation output = Allocation.createTyped(script,input.getType());
        st.forEach_root(input,output);
        output.copyTo(bm);

        bm.getPixels(img.getRGB(),0,img.getWidth(),0,0,img.getWidth(),img.getHeight());
        st.destroy();
    }

    @Override
    public String OperatorName() {
        return "反色";
    }
}
