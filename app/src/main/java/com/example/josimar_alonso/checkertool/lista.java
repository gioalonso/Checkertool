package com.example.josimar_alonso.checkertool;

/**
 * Created by josimar_alonso on 10/30/2017.
 */

import android.os.Environment;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.example.josimar_alonso.checkertool.MainActivity.isExternalStorageAvailable;
import static com.example.josimar_alonso.checkertool.MainActivity.isExternalStorageReadOnly;

/**
 * Created by Giovanni_Alonso on 8/02/17.
 */

//function to read invited list data from a xls document (invitados.xls) previously saved in the device downloads directory
public class lista {

    private static final Map<String,String> listado;
    static {
        listado = new HashMap<String, String>();

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            System.out.println("Storage not available or read only");
            //return;
        }

        try {
            // Creating Input Stream
            //File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/invitados.xls");

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
            for (int i=0; i<single.length;i++){
                listado.put(single[i], single[i+1]);
                i++;
            }


        }catch (Exception e){e.printStackTrace(); }
    }

    String check(String ID){
        return listado.get(ID);
    }


}
