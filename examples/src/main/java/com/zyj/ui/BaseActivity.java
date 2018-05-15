package com.zyj.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.socks.library.KLog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;
import com.zyj.retrofit.AppConfig;
import com.zyj.retrofit.GetRequest_Interface;
import com.zyj.retrofit.Translation;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zhaoyuejun on 2018/5/15.
 */

public class BaseActivity extends Activity {

    public static class ImageTarget implements Target {

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

    /**
     * 第一种请求，得到解析后的json数据
     */
    public void request() {
        //步骤4:创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        // 步骤5:创建 网络请求接口 的实例
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);
        //对 发送请求 进行封装
        Call<Translation> call = request.getCall();
        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<Translation>() {
            //请求成功时回调
            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                // 步骤7：处理返回的数据结果
                response.body().show();
            }

            //请求失败时回调
            @Override
            public void onFailure(Call<Translation> call, Throwable throwable) {
                KLog.e("连接失败");
            }
        });
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
                } catch (IOException e) {
                    e.printStackTrace();
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
