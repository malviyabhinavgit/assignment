package com.jpmc.assignment.service;


public class ConsoleReportWriter implements ReportWriter {
    @Override
    public void write(String text) {
        System.out.println(text);
    }
}
