package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dhc.gallery.GalleryConfig;
import com.dhc.gallery.GalleryHelper;
import com.dhc.gallery.ui.GalleryActivity;
import com.example.myapplication.bean.ShangpinBean;
import com.example.myapplication.util.HttpUtils;
import com.example.myapplication.util.StorageType;
import com.example.myapplication.util.StorageUtil;
import com.example.myapplication.util.TextCallBack;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements PermissionListener {

    private long timea;
    private ImageView imgHead = null;
    String headFile = "";// 已选择头像地址
    List<String> mList;
    String outputPath;
    ShangpinBean shangpinBean;

private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context=this;
        if (!AndPermission.hasPermission(MainActivity.this
                , Manifest.permission.CAMERA
                , Manifest.permission.READ_PHONE_STATE
                , Manifest.permission.RECORD_AUDIO)) {
            AndPermission.with(MainActivity.this)
                    .requestCode(100)
                    .permission(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE)
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                            AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                        }
                    })
                    .send();
        }
     //   showTip("toas测试");


        /**
         * 初始化监测sdcard
         * */
        StorageUtil.init(this, null);
        /**
         *
         * */

        mList = new ArrayList<>();
        mList.add((mList.size() + 1) + ".  选择单张图片");
        mList.add((mList.size() + 1) + ".  选择单张图片并裁剪");
        mList.add((mList.size() + 1) + ".  选择多张图片");
        mList.add((mList.size() + 1) + ".  选择视频");
        mList.add((mList.size() + 1) + ".  拍摄视频(可限制时长)");
        mList.add((mList.size() + 1) + ".  拍照片");
        mList.add((mList.size() + 1) + ".  拍照片并裁剪");


        ListView listView = (ListView) findViewById(R.id.ls_home);

        listView.setAdapter(new MyAdapter(this));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: /*** 选择单张图片 onActivityResult{@link GalleryActivity.PHOTOS}*/
                        GalleryHelper.with(MainActivity.this).type(GalleryConfig.SELECT_PHOTO).requestCode(12).singlePhoto().execute();
                        break;
                    case 1:  /***选择单张图片并裁剪 onActivityResult{@link GalleryActivity.PHOTOS}*/
                        outputPath = StorageUtil.getWritePath(StorageUtil.get32UUID() + ".jpg", StorageType.TYPE_TEMP);
                        GalleryHelper.with(MainActivity.this).type(GalleryConfig.SELECT_PHOTO).requestCode(12).singlePhoto().isNeedCropWithPath(outputPath).execute();
                        break;
                    case 2:  /*** 选择多张图片 onActivityResult{@link GalleryActivity.PHOTOS}*/
                        GalleryHelper.with(MainActivity.this).type(GalleryConfig.SELECT_PHOTO).requestCode(12).limitPickPhoto(9).execute();
                        break;
                    case 3:/***选择视频 onActivityResult{@link GalleryActivity.VIDEO}*/
                        GalleryHelper.with(MainActivity.this).type(GalleryConfig.SELECT_VEDIO).requestCode(12).isSingleVedio().execute();
                        break;
                    case 4:/***拍摄视频 onActivityResult{@link GalleryActivity.VIDEO}*/

                        GalleryHelper.with(MainActivity.this).type(GalleryConfig.RECORD_VEDIO).requestCode(12)
