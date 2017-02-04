package com.jpmc.assignment.handler;

import java.util.List;
import java.util.Map;

import com.jpmc.assignment.model.IncomingSaleMessage;
import com.jpmc.assignment.model.Sale;


public interface MessageHandler {
    /**
     *
     * @param incomingSaleMessage  sale message to be processed
     *
     * @return return the updated processdMessages
     */
    public void handle(IncomingSaleMessage incomingSaleMessage);
}
