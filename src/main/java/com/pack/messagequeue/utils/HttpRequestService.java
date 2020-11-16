package com.pacific.messagequeue.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author maoxy
 * @date 2019/1/18 09:43
 */
@Service
public class HttpRequestService {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpRequestService.class);

    @Autowired
    private RestTemplate restTemplate;
    private static final String ADDRESS = "http://apigetway-eureka-provider/";
    private static final String FLAG = "3";

    /**
     * @param uri   uri
     * @param data  请求参数
     * @param responseType  返回值类型
     * @param <T>
     * @return
     */
    public  <T> T post(String uri, Object data, Class<T> responseType,String companyflag){
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept-Charset", MediaType.APPLICATION_JSON.toString());
        headers.add("flag",FLAG);
        headers.add("companyflag",companyflag);
        HttpEntity<Object> object = new HttpEntity<>(data, headers);
        String url = ADDRESS+uri;
        LOGGER.info("post request url:{}",url);
        ResponseEntity<T> responseEntity =
                restTemplate.postForEntity(url, object, responseType);
        return responseEntity.getBody();
    }
}
