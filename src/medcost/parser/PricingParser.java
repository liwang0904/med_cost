package medcost.parser;
import java.io.*;
import java.util.*;
import medcost.util.ProviderConfig;

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

    static float parse_float(String str){
	if(mm.util.Validator.empty(str))return 0f;
	str = str.replaceAll(",", "");
	return Float.parseFloat(str);
    }
}

//###https://uhhospitals.patientsimple.com/guest/#/estimates/patientestimate
