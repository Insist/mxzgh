package com.mxzgh;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
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

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/1/4.
 */
public class CopyBookFromOther2 {

    static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(CopyBookFromOther2.class);

    static CloseableHttpClient client ;
    static Pattern p = Pattern.compile("http://www.yidm.com/article/html/[0-9]/[0-9]+/[0-9]+[.]html");
    static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss -- ");
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
        for(int i = 2050;i<2100;i++){
                try {
                    boolean result = false;
                    for(int j = 1;j<4;j++){
                        result = printUrl(MessageFormat.format(uri, i / 1000, i),i);
                        if(result)break;
                        System.out.println("retry id:" + i + ";times=" + j);
                        if(j==3){
                            logger.info("book error,id="+i);
                        }
                        Thread.sleep(2000);
                    }
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
        }
    }

    private static boolean printUrl(String uri, Integer is) {
        HttpGet httpPost = new HttpGet(uri);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            String info = dom.getElementsByTag("title").html();
            System.out.println("<a href='"+uri+"'>"+info+"</a>");
            String title = info.split(" - ")[1];
            String auth = info.split(" - ")[2];
            String wenku = info.split(" - ")[3];

            boolean result = false;
            for(int i = 1;i<4;i++){
                result = uploadBook(title, auth, wenku);
                if(result)break;
            }
            if(result==false){
                System.out.println("error upload:"+title);
                return true;
            }
            String bookName = "";
            for(Element d:dom.getElementsByTag("a")){
                String href = d.attr("href");
                if(p.matcher(href).matches()){
                    result = false;
                    for(int i = 1;i<6;i++){
                        result = printPage(uri, href, d.html(), title);
                        if(result)break;
                        Thread.sleep(2000);
                        if(i==5){
                            logger.info(MessageFormat.format("upload page error,book={0},cha={1}",title,d.html()));
                        }
                    }
                }
            }
        }catch (SocketTimeoutException e){
            System.out.println("链接超时");
            return false;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean printPage(String uri, String href, String chaName, String title) {
        HttpGet httpPost = new HttpGet(href);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            Element e = dom.getElementsByClass("bd").get(0);
            String allName = e.getElementsByTag("h4").html();
            chaName = allName.replace(chaName,"");
            String text = e.html().replace("<br />", "");
            boolean result = false;
            if(text.length()<75000){
                for(int i = 1;i<4;i++){
                    result = uploadChapter(title,allName,chaName,href,text);
                    if(result)break;
                    if(i==4){
                        logger.info(MessageFormat.format("upload chapter error,book={0},cha={1}",title,chaName));
                    }
                }
            }else {
                for(int j=0;j<text.length()/75000+1;j++){
                    for(int i = 1;i<4;i++){
                        result = uploadChapter(title,allName+"("+(j+1)+")",chaName,href,text.substring(j*75000,Math.min((j+1)*75000,text.length()-1)));
                        if(result)break;
                    }
                }
            }
        } catch (SocketTimeoutException e) {
            System.out.println("链接超时");
            return false;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean uploadBook(String name,String auth,String wenku) {
        System.out.println(sdf.format(new Date())+"upload:"+name);
        HttpPost httpPost = new HttpPost("http://magicindexlib.com/upload.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("method", "addBook"));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("auth", auth));
            params.add(new BasicNameValuePair("wenku", wenku));
            params.add(new BasicNameValuePair("source", "yidm"));
            httpPost.setHeader("User-Agent","Mozilla");
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

    private static boolean uploadChapter(String name,String chapter,String volumeName,String url,String text) {
        System.out.println(sdf.format(new Date())+"upload:"+name+":"+chapter);
        HttpPost httpPost = new HttpPost("http://magicindexlib.com/upload.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("method", "addChapter"));
            params.add(new BasicNameValuePair("book", name));
            params.add(new BasicNameValuePair("chapter", chapter));
            params.add(new BasicNameValuePair("text", text));
            params.add(new BasicNameValuePair("uri", url));
            httpPost.setHeader("User-Agent", "Mozilla");
//            params.add(new BasicNameValuePair("volume", volumeName));

            httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            if(rs.indexOf("添加成功")==-1){
                return false;
            }
        } catch(NoHttpResponseException e){
            System.out.println("上传失败");
            return  false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean uploadImage(String name,String chapter,String text) {
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

}
