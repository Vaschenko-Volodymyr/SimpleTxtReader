package com.hardtechnologycreator.simplefilemanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_PATH = 1;
    private final static int REQUEST_FILE_NAME = 2;
    String absoluteFilePath = "";
    String filesContent = "";
    private static String filename = "";

    TextView tv;
    Button btnClear, btnWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv = (TextView) findViewById(R.id.tvMain);

        Toast.makeText(this, String.valueOf(System.currentTimeMillis()).replaceAll("\\W", ""), Toast.LENGTH_LONG).show();

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
        /*
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            //SDcard is available
            File f = new File("/sdcard/SimpleFileManager/test.txt");
            if (!f.exists()) {
                //File does not exists
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), FileChooser.class);
                startActivityForResult(intent1, REQUEST_PATH);
            }
        });

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearTextViewText();
            }
        });

        btnWrite = (Button) findViewById(R.id.btnWriteToFile);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = tv.getText().toString();
                saveFile(text);
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        if (requestCode == REQUEST_PATH){
            tv.setTextColor(getResources().getColor(R.color.black));
            if (resultCode == RESULT_OK) {
                String fileContent = "Current file: SDCard";
                absoluteFilePath = data.getStringExtra("AbsolutePath");
                String path = data.getStringExtra("GetPath");
                String name = data.getStringExtra("GetFileName");
                fileContent += absoluteFilePath;
                fileContent = fileContent  + "\nFile context: " + "\n";
                fileContent += readFileSD(path, name);
                fileContent = fileContent + "\nEnd of file." + "\n\n";
                filesContent += fileContent;
                tv.setText(filesContent);
            } else if (resultCode == RESULT_CANCELED) {
                tv.clearComposingText();
                tv.setTextColor(getResources().getColor(R.color.warning));
                tv.setText(R.string.declaringOpeningNotTxtFiles);
            }
        } else if (requestCode == REQUEST_FILE_NAME) {
            if (resultCode == RESULT_OK) {
                filename = data.getStringExtra("FileName");
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

    private boolean saveFile() {
        String fileName = String.valueOf(System.currentTimeMillis()).replaceAll("\\W", "");
        return saveFile(fileName);
    }

    private boolean saveFile(String fileName) {
        try {
            File myFile = new File("/sdcard/SimpleFileManager/File.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(tv.getText());
            myOutWriter.close();
            fOut.close();
            /*Toast.makeText(getBaseContext(),
                    fileName,
                    Toast.LENGTH_SHORT).show();*/
            return true;
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void clearTextViewText () {
        tv.setTextColor(getResources().getColor(R.color.tvMainColor));
        tv.setText(R.string.tvMainText);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            clearTextViewText();
            return true;
        }
        if (id == R.id.action_save) {
            Intent intent = new Intent(this, SaveFileActivity.class);
            startActivityForResult(intent, REQUEST_FILE_NAME);

            if (filename.equals("")) filename = String.valueOf(System.currentTimeMillis()).replaceAll("\\W", "");
            saveFile(filename);
        }

        return super.onOptionsItemSelected(item);
    }
}
