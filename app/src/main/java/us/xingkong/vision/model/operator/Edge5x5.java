package us.xingkong.vision.model.operator;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve5x5;

import vision.core.Operator;
import vision.image.VisionImage;

/**
 * 模糊
 *
 * Created by 饶翰新 on 2018/4/13.
 */
public class Edge5x5 extends Convolve5x5 {


    public Edge5x5(){
        super();
        kerlen = Operator.Edge_5X5;
    }


    @Override
    public String OperatorName() {
        return "边缘提取5x5";
    }
}
