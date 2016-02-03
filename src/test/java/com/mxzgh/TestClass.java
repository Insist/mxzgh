package com.mxzgh;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */
public class TestClass {

    private static CloseableHttpClient client = HttpClientBuilder.create().build();

    public static void main(String[] args) {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "info");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "info");
        String uri = "http://www.wenku8.com/novel/{0}/{1,number,#}/";
        for(int i = 1000;i<2202;i++){
            try {
                printUrl( MessageFormat.format(uri, i / 1000, i));
                Thread.sleep(300);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void printUrl( String uri) {
        HttpGet httpPost = new HttpGet(uri);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            if(dom==null|| dom.getElementById("title")==null||dom.getElementById("info")==null){
                return;
            }
            System.out.println(MessageFormat.format("<a target=''_blank'' href=''{0}''>{1} {2}</a><br/>", uri, dom.getElementById("title").html(), dom.getElementById("info").html()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
