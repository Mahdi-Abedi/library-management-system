module library.management.system {
    exports entities;
    exports entities.items;
    exports entities.people;
    exports entities.transactions;
    exports enums;
    exports exceptions;
    exports interfaces;
    exports services;
    exports main;

    requires java.base;
    requires java.sql;
    requires java.logging;
    requires java.desktop;
    requires com.h2database;

    opens entities to java.base;
    opens entities.items to java.base;

    uses interfaces.ReportGenerator;

    provides interfaces.ReportGenerator with services.TextReportGenerator, services.HtmlReportGenerator;
}