//package com.tht.test;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class ReportGenerator {
//
//    private BufferedWriter writer;
//
//    public ReportGenerator(String reportName) {
//        try {
//            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//            String fileName = reportName + "_" + timestamp + ".html";
//            writer = new BufferedWriter(new FileWriter(fileName));
//
//            // Write HTML report header
//            writer.write("<html><head><title>Test Report</title></head><body>");
//            writer.write("<h1>Test Report</h1>");
//            writer.write("<table border='1'><tr><th>Test Case</th><th>Status</th><th>Details</th></tr>");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void log(String testCase, String status, String details) {
//        try {
//            writer.write("<tr><td>" + testCase + "</td><td>" + status + "</td><td>" + details + "</td></tr>");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void close() {
//        try {
//            // Write HTML report footer
//            writer.write("</table></body></html>");
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}