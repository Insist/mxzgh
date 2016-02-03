package com.mxzgh;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
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
public class UpdateBook2 {

    static CloseableHttpClient client ;
    static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss -- ");
    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "info");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "info");
        client = HttpClientBuilder.create().build();
    }
    static Pattern p = Pattern.compile("http://www.yidm.com/article/html/[0-9]/[0-9]+/[0-9]+[.]html");

    public static void main(String[] args) {
        String uri = "http://www.yidm.com/article/html/{0}/{1,number,#}/";
        for(int i = 1973;i>=1;i--){
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
            String info = dom.getElementsByTag("title").html();
            String title = info.split(" - ")[1];
            String auth = info.split(" - ")[2];
            String wenku = info.split(" - ")[3];
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
            if(lastChaUrl.length()<1){
                return true;
            }

            boolean start = false;
            String chaFirstName = "";
            for(Element d:dom.getElementsByTag("a")){
                String href = d.attr("href");
                if(p.matcher(href).matches()){
                    if(href.equals(lastChaUrl)) {
                        start =true;
                        continue;
                    } else if(!start){
                        continue;
                    }
                    boolean rss = false;
                    for(int i = 1;i<4;i++) {
                        rss = printPage(uri, href, "", title);
                        if(rss)break;
                    }
                }

            }
        } catch (Exception e) {
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

}
