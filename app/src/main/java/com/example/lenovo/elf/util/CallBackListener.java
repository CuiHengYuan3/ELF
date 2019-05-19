package com.example.lenovo.elf.util;

public interface CallBackListener {
   void  onFinish(String response);
   void  onFail(Exception e);
}
