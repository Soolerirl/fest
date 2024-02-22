package eu.fest;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Session;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.fest.model.User;
import eu.fest.model.databases.CurrentUser;
import eu.fest.presentation.DatabaseManager;
import eu.fest.service.WebServerService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

@EActivity(R.layout.activity_changeuserdata)
public class UserDataChangeActivity extends FragmentActivity {
    @App
    ServiceBusApplication app;

    @Bean
    WebServerService webServerService;

    @Bean(DatabaseManager.class)
    DatabaseManager db;

    @ViewById
    Spinner genderspiner;

    @ViewById
    EditText birthdateInput;

    @ViewById
    EditText firstNameInput;

    @ViewById
    EditText lastNameInput;

    @ViewById
    CircleImageView profile_image;

    private HashMap<String, String> genders = new HashMap<String, String>();

    private Calendar calendar;

    private int year, month, day;

    private Dialog mDialog;

    TextView text;

    CurrentUser localuser;

    String path;

    Dialog dialog;

    private static final int CAMERA_REQUEST = 1888;
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void setupUi(){

        mDialog = new Dialog(UserDataChangeActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.message_dialog);
        text = (TextView) mDialog.findViewById(R.id.text);

        db = new DatabaseManager(this);
        for(CurrentUser user : getUser()){
            localuser = user;
        }

        if(localuser.getProfileImage()!=null) {
            Picasso.with(UserDataChangeActivity.this).load(localuser.getProfileImage().startsWith("http") ? localuser.getProfileImage() : "http://" + localuser.getProfileImage()).into(profile_image);
        }
        final ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        genders.put(" ", " ");
        genders.put(getResources().getString(R.string.male), "male");
        genders.put(getResources().getString(R.string.female), "female");

        int currentIndex = 0, i = 0;
        for (Map.Entry<String, String> gender: genders.entrySet()) {
            genderAdapter.add(gender.getKey());
            if(gender.getValue().equals(localuser.getGender()))
                currentIndex = i;

            i++;
        }

        genderspiner.setAdapter(genderAdapter);
        genderspiner.setSelection(currentIndex);
        localuser.setGender(genderAdapter.getItem(currentIndex));
        genderspiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String gender = genderAdapter.getItem(position);
                localuser.setGender(gender);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        birthdateInput.setText(localuser.getBirthday());
        firstNameInput.setText(localuser.getFirstName());
        lastNameInput.setText(localuser.getLastName());
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private Collection<CurrentUser> getUser() {
        try {
            return db.getDatabaseHelper().getUserDataDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new LinkedList<CurrentUser>();
    }

    private void showDate(int year, int month, int day) {
        String birthday = year + "." + month + "." + day;
        localuser.setBirthday(birthday);
        birthdateInput.setText(birthday);
    }

    @Click(R.id.birthdateInput)
    void clickHandle() {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    @Click(R.id.change_image_icon)
    void changeImageClickHandle(){
        dialog = new Dialog(UserDataChangeActivity.this);
        dialog.requestWindowFeature((int) Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.image_change_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageButton camera_choser = (ImageButton) dialog.findViewById(R.id.camera_choser);
        camera_choser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_camera();
            }
        });

        ImageButton file_choser = (ImageButton) dialog.findViewById(R.id.file_choser);
        file_choser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                path="";
                dialog.dismiss();
            }
        });

        Button btn_change = (Button) dialog.findViewById(R.id.btn_change);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImageToServer();
            }
        });

        //dialog.getWindow().setLayout(android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    @Click(R.id.save)
    void saveClickHandle(){
        if (isFilled(firstNameInput)) {
            localuser.setFirstName(lastEmptyCharDelete(firstNameInput));
        }else{
            text.setText("Keresztnév megadása kötelező");
            return;
        }

        if (isFilled(lastNameInput)) {
            localuser.setLastName(lastEmptyCharDelete(lastNameInput));
        }else{
            text.setText("Vezetéknév megadása kötelező");
            return;
        }

        webServerService.changeUserData(localuser, new Callback<CurrentUser>() {
            @Override
            public void success(CurrentUser user, Response response) {
                mDialog.dismiss();
                try {
                    db.getDatabaseHelper().getUserDataDao().update(localuser);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                SettingsActivity_.intent(UserDataChangeActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }

            @Override
            public void failure(RetrofitError error) {
                text.setText(error.getMessage());
            }
        });
    }

    private void call_camera(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);
    }

    private void showFileChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){
                this.requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            }
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED){
                this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i = Intent.createChooser(i, "Choose a file");

        startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            if(requestCode == CAMERA_REQUEST) {
                path = getPath(UserDataChangeActivity.this, data.getData());
                if(path==null){
                    path = data.getData().getPath();
                    path = path.replace("document", "storage");
                    path = path.replace(":", "/");
                }
            }
        }

        if(requestCode == 2){
            //filedelete(path);
        }

        if(resultCode==-1)
        {
            path = getPath(UserDataChangeActivity.this, data.getData());
            if(path==null){
                path = data.getData().getPath();
                path = path.replace("document", "storage");
                path = path.replace(":", "/");
            }
        }
    }

    public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    @Background
    public void sendImageToServer(){
        File image = new File(path);
        Uri uri = Uri.parse(path);
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap b = BitmapFactory.decodeFile(image.getAbsolutePath(),option);
        Matrix matrix = new Matrix();
        matrix.postRotate(getCameraPhotoOrientation(UserDataChangeActivity.this,uri,path));
        Bitmap rotatedBitmap = Bitmap.createBitmap(b , 0, 0, b.getWidth(), b.getHeight(), matrix, true);
        Bitmap d =  scaleDown(rotatedBitmap,3000,true);
        File filesDir = this.getFilesDir();
        File imageFile = new File(filesDir, "profile_pic.jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            d.compress(Bitmap.CompressFormat.JPEG, 0, os);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            d.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }

        TypedFile in = new TypedFile("image/jpeg", imageFile);
        webServerService.fileUpload(in, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                refresDB();
                path="";
                dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("Error", error.getMessage());
            }
        });
    }

    @Background
    void refresDB(){
        try {
            db.getDatabaseHelper().getUserDataDao().update(localuser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] {
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean isFilled(EditText editText) {
        return editText.getText().toString() != null && !editText.getText().toString().isEmpty();
    }

    String lastEmptyCharDelete(EditText editText){
        String str = editText.getText().toString();
        if (str.charAt(str.length()-1)==' ')
        {
            str = str.replace(str.substring(str.length()-1), "");
        }
        return str;
    }
}
