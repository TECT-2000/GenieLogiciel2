package com.example.visas.genielogiciel2;

/**
 * Created by visas on 5/9/18.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Random;

public class CircularTextView extends android.support.v7.widget.AppCompatTextView {
    private float strokeWidth;
    int strokeColor, solidColor;
    Random rand = new Random();

    private String color;

    private static String[] solidColors = new String[]{"#00838F", "#2E7D32", "#00796B", "#EF6C00",
    "#827717", "#0277BD", "#AD1457", "#4527A0", "#283593", "#1565C0"};

    public CircularTextView(Context context) {
        super(context);
        setStrokeColor("WHITE");
        int i = rand.nextInt(10);
        color = solidColors[i];
        setSolidColor(color);
    }

    public CircularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setStrokeColor("WHITE");
        int i = rand.nextInt(10);
        setSolidColor(solidColors[i]);
    }

    public CircularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setStrokeColor("WHITE");
        int i = rand.nextInt(10);
        setSolidColor(solidColors[i]);
    }

    @Override
    public void draw(Canvas canvas) {

        Paint circlePaint = new Paint();
        circlePaint.setColor(solidColor);
        circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        Paint strokePaint = new Paint();
        strokePaint.setColor(strokeColor);
        strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        int h = this.getHeight();
        int w = this.getWidth();

        int diameter = ((h > w) ? h : w);
        int radius = diameter / 2;

        this.setHeight(diameter);
        this.setWidth(diameter);

        canvas.drawCircle(diameter / 2, diameter / 2, radius, strokePaint);

        canvas.drawCircle(diameter / 2, diameter / 2, radius - strokeWidth, circlePaint);

        super.draw(canvas);
    }

    public void setStrokeWidth(int dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        strokeWidth = dp * scale;

    }

    public void setStrokeColor(String color) {
        strokeColor = Color.parseColor(color);
    }

    public void setSolidColor(String color) {
        solidColor = Color.parseColor(color);

    }

    public String getColor() {
        return color;
    }

}