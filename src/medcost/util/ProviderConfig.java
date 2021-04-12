package medcost.util;
import java.util.ArrayList;

public class ProviderConfig{
    public String id;
    public String state;
    public ArrayList<Config> configs = new ArrayList();
    public Config getConfig(String type){
	for(Config cfg : configs){
	    if(cfg.type.equals(type))return cfg;
	}
	return null;
    }
	
    private Config _cfg(String type){//add a new Config if needed
	for(Config cfg : configs){
	    if(cfg.type.equals(type))return cfg;
	}
	Config  cfg = new Config(this);
	cfg.type = type;
	configs.add(cfg);
	return cfg;
    }
    
    public static class Config{
	public Config(ProviderConfig cfg){this.cfg = cfg;}
	private ProviderConfig cfg;
	
	public String type="";
	public String ref_url;    
	public String file_url;
	public String file;
	public String parser;    
	public String header_line_start;
	public String[][] header;
	public  ProviderConfig getProviderConfig(){return cfg;}
    }

    public static ProviderConfig parse(String str){
	String[] lines = str.split("\n");
	ProviderConfig config = new ProviderConfig();
	for(String line : lines){
	    int p = line.indexOf("=");
	    if(p < 0 || line.startsWith("#"))continue;
	    String key = line.substring(0,p).trim();
	    String val = line.substring(p+1).trim();
	    String type="";
	    int px = key.indexOf(":");
	    if(px > 0){
		type= key.substring(0, p).trim().toLowerCase();
		key = key.substring(p +1).trim().toLowerCase();
	    }
	    Config cfg = config._cfg(type);	    
	    if("ref_url".equals(key))cfg.ref_url=val;
	    else if("file".equals(key))cfg.file = val;
	    else if("file_url".equals(key))cfg.file_url = val;
	    else if("ref_url".equals(key))cfg.ref_url = val;
	    else if("parser".equals(key))cfg.parser = val;
	    else if("header".equals(key))cfg.header = _split_header(val);
	    else System.err.println("Unknown key "+line);
	}
	return config;
    }

    public static String[][] _split_header(String val){	
	String[] ss = val.split("\\|\\|");
	String[][] header = new String[ss.length][2];
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
