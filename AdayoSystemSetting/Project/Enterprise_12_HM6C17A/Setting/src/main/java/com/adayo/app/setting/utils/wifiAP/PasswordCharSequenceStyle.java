package com.adayo.app.setting.utils.wifiAP;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

public class PasswordCharSequenceStyle extends PasswordTransformationMethod {

    class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence charSequence) {
            this.mSource = charSequence;
        }

        @Override
        public char charAt(int i) {
            return '*';
        }

        @Override
        public int length() {
            return this.mSource.length();
        }

        @Override
        public CharSequence subSequence(int i, int i2) {
            return this.mSource.subSequence(i, i2);
        }
    }

    @Override
    public CharSequence getTransformation(CharSequence charSequence, View view) {
        return new PasswordCharSequence(charSequence);
    }
}
