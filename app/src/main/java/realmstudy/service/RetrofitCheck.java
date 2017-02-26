package realmstudy.service;//package com.Taximobility.service;
//
//import com.Taximobility.util.TaxiUtil;
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
///**
// * Created by developer on 10/19/16.
// */
//public class RetrofitCheck {
//
//
//    public void getData(String request_json) {
//        CoreClient client = new ServiceGenerator().createService(CoreClient.class);
////        QueryMap f=new QueryMap();
//        Call<ResponseBody> coreResponse = client.coreDetails(TaxiUtil.COMPANY_KEY,TaxiUtil.DYNAMIC_AUTH_KEY);
//        coreResponse.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                String data = null;
//                try {
//                    data = response.body().string();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("rrc_____"+data);
//                if (data != null) {
//                    System.out.println("rrc_____"+data);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }
//
//
////    public void Login(JSONObject request_json) {
////        CoreClient client = new ServiceGenerator().createService(CoreClient.class);
//////        QueryMap f=new QueryMap();
////
////
////        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (request_json).toString());
////
////        Call<ResponseBody> coreResponse = client.updateUser(body, "passenger_login");
////        coreResponse.enqueue(new Callback<ResponseBody>() {
////            @Override
////            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
////                String data = null;
////                try {
////                    data = response.body().string();
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////                System.out.println("rrc_____"+data);
////                if (data != null) {
////                    System.out.println("rrc_____"+data);
////                }
////            }
////
////            @Override
////            public void onFailure(Call<ResponseBody> call, Throwable t) {
////                t.printStackTrace();
////            }
////        });
////    }
//
//}
