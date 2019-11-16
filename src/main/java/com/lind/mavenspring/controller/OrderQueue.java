package com.lind.mavenspring.controller;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OrderQueue {
    private static Queue<DeferredResult<Object>> list = new ConcurrentLinkedQueue<>();

    public static void put(DeferredResult<Object> data) {
        list.add(data);
    }

    public static DeferredResult<Object> pop() {
        return list.poll();
    }
}