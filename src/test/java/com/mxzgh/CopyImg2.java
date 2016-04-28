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
public class CopyImg2 {

    static CloseableHttpClient client ;
    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "info");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "info");
        client = HttpClientBuilder.create().build();
    }

    public static void main(String[] args) {
        String uri = "http://www.yidm.com/article/html/{0}/{1,number,#}/";
        for(int i = 1172;i<1173;i++){
            try {
                printUrl( MessageFormat.format(uri, i / 1000, i));
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static boolean printUrl(String uri) {
        HttpGet httpPost = new HttpGet(uri);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            String info = dom.getElementsByTag("title").html();
            String title = info.split(" - ")[1];
            String auth = info.split(" - ")[2];
            String wenku = info.split(" - ")[3];
            String lastChaUrl = "";
            for(int i = 1;i<4;i++){
                lastChaUrl = getLast(title);
                if(lastChaUrl.length()>0)break;
            }
            if(lastChaUrl.indexOf("error")!=-1){
                return true;
            }
//            boolean result = false;
//            for(int i = 1;i<4;i++){
//                result = uploadBook(title, auth);
//                if(result)break;
//            }
//            if(result==false){
//                System.out.println("error upload:"+title);
//                return;
//            }
            String bookName = "";

            boolean start = false;
            for(Element d:dom.getElementsByTag("a")){
                String href = d.attr("href");
                String chName = d.html();
                if(chName.indexOf("插图")!=-1||chName.indexOf("彩图")!=-1||chName.indexOf("插画")!=-1||chName.indexOf("彩插")!=-1){
                    printPage(uri, href, d.html(), title);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void printPage(String uri, String href, String chaName,String title) {
        HttpGet httpPost = new HttpGet(href);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            Element e = dom.getElementsByClass("bd").get(0);
            String allName = e.getElementsByTag("h4").html();
            chaName = allName.replace(chaName,"");
            for(Element img:e.getElementsByTag("img")){
                img.attr("src",img.attr("osrc"));
            }
            String text = e.html();
            boolean result = false;
            for(int i = 1;i<4;i++){
                result = uploadChapter(title,allName,text);
                if(result)break;
            }
//            if(text.length()<75000){
//                for(int i = 1;i<4;i++){
//                    result = uploadChapter(title,chaName,text);
//                    if(result)break;
//                }
//            }else {
//                for(int j=0;j<text.length()/75000+1;j++){
//                    for(int i = 1;i<4;i++){
//                        result = uploadChapter(title,chaName+"("+(j+1)+")",text.substring(j*75000,Math.min((j+1)*75000,text.length()-1)));
//                        if(result)break;
//                    }
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static boolean uploadBook(String name,String auth) {
        System.out.println("upload:"+name);
        HttpPost httpPost = new HttpPost("http://magicindexlib.com/upload.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("method", "addBook"));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("auth", auth));
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

    private static boolean uploadChapter(String name,String chapter,String text) {
        System.out.println("upload:" + name + ":" + chapter);
        HttpPost httpPost = new HttpPost("http://magicindexlib.com/uploadImage.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("method", "editImage"));
            params.add(new BasicNameValuePair("book", name));
            params.add(new BasicNameValuePair("chapter", chapter));
            params.add(new BasicNameValuePair("text", text));
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
    private static String getLast(String name) {
        HttpPost httpPost = new HttpPost("http://magicindexlib.com/getLastChaUrl.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("book", name));
            params.add(new BasicNameValuePair("start", "1970"));
            params.add(new BasicNameValuePair("end", "3000"));
            httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            httpPost.setHeader("User-Agent", "Mozilla");
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            return rs;
        } catch (IOException e) {
            return "";
        }
    }
}
