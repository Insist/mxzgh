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
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/1/4.
 */
public class CopyBookFromOther {

    static CloseableHttpClient client ;
    static Pattern p = Pattern.compile("[0-9]+[.]html");
    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "info");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "info");
        client = HttpClientBuilder.create().build();
    }

    public static void main(String[] args) {
        String uri = "http://www.shencou.com/read/{0}/{1,number,#}/";
        for(int i = 174;i<175;i++){
            try {
                printUrl( MessageFormat.format(uri, i / 1000, i),i);
                Thread.sleep(300);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void printUrl(String uri,Integer is) {
        HttpGet httpPost = new HttpGet(uri);
        System.out.println(uri);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            System.out.println(rs);
            Document dom = Jsoup.parse(rs);
            String info = dom.getElementsByTag("title").html();
            String title = info.split("\\|")[0].replace("插图", "").replace("小说", "")+"(测)";
            String auth = info.split("\\|")[1];

            if(auth == null||auth.trim().length()<1||auth.indexOf("ggo")!=-1||checkSF(is)){
                System.out.println("国产警告，小说名:"+title);
                return;
            }

            boolean start = false;
//            for(Element d:dom.getElementsByTag("a")){
//                String href = d.attr("href");
//                if(p.matcher(href).matches()){
//                    if(d.html().indexOf("第十五章")!=-1||d.html().indexOf("15")!=-1){
//                        System.out.println("国产警告，小说名:"+title);
//                        start = true;
//                    }
//                }
//            }
            if(start){
                return;
            }
//            System.out.println();
//            System.out.println(info.split("\\|")[1]);

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

            for(Element d:dom.getElementsByTag("a")){
                String href = d.attr("href");
                if(p.matcher(href).matches()){
                    printPage(uri, href, d.html(), title);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkSF(Integer i) {
        String uri = "http://www.shencou.com/books/read_{0,number,#}.html";
        HttpGet httpPost = new HttpGet(MessageFormat.format(uri, i));
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            for(Element d:dom.getElementsByTag("td")){
                if(d.html().indexOf("sf文库")!=-1 || d.html().indexOf("少女文库")!=-1){
                    return true;
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    private static void printPage(String uri, String href, String chaName,String title) {
        HttpGet httpPost = new HttpGet(uri+href);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            chaName = dom.getElementsByTag("H1").get(0).html().replace(title+" ","");
            String text = rs.split("<!--go-->")[1].split("<!--over-->")[0].replace("<br />","");
            boolean result = false;
            if(text.length()<75000){
                for(int i = 1;i<4;i++){
                    result = uploadChapter(title,chaName,text);
                    if(result)break;
                }
            }else {
                for(int j=0;j<text.length()/75000+1;j++){
                    for(int i = 1;i<4;i++){
                        result = uploadChapter(title,chaName+"("+(j+1)+")",text.substring(j*75000,Math.min((j+1)*75000,text.length()-1)));
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
        System.out.println("upload:"+name+":"+chapter);
        HttpPost httpPost = new HttpPost("http://magicindexlib.com/upload.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("method", "addChapter"));
            params.add(new BasicNameValuePair("book", name));
            params.add(new BasicNameValuePair("chapter", chapter));
            params.add(new BasicNameValuePair("text", text));

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
