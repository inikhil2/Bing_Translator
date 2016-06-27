package TestNG;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class AdmAuthentication {

	public static final String DatamarketAccessUri = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";
	public static final String txtFilePath="/home/nikhilendrapandey/Documents/workspace/Bing_Translator/src/test.txt";
	private String clientId;
	private String clientSecret;
	private String request;
	private AdmAccessToken token;
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public AdmAccessToken getToken() {
		return token;
	}
	public void setToken(AdmAccessToken token) {
		this.token = token;
	}
	
	
	public AdmAuthentication(String clientId, String clientSecret) throws IOException
    {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	    this.clientId= URLEncoder.encode(this.clientId,"UTF-8");
	    this.clientSecret= URLEncoder.encode(this.clientSecret,"UTF-8");
	    this.request= "grant_type=client_credentials&client_id="+ this.clientId +"&client_secret="+ this.clientSecret +"&scope=http://api.microsofttranslator.com";
		
    }
	
	public AdmAccessToken getAccessTokenUsingPost(String DatamarketAccessUri,String request) throws IOException, ParseException{
		
		URL url= new URL(DatamarketAccessUri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
	    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
		wr.writeBytes(request);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
	//	System.out.println(response.toString());
		JSONParser parser = new JSONParser();
		JSONObject object= (JSONObject) parser.parse(response.toString());
		AdmAccessToken admToken= new AdmAccessToken();
		String tokenType= (String) object.get("token_type");
		String accessToken=(String) object.get("access_token");
		String expiresIn=(String)object.get("expires_in");
		String scope= (String)object.get("scope");
		admToken.setAccessToken(accessToken);
		admToken.setTokenType(tokenType);
		admToken.setExpiresIn(expiresIn);
		admToken.setScope(scope);
		setToken(admToken);
		conn.disconnect();
		return this.getToken();
	}
	
	public String translateText(String from,String to,String text) throws IOException, ParserConfigurationException, SAXException{
	
		if(from==null||to==null||text==null || from.equals(to)){
			return null;
		}
		
		String uri = "http://api.microsofttranslator.com/v2/Http.svc/Translate?text="+URLEncoder.encode(text,"UTF-8")+ "&from=" + from + "&to=" + to;
	    String authToken = "Bearer" + " " + this.getToken().getAccessToken();
	    URL url= new URL(uri);
		//System.out.println(authToken);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
	    conn.setRequestProperty("Authorization", authToken);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
        conn.disconnect();
		return this.xmlToDataRetriever(response.toString());

	}
	public String xmlToDataRetriever(String xmlString) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	    InputSource is = new InputSource();
	    is.setCharacterStream(new StringReader(xmlString));
	    Document doc = db.parse(is);
	    Element e= doc.getDocumentElement();
	    String str= e.getTextContent();
		return str;
	}
	
	public String autodetectTranslation(String to,String text)throws IOException, ParserConfigurationException, SAXException{
		String uri ="http://api.microsofttranslator.com/v2/Http.svc/Detect?text=" + URLEncoder.encode(text,"UTF-8");
		String authToken = "Bearer" + " " + this.getToken().getAccessToken();
	    URL url= new URL(uri);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
	    conn.setRequestProperty("Authorization", authToken);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		conn.disconnect();
		String detectedXml= response.toString();
		String detectedLanguage= this.xmlToDataRetriever(detectedXml);
		return this.translateText(detectedLanguage, to, text);
	}
	
	public List<String> getLinesFromFile(String path) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(path));
		String line;
		List<String> fileStrings= new ArrayList<String>();
		while((line = in.readLine()) != null)
		{
		    fileStrings.add(line);
		}
		in.close();
		return fileStrings;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, JSONException{
		
		String clientId="xxxy123";
		String clientSecret="QW7xU9PnSI+Ijt9k2/M1v8wOJ7Goo1f+Lyj5Bi/Jd5s=";
	//	AdmAuthentication authToken= new AdmAuthentication(clientId, clientSecret);
		String com;
		
		try {
			AdmAuthentication authToken= new AdmAuthentication(clientId, clientSecret);
			List<String> lines= authToken.getLinesFromFile(txtFilePath);
			try {
			AdmAccessToken token=	authToken.getAccessTokenUsingPost(DatamarketAccessUri, authToken.getRequest());
				//System.out.println(token);
			//	System.out.println(token.getAccessToken());
				String text = null;
				String from = null;
				String to = null;
					try {
					for(int i=0;i<lines.size();i++){
						String[] problem= lines.get(i).split(",");
						NewTest n= new NewTest();
						String a=n.transTest(problem[0],problem[1],problem[2]);
						String []s=a.split(",");
					    from= s[0];
					    to= s[1];
					    text= problem[2];
				   if(from==null || from.equals("")){
					    	 com=authToken.autodetectTranslation(to, text);
					    }
					    else{
					    	 com=authToken.translateText(from, to, text);
					    }
				   if (com.equals(s[2])){
					   System.out.println(com+"  -True");
				   }
				   
					}
						
					
				} catch (ParserConfigurationException e) {
					
					e.printStackTrace();
				} catch (SAXException e) {
				
					e.printStackTrace();
				}
			
			} catch (ParseException e) {
			
				e.printStackTrace();
			}
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		}
}