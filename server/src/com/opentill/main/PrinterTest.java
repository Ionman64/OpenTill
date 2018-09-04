package com.opentill.main;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.opentill.logging.Log;

public class PrinterTest {

    public static void main(String[] args) {
    	try 
    	{
    	  Log.log("Attempting to print");
    	  File file = new File("/dev/usb/lp1");
    	  
    	  if (!file.exists()) {
    		  Log.log("Usb not found");
    	  }
    	  
    	  PrintStream printStream = new PrintStream(new FileOutputStream(file));
    	  printStream.write(new byte[] {0x1B, 0x40});
    	  printStream.write(new byte[] {0x1B, 0x69, 0x41, 0x40});
    	  //printStream.write(Utils.generateBarcode(String.format("Test"));
    	  printStream.write(new byte[] {0x1A});
    	  if (printStream.checkError()) {
    		  Log.warn("There has been an error");
    	  }
    	  
    	  //printStream.print("Hello World");
    	  printStream.flush();
    	  printStream.close();
    	  Log.log("Script Ends");
    	} 
    	catch (Exception ex) 
    	{
    	  System.out.println(ex.getMessage());
    	}
    	if ("1".equals("1")) {
    		return;
    	}
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (pj.printDialog()) {
            PageFormat pf = pj.defaultPage();
            Paper paper = pf.getPaper();    
            double width = fromCMToPPI(6.2);
            double height = fromCMToPPI(2.9);    
            paper.setSize(width, height);
            paper.setImageableArea(
                            fromCMToPPI(0.25), 
                            fromCMToPPI(0.5), 
                            width - fromCMToPPI(0.35), 
                            height - fromCMToPPI(1));                
            System.out.println("Before- " + dump(paper));    
            pf.setOrientation(PageFormat.LANDSCAPE);
            pf.setPaper(paper);    
            System.out.println("After- " + dump(paper));
            System.out.println("After- " + dump(pf));                
            dump(pf);    
            PageFormat validatePage = pj.validatePage(pf);
            System.out.println("Valid- " + dump(validatePage));                
            //Book book = new Book();
            //book.append(new MyPrintable(), pf);
            //pj.setPageable(book);    
            
            pj.setPrintable(new MyPrintable(), pf);
            try {
                pj.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
            }    
        }    
    }

    protected static double fromCMToPPI(double cm) {            
        return toPPI(cm * 0.393700787);            
    }

    protected static double toPPI(double inch) {            
        return inch * 72d;            
    }

    protected static String dump(Paper paper) {            
        StringBuilder sb = new StringBuilder(64);
        sb.append(paper.getWidth()).append("x").append(paper.getHeight())
           .append("/").append(paper.getImageableX()).append("x").
           append(paper.getImageableY()).append(" - ").append(paper
       .getImageableWidth()).append("x").append(paper.getImageableHeight());            
        return sb.toString();            
    }

    protected static String dump(PageFormat pf) {    
        Paper paper = pf.getPaper();            
        return dump(paper);    
    }

    public static class MyPrintable implements Printable {

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, 
            int pageIndex) throws PrinterException {                   
        	if (pageIndex > 0) {
                return NO_SUCH_PAGE;
           }

           // User (0,0) is typically outside the
           // imageable area, so we must translate
           // by the X and Y values in the PageFormat
           // to avoid clipping.
           Graphics2D g2d = (Graphics2D) graphics;
           g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

           // Now we perform our rendering
           graphics.drawString("Hello world!", 10, 10);

           // tell the caller that this page is part
           // of the printed document
           return PAGE_EXISTS; 
        }
    }
}
