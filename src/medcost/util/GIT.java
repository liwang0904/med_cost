package medcost.util;

import java.io.*;
import java.util.*;
import java.net.*;

import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.*;
import org.eclipse.egit.github.core.service.*;

import medcost.components.Provider;
import medcost.parser.PricingParser;

public class GIT{
    static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(GIT.class);
    
    public static GitHubClient github_client(String token){
    	GitHubClient client = new GitHubClient();
	client.setOAuth2Token(token);
	return client;
    }
    
    public static medcost.util.ProviderConfig import_data_get_config(GitHubClient client, String uname, String repo_name, Provider provider)throws IOException{
	//ContentsService c_s = new ContentsService(client);
	RepositoryService r_s = new RepositoryService(client);
	DataService d_s = new DataService(client);
	Repository repo = r_s.getRepository(uname, repo_name);
	String base_path  =  "DATA/"+provider.getAddress_state().toUpperCase()+"/"+ provider.getHid();	
	String cstr = get_content(r_s,d_s, repo, base_path, "config.txt");
	//System.out.println(base_path+"@@@@CFG = "+cstr);
	medcost.util.ProviderConfig config = medcost.util.ProviderConfig.parse(cstr);
	config.id = provider.getId(); config.state = provider.getAddress_state();
	return config;
    }
    
    public static List<medcost.components.ItemPrice> import_data(GitHubClient client, String uname, String repo_name, Provider provider, medcost.util.ProviderConfig.Config cfg)throws IOException{
	RepositoryService r_s = new RepositoryService(client);
	DataService d_s = new DataService(client);
	Repository repo = r_s.getRepository(uname, repo_name);	
	String base_path  =  "DATA/"+provider.getAddress_state().toUpperCase()+"/"+ provider.getHid();	
	if(cfg == null)return null;	
	PricingParser parser  = PricingParser.getParser(cfg);
	if(!mm.util.Validator.empty(cfg.file_url)){//First : try download using file_url
	    URL url = new URL(cfg.file_url);
	    URLConnection uc = url.openConnection();
	    InputStream is = uc.getInputStream();
	    //TODO : cache as file ?
	    return parser.parse(cfg, is);
	}
	
	if(!mm.util.Validator.empty(cfg.file)){
	    byte[] bytes = get_content_bytes(r_s, d_s, repo, base_path, cfg.file);
	    return parser.parse(cfg, new ByteArrayInputStream(bytes));
	}
	
	return null;		
    }
    
    public final static String get_content(RepositoryService r_s, DataService d_s,  Repository repo, String base_path,  String fname)throws IOException{
	return new String(get_content_bytes(r_s, d_s, repo, base_path, fname));
    }
    
    public final static byte[] get_content_bytes(RepositoryService r_s, DataService d_s,  Repository repo, String base_path,  String fname)throws IOException{
	String sha = r_s.getBranches(repo).get(0).getCommit().getSha();
	System.out.println(sha);
	Tree tree = d_s.getTree(repo, sha, true);
	String path = base_path+"/"+fname;
	String blob_sha = null;
	for(TreeEntry en : tree.getTree()){     //find sha for file path
	    System.out.println(path + " "+ en.getPath());
	    if(path.equals(en.getPath())){blob_sha = en.getSha(); break;}
	}
	if(blob_sha == null)throw new RuntimeException("Can not find path :"+path);
	
	Blob blob  = d_s.getBlob(repo,  blob_sha);
	return org.apache.commons.codec.binary.Base64.decodeBase64(blob.getContent());
    }

    public static void main(String[] args)throws IOException{
	String token = args[0] + "_PWH53JTYnPcuWtklAfNdM9cqUzottI1EBuhh";
	
	String uname = "liwang0904";
	String repo_name = "med_cost";
	Provider provider = new Provider();
	//provider.setId("barnesville-hospital");
	provider.setId("university-hospitals-ahuja-medical-center");
	provider.setAddress_state("OH");
	GitHubClient client = github_client(token);
	medcost.util.ProviderConfig config = import_data_get_config(client, uname, repo_name, provider);
	medcost.util.ProviderConfig.Config cfg = config.getConfig("");	
	System.out.println(import_data(client, uname, repo_name, provider, cfg));
    }
}



	/*
	List<RepositoryContents> cts = null;	
	try{
	    cts = c_s.getContents(repo,(base_path+"/"+fname).replaceAll("//", "/") );
	}catch(org.eclipse.egit.github.core.client.RequestException e){e.printStackTrace();System.out.println("Error getting "+base_path+"/"+fname);}
	if(mm.util.Validator.empty(cts))return null;
	for(RepositoryContents  c : cts){
	    //c.getContent() ONLY work for files less than 1MB !!!
	    Blob blob  = d_s.getBlob(repo, c.getSha());
	    System.out.println("Get content name="+c.getName() + " path=" + c.getPath());
	    return new String(decodeBase64(blob.getContent()));
	}



DATA fc9113953f9ddb4423eca0365801fe499a59aef4
DATA/OH f25e94d631e8426ba5096ab82228efd99c088799
DATA/OH/university-hospitals-case-medical-center 8be8eecf4d9b1e79cb89fc4b4d580a2ac29db6ce
DATA/OH/university-hospitals-case-medical-center/CLEVELAND-CDM-20201228.csv 5e47bfd0369dd533aa2000e79c15661a5ad1b270
	

https://github.com/simonw/github-contents/blob/main/github_contents.py
org.eclipse.egit.github.core.client.RequestException: This API returns blobs up to 1 MB in size. The requested blob is too large to fetch via the API, but you can use the Git Data API to request blobs up to 100 MB in size. (403): Error with 'data' field in Blob resource


    Get the branch to find the commit sha
    Get the commit for the branch to find the tree sha
    Get the tree corresponding to the commit to find subtrees or the blob sha
    recurse through subtrees if the JSON file is not at the root of the repository
    Get the blob corresponding to the file
    base64decode the blob (or maybe use octokat's .readBinary() method)

//ghp
*/
