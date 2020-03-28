package com.csquared.bgcloud.resolver;

import android.util.Log;

import com.csquared.bgcloud.json.CourseContent;
import com.csquared.bgcloud.json.DownloadableVideoResponse;
import com.csquared.bgcloud.net.OkHttpBuilder;
import com.csquared.bgcloud.statics.Session;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadLinkRetriever extends Thread {

    private int type;
    private String versionCode, chapterId, subChapterId, serviceId;
    private int serviceType;
    private Callback callback;

    public DownloadLinkRetriever(int type, String versionCode, String chapterId, String subChapterId,
                                 String serviceId, int serviceType, Callback callback) {
        this.type = type;
        this.versionCode = versionCode;
        this.chapterId = chapterId;
        this.subChapterId = subChapterId;
        this.serviceId = serviceId;
        this.serviceType = serviceType;
        this.callback = callback;
        Log.d("TYPE", "run: " + type);
    }

    public interface Callback {
        void onResponse(String link);
        void onError();
    }

    @Override
    public void run() {
        if (type == CourseContent.TYPE_VIDEO) {
            String api = "http://kczystudy.edufe.com.cn/lms-study/getVideoJson?" +
                    "versionCode=" + versionCode +
                    "&chapterId=" + chapterId +
                    "&subChapterId=" + subChapterId +
                    "&serviceId=" + serviceId +
                    "&serviceType=" + serviceType;
            //Log.d("API", "run: " + api);
            OkHttpClient client = new OkHttpBuilder().createNoRedirect();
            Request request = new Request.Builder()
                    .url(api).addHeader("Cookie", Session.cookie).build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    callback.onError();
                    //Log.d("LINK", "onFailure: ");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String res = response.body().string();
                    //Log.d("LINK", "onResponse: " + res);
                    DownloadableVideoResponse resObj = new Gson().fromJson(res, DownloadableVideoResponse.class);
                    if (resObj == null || resObj.sources.size() == 0) {
                        callback.onError();
                        return;
                    }
                    callback.onResponse(resObj.sources.get(0).file);
                }
            });
        }

        if (type == CourseContent.TYPE_DOCUMENT) {
            String api = "http://kczystudy.edufe.com.cn/lms-study/emptyCss/showViewPage?" +
                    "versionCode=" + versionCode +
                    "&chapterId=" + chapterId +
                    "&subChapterId=" + subChapterId +
                    "&serviceId=" + serviceId +
                    "&serviceType=" + serviceType;
            //Log.d("API", "run: " + api);
            OkHttpClient client = new OkHttpBuilder().createNoRedirect();
            Request request = new Request.Builder()
                    .url(api).addHeader("Cookie", Session.cookie).build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    callback.onError();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String res = response.body().string();
                    //Log.d("LINK", "onResponse: " + res);
                    Document doc = Jsoup.parse(res);
                    Element iFrame = doc.getElementsByTag("iframe").first();
                    if (iFrame == null) {
                        callback.onError();
                        return;
                    }
                    String[] args = iFrame.attr("src").split("&furl=");
                    if (args.length != 2) {
                        callback.onError();
                        return;
                    }
                    callback.onResponse(args[1]);
                }
            });
        }
    }
}
