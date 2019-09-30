package it.android.j940549.myreg_elettronico.HttpUrlConnection;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

import it.android.j940549.myreg_elettronico.model.Assenza;
import it.android.j940549.myreg_elettronico.model.Compito;
import it.android.j940549.myreg_elettronico.model.Comunicazione;
import it.android.j940549.myreg_elettronico.model.ConvertiData;
import it.android.j940549.myreg_elettronico.model.Voto;

/**
 * @author J940549
 */
public class RegHttpConnection_adatto {//extends SwingWorker<String, Integer> {

    private final String USER_AGENT = "Mozilla/5.0 Chrome/73.0.3683.103";
    private String scuola, cod_alunno, classe_sez, anno_sc;
    private String utente, password;
    private String html_votiI, html_votiII, html_compiti, html_asssenze, html_classe, html_comunicazioni;
    private ArrayList<Assenza> assenze = new ArrayList<Assenza>();
    private ArrayList<Compito> compiti = new ArrayList<Compito>();
    private ArrayList<Voto> voti = new ArrayList<Voto>();
    private ArrayList<Comunicazione> comunicazioni = new ArrayList<Comunicazione>();
    String TAG_LOG = "RegHttpConnection";

    String url, host, urlRedirect;
    private CookieManager mCookieManager;
    //private Proxy proxy;

    public RegHttpConnection_adatto(String scuol, String cod_al, String unutente, String pw, String annosc) throws Exception {

        cod_alunno = cod_al;
        utente = unutente;
        password = pw;
        this.scuola = scuol;
        anno_sc = annosc.split("/")[0];
        Log.i(TAG_LOG, "dati" + cod_alunno + ".." + scuola + ".." + anno_sc);

    }

