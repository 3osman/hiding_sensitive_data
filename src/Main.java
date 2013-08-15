
import java.io.*;
import java.util.Locale;
import java.util.Scanner;
import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

public class Main {

    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;
    private String inputFile;
    String[] fin = new String[6];

    public void setOutputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    private void createLabel(WritableSheet sheet)
            throws WriteException {
        // Lets create a times font
        WritableFont times10pt = new WritableFont(WritableFont.ARIAL, 10);
        // Define the cell format
        times = new WritableCellFormat(times10pt);
        // Lets automatically wrap the cells
        times.setWrap(true);

        // Create create a bold font with unterlines
        WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);

        // Write a few headers
        addCaption(sheet, 0, 0, "Rule");
        addCaption(sheet, 1, 0, "Support");
        addCaption(sheet, 2, 0, "Confidence");
        addCaption(sheet, 3, 0, "Lift");
        addCaption(sheet, 4, 0, "Correlation");
        addCaption(sheet, 5, 0, "Chi-square");


    }

    private void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }

    public String getNoOfRules(String in, int type) throws WriteException, IOException{
        String noOfRules = "";
      
        try {
            String content = new Scanner(new File(in)).useDelimiter("\\Z").next();
            if (type == 1) {
                content = content.substring(content.lastIndexOf("----------------- Assosciation Rules---------------------") + 1, content.length());
            } else {
                content = content.substring(content.indexOf("\n") + 1, content.length());
            }
            if (type == 1) {
                noOfRules = content.substring(content.indexOf("Number Of Rules"), content.indexOf("Number Of Frequent Itemset") - 1);
                content = content.substring(content.indexOf("\n") + 1, content.lastIndexOf("\n"));

            } else {
                noOfRules = content.substring(content.indexOf("Number Of Rules"), content.length());
                content = content.substring(0, content.lastIndexOf("\n"));


            }
            } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return noOfRules;
    }
    public String dof(String in, String ou, int type) throws WriteException, IOException {
        //Main this = new Main();
        int i = 1;
        File out = new File(ou);
        this.setOutputFile(out.getAbsolutePath());
        File file = new File(this.inputFile);
        String noOfRules = "";
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Report", 0);
        WritableSheet excelSheet = workbook.getSheet(0);
        this.createLabel(excelSheet);

        try {
            String content = new Scanner(new File(in)).useDelimiter("\\Z").next();
            if (type == 1) {
                content = content.substring(content.lastIndexOf("----------------- Assosciation Rules---------------------") + 1, content.length());
            } else {
                content = content.substring(content.indexOf("\n") + 1, content.length());
            }
            if (type == 1) {
                noOfRules = content.substring(content.indexOf("Number Of Rules"), content.indexOf("Number Of Frequent Itemset") - 1);
                content = content.substring(content.indexOf("\n") + 1, content.lastIndexOf("\n"));

            } else {
                noOfRules = content.substring(content.indexOf("Number Of Rules"), content.length());
                content = content.substring(0, content.lastIndexOf("\n"));


            }
            String[] content2 = content.split("\n");
            String[] strLine = new String[2];
            String[] temp = new String[20];

            for (int k = 0; k < content2.length - 1; k = k + 2) {
                strLine[0] = content2[k];
                strLine[1] = content2[k + 1];
                String temp1 = strLine[0] + " " + strLine[1];
                this.fin[0] = temp1.substring(0, temp1.lastIndexOf("]") + 1);
                String temp2 = temp1.substring(temp1.lastIndexOf("]") + 1, temp1.length() - 1);
                temp = temp2.split(" ");
                this.fin[1] = temp[5];
                this.fin[2] = temp[10];
                this.fin[3] = temp[13];
                this.fin[4] = temp[18];
                this.fin[5] = temp[27];

                this.addLabel(excelSheet, 0, i, this.fin[0]);
                this.addLabel(excelSheet, 1, i, this.fin[1]);
                this.addLabel(excelSheet, 2, i, this.fin[2]);

                this.addLabel(excelSheet, 3, i, this.fin[3]);
                this.addLabel(excelSheet, 4, i, this.fin[4]);
                this.addLabel(excelSheet, 5, i, this.fin[5]);

                i++;


            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        workbook.write();
        workbook.close();
        System.out.println("Please check the result file under " + out.getAbsolutePath());
        return noOfRules;

    }
}
