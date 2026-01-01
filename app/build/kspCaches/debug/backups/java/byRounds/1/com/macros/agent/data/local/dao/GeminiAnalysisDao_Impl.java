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
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.macros.agent.data.local.entity.GeminiAnalysis;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class GeminiAnalysisDao_Impl implements GeminiAnalysisDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<GeminiAnalysis> __insertionAdapterOfGeminiAnalysis;

  private final EntityDeletionOrUpdateAdapter<GeminiAnalysis> __updateAdapterOfGeminiAnalysis;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  public GeminiAnalysisDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfGeminiAnalysis = new EntityInsertionAdapter<GeminiAnalysis>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `gemini_analyses` (`id`,`photoUri`,`timestamp`,`rawResponse`,`detectedFoodsJson`,`totalCalories`,`totalProtein`,`totalCarbs`,`totalFat`,`confidence`,`wasAccepted`,`wasModified`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GeminiAnalysis entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPhotoUri());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindString(4, entity.getRawResponse());
        statement.bindString(5, entity.getDetectedFoodsJson());
        statement.bindDouble(6, entity.getTotalCalories());
        statement.bindDouble(7, entity.getTotalProtein());
        statement.bindDouble(8, entity.getTotalCarbs());
        statement.bindDouble(9, entity.getTotalFat());
        statement.bindDouble(10, entity.getConfidence());
        final int _tmp = entity.getWasAccepted() ? 1 : 0;
        statement.bindLong(11, _tmp);
        final int _tmp_1 = entity.getWasModified() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
      }
    };
    this.__updateAdapterOfGeminiAnalysis = new EntityDeletionOrUpdateAdapter<GeminiAnalysis>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `gemini_analyses` SET `id` = ?,`photoUri` = ?,`timestamp` = ?,`rawResponse` = ?,`detectedFoodsJson` = ?,`totalCalories` = ?,`totalProtein` = ?,`totalCarbs` = ?,`totalFat` = ?,`confidence` = ?,`wasAccepted` = ?,`wasModified` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GeminiAnalysis entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPhotoUri());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindString(4, entity.getRawResponse());
        statement.bindString(5, entity.getDetectedFoodsJson());
        statement.bindDouble(6, entity.getTotalCalories());
        statement.bindDouble(7, entity.getTotalProtein());
        statement.bindDouble(8, entity.getTotalCarbs());
        statement.bindDouble(9, entity.getTotalFat());
        statement.bindDouble(10, entity.getConfidence());
        final int _tmp = entity.getWasAccepted() ? 1 : 0;
        statement.bindLong(11, _tmp);
        final int _tmp_1 = entity.getWasModified() ? 1 : 0;
        statement.bindLong(12, _tmp_1);
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM gemini_analyses WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final GeminiAnalysis analysis,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfGeminiAnalysis.insertAndReturnId(analysis);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final GeminiAnalysis analysis,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfGeminiAnalysis.handle(analysis);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOlderThan(final long before, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, before);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super GeminiAnalysis> $completion) {
    final String _sql = "SELECT * FROM gemini_analyses WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GeminiAnalysis>() {
      @Override
      @Nullable
      public GeminiAnalysis call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUri");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRawResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "rawResponse");
          final int _cursorIndexOfDetectedFoodsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedFoodsJson");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfTotalProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProtein");
          final int _cursorIndexOfTotalCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbs");
          final int _cursorIndexOfTotalFat = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFat");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfWasAccepted = CursorUtil.getColumnIndexOrThrow(_cursor, "wasAccepted");
          final int _cursorIndexOfWasModified = CursorUtil.getColumnIndexOrThrow(_cursor, "wasModified");
          final GeminiAnalysis _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhotoUri;
            _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpRawResponse;
            _tmpRawResponse = _cursor.getString(_cursorIndexOfRawResponse);
            final String _tmpDetectedFoodsJson;
            _tmpDetectedFoodsJson = _cursor.getString(_cursorIndexOfDetectedFoodsJson);
            final float _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
            final float _tmpTotalProtein;
            _tmpTotalProtein = _cursor.getFloat(_cursorIndexOfTotalProtein);
            final float _tmpTotalCarbs;
            _tmpTotalCarbs = _cursor.getFloat(_cursorIndexOfTotalCarbs);
            final float _tmpTotalFat;
            _tmpTotalFat = _cursor.getFloat(_cursorIndexOfTotalFat);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final boolean _tmpWasAccepted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasAccepted);
            _tmpWasAccepted = _tmp != 0;
            final boolean _tmpWasModified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfWasModified);
            _tmpWasModified = _tmp_1 != 0;
            _result = new GeminiAnalysis(_tmpId,_tmpPhotoUri,_tmpTimestamp,_tmpRawResponse,_tmpDetectedFoodsJson,_tmpTotalCalories,_tmpTotalProtein,_tmpTotalCarbs,_tmpTotalFat,_tmpConfidence,_tmpWasAccepted,_tmpWasModified);
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

  @Override
  public Flow<List<GeminiAnalysis>> getRecent(final int limit) {
    final String _sql = "SELECT * FROM gemini_analyses ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"gemini_analyses"}, new Callable<List<GeminiAnalysis>>() {
      @Override
      @NonNull
      public List<GeminiAnalysis> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUri");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRawResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "rawResponse");
          final int _cursorIndexOfDetectedFoodsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedFoodsJson");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfTotalProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProtein");
          final int _cursorIndexOfTotalCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbs");
          final int _cursorIndexOfTotalFat = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFat");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfWasAccepted = CursorUtil.getColumnIndexOrThrow(_cursor, "wasAccepted");
          final int _cursorIndexOfWasModified = CursorUtil.getColumnIndexOrThrow(_cursor, "wasModified");
          final List<GeminiAnalysis> _result = new ArrayList<GeminiAnalysis>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final GeminiAnalysis _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhotoUri;
            _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpRawResponse;
            _tmpRawResponse = _cursor.getString(_cursorIndexOfRawResponse);
            final String _tmpDetectedFoodsJson;
            _tmpDetectedFoodsJson = _cursor.getString(_cursorIndexOfDetectedFoodsJson);
            final float _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
            final float _tmpTotalProtein;
            _tmpTotalProtein = _cursor.getFloat(_cursorIndexOfTotalProtein);
            final float _tmpTotalCarbs;
            _tmpTotalCarbs = _cursor.getFloat(_cursorIndexOfTotalCarbs);
            final float _tmpTotalFat;
            _tmpTotalFat = _cursor.getFloat(_cursorIndexOfTotalFat);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final boolean _tmpWasAccepted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasAccepted);
            _tmpWasAccepted = _tmp != 0;
            final boolean _tmpWasModified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfWasModified);
            _tmpWasModified = _tmp_1 != 0;
            _item = new GeminiAnalysis(_tmpId,_tmpPhotoUri,_tmpTimestamp,_tmpRawResponse,_tmpDetectedFoodsJson,_tmpTotalCalories,_tmpTotalProtein,_tmpTotalCarbs,_tmpTotalFat,_tmpConfidence,_tmpWasAccepted,_tmpWasModified);
            _result.add(_item);
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
  public Object getByPhotoUri(final String photoUri,
      final Continuation<? super GeminiAnalysis> $completion) {
    final String _sql = "SELECT * FROM gemini_analyses WHERE photoUri = ? ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, photoUri);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<GeminiAnalysis>() {
      @Override
      @Nullable
      public GeminiAnalysis call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUri");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfRawResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "rawResponse");
          final int _cursorIndexOfDetectedFoodsJson = CursorUtil.getColumnIndexOrThrow(_cursor, "detectedFoodsJson");
          final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
          final int _cursorIndexOfTotalProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProtein");
          final int _cursorIndexOfTotalCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbs");
          final int _cursorIndexOfTotalFat = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFat");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfWasAccepted = CursorUtil.getColumnIndexOrThrow(_cursor, "wasAccepted");
          final int _cursorIndexOfWasModified = CursorUtil.getColumnIndexOrThrow(_cursor, "wasModified");
          final GeminiAnalysis _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhotoUri;
            _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpRawResponse;
            _tmpRawResponse = _cursor.getString(_cursorIndexOfRawResponse);
            final String _tmpDetectedFoodsJson;
            _tmpDetectedFoodsJson = _cursor.getString(_cursorIndexOfDetectedFoodsJson);
            final float _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
            final float _tmpTotalProtein;
            _tmpTotalProtein = _cursor.getFloat(_cursorIndexOfTotalProtein);
            final float _tmpTotalCarbs;
            _tmpTotalCarbs = _cursor.getFloat(_cursorIndexOfTotalCarbs);
            final float _tmpTotalFat;
            _tmpTotalFat = _cursor.getFloat(_cursorIndexOfTotalFat);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final boolean _tmpWasAccepted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfWasAccepted);
            _tmpWasAccepted = _tmp != 0;
            final boolean _tmpWasModified;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfWasModified);
            _tmpWasModified = _tmp_1 != 0;
            _result = new GeminiAnalysis(_tmpId,_tmpPhotoUri,_tmpTimestamp,_tmpRawResponse,_tmpDetectedFoodsJson,_tmpTotalCalories,_tmpTotalProtein,_tmpTotalCarbs,_tmpTotalFat,_tmpConfidence,_tmpWasAccepted,_tmpWasModified);
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
