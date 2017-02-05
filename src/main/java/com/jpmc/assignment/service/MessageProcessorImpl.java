package com.jpmc.assignment.service;


import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.jpmc.assignment.entity.IncomingSaleMessage;
import com.jpmc.assignment.entity.MessageType;
import com.jpmc.assignment.exception.MessageProcessorException;
import com.jpmc.assignment.handler.MessageHandler;


public class MessageProcessorImpl implements MessageProcessor {

    private final Map<MessageType, MessageHandler> messageHandlers;
    private final ReportGenerator reportGenerator;
    private final int logCutOffCount;
    private final int pauseCutOffCount;


    public MessageProcessorImpl(Map<MessageType, MessageHandler> messageHandlers, ReportGenerator reportGenerator, int logCutOffCount, int pauseCutOffCount) {
        if (messageHandlers == null || reportGenerator == null || logCutOffCount <= 0 || pauseCutOffCount <= 0) {
            throw new IllegalArgumentException("Invalid arguments supplied messageHandlers=" + messageHandlers + " reportGenerator=" + reportGenerator
                    + " logCutOffCount=" + logCutOffCount + " pauseCutOffCount=" + pauseCutOffCount);
        }
        this.messageHandlers = messageHandlers;
        this.reportGenerator = reportGenerator;
        this.logCutOffCount = logCutOffCount;
        this.pauseCutOffCount = pauseCutOffCount;
    }

    private AtomicInteger processedMessageCount = new AtomicInteger();
    private AtomicBoolean isPaused = new AtomicBoolean();

    private final static Logger logger = Logger.getLogger(MessageProcessorImpl.class);


    @Override
    public void process(IncomingSaleMessage incomingSaleMessage) throws MessageProcessorException{

        if (incomingSaleMessage == null) {
            throw new IllegalArgumentException("Invalid message received");
        }

        MessageHandler messageHandler = messageHandlers.get(incomingSaleMessage.getMessageType());

        if (messageHandler == null) {
            RuntimeException exception = new RuntimeException("Unknown message type! Can't process this message " + incomingSaleMessage);
            logger.error(exception);
            throw exception;
        }

        if (!isPaused.get()) {
            messageHandler.handle(incomingSaleMessage);
            processedMessageCount.incrementAndGet();

            if (shouldLogProductDetails(processedMessageCount)) {
                logProductDetailsReport();
            }

            if (shouldPauseApplication(processedMessageCount)) {
                pauseApplication();
                logAdjustmentReport();
                resumeApplication();
            }

        } else {
            logger.info("Application is currently paused and can't process new message : " + incomingSaleMessage);
            throw new MessageProcessorException("Application is currently paused and can't process new message. Please try after sometime");
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
        return processedMessageCount.intValue() % pauseCutOffCount == 0;
    }

    private boolean shouldLogProductDetails(AtomicInteger processedMessageCount) {
        return processedMessageCount.intValue() % logCutOffCount == 0;
    }

    private void logAdjustmentReport() {
        reportGenerator.generateAdjustmentReport();
    }

    private void logProductDetailsReport() {
        reportGenerator.generateProductDetailsReport();
    }
}


