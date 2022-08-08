package com.pitts.photo_detector;

import android.content.Context;

import androidx.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

public class test_module_mapping {
    private final Context context = InstrumentationRegistry.getTargetContext();

    //If no exceptions are thrown, then this test is passed
    @Test
    public void testReadCsv() {
        Assert.assertFalse(module_mapping.Companion.isReady());
        module_mapping.Companion.init(context, "index_mapping.csv");
        Assert.assertTrue(module_mapping.Companion.isReady());
        module_mapping.Companion.init(context, "foo.csv");
        Assert.assertFalse(module_mapping.Companion.isReady());
    }



    //Change this accordingly if csv changed
    @Test
    public void testMapQuery() {
        module_mapping.Companion.init(context, "index_mapping.csv");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(0).getFirst(), "DF");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(0).getSecond(), "DERMATOFIBROMA");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(1).getFirst(), "BCC");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(1).getSecond(), "BASAL CELL CAMINOMA");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(2).getFirst(), "MEL");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(2).getSecond(), "MELANOMA");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(3).getFirst(), "SCC");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(3).getSecond(), "SQUAMOUS CELL CARCINOMAS");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(4).getFirst(), "NV");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(4).getSecond(), "MELANOCYTIC NEVUS");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(5).getFirst(), "ERROR_ABBR");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(5).getSecond(), "ERROR_TITL");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(-1).getFirst(), "ERROR_ABBR");
        Assert.assertEquals(module_mapping.Companion.getPairFromIndex(-1).getSecond(), "ERROR_TITL");
/*      0, DF, DERMATOFIBROMA
        1, BCC, BASAL CELL CAMINOMA
        2, MEL, MELANOMA
        3, SCC, SQUAMOUS CELL CARCINOMAS
        4, NV, MELANOCYTIC NEVUS*/
    }
}
