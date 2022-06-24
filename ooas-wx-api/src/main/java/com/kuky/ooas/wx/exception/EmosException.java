package com.kuky.ooas.wx.exception;

import lombok.Data;

/**
 * @Description: EmosException
 * @Author Kuky
 * @Date: 2021/6/3 23:20
 * @Version 1.0
 */
@Data
public class EmosException extends RuntimeException{
        private String msg;
        private int code = 500;

        public EmosException(String msg) {
            super(msg);
            this.msg = msg;
        }

        public EmosException(String msg, Throwable e) {
            super(msg, e);
            this.msg = msg;
        }

        public EmosException(String msg, int code) {
            super(msg);
            this.msg = msg;
            this.code = code;
        }

        public EmosException(String msg, int code, Throwable e) {
            super(msg, e);
            this.msg = msg;
            this.code = code;
        }
    }
