package com.e.smarthome;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

Button test;
EditText name;
String a;
String b;
Button test2;
Button bio;
Button setTimeOff;
Button setTimeOn;
Button lightLevel;
Button modeLight;
Button infoSensor;
Button changePass;
Button manageCard;
String c;
Button forgotPass;
boolean isTrue = false;
ProgressDialog progressDialog;
ProgressDialog progressDialog1;
ProgressBar bar;
private Executor executor;
int hours;
int minutes;
String recentLevel;
int second;
String auto;
boolean notloop;
BiometricPrompt biometricPrompt;
BiometricPrompt biometricPrompt1;
BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout relativeLayout = findViewById(R.id.rl_layout);
        test = findViewById(R.id.btn_test);
        test2 = findViewById(R.id.btn_test2);
        bio = findViewById(R.id.btn_bio);
        setTimeOff = findViewById(R.id.btn_timeoff);
        setTimeOn = findViewById(R.id.btn_timeon);
        lightLevel = findViewById(R.id.btn_lightLevel);
        modeLight = findViewById(R.id.btn_modeLight);
        changePass = findViewById(R.id.btn_changepass);
        forgotPass = findViewById(R.id.btn_forgot);
        executor = ContextCompat.getMainExecutor(this);
        infoSensor = findViewById(R.id.btn_infoSensor);
        FirebaseDatabase databasemain = FirebaseDatabase.getInstance();
        DatabaseReference myRefmain = databasemain.getReference("goHome");
        myRefmain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Snackbar.make(relativeLayout,String.valueOf(snapshot.getValue(String.class)) + " đã vào nhà" , Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        infoSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean con = isConnect(MainActivity.this);
                if (con) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(MainActivity.this);
                    mydialog.setTitle("Thông số quang trở");
                    TextView info = new TextView(MainActivity.this);
                    info.setGravity(Gravity.CENTER_HORIZONTAL);
                    info.setTextSize(45);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef1 = database.getReference("obj/IsAuto/lux");
                    myRef1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            info.setText(String.valueOf(snapshot.getValue(Double.class)));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    mydialog.setView(info);
                    mydialog.show();
                }
                else
                {
                    Toast.makeText(v.getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        modeLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean con = isConnect(MainActivity.this);
                if (con) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(MainActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.selmode, null);
                    mydialog.setTitle("Chọn chế độ");
                    Button auto = mView.findViewById(R.id.btn_auto);
                    Button notAuto = mView.findViewById(R.id.btn_notauto);
                    auto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("obj/IsAuto/Auto");
                            myRef.setValue("1");
                            Toast.makeText(v.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                            notloop =false;
                        }
                    });
                    notAuto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("obj/IsAuto/Auto");
                            myRef.setValue("0");
                            Toast.makeText(v.getContext(), "Thành công", Toast.LENGTH_SHORT).show();
                            notloop = false;
                        }
                    });
                    mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    mydialog.setView(mView);
                    mydialog.show();
                }
                else
                {
                    Toast.makeText(v.getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        lightLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean con = isConnect(MainActivity.this);
                if (con)
                {
                    notloop = true;
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef1 = database.getReference("obj/IsAuto/Auto");
                    myRef1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            auto = snapshot.getValue(String.class);
                            if (auto.equals("1"))
                            {
                                if (notloop) {
                                    Toast.makeText(v.getContext(), "Đang ở chế độ tự động, hãy chỉnh qua thủ công", Toast.LENGTH_SHORT).show();
                                    notloop = false;
                                }
                            }
                            else
                            {
                                if (notloop) {
                                    AlertDialog.Builder mydialog = new AlertDialog.Builder(MainActivity.this);
                                    mydialog.setTitle("Điều chỉnh độ sáng");
                                    mydialog.setMessage("Mức độ sáng:");
                                    View mview = getLayoutInflater().inflate(R.layout.sellevel, null);
                                    SeekBar seekBar = mview.findViewById(R.id.seekbar);
                                    FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef3 = database2.getReference("obj/IsAuto/mode");
                                    myRef3.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            recentLevel = snapshot.getValue(String.class);
                                            seekBar.setProgress(Integer.parseInt(recentLevel)/2);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                        @Override
                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference myRef = database.getReference("obj/IsAuto/mode");
                                            if (progress==0)
                                            {
                                                myRef.setValue("0");
                                            }
                                            if (progress==1)
                                            {
                                                myRef.setValue("2");
                                            }
                                            if (progress==2)
                                            {
                                                myRef.setValue("4");
                                            }
                                            if (progress==3)
                                            {
                                                myRef.setValue("6");
                                            }
                                            if (progress==4)
                                            {
                                                myRef.setValue("8");
                                            }
                                            if (progress==5)
                                            {
                                                myRef.setValue("10");
                                            }
                                        }

                                        @Override
                                        public void onStartTrackingTouch(SeekBar seekBar) {

                                        }

                                        @Override
                                        public void onStopTrackingTouch(SeekBar seekBar) {

                                        }
                                    });
                                    mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    mydialog.setView(mview);
                                    mydialog.show();
                                    notloop = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else
                {
                    Toast.makeText(v.getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setTimeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog();
            }
        });
        setTimeOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialogOn();
            }
        });
        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this,"Lỗi!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef2 = database.getReference("open");
                myRef2.setValue("1");
                DatabaseReference myRef1 = database.getReference("open");
                myRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        a = snapshot.getValue(String.class);
                        //Toast.makeText(v.getContext(), a+"done", Toast.LENGTH_SHORT).show();
                        if (a.equals("1")) {
                            Toast.makeText(MainActivity.this,"Xác thực thành công!", Toast.LENGTH_SHORT).show();
                            progressDialog1.cancel();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                progressDialog1 = new ProgressDialog(MainActivity.this);
                progressDialog1.setMessage("Đang xác thực...");
                progressDialog1.setCancelable(false);
                progressDialog1.show();
                //Toast.makeText(MainActivity.this,"Xác thực thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this,"Sinh trắc học không khớp", Toast.LENGTH_SHORT).show();
            }
        });
        biometricPrompt1 = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this,"Lỗi!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                AlertDialog.Builder mydialog = new AlertDialog.Builder(MainActivity.this);
                mydialog.setTitle("Mật khẩu: ");
                TextView info = new TextView(MainActivity.this);
                info.setGravity(Gravity.CENTER_HORIZONTAL);
                info.setTextSize(45);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef1 = database.getReference("oldPass");
                myRef1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        info.setText(String.valueOf(snapshot.getValue(String.class)));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mydialog.setView(info);
                mydialog.show();

                //Toast.makeText(MainActivity.this,"Xác thực thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this,"Sinh trắc học không khớp", Toast.LENGTH_SHORT).show();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Xác thực sinh trắc học").setSubtitle("Hãy đặt vân tay vào ô tròn").setNegativeButtonText("Cancel").build();
        bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean con = isConnect(v.getContext());
                if (con) {
                    biometricPrompt.authenticate(promptInfo);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean con = isConnect(v.getContext());
                if (con) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("newname");
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Toast.makeText(MainActivity.this, snapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean con = isConnect(v.getContext());
                if (con) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    View mView = getLayoutInflater().inflate(R.layout.info, null);
                    name = mView.findViewById(R.id.ed_name);
                    builder.setMessage("Nhập thông tin");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("newname");
                            myRef.setValue(name.getText().toString());
                            DatabaseReference myRef2 = database.getReference("isAdding");
                            myRef2.setValue("1");
                            DatabaseReference myRef1 = database.getReference("isAdding");
                            myRef1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    a = snapshot.getValue(String.class);
                                    //Toast.makeText(v.getContext(), a+"done", Toast.LENGTH_SHORT).show();
                                    if (a.equals("0")) progressDialog.cancel();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setMessage("Hãy đặt thẻ vào đầu đọc RFID...");
                            progressDialog.setCancelable(false);
                            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    myRef2.setValue("0");
                                    progressDialog.dismiss();//dismiss dialog
                                }
                            });
                            progressDialog.show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setView(mView);
                    builder.show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean con = isConnect(v.getContext());
                if (con) {
                    AlertDialog.Builder mydialog = new AlertDialog.Builder(v.getContext());
                    mydialog.setTitle("Xác nhận mật khẩu cũ");
                    isTrue = false;
                    EditText oldPass = new EditText(v.getContext());
                    oldPass.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    mydialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef1 = database.getReference("oldPass");
                            myRef1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    b = snapshot.getValue(String.class);

                                    //Toast.makeText(v.getContext(), a+"done", Toast.LENGTH_SHORT).show();
                                    if (b.equals(oldPass.getText().toString())) {

                                        AlertDialog.Builder mydialog = new AlertDialog.Builder(MainActivity.this);
                                        mydialog.setTitle("Thay đổi mật khẩu mới.");
                                        View mView = getLayoutInflater().inflate(R.layout.changepass, null);
                                        EditText newPass = mView.findViewById(R.id.ed_new_pass);
                                        EditText confirm = mView.findViewById(R.id.ed_confirm_new_pass);
                                        TextView check = mView.findViewById(R.id.txt_change_check);
                                        newPass.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if (newPass.getText().toString().length() > 6) {
                                                    check.setText("Mật khẩu không được quá 6 số");
                                                    check.setTextColor(Color.parseColor("#fc1105"));
                                                }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {

                                            }
                                        });
                                        confirm.addTextChangedListener(new TextWatcher() {
                                            @Override
                                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                            }

                                            @Override
                                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                if (confirm.getText().toString().equals(newPass.getText().toString())) {
                                                    check.setText("Mật khẩu xác nhận khớp");
                                                    check.setTextColor(Color.parseColor("#1433e0"));
                                                } else {
                                                    check.setText("Mật khẩu xác nhận chưa khớp");
                                                    check.setTextColor(Color.parseColor("#fc1105"));
                                                }
                                            }

                                            @Override
                                            public void afterTextChanged(Editable s) {

                                            }
                                        });
                                        mydialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (newPass.getText().toString().length() > 6) {
                                                    Toast.makeText(MainActivity.this, "Mật khẩu không được quá 6 số", Toast.LENGTH_SHORT).show();
                                                    oldPass.setText("");
                                                    dialog.cancel();
                                                } else {
                                                    if (newPass.getText().toString().equals(confirm.getText().toString())) {
                                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                        DatabaseReference myRef2 = database.getReference("newPass");
                                                        myRef2.setValue(newPass.getText().toString());
                                                        DatabaseReference myRef3 = database.getReference("isChange");
                                                        myRef3.setValue("1");
                                                        DatabaseReference myRef4 = database.getReference("oldPass");
                                                        myRef4.setValue(newPass.getText().toString());
                                                        Toast.makeText(MainActivity.this, "Thay đổi thành công", Toast.LENGTH_SHORT).show();
                                                        b = "";
                                                        oldPass.setText("");
                                                        isTrue = true;
                                                        dialog.cancel();
                                                        //isTrue = false;
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "Mật khẩu xác nhận chưa khớp", Toast.LENGTH_SHORT).show();
                                                        isTrue = true;
                                                        dialog.cancel();
                                                        //isTrue = false;
                                                    }
                                                }
                                            }
                                        });
                                        mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        mydialog.setView(mView);
                                        mydialog.show();
                                    } else {
                                        //isTrue = false;
                                        if (!isTrue) {
                                            Toast.makeText(MainActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                    mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    mydialog.setView(oldPass);
                    mydialog.show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean con = isConnect(v.getContext());
                if (con) {
                    biometricPrompt1.authenticate(promptInfo);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public boolean isConnect (Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (connectivityManager!=null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info!=null)
            {
                for (int i= 0; i< info.length; i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return  false;
    }
    public void showDateTimeDialog()
    {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hours = hourOfDay;
                        minutes = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,hours, minutes);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

                        boolean con = isConnect(MainActivity.this);
                        if (con) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("obj/setAlarmTime");
                            myRef.setValue(simpleDateFormat.format(calendar.getTime())+":00");
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Lỗi mạng.", Toast.LENGTH_SHORT ).show();
                        }
                    }
                }, 12, 0 , false
        );
        timePickerDialog.updateTime(hours, minutes);
        timePickerDialog.show();
    }
    public void showDateTimeDialogOn()
    {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hours = hourOfDay;
                        minutes = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0,0,0,hours, minutes);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

                        boolean con = isConnect(MainActivity.this);
                        if (con) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("obj/setOnTime");
                            myRef.setValue(simpleDateFormat.format(calendar.getTime())+":00");
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Lỗi mạng.", Toast.LENGTH_SHORT ).show();
                        }
                    }
                }, 12, 0 , false
        );
        timePickerDialog.updateTime(hours, minutes);
        timePickerDialog.show();
    }

}