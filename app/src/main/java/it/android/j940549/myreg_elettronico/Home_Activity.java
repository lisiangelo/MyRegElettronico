package it.android.j940549.myreg_elettronico;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;


public class Home_Activity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Alunno> dataSet = new ArrayList<Alunno>();
    public static String annoscolastico, quadrimestre, quad;
    private String TAG_LOG = "Home_activity";
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int ACTION_TAKE_GALLERY = 2;
    private boolean permessiOK;
    private String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GregorianCalendar ddate = new GregorianCalendar();

        int mese = ddate.get(GregorianCalendar.MONTH) + 1;
        int anno = ddate.get((GregorianCalendar.YEAR));
        Log.i(TAG_LOG, "" + mese + "---" + anno);
        if (mese > 1 && mese < 8) {
            quadrimestre = "II quad/trim";
            quad = "2";
        } else {
            quadrimestre = "I quad/trim";
            quad = "1";
        }
/*        if (anno == 2018 && mese > 9) {
            annoscolastico = "2018/2019";
        } else if (anno == 2019 && mese > 9) {
            annoscolastico = "2019/2020";
        } else {
            annoscolastico = "2018/2019";
        }*/
        if (mese > 8) {
            annoscolastico = ""+anno+"/"+(anno+1);
        } else {
            annoscolastico = ""+(anno-1)+"/"+anno;
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view_elenco_alunni);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        caricaDati();
        mCurrentPhotoPath = "";
        mAdapter = new MyRecyclerViewAdapter_ElencoAlunni(dataSet, this, annoscolastico, quad);
        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vai_agg_alunno();
            }
        });
    }


    private void vai_agg_alunno() {
        Intent intent = new Intent(this, Aggiungi_Alunno.class);
        startActivity(intent);
    }

    private ArrayList<Alunno> getDataSet() {
        return dataSet;
    }

    public void caricaDati() {

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();
            Cursor cursor = dbLayer.getAllAlunni();

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    Alunno dataObject_alunno = new Alunno();
                    dataObject_alunno.setId_alunno(cursor.getString(0));
                    dataObject_alunno.setCfIstituto(cursor.getString(1));
                    dataObject_alunno.setTipo(cursor.getString(2));
                    dataObject_alunno.setNome_istituto(cursor.getString(3));
                    dataObject_alunno.setIndirizzo(cursor.getString(4));
                    dataObject_alunno.setNome_alunno(cursor.getString(5));
                    dataObject_alunno.setCod_alunno(cursor.getString(6));
                    dataObject_alunno.setClasse_sez(cursor.getString(7));
                    dataObject_alunno.setUserid(cursor.getString(8));
                    dataObject_alunno.setPassword(cursor.getString(9));
                    Log.i(TAG_LOG, dataObject_alunno.getNome_alunno() + "--" + dataObject_alunno.getCod_alunno() + "..." +
                            dataObject_alunno.getNome_istituto() + "\n"
                            + dataObject_alunno.getPassword() + "---" + dataObject_alunno.getUserid());
                    dataSet.add(dataObject_alunno);
                } while (cursor.moveToNext());
            } else {
                vai_agg_alunno();
            }
        } catch (SQLException ex) {
            Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();


    }

    /*
        private void setPic(ImageView mImageView) {

            // There isn't enough memory to open up more than a couple camera photos
            // So pre-scale the target bitmap into which the file is decoded

            // Get the size of the ImageView
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            // Get the size of the image
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Figure out which way needs to be reduced less
            int scaleFactor = 1;
            if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(photoW/targetW, photoH/targetH);
            }

            // Set bitmap options to scale the image decode target
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            // Decode the JPEG file into a Bitmap


            //Bitmap bitmaporig = BitmapFactory.decodeFile(mCurrentPhotoPath);
            //Bitmap bitmap = Bitmap.createScaledBitmap(bitmaporig, 50, 50, true);
            Bitmap bitmap =BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            Toast.makeText(this, "pic", Toast.LENGTH_SHORT).show();
            // Associate the Bitmap to the ImageView
            mImageView.setImageBitmap(bitmap);
            //mImageView.setVisibility(View.VISIBLE);
        }
    */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        Log.i(TAG_LOG, "mCurrentPhotoPath..addpic.." + mCurrentPhotoPath);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        Toast.makeText(this, "galleriaddpicc", Toast.LENGTH_SHORT).show();
        this.sendBroadcast(mediaScanIntent);
    }


    private void handleBigCameraPhoto() {
        //galleryAddPic();
        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }


    private File createImageFile(String cod_alunno) throws IOException {

        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + cod_alunno + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir + File.separator + imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();

        ///File imageF = new File( myActivity.getBaseContext().getExternalFilesDir(null), id_alunno+".jpg");
        Log.i(TAG_LOG, "image file patch.." + image.getAbsolutePath());      // createImageFile();
        //  mCurrentPhotoPath = imageF.getAbsolutePath();

        return image;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG_LOG, "requestcod or.." + requestCode);
        int valore = requestCode;
        for (Alunno alunno : dataSet) {
            int cod_alunno = Integer.parseInt(alunno.getCod_alunno());
            requestCode = valore - cod_alunno;
            Log.i(TAG_LOG, "cod.." + cod_alunno);
            Log.i(TAG_LOG, "requestcod.. dopo.." + requestCode);
            switch (requestCode) {

                case ACTION_TAKE_PHOTO_B: {
                    Log.i(TAG_LOG, "azione.." + requestCode);

                    if (resultCode == RESULT_OK) {
                        Log.i(TAG_LOG, "resultCode.." + resultCode);

                        File image = null;
                        try {
                            image = createImageFile("" + cod_alunno);
                            mCurrentPhotoPath = image.getAbsolutePath();
                            salvaImage_patch("" + cod_alunno, mCurrentPhotoPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //     Log.i(TAG_LOG, "data photo..."+data.getExtras().toString());
//                    Log.i(TAG_LOG, "data...."+data.toString());

                        handleBigCameraPhoto();

                        //     ritornaActionChiamante();
                    }
                    break;
                } // ACTION_TAKE_PHOTO_B
                case ACTION_TAKE_GALLERY: {
                    Log.i(TAG_LOG, "requestCode.." + requestCode);

                    //  Toast.makeText(this, "image rom gallery"+mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
                    if (resultCode == RESULT_OK) {
                   /*
                    Log.i(TAG_LOG, "resultCode.."+resultCode);
                    Log.i(TAG_LOG, "data gallery"+data.getData().getPath());
                    Log.i(TAG_LOG, "id_alunno"+data.getExtras().getString("id_alunno"));*/

                        //           Toast.makeText(this, "image rom gallery"+mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
                        Uri contentUri = data.getData();
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
                        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        mCurrentPhotoPath = cursor.getString(colum_index);
                        String pathorig = mCurrentPhotoPath;
                        Log.i(TAG_LOG, "mCurrentPhotoPath..." + pathorig);

                        File dest = null;
                        if (pathorig != null) {
                            try {
                                dest = createImageFile("" + cod_alunno);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String pathchdest = dest.getAbsolutePath();

                            Log.d("image rom gallery 2", pathchdest);
                            try {
                                copyFile(pathorig, pathchdest);
                                salvaImage_patch("" + cod_alunno, pathchdest);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mCurrentPhotoPath = null;


                            //  ritornaActionChiamante();
                        }

                    }
                    break;
                } // ACTION_TAKE_PHOTO_B
            }
        }// switch
        recreate();
    }

    private void copyFile(String patchOrig, String patchDest) throws IOException {
        File inputFile = new File(patchOrig);
        File outFile = new File(patchDest);
        Log.d("file dest", outFile.getAbsolutePath());
        try {

            FileChannel src = new FileInputStream(inputFile).getChannel();
            FileChannel dst = new FileOutputStream(outFile).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();

            Toast.makeText(getBaseContext(), "Import Successful!", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Log.d("image rom gallery ", e.toString());
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    private void salvaImage_patch(String alunno, String image_patch) {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(alunno, image_patch);
        editor.commit();
        Log.i(TAG_LOG, "salvo_patch.." + alunno + "--" + image_patch);
    }


}

