package it.android.j940549.myreg_elettronico;

/**
 * Created by J940549 on 30/12/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.system.ErrnoException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.android.j940549.myreg_elettronico.SQLite.DBLayer;
import it.android.j940549.myreg_elettronico.model.Alunno;
import it.android.j940549.myreg_elettronico.navigationDrawer.ScegliDettaglioNav;

public class MyRecyclerViewAdapter_ElencoAlunni extends RecyclerView.Adapter<MyRecyclerViewAdapter_ElencoAlunni.DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<Alunno> mDataset;
    private Activity myActivity;
    private String quad, annosc;
    private String TAG_LOG = "myRecyclerElencoAlunni";
    private int ACTION_TAKE_PHOTO_B = 1;
    private int ACTION_TAKE_GALLERY = 2;
    private boolean permessiOK;
//    private String mCurrentPhotoPath;

    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        //        implements View
        //          .OnClickListener {
        Alunno alunno;
        TextView tipo_istituto, nome_istituto, userid, password;
        TextView cf_istituto;
        TextView idalunno, nome_alunno, classe_sez_alunno;
        ImageView imageView;
        Button btn_option;

        public DataObjectHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_alunno);
            nome_istituto = (TextView) itemView.findViewById(R.id.nome_istituto);
            tipo_istituto = (TextView) itemView.findViewById(R.id.tipo_istituto);
            cf_istituto = (TextView) itemView.findViewById(R.id.cf_istituto);
            userid = (TextView) itemView.findViewById(R.id.userid);
            password = (TextView) itemView.findViewById(R.id.password);
            nome_alunno = (TextView) itemView.findViewById(R.id.nome_alunno);
            classe_sez_alunno = (TextView) itemView.findViewById(R.id.classe_se_alunno);
            idalunno = (TextView) itemView.findViewById(R.id.id_alunno);
            btn_option = itemView.findViewById(R.id.btn_option);
            Log.i(LOG_TAG, "Adding Listener");
            // itemView.setOnClickListener(this);
        }

    }


    public MyRecyclerViewAdapter_ElencoAlunni(ArrayList<Alunno> myDataset, Activity activity, String anno_sc, String quadrim) {
        mDataset = myDataset;
        myActivity = activity;
        annosc = anno_sc;
        quad = quadrim;

    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_row_alunni, parent, false);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView id = (TextView) view.findViewById(R.id.id_alunno);
                vai_alunno(view, id.getText().toString());
            }
        });

        Button btn_option = view.findViewById(R.id.btn_option);
        btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView id = (TextView) view.findViewById(R.id.id_alunno);
                apriPopup(v, id.getText().toString());
            }
        });


        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        Alunno alunno=mDataset.get(position);
        String path_image=getPatchImage(""+Integer.parseInt(alunno.getCod_alunno()));
        if(!path_image.equals("")){
            setPic(        holder.imageView,path_image);
        }
        holder.cf_istituto.setText(mDataset.get(position).getCfIstituto());
        holder.userid.setText(mDataset.get(position).getUserid());
        holder.password.setText(mDataset.get(position).getPassword());
        holder.tipo_istituto.setText(mDataset.get(position).getTipo());
        holder.nome_istituto.setText(mDataset.get(position).getNome_istituto());
        holder.nome_alunno.setText(mDataset.get(position).getNome_alunno());
        holder.classe_sez_alunno.setText(mDataset.get(position).getClasse_sez());
        holder.idalunno.setText(mDataset.get(position).getId_alunno());
    }

    public void addItem(Alunno dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void apriPopup(View v, final String id_alunno) {
        android.support.v7.widget.PopupMenu popupMenu = new android.support.v7.widget.PopupMenu(myActivity, v);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.change_image:
                        //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        //myActivity.startActivityForResult(cameraIntent, TAKE_PICTURE);
                        //myActivity.startActivityForResult(getPickImageChooserIntent(), 200);
                        selectImage(myActivity, id_alunno);
                        break;

                    case R.id.delete_alunno:
                        DBLayer dbLayer = null;

                        try {
                            dbLayer = new DBLayer(myActivity);
                            dbLayer.open();
                            dbLayer.deleteAlunno(id_alunno);


                        } catch (SQLException ex) {
                            Toast.makeText(myActivity, "" + ex.toString(), Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        Toast.makeText(myActivity, "dato cancellato!" + id_alunno, Toast.LENGTH_SHORT).show();


                        dbLayer.close();
                        myActivity.recreate();
                        break;
                }

                return true;
            }

        });

        popupMenu.show();

    }

    public void vai_alunno(View view, String id_alunno) {//alunno, String classe_sez, String cf_istituto, String userid, String pw, String nome_ist) {
        Alunno alunno = get_Alunno(id_alunno);
        Intent intent = new Intent(myActivity, ScegliDettaglioNav.class);
        intent.putExtra("alunno", (Serializable) alunno);
        intent.putExtra("annosc", annosc);
        intent.putExtra("quadrimestre", quad);
        intent.putExtra("pagina", 0);
        myActivity.startActivity(intent);
        myActivity.finish();
    }

    public Alunno get_Alunno(String id_alunno) {
        Alunno dataObject_alunno = null;
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(myActivity);
            dbLayer.open();
            Cursor cursor = dbLayer.getAlunno(id_alunno);

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    dataObject_alunno = new Alunno();
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

                } while (cursor.moveToNext());
            }
        } catch (SQLException ex) {
            Toast.makeText(myActivity, "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();


        return dataObject_alunno;
    }

    private void selectImage(Context context, final String id_alunno) {
        final Alunno alunno = get_Alunno(id_alunno);
        ACTION_TAKE_PHOTO_B = Integer.parseInt(alunno.getCod_alunno()) + 1;
        ACTION_TAKE_GALLERY = Integer.parseInt(alunno.getCod_alunno()) + 2;
        final CharSequence[] options = {"Fai una foto", "Scegli da Gallery", "Imagine Default", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Fai una foto")) {
                    /*Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    myActivity.startActivityForResult(takePicture, 0);*/
                    controllaPermessi();
                    if (permessiOK == true) {
                        dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                    } else {
                        Toast.makeText(myActivity, "L'applicazione non ha i permessi per proseguire", Toast.LENGTH_SHORT).show();
                    }

                } else if (options[item].equals("Scegli da Gallery")) {
                    /*Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    myActivity.startActivityForResult(pickPhoto, 1);//one can be replaced with any action code*/
                    controllaPermessi();
                    if (permessiOK == true) {
                        Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        myActivity.startActivityForResult(in, ACTION_TAKE_GALLERY);
                        //       finish();
                    } else {
                        Toast.makeText(myActivity, "L'applicazione non ha i permessi per proseguire", Toast.LENGTH_SHORT).show();
                    }

                } else if (options[item].equals("Imagine Default")) {
                    SharedPreferences sharedPref = myActivity.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(""+Integer.parseInt(alunno.getCod_alunno()), "");
                    editor.commit();
                    myActivity.recreate();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    public void controllaPermessi() {
        if (ContextCompat.checkSelfPermission(myActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(myActivity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    ACTION_TAKE_GALLERY);
        } else {
            permessiOK = true;
        }
        if (ContextCompat.checkSelfPermission(myActivity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(myActivity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    ACTION_TAKE_PHOTO_B);

        } else {
            permessiOK = true;
        }
    }


    private void dispatchTakePictureIntent(int actionCode) {

        Log.i(TAG_LOG, "cod_lunno...+1" + actionCode);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(myActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile("" + (actionCode - 1));
            } catch (IOException ex) {
                // Error occurred while creating the File
                //...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(myActivity.getBaseContext(),
                        "it.android.j940549.myreg_elettronico.provider",
                        photoFile);
                Log.i(TAG_LOG, "photoURI.." + photoURI.toString());      // createImageFile();

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                myActivity.startActivityForResult(takePictureIntent, actionCode);
            }
        }
    }

    private File createImageFile(String cod_alunno) throws IOException {

        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + cod_alunno + ".jpg";
        File storageDir = myActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir + File.separator + imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = image.getAbsolutePath();

        ///File imageF = new File( myActivity.getBaseContext().getExternalFilesDir(null), id_alunno+".jpg");
        Log.i(TAG_LOG, "image file patch.." + image.getAbsolutePath());      // createImageFile();
        //  mCurrentPhotoPath = imageF.getAbsolutePath();

        return image;

    }


    private String getPatchImage(String cod_alunno) {
        SharedPreferences sharedPref = myActivity.getPreferences(Context.MODE_PRIVATE);
        String patch_Image = sharedPref.getString(cod_alunno, "");
        Log.i(TAG_LOG, "leggo_patch.." + cod_alunno+ "--" + patch_Image);


       return patch_Image;
    }


    private void setPic(ImageView mImageView, String patch_image) {

        // There isn't enough memory to open up more than a couple camera photos
        // So pre-scale the target bitmap into which the file is decoded

        // Get the size of the ImageView
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the size of the image
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(patch_image, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Figure out which way needs to be reduced less
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        // Set bitmap options to scale the image decode target
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        // Decode the JPEG file into a Bitmap


        //Bitmap bitmaporig = BitmapFactory.decodeFile(mCurrentPhotoPath);
        //Bitmap bitmap = Bitmap.createScaledBitmap(bitmaporig, 50, 50, true);
        Bitmap bitmap = BitmapFactory.decodeFile(patch_image, bmOptions);
        //Toast.makeText(this, "pic", Toast.LENGTH_SHORT).show();
        // Associate the Bitmap to the ImageView
        mImageView.setImageBitmap(bitmap);
        //mImageView.setVisibility(View.VISIBLE);
    }

}
