package com.mxzgh;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/4.
 */
public class ReCopyBook2 {

    static CloseableHttpClient client ;
    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "info");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "info");
        client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(15000).setConnectionRequestTimeout(15000)
                .setSocketTimeout(15000).build()).build();
    }

    public static void main(String[] args) {
        String uri = "http://www.yidm.com/article/html/{0}/{1,number,#}/";
        for(int i = 5;i<2000;i++){
            try {
                boolean result = false;
                for(int j = 1;j<4;j++){
                    result = printUrl(MessageFormat.format(uri, i / 1000, i),i);
                    if(result)break;
                    Thread.sleep(1000);
                }
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    static int count = 0;

    private static boolean printUrl(String uri,Integer is) {
        HttpGet httpPost = new HttpGet(uri);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            String info = dom.getElementsByTag("title").html();
            String title = info.split(" - ")[1];
            String auth = info.split(" - ")[2];
            String wenku = info.split(" - ")[3];
            System.out.println(title+"-"+auth);

            String result = "";
            for(int i = 1;i<4;i++){
                result = getLast(title);
                if(result.length()>0)break;
            }

            String chaFirstName = "";
            for(Element d:dom.getElementsByClass("volume")){
                Elements as = d.getElementsByClass("vname");
                chaFirstName=as.get(0).html();
                for(Element ds:d.getElementsByTag("a")){
                    String href = ds.attr("href");
                    String chaName = chaFirstName+" "+ds.html();
                    if(result.indexOf(chaName)!=-1){
                        boolean rss = false;
                        for(int i = 1;i<4;i++) {
                            rss = updateLastId(title, href);
                            if(rss)break;
                        }
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String getLast(String name) {
        HttpPost httpPost = new HttpPost("http://magicindexlib.com/getLastCha.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("book", name));
            httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            httpPost.setHeader("User-Agent", "Mozilla");
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            return rs;
        } catch (IOException e) {
            return "";
        }
    }

    private static boolean updateLastId(String name,String uri) {
        HttpPost httpPost = new HttpPost("http://magicindexlib.com/updateLastId.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("book", name));
            params.add(new BasicNameValuePair("uri", uri));
            httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            httpPost.setHeader("User-Agent", "Mozilla");
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            if(rs.indexOf("success")!=-1){
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
