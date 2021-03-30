package medcost.util;
import java.io.*;
import java.util.*;
import org.apache.commons.csv.*;

public class CSV_Parser{

    ///Code Type,Code,Facility,IP Expected Reimbursement,IP_Charge,IP_Selfpay,MAX IP XR,MAX OP XR,MIN IP XR,MIN OP XR,NDC,OP Expected Reimbursement,OP_Charge,OP_Selfpay,Payer,Plan(s),Procedure Description,Procedure,Rev Code
    public static void test()throws IOException{
	Reader in = new FileReader("lodi-hospital.csv");
	CSVParser parser = CSVFormat.EXCEL.withHeader().parse(in);
	//CSVParser parser = CSVParser.parse(in, CSVFormat.EXCEL);
	for (CSVRecord record : parser){
	    System.out.println(record.get("Code") + " " + record.get("IP_Charge"));
	}
    }

    public static void main(String[] args)throws IOException{
	test();
    }

}
