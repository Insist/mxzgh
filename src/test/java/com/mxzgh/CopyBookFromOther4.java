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
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/1/5.
 */
public class CopyBookFromOther4 {
    static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss -- ");
    static  CloseableHttpClient client ;
    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "info");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "info");
        client = HttpClientBuilder.create().build();
    }

    static Logger logger = Logger.getLogger(CopyBookFromOther4.class);

    public static void main(String[] args) throws IOException {
        String uri = "http://xs.dmzj.com/{0,number,#}/index.shtml";
        for(int i = 7;i<2100;i++){
            try {
                boolean result = false;
                for(int j = 1;j<4;j++){
                    result = printUrl(MessageFormat.format(uri, i));
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

    private static boolean printUrl(String uri) {
        HttpGet httpPost = new HttpGet(uri);
        System.out.println(uri);
        try {
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            Document dom = Jsoup.parse(rs);
            String info = dom.getElementsByTag("title").html();
            String name = info.split("\\|")[0];
            if(name.indexOf("动漫之家轻小说站")!=-1){
                return true;
            }
            Element d=dom.getElementById("full_intro");
            String desc = (d.html());
            Element img=dom.getElementById("cover_pic");
            String src = "";
            if(img!=null)
            src = (img.attr("src"));

            boolean result = false;
            for(int j = 1;j<4;j++) {
                result = uploadImage(name,src,desc);
                if(result)break;
                Thread.sleep(2000);
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


    private static boolean uploadImage(String name,String src,String desc) {
        System.out.println(sdf.format(new Date())+"upload:"+name);
        HttpPost httpPost = new HttpPost("http://magicindexlib.com/uploadCover.php");
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("src", src));
            params.add(new BasicNameValuePair("desc", desc));
            httpPost.setHeader("User-Agent", "Mozilla");
            httpPost.setEntity(new UrlEncodedFormEntity(params, Consts.UTF_8));
            HttpResponse response = client.execute(httpPost);
            String rs = EntityUtils.toString(response.getEntity(), "GBK");
            if(rs.indexOf("书不存在")!=-1){
                System.out.println("书不存在:"+name);
                logger.info(MessageFormat.format("bookName:{0}\r\nbookDesc:{1}\r\nbookCover:{2}",name,desc,src));
                return true;
            }
            if(rs.indexOf("添加成功")==-1){
                return false;
            }
        } catch(NoHttpResponseException e){
            System.out.println("上传失败");
            return  false;
        } catch (IOException e) {
            System.out.println("上传失败");
            return false;
        }
        return true;
    }
}
