package com.pitts.photo_detector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.test.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class test_module_pytorch {

    private final Context appContext = InstrumentationRegistry.getTargetContext();
    private final Context androidTestContext = InstrumentationRegistry.getContext();
    private final int timeout = 10000;
    private final kotlin.Pair<Integer, Integer> resizeDimension = new kotlin.Pair<>(64, 72);

    private Integer injectToTorch(String fn, kotlin.Pair<Boolean, Boolean> resizeParam) {
        Integer toReturn = null;
        Callable<Integer> callable = () -> {
            Log.d("Q_TEST_MODULE_PYTORCH", "MATCHTEST() TESTING "
                    + fn + (resizeParam.getFirst() ? (" RESIZED TO "
                    + resizeDimension.getFirst()
                    + " * " + resizeDimension.getSecond()
                    + (resizeParam.getSecond() ? " W/" : " W/O")
                    + " FILTER") : " RAW"));
            InputStream inputStream = null;
            try {
                inputStream = androidTestContext.getAssets().open("TEST_PICTURES/" + fn);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (resizeParam.getFirst()) {
                bitmap = Bitmap.createScaledBitmap(bitmap, resizeDimension.getFirst(),
                        resizeDimension.getSecond(), resizeParam.getSecond());
            }
            return module_pytorch.Companion.runInference(bitmap, androidTestContext);
        };
        ExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        Future<Integer> future = scheduledExecutorService.submit(callable);
        try {
            toReturn = future.get(timeout, TimeUnit.MILLISECONDS);
            Log.d("Q_TEST_MODULE_PYTORCH", "MATCHTEST() INFERENCE RETURNED A DECISION");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.d("Q_TEST_MODULE_PYTORCH", "MATCHTEST() GENERAL INTERRUPTIONS OCCURED");
        } catch (TimeoutException e) {
            future.cancel(true);
            Log.d("Q_TEST_MODULE_PYTORCH", "MATCHTEST() TIME-OUT THREAD CANCELLED");
        }
        return toReturn;
    }

    @Before

    public void initParam() {
        module_param.Companion.initParametersByDefault(appContext);
    }
/*

    @Test
    public void bccTest() {

    }
*/

    @Test
    public void matchTest() {
        List<kotlin.Pair<Boolean, Boolean>> resizeParamSequence = Arrays.asList(
                new kotlin.Pair<>(false, false),//NO RESIZE
                new kotlin.Pair<>(true, false),//RESIZE WITHOUT FILTER
                new kotlin.Pair<>(true, true)//RESIZE WITH FILTER
        );
        List<String> jpgNameSequence = Arrays.asList(
                "TEST/_TEST_01.jpg",
                "TEST/_TEST_02.jpg",
                "TEST/_TEST_OVERSIZE.jpg",
                "TEST/_TEST_UNDERSIZE.jpg"
        );
        jpgNameSequence.forEach((it_jns) -> {
            resizeParamSequence.forEach((it_rps) -> {
                injectToTorch(it_jns, it_rps);
            });
        });
    }
/*
    @Test
    public void matchTest() throws IOException {
        InputStream inputStream = androidTestContext.getAssets().open("TEST_PICTURES/TEST/_TEST_01.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        Log.d("Q_TEST_MODULE_PYTORCH", "MATCHTEST() TESTING RAW 01 JPG");
        module_pytorch.Companion.runInference(bitmap, androidTestContext);
        bitmap = Bitmap.createScaledBitmap(bitmap, 72, 64, false);
        Log.d("Q_TEST_MODULE_PYTORCH", "MATCHTEST() TESTING RESIZED 01 JPG WITH FILTER ON");
        module_pytorch.Companion.runInference(bitmap, androidTestContext);
        inputStream = androidTestContext.getAssets().open("TEST_PICTURES/TEST/_TEST_02.jpg");
    }

    @Test
    public void boundaryTest() throws IOException {
        InputStream inputStream = androidTestContext.getAssets().open("TEST_PICTURES/TEST/_TEST_UNDERSIZE.jpg");
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        Log.d("Q_TEST_MODULE_PYTORCH", "MATCHTEST() TESTING UNDERSIZED JPG");
        module_pytorch.Companion.runInference(bitmap, androidTestContext);
        inputStream = androidTestContext.getAssets().open("TEST_PICTURES/TEST/_TEST_OVERSIZE.jpg");
        Log.d("Q_TEST_MODULE_PYTORCH", "MATCHTEST() TESTING OVERSIZED JPG");
        module_pytorch.Companion.runInference(bitmap, androidTestContext);
    }*/
}
