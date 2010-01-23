/*
 *  YUI Test
 *  Author: Nicholas C. Zakas <nzakas@yahoo-inc.com>
 *  Copyright (c) 2009, Yahoo! Inc. All rights reserved.
 *  Code licensed under the BSD License:
 *      http://developer.yahoo.net/yui/license.txt
 */

package com.yahoo.platform.yuitest.coverage.report;

import com.yahoo.platform.yuitest.coverage.results.FileReport;
import com.yahoo.platform.yuitest.coverage.results.SummaryReport;
import com.yahoo.platform.yuitest.writers.ReportWriter;
import com.yahoo.platform.yuitest.writers.ReportWriterFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

/**
 *
 * @author Nicholas C. Zakas
 */
public class LCOVReportGenerator implements ReportGenerator {

    private File outputdir = null;
    private boolean verbose = false;
    private File reportdir = null;

    /**
     * Creates a new HTMLReportGenerator
     * @param outputdir The output directory for the HTML report.
     * @param verbose True to output additional information to the console.
     */
    public LCOVReportGenerator(String outputdir, boolean verbose){
        this.outputdir = new File(outputdir);
        this.verbose = verbose;
        this.reportdir = new File(outputdir + File.separator + "lcov-report");

        //create directories if not already there
        if (!this.outputdir.exists()){
            this.outputdir.mkdirs();
        }

    }

    public void generate(SummaryReport report) throws IOException {
        Date now = new Date();
        generateSummaryPage(report, now);

        //create the report directory now
        if (!this.reportdir.exists()){
            this.reportdir.mkdirs();
            if (verbose){
                System.err.println("[INFO] Creating " + reportdir.getCanonicalPath());
            }
        }

        generateFilePages(report, now);
    }

    /**
     * Generates the lcov.info file for the coverage information.
     * @param report The coverage information to generate a report for.
     * @param date The date associated with the report.
     * @throws IOException When a file cannot be written to.
     */
    private void generateSummaryPage(SummaryReport report, Date date) throws IOException {

        String outputFile = outputdir.getAbsolutePath() + File.separator + "lcov.info";
        Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));

        if (verbose){
            System.err.println("[INFO] Outputting " + outputFile);
        }

        ReportWriter reportWriter = (new ReportWriterFactory<SummaryReport>()).getWriter(out, "CoverageSummaryReportLCOV");
        reportWriter.write(report, date);
        out.close();
    }

    /**
     * Generates a report page for each file in the coverage information.
     * @param report The coverage information to generate reports for.
     * @param date The date associated with the report.
     * @throws IOException When a file cannot be written to.
     */
    private void generateFilePages(SummaryReport report, Date date) throws IOException {

        FileReport[] fileReports = report.getFileReports();

        for (int i=0; i < fileReports.length; i++){

            //make the directory to mimic the source file
            String parentDir = fileReports[i].getFile().getParent();
            File parent = new File(reportdir.getAbsolutePath() + File.separator + parentDir);
            if (!parent.exists()){
                parent.mkdirs();
            }
            
            generateFilePage(fileReports[i], date, parent);
            generateFunctionPage(fileReports[i], date, parent);
        }
    }

    /**
     * Generates a report page for the file coverage information.
     * @param report The coverage information to generate reports for.
     * @param date The date associated with the report.
     * @throws IOException When a file cannot be written to.
     */
    private void generateFilePage(FileReport report, Date date, File parent) throws IOException {
        String outputFile = parent.getAbsolutePath() + File.separator + report.getFile().getName() + ".gcov.html";

        if (verbose){
            System.err.println("[INFO] Outputting " + outputFile);
        }

        Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));
        ReportWriter reportWriter = (new ReportWriterFactory<FileReport>()).getWriter(out, "LCOVHTMLFileReport");
        reportWriter.write(report, date);
        out.close();
    }

    /**
     * Generates a report page for the function coverage information.
     * @param report The coverage information to generate reports for.
     * @param date The date associated with the report.
     * @throws IOException When a file cannot be written to.
     */
    private void generateFunctionPage(FileReport report, Date date, File parent) throws IOException {
        String outputFile = parent.getAbsolutePath() + File.separator + report.getFile().getName() + ".func.html";

        if (verbose){
            System.err.println("[INFO] Outputting " + outputFile);
        }

        Writer out = new OutputStreamWriter(new FileOutputStream(outputFile));
        ReportWriter reportWriter = (new ReportWriterFactory<FileReport>()).getWriter(out, "LCOVHTMLFunctionReport");
        reportWriter.write(report, date);
        out.close();
    }


}
