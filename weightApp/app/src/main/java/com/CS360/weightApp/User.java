package com.CS360.weightApp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import androidx.annotation.NonNull;

// this is the user entity for the database
@Entity(tableName = "users", indices = {@Index(value = {"username"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "username")
    @NonNull
    public String username;

    @ColumnInfo(name = "password")
    @NonNull
    public String password;

    // this constructor creates a new user
    public User(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
    }
}