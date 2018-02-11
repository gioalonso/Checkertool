package com.example.josimar_alonso.checkertool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.FileInputStream;
import java.util.Iterator;

import static com.example.josimar_alonso.checkertool.MainActivity.isExternalStorageAvailable;
import static com.example.josimar_alonso.checkertool.MainActivity.isExternalStorageReadOnly;

public class asistencia extends AppCompatActivity {

    TextView infonm;
    TextView infodt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);
        infonm = (TextView)findViewById(R.id.infonm);
        infodt = (TextView)findViewById(R.id.infodt);

            if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                System.out.println("Storage not available or read only");
                //return;
            }

            try {
                // Creating Input Stream
                //File file = new File(context.getExternalFilesDir(null), filename);
                FileInputStream myInput = new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/asistencia.xls");

                // Create a POIFSFileSystem object
                //POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

                // Create a workbook using the File System
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myInput);

                // Get the first sheet from workbook
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);

                //list iterator
                Iterator rowIter = mySheet.rowIterator();
                String prueba="";

                while(rowIter.hasNext()){
                    HSSFRow myRow = (HSSFRow) rowIter.next();
                    Iterator cellIter = myRow.cellIterator();
                    while(cellIter.hasNext()){
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        myCell.setCellType(Cell.CELL_TYPE_STRING);
                        prueba=prueba+(myCell.toString())+"#";
                    }
                }
                //split string prueba to individual substrings by # symbol, then all the data is correctly added to listado Hashmap object to be accessible to compare later
                String single[] = prueba.split("#");
                String jon = "";
                String jas = "";
                for (int i=2; i<single.length;i++){
                    String pes=(String.format("%.28s", single[i]));
                    String voi=(single[i+1]);
                    jon= pes+"\n"+jon;
                    jas= voi+"\n"+jas;
                    i++;
                }
                infonm.setText("Nombre:"+"\n\n"+jon);
                infodt.setText("Fecha: "+ "\n\n"+jas);



            }catch (Exception e){e.printStackTrace(); }



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(asistencia.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



}
