package com.jpmc.assignment.service;

import com.jpmc.assignment.model.IncomingSaleMessage;

public interface MessageProcessor {

    /**
     *
     * @param incomingSaleMessage  message sent by external system
     */
    public void process(IncomingSaleMessage incomingSaleMessage);
}
