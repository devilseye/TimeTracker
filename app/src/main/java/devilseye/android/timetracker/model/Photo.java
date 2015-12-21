package devilseye.android.timetracker.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class Photo {
    int _id;
    byte[] _image;
    Record _record;

    public Photo() {}

    public Photo(int id, byte[] image, Record record){
        this._id=id;
        this._image=image;
        this._record=record;
    }

    public Photo(byte[] image, Record record){
        this._image=image;
        this._record=record;
    }

    public int get_id() {
        return this._id;
    }

    public void set_id(int id) {
        this._id = id;
    }

    public byte[] get_image() {
        return this._image;
    }

    public void set_image(byte[] image) {
        this._image = image;
    }

    public Record get_record() {
        return this._record;
    }

    public void set_record(Record record) {
        this._record = record;
    }

    public static Bitmap bytesToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static byte[] bitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
