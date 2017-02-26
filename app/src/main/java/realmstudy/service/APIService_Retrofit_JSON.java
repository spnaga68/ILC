//package com.example.developer.realmstudy.service;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.Taximobility.R;
//import com.Taximobility.interfaces.APIResult;
//import com.Taximobility.util.NetworkStatus;
//import com.Taximobility.util.SessionSave;
//import com.Taximobility.util.TaxiUtil;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.HashMap;
//
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//
///**
// * @author developer This AsyncTask used to communicate the application with server through Volley framework. Here the response completely in JSON format. Constructor get the input details List<NameValuePair>,POST or GET then url. In pre execute,Show the progress dialog. In Background,Connect and get the response. In Post execute, Return the result with interface. This class call the API without any progress on UI.
// */
//public class APIService_Retrofit_JSON {
//    HashMap<String, String> map;
//    final String check_internet = "Check your Internet Connection";
//    final String please_try_again = "Please try again later!";
//    public Dialog mProgressdialog;
//    public Context mContext;
//    private boolean isSuccess = true;
//    private boolean GetMethod;
//    private Dialog mDialog;
//    private JSONObject data;
//    public APIResult response;
//    public boolean wholeURL;
//    String result = "";
//    private String url_type;
//
//    public APIService_Retrofit_JSON(Context ctx, APIResult res, JSONObject j, boolean getmethod) {
//        mContext = ctx;
//        response = res;
//        this.data = j;
//        GetMethod = getmethod;
//    }
//
//    public APIService_Retrofit_JSON(Context ctx, APIResult res, String j, boolean getmethod) {
//        mContext = ctx;
//        response = res;
//        JSONObject jobj = null;
//        try {
//            if (!getmethod)
//                jobj = new JSONObject(j);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        this.data = jobj;
//        GetMethod = getmethod;
//    }
//
//
//    public APIService_Retrofit_JSON(Context ctx, APIResult res, JSONObject j, boolean getmethod, String url) {
//        mContext = ctx;
//        response = res;
//        this.data = j;
//        GetMethod = getmethod;
//        String type[] = url.split("type=");
//        if (type.length > 1)
//            url_type = type[1];
//        else {
//            wholeURL = true;
//            url_type = url;
//        }
//    }
//
//    public APIService_Retrofit_JSON(Context ctx, APIResult res, boolean getmethod, String url) {
//        mContext = ctx;
//        response = res;
//        this.data = null;
//        GetMethod = getmethod;
////        String type[] = url.split("type=");
////        if (type.length > 1)
////            url_type = type[1];
////        else {
//        wholeURL = true;
//        url_type = url;
//        // }
//    }
//
//    public APIService_Retrofit_JSON(Context ctx, APIResult res, boolean getmethod) {
//        mContext = ctx;
//        response = res;
//        GetMethod = getmethod;
//    }
//
//    //@Override
//    protected void onPreExecute() {
//        // TODO Auto-generated method stub
////		super.onPreExecute();
//        showDialog();
//        doInBackground();
//    }
//
//    public void showDialog() {
//        try {
//            if (NetworkStatus.isOnline(mContext)) {
//                View view = View.inflate(mContext, R.layout.progress_bar, null);
//                mDialog = new Dialog(mContext, R.style.dialogwinddow);
//                mDialog.setContentView(view);
//                mDialog.setCancelable(false);
//                mDialog.show();
//
//                ImageView iv = (ImageView) mDialog.findViewById(R.id.giff);
//                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(iv);
//                Glide.with(mContext)
//                        .load(R.raw.loading_anim)
//                        .into(imageViewTarget);
//
//            } else {
//
//            }
//        } catch (Exception e) {
//
//        }
//
//    }
//
//    public void closeDialog() {
//        try {
//            if (mDialog != null)
//                if (mDialog.isShowing())
//                    mDialog.dismiss();
//        } catch (Exception e) {
//
//        }
//    }
//
//    //@Override
//    protected void doInBackground() {
//        // TODO Auto-generated method stub
//        if (!NetworkStatus.isOnline(mContext)) {
//            isSuccess = false;
//            response.getResult(false, mContext.getResources().getString(R.string.check_internet_connection));
//            result = mContext.getResources().getString(R.string.check_internet_connection);
//            //return result;
//        } else {
//            if (GetMethod) {
//                CoreClient client = null;
//                System.out.println("rrc_____get" + url_type + "___" + wholeURL + "___" + data);
//
//                client = new ServiceGenerator().createService(CoreClient.class);
//                Call<ResponseBody> coreResponse = null;
//                if (!wholeURL)
//                    coreResponse = client.coreDetails(TaxiUtil.COMPANY_KEY,url_type, TaxiUtil.DYNAMIC_AUTH_KEY);
//                else
//                    coreResponse = client.getWhole(url_type);
//                coreResponse.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                        String data = null;
//                        closeDialog();
//                        try {
//                            data = response.body().string();
//                            APIService_Retrofit_JSON.this.response.getResult(true, data);
//
//
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println("rrc_____get" + data);
//                        if (data != null) {
//                            System.out.println("rrc_____get" + data);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        t.printStackTrace();
//                        closeDialog();
//                    }
//                });
//
//            } else {
//                CoreClient client = new ServiceGenerator().createService(CoreClient.class);
//                RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (data).toString());
//
//                Call<ResponseBody> coreResponse = client.updateUser(TaxiUtil.COMPANY_KEY, TaxiUtil.DYNAMIC_AUTH_KEY, body, url_type, SessionSave.getSession("Lang", mContext));
//                coreResponse.enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
//                        String data = null;
//                        closeDialog();
//                        try {
//                            if (response.body() != null) {
//                                data = response.body().string();
//                                APIService_Retrofit_JSON.this.response.getResult(true, data);
//
//                                try {
//                                    JSONObject jj = new JSONObject(data);
//                                    if (jj.getString("status").trim().equals("-4"))
//                                        new GetAuthKey();
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            } else {
//                                Toast.makeText(mContext, mContext.getString(R.string.server_con_error), Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println("rrc_____post" + data);
//                        if (data != null) {
//                            System.out.println("rrc_____post" + data);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        t.printStackTrace();
//                        closeDialog();
//                    }
//                });
//            }
//
//        }
//
//    }
//
////			params[0] = params[0].replace(" ", "%20");
////			String url = TaxiUtil.APIBase_Path + "lang=" + SessionSave.getSession("Lang", mContext) + "&" + params[0];
////			System.out.println("------ur"+url+"----"+data);
////			Log.d("Pass Api request", "" + url + "\ndata" + data);
////			if (GetMethod) {
////				requestingMethod = 0;
////			} else {
////				requestingMethod = 1;
////			}
////
////			JOR = new JsonObjectRequest(Request.Method.POST, url, data, new Response.Listener<JSONObject>() {
////				@Override
////				public void onResponse(JSONObject jresponse)
////				{
////					if (mDialog != null ) {
////						if(mDialog.isShowing())
////							try {
////								mDialog.dismiss();
////							} catch (Exception e) {
////								e.printStackTrace();
////							}
////						mDialog = null;
////					}
////					response.getResult(isSuccess, jresponse.toString());
////					result = jresponse.toString();
////					System.out.println("Pass Api response" + result);
////				}
////			}, new Response.ErrorListener() {
////				@Override
////				public void onErrorResponse(VolleyError error)
////				{
////					if (mDialog != null || mDialog.isShowing()) {
////						mDialog.dismiss();
////						mDialog = null;
////					}
////					String errorMessage = "Please Try Again, ";
////					if (error instanceof NetworkError) {
////						errorMessage += "Network Connection Error ";
////					} else if (error instanceof ClientError) {
////						errorMessage += "Client Error ";
////					} else if (error instanceof ServerError) {
////						errorMessage += "Server connection Error ";
////					} else if (error instanceof AuthFailureError) {
////						errorMessage += "AuthFailureError ";
////					} else if (error instanceof ParseError) {
////						errorMessage += "ParseError ";
////					} else if (error instanceof NoConnectionError) {
////						errorMessage += "Network Connection Error ";
////					} else if (error instanceof TimeoutError) {
////						errorMessage += "Request Timeout";
////					}
////					response.getResult(false, errorMessage);
////				}
////			})
////			{
////				@Override
////				protected Map<String, String> getParams()
////				{
////					Map<String, String> param = new HashMap<String, String>();
////					if (!GetMethod) {
////						param.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
////						return param;
////					}
////					return param;
////				}
////			};
////			JOR.setRetryPolicy(new DefaultRetryPolicy(34000,0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
////			AppController.getInstance().addToRequestQueue(JOR);
////			return result;
////		}
//
//
//    public void execute(String url) {
//        String[] type = url.split("=");
//        this.url_type = type[1];
//        onPreExecute();
//
//    }
//
//    public void execute() {
//
//        onPreExecute();
//
//    }
//
//
//    private class GetAuthKey implements APIResult {
//
//        public GetAuthKey() {
//            // TODO Auto-generated constructor stub
//            String url = "type=get_authentication";
//            JSONObject j = new JSONObject();
//            try {
//                System.out.println("domianurl_________" + SessionSave.getSession("base_url", mContext).replace("mobileapi117/index/", "").replace("http://", "").replace("/", ""));
//                j.put("mobilehost", SessionSave.getSession("base_url", mContext).replace("mobileapi117/index/", "").replace("http://", "").replace("/", ""));
//                j.put("encode", SessionSave.getSession("encode", mContext));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            System.out.println("naga______callinghiii" + url);
//            new APIService_Retrofit_JSON(mContext, this, j, false).execute(url);
//        }
//
//
//        @Override
//        public void getResult(final boolean isSuccess, final String result) {
//            // TODO Auto-generated method stub
//            System.out.println("naga______callingrrrr" + result);
//            try {
//                if (isSuccess) {
//                    final JSONObject json = new JSONObject(result);
//                    if (json.getInt("status") == 1) {
//                        Long tsLong = System.currentTimeMillis() / 1000;
//                        String ts = tsLong.toString();
//                        SessionSave.saveSession("encode", json.getString("encode"), mContext);
//                        TaxiUtil.DYNAMIC_AUTH_KEY = SessionSave.getSession("encode", mContext);
//                        TaxiUtil.API_BASE_URL = SessionSave.getSession("base_url", mContext);
//                        TaxiUtil.COMPANY_KEY = SessionSave.getSession("api_key", mContext);
//                        SessionSave.saveSession("auth_last_call_type", String.valueOf(ts), mContext);
//
//                    }
//                    // alert_view(this, "" + getResources().getString(R.string.message), "" + json.getString("message"), "" + getResources().getString(R.string.ok), "");
////                        if (json.getInt("status") == 2)
////                            alert_view(this, "" + getResources().getString(R.string.message), "" + json.getString("message"), "" + getResources().getString(R.string.ok), "");
//                } else {
////                        runOnUiThread(new Runnable() {
////                            public void run() {
////                                ShowToast(this, result);
////                            }
////                        });
//                }
//            } catch (final JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        // }
//    }
//
//
//}