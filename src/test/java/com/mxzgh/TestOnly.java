package com.mxzgh;

import junit.framework.Test;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/1/5.
 */
public class TestOnly {
    static  CloseableHttpClient client ;
    static {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "info");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "info");
        client = HttpClientBuilder.create().build();
    }

    static Logger logger = Logger.getLogger(TestOnly.class);

    public static void main(String[] args) throws IOException {
        String src = "../../img/1125/33/a582f7fd4b11349ef1a022604c0d5856.jpg";
        System.out.println(src.replace("../../img","http://xs.dmzj.com/img"));
    }
}
