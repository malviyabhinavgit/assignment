package com.jpmc.assignment.service;

import com.jpmc.assignment.handler.AdjustmentSaleMessageHandler;
import com.jpmc.assignment.handler.MessageHandler;
import com.jpmc.assignment.model.AdjustmentSaleMessage;
import com.jpmc.assignment.model.IncomingSaleMessage;
import com.jpmc.assignment.model.MessageType;
import com.jpmc.assignment.model.Sale;

import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;


public class MessageMessageProcessorImpl implements MessageProcessor {

    private Map<MessageType, MessageHandler> messageHandlers;
    private ReportGenerator reportGenerator;
    private Integer pauseCutOffCount;
    private Integer logCutOffCount;
    private Integer processedMessageCount=0;
    private Boolean isPaused = Boolean.FALSE;
    private final static Logger logger = Logger.getLogger("MessageMessageProcessorImpl.class");


    /**
     *
     * @param incomingSaleMessage  message sent by external system
     */
    public void process(IncomingSaleMessage incomingSaleMessage) {

        if(incomingSaleMessage == null) {
            throw new IllegalArgumentException("Invalid message received");
        }

        MessageHandler messageHandler = messageHandlers.get(incomingSaleMessage.getMessageType());

        if(messageHandler == null) {
            logger.info("Unknown message type! Can't process this message");
            return;
        }

        if(!isPaused) {
            try {
                messageHandler.handle(incomingSaleMessage);
                processedMessageCount++;

                if(shouldLogProductDetails(processedMessageCount)) {
                    logProductDetails();
                }

                if(shouldPauseApplication(processedMessageCount)) {
                    pauseApplication();
                    logAdjustmentReport();
                    resumeApplication();
                }
            } catch (Exception exception) {
                logger.info("Exception Happened while processing " + exception.getStackTrace());
            }

        }

    }

    private void pauseApplication() {
        isPaused = true;
        logger.info("Application is taking a pause");
    }

    private void resumeApplication() {
        logger.info("Application is resuming now");
        isPaused = false;
    }


    private boolean shouldPauseApplication(Integer processedMessageCount) {
        return processedMessageCount == pauseCutOffCount;
    }

    private boolean shouldLogProductDetails(Integer processedMessageCount) {
        if(processedMessageCount % 10 == 0) {
            return true;
        }
        return false;
    }

    private void logAdjustmentReport() {
        reportGenerator.generateProductDetailsReport();
    }

    private void logProductDetails() {
        reportGenerator.generateAdjustmentReport();
    }

    public void setLogCutOffCount(Integer logCutOffCount) {
        this.logCutOffCount = logCutOffCount;
    }

    public void setMessageHandlers(Map<MessageType, MessageHandler> messageHandlers) {
        this.messageHandlers = messageHandlers;
    }

    public void setPauseCutOffCount(Integer pauseCutOffCount) {
        this.pauseCutOffCount = pauseCutOffCount;
    }

    public ReportGenerator getReportGenerator() {
        return reportGenerator;
    }

    public void setReportGenerator(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }
}


