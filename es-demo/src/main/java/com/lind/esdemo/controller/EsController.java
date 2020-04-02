package com.lind.esdemo.controller;

import com.lind.esdemo.model.Item;
import com.lind.esdemo.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EsController {
    @Autowired
    ItemRepository itemRepository;

    @GetMapping("/es/add")
    public String add() {
        itemRepository.save(new Item(1L, "test", "测试", "zzl", 1.0, "1.jpg"));
        return "ok";
    }

    @GetMapping("/es/get")
    public Iterable<Item> get() {
        return itemRepository.findAll();
    }
}
