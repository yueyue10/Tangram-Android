package com.zyj.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.libra.Utils;
import com.socks.library.KLog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.example.R;
import com.tmall.wireless.tangram.example.data.SimpleImgView;
import com.tmall.wireless.tangram.example.data.TRAVELITEM;
import com.tmall.wireless.tangram.example.data.TestView;
import com.tmall.wireless.tangram.example.data.VVTEST;
import com.tmall.wireless.tangram.example.support.SampleClickSupport;
import com.tmall.wireless.tangram.util.IInnerImageSetter;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;
import com.zyj.ZUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoyuejun on 2018/5/13.
 */

public class JdHomeAc extends Activity {

    private Handler mHandler;
    TangramEngine tangramEngine;
    TangramBuilder.InnerBuilder innerBuilder;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_travels);
        recyclerView = (RecyclerView) findViewById(R.id.main_view);
        TangramBuilder.init(this.getApplicationContext(), new IInnerImageSetter() {
            @Override
            public <IMAGE extends ImageView> void doLoadImageUrl(@NonNull IMAGE view, @Nullable String url) {
                Picasso.with(JdHomeAc.this.getApplicationContext()).load(url).into(view);
            }
        }, ImageView.class);
        mHandler = new Handler(getMainLooper());
        innerBuilder = TangramBuilder.newInnerBuilder(this);
        innerBuilder.registerCell(1, TestView.class);
        innerBuilder.registerCell(2, SimpleImgView.class);
        innerBuilder.registerVirtualView("TravelItem");
        innerBuilder.registerVirtualView("vvtest");
        tangramEngine = innerBuilder.build();
        tangramEngine.setVirtualViewTemplate(TRAVELITEM.BIN);
        tangramEngine.setVirtualViewTemplate(VVTEST.BIN);
        tangramEngine.getService(VafContext.class).setImageLoaderAdapter(new ImageLoader.IImageLoaderAdapter() {

            private List<JdHomeAc.ImageTarget> cache = new ArrayList<JdHomeAc.ImageTarget>();

            @Override
            public void bindImage(String uri, final ImageBase imageBase, int reqWidth, int reqHeight) {
                RequestCreator requestCreator = Picasso.with(JdHomeAc.this).load(uri);
                Log.d("JdHomeAc", "bindImage request width height " + reqHeight + " " + reqWidth);
                if (reqHeight > 0 || reqWidth > 0) {
                    requestCreator.resize(reqWidth, reqHeight);
                }
                JdHomeAc.ImageTarget imageTarget = new JdHomeAc.ImageTarget(imageBase);
                cache.add(imageTarget);
                requestCreator.into(imageTarget);
            }

            @Override
            public void getBitmap(String uri, int reqWidth, int reqHeight, final ImageLoader.Listener lis) {
                RequestCreator requestCreator = Picasso.with(JdHomeAc.this).load(uri);
                Log.d("JdHomeAc", "getBitmap request width height " + reqHeight + " " + reqWidth);
                if (reqHeight > 0 || reqWidth > 0) {
                    requestCreator.resize(reqWidth, reqHeight);
                }
                JdHomeAc.ImageTarget imageTarget = new JdHomeAc.ImageTarget(lis);
                cache.add(imageTarget);
                requestCreator.into(imageTarget);
            }
        });
        Utils.setUedScreenWidth(720);
        tangramEngine.addSimpleClickSupport(new SampleClickSupport());
//        engine.getService(VafContext.class)
//        engine.getService(BusSupport.class);
        //Step 6: enable auto load more if your page's data is lazy loaded
        tangramEngine.enableAutoLoadMore(true);

        //Step 7: bind recyclerView to engine
        tangramEngine.bindView(recyclerView);

        //Step 8: listener recyclerView onScroll event to trigger auto load more
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                tangramEngine.onScrolled();
            }
        });
        //Step 9: set an offset to fix card
        tangramEngine.getLayoutManager().setFixOffset(0, 0, 0, 0);
        //Step 10: get tangram data and pass it to engine
        String json = new String(ZUtils.getAssertsFile(this, "data_jdhome.json"));
        JSONArray data = null;
        try {
            data = new JSONArray(json);
            tangramEngine.setData(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static class ImageTarget implements Target {

        ImageBase mImageBase;
        ImageLoader.Listener mListener;

        public ImageTarget(ImageBase imageBase) {
            mImageBase = imageBase;
        }

        public ImageTarget(ImageLoader.Listener listener) {
            mListener = listener;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mImageBase.setBitmap(bitmap, true);
            if (mListener != null) {
                mListener.onImageLoadSuccess(bitmap);
            }
            KLog.d("onBitmapLoaded" + from);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            if (mListener != null) {
                mListener.onImageLoadFailed();
            }
            KLog.d("onBitmapFailed ");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            KLog.d("onPrepareLoad ");
        }
    }
}

