package medcost.parser;
import java.io.*;
import java.util.*;
import javax.json.*;
import medcost.util.ProviderConfig;
    
public class JSON_Parser implements PricingParser{

    @Override
    public List<medcost.components.ItemPrice> parse(ProviderConfig cfg, InputStream is)throws IOException{
	//is = new CleanInputStream(is);
	List<medcost.components.ItemPrice> list = new LinkedList();
	JsonReader jsonReader = Json.createReader(is);
	JsonArray array = jsonReader.readArray();
	jsonReader.close();
	for(int i = 0 ; i < array.size(); i++){
	    JsonObject js = array.get(i);
	    list.add(js2ip(js, cfg));
	}
	return list;
    }  

    private static medcost.components.ItemPrice js2ip(JsonObject js, ProviderConfig cfg){
	medcost.components.ItemPrice ip = new medcost.components.ItemPrice();

	return ip;
    }
    
    public static void main(String[] args)throws IOException{
	FileInputStream is = new FileInputStream(new File("/Users/bill/Desktop/t.json"));
	/*
	CleanInputStream cis = new CleanInputStream(is);
	BufferedReader br = new BufferedReader(new InputStreamReader(cis));
	String line = null;
	while ( (line=br.readLine()) != null)System.out.print(line);
	*/
	ProviderConfig cfg= new ProviderConfig();
	cfg.id = "test";
	cfg.state= "OH";
	cfg.parser = "csv";
	cfg.header = ProviderConfig._split_header("code:Code||price:OP_Charge");
	JSON_Parser xp = new JSON_Parser();

	xp.parse(cfg, is);	
    }
}



class CleanInputStream extends FilterInputStream {
    public CleanInputStream(InputStream is){
        super(is);
    }

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

    
    @Override
    public int read() throws IOException {
        int c;
        do {
            c = super.read();
        } while(c != -1  && c != 9 && c != 10  && (c < 32 || c > 126) );
        return c;
    }
}



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