package devilseye.android.timetracker.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Record {
    int _id;
    Date _timeStart;
    Date _timeEnd;
    long _timeMinutes;
    String _description;
    Category _category;

    public Record() {}

    public Record(int id, String timeStart, String timeEnd, String description, Category category){
        this._id=id;
        this._timeStart=getDateTime(timeStart);
        this._timeEnd=getDateTime(timeEnd);
        this._timeMinutes=(this._timeEnd.getTime()-this._timeStart.getTime())/60000;
        if (this._timeMinutes<0){
            this._timeEnd=this._timeStart;
            this._timeMinutes=0;
        }
        this._description=description;
        this._category=category;
    }

    public Record(String timeStart, String timeEnd, String description, Category category){
        this._timeStart=getDateTime(timeStart);
        this._timeEnd=getDateTime(timeEnd);
        this._timeMinutes=(this._timeEnd.getTime()-this._timeStart.getTime())/60000;
        if (this._timeMinutes<0){
            this._timeEnd=this._timeStart;
            this._timeMinutes=0;
        }
        this._description=description;
        this._category=category;
    }

    public Record(int id, String timeStart, String timeEnd, long timeMinutes, String description, Category category){
        this._id=id;
        this._timeStart=getDateTime(timeStart);
        this._timeEnd=getDateTime(timeEnd);
        this._timeMinutes=timeMinutes;
        this._description=description;
        this._category=category;
    }

    public static String getStringDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date getDateTime(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String get_description() {
        return this._description;
    }

    public void set_description(String description) {
        this._description = description;
    }

    public long get_timeMinutes() {
        return this._timeMinutes;
    }

    public Category get_category() {
        return this._category;
    }

    public void set_category(Category category) {
        this._category = category;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int id) {
        this._id = id;
    }

    public Date get_timeStart() {
        return this._timeStart;
    }

    public void set_timeStart(Date timeStart) {
        this._timeStart = timeStart;
        if (_timeStart != null && _timeEnd != null) {
            this._timeMinutes = (this._timeEnd.getTime() - this._timeStart.getTime()) / 60000;
            if (this._timeMinutes<0){
                this._timeEnd=this._timeStart;
                this._timeMinutes=0;
            }
        }
    }

    public Date get_timeEnd() {
        return this._timeEnd;
    }

    public void set_timeEnd(Date timeEnd) {
        this._timeEnd = timeEnd;
        if (_timeStart != null && _timeEnd != null) {
            this._timeMinutes = (this._timeEnd.getTime() - this._timeStart.getTime()) / 60000;
            if (this._timeMinutes<0){
                this._timeEnd=this._timeStart;
                this._timeMinutes=0;
            }
        }
    }
}
