package com.example.mediaplayx.utils;

import android.content.Context;
import android.graphics.Typeface;
import java.lang.reflect.Field;

public class TypefaceUtil {
    public static void overrideFont(Context context, String defaultFont, String customFontFile) {
        try {
            Typeface customTypeface = Typeface.createFromAsset(context.getAssets(), "font/" + customFontFile);
            Field defaultField = Typeface.class.getDeclaredField(defaultFont);
            defaultField.setAccessible(true);
            defaultField.set(null, customTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
