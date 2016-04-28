package bt;

import java.io.IOException;
import java.net.*;

/**
 * Created by Administrator on 2016/3/1.
 */
public class UdpTest {
    public static void rec(String[] args) throws Exception {

        DatagramSocket ds = new DatagramSocket(10000);  //定义服务，监视端口上面的发送端口，注意不是send本身端口

        byte[] buf = new byte[1024];//接受内容的大小，注意不要溢出

        DatagramPacket dp = new DatagramPacket(buf,0,buf.length);//定义一个接收的包

        ds.receive(dp);//将接受内容封装到包中

        String data = new String(dp.getData(), 0, dp.getLength());//利用getData()方法取出内容

        System.out.println(data);//打印内容

        ds.close();//关闭资源
    }

    public static void send(String[] args)  {

        DatagramSocket ds = null;  //建立套间字udpsocket服务

        try {
            ds = new DatagramSocket(8999);  //实例化套间字，指定自己的port
        } catch (SocketException e) {
            System.out.println("Cannot open port!");
            System.exit(1);
        }

        byte[] buf= "Hello, I am sender!".getBytes();  //数据
        InetAddress destination = null ;
        try {
            destination = InetAddress.getByName("192.168.1.5");  //需要发送的地址
        } catch (UnknownHostException e) {
            System.out.println("Cannot open findhost!");
            System.exit(1);
        }
        DatagramPacket dp =
                new DatagramPacket(buf, buf.length, destination , 10000);
        //打包到DatagramPacket类型中（DatagramSocket的send()方法接受此类，注意10000是接受地址的端口，不同于自己的端口！）

        try {
            ds.send(dp);  //发送数据
        } catch (IOException e) {
        }
        ds.close();
    }
}
