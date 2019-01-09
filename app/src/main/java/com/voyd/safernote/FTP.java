package com.voyd.safernote;

import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Project name : DurianFtp
 * Created by zhibao.liu on 2016/1/12.
 * Time : 9:24
 * Email warden_sprite@foxmail.com
 * Action : durian
 */
public class FTP {

    private final static String TAG="FTP";

    public static String ftpUpload(String url, String port, String username, String password, String remotePath, String filenamepath, String filename) {

        FTPClient ftpClient = new FTPClient();
        FileInputStream fis=null;
        String retMessage="0";

        try {
            ftpClient.connect(url,Integer.parseInt(port));
            boolean loginResult=ftpClient.login(username,password);
            int retCode=ftpClient.getReplyCode();
            if(loginResult && FTPReply.isPositiveCompletion(retCode)){
                ftpClient.makeDirectory(remotePath);
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.enterLocalPassiveMode();
                fis=new FileInputStream(filenamepath+filename);
                ftpClient.storeFile(filename,fis);

                retMessage="1";

                Log.i(TAG,"retMessage : 1");

            }else{
                retMessage="0";
                Log.i(TAG,"retMessage : 0");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retMessage;

    }

    public static boolean downLoadFromFTP(String url, String port, String username, String password, String remotePath,String localpath){

        FTPClient ftpClient=new FTPClient();
        InputStream is=null;
        FileOutputStream io=null;
        File file=new File(localpath);

        byte[] buffer=new byte[1024];

        int currentlength=0;
        int len=0;

        try {

            ftpClient.connect(url,Integer.parseInt(port));
            boolean loginResult=ftpClient.login(username,password);
            int retCode=ftpClient.getReplyCode();

            //following programe is very important
            //setting it is neccessary
            ftpClient.enterLocalPassiveMode();

            if(!FTPReply.isPositiveCompletion(retCode)) {
                ftpClient.disconnect();
                Log.i(TAG,"FTP server refused connection.");
                System.exit(1);
            }

            if(loginResult && FTPReply.isPositiveCompletion(retCode)){

                is=ftpClient.retrieveFileStream(remotePath);
                io=new FileOutputStream(file,false);

                while((len=is.read(buffer))!=-1){

                    io.write(buffer,0,len);
                    currentlength=currentlength+len;
                    Log.i(TAG,"download "+currentlength+" byte");

                }

                is.close();
                io.close();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}
