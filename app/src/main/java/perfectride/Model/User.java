package perfectride.Model;

public class User {

    //private variables
    int _id;
    String _fname;
    String _lname;
    String _photo;
    String _cardNum;
    String _expiry;

    public User(int _id, String _fname, String _lname, String _photo, String _cardNum, String _expiry) {
        this._id = _id;
        this._fname = _fname;
        this._lname = _lname;
        this._photo = _photo;
        this._cardNum = _cardNum;
        this._expiry = _expiry;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_fname() {
        return _fname;
    }

    public void set_fname(String _fname) {
        this._fname = _fname;
    }

    public String get_lname() {
        return _lname;
    }

    public void set_lname(String _lname) {
        this._lname = _lname;
    }

    public String get_photo() {
        return _photo;
    }

    public void set_photo(String _photo) {
        this._photo = _photo;
    }

    public String get_cardNum() {
        return _cardNum;
    }

    public void set_cardNum(String _cardNum) {
        this._cardNum = _cardNum;
    }

    public String get_expiry() {
        return _expiry;
    }

    public void set_expiry(String _expiry) {
        this._expiry = _expiry;
    }


}