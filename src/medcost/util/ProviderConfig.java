package medcost.util;

public class ProviderConfig{
    public String id;
    public String state;
    
    public String ref_url;    
    public String file_url;
    public String file;
    public String parser;
    public String header_line_start;
    public String[][] header;
    

    public static ProviderConfig parse(String str){
	String[] lines = str.split("\n");
	ProviderConfig cfg = new ProviderConfig();
	for(String line : lines){
	    int p = line.indexOf("=");
	    if(p < 0)continue;
	    String key = line.substring(0,p).trim();
	    String val = line.substring(p+1).trim();
	    if("ref_url".equals(key))cfg.ref_url=val;
	    else if("file".equals(key))cfg.file = val;
	    else if("file_url".equals(key))cfg.file_url = val;
	    else if("ref_url".equals(key))cfg.ref_url = val;
	    else if("parser".equals(key))cfg.parser = val;
	    else if("header".equals(key))cfg.header = _split_header(val);
	    else System.err.println("Unknown key "+line);
	}
	return cfg;
    }

    public static String[][] _split_header(String val){	
	String[] ss = val.split("\\|\\|");
	String[][] header = new String[2][ss.length];
	for(int i = 0 ; i < ss.length; i++){
	    String s = ss[i];
	    int px = s.indexOf(":");
	    String n = s.substring(0,px).trim();
	    String v = s.substring(px+1).trim();
	    header[i][0] = n;
	    header[i][1] = v;
	}
	return header;
    }
}
