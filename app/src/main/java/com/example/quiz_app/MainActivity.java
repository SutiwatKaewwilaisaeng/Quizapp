package com.example.quiz_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quiz_app.BroadcastReceiver.AlarmReceiver;
import com.example.quiz_app.Common.Common;
import com.example.quiz_app.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{
    MaterialEditText edtNewUser,edtNewPassword,edtNewEmail;
    MaterialEditText edtUser,edtPassword;
    Button btnSignup, btnSignIn;
    FirebaseDatabase database;
    DatabaseReference users;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registerAlarm();

        //Firebase

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        edtUser = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);

        btnSignIn = findViewById(R.id.btn_sign_in);
        btnSignup = findViewById(R.id.btn_sign_up);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showSignUpDialog();

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                 signIn(edtUser.getText().toString(),edtPassword.getText().toString());
            }
        });
    }
    //daily Notification & AlarmReceiver
    private void registerAlarm()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,23); //23 hour
        calendar.set(Calendar.MINUTE,21); //
        calendar.set(Calendar.SECOND,0);

        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,intent,PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

    }

    private void signIn(final String user, final String pwd)
    {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(user).exists()) {
                    if (!user.isEmpty()) {
                        User login = dataSnapshot.child(user).getValue(User.class);
                        if (login.getPassword().equals(pwd))
                        {
                            Intent homeActivity = new Intent(MainActivity.this,HomeActivity.class);
                            Common.currentUser1 = login;
                            startActivity(homeActivity);
                            finish();

                        }
                        else
                            Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(MainActivity.this, "Please enter your user name", Toast.LENGTH_SHORT).show();
                    }
                }
                    else
                    {
                        Toast.makeText(MainActivity.this,"User is nor exists !",Toast.LENGTH_SHORT).show();
                    }

                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }

    private void showSignUpDialog()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Sign Up");
        alertDialog.setMessage("Please fill full information");
        LayoutInflater inflater = this.getLayoutInflater();
        View sign_up_layout = inflater.inflate(R.layout.sign_up_layout,null);

        edtNewUser = sign_up_layout.findViewById(R.id.edtNewUserName);
        edtNewEmail = sign_up_layout.findViewById(R.id.edtNewEmail);
        edtNewPassword = sign_up_layout.findViewById(R.id.edtNewPassword);

        alertDialog.setView(sign_up_layout);
        alertDialog.setIcon(R.drawable.ic_account);
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override

            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final User user = new User(edtNewUser.getText().toString(),
                        edtNewPassword.getText().toString()
                        , edtNewEmail.getText().toString());
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getUserName()).exists())
                            Toast.makeText(MainActivity.this, "User already exitsts !", Toast.LENGTH_SHORT).show();
                        else {
                            users.child(user.getUserName())
                                    .setValue(user);
                            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.dismiss();

            }


        });
        alertDialog.show();
    }
}
