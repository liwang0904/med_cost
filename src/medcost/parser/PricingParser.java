package medcost.parser;
import java.io.*;
import java.util.*;
import medcost.util.ProviderConfig;

public interface PricingParser{

    List<medcost.components.ItemPrice> parse(ProviderConfig.Config cfg, InputStream is)throws IOException;

    static boolean DEBUG = true;
    static PricingParser getParser(ProviderConfig.Config cfg){
	if(cfg.parser != null && cfg.parser.startsWith("class@")){
	    String clazz = cfg.parser.substring(6).trim();
	    try{
		return (PricingParser)Class.forName(clazz).newInstance();
	    }catch(Throwable t){		
		t.printStackTrace();
	    }
	}

	
	   
	if("json".equalsIgnoreCase(cfg.parser))
	    return new JSON_Parser();
	   
	if("xlsx".equalsIgnoreCase(cfg.parser))
	    return new XLSX_Parser();

	if("csv".equalsIgnoreCase(cfg.parser) || cfg.parser == null || cfg.parser.length() == 0)
	    return new CSV_Parser();
	
	throw new RuntimeException("Unknown parser ? " + cfg.parser);
		
    }

    static float parse_float(String str){
	if(mm.util.Validator.empty(str))return 0f;
	str = str.replaceAll(",", "");
	return Float.parseFloat(str);
    }

    static boolean is_known_key(String key, ProviderConfig.Config cfg){
	for(String[] ss : cfg.header){
	    String map_to = ss[1];
	    if(map_to.equals(key))return true;
	}
	return false;
    }
}

//###https://uhhospitals.patientsimple.com/guest/#/estimates/patientestimate
