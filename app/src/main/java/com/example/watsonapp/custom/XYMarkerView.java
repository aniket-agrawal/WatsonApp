package com.example.watsonapp.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.watsonapp.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class XYMarkerView extends MarkerView {

    private final TextView tvContent;
    private final ValueFormatter xAxisValueFormatter;
    int t;
    private final DecimalFormat format;

    public XYMarkerView(Context context, ValueFormatter xAxisValueFormatter, int t) {
        super(context, R.layout.custom_marker_view);
        this.t = t;
        this.xAxisValueFormatter = xAxisValueFormatter;
        tvContent = findViewById(R.id.tvContent);
        format = new DecimalFormat("###.0");
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if(t == 0) {
            BarEntry be = (BarEntry) e;
            int i = highlight.getStackIndex();
            String s;
            if (i == 0) {
                s = "Bad Apps- ";
            } else if (i == 1) {
                s = "Neutral Apps- ";
            } else {
                s = "Good Apps- ";
            }

            tvContent.setText(String.format(s + "%s hr", format.format(be.getYVals()[highlight.getStackIndex()])));
        }
        else if(t == 1){
            tvContent.setText(String.format("%s hr", format.format(e.getY())));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
