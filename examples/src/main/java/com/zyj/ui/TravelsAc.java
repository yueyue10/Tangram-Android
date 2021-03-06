package com.zyj.ui;

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
import com.tmall.wireless.tangram.TangramBuilder;
import com.tmall.wireless.tangram.TangramEngine;
import com.tmall.wireless.tangram.example.R;
import com.tmall.wireless.tangram.example.data.TRAVELITEM;
import com.tmall.wireless.tangram.example.data.TestView;
import com.tmall.wireless.tangram.example.data.VVTEST;
import com.tmall.wireless.tangram.example.support.SampleClickSupport;
import com.tmall.wireless.tangram.util.IInnerImageSetter;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;
import com.zyj.ZUtils;
import com.zyj.retrofit.AppConfig;
import com.zyj.retrofit.GetRequest_Interface;
import com.zyj.retrofit.Translation;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by zhaoyuejun on 2018/5/12.
 */

public class TravelsAc extends BaseActivity {

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
                Picasso.with(TravelsAc.this.getApplicationContext()).load(url).into(view);
            }
        }, ImageView.class);
        mHandler = new Handler(getMainLooper());
        innerBuilder = TangramBuilder.newInnerBuilder(this);
        innerBuilder.registerCell(1, TestView.class);
        innerBuilder.registerVirtualView("TravelItem");
        innerBuilder.registerVirtualView("vvtest");
        tangramEngine = innerBuilder.build();
        tangramEngine.setVirtualViewTemplate(TRAVELITEM.BIN);
        tangramEngine.setVirtualViewTemplate(VVTEST.BIN);
        tangramEngine.getService(VafContext.class).setImageLoaderAdapter(new ImageLoader.IImageLoaderAdapter() {

            private List<TravelsAc.ImageTarget> cache = new ArrayList<TravelsAc.ImageTarget>();

            @Override
            public void bindImage(String uri, final ImageBase imageBase, int reqWidth, int reqHeight) {
                RequestCreator requestCreator = Picasso.with(TravelsAc.this).load(uri);
                Log.d("TravelsAc", "bindImage request width height " + reqHeight + " " + reqWidth);
                if (reqHeight > 0 || reqWidth > 0) {
                    requestCreator.resize(reqWidth, reqHeight);
                }
                TravelsAc.ImageTarget imageTarget = new TravelsAc.ImageTarget(imageBase);
                cache.add(imageTarget);
                requestCreator.into(imageTarget);
            }

            @Override
            public void getBitmap(String uri, int reqWidth, int reqHeight, final ImageLoader.Listener lis) {
                RequestCreator requestCreator = Picasso.with(TravelsAc.this).load(uri);
                Log.d("TravelsAc", "getBitmap request width height " + reqHeight + " " + reqWidth);
                if (reqHeight > 0 || reqWidth > 0) {
                    requestCreator.resize(reqWidth, reqHeight);
                }
                TravelsAc.ImageTarget imageTarget = new TravelsAc.ImageTarget(lis);
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
//        request1();
        String json = new String(ZUtils.getAssertsFile(this, "data_travels.json"));
        JSONArray data = null;
        try {
            data = new JSONArray(json);
            tangramEngine.setData(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 第二种请求，得到原始的json数据
     */
    public void request1() {
        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.baseUrl) // 设置 网络请求 Url
                .build();
        // 步骤5:创建 网络请求接口 的实例
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);
        //对 发送请求 进行封装
        Call<ResponseBody> call = request.getTravelDetails();
        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<ResponseBody>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // 步骤7：处理返回的数据结果
                try {
                    String jsonStr = new String(response.body().bytes());
                    KLog.d(jsonStr);
                    JSONArray data = new JSONArray(jsonStr);
                    tangramEngine.setData(data);
                } catch (IOException e) {
                    e.printStackTrace();
                    KLog.e(Log.getStackTraceString(e));
                } catch (JSONException e) {
                    e.printStackTrace();
                    KLog.e(Log.getStackTraceString(e));
                }
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                KLog.e("连接失败");
            }
        });
    }

}
