package com.macros.agent.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.macros.agent.data.local.entity.UserGoals;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GoalsDao_Impl implements GoalsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserGoals> __insertionAdapterOfUserGoals;

  private final EntityDeletionOrUpdateAdapter<UserGoals> __updateAdapterOfUserGoals;

  public GoalsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserGoals = new EntityInsertionAdapter<UserGoals>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_goals` (`id`,`dailyCalories`,`proteinGrams`,`carbsGrams`,`fatGrams`,`fiberGrams`,`sugarGrams`,`sodiumMg`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserGoals entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDailyCalories());
        statement.bindLong(3, entity.getProteinGrams());
        statement.bindLong(4, entity.getCarbsGrams());
        statement.bindLong(5, entity.getFatGrams());
        statement.bindLong(6, entity.getFiberGrams());
        statement.bindLong(7, entity.getSugarGrams());
        statement.bindLong(8, entity.getSodiumMg());
        statement.bindLong(9, entity.getUpdatedAt());
      }
    };
    this.__updateAdapterOfUserGoals = new EntityDeletionOrUpdateAdapter<UserGoals>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user_goals` SET `id` = ?,`dailyCalories` = ?,`proteinGrams` = ?,`carbsGrams` = ?,`fatGrams` = ?,`fiberGrams` = ?,`sugarGrams` = ?,`sodiumMg` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserGoals entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDailyCalories());
        statement.bindLong(3, entity.getProteinGrams());
        statement.bindLong(4, entity.getCarbsGrams());
        statement.bindLong(5, entity.getFatGrams());
        statement.bindLong(6, entity.getFiberGrams());
        statement.bindLong(7, entity.getSugarGrams());
        statement.bindLong(8, entity.getSodiumMg());
        statement.bindLong(9, entity.getUpdatedAt());
        statement.bindLong(10, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final UserGoals goals, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserGoals.insert(goals);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final UserGoals goals, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserGoals.handle(goals);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserGoals> getGoals() {
    final String _sql = "SELECT * FROM user_goals WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_goals"}, new Callable<UserGoals>() {
      @Override
      @Nullable
      public UserGoals call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyCalories");
          final int _cursorIndexOfProteinGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinGrams");
          final int _cursorIndexOfCarbsGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsGrams");
          final int _cursorIndexOfFatGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "fatGrams");
          final int _cursorIndexOfFiberGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberGrams");
          final int _cursorIndexOfSugarGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarGrams");
          final int _cursorIndexOfSodiumMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumMg");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final UserGoals _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpDailyCalories;
            _tmpDailyCalories = _cursor.getInt(_cursorIndexOfDailyCalories);
            final int _tmpProteinGrams;
            _tmpProteinGrams = _cursor.getInt(_cursorIndexOfProteinGrams);
            final int _tmpCarbsGrams;
            _tmpCarbsGrams = _cursor.getInt(_cursorIndexOfCarbsGrams);
            final int _tmpFatGrams;
            _tmpFatGrams = _cursor.getInt(_cursorIndexOfFatGrams);
            final int _tmpFiberGrams;
            _tmpFiberGrams = _cursor.getInt(_cursorIndexOfFiberGrams);
            final int _tmpSugarGrams;
            _tmpSugarGrams = _cursor.getInt(_cursorIndexOfSugarGrams);
            final int _tmpSodiumMg;
            _tmpSodiumMg = _cursor.getInt(_cursorIndexOfSodiumMg);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserGoals(_tmpId,_tmpDailyCalories,_tmpProteinGrams,_tmpCarbsGrams,_tmpFatGrams,_tmpFiberGrams,_tmpSugarGrams,_tmpSodiumMg,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getGoalsOnce(final Continuation<? super UserGoals> $completion) {
    final String _sql = "SELECT * FROM user_goals WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserGoals>() {
      @Override
      @Nullable
      public UserGoals call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyCalories");
          final int _cursorIndexOfProteinGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "proteinGrams");
          final int _cursorIndexOfCarbsGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "carbsGrams");
          final int _cursorIndexOfFatGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "fatGrams");
          final int _cursorIndexOfFiberGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "fiberGrams");
          final int _cursorIndexOfSugarGrams = CursorUtil.getColumnIndexOrThrow(_cursor, "sugarGrams");
          final int _cursorIndexOfSodiumMg = CursorUtil.getColumnIndexOrThrow(_cursor, "sodiumMg");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final UserGoals _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpDailyCalories;
            _tmpDailyCalories = _cursor.getInt(_cursorIndexOfDailyCalories);
            final int _tmpProteinGrams;
            _tmpProteinGrams = _cursor.getInt(_cursorIndexOfProteinGrams);
            final int _tmpCarbsGrams;
            _tmpCarbsGrams = _cursor.getInt(_cursorIndexOfCarbsGrams);
            final int _tmpFatGrams;
            _tmpFatGrams = _cursor.getInt(_cursorIndexOfFatGrams);
            final int _tmpFiberGrams;
            _tmpFiberGrams = _cursor.getInt(_cursorIndexOfFiberGrams);
            final int _tmpSugarGrams;
            _tmpSugarGrams = _cursor.getInt(_cursorIndexOfSugarGrams);
            final int _tmpSodiumMg;
            _tmpSodiumMg = _cursor.getInt(_cursorIndexOfSodiumMg);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserGoals(_tmpId,_tmpDailyCalories,_tmpProteinGrams,_tmpCarbsGrams,_tmpFatGrams,_tmpFiberGrams,_tmpSugarGrams,_tmpSodiumMg,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
