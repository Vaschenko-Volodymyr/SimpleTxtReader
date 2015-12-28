package com.hardtechnologycreator.simplefilemanager;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_PATH = 1;
    private final static int REQUEST_FILE_NAME = 100;
    String absoluteFilePath = "";
    String filesContent = "";
    private static String filename = "";

    TextView tvMain, tvHeader, tvFileLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvMain = (TextView) findViewById(R.id.tvMain);
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        tvFileLocation = (TextView) findViewById(R.id.tvFileLocation);

        File fileName;
        String sdState = android.os.Environment.getExternalStorageState();
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            fileName = new File(sdDir, "SimpleFileManager/");
        } else {
            fileName = getApplicationContext().getCacheDir();
        }
        if (!fileName.exists())
            fileName.mkdirs();

        try {
            File myFile = new File("/sdcard/SimpleFileManager/File.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(tvMain.getText());
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), FileChooser.class);
                startActivityForResult(intent1, REQUEST_PATH);
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if (requestCode == REQUEST_PATH){
            tvMain.setTextColor(getResources().getColor(R.color.black));
            if (resultCode == RESULT_OK) {

                absoluteFilePath = data.getStringExtra("AbsolutePath");
                String path = data.getStringExtra("GetPath");
                String name = data.getStringExtra("GetFileName");
                String fileContent = "";

                tvHeader.setText("File: " + name);
                tvFileLocation.setText("Path: ../" + path);
                tvMain.setText("");
                fileContent += readFileSD(path, name);
                filesContent += fileContent;
                tvMain.setText(filesContent);
            } else if (resultCode == RESULT_CANCELED) {
                tvMain.clearComposingText();
                tvMain.setTextColor(getResources().getColor(R.color.warning));
                tvMain.setText(R.string.declaringOpeningNotTxtFiles);
            }
        } else if (requestCode == REQUEST_FILE_NAME) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    filename = data.getStringExtra("FileName");
                } else Toast.makeText(this, "data is null", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String readFileSD(String path, String name) {
        String result = "";

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return "Error with opening file.";
        }
        File sdPath = new File(path);
        File sdFile = new File(sdPath, name);
        try {
            BufferedReader br = new BufferedReader(new FileReader(sdFile));
            String str;
            while ((str = br.readLine()) != null) {
                result += str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean saveFile(String fileName) {
        try {
            File myFile = new File("/sdcard/SimpleFileManager/" + fileName + ".txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(tvMain.getText());
            myOutWriter.close();
            fOut.close();
            return true;
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void clearTextViewText () {
        tvMain.setTextColor(getResources().getColor(R.color.tvMainColor));
        tvMain.setText(R.string.tvMainText);
        tvFileLocation.setText("");
        tvHeader.setText(getResources().getString(R.string.tvHeaderText));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            clearTextViewText();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
