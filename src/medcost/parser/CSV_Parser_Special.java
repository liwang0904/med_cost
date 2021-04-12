package medcost.parser;
import java.io.*;
import java.util.*;
import org.apache.commons.lang.*;
import org.apache.commons.csv.*;
import medcost.util.ProviderConfig;

public class CSV_Parser_Special extends CSV_Parser {
    /*
    public boolean isEmpty(CSVRecord csvRecord) {
	if (csvRecord == null) return true;
	for (int i = 0; i < csvRecord.size(); i++)
	    if (StringUtils.isNotBlank(csvRecord.get(i))) return false;
	return true;
    }
    
    @Override
    public List<medcost.components.ItemPrice> parse(ProviderConfig.Config cfg, InputStream is) throws IOException{
	List<medcost.components.ItemPrice> list = new LinkedList();
	BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	System.out.println("Header Line Start: " + cfg.header_line_start);
	if (cfg.header_line_start != null){
	    while(true){
		reader.mark(8192);
		String line = reader.readLine();
		if (line.startsWith(cfg.header_line_start)){
		    reader.reset();
		    break;
		}
	    }
	}
	
	CSVParser parser = CSVFormat.EXCEL.withHeader().parse(reader);
	System.out.println(parser.getHeaderMap());
	int i = 1;
	for (CSVRecord record : parser) {
	    i++;
	    if (!record.isConsistent()) { //System.out.println(i);
		continue; }
	    String topic = record.get(0);
	    if (topic != null && !topic.isEmpty()) {}
	}

	Map<String,Integer> map = parser.getHeaderMap();
	return list;
    }

    public static void main(String[] args) throws IOException{
	ProviderConfig cfg = new ProviderConfig();
	cfg.id = "test";
	cfg.state= "OH";
	cfg.parser = "csv";
	cfg.header_line_start = "Description";
	cfg.header = ProviderConfig._split_header("code: CPT/HCPCS||price: Gross Charge (*Pharmacy Reflects Average Charge Per Patient)");
	CSV_Parser_Special xp = new CSV_Parser_Special();
	FileInputStream is = new FileInputStream(new File("src/medcost/parser/31_0833936_CCHMC_STANDARD_CHARGES.csv"));
	
	xp.parse(cfg, is);	
    }
    */
}
