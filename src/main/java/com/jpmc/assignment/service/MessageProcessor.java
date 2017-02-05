package com.jpmc.assignment.service;

import com.jpmc.assignment.entity.IncomingSaleMessage;
import com.jpmc.assignment.exception.MessageProcessorException;


public interface MessageProcessor {

    void process(IncomingSaleMessage incomingSaleMessage) throws MessageProcessorException;
}
