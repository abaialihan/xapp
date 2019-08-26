package com.abai.xapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.abai.xapp.entity.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignIn, buttonRegister;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private MaterialEditText editTextEmail, editTextPassword, editTextName, editTextPhoneNumper;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        buttonSignIn = findViewById(R.id.buttonSingIn);
        buttonRegister = findViewById(R.id.buttonRegister);

        relativeLayout = findViewById(R.id.activityMain);

        auth = FirebaseAuth.getInstance();  //запускаем авторизацию в БД
        database = FirebaseDatabase.getInstance();  //подключаемся к БД
        users = database.getReference("users");  //привязываем таблицу к БД

        buttonRegister.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSingIn:

                break;
            case R.id.buttonRegister:
                    openRegisterWindow();
                break;
        }
    }

    private void openRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);  //создаем всплывающее окно
        dialog.setTitle(getString(R.string.alertDialogRegistration));
        dialog.setMessage(getString(R.string.alertDialogEnterAllData));

        LayoutInflater inflater = LayoutInflater.from(this);
        View registerWindow = inflater.inflate(R.layout.registration_window, null);  //получаем всплывающее окно
        dialog.setView(registerWindow);  //делаем видимым всплывающее окно

        editTextEmail = registerWindow.findViewById(R.id.emailField);
        editTextPassword = registerWindow.findViewById(R.id.passwordlField);
        editTextName = registerWindow.findViewById(R.id.nameField);
        editTextPhoneNumper = registerWindow.findViewById(R.id.numberField);

        dialog.setNegativeButton(getString(R.string.negativeBtnCancel), new DialogInterface.OnClickListener() {  //кнопка для отмены регистрации
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton(getString(R.string.buttonRegister), new DialogInterface.OnClickListener() {  //кнопка для продолжения регистрации
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(editTextEmail.getText().toString().isEmpty()){     // проверка всех вводимых данных на пустоту
                    editTextEmail.setError(getString(R.string.emailIsEmpty));
                    return;
                }
                if(editTextPassword.getText().toString().isEmpty()){
                    editTextPassword.setError(getString(R.string.passwordIsEmpty));
                    return;
                }
                if(editTextName.getText().toString().isEmpty()){
                    editTextName.setError(getString(R.string.nameIsEmpty));
                    return;
                }
                if(editTextPhoneNumper.getText().toString().isEmpty()){
                    editTextPhoneNumper.setError(getString(R.string.hintNumberPhone));
                    return;
                }

                userRegistration();
            }
        });

        dialog.show();  // делаем dialog видимым

    }

    private void userRegistration(){   //регистрация  пользователя
        auth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        User user = new User();
                        user.setEmail(editTextEmail.getText().toString());
                        user.setPassword(editTextPassword.getText().toString());
                        user.setName(editTextName.getText().toString());
                        user.setPhoneNumber(editTextPhoneNumper.getText().toString());

                        users.child(user.getEmail()) // ключ идентификации пользователя устанавливаем на email
                                .setValue(user) // в таблицу добовляем ползователя
                                .addOnSuccessListener(new OnSuccessListener<Void>() {  // уведомление при успешной регистрации
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Snackbar.make(relativeLayout, getString(R.string.successRegistration), Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }
}
