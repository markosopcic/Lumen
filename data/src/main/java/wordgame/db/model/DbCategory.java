package wordgame.db.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "categories",indices = @Index(value = "id"))
public class DbCategory {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    public int id;

    @NonNull
    @ColumnInfo(name = "category")
    public String value;


    public DbCategory(String value) {
        this.value = value;
    }
}
