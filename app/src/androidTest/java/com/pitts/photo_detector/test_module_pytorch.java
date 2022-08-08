package com.pitts.photo_detector;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

public class test_module_pytorch {

    private final Context appContext = InstrumentationRegistry.getTargetContext();
    private final Context androidTestContext = InstrumentationRegistry.getContext();

    @Before

    public void initParam() {
        module_param.Companion.initParametersByDefault(appContext);
    }

    @Test
    public void bccTest() {

    }
}
