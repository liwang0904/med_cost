package medcost.parser;
import java.io.*;
import java.util.*;
import org.apache.commons.csv.*;
import medcost.util.ProviderConfig;
    
public class CSV_Parser implements PricingParser{

    @Override
    public List<medcost.components.ItemPrice> parse(ProviderConfig.Config cfg, InputStream is)throws IOException{
	List<medcost.components.ItemPrice> list = new LinkedList();
	BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	if(cfg.header_line_start != null){
	    while(true){
		reader.mark(8192);
		String line = reader.readLine();
		if(line.startsWith(cfg.header_line_start)){
		    reader.reset();
		    break;
		}
	    }
	}	
	////for(int i = 0; i<cfg.header_skip_lines; i++)	 reader.readline();
	
	CSVParser parser = CSVFormat.EXCEL.withHeader().parse(reader);

	Map<String,Integer> map = parser.getHeaderMap();//figure out all the column index
	System.out.println("MAP***" + map);
	Map<String,Integer> indices = new HashMap();    //known key index
	Map<String,Integer> other_indices = new HashMap(); //all other index
	for(Map.Entry<String,Integer> entry : map.entrySet()){
	    String key = entry.getKey();
	    String[] header = null;//is it an known header
	    for(String[] ss : cfg.header){
		if(ss[1].equals(key)){header = ss; break;}
	    }
	    
	    if(header != null)	indices.put(header[0], entry.getValue());
	    else		other_indices.put(key, entry.getValue());	    
	}
	
	for (CSVRecord record : parser){
	    medcost.components.ItemPrice ip = record2ip(record, cfg, indices, other_indices);
	    if(ip != null)list.add(ip);
	}
	reader.close(); is.close();
	return list;
    }  

    private static medcost.components.ItemPrice record2ip(CSVRecord record, ProviderConfig.Config cfg, Map<String,Integer> indices, Map<String,Integer> other_indices){
	medcost.components.ItemPrice ip = new medcost.components.ItemPrice();
	ip.setProvider(cfg.getProviderConfig().id);
	
	if(!parse_known_keys(ip, record, indices))return null;
	
	for(Map.Entry<String,Integer> entry : other_indices.entrySet()){
	    ip.SET(entry.getKey(), record.get(entry.getValue()));
	}

	if(DEBUG)System.out.println(ip);
	return ip;
    }

    

    private static boolean parse_known_keys(medcost.components.ItemPrice ip, CSVRecord record, Map<String,Integer> indices){
	Integer code_index = indices.get("code");
	Integer iob_index = indices.get("iob");
	Integer payer_index = indices.get("payer");		
	Integer desc_index = indices.get("description");
	Integer allowed_index = indices.get("allowed_amount");		
	Integer price_index = indices.get("price");
	if(code_index != null)ip.setCode(record.get(code_index));
	if(iob_index != null)ip.setIob(record.get(iob_index));
	if(payer_index != null)ip.setPayer(record.get(payer_index));
	if(desc_index != null)ip.setDescription(record.get(desc_index));
	if(allowed_index != null && !mm.util.Validator.empty(record.get(allowed_index)))ip.setAllowed_amount(PricingParser.parse_float(record.get(allowed_index)));
	if(price_index != null && !mm.util.Validator.empty(record.get(price_index)))ip.setPrice(PricingParser.parse_float(record.get(price_index)));
	if(ip.getCode() == null || ip.getPrice() == 0){
	    System.out.println("@@@@ No code or price: "+record + " ----- "+ indices);return false;
	}
	return true;
    }
    
    
    public static void main(String[] args)throws IOException{
	ProviderConfig config= new ProviderConfig();
	config.id = "test";
	config.state= "OH";
	ProviderConfig.Config cfg = new ProviderConfig.Config(config);
	cfg.parser = "csv";
	cfg.header_line_start = "";
	cfg.header = ProviderConfig._split_header("code:CPT/HCPCS||price:Gross Charge (*Pharmacy Reflects Average Charge Per Patient) ");
	config.configs.add(cfg);
	
	CSV_Parser xp = new CSV_Parser();
	FileInputStream is = new FileInputStream(new File("src/medcost/parser/31_0833936_CCHMC_STANDARD_CHARGES.csv"));	
	xp.parse(cfg, is);	
    }
}
