package com.jpmc.assignment.handler;

import com.jpmc.assignment.entity.IncomingSaleMessage;


public interface MessageHandler {

    void handle(IncomingSaleMessage incomingSaleMessage);
}