    //    @Override
    public String doInBackground() throws Exception {
        String result = "";//       publish(1);

        //proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxygdf.gdfnet.gdf.it", 8080));
        url = "https://family.axioscloud.it/Secret/REStart.aspx?Customer_ID=" + scuola;

// inizio correzione problema excepiton ssl x certificato
        TrustManager tm = new X509TrustManager()  {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        ////
/*
        TrustManager[] trustManagers = new TrustManager[0];
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            trustManagers = new TrustManager[]{
                    new X509ExtendedTrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] xcs, String string, Socket socket) throws CertificateException {
                        }
                        @Override
                        public void checkServerTrusted(X509Certificate[] xcs, String string, Socket socket) throws CertificateException {
                        }
                        @Override
                        public void checkClientTrusted(X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException {
                        }
                        @Override
                        public void checkServerTrusted(X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException {
                        }
                        @Override
                        public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                        }
                        @Override
                        public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                        }
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };
        }
*/


        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[] { tm }, null);

//        sc.init(null, trustManagers, new java.security.SecureRandom());

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // crea tutti gli all-trusting host verifier
        HostnameVerifier allHostValid = new HostnameVerifier() {
            @Override
            public boolean verify(String string, SSLSession ssls) {
                return true;
            }
        };

        // installa  all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostValid);

        // fine correzione problema excepiton ssl x certificato
//        publish(2);
        String html = "";
        try {

//prima get per prendere la sessionid
            html = GetPageContent(url);

        } catch (Exception ex) {

            Log.i(TAG_LOG, "qui .." + ex.toString());
        }

        String param = "", param2 = "";
        try {

            //prendo i parametri della pagina aspx
            param = getFormParams(html, "", "");
        } catch (UnsupportedEncodingException ex) {
            //     Logger.getLogger(BD_Com_Nettuno.class.getName()).log(Level.SEVERE, null, ex);
        }

        //       publish(3);
        String html1 = "";
        try {

            //primo post per inviare i parametri della pagine aspx
            html1 = sendPost(url, param);
//            seconda get alla pagina ritornata in Location con codice 302
            html1 = sendGet(urlRedirect);//sendGet2(urlRedirect);
            //terza get alla pagina di login ritornata in Location con codice 302;
            html1 = sendGet(urlRedirect);//sendGet3(urlRedirect);

            try {
                // prendo i parametri della pagina di login ed aggiungo userid e password
                param2 = getFormParams(html1, "", "");
            } catch (UnsupportedEncodingException ex) {
                //     Logger.getLogger(BD_Com_Nettuno.class.getName()).log(Level.SEVERE, null, ex);
            }
            //invio post di login
            html1 = sendPost(urlRedirect, param2);//sendPost(urlRedirect, param2);
            //terza get alla pagina di REFamily ritornata in Location con codice 302;

            html1 = sendGet(urlRedirect);//sendGet3(urlRedirect);

            // prendo i parametri della pagina dopo il login ed aggiungo i parametri per registro docente (voti)


            param2 = "";
            //invio i dati per ottenere i voti I quadr

            param2 = getFormParams(html1, "RED", "FT01");
            html_votiI = sendPost(urlRedirect, param2);//sendPost(urlRedirect, param2);
            Log.i(TAG_LOG, "html_voti Iquadr..\n" + html_votiI);
            voti.clear();
            estraiVoti(html_votiI, cod_alunno, "1");

            //invio i dati per ottenere i voti II quadr
            html1 = sendGet(urlRedirect);
            param2 = getFormParams(html1, "RED", "FT02");
            html_votiII = sendPost(urlRedirect, param2);//sendPost(urlRedirect, param2);
            Log.i(TAG_LOG, "html_voti IIquadr..\n" + html_votiII);
            estraiVoti(html_votiII, cod_alunno, "2");

            // prendo i parametri della pagina dopo il login ed aggiungo i parametri per registro di classe (compiti)

            param2 = "";
            param2 = getFormParams(html1, "REC", "");

            //invio i dati per ottenere i compiti
            html_compiti = sendPost(urlRedirect, param2);//sendPost(urlRedirect, param2);
            compiti.clear();
            estraiCompiti(html_compiti, cod_alunno);

            // prendo i parametri della pagina dopo il login ed aggiungo i parametri per le assenze
            param2 = "";
            param2 = getFormParams(html1, "Assenze", "");

            //invio i dati per ottenere le assenze
            html_asssenze = sendPost(urlRedirect, param2);//sendPost(urlRedirect, param2);
            assenze.clear();
            estraiAssenze(html_asssenze, cod_alunno);


            // prendo i parametri della pagina dopo il login ed aggiungo i parametri per le comunicazioni
            param2 = "";
            param2 = getFormParams(html1, "COMUNICAZIONI", "");

            //invio i dati per ottenere le comunicazioni
            html_comunicazioni = sendPost(urlRedirect, param2);//sendPost(urlRedirect, param2);
            comunicazioni.clear();
            estraiComunicazioni(html_comunicazioni, cod_alunno);


            param2 = getFormParams(html1, "Curriculum", "");

            //invio i dati per ottenere le assenze
            html_classe = sendPost(urlRedirect, param2);//sendPost(urlRedirect, param2);
            classe_sez = estraiClasse(html_classe);


        } catch (Exception ex) {
            Logger.getLogger(RegHttpConnection_adatto.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result = voti.size() + "*" + compiti.size() + "*" + assenze.size();
    }

    public String GetPageContent(String url) throws IOException {

        // abilita cooki menager
        mCookieManager = new CookieManager();

        URL obj = null;
        try {
            obj = new URL(url);

            host = obj.getProtocol() + "://" + obj.getHost();
//            Log.i(TAG_LOG,"url: " + obj.getProtocol() + "\n" + obj.getHost() + "\n" + obj.getPath() + "\n" + obj.getPort() + "\n" + obj.getQuery());
            //          Log.i(TAG_LOG,"hosturl: " + host);

        } catch (MalformedURLException ex) {
            Log.i(TAG_LOG, "qui 1.." + ex.toString());
        }
        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) obj.openConnection();
            //conn = (HttpsURLConnection) obj.openConnection(proxy);

            conn.setRequestMethod("GET");

            conn.setUseCaches(false);

            // act like a browser
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Accept-Language", "it-IT,en-US;q=0.7");
            conn.setReadTimeout(10000);
            conn.setInstanceFollowRedirects(false);
            HttpsURLConnection.setFollowRedirects(false);

        } catch (ProtocolException ex) {
            Log.i(TAG_LOG, "qui 3.." + ex.toString());
        }

        int responseCode = 0;
        try {
            responseCode = conn.getResponseCode();
            Log.i(TAG_LOG, "responseCode.." + responseCode);
        } catch (IOException ex) {
            Log.i(TAG_LOG, "qui 4.." + ex.toString());
        }

        Map<String, List<String>> headerFields = conn.getHeaderFields();

        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            //        Log.i(TAG_LOG,"Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        List<String> cookiesHeader = headerFields.get("Set-Cookie");
        List<String> locations = headerFields.get("Location");

        if (locations != null) {
            for (String location : locations) {
                //          Log.i(TAG_LOG,"location.." + location);
                urlRedirect = location;
                //        Log.i(TAG_LOG,"urlRedirect.." + urlRedirect);

            }
        }
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                //      Log.i(TAG_LOG,"cookie.." + HttpCookie.parse(cookie).get(0) + "\n");

                mCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
            //Log.i(TAG_LOG,"CookieStore alla prima get..\n" + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";") + "\n");
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        } catch (IOException ex) {
            Log.i(TAG_LOG, "qui 5.." + ex.toString());
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


    public String getFormParams(String html, String red_rec_ass, String ft)
            throws UnsupportedEncodingException {

//        Log.i(TAG_LOG,"Extracting form's data...");

        Document doc = Jsoup.parse(html);
        //      Log.i(TAG_LOG," elemts = " + doc.getAllElements().size());

        // Google form id
        Elements loginforms = doc.getElementsByTag("form");
        //       Log.i(TAG_LOG," elemts login form= " + loginforms.size());
        String keyloginform = "";
        List<String> paramList = new ArrayList<String>();
        //for (Element loginform : loginforms) {
        keyloginform = loginforms.get(0).attr("action");
        //     Log.i(TAG_LOG,"action: " + keyloginform);
        //if (keyloginform.equals("keyloginform"))
        Elements inputElements = loginforms.get(0).getElementsByTag("input");
        Elements selectElements = loginforms.get(0).getElementsByTag("select");
        //     Log.i(TAG_LOG," elemts inserimento form= " + inputElements.size());
        for (Element inputElement : inputElements) {
            String key = inputElement.attr("name");
            //      Log.i(TAG_LOG,key);
            String value = inputElement.attr("value");
            if (key.equals("txtUser")) {
                value = utente;
                //            Log.i(TAG_LOG,value);
            } else if (key.equals("txtPassword")) {
                value = password;
                //          Log.i(TAG_LOG,value);

            } else if (key.equals("ibtnRE")) {
                aggiungi_param(paramList);

            } else if (key.equals("btnLoginACC")) {
                break;

            } else if (key.equals("__EVENTTARGET")) {
                value = "FAMILY";

            } else if (key.equals("__EVENTARGUMENT")) {

                value = red_rec_ass;

            } else if (key.equals("ctl00$ContentPlaceHolderBody$txtAluSelected")) {
                value = cod_alunno;//"00008078";

            } else if (key.equals("ctl00$ContentPlaceHolderBody$txtIDAluSelected")) {
                value = "0";

            }
            //        Log.i(TAG_LOG,value);

            paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
        }

        //Log.i(TAG_LOG," elemts Select inserimento form= " + selectElements.size());
        for (Element selectElement : selectElements) {
            String key = selectElement.attr("name");
            //  Log.i(TAG_LOG,key);
            String value = selectElement.attr("value");
            if (key.equals("ctl00$ContentPlaceHolderMenu$ddlFT")) {
                if (!ft.equals("")) {
                    value = ft;
                }
//                Log.i(TAG_LOG, value);
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            } else if (key.equals("ctl00$ContentPlaceHolderMenu$ddlAnno")) {
                value = anno_sc;
                paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
            }
            //          Log.i(TAG_LOG,value);


        }
        StringBuilder result = new StringBuilder();
        for (String param : paramList) {
            if (result.length() == 0) {
                result.append(param);
            } else {
                //    Log.i(TAG_LOG,"param partil....\n" + param);
                if (param.equals("=")) {

                } else {
                    result.append("&" + param);
                }
            }
        }
        String res = result.toString();
        //System.out.println("params....\n" + result.toString());
        //Log.i(TAG_LOG,"params....\n" + res);
        return res;
    }

    public void aggiungi_param(List<String> paramList) throws UnsupportedEncodingException {

        paramList.add("ibtnRE" + ".x" + "=" + URLEncoder.encode("0", "UTF-8"));
        paramList.add("ibtnRE" + ".y" + "=" + URLEncoder.encode("0", "UTF-8"));

    }

    public String loadCookies() {
        String cookies = "";
        if (mCookieManager.getCookieStore().getCookies().size() > 0) {
            //       Log.i(TAG_LOG,"mCookieManager.getCookieStore().getCookies().size()..." + mCookieManager.getCookieStore().getCookies().size());

            cookies = mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";");

        }
        return cookies;
    }

