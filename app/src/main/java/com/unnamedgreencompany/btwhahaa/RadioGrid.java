package com.unnamedgreencompany.btwhahaa;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

public class RadioGrid extends TableLayout implements View.OnClickListener {

    private RadioButton activeRadioButton;
    private OnOptionSelectListener mListener;

    /**
     * @param context
     */
    public RadioGrid(Context context) {
        super(context);
        initListener(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public RadioGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        initListener(context);
    }

    private void initListener(Context context) {
        if (context instanceof OnOptionSelectListener) {
            mListener = (OnOptionSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnOptionSelectListener");
        }
    }

    @Override
    public void onClick(View v) {
        final RadioButton rb = (RadioButton) v;
        if ( activeRadioButton != null ) {
            activeRadioButton.setChecked(false);
        }
        rb.setChecked(true);
        activeRadioButton = rb;
        mListener.onFunctionSelection(rb.getId());
    }

    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, int, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        setChildrenOnClickListener((TableRow)child);
    }

    /* (non-Javadoc)
     * @see android.widget.TableLayout#addView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setChildrenOnClickListener((TableRow)child);
    }

    private void setChildrenOnClickListener(TableRow tr) {
        final int c = tr.getChildCount();
        for (int i=0; i < c; i++) {
            final View v = tr.getChildAt(i);
            if ( v instanceof RadioButton ) {
                v.setOnClickListener(this);
            }
        }
    }

    public int getCheckedRadioButtonId() {
        if ( activeRadioButton != null ) {
            return activeRadioButton.getId();
        }
        return -1;
    }

    public interface OnOptionSelectListener {
        void onFunctionSelection(int fieldId);
    }
}
