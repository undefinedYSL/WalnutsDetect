package photo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDataBaseHelper extends SQLiteOpenHelper {
           public static final String CREATE_BOOK="create table person("+"id integer primary key autoincrement,"+"name text,"+"pages text,"+"salary text)";


    /*
3    * context：上下文
4    * databaseName:创建的数据库名称
5    * databaseVersion：数据库版本
6    * */
//           public MyDataBaseHelper(Context context, String databaseName, int databaseVersion){
//              super(context,databaseName,null,databaseVersion);
//          }

   private Context mContext;
    public MyDataBaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version) {
        super(context, name, factory, version);
        mContext=context;

    }

    /*
   * 数据库第一次创建的时候，调用onCreate；数据库已经创建成功之后，就不调用它了
12     * db就是创建的数据库
13     * db.execSQL这句是用来创建数据库表
14     * */
//           @Override
//   public void onCreate(SQLiteDatabase db) {
//                System.out.println("数据库创建成功");
//                db.execSQL("create table imagetable(_id integer primary key autoincrement,word varchar(255),detail varchar(255))");//执行创建表的sql语句
//            }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //打开数据库，建立了一个叫records的表，里面只有一列name来存储历史记录：
//        db.execSQL("create table records(id integer primary key autoincrement,name varchar(200))");
        db.execSQL(CREATE_BOOK);
        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT).show();
    }
            @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

           }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }
}
