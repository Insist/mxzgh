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
public class UpdateBook {

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
        String uri = "http://www.wenku8.com/novel/{0}/{1,number,#}/";
        for(int i = 2300;i>=1;i--){
            try {
                boolean result = false;
                for(int j = 1;j<4;j++){
                    result = printUrl(MessageFormat.format(uri, i / 1000, i),i);
                    if(result)break;
                    System.out.println("retry id:" + i + ";times=" + j);
                    Thread.sleep(1000);
                }
                Thread.sleep(1000);
            }catch (Exception e){
                System.out.println("error book id:"+i);
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
            String title = dom.getElementById("title").html();
            String auth = dom.getElementById("info").html().split("：")[1];
            System.out.println(title+"-"+auth);

            String lastChaUrl = "";
            for(int i = 1;i<4;i++){
                lastChaUrl = getLast(title);
                if(lastChaUrl.length()>0)break;
            }
            if(lastChaUrl.indexOf("error")!=-1){
                return true;
            }
            lastChaUrl = lastChaUrl.split("-")[1];

            boolean start = false;
            String chaFirstName = "";
            for(Element d:dom.getElementsByTag("td")){
                Elements as = d.getElementsByTag("a");
                if(as.size()<1){
                    if(!"&nbsp;".equals(d.html())){
                        chaFirstName=d.html();
                    }
                    continue;
                }
                String href = as.get(0).attr("href");
                if(href.equals(lastChaUrl)) {
                    start =true;
                    continue;
                } else if(!start){
                    continue;
                }
                String chaName = chaFirstName+" "+as.get(0).html();
                System.out.println("更新章节:"+chaName);
                boolean rss = false;
                for(int i = 1;i<4;i++) {
                    rss = printPage(uri, href, chaName, title);
                    if(rss)break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String getLast(String name) {
        HttpPost httpPost = new HttpPost("http://magicindexlib.com/getLastChaUrl.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("book", name));
            params.add(new BasicNameValuePair("source", "wenku8"));
            params.add(new BasicNameValuePair("start", "1"));
            params.add(new BasicNameValuePair("end", "1930"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            httpPost.setHeader("User-Agent", "Mozilla");
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            return rs;
        } catch (IOException e) {
            return "";
        }
    }

    private static boolean printPage(String uri, String href, String chaName,String title) {
        HttpGet httpPost = new HttpGet(uri+href);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            String text = dom.getElementById("content").html().replace("<br />", "");
            boolean result = false;
            if(text.length()<75000){
                for(int i = 1;i<4;i++){
                    result = uploadChapter(title,chaName,text,href);
                    if(result)break;
                }
            }else {
                for(int j=0;j<text.length()/75000+1;j++){
                    for(int i = 1;i<4;i++){
                        result = uploadChapter(title,chaName+"("+(j+1)+")",text.substring(j*75000,Math.min((j+1)*75000,text.length()-1)),href);
                        if(result)break;
                    }
                }
            }
            Thread.sleep(1000);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static boolean uploadChapter(String name,String chapter,String text,String uri) {
        System.out.println("upload:"+name+":"+chapter);
        HttpPost httpPost = new HttpPost("http://magicindexlib.890m.com/upload.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("method", "addChapter"));
            params.add(new BasicNameValuePair("book", name));
            params.add(new BasicNameValuePair("chapter", chapter));
            params.add(new BasicNameValuePair("text", text));
            params.add(new BasicNameValuePair("uri", uri));
            httpPost.setHeader("User-Agent", "Mozilla");
            httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            if(rs.indexOf("添加成功")==-1){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
