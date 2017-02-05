package com.jpmc.assignment.service;


import com.jpmc.assignment.entity.IncomingSaleMessage;
import com.jpmc.assignment.entity.MessageType;
import com.jpmc.assignment.handler.MessageHandler;

import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class MessageProcessorImpl implements MessageProcessor {

    private final Map<MessageType, MessageHandler> messageHandlers;
    private final ReportGenerator reportGenerator;
    private final int logCutOffCount;
    private final int pauseCutOffCount;



    public MessageProcessorImpl(Map<MessageType, MessageHandler> messageHandlers, ReportGenerator reportGenerator, int logCutOffCount, int pauseCutOffCount) {
        if(messageHandlers == null || reportGenerator == null || logCutOffCount <=0 || pauseCutOffCount <= 0){
            throw new IllegalArgumentException("Invalid arguments supplied messageHandlers="+messageHandlers+ " reportGenerator="+reportGenerator
            +" logCutOffCount="+logCutOffCount +" pauseCutOffCount="+pauseCutOffCount);
        }
        this.messageHandlers = messageHandlers;
        this.reportGenerator = reportGenerator;
        this.logCutOffCount = logCutOffCount;
        this.pauseCutOffCount = pauseCutOffCount;
    }

    private AtomicInteger processedMessageCount= new AtomicInteger();
    private AtomicBoolean isPaused = new AtomicBoolean();

    private final static Logger logger = Logger.getLogger(MessageProcessorImpl.class);



    @Override
    public void process(IncomingSaleMessage incomingSaleMessage) {

        if(incomingSaleMessage == null) {
            throw new IllegalArgumentException("Invalid message received");
        }

        MessageHandler messageHandler = messageHandlers.get(incomingSaleMessage.getMessageType());

        if(messageHandler == null) {
            RuntimeException exception = new RuntimeException("Unknown message type! Can't process this message " + incomingSaleMessage);
            logger.error(exception);
            throw exception;
        }

        if(!isPaused.get()) {
                messageHandler.handle(incomingSaleMessage);
                processedMessageCount.incrementAndGet();

                if(shouldLogProductDetails(processedMessageCount)) {
                    logProductDetailsReport();
                }

                if(shouldPauseApplication(processedMessageCount)) {
                    pauseApplication();
                    logAdjustmentReport();
                    resumeApplication();
                }

        }else{
            //TODO should a checked exception be thrown so that client can take corrective action? Like trying after sometime.
            logger.info("Application is currently paused and can't process new message : "+ incomingSaleMessage );
        }

    }

    private void pauseApplication() {
        isPaused.set(true);
        logger.info("Application is taking a pause");
    }

    private void resumeApplication() {
        logger.info("Application is resuming now");
        isPaused.set(false);
    }


    private boolean shouldPauseApplication(AtomicInteger processedMessageCount) {
        if(processedMessageCount.intValue() % pauseCutOffCount == 0){
            return true;
        }
        return false;
    }

    private boolean shouldLogProductDetails(AtomicInteger processedMessageCount) {
        if(processedMessageCount.intValue() % logCutOffCount == 0) {
            return true;
        }
        return false;
    }

    private void logAdjustmentReport() {
        reportGenerator.generateAdjustmentReport();
    }

    private void logProductDetailsReport() {
        reportGenerator.generateProductDetailsReport();
    }
}


