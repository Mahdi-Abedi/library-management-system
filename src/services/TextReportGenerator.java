package services;

import interfaces.ReportGenerator;

public class TextReportGenerator implements ReportGenerator {
    @Override
    public String generateReport(Object data) {
        return "Text Report: " + data.toString();
    }
}