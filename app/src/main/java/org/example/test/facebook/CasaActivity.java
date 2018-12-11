
package org.example.test.facebook;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.example.test.facebook.Models.CasaDB;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class CasaActivity extends AppCompatActivity  {

    /**
     * Adapter to sync the items list with the view
     */
    private CasaItemAdapter mAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casa);

        // Create an adapter to bind the items with the view
        mAdapter = new CasaItemAdapter(this, R.layout.row_list_casa);
        ListView listViewCasa = (ListView) findViewById(R.id.listViewCasa);
        listViewCasa.setAdapter(mAdapter);

        //UUID.randomUUID();
        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://AndroidAppDB.azurewebsites.net",
                    this).withFilter(new CasaActivity.ProgressFilter());

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

            // Get the Mobile Service Table instance to use

            mCasaDBTable = mClient.getTable("casas",CasaDB.class);



            //Init local storage
            //initLocalStore().get();

            //getAllCasasDBItemsFromTableAsync();
            getAllCasasDBItemsPorUsuarioFromTableAsync("steven");


        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error Oncreate1");
        } catch (Exception e){
            createAndShowDialog(e, "Error Oncreate2");
        }
    }


    public void irACasa(CasaDB casa) {
        //esteban pasar la casa para jalar las habitaciones de esa casa
        Intent intent = new Intent(this,HabitacionActivity.class);
        intent.putExtra("casa",casa);
        startActivity(intent);
    }

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




    /**
     *  ************ CASAS **************************
     */




    /**
     * Refresh the list with the items in the Table
     */
    private void getAllCasasDBItemsFromTableAsync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<CasaDB> results = getAllCasasDB();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //mAdapter.clear();
                            if(results != null) {
                                //((TextView)findViewById(R.id.txtTest)).setText(results.get(2).getDescripcion());

                                for (CasaDB item : results) {
                                    // mAdapter.add(item);
                                }
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error refreshItemsFromTable");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    private void getAllCasasDBItemsPorUsuarioFromTableAsync(final String usuario) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    final List<CasaDB> results = getAllCasasPorUsuarioDB(usuario);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //mAdapter.clear();
                            if(results != null) {
                                //((TextView)findViewById(R.id.txtTest)).setText(results.get(0).getDescripcion());

                                for (CasaDB item : results) {
                                     mAdapter.add(item);
                                }
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error refreshItemsFromTable");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    private List<CasaDB> getAllCasasDBItemsFromTable(){
        List<CasaDB> results = null;
        try {
            results = getAllCasasDB();
        } catch (final Exception e){
            createAndShowDialogFromTask(e, "Error refreshItemsFromTable");
        }
        return results;

    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<CasaDB> getAllCasasDB() throws ExecutionException, InterruptedException {
        return mCasaDBTable.where().field("complete").
                eq(val(false)).execute().get();
    }


    private List<CasaDB> getAllCasasPorUsuarioDB(String usuario) throws ExecutionException, InterruptedException {
        return mCasaDBTable.where().field("usuario").
                eq(usuario).execute().get();
    }


    /**
     * Add a new item
     *
     * @param casa
     *            The view that originated the call
     */
    //Llamar este para add casa
    public void addItemCasa(CasaDB casa) {
        if (mClient == null) {
            return;
        }

        final CasaDB item = casa;


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


    /**
     * Mark an item as completed
     *
     * @param item
     *            The item to mark
     */
    public void deleteCasaItem(final CasaDB item) {
        if (mClient == null) {
            return;
        }

        // Set the item as completed and update it in the table
        item.setComplete(true);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    deleteCasaItemInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*if (item.isComplete()) {
                                mAdapter.remove(item);
                            }*/
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error checkItem");
                }

                return null;
            }
        };

        runAsyncTask(task);


    }

    /**
     * Mark an item as completed in the Mobile Service Table
     *
     * @param item
     *            The item to mark
     */
    public void deleteCasaItemInTable(CasaDB item) throws ExecutionException, InterruptedException {
        mCasaDBTable.update(item).get();
    }


    /**
     * update an item
     *
     * @param item
     *            The item to mark
     */
    public void updateCasaItem(final CasaDB item) {
        if (mClient == null) {
            return;
        }

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    updateCasaItemInTable(item);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*if (item.isComplete()) {
                                mAdapter.remove(item);
                            }*/
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error checkItem");
                }

                return null;
            }
        };

        runAsyncTask(task);



    }

    /**
     * Mark an item as updated in the Mobile Service Table
     *
     * @param item
     *            The item to mark
     */
    public void updateCasaItemInTable(CasaDB item) throws ExecutionException, InterruptedException {
        mCasaDBTable.update(item).get();
    }


}
