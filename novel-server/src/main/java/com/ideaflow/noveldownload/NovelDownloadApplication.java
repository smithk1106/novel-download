package com.ideaflow.noveldownload;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@MapperScan(basePackages = {"com.ideaflow.noveldownload.**.mapper"})
public class NovelDownloadApplication {

    public static void main(String[] args) throws UnknownHostException {


        ConfigurableApplicationContext application = SpringApplication.run(NovelDownloadApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        port = StringUtils.hasText(port) ? port : "8080";
        String active = env.getProperty("spring.profiles.active");
        String path = env.getProperty("server.servlet.context-path");
        path = StringUtils.hasText(path) ? path : "";

        String websocketPath = env.getProperty("wpl.websocket.path");

        System.out.println("\n----------------------------------------------------------\n\t" +
                "application is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
                "前端页面: \thttp://" + ip + ":" + port + path + "/\n\t" +
                "websocketPath: \thttp://" + ip + ":" + port + websocketPath + "/\n\t" +
                "----------------------------------------------------------");
    }

}
