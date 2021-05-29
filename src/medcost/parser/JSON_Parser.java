package medcost.parser;
import java.io.*;
import java.util.*;
//import javax.json.*;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;

import medcost.util.ProviderConfig;
    
public class JSON_Parser implements PricingParser{

    @Override
    public List<medcost.components.ItemPrice> parse_charges(ProviderConfig.Config cfg, InputStream is)throws IOException{
	List<medcost.components.ItemPrice> list = new LinkedList();
	Reader reader = new InputStreamReader(is);
	JsonElement root_elt = new JsonParser().parse(reader);
	JsonArray array = root_elt.getAsJsonArray();
	for(int i = 0 , size = array.size();  i < size; i++){
	    JsonObject js = array.get(i).getAsJsonObject();
	    medcost.components.ItemPrice ip = js2ip(js, cfg);
	    if(ip == null)continue;
	    list.add(ip);	    
	}
	/*
	is = new CleanInputStream(is);
	JsonReader jsonReader = Json.createReader(is);
	JsonArray array = jsonReader.readArray();
	jsonReader.close();
	for( int i = 0 ; i < array.size() ; i++){
	    JsonObject js = array.getJsonObject(i);
	    medcost.components.ItemPrice ip = js2ip(js, cfg);
	    if(ip == null)continue;
	    list.add(ip);
	}
	*/
	return list;
    }  

    private static medcost.components.ItemPrice js2ip(JsonObject js, ProviderConfig.Config cfg){
	medcost.components.ItemPrice ip = new medcost.components.ItemPrice();
	ip.setProvider(cfg.getProviderConfig().id);
	ip.setCode_type(cfg.type);
	
	if(!parse_known_keys(ip, js, cfg))return null;

	for(Map.Entry<String, JsonElement> entry : js.entrySet()){
	    String key = entry.getKey();
	    if(PricingParser.is_known_key(key, cfg))continue; //Do not put known key in attributes
	    JsonElement value = entry.getValue();
	    ip.SET(key,value.toString());
	}
	    
	/*
	for (String key : js.keySet()){
	    if(PricingParser.is_known_key(key, cfg))continue; //Do not put known key in attributes
	    
	    Object value = js.get(key);
	    ip.SET(key, value);
	}
	*/
	return ip;
    }


    private static boolean parse_known_keys(medcost.components.ItemPrice ip, JsonObject js, ProviderConfig.Config cfg){
	for(String[] ss : cfg.header){
	    String key = ss[0];
	    String map_to = ss[1];
	    JsonElement val_elt  = js.get(map_to);
	    if(val_elt == null){
		System.err.println("Not found "+map_to + " js="+js);
		return false;
	    }
	    String val = val_elt.toString();
	    if("code".equals(key))ip.setCode(val);
	    else if("code_type".equals(key))ip.setCode_type(val);
	    else if("desc".equals(key) || "description".equals(key))ip.setDescription(val);
	    else if("list_price".equals(key))ip.setList_price(PricingParser.parse_float(val));
	    else if("self_price".equals(key))ip.setSelf_price(PricingParser.parse_float(val));	    
	    else throw new RuntimeException("Unknown Key "+key +" => " +map_to);
	}
	
	/*
	for(String[] ss : cfg.header){
	    String key = ss[0];
	    String map_to = ss[1];
	    String val  = js.getString(map_to, null);
	    if(val == null){//WTF !?!!
		Object obj = js.get(map_to);		
		if(obj != null)val = obj.toString();
	    }
	    if(val == null){
		System.err.println("Not found "+map_to + " js="+js);
		return false;
	    }
	    if("code".equals(key))ip.setCode(val);
	    else if("code_type".equals(key))ip.setCode_type(val);
	    else if("desc".equals(key) || "description".equals(key))ip.setDescription(val);
	    else if("list_price".equals(key))ip.setList_price(PricingParser.parse_float(val));
	    else if("self_price".equals(key))ip.setSelf_price(PricingParser.parse_float(val));	    
	    else throw new RuntimeException("Unknown Key "+key +" => " +map_to);
	}
	*/
	return true;
    }



    @Override
    public List<medcost.components.FeeSchedule> parse_fees(ProviderConfig.Config cfg, InputStream is)throws IOException{
	return null;
    }

    
    public static void main(String[] args)throws IOException{
	FileInputStream is = new FileInputStream(new File("/tmp/t.json"));
	JsonElement elt = new JsonParser().parse(new FileReader(new File("/tmp/t.json")));
	System.out.println(elt.getAsJsonArray());
	/*
	CleanInputStream cis = new CleanInputStream(is);
	BufferedReader br = new BufferedReader(new InputStreamReader(cis));
	String line = null;
	while ( (line=br.readLine()) != null)System.out.print(line);
	*/
	ProviderConfig config= new ProviderConfig();
	config.id = "test";
	config.state= "OH";

	ProviderConfig.Config cfg= new ProviderConfig.Config(config);
	cfg.parser = "csv";
	cfg.header = ProviderConfig._split_header("code:Code||price:OP_Charge");
	JSON_Parser xp = new JSON_Parser();
	
	xp.parse_charges(cfg, is);  
    }
}



class CleanInputStream extends FilterInputStream {
    public CleanInputStream(InputStream is){super(is);}
    
    @Override
    public int read() throws IOException {
        int c;
        do {
            c = super.read();
        } while(c != -1 && c != '\r' && c != '\n' && c != 9 && c != 10  && (c < 32 || c > 126) );
        return c;
    }
}
    

    /*
    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
	System.out.println(b.length+ " " + off + " " + len);
        int n = 0, c;
        do {
            c = this.read();
            if(c != -1) {
                b[off + n] = (byte) c;
                n++;
                len--;  
            } else {
		System.out.println("Get -1");
                return c;
            }
        } while(c != -1 && len > 0);
	
        return n;
    }
    */
    

/*
class NoNewLineInputStream extends FilterInputStream {
    public NoNewLineInputStream(InputStream is){
        super(is);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = 0, c;
        do {
            c = this.read();
            if(c != -1) {
                b[off + n] = (byte) c;
                n++;
                len--;  
            } else {
                return c;
            }
        } while(c != -1 && len > 0);
        return n;
    }


    @Override
    public int read() throws IOException {
        int c;
        do {
            c = super.read();
        } while(c != -1 && (c == '\n' || c == '\r' || c == 0 || c == 31579));
        return c;
    }
}
*/
