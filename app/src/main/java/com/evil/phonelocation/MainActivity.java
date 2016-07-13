package com.evil.phonelocation;

import android.app.Activity;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends Activity {
	EditText et_phone;
	TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除标题
		
		setContentView(R.layout.activity_main);

		copySql(); // 初始化数据库,把数据库加载进数据库文件夹下

		et_phone = (EditText) findViewById(R.id.et_phone);
		tv = (TextView) findViewById(R.id.tv);

	}

	/**
	 * check 查询按钮点击事件
	 * @param v
	 * void
	 */
	public void check(View v) {
		if (TextUtils.isEmpty(et_phone.getText())) {	//判断是否输入数字
			Toast.makeText(this, "请输入要查询的手机号", Toast.LENGTH_SHORT).show();
			return;
		} else if (et_phone.getText().toString().length() != 11) {	//判断是否是11位手机号
			Toast.makeText(this, "无效的手机号码", Toast.LENGTH_SHORT).show();
			return;
		}

		String phone = et_phone.getText().toString().substring(0, 7);	//截取手机号的前7位来查询数据库
		SQLiteDatabase db = openOrCreateDatabase("address.db", 0, null);	//打开指定的数据库
		Cursor cursor = db.query("data1", new String[] { "outKey" }, "id=?",	//根据条件获取data1游标
				new String[] { phone }, null, null, null);
		String id = null;
		if (cursor.moveToFirst()) {	//把游标移动到第一位
			id = cursor.getString(0);	//获取到手机号代表的id
			cursor.close();
		} else {					//没有查到数据
			Toast.makeText(this, "数据库中没有您要查询的手机号", Toast.LENGTH_SHORT).show();
			cursor.close();
			return;
		}
		Cursor cursor2 = db.query("data2", new String[] { "location", "area" },//查询数据库data2
				"id=?", new String[] { id }, null, null, null);
		if (cursor2.moveToFirst()) {	//是否查询到数据
			String location = cursor2.getString(0);//获取归属地
			String area = cursor2.getString(1);		//获取区号
			if (area.length() == 2) {				//判断区号是否2位数
				area = "00" + area;
			} else {
				area = "0" + area;
			}
			cursor2.close();						//关闭资源
			tv.setText("手机号:" + et_phone.getText().toString() + "\n归属地:" + location + "\n"
					+ "区号:" + area);
			db.close();								//关闭数据库
		} else {
			Toast.makeText(this, "数据库中没有您要查询的手机号", Toast.LENGTH_SHORT).show();
			cursor2.close();
			db.close();
			return;
		}
	}

	/**
	 * copySql 初始化时加载数据库,如果没有就copy
	 * 
	 * void
	 */
	public void copySql() {
		try {
			File sqlDb = getDatabasePath("address.db");		//获取数据库文件下的数据库文件
			if (!sqlDb.exists()) {							//假如不存在则复制数据库
				File dir = sqlDb.getParentFile();			//获取数据库的文件夹
				dir.mkdirs();								//创建文件夹
				AssetManager assets = getAssets();			//获取资源管理器
				InputStream open = assets.open("address.db");//打开资源文件下的数据库文件的输入流
				FileOutputStream fos = new FileOutputStream(sqlDb);//打开输出流
				byte[] arr = new byte[8192];
				int len;
				while ((len = open.read(arr)) != -1) {
					fos.write(arr, 0, len);
				}
				open.close();		//关流
				fos.close();		//关流
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
