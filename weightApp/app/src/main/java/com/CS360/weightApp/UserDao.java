package com.CS360.weightApp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.annotation.NonNull;

// this handles user database operations
@Dao
public interface UserDao {
    @Insert
    long insertUser(User user);

    // this checks login credentials
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User getUser(String username);

    // this finds user by username
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    // this checks if username exists
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    boolean usernameExists(String username);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    User findUserByUsername(String username);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int countUsersByUsername(String username);
}