package it.android.j940549.myreg_elettronico.HttpUrlConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.android.j940549.myreg_elettronico.model.Scuola;

/**
 *
 * @author J940549
 */
public class CercaScuolaHttpConnection {//extends SwingWorker<String, Integer> {

    private final String USER_AGENT = "Mozilla/5.0 Chrome/73.0.3683.103";
    private String scuola, cfscuola, indirizzo;


    String url, host, urlRedirect;
    List<String> cooki;
    private CookieStore cookieStore;
    private CookieManager mCookieManager;
    private Proxy proxy;

    public CercaScuolaHttpConnection(String scuola, String cfscuola, String indirizzo) throws Exception {

        this.scuola = scuola;
        this.cfscuola = cfscuola;
        this.indirizzo = indirizzo;

        System.out.println("creato");
        doInBackground();
    }

    //    @Override
    protected String doInBackground() throws Exception {
        //       publish(1);

        proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxygdf.gdfnet.gdf.it", 8080));

        url = "https://www.sissiweb.it/AXIOS_SWAccessoRE2.aspx";

        String html = "";
        try {

//prima get per prendere la sessionid
            html = GetPageContent(url);

        } catch (Exception ex) {

            System.out.println("qui .." + ex.toString());
        }

        String param = "";
        try {

            param = getFormParams(html, "");
        } catch (UnsupportedEncodingException ex) {
            //     Logger.getLogger(BD_Com_Nettuno.class.getName()).log(Level.SEVERE, null, ex);
        }

        //       publish(3);
        String html1 = "";
        try {

            // post per inviare i parametri di ricerca al server
            html1 = sendPost(url, param);
            //estari di dati della scuola
            estraiDati(html1);

        } catch (Exception ex) {
            Logger.getLogger(CercaScuolaHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
        }


        return html1;
    }

    public String GetPageContent(String url) throws IOException {

        // abilita cooki menager
        mCookieManager = new CookieManager();

        URL obj = null;
        try {
            obj = new URL(url);

            host = obj.getProtocol() + "://" + obj.getHost();
            System.out.println("url: " + obj.getProtocol() + "\n" + obj.getHost() + "\n" + obj.getPath() + "\n" + obj.getPort() + "\n" + obj.getQuery());
            System.out.println("hosturl: " + host);

        } catch (MalformedURLException ex) {
            System.out.println("qui 1.." + ex.toString());
        }
        HttpURLConnection conn = null;
        try {
            //conn = (HttpsURLConnection) obj.openConnection();
            conn = (HttpURLConnection) obj.openConnection(proxy);

            conn.setRequestMethod("GET");

            conn.setUseCaches(false);

            // act like a browser
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "it-IT,en-US;q=0.7");
            conn.setReadTimeout(10000);
            conn.setInstanceFollowRedirects(false);
            HttpURLConnection.setFollowRedirects(false);

        } catch (ProtocolException ex) {
            System.out.println("qui 3.." + ex.toString());
        }

        int responseCode = 0;
        try {
            responseCode = conn.getResponseCode();
            System.out.println("responseCode.." + responseCode);
        } catch (IOException ex) {
            System.out.println("qui 4.." + ex.toString());
        }

        Map<String, List<String>> headerFields = conn.getHeaderFields();

        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        List<String> cookiesHeader = headerFields.get("Set-Cookie");
        List<String> locations = headerFields.get("Location");

        if (locations != null) {
            for (String location : locations) {
                System.out.println("location.." + location);
                urlRedirect = location;
                System.out.println("urlRedirect.." + urlRedirect);

            }
        }
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                System.out.println("cookie.." + HttpCookie.parse(cookie).get(0) + "\n");

                mCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
            System.out.println("CookieStore alla prima get..\n" + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";") + "\n");
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        } catch (IOException ex) {
            System.out.println("qui 5.." + ex.toString());
        }
        String inputLine;
        StringBuffer response = new StringBuffer();

        try {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");

            }
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        conn.disconnect();

        return response.toString();

    }


    public String getFormParams(String html, String red_rec_ass)
            throws UnsupportedEncodingException {

        System.out.println("Extracting form's data...");

        Document doc = Jsoup.parse(html);
        System.out.println(" elemts = " + doc.getAllElements().size());

        // Google form id
        Elements loginforms = doc.getElementsByTag("form");
        System.out.println(" elemts login form= " + loginforms.size());
        String keyloginform = "";
        List<String> paramList = new ArrayList<String>();
        //for (Element loginform : loginforms) {
        keyloginform = loginforms.get(0).attr("action");
        System.out.println("action: " + keyloginform);
        //if (keyloginform.equals("keyloginform"))
        Elements inputElements = loginforms.get(0).getElementsByTag("input");
        System.out.println(" elemts inserimento form= " + inputElements.size());
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            System.out.println(key);
            String value = inputElement.attr("value");
            System.out.println(value);
            if (key.equals("txtSearch")) {
                value = scuola;
                System.out.println(value);
            } else if (key.equals("txtSearchCF")) {
                value = cfscuola;
                System.out.println(value);
            } else if (key.equals("txtSearchAddress")) {
                value = indirizzo;
                System.out.println(value);
            }

            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                System.out.println("param partil....\n" + param);
                if (param.equals("=")) {

                } else {
                    result.append("&" + param);
                }
            }
        }
        String res = result.toString();
        //System.out.println("params....\n" + result.toString());
        System.out.println("params....\n" + res);
        return res;
    }


    public String loadCookies() {
        String cookies = "";
        if (mCookieManager.getCookieStore().getCookies().size() > 0) {
            System.out.println("mCookieManager.getCookieStore().getCookies().size()..." + mCookieManager.getCookieStore().getCookies().size());

            cookies = mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";");

        }
        return cookies;
    }

    public String sendPost(String unurl, String unpostParams) throws Exception {

        URL obj = new URL(unurl);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection(proxy);
//        HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

        // Acts like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");

        conn.setRequestProperty("Cookie", loadCookies());

        conn.setRequestProperty("Referer", unurl);//"https://family.axioscloud.it/Secret/RELogin.aspx");
        conn.setRequestProperty("Cache-Control", "max-age=0");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setReadTimeout(10000);
        conn.setInstanceFollowRedirects(false);
        HttpURLConnection.setFollowRedirects(false);
        //System.out.println("Cookie" + sessionid);
        System.out.println("Cookie al primo post : " + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";"));//.split(";")[0]);

        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(unpostParams);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending POST request to URL : " + unurl);
        System.out.println("Post parameters : " + unpostParams);
        //System.out.println("Post parameters : " + postParams);
        System.out.println("Response Code : " + responseCode);

        //leggo gli header del post
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        List<String> cookiesHeader = headerFields.get("Set-Cookie");
        List<String> locations = headerFields.get("Location");

        if (locations != null) {
            for (String location : locations) {
                System.out.println("location.." + location);
                urlRedirect = location;
                System.out.println("urlRedirect.." + urlRedirect);

                System.out.println("url1.." + urlRedirect);

            }
        }
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                System.out.println("cookie.." + HttpCookie.parse(cookie).get(0) + "\n");

                mCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
            System.out.println("CookieStore..\n" + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";") + "\n");
        }
        BufferedReader in
                = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();
        //  System.out.println("pagina post user.." + response.toString());

        try {
            String filename = "outfilename.html";

            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(response.toString());
            out.close();
        } catch (IOException e) {
            //Codice per gestire l'eccezione
        }
        return response.toString();
    }


    public ArrayList<Scuola> estraiDati(String html) {
        ArrayList<Scuola> scuole = new ArrayList<Scuola>();
        Document doc = Jsoup.parse(html);

        Elements tabelle = doc.getElementsByTag("table");
        System.out.println(" elemts tables= " + tabelle.size());
        Element tabella = tabelle.get(2);
        System.out.println(" elemts teabella form= " + tabella.text());
        Element tabella_body = tabella.getElementsByTag("tbody").get(0);
        Elements trs = tabella_body.getElementsByTag("tr");
        System.out.println(" elemts trs= " + trs.size() + "\n" + trs.text());


        for (int i = 4; i <= trs.size() - 2; i++) {
            System.out.println("elemts tr" + trs.get(i).text());

            Elements tds = trs.get(i).getElementsByTag("td");

            System.out.println("td.... " + tds.size() + "..dato" + tds.get(0));

            Scuola scuola = new Scuola();

            scuola.setTipo(tds.get(0).text());
            scuola.setNomescuola(tds.get(1).text());
            scuola.setIndirizzo(tds.get(2).text());
            scuola.setComune(tds.get(3).text());
            scuola.setCfscuola(tds.get(4).text());
            scuole.add(scuola);

            System.out.println(scuola.getTipo() + " ..... " + scuola.getNomescuola() + " ..... " + scuola.getCfscuola());
            System.out.println("Scuole.size.... " + scuole.size());

        }

        return scuole;
    }
}

