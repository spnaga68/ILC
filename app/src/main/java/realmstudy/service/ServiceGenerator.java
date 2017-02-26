package realmstudy.service;

import android.util.Base64;

import realmstudy.data.CommanData;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSink;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by developer on 8/31/16.
 */
public class
ServiceGenerator {
    private final OkHttpClient.Builder httpClient;
    private final Retrofit.Builder builder;

    public ServiceGenerator() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        Base64EncodeRequestInterceptor b=new Base64EncodeRequestInterceptor();

        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS);
        httpClient.interceptors().add(logging);
      //  httpClient.interceptors().add(b);
        //  httpClient.addInterceptor(b);



        builder =
                new Retrofit.Builder()
                        .baseUrl(CommanData.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
    }

    public ServiceGenerator(String Url) {
        // http://192.168.1.115:1021/mobileapi115/supervisor_assignedjobs/dGF4aV9hbGw=/
        //  final String API_BASE_URL = "http://192.168.1.115:1021/mobileapi115/";
        //  final String API_BASE_URL = "http://192.168.1.65:1007/mobileapi115/";
        CommanData.API_BASE_URL = Url;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        Base64EncodeRequestInterceptor b=new Base64EncodeRequestInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder().connectTimeout(40, TimeUnit.SECONDS).readTimeout(40, TimeUnit.SECONDS);
        httpClient.interceptors().add(logging);
       // httpClient.interceptors().add(b);
        builder =
                new Retrofit.Builder()
                        .baseUrl(CommanData.API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());
    }

    public <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }



    public class Base64EncodeRequestInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();
            System.out.println("*******"+originalRequest.method());
            if (originalRequest.method().equalsIgnoreCase("POST")) {
                builder = originalRequest.newBuilder()
                        .method(originalRequest.method(), encode(originalRequest.body()));
                System.out.println("*******"+originalRequest.method()+"____"+originalRequest.body());
            }


            return chain.proceed(builder.build());
        }

        private RequestBody encode(final RequestBody body) {
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    Buffer buffer = new Buffer();
                    body.writeTo(buffer);
                    byte[] encoded = Base64.encode(buffer.readByteArray(), Base64.DEFAULT);
                    sink.write(encoded);
                    buffer.close();
                    sink.close();
                    System.out.println("*******eeee"+encoded.toString());
                }
            };
        }
    }

}

