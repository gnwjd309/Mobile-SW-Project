package com.example.mobilepjapp;


import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpConnection {
    String url = "http://210.119.32.224:5000/";
    String result;

    public String httpRequest(String link, JSONObject reqForm) {
        url = url + link;
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqForm.toString());

        result = null;
        postRequest(url, body);

        int count = 0;
        while(result==null){
            count++;
        }
        return result;
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                e.printStackTrace();
                result = "Failure";
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    final String responseString = response.body().string().trim();
                    if (responseString.equals("Failure")) {
                        result = "Failure";
                    }
                    else {   // 성공했을 때
                        System.out.println("Success : " + responseString);
                        result = responseString;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
