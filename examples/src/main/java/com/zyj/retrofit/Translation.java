package com.zyj.retrofit;

import com.socks.library.KLog;

public class Translation {

    private int status;
    private content content;

    private static class content {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;

        @Override
        public String toString() {
            return "content{" +
                    "from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", vendor='" + vendor + '\'' +
                    ", out='" + out + '\'' +
                    ", errNo=" + errNo +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Translation{" +
                "status=" + status +
                ", content=" + content +
                '}';
    }

    //定义 输出返回数据 的方法
    public void show() {
        KLog.d(toString());
    }
}