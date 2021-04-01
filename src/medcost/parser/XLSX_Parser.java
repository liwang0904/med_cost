package medcost.parser;
import java.io.*;
import java.util.*;
import medcost.util.ProviderConfig;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSX_Parser implements PricingParser{
    
    @Override
    public List<medcost.components.ItemPrice> parse(ProviderConfig cfg, InputStream is)throws IOException{
	List<medcost.components.ItemPrice> list = new LinkedList();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);//should it be configuarable ???
        Iterator<Row> iter = sheet.iterator();
	Map<String, Integer>  indices = null;
	Map<String, Integer>  other_indices = null;
        while (iter.hasNext()){//read headers
	    indices = new HashMap();
	    other_indices = new HashMap();
            Row row = iter.next();
	    for(int i = 0; i < row.getLastCellNum(); i++){ 
		Cell cell = row.getCell(i);
		Object val = val(cell);
		for(String[] s : cfg.header){//figure out where indices of the headers
		    if(s[1].equals(val)) indices.put(s[0], i);
		    else other_indices.put(val.toString().replace('\n', ' '), i);//store the name of column, some might contain newline
		}
	    }
	    if(indices.size() > 0){
		if(indices.size() < cfg.header.length)
		    System.err.println("Missing headers ?  Expecting "+ cfg.header.length + " , matched only "+indices.size());
		break;
	    }
	}
	if(DEBUG)System.out.println(indices +" " + other_indices);
	
	while(iter.hasNext()){
	    Row row = iter.next();
	    medcost.components.ItemPrice ip = row2ip(row,cfg, indices, other_indices);
	    if(ip != null)list.add(ip);	    	    
        }
         
        workbook.close();  is.close();
	return list;
    }

    
    private static medcost.components.ItemPrice row2ip(Row row, ProviderConfig cfg, Map<String, Integer>  indices, Map<String, Integer>  other_indices){
	if(row == null){System.err.println("Null Row ?"); return null;}	    	
	medcost.components.ItemPrice ip = new medcost.components.ItemPrice();
	Map<String, Object> vals = new HashMap();
	for(Map.Entry<String, Integer> entry : indices.entrySet()){
	    String key = entry.getKey();
	    int index = entry.getValue();
	    Cell cell = row.getCell(index);
	    if(cell == null){//missing cell !!!
		System.err.println("Not found "+key +" for row "+vals);
		continue;
	    }
	    if("code".equals(key)) vals.put(key, code_val(cell));
	    else vals.put(key, val(cell));
	}
	    
	for(Map.Entry<String, Integer> entry : other_indices.entrySet()){//Store values that are not standard
	    String key = entry.getKey();
	    int index = entry.getValue();
	    Cell cell = row.getCell(index);
	    if(cell == null){//missing cell !!!
		System.err.println("Not found "+key +" for row "+vals);
		continue;
	    }
	    ip.SET(key, val(cell));
	}
	    
	if(DEBUG)System.out.println(vals);

	
	ip.setProvider(cfg.id);
	parse_known_keys(ip, vals);
	
	return ip;
    }

    
    //TODO : parse all known keys, not just code / price
    private static boolean parse_known_keys(medcost.components.ItemPrice ip, Map<String, Object> vals){	       
	Object code_obj = vals.get("code");
	Object price_obj = vals.get("price");
	if(code_obj == null || price_obj == null)return false;
	
	String code = code_obj.toString();	
	String pricestr = price_obj.toString();
	float price = 0;
	try{price = Float.parseFloat(pricestr);}catch(Exception e){
	    System.err.println("Not a number "+pricestr);
	    e.printStackTrace();
	    return false;
	}
	ip.setCode(code);
	ip.setPrice(price);
	return true;
    }
    
    private static String code_val(Cell cell){//DO NOT return code value as a double number, return it as integer  !
	switch (cell.getCellType()) {
	case STRING:
	    return cell.getStringCellValue();
	case NUMERIC:
	    return Integer.toString((int)cell.getNumericCellValue());
	default:
	    throw new RuntimeException("Unknown code cell "+cell.getCellType());
	}
    }
    
    
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
    

    public static void main(String[] args)throws IOException{
	ProviderConfig cfg= new ProviderConfig();
	cfg.id = "test";
	cfg.state= "OH";
	cfg.parser = "xlsx";
	cfg.header = ProviderConfig._split_header("code:Bill Item||price:Base Price");
	XLSX_Parser xp = new  XLSX_Parser();
	FileInputStream is = new FileInputStream(new File("Blanchard Valley Hospital and Bluffton Hospital.xlsx"));	
	xp.parse(cfg, is);
    }
}

//https://en.wikipedia.org/wiki/Chargemaster
//https://www.cms.gov/Medicare/Coding/HCPCSReleaseCodeSets/Alpha-Numeric-HCPCS
//https://www.cms.gov/research-statistics-data-systems/medicare-provider-utilization-and-payment-data/medicare-provider-utilization-and-payment-data-inpatient/inpatient-charge-data-fy-2018

//https://www.cms.gov/Medicare/Coding/ICD10/ICD-10-MS-DRG-Conversion-Project




/*

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
*/
