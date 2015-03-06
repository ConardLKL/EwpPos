package com.limahao.ticket.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.limahao.ticket.R;

public class NumberPicker2 extends FrameLayout {

    private final NumberPicker mNumberSpinner;

    private final EditText mNumberSpinnerInput;

    public NumberPicker2(Context context) {
        this(context, null);
    }

    public NumberPicker2(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.numberPickerStyle);
    }

    public NumberPicker2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        int layoutResourceId = R.layout.number_picker_holo;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResourceId, this, true);

        mNumberSpinner = (NumberPicker) findViewById(R.id.number);
        //mNumberSpinner.setMinValue(0);
        //mNumberSpinner.setMaxValue(59);
        mNumberSpinner.setOnLongPressUpdateInterval(100);
        mNumberSpinner.setFormatter(NumberPicker.getTwoDigitFormatter());
        
        mNumberSpinnerInput = (EditText) mNumberSpinner.findViewById(R.id.np__numberpicker_input);
        mNumberSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mNumberSpinnerInput.setFocusable(false); 
    }

    /**
* Used to save / restore state of picker
*/
    private static class SavedState extends BaseSavedState {

        private final int mMinute;

        private SavedState(Parcelable superState, int minute) {
            super(superState);
            mMinute = minute;
        }

        private SavedState(Parcel in) {
            super(in);
            mMinute = in.readInt();
        }

        public int getMinute() {
            return mMinute;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mMinute);
        }

        @SuppressWarnings({"unused", "hiding"})
        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getCurrentNumber());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentNumber(ss.getMinute());
    }

    public Integer getCurrentNumber() {
        return mNumberSpinner.getValue();
    }

    public void setCurrentNumber(Integer currentNumber) {
        if (currentNumber == getCurrentNumber()) {
            return;
        }
        mNumberSpinner.setValue(currentNumber);
    }

}
