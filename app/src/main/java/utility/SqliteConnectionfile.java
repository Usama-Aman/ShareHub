package utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteConnectionfile extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Compiit";


    public SqliteConnectionfile(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_MYMATCHES_TABLE = "CREATE TABLE Mymatches ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "comp_id INTEGER, " +
                "comp_name TEXT, " +
                "team_id_1 INTEGER, " +
                "team_id_2 INTEGER, " +
                "team1 TEXT, " +
                "team2 TEXT, " +
                "date TEXT, " +
                "time TEXT, " +
                "match_ID INTEGER, " +
                "winner_name TEXT, " +
                "MatchDraw TEXT, " +
                "individual_team TEXT)";

        // create table
        db.execSQL(CREATE_MYMATCHES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS Mymatches");

        // create fresh books table
        this.onCreate(db);
    }

    // Tables name
    private static final String TABLE_MYMATCHES = "Mymatches";

    // Table Column names
    private static final String Id = "id";
    private static final String CompID = "comp_id";
    private static final String CompName = "comp_name";
    private static final String TeamIdOne = "team_id_1";
    private static final String TeamIdTwo = "team_id_2";
    private static final String TeamOneName = "team1";
    private static final String TeamTwoName = "team2";
    private static final String Date = "date";
    private static final String Time = "time";
    private static final String MatchId = "match_ID";
    private static final String WinnerName = "winner_name";
    private static final String MatchDraw = "MatchDraw";
    private static final String IndividualTeam = "individual_team";


    private static final String[] MYMATCHES_COLUMNS = {Id, CompID, CompName, TeamIdOne, TeamIdTwo
            , TeamOneName, TeamTwoName, Date, Time, MatchId, WinnerName, MatchDraw, IndividualTeam};

    /*public void saveMyMatchesData(MyMatches mymatches) {
        URLogs.d("addMatches", mymatches.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        //values.put(Id, mymatches.getId());
        values.put(CompID, mymatches.getComp_id());
        values.put(CompName, mymatches.getComp_name());
        values.put(TeamIdOne, mymatches.getTeam_id_1());
        values.put(TeamIdTwo, mymatches.getTeam_id_2());
        values.put(TeamOneName, mymatches.getTeam1());
        values.put(TeamTwoName, mymatches.getTeam2());
        values.put(Date, mymatches.getDate());
        values.put(Time, mymatches.getTime());
        values.put(MatchId, mymatches.getMatch_ID());
        values.put(WinnerName, mymatches.getWinner_name());
        values.put(MatchDraw, mymatches.getMatchDraw());
        values.put(IndividualTeam, mymatches.getIndividual_team());

        // 3. insert into the table
        db.insert(TABLE_MYMATCHES, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        *//*db.insertWithOnConflict(TABLE_USER, // table
                null, //nullColumnHack
                values, SQLiteDatabase.CONFLICT_REPLACE);*//*

        // 4. close
        db.close();
    }
*/
    // Get All Books
    /*public List<MyMatches> getMyMatchesData() {
        List<MyMatches> myMatchesList = new LinkedList<MyMatches>();
        String query = "SELECT  * FROM " + TABLE_MYMATCHES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        MyMatches matchData = null;
        if (cursor.moveToFirst()) {
            do {
                matchData = new MyMatches();
                //matchData.setId(cursor.getString(0));
                matchData.setComp_id(cursor.getString(1)); // "user_type TEXT, " +
                matchData.setComp_name(cursor.getString(2));  //  "acount_id TEXT, " +
                matchData.setTeam_id_1(cursor.getString(3));  //  "password TEXT, " +
                matchData.setTeam_id_2(cursor.getString(4)); // "Appcode TEXT, " +
                matchData.setTeam1(cursor.getString(5)); //  "secondary_email TEXT, " +
                matchData.setTeam2(cursor.getString(6)); //  "last_name TEXT, " +
                matchData.setDate(cursor.getString(7)); //  "parent_id TEXT, " +
                matchData.setTime(cursor.getString(8)); //  "change_pass TEXT, " +
                matchData.setMatch_ID(cursor.getString(9)); //  "update_at TEXT, " +
                matchData.setWinner_name(cursor.getString(10)); //   "primary_phone TEXT, " +
                matchData.setMatchDraw(cursor.getString(11)); //   "user_id TEXT, " +
                matchData.setIndividual_team(cursor.getString(12)); //  "group_id TEXT, " +

                myMatchesList.add(matchData);
            } while (cursor.moveToNext());
        }
        URLogs.d("getAllMyMatches()", myMatchesList.toString());

        // return my matches
        return myMatchesList;
    }
*/


}
