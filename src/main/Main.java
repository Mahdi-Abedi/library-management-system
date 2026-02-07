package main;

import interfaces.ReportGenerator;
import services.ReportService;

import java.net.URL;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n=== CHAPTER 12: MODULES ===\n");

        System.out.println("1. Module Information:");
        Module currentModule = Main.class.getModule();
        System.out.println("Module name: " + currentModule.getName());
        System.out.println("Module descriptor: " + currentModule.getDescriptor());
        System.out.println("Is named module: " + currentModule.isNamed());

        System.out.println("\n2. Package Information:");
        Package pkg = Main.class.getPackage();
        System.out.println("Package name: " + pkg.getName());
        System.out.println("Implementation title: " + pkg.getImplementationTitle());
        System.out.println("Specification version: " + pkg.getSpecificationVersion());

        System.out.println("\n3. ServiceLoader Test (if implemented):");
        try {
            ReportGenerator generator = ReportService.getReportGenerator();
            System.out.println("Report generator: " + generator.getClass().getSimpleName());
            System.out.println("Sample report: " + generator.generateReport("Test Data"));
        } catch (Exception e) {
            System.out.println("ServiceLoader not configured: " + e.getMessage());
        }

        System.out.println("\n4. Module Layer Information:");
        ModuleLayer layer = ModuleLayer.boot();
        System.out.println("Boot layer modules: " + layer.modules().size());
        layer.modules().forEach(m ->
                System.out.println("  - " + m.getName()));

        System.out.println("\n5. Resource Access in Modules:");
        try {
            URL resource = Main.class.getResource("/resources/messages_en.properties");
            if (resource != null) {
                System.out.println("✓ Resources accessible from module");
            } else {
                System.out.println("✗ Resources not accessible");
            }
        } catch (Exception e) {
            System.out.println("Error accessing resources: " + e.getMessage());
        }

        System.out.println("\n=== MODULES DEMO COMPLETED ===");
    }
}