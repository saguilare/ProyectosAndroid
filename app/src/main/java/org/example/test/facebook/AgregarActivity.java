package org.example.test.facebook;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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


import org.example.test.facebook.Models.HabitacionDB;
import org.example.test.facebook.Pojos.Habitacion;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AgregarActivity extends AppCompatActivity {


    int PHOTO_PICKER_REQUEST = 2;


    Button btnCargarImagen;

    Bitmap bitmap;

    Uri targetUri;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;


    String archivo; //Nombre de la imagen que va a ser almacenada en firebase
    String cod;
    String casa;
    String descrip;

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<HabitacionDB> mHabitacionDBTable;

    //Web services
    Gson gson = new Gson();
    private StringBuilder sb = new StringBuilder();
    private String method= "GET";

    private List<Habitacion> TiposHabitacion;

    //Declara Spinner
    Spinner mySipinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);

        //Crea spinner
        mySipinner = (Spinner)findViewById(R.id.spinner2);

        GetTiposHabitacion();

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://AndroidAppDB.azurewebsites.net",
                    this).withFilter(new AgregarActivity.ProgressFilter());

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

        mHabitacionDBTable = mClient.getTable("Habitaciones", HabitacionDB.class);

        btnCargarImagen = (Button) findViewById(R.id.btnAgregarFoto);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            FirebaseUser fbuser = mAuth.getCurrentUser();
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    /* @Override
   public void onStart() {
        super.onStart();

        GetTiposHabitacion();

    }*/

    public void GetTiposHabitacion() {
        try {
            new AgregarActivity.GetTiposHabitacionesService().execute("habitacion").get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private class GetTiposHabitacionesService extends AsyncTask<String, Void,String> {
        @Override
        protected String doInBackground(String... losGet) {
            final String baseurl = "http://35.231.149.112/iswservice/api/habitacion";

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
            TiposHabitacion = gson.fromJson(sb, new TypeToken<List<Habitacion>>(){}.getType());

            ArrayAdapter<Habitacion> adapter = new ArrayAdapter<Habitacion>( AgregarActivity.this,
                    android.R.layout.simple_spinner_item, TiposHabitacion);
            //((TextView)findViewById(R.id.tvCodigoH)).setText(TiposHabitacion.get(0).getDescripcion());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mySipinner.setAdapter(adapter);
        }
    }

    public void EditarHabitacion(HabitacionDB habitacion) {
        //esteban pasar la habitacion que se v a editar
        //Intent intent = new Intent(this,HabitacionActivity.class);
        //startActivity(intent);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param exception The exception to show in the dialog
     * @param title     The dialog title
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
     * @param exception The exception to show in the dialog
     * @param title     The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if (exception.getCause() != null) {
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message The dialog message
     * @param title   The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     *
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

    public void addItemHabitacion() {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final HabitacionDB item = new HabitacionDB();
        this.setVariables();
        item.setCodigo(cod);
        item.setCasa(casa);
        item.setDescripcion(descrip);
        item.setImagen(archivo);
        item.setComplete(false);


        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final HabitacionDB entity = addHabitacionItemInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!entity.isComplete()) {
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
     * @param item The item to Add
     */
    public HabitacionDB addHabitacionItemInTable(HabitacionDB item) throws ExecutionException, InterruptedException {
        HabitacionDB entity = mHabitacionDBTable.insert(item).get();
        return entity;
    }

    /*Seleccionar y guardar foto*/

    public void seleccionarFoto(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PHOTO_PICKER_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int codigoRequest, int codigoResult, Intent data) {
        if (codigoRequest == PHOTO_PICKER_REQUEST) {
            targetUri = data.getData();
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                ImageView ivFoto = (ImageView) findViewById(R.id.ivFoto);
                ivFoto.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void setVariables() {

            //Variables a insertar
             cod = ((EditText) findViewById(R.id.etCodigoH)).getText().toString();
             casa = ((EditText) findViewById(R.id.etCasaH)).getText().toString();
             descrip = ((EditText) findViewById(R.id.etDescrpH)).getText().toString();
    }

    //Inserta en BD y guarda imagen en firebase
    public void insertar (View view){
            try {

                archivo = ((EditText) findViewById(R.id.etFoto)).getText().toString();

                addItemHabitacion();


                StorageReference photoref = mStorageRef.child("imagenes/" + archivo);
                UploadTask uploadTask = photoref.putFile(targetUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(AgregarActivity.this, "Upload complete.",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(AgregarActivity.this, "Upload failed.",
                                Toast.LENGTH_SHORT).show();
                    }

                });
            } catch (Exception e) {
                Toast.makeText(AgregarActivity.this, "Error: Su accion no se ejecuto", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        public void RegresarMainNav (View view){
            Intent intent = new Intent(this, NavDrawerActivity.class);
            startActivity(intent);
            finish();
        }

}


