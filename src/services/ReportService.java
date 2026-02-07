package services;

import interfaces.ReportGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ReportService {
    public static ReportGenerator getReportGenerator() {
        ServiceLoader<ReportGenerator> loader = ServiceLoader.load(ReportGenerator.class);

        return loader.findFirst().orElseThrow(() -> new IllegalStateException("No ReportGenerator found"));
    }

    public static List<ReportGenerator> getAllGenerators() {
        ServiceLoader<ReportGenerator> loader = ServiceLoader.load(ReportGenerator.class);

        List<ReportGenerator> generators = new ArrayList<>();
        loader.forEach(generators::add);
        return generators;
    }

    public static HtmlReportGenerator getHtmlReportGenerator() {
        ServiceLoader<HtmlReportGenerator> loader = ServiceLoader.load(HtmlReportGenerator.class);

        return loader.findFirst().orElseThrow(() -> new IllegalStateException("No ReportGenerator found"));
    }

    public static TextReportGenerator getTextReportGenerator() {
        ServiceLoader<TextReportGenerator> loader = ServiceLoader.load(TextReportGenerator.class);

        return loader.findFirst().orElseThrow(() -> new IllegalStateException("No ReportGenerator found"));
    }
}