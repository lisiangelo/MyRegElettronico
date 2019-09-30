package it.android.j940549.myreg_elettronico;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.android.j940549.myreg_elettronico.HttpUrlConnection.CercaScuolaHttpConnection;
import it.android.j940549.myreg_elettronico.argomenti.DataObject_Argomenti;
import it.android.j940549.myreg_elettronico.argomenti.MyRecyclerViewAdapter_ElencoArgomenti;
import it.android.j940549.myreg_elettronico.model.Scuola;
import it.android.j940549.myreg_elettronico.voti.MyRecyclerViewAdapter_ElencoVoti;

public class Cerca_istituto extends AppCompatActivity {
  private RecyclerView mRecyclerView;
  private RecyclerView.Adapter mAdapter;
  private RecyclerView.LayoutManager mLayoutManager;
  private static String LOG_TAG = "Cercaistituto";
  private EditText nome_scuola, cf_scuola,indirizzo;
  private Button cerca;
  ArrayList dataSet = new ArrayList<Scuola>();
  HttpGetTask cercaIstituto;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_cerca_istituto);

    nome_scuola=findViewById(R.id.nome_istituto);
    cf_scuola=findViewById(R.id.cf_istituto);
    indirizzo=findViewById(R.id.indirizzo);

    mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_elenco_scuole);
    mRecyclerView.setHasFixedSize(true);
    mLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLayoutManager);

    cerca=findViewById(R.id.button_cerca);
    cerca.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(findViewById(R.id.button_cerca).getWindowToken(),0);

        String nome=nome_scuola.getText().toString();
        String cf=cf_scuola.getText().toString();
        String indir=indirizzo.getText().toString();
        cerca_ist(nome,cf,indir);

      }
    });

    mAdapter = new MyRecyclerViewAdapter_ElencoScuole(dataSet,this);
    mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment




  }
  public void cerca_ist(String nome,String cf,String indir){
    cercaIstituto= new HttpGetTask(this);

    cercaIstituto.execute(nome,cf,indir);

  }

  private class HttpGetTask extends AsyncTask<String,String,String> {
    private final String USER_AGENT = "Mozilla/5.0 Chrome/73.0.3683.103";
    private String scuola, cfscuola, indirizzo;
    private Activity myActivity;

    String url, host, urlRedirect;
    List<String> cooki;
    private CookieStore cookieStore;
    private CookieManager mCookieManager;
    // private Proxy proxy;

    private HttpGetTask(Activity activity){
      myActivity=activity;
    }
    ProgressDialog progressDialog;
    @Override
    protected void onPreExecute() {

      progressDialog = new ProgressDialog(myActivity);
      progressDialog.setMessage("caricamento dati in corso");
      progressDialog.setCancelable(false);
      progressDialog.setProgress(ProgressDialog.STYLE_SPINNER);
      progressDialog.show();
      super.onPreExecute();

      if (isNetworkConnected() == false) {
        // Cancel request.
        Toast.makeText(myActivity, "non sei connesso ad Internet", Toast.LENGTH_SHORT).show();
        cancel(true);
        return;
      }
    }
    @Override
    protected String doInBackground(String... params) {
      String url = "https://www.sissiweb.it/AXIOS_SWAccessoRE2.aspx";
      scuola=params[0];
      cfscuola=params[1];
      indirizzo=params[2];
      Log.i(LOG_TAG, scuola+"..."+cfscuola+"...."+indirizzo);
      String html = "";
      try {

//prima get per prendere la sessionid
        html = GetPageContent(url);

      } catch (Exception ex) {

        Log.i(LOG_TAG,"qui .." + ex.toString());
      }

      String param = "";
      try {

        param = getFormParams(html, "");
      } catch (
              UnsupportedEncodingException ex) {
        //     Logger.getLogger(BD_Com_Nettuno.class.getName()).log(Level.SEVERE, null, ex);
      }

      //       publish(3);
      String html1 = "";
      try {

        // post per inviare i parametri di ricerca al server
        html1 = sendPost(url, param);

      } catch (Exception ex) {
        Logger.getLogger(CercaScuolaHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
      }


      return html1;


    }

    @Override
    protected void onProgressUpdate(String... values) {

    }

    @Override
    protected void onPostExecute(String html) {
      // aggiorno la textview con il risultato ottenuto
      Log.i(LOG_TAG, "parsing data on postExec "+html.toString());

      try{

        Document doc = Jsoup.parse(html);

        Elements tabelle = doc.getElementsByTag("table");
        Log.i(LOG_TAG, " elemts tables= " + tabelle.size());
        Element tabella = tabelle.get(2);
        Log.i(LOG_TAG, " elemts teabella form= " + tabella.text());
        Element tabella_body = tabella.getElementsByTag("tbody").get(0);
        Elements trs = tabella_body.getElementsByTag("tr");
        Log.i(LOG_TAG, " elemts trs= " + trs.size() + "\n" + trs.text());


        for (int i = 4; i <= trs.size() - 2; i++) {
          Log.i(LOG_TAG, "elemts tr" + trs.get(i).text());

          Elements tds = trs.get(i).getElementsByTag("td");

          Log.i(LOG_TAG, "td.... " + tds.size() + "..dato" + tds.get(0));

          Scuola scuola = new Scuola();

          scuola.setTipo(tds.get(0).text());
          scuola.setNomescuola(tds.get(1).text());
          scuola.setIndirizzo(tds.get(2).text());
          scuola.setComune(tds.get(3).text());
          scuola.setCfscuola(tds.get(4).text());
          dataSet.add(scuola);

          Log.i(LOG_TAG, scuola.getTipo() + " ..... " + scuola.getNomescuola() + " ..... " + scuola.getCfscuola());
          Log.i(LOG_TAG, "Scuole.size.... " + dataSet.size());

        }



        Log.i(LOG_TAG,  "results... " + dataSet.size());

        mAdapter = new MyRecyclerViewAdapter_ElencoScuole(dataSet,myActivity);
        mRecyclerView.setAdapter(mAdapter);

      }

      catch(Exception e){
        Log.e("log_tag", "Error parsing data "+e.toString());
        dataSet.clear();
        mAdapter = new MyRecyclerViewAdapter_ElencoScuole(dataSet,myActivity);
        mRecyclerView.setAdapter(mAdapter);

      }
      progressDialog.dismiss();

    }


    public String GetPageContent(String url) throws IOException {

      // abilita cooki menager
      mCookieManager = new CookieManager();

      URL obj = null;
      try {
        obj = new URL(url);

        host = obj.getProtocol() + "://" + obj.getHost();
        Log.i(LOG_TAG, "url: " + obj.getProtocol() + "\n" + obj.getHost() + "\n" + obj.getPath() + "\n" + obj.getPort() + "\n" + obj.getQuery());
        Log.i(LOG_TAG, "hosturl: " + host);

      } catch (MalformedURLException ex) {
        Log.i(LOG_TAG, "qui 1.." + ex.toString());
      }
      HttpURLConnection conn = null;
      try {
        conn = (HttpURLConnection) obj.openConnection();
        //conn = (HttpURLConnection) obj.openConnection(proxy);

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
        Log.i(LOG_TAG, "qui 3.." + ex.toString());
      }

      int responseCode = 0;
      try {
        responseCode = conn.getResponseCode();
        Log.i(LOG_TAG, "responseCode.." + responseCode);
      } catch (IOException ex) {
        Log.i(LOG_TAG, "qui 4.." + ex.toString());
      }

      Map<String, List<String>> headerFields = conn.getHeaderFields();

      for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
        Log.i(LOG_TAG, "Key = " + entry.getKey() + ", Value = " + entry.getValue());
      }
      List<String> cookiesHeader = headerFields.get("Set-Cookie");
      List<String> locations = headerFields.get("Location");

      if (locations != null) {
        for (String location : locations) {
          Log.i(LOG_TAG, "location.." + location);
          urlRedirect = location;
          Log.i(LOG_TAG, "urlRedirect.." + urlRedirect);

        }
      }
      if (cookiesHeader != null) {
        for (String cookie : cookiesHeader) {
          Log.i(LOG_TAG, "cookie.." + HttpCookie.parse(cookie).get(0) + "\n");

          mCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
        }
        Log.i(LOG_TAG, "CookieStore alla prima get..\n" + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";") + "\n");
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

      Log.i(LOG_TAG, "Extracting form's data...");

      Document doc = Jsoup.parse(html);
      Log.i(LOG_TAG, " elemts = " + doc.getAllElements().size());

      // Google form id
      Elements loginforms = doc.getElementsByTag("form");
      Log.i(LOG_TAG, " elemts login form= " + loginforms.size());
      String keyloginform = "";
      List<String> paramList = new ArrayList<String>();
      //for (Element loginform : loginforms) {
      keyloginform = loginforms.get(0).attr("action");
      Log.i(LOG_TAG, "action: " + keyloginform);
      //if (keyloginform.equals("keyloginform"))
      Elements inputElements = loginforms.get(0).getElementsByTag("input");
      Log.i(LOG_TAG, " elemts inserimento form= " + inputElements.size());
      for (Element inputElement : inputElements) {
        String key = inputElement.attr("name");
        Log.i(LOG_TAG, key);
        String value = inputElement.attr("value");
        Log.i(LOG_TAG, value);
        if (key.equals("txtSearch")) {
          value = scuola;
          Log.i(LOG_TAG, value);
        } else if (key.equals("txtSearchCF")) {
          value = cfscuola;
          Log.i(LOG_TAG, value);
        } else if (key.equals("txtSearchAddress")) {
          value = indirizzo;
          Log.i(LOG_TAG, value);
        }

        paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
      }

      StringBuilder result = new StringBuilder();
      for (String param : paramList) {
        if (result.length() == 0) {
          result.append(param);
        } else {
          Log.i(LOG_TAG, "param partil....\n" + param);
          if (param.equals("=")) {

          } else {
            result.append("&" + param);
          }
        }
      }
      String res = result.toString();
      //System.out.println("params....\n" + result.toString());
      Log.i(LOG_TAG, "params....\n" + res);
      return res;
    }


    public String loadCookies() {
      String cookies = "";
      if (mCookieManager.getCookieStore().getCookies().size() > 0) {
        Log.i(LOG_TAG, "mCookieManager.getCookieStore().getCookies().size()..." + mCookieManager.getCookieStore().getCookies().size());

        cookies = mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";");

      }
      return cookies;
    }

    public String sendPost(String unurl, String unpostParams) throws Exception {

      URL obj = new URL(unurl);
      // HttpURLConnection conn = (HttpURLConnection) obj.openConnection(proxy);
      HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

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
      Log.i(LOG_TAG, "Cookie al primo post : " + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";"));//.split(";")[0]);

      conn.setDoOutput(true);
      conn.setDoInput(true);

      // Send post request
      DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
      wr.writeBytes(unpostParams);
      wr.flush();
      wr.close();

      int responseCode = conn.getResponseCode();
      Log.i(LOG_TAG, "\nSending POST request to URL : " + unurl);
      Log.i(LOG_TAG, "Post parameters : " + unpostParams);
      //System.out.println("Post parameters : " + postParams);
      Log.i(LOG_TAG, "Response Code : " + responseCode);

      //leggo gli header del post
      Map<String, List<String>> headerFields = conn.getHeaderFields();
      for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
        Log.i(LOG_TAG, "Key = " + entry.getKey() + ", Value = " + entry.getValue());
      }
      List<String> cookiesHeader = headerFields.get("Set-Cookie");
      List<String> locations = headerFields.get("Location");

      if (locations != null) {
        for (String location : locations) {
          Log.i(LOG_TAG, "location.." + location);
          urlRedirect = location;
          Log.i(LOG_TAG, "urlRedirect.." + urlRedirect);

          Log.i(LOG_TAG, "url1.." + urlRedirect);

        }
      }
      if (cookiesHeader != null) {
        for (String cookie : cookiesHeader) {
          Log.i(LOG_TAG, "cookie.." + HttpCookie.parse(cookie).get(0) + "\n");

          mCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
        }
        Log.i(LOG_TAG, "CookieStore..\n" + mCookieManager.getCookieStore().getCookies().toString().replace("[", "").replace("]", "").replace(",", ";") + "\n");
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

  }
  protected boolean isNetworkConnected() {
    ConnectivityManager mConnectivityManager = null;
    // Instantiate mConnectivityManager if necessary
    if (mConnectivityManager == null) {
      mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    // Is device connected to the Internet?
    NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
    if (networkInfo != null && networkInfo.isConnected()) {
      return true;
    } else {
      return false;
    }
  }
}
