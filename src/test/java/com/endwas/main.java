package com.endwas;

import com.endwas.service.EndwasService;
import org.springframework.config.EndwasApplicationContext;

/**
 * 测试类
 *
 * @author endwas
 * @date Created in 2021/12/4 14:25
 */
public class main {

    public static void main(String[] args) {
        EndwasApplicationContext context = new EndwasApplicationContext(AppConfig.class);
        EndwasService endwasService = (EndwasService) context.getBean("endwasService");
        endwasService.execute();
    }
}
