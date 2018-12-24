package JavaNavtive;



public class Config {


    static public native int nativa_Deal(long src,long dst);

    static {
        System.loadLibrary("MyCppNative");
        System.loadLibrary("opencv_java");

    }
}
