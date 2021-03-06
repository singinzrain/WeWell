package edu.neu.madcourse.wewell;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.neu.madcourse.wewell.service.UserService;

import static android.view.View.OnClickListener;

public class SignUpActivity extends AppCompatActivity {
    EditText email;
    EditText password;
    EditText confirmPassword;
    Button signup;
    TextView login;
    ImageView btnBackArrow;
    FirebaseAuth firebaseAuth;
    UserService userService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        confirmPassword = findViewById(R.id.etConfirmPassword);
        signup = findViewById(R.id.btnSignup);
        login = findViewById(R.id.btnLogin);
        btnBackArrow = findViewById(R.id.btnBackArrow);
        firebaseAuth = FirebaseAuth.getInstance();
        userService = new UserService();
        signup.setOnClickListener(new OnClickListener() {
            public void ShowToast(Context context, String info) {
                Toast toast = Toast.makeText(context, Html.fromHtml("<font color=black ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(confirmPassword.getText().toString())) {
                   // Toast.makeText(SignUpActivity.this, "Email Address or Password can't be empty.", Toast.LENGTH_LONG).show();
                    ShowToast(SignUpActivity.this, "Email Address or Password can't be empty.");
                } else if(!password.getText().toString().equals(confirmPassword.getText().toString())) {
                    //Toast.makeText(SignUpActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                   ShowToast(SignUpActivity.this, "Passwords do not match.");
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),
                            password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        firebaseAuth.getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        email.setText("");
                                                        password.setText("");
                                                        //create corresponding userId to Firestore
                                                        userService.saveUser(firebaseAuth.getCurrentUser().getUid());
                                                        Intent intent = new Intent(SignUpActivity.this, EmailVerifyActivty.class);
                                                        startActivity(intent);
                                                    } else {
                                                        Toast.makeText(SignUpActivity.this, task1.getException().getMessage(),
                                                                Toast.LENGTH_LONG).show();
                                                    }

                                                });
                                    } else {
                                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
            });

        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    public void onBackButton(View view) {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
    }
}
