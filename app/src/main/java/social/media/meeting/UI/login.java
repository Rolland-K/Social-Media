package social.media.meeting.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import social.media.meeting.R;
import social.media.meeting.MainActivity;
import social.media.meeting.Util.Global;

public class login extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private ProgressBar loading;
    public static Integer INVALIDEMAIL = 1;
    private Integer INVLAIDPASS = 2;
    private Integer EMPTYEMAIL = 3;
    private Integer EMPTYPASS = 4;
    private Integer VALID = 0;
    private Button login, register;
    private String TAG = "Login";
    private CheckBox remember_me;

    String MY_PREFS_NAME = "user_auth_info";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        verify();
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        loading = (ProgressBar)findViewById(R.id.loading);
        ProgressBar loadingProgressBar = findViewById(R.id.loading);
        remember_me = (CheckBox)findViewById(R.id.cbx_remember);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(validate()){
                    case 1:
                        Toast.makeText(login.this,"Please input Valid Email Address!",Toast.LENGTH_LONG).show();
                        usernameEditText.setText("");
                        break;
                    case 2:
                        Toast.makeText(login.this,"Please input Valid Password!",Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(login.this,"Please input Email Address",Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(login.this,"Please input Password",Toast.LENGTH_LONG).show();
                        break;
                    case 0:
                        loading.setVisibility(View.VISIBLE);
                        login();
                        break;
                    default:
                        break;
                }

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, register.class);
                startActivity(intent);
            }
        });

    }

    private void verify() {

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String email = prefs.getString("email", "dummy user");//"No name defined" is the default value.
        String verify = prefs.getString("verify", "FALSE");
        if (!email.equals("dummy user") && verify.equals("TRUE")) {
            Global.current_user_email = email;
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void login() {
        String username = String.valueOf(usernameEditText.getText());
        String password = String.valueOf(passwordEditText.getText());
//        Intent intent = new Intent(login.this, shop.carate.shopper.MainActivity.class);
//        startActivity(intent);
        FirebaseApp.initializeApp(getApplicationContext());
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loading.setVisibility(View.INVISIBLE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Global.current_user_email = user.getEmail();
                            Intent intent = new Intent(login.this, MainActivity.class);
                            startActivity(intent);

                            if (remember_me.isChecked()) {
                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("email", Global.current_user_email);
                                editor.putString("verify", "TRUE");
                                editor.apply();
                            }
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.
                            loading.setVisibility(View.INVISIBLE);
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

//        mAuth.signInWithEmailAndPassword(username, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            loading.setVisibility(View.INVISIBLE);
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Global.current_user_email = user.getEmail();
//                            Intent intent = new Intent(login.this, shop.carate.shopper.MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(login.this,"Login Failed, please try again", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });



    }

    private Integer validate() {
        String username = String.valueOf(usernameEditText.getText());
        String password = String.valueOf(passwordEditText.getText());
        if(username.length()==0){
            return EMPTYEMAIL;
        }
        if(password.length()==0){
            return EMPTYPASS;
        }
        if(!username.contains("@")){
            return INVALIDEMAIL;
        }
        if(password.length()<6){
            return INVLAIDPASS;
        }
        return VALID;
    }

}
