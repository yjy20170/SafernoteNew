package com.voyd.safernote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.voyd.safernote.R;


public class activity_testNewDb extends SafeActivity implements OnClickListener{
    private TextView tip;
    private PasswordInputer passwordInputer;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set_password);//只是借用布局，逻辑和activity_setPassword不同
        ((Button)findViewById(R.id.finish)).setOnClickListener(this);
        ((Button)findViewById(R.id.save)).setOnClickListener(this);
        tip = (TextView)findViewById(R.id.titlebarText);
        tip.setText("验证导入数据的密码");
        passwordInputer = (PasswordInputer)findViewById(R.id.password_inputer);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.save:
                String MD5inputString = MD5Util.MD5(passwordInputer.getInput());
                String backupDBPath  = "/" + getString(R.string.app_name)
                        + "/" + getString(R.string.database_name);
                if(MD5inputString.equals(getIntent().getStringExtra("MD5Password"))){
                    final String passwordOfImportDb = passwordInputer.getInput();
                    //弹出对话框，询问是否导出原数据库
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("确定要导入新数据吗？");
                    dialog.setMessage("若选择“继续”，现有的所有数据将会自动导出到" + backupDBPath
                            + "\n同时，密码将变更为导入数据的密码");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity_importExportDb.THIS.exportDB();
                            String  tempDbPath= Environment.getDataDirectory() + "//data//"
                                    + MyApp.context.getString(R.string.package_name)
                                    + "//databases//" + "temp.db";
                            activity_importExportDb.THIS.importDB(tempDbPath,MyApp.context.getString(R.string.database_name));
                            MyApp.password = passwordOfImportDb;
                            activity_testNewDb.this.onBackPressed();//返回importExport
                        }});
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity_testNewDb.this.onBackPressed();
                        }});
                    dialog.show();
                    //TODO 可选择覆盖or合并
                }else{
                    new alert("密码错误！");
                    passwordInputer.reset();
                }
                break;
            case R.id.finish:
                onBackPressed();
        }
    }
}