    public String sendPost(String unurl, String unpostParams) throws Exception {

        URL obj = new URL(unurl);
//        HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection(proxy);
        HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

        // Acts like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");

        conn.setRequestProperty("Cookie", loadCookies());

        conn.setRequestProperty("Referer", unurl);//"https://family.axioscloud.it/Secret/RELogin.aspx");
        conn.setRequestProperty("Cache-Control", "max-age=0");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setReadTimeout(10000);
        conn.setInstanceFollowRedirects(false);
        HttpsURLConnection.setFollowRedirects(false);
        //System.out.println("Cookie" + sessionid);
        //    Log.i(TAG_LOG,"Cookie al primo post : " + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";"));//.split(";")[0]);

        conn.setDoOutput(true);
        conn.setDoInput(true);

        // Send post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(unpostParams);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        //     Log.i(TAG_LOG,"\nSending POST request to URL : " + unurl);
        //     Log.i(TAG_LOG,"Post parameters : " + unpostParams);
        //System.out.println("Post parameters : " + postParams);
        //     Log.i(TAG_LOG,"Response Code : " + responseCode);

        //leggo gli header del post
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            //         Log.i(TAG_LOG,"Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        List<String> cookiesHeader = headerFields.get("Set-Cookie");
        List<String> locations = headerFields.get("Location");

        if (locations != null) {
            for (String location : locations) {
                //              Log.i(TAG_LOG,"location.." + location);
                urlRedirect = location;
                //              Log.i(TAG_LOG,"urlRedirect.." + urlRedirect);
                urlRedirect = urlRedirect.replace("Customer_WebSite=http://www.axiositalia.com", "Customer_WebSite=https://family.axioscloud.it/Secret/REStart.aspx?Customer_ID=80249350580");
                //url1=url1.replace("Customer_WebSite=http://www.axiositalia.com", "Customer_WebSite=https://family.axioscloud.it/Secret/REStart.aspx?Customer_ID=80249350580");
                if (urlRedirect.startsWith("/")) {
                    urlRedirect = "https://family.axioscloud.it" + urlRedirect;
                }
                //            Log.i(TAG_LOG,"url1.." + urlRedirect);

            }
        }
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                //          Log.i(TAG_LOG,"cookie.." + HttpCookie.parse(cookie).get(0) + "\n");

                mCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
            //        Log.i(TAG_LOG,"CookieStore..\n" + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";") + "\n");
        }
        BufferedReader in
                = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();

