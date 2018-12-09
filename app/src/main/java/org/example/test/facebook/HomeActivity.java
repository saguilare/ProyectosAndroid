package org.example.test.facebook;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.example.test.facebook.Pojos.*;

public class HomeActivity extends AppCompatActivity {

    Gson gson = new Gson();
    private Precio precio;

    private StringBuilder sb = new StringBuilder();
    private String method= "GET";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Get();
    }

    public void signOut(View view) {
        if (mAuth != null){
            mAuth.signOut();
            LoginManager.getInstance().logOut();

            // Google sign out
            mGoogleSignInClient.signOut();
        }



        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    public void Get(){
        precio = new Precio();
        precio.setCodigo("1");

        method = "GET";
        new SendSimpleRequest().execute(precio);
    }

    private class SendSimpleRequest extends AsyncTask<Precio, Void,String> {
        @Override
        protected String doInBackground(Precio... losGet) {
            final String baseurl = "http://35.227.65.203/iswservice/api";
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
                        sb.append(line);
                    }
                    br.close();
                }
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
            precio = gson.fromJson(sb, Precio.class);
            ((Button)findViewById(R.id.btnSalir)).setText(precio.getDescripcion());
        }
    }
}
