package services;

import interfaces.ReportGenerator;

public class HtmlReportGenerator implements ReportGenerator {
    @Override
    public String generateReport(Object data) {
        return "<html><body>" + data + "</body></html>";
    }
}