//                                .limitRecordTime(10)//限制时长
                                .limitRecordSize(1)//限制大小
                                .execute();
                        break;
                    case 5:/***拍照片onActivityResult {@link GalleryActivity.PHOTOS}*/

                        GalleryHelper.with(MainActivity.this).type(GalleryConfig.TAKE_PHOTO).requestCode(12).execute();
                        break;
                    case 6: /***拍照片并裁剪 onActivityResult{@link GalleryActivity.CROP}*/
                        outputPath = StorageUtil.getWritePath(StorageUtil.get32UUID() + ".jpg", StorageType.TYPE_TEMP);
                        GalleryHelper.with(MainActivity.this).type(GalleryConfig.TAKE_PHOTO).isNeedCropWithPath(outputPath).requestCode(12).execute();
                        break;
                    default:
                        break;
                }
            }
        });

        timea = 1541569323155l;
        Toast.makeText(this, timeStampToDate(timea, "yyyy年MM月dd日 HH:mm:ss E"), Toast.LENGTH_SHORT).show();
        Log.e("time", "onCreate: " + timeStampToDate(timea, "yyyy年MM月dd日 HH:mm:ss E"));
        api();
    }



    public static String timeStampToDate(long tsp, String... format) {
        SimpleDateFormat sdf;
        if (format.length < 1) {
            sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss E", Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat(format[0], Locale.getDefault());
        }

        return sdf.format(tsp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (12 == requestCode && resultCode == Activity.RESULT_OK) {
            if (data.getStringArrayListExtra(GalleryActivity.PHOTOS) != null) {//选择图片返回

                ArrayList<String> path = data.getStringArrayListExtra(GalleryActivity.PHOTOS);
                Toast.makeText(MainActivity.this, path.toString(), Toast.LENGTH_SHORT).show();

                //    Toast.makeText(MainActivity.this,"111", Toast.LENGTH_SHORT).show();

            } else if (data.getStringExtra(GalleryActivity.VIDEO) != null) {//选择和拍摄视频(目前支持单选)

                String path = data.getStringExtra(GalleryActivity.VIDEO);
                Toast.makeText(MainActivity.this, path.toString(), Toast.LENGTH_SHORT).show();

                //    Toast.makeText(MainActivity.this,"222", Toast.LENGTH_SHORT).show();

            } else if (data.getStringExtra(GalleryActivity.DATA) != null) {//裁剪回来
                if (outputPath == null) {//没有传入返回裁剪路径
                    byte[] datas = data.getByteArrayExtra(GalleryActivity.DATA);
                    Toast.makeText(MainActivity.this, datas.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        } else if (requestCode == 300 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "从设置回来", Toast.LENGTH_LONG).show();
        }

    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (data.getStringArrayListExtra(GalleryActivity.PHOTOS) != null) {//选择图片返回
////
//                ArrayList<String> path = data.getStringArrayListExtra(GalleryActivity.PHOTOS);
////                Toast.makeText(MainActivity.this, path.toString(), Toast.LENGTH_SHORT).show();
//                try {
//                    FileInputStream fis = new FileInputStream(path.get(0));
//                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
//
//                    Matrix matrix = new Matrix();
//                    matrix.postRotate(readPictureDegree(path.get(0))); /*翻转90度*/
//                    int width = bitmap.getWidth();
//                    int height = bitmap.getHeight();
//                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//
//
//                    ByteArrayOutputStream baos;
//                    // mIvaddSpImg.setImageBitmap(bitmap);
//                    baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    byte[] imgs;
//                    //                        bytes = baos.toByteArray();
//                    imgs = baos.toByteArray();
//
//                    String base = Base64.encodeToString(imgs, Base64.DEFAULT);
//                   // chuantupian(base);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
////
//            if (data.getStringExtra(GalleryActivity.DATA) != null) {//裁剪回来
//                Log.e("photo", "onActivityResult: "+"111111111111111111" );
//                if (outputPath == null) {//没有传入返回裁剪路径
//
//                    Log.e("photo", "onActivityResult: "+"22222222222222222222" );
//                    byte[] datas = data.getByteArrayExtra(GalleryActivity.DATA);
//                    try {
//                        FileInputStream fis = new FileInputStream(datas.toString());
//                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
//
//                        Matrix matrix = new Matrix();
//                        matrix.postRotate(readPictureDegree(datas.toString())); /*翻转90度*/
//                        int width = bitmap.getWidth();
//                        int height = bitmap.getHeight();
//                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
//                        ByteArrayOutputStream baos;
//                        // mIvaddSpImg.setImageBitmap(bitmap);
//                        baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] imgs;
//                        //                        bytes = baos.toByteArray();
//                        imgs = baos.toByteArray();
//
//                        String base = Base64.encodeToString(imgs, Base64.DEFAULT);
//                      //  chuantupian(base);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
////
//        }
//
//        }
//
//


    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        //传入图片路径
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    private class MyAdapter extends BaseAdapter {
        Context mContext;
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item, null);
                holder.info = (TextView) convertView.findViewById(R.id.tv_info);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.info.setText(mList.get(position));

            return convertView;
        }

        class ViewHolder {
            public TextView info;
        }
    }


    //----------------------------------权限回调处理----------------------------------//

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /**
         * 转给AndPermission分析结果。
         *
         * @param requestCode  请求码。
         * @param permissions  权限数组，一个或者多个。
         * @param grantResults 请求结果。
         * @param listener PermissionListener 对象。
         */
        AndPermission.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onSucceed(int requestCode, List<String> grantPermissions) {
        Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(int requestCode, List<String> deniedPermissions) {

        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (!AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第一种：用默认的提示语。
            AndPermission.defaultSettingDialog(this, 300).show();
            Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void tanchuang(View view) {

        showWaitTranslate("loading测试", null);
    }
private void api(){
        showWaitTranslate("加载数据",null);
    JSONObject jsonObject = new JSONObject();
   // Map<String, String> map;
    try {
        jsonObject.put("index_page", 1);
        jsonObject.put("index_size", 10);
        HttpUtils.post(context, "http://app.pangumeng.com/Api.php/Cate/goodslist", jsonObject, new TextCallBack() {
            @Override
            protected void onSuccess(String text) {
                Gson gson = new GsonBuilder().registerTypeAdapter(String.class, new HttpUtils.StringConverter()).create();
                shangpinBean = gson.fromJson(text, ShangpinBean.class);
                if (shangpinBean.getStatus() == 1) {
               //    hideWait();
                   showTip("chenggong");
                } else {
                   hideWait();
                }
            }

            @Override
            protected void onFailure(ResponseException e) {

            }
        });
    } catch (JSONException e) {
        e.printStackTrace();
    }
}

   /* *//**
     * 接口
     *//*
    class Api extends AsyncTask<String, Integer, ShangpinBean> {
        @Override
        protected ShangpinBean doInBackground(String... strings) {
            *//*异步执行后台线程要完成的任务,耗时操作将在此方法中完成*//*
            //对接口

            return shangpinBean;
        }

        @Override
        protected void onPostExecute(ShangpinBean shangpinBean) {

                *//*当doInBackground方法完成后,系统将自动调用此方法,并将doInBackground方法返回的值传入此方法.通过此方法进行UI的更新.*//*
      //     showTip(shangpinBean.getData()+"");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            *//*:执行后台耗时操作前被调用,通常用于进行初始化操作.*//*
           showWaitTranslate("加载中",null);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            *//*当在doInBackground方法中调用publishProgress方法更新任务执行进度后,将调用此方法.通过此方法我们可以知晓任务的完成进度*//*
        }
    }*/
}
