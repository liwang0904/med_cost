package medcost.util;
import java.io.*;
import java.util.*;

public interface PricingParser{

    List<medcost.components.ItemPrice> parse(ProviderConfig cfg, InputStream is)throws IOException;

    static boolean DEBUG = true;
    static PricingParser getParser(ProviderConfig cfg){
	if(cfg.parser != null && cfg.parser.startsWith("class:")){
	    String clazz = cfg.parser.substring(6).trim();
	    try{
		return (PricingParser)Class.forName(clazz).newInstance();
	    }catch(Throwable t){		
		t.printStackTrace();
	    }
	}
	   
	if("xlsx".equalsIgnoreCase(cfg.parser))
	    return new XLSX_Parser();

	return new CSV_Parser();	
    }
}
