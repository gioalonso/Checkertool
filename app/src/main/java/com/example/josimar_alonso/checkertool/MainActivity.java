package com.example.josimar_alonso.checkertool;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    EditText txtID;
    TextView lblNombre, lblPLanta;
    ImageView imgPerfil;
    static Context context;
    static String pathi = "";
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    CircleImageView fotoperfil;
    TextView email;
    TextView nombre;
    NavigationView mNavigationView;
    View mHeaderView;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
    String date = sdf.format(new Date());


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        // NavigationView Header
        mHeaderView =  mNavigationView.getHeaderView(0);

        mAuth = FirebaseAuth.getInstance();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        nombre = (TextView)header.findViewById(R.id.nombreUsuario);
        email = (TextView)header.findViewById(R.id.emailUsuario);
        fotoperfil = (CircleImageView) header.findViewById(R.id.imageView);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    setUserData(user);

                }
            }
        };





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        ////////////////////////////////////////ported code//////////////////////////////////////////////

        // TODO Auto-generated method stub
        context = getApplicationContext();
        //reading data from lista class to compare and proceed with further methods
        txtID = (EditText) findViewById(R.id.editTexto);
        lblNombre = (TextView) findViewById(R.id.nombreEmp);
        lblPLanta = (TextView) findViewById(R.id.plantaEmp);
        imgPerfil = (ImageView) findViewById(R.id.imgPerfil);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        //define text fields to empty first
        txtID.setText("");
        lblNombre.setText("");
        lblPLanta.setText("");
        try {
            txtID.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent e) {
                    final InvitadosDB invitadosdb = new InvitadosDB(getApplicationContext());
                    String linea = txtID.getText().toString().toUpperCase();

                    if ((e.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {

                        if (invitadosdb.returndata(linea) != null){
                            String nomemp = invitadosdb.returndata(linea);
                            //Toast.makeText(MainActivity.this, "usuario valido", Toast.LENGTH_SHORT).show();
                            txtID.setText("");
                            lblNombre.setText(""+ nomemp);
                            lblPLanta.setText("Invitado!");
                            imgPerfil.setImageResource(R.drawable.mapsandflags);
                            saveExcelFile("asistencia.xls", nomemp);
                        }else{
                            //Toast.makeText(MainActivity.this, "usuario no valido", Toast.LENGTH_SHORT).show();
                            //in case an uninvited person is scanned a "denied" image will be shown in the app
                            txtID.setText("");
                            lblNombre.setText("Usuario");
                            lblPLanta.setText("No Invitado");
                            imgPerfil.setImageResource(R.drawable.error);
                        }

                        return true;
                    }
                    return false;
                }
            });
        } catch(NullPointerException e){
            e.printStackTrace();
        }


    }

    //function to save scanned people on a excel sheet (asistencia.xls on the downloads directory)
    private boolean saveExcelFile(String fileName, String nombre) {
        //defining function to get the precise time and save it on an specific cell next to the scanned invited name
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String currentDateandTime = sdf.format(new Date());
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            System.out.println("----------------->Storage not available or read only");
            Toast.makeText(MainActivity.this, "almacenamento no disponible"+ fileName, Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean success = false;
        //New Workbook
        Workbook wb=null;
        InputStream inp=null;
        try {
            inp = new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+fileName);
            wb = WorkbookFactory.create(inp);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }

        Cell c = null;
        Row row=null;
        Sheet sheet1 = wb.getSheetAt(0);
        if(sheet1==null) {
            System.out.println("--->sheet era null");
            //sheet1 = wb.createSheet();
        }
        row = sheet1.getRow(0);
        // Generate column headings
        if (row == null) {
            System.out.println("--->row 0 era null");
            row = sheet1.createRow(0);
            c = row.createCell(0);
            c.setCellValue("Nombre:");
            //c.setCellStyle(cs);
            c = row.createCell(1);
            c.setCellValue("Fecha");

        }

        int fila=0;
        row = sheet1.getRow(fila);
        do{
            fila++;
            row = sheet1.getRow(fila);
            if(row==null){
                System.out.println("--->Fila:"+fila);
                break;
            }
            c = row.getCell(0);
            System.out.println("--->Fila:"+fila+"name:"+c.getStringCellValue());

        }while(row!=null);


        System.out.println("--->salio del ciclo con fila:"+fila);
        row = sheet1.createRow(fila);
        c = row.createCell(0);
        c.setCellValue(nombre);
        c = row.createCell(1);
        c.setCellValue(currentDateandTime);
        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+fileName);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            wb.write(os);
            System.out.println("1--------->FileUtils"+ "Writing file" + file);

            success = true;
        } catch (IOException e) {
            System.out.println("2--------->FileUtils"+"Error writing " + file);
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("3--------->FileUtils"+ "Failed to save file");
            e.printStackTrace();
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }

        try {
            os.close();
            inp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    //function to check if device storage is configured as readonly
    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    //function to check if device storage is available
    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    ////////////////////////////////////end ported code//////////////////////////////////////////////

    // if user press the back button drawer closes (on drawer opened)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // 3 dots menu option for devices without menu key brings up settings option with no listener or function
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }
        else if (id == R.id.nav_update) {

            explorar();


        }else if (id == R.id.nav_manage) {
            Intent intent = new Intent(getApplicationContext(), asistencia.class);
            startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(MainActivity.this, about.class));
            finish();

        } else if (id == R.id.nav_send) {

            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, login.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //////////////////////////////user account data to view/////////////////////////////7


    private void setUserData(FirebaseUser user) {
        nombre.setText(user.getDisplayName());
        email.setText(user.getEmail());
        Picasso.with(this).load(user.getPhotoUrl()).fit().into(fotoperfil);
    }


    public void actualizarDatos() {
        final InvitadosDB invitadosdb = new InvitadosDB(getApplicationContext());

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            System.out.println("Storage not available or read only");
            //return;
        }

        try {
            invitadosdb.limpiarData();
            invitadosdb.creaTabla();
            //invitadosdb.updateData();
            // Creating Input Stream
            //File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(pathi);

            // Create a POIFSFileSystem object
            //POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myInput);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            //list iterator
            Iterator rowIter = mySheet.rowIterator();
            String prueba = "";

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    myCell.setCellType(Cell.CELL_TYPE_STRING);
                    prueba = prueba + (myCell.toString()) + "#";
                }
            }
            //split string prueba to individual substrings by # symbol, then all the data is correctly added to listado Hashmap object to be accessible to compare later
            String single[] = prueba.split("#");
            for (int i = 0; i < single.length; i++) {
                //listado.put(single[i], single[i+1]);
                invitadosdb.agregarDatos(single[i].toUpperCase(), single[i + 1].toUpperCase());
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class MyTask extends AsyncTask<Void, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar spinner;
            spinner = (ProgressBar)findViewById(R.id.actualizarspinner);
            spinner.setVisibility(View.VISIBLE);
            TextView actText;
            actText = (TextView)findViewById(R.id.actualizando);
            actText.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            actualizarDatos();
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            ProgressBar spinner;
            spinner = (ProgressBar)findViewById(R.id.actualizarspinner);
            spinner.setVisibility(View.GONE);
            TextView actText;
            actText = (TextView)findViewById(R.id.actualizando);
            actText.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "Datos Actualizados!", Toast.LENGTH_LONG).show();
        }
    }

    public void explorar (){
        Intent intent = new Intent();
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Set your required file type
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "DEMO"), 1001);
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        // super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == 1001) {
                Uri currFileURI = data.getData();
                pathi = currFileURI.getPath();
                new MyTask().execute();
            }
        }else {
            Toast.makeText(MainActivity.this, "No se selecciono archivo!...no habra actualización de datos",Toast.LENGTH_LONG).show();
        }
    }





    }
