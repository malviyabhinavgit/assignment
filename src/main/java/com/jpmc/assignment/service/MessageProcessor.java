package com.jpmc.assignment.service;

import com.jpmc.assignment.entity.IncomingSaleMessage;


public interface MessageProcessor {

     void process(IncomingSaleMessage incomingSaleMessage);
}
