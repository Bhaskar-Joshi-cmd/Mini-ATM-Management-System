package com.atm.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Date;

public class ReceiptService {

    public String generateBalanceReceipt(String accNum, Map<String, Object> details) {
        // Create the folder path
        String folderPath = "receipts";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir(); // Creates the 'receipts' folder if missing
        }

        String fileName = folderPath + File.separator + "Receipt_" + accNum + "_" + System.currentTimeMillis() + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Fonts
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

            // Header
            Paragraph title = new Paragraph("MINI ATM SYSTEM", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            Paragraph subTitle = new Paragraph("Transaction Receipt", bodyFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subTitle);
            document.add(new Chunk(new LineSeparator()));

            // Content
            document.add(new Paragraph("\nDate: " + new Date().toString(), bodyFont));
            document.add(new Paragraph("Account Number: ****" + accNum.substring(accNum.length() - 4), bodyFont));
            document.add(new Paragraph("Account Type: " + details.get("type").toString().toUpperCase(), bodyFont));
            
            document.add(new Paragraph("\n" + "=".repeat(40), bodyFont));
            Paragraph bal = new Paragraph("CURRENT BALANCE: ₹" + String.format("%.2f", details.get("balance")), boldFont);
            document.add(bal);
            document.add(new Paragraph("=".repeat(40) + "\n", bodyFont));

            // Footer
            Paragraph footer = new Paragraph("Please keep this receipt for your records.", bodyFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return fileName; // Returns the full path
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String generateTransactionReceipt(String accNum, java.util.Map<String, Object> data) {
        String folderPath = "receipts";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdir();

        // Get the type (Deposit or Withdraw) to name the file correctly
        String type = data.getOrDefault("type", "Transaction").toString();
        String fileName = folderPath + File.separator + type + "_" + accNum + "_" + System.currentTimeMillis() + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Fonts
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
            Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);

            // Header
            Paragraph title = new Paragraph("MINI ATM SYSTEM", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // DYNAMIC HEADER: Will show "Deposit Receipt" or "Withdrawal Receipt"
            Paragraph subTitle = new Paragraph(type + " Receipt", bodyFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subTitle);
            document.add(new Chunk(new LineSeparator()));

            // Content
            document.add(new Paragraph("\nDate: " + new java.util.Date().toString(), bodyFont));
            document.add(new Paragraph("Account: ****" + accNum.substring(accNum.length() - 4), bodyFont));
            document.add(new Paragraph("\n" + "-".repeat(50), bodyFont));
            
            // DYNAMIC DETAILS: Shows "DEPOSIT AMOUNT" or "WITHDRAWAL AMOUNT"
            String amountLabel = type.equalsIgnoreCase("Deposit") ? "DEPOSITED AMOUNT: " : "WITHDRAWAL AMOUNT: ";
            document.add(new Paragraph(amountLabel + "₹" + String.format("%.2f", data.get("amount")), boldFont));
            document.add(new Paragraph("NEW BALANCE: ₹" + String.format("%.2f", data.get("new_balance")), bodyFont));
            document.add(new Paragraph("-".repeat(50) + "\n", bodyFont));

            // Footer
            Paragraph footer = new Paragraph("Thank you for using our ATM service!", bodyFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return fileName; 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
