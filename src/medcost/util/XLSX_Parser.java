package medcost.util;
import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSX_Parser{

    private static Object val(Cell cell){
	switch (cell.getCellType()) {
	case STRING:
	    return cell.getStringCellValue();
	case BOOLEAN:
	    return cell.getBooleanCellValue();
	case NUMERIC:
	    return cell.getNumericCellValue();
	}
	return "";
    }
    
    public static void test()throws IOException{	
	FileInputStream inputStream = new FileInputStream(new File("Blanchard Valley Hospital and Bluffton Hospital.xlsx"));	
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iter = sheet.iterator();         
        while (iter.hasNext()) {
            Row row = iter.next();
	    ///System.out.println(row.getFirstCellNum() + " " + row.getLastCellNum());
            Iterator<Cell> citer = row.cellIterator();             
            while (citer.hasNext()) {
                Cell cell = citer.next();
		System.out.print(val(cell) + " - ");
		
            }
            System.out.println();
        }
         
        workbook.close();
        inputStream.close();
    }

    public static void main(String[] args)throws IOException{
	test();
    }
}

//https://en.wikipedia.org/wiki/Chargemaster
//https://www.cms.gov/Medicare/Coding/HCPCSReleaseCodeSets/Alpha-Numeric-HCPCS
//https://www.cms.gov/research-statistics-data-systems/medicare-provider-utilization-and-payment-data/medicare-provider-utilization-and-payment-data-inpatient/inpatient-charge-data-fy-2018

//https://www.cms.gov/Medicare/Coding/ICD10/ICD-10-MS-DRG-Conversion-Project
