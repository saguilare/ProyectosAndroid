package org.example.test.facebook;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import org.example.test.facebook.Models.CasaDB;
import org.example.test.facebook.Pojos.Casa;
import org.example.test.facebook.Pojos.Precio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AgregarCasaActivity extends AppCompatActivity {


    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<CasaDB> mCasaDBTable;

    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;

    String cod;
    String usuario;
    String descrip;

    //Llenar WS
    //Declara Spinner
    Spinner mySipinner;


    Gson gson = new Gson();
    private StringBuilder sb = new StringBuilder();
    private StringBuilder sbs = new StringBuilder(); //Se usa para sacar los precios por m2
    private String method= "GET";

    private List<Casa> TiposCasas;

    TextView tvCodigo;
    private Precio precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_casa);

        //Crea spinner
        mySipinner = (Spinner)findViewById(R.id.spinner);
        tvCodigo = (TextView) findViewById(R.id.tvCodigo);

        //GetPrecioM2Provincia();

        //Recibe Usuario
        Bundle extras = getIntent().getExtras();
        usuario = extras.getString("emailCasaAct");

        //UUID.randomUUID();
        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://AndroidAppDB.azurewebsites.net",
                    this).withFilter(new AgregarCasaActivity.ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });
        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error Oncreate1");
        }

            // Get the Mobile Service Table instance to use

            mCasaDBTable = mClient.getTable("Casas",CasaDB.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        //obtener lista de tipos casa
        GetTiposCasas();


    }

    public void GetTiposCasas() {
        try{
            new AgregarCasaActivity.GetTiposCasasDromService().execute("casa").get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private class GetTiposCasasDromService extends AsyncTask<String, Void,String> {
        @Override
        protected String doInBackground(String... losGet) {
            final String baseurl = "http://35.231.149.112/iswservice/api/casa";

            HttpURLConnection conn;
            try {
                conn = (HttpURLConnection) new URL(baseurl).openConnection();
                conn.setRequestMethod(method);
                //conn.setDoOutput(true);
                conn.connect();


                int HttpResult =conn.getResponseCode();
                if(HttpResult ==HttpURLConnection.HTTP_OK || HttpResult==HttpURLConnection.HTTP_CREATED){
                    // sb.append(conn.getResponseMessage());
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(),"utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                }
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
            return sb.toString();
        }

        protected void onPostExecute(String sb) {
            TiposCasas = gson.fromJson(sb, new TypeToken<List<Casa>>(){}.getType());

            ArrayAdapter<Casa> adapter = new ArrayAdapter<Casa>( AgregarCasaActivity.this,
                    android.R.layout.simple_spinner_item, TiposCasas);
            //((TextView)findViewById(R.id.tvCodigo)).setText(TiposCasas.get(0).getDescripcion());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mySipinner.setAdapter(adapter);
        }
    }

    /*public void GetPrecioM2Provincia(){
        precio = new Precio();
        precio.setCodigo("1");
        try{
            new AgregarCasaActivity.GetPrecioM2Provincia().execute(precio).get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }*/

    /*private class GetPrecioM2Provincia extends AsyncTask<Precio, Void,String> {
        @Override
        protected String doInBackground(Precio... losGet) {
            final String baseurl = "http://35.231.149.112/iswservice/api";
            String url = baseurl;

            url += "/precio/"+precio.getCodigo();

            HttpURLConnection conn;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod(method);
                //conn.setDoOutput(true);
                conn.connect();


                int HttpResult =conn.getResponseCode();
                if(HttpResult ==HttpURLConnection.HTTP_OK || HttpResult==HttpURLConnection.HTTP_CREATED){
                    // sb.append(conn.getResponseMessage());
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(),"utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sbs.append(line);
                    }
                    br.close();
                }
                conn.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
            return sb.toString();
        }

        protected void onPostExecute(String sbs) {
            precio = gson.fromJson(sbs, Precio.class);
            //((Button)findViewById(R.id.btnSalir)).setText(precio.getDescripcion());
            ((TextView)findViewById(R.id.tvCodigo)).setText(precio.getPrecio());
        }
    }
    */
    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, final String title) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, title);
            }
        });
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }


    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }

    //Llamar este para add casa
    public void addItemCasa() {
        if (mClient == null) {
            return;
        }

        final CasaDB item = new CasaDB();
        setVariables();
        item.setCodigo(cod);
        item.setDescripcion(descrip);
        item.setUsuario(usuario);
        item.setComplete(false);


        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final CasaDB entity = addCasaItemInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!entity.isComplete()){
                                //mAdapter.add(entity);
                            }
                        }
                    });

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error Add Item");
                }
                return null;
            }
        };

        runAsyncTask(task);


        //mTextNewCodigo.setText("");

    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public CasaDB addCasaItemInTable(CasaDB item) throws ExecutionException, InterruptedException {
        CasaDB entity = mCasaDBTable.insert(item).get();
        return entity;
    }

    //Setar varuables
    public void setVariables() {

        //Variables a insertar
        cod = ((EditText) findViewById(R.id.etCodigoC)).getText().toString();
        descrip = ((EditText) findViewById(R.id.etDescripC)).getText().toString();
    }

    public void guardar(View view){
        addItemCasa();
        Toast.makeText(AgregarCasaActivity.this, "Upload complete.",
                Toast.LENGTH_SHORT).show();
    }
}
