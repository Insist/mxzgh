package com.mxzgh;

/**
 * Created by Administrator on 2016/4/11.
 */
public class MainRunUpload {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            public void run() {
                UpdateBook.main(null);
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                UpdateBook2.main(null);
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                CopyBook.main(null);
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                CopyBookFromOther2.main(null);
            }
        }).start();
    }
}
