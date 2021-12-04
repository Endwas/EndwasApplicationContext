package org.springframework.exception;

/**
 * endwas异常类
 *
 * @author endwas
 * @date Created in 2021/12/4 10:14
 */
public class EndwasException extends RuntimeException {
    public EndwasException(){
        super();
    }

    public EndwasException(String message){
        super(message);
    }
}
