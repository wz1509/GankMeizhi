package com.wazing.gankmeizhi.model;

import android.accounts.NetworkErrorException;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class ApiCodeException extends Exception {

    public ApiCodeException(String errorMsg) {
        super(errorMsg);
    }

    public static String checkException(Throwable ex) {
        if (!(ex instanceof ApiCodeException)) {
            ex.printStackTrace();
        }
        if (ex instanceof SocketTimeoutException) {
            return "连接超时，请检查网络";
        } else if (ex instanceof NetworkErrorException) {
            return "网络连接错误，请检查网络";
        } else if (ex instanceof ConnectException) {
            return "无网络连接，请检查网络";
        } else if (ex instanceof MalformedJsonException || ex instanceof JsonSyntaxException) {
            return "解析Json异常";
        } else if (ex instanceof ApiCodeException) {
            return "" + ex.getMessage();
        } else {
            return "未知错误";
        }
    }

}