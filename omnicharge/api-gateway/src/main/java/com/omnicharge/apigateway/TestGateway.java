package com.omnicharge.apigateway;

import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestGateway {
    public static void main(String[] args) {
        System.out.println("Methods in HandlerFunctions:");
        for (Method m : HandlerFunctions.class.getDeclaredMethods()) {
            if (m.getName().equals("http")) {
                System.out.println("http method:");
                System.out.println(Arrays.toString(m.getParameterTypes()));
            }
        }
    }
}