        return response.toString();
    }

    public String sendGet(String unurl) throws Exception {
        //     Log.i(TAG_LOG,"url encode sendGet2: " + unurl);
        URL obj = new URL(unurl);
        if (unurl.contains("https://family.axioscloud.it/Secret/REDefault.aspx?")) {
            String url_corretta = "https://family.axioscloud.it/Secret/REDefault.aspx?" + creaquery(obj.getQuery());//url[0]+"?"+URLEncoder.encode(url[1], "UTF-8"));
            ///         Log.i(TAG_LOG,"url_corretta..\n" + url_corretta);

            obj = new URL(url_corretta);
        }
        //    Log.i(TAG_LOG,"url: " + obj.getHost() + "\n" + obj.getPath() + "\n" + obj.getPort() + "\n" + obj.getQuery());

        HttpsURLConnection conn = null;
        try {
            conn = (HttpsURLConnection) obj.openConnection();
            //conn = (HttpsURLConnection) obj.openConnection(proxy);

            conn.setRequestMethod("GET");
        } catch (ProtocolException ex) {
            System.out.println("qui 3.." + ex.toString());
        }

        //conn.setUseCaches(false);
        // act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        conn.setRequestProperty("Accept-Language", "it-IT,en-US;q=0.it-IT,it;q=0.9,en-US;q=0.8,en;q=0.77");
        conn.setRequestProperty("Cookie", loadCookies());

        conn.setRequestProperty("Referer", "https://family.axioscloud.it/Secret/REStart.aspx?Customer_ID=80249350580");
        conn.setReadTimeout(10000);
        conn.setInstanceFollowRedirects(false);
        HttpsURLConnection.setFollowRedirects(false);
        //     Log.i(TAG_LOG,"Cookie al sendget 2: " + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";"));

        int responseCode = 0;
        try {
            responseCode = conn.getResponseCode();
            //         Log.i(TAG_LOG,"responseCode.." + responseCode);
        } catch (IOException ex) {
            Log.i(TAG_LOG, "qui 4.." + ex.toString());
        }

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            //         Log.i(TAG_LOG,"Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        List<String> cookiesHeader = headerFields.get("Set-Cookie");
        List<String> locations = headerFields.get("Location");

        if (locations != null) {
            for (String location : locations) {
                //             Log.i(TAG_LOG,"location.." + location);
                if (!location.contains("family.axioscloud.it")) {
                    location = "https://family.axioscloud.it" + location;
                }
                urlRedirect = location;
                //            Log.i(TAG_LOG,"urlRedirect.." + urlRedirect);

            }
        }
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                //           Log.i(TAG_LOG,"cookie.." + HttpCookie.parse(cookie).get(0) + "\n");

                mCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
//            System.out.println("sessionid.." + sessionid + "\n");
            //       Log.i(TAG_LOG,"CookieStore..\n" + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";"));
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


        return response.toString();

    }


    public String creaquery(String unaquery) throws UnsupportedEncodingException {
        StringBuilder query = new StringBuilder();
        String querys[] = unaquery.split("&");
        for (String query_ : querys) {
            //        Log.i(TAG_LOG,query_);
            String keys_values[] = query_.split("=");
            //         Log.i(TAG_LOG,keys_values[0] + "..." + keys_values[1]);

            if (keys_values[1].contains(" ")) {
                query.append("&" + keys_values[0] + "=" + URLEncoder.encode(keys_values[1], "UTF-8"));
            } else {
                query.append("&" + keys_values[0] + "=" + keys_values[1]);
            }
        }
        String res = query.toString().substring(1, query.toString().length());
        //      Log.i(TAG_LOG,res);
        return res;
    }

    public ArrayList<Voto> estraiVoti(String html, String alunno, String quadr) {

        Document doc = Jsoup.parse(html);
        Element divtab = doc.body().getElementById("votiEle");
        Elements tabelle = divtab.getElementsByTag("table");
        Element tabella = tabelle.get(0);
        Element tabella_body = tabelle.get(0).getElementsByTag("tbody").get(0);
        Elements trs = tabella_body.getElementsByTag("tr");
        Log.i(TAG_LOG, " elemts login form= " + trs.size());
        //    Log.i(TAG_LOG," elemts login form= " + tabella.text());

        for (int i = 0; i <= trs.size() - 1; i++) {
            Log.i(TAG_LOG, trs.get(i).text());

            Elements tds = trs.get(i).getElementsByTag("td");

            if (tds.get(0).text().contains("/")) {
                String voto_attr = tds.get(3).attr("title");
                String voto_orig = "";
                try {
                    voto_orig = voto_attr.substring(voto_attr.length() - 5, voto_attr.length());
                } catch (Exception e) {
                    Log.i(TAG_LOG, e.toString());
                }
                if (isDoubleOrInt(voto_orig)) {
                    Voto voto = new Voto();
                    voto.setAlunno(alunno);
                    voto.setQuadrim(quadr);
                    voto.setData(new ConvertiData().da_String_a_Millis(tds.get(0).text()));
                    voto.setMateria(tds.get(1).text());
                    voto.setTipo(tds.get(2).text());

                    Log.i(TAG_LOG, "voto_orig.." + voto_orig);
                    voto.setVoto(voto_orig);
                    voto.setCommento(tds.get(4).text());
                    voto.setDocente(tds.get(5).text());
                    voti.add(voto);
                }
                // System.out.println(voto.getData() + " ..... "+voto.getVoto()+" ..... "  + alunno);
                Log.i(TAG_LOG, "Voti.size.... " + voti.size());
                //textArea.append(voto.getData()+"....."+alunno+"\n");
            }

        }

        return voti;
    }

    public ArrayList<Compito> estraiCompiti(String html, String alunno) {

        Document doc = Jsoup.parse(html);
        Element divtab = doc.body().getElementById("content-comunicazioni");
        Elements tabelle = divtab.getElementsByTag("table");
        Element tabella = tabelle.get(0);
        Element tabella_body = tabelle.get(0).getElementsByTag("tbody").get(0);
        Elements trs = tabella_body.getElementsByTag("tr");
        Log.i(TAG_LOG, " elemts login form= " + trs.size());
        Log.i(TAG_LOG, " elemts login form= " + tabella.text());

        for (int i = 0; i <= trs.size() - 1; i++) {
            // System.out.println(trs.get(i).text());

            Elements tds = trs.get(i).getElementsByTag("td");

            if (tds.get(0).text().contains("/")) {
                Compito rigacompiti = new Compito();
                rigacompiti.setAlunno(alunno);
                rigacompiti.setData(tds.get(0).text());
                rigacompiti.setArgoemnto(tds.get(1).html().replaceAll("<b>", "").replaceAll("</b>", "\n").replaceAll("<br>", "\n"));
                rigacompiti.setCompiti(tds.get(2).html().replaceAll("<b>", "").replaceAll("</b>", "\n").replaceAll("<br>", "\n"));
                rigacompiti.setAssenze(tds.get(3).text());
                rigacompiti.setNoteProf(tds.get(4).text());
                rigacompiti.setNoteDiscip(tds.get(5).text());
                compiti.add(rigacompiti);

                Log.i(TAG_LOG, "compiti.size.... " + compiti.size());
                //textArea.append(voto.getData()+"....."+alunno+"\n");
            }

        }

        return compiti;
    }

    public ArrayList<Assenza> estraiAssenze(String html, String alunno) {

        Document doc = Jsoup.parse(html);
        Element divtab = doc.body().getElementById("content-comunicazioni");
        Elements tabelle = divtab.getElementsByTag("table");
        Element tabella = tabelle.get(0);
        Element tabella_body = tabelle.get(0).getElementsByTag("tbody").get(0);
        Elements trs = tabella_body.getElementsByTag("tr");
        Log.i(TAG_LOG, " elemts login form= " + trs.size());
        Log.i(TAG_LOG, " elemts login form= " + tabella.text());

        for (int i = 0; i <= trs.size() - 1; i++) {
            // System.out.println(trs.get(i).text());

            Elements tds = trs.get(i).getElementsByTag("td");

            if (tds.get(0).text().contains("/")) {
                Assenza rigaAssenza = new Assenza();
                rigaAssenza.setAlunno(alunno);
                rigaAssenza.setData(new ConvertiData().da_String_a_Millis(tds.get(0).text()));
                rigaAssenza.setTipoAssenza(tds.get(1).text());
                rigaAssenza.setGiustificazione(tds.get(2).text());

                assenze.add(rigaAssenza);

                Log.i(TAG_LOG, "assense.size.... " + assenze.size());
                //textArea.append(voto.getData()+"....."+alunno+"\n");
            }

        }

        return assenze;
    }


    public ArrayList<Comunicazione> estraiComunicazioni(String html, String alunno) {

        Document doc = Jsoup.parse(html);
        Element divtab = doc.body().getElementById("content-comunicazioni");
        Elements tabelle = divtab.getElementsByTag("table");
        Element tabella = tabelle.get(0);
        Element tabella_body = tabelle.get(0).getElementsByTag("tbody").get(0);
        Elements trs = tabella_body.getElementsByTag("tr");
        Log.i(TAG_LOG, " elemts login form= " + trs.size());
        Log.i(TAG_LOG, " elemts login form= " + tabella.text());

        for (int i = 0; i <= trs.size() - 1; i++) {
            // System.out.println(trs.get(i).text());

            Elements tds = trs.get(i).getElementsByTag("td");

            if (tds.get(0).text().contains("/")) {
                Comunicazione comunicazione = new Comunicazione();
                comunicazione.setAlunno(alunno);
                comunicazione.setData(new ConvertiData().da_String_a_Millis(tds.get(0).text()));
                comunicazione.setTesto(tds.get(2).text());

                comunicazioni.add(comunicazione);

                Log.i(TAG_LOG, "comunicazioni.size.... " + comunicazioni.size());
                //textArea.append(voto.getData()+"....."+alunno+"\n");
            }

        }

        return comunicazioni;
    }

    public String estraiClasse(String html) {
        String classe = "";

        try {
            Document doc = Jsoup.parse(html);
            Element tabella = doc.body().getElementById("curTab");

            Element tabella_body = tabella.getElementsByTag("tbody").get(0);
            Elements trs = tabella_body.getElementsByTag("tr");
            Log.i(TAG_LOG, " elemts login form= " + trs.size());
            Log.i(TAG_LOG, " elemts login form= " + tabella.text());

            // for (int i = 0; i <= trs.size() - 1; i++) {
            // System.out.println(trs.get(i).text());

            Elements tds = trs.get(0).getElementsByTag("td");

            if (tds.get(0).text().contains("/")) {

                classe = tds.get(3).text() + " " + tds.get(4).text();

                Log.i(TAG_LOG, "classe e sez.... " + classe);

            }

            // }
        } catch (Exception e) {
            Log.i(TAG_LOG, e.toString());
        }
        return classe;
    }


    public ArrayList<Assenza> getAssenze() {
        return assenze;
    }

    public void setAssenze(ArrayList<Assenza> assenze) {
        this.assenze = assenze;
    }

    public ArrayList<Compito> getCompiti() {
        return compiti;
    }

    public void setCompiti(ArrayList<Compito> compiti) {
        this.compiti = compiti;
    }

    public ArrayList<Voto> getVoti() {
        return voti;
    }

    public void setVoti(ArrayList<Voto> voti) {
        this.voti = voti;
    }

    public ArrayList<Comunicazione> getComunicazioni() {
        return comunicazioni;
    }

    public void setComunicazioni(ArrayList<Comunicazione> comunicazioni) {
        this.comunicazioni = comunicazioni;
    }

    public String getCod_alunno() {
        return cod_alunno;
    }

    public void setCod_alunno(String cod_alunno) {
        this.cod_alunno = cod_alunno;
    }

    public String getClasse_sez() {
        return classe_sez;
    }

    public void setClasse_sez(String classe_sez) {
        this.classe_sez = classe_sez;
    }

    public boolean isDoubleOrInt(String num) {
        boolean result = false;

        try {
            if (num.contains(",")) {
                num = num.replaceAll(",", ".");
            }
            if (num.contains("+")) {
                num = num.replaceAll("\\+", " ").trim();

            }
            if (num.contains("-") || num.contains("–")) {
                num = num.replaceAll("\\-", " ").trim();
                num = num.replaceAll("–", " ").trim();
            }
            Log.i(TAG_LOG, "numero dopo i controlli..  " + num);
        } catch (Exception e) {
            Log.i(TAG_LOG, "errore in isDoubleOrInt..  " + e.toString());
        }
        try {
            Integer.parseInt(num);
            result = true;
        } catch (Exception e) {
            try {
                Double.parseDouble(num);
                result = true;
            } catch (Exception ex) {
                result = false;
                System.out.println(ex.toString());
            }
        }
        Log.i(TAG_LOG, "numero da valutarre..  " + num + " vero/falso " + result);
        return result;
    }

}