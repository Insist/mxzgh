package com.mxzgh;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
public class CopyBook {

    static CloseableHttpClient client ;
    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "info");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "info");
        client = HttpClientBuilder.create().build();
    }

    public static void main(String[] args) {
        String uri = "http://www.wenku8.com/novel/{0}/{1,number,#}/";
        for(int i = 2050;i<2200;i++){
            try {
                printUrl( MessageFormat.format(uri, i / 1000, i));
                Thread.sleep(300);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void printUrl(String uri) {
        HttpGet httpPost = new HttpGet(uri);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            String title = dom.getElementById("title").html();
            String auth = dom.getElementById("info").html().split("：")[1];
            boolean result = false;
            for(int i = 1;i<4;i++){
                result = uploadBook(title, auth);
                if(result)break;
            }
            if(result==false){
                System.out.println("error upload:"+title);
                return;
            }
            String bookName = "";

            boolean start = false;
            for(Element d:dom.getElementsByTag("td")){
                Elements as = d.getElementsByTag("a");
                if(as.size()<1){
                    if(!"&nbsp;".equals(d.html())){
                        bookName=d.html();
                    }
                    continue;
                }
                String href = as.get(0).attr("href");
//                if(!href.equals("22760.htm")){
//                    if(!start){
//                        continue;
//                    }
//                }
                start =true;
                printPage( uri, href,bookName+" "+as.get(0).html(),title);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printPage(String uri, String href, String chaName,String title) {
        HttpGet httpPost = new HttpGet(uri+href);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            String text = dom.getElementById("content").html().replace("<br />", "");
            boolean result = false;
            if(text.length()<75000){
                for(int i = 1;i<4;i++){
                    result = uploadChapter(href,title,chaName,text);
                    if(result)break;
                }
            }else {
                for(int j=0;j<text.length()/75000+1;j++){
                    for(int i = 1;i<4;i++){
                        result = uploadChapter(href,title,chaName+"("+(j+1)+")",text.substring(j*75000,Math.min((j+1)*75000,text.length()-1)));
                        if(result)break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean uploadBook(String name,String auth) {
        System.out.println("upload:"+name);
        HttpPost httpPost = new HttpPost("http://magicindexlib.890m.com/upload.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("method", "addBook"));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("auth", auth));
            params.add(new BasicNameValuePair("source", "wenku8"));
            httpPost.setHeader("User-Agent", "Mozilla");
            httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            System.out.println("upload:"+rs);
            if(rs.indexOf("添加成功")==-1){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean uploadChapter(String url,String name,String chapter,String text) {
        System.out.println("upload:"+name+":"+chapter);
        HttpPost httpPost = new HttpPost("http://magicindexlib.890m.com/upload.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("method", "addChapter"));
            params.add(new BasicNameValuePair("book", name));
            params.add(new BasicNameValuePair("chapter", chapter));
            params.add(new BasicNameValuePair("text", text));
            params.add(new BasicNameValuePair("uri", url));

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
