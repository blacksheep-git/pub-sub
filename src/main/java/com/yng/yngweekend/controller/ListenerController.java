package com.yng.yngweekend.controller;

import com.yng.yngweekend.domain.Listener;
import com.yng.yngweekend.domain.ListenersState;
import com.yng.yngweekend.domain.User;
import com.yng.yngweekend.service.ListenerService;
import com.yng.yngweekend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ListenerController {
    private final Logger LOGGER = LoggerFactory.getLogger(ListenerController.class);
    ListenerService listenerService;

    @Autowired
    public ListenerController(ListenerService listenerService){
        this.listenerService = listenerService;
    }

    @GetMapping("/listeners/stop")
    public ListenersState stopListeners(){
        return listenerService.updateListenersState();
    }

    @GetMapping("/listeners/status")
    public Object getClusterListenerStatus(){
        return listenerService.getClusterListenerStatus();
    }

}