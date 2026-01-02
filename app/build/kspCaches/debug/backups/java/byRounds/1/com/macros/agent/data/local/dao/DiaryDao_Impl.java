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
import com.macros.agent.data.local.Converters;
import com.macros.agent.data.local.entity.DiaryEntry;
import com.macros.agent.data.local.entity.MealType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class DiaryDao_Impl implements DiaryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DiaryEntry> __insertionAdapterOfDiaryEntry;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<DiaryEntry> __deletionAdapterOfDiaryEntry;

  private final EntityDeletionOrUpdateAdapter<DiaryEntry> __updateAdapterOfDiaryEntry;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllForDate;

  public DiaryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDiaryEntry = new EntityInsertionAdapter<DiaryEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `diary_entries` (`id`,`date`,`mealType`,`fdcId`,`foodName`,`servingSize`,`servingUnit`,`servingsConsumed`,`calories`,`protein`,`carbs`,`fat`,`timestamp`,`photoUri`,`notes`,`isFromGemini`,`geminiAnalysisId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DiaryEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        final String _tmp = __converters.fromMealType(entity.getMealType());
        statement.bindString(3, _tmp);
        if (entity.getFdcId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getFdcId());
        }
        statement.bindString(5, entity.getFoodName());
        statement.bindDouble(6, entity.getServingSize());
        statement.bindString(7, entity.getServingUnit());
        statement.bindDouble(8, entity.getServingsConsumed());
        statement.bindDouble(9, entity.getCalories());
        statement.bindDouble(10, entity.getProtein());
        statement.bindDouble(11, entity.getCarbs());
        statement.bindDouble(12, entity.getFat());
        statement.bindLong(13, entity.getTimestamp());
        if (entity.getPhotoUri() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getPhotoUri());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getNotes());
        }
        final int _tmp_1 = entity.isFromGemini() ? 1 : 0;
        statement.bindLong(16, _tmp_1);
        if (entity.getGeminiAnalysisId() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getGeminiAnalysisId());
        }
      }
    };
    this.__deletionAdapterOfDiaryEntry = new EntityDeletionOrUpdateAdapter<DiaryEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `diary_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DiaryEntry entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfDiaryEntry = new EntityDeletionOrUpdateAdapter<DiaryEntry>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `diary_entries` SET `id` = ?,`date` = ?,`mealType` = ?,`fdcId` = ?,`foodName` = ?,`servingSize` = ?,`servingUnit` = ?,`servingsConsumed` = ?,`calories` = ?,`protein` = ?,`carbs` = ?,`fat` = ?,`timestamp` = ?,`photoUri` = ?,`notes` = ?,`isFromGemini` = ?,`geminiAnalysisId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DiaryEntry entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        final String _tmp = __converters.fromMealType(entity.getMealType());
        statement.bindString(3, _tmp);
        if (entity.getFdcId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getFdcId());
        }
        statement.bindString(5, entity.getFoodName());
        statement.bindDouble(6, entity.getServingSize());
        statement.bindString(7, entity.getServingUnit());
        statement.bindDouble(8, entity.getServingsConsumed());
        statement.bindDouble(9, entity.getCalories());
        statement.bindDouble(10, entity.getProtein());
        statement.bindDouble(11, entity.getCarbs());
        statement.bindDouble(12, entity.getFat());
        statement.bindLong(13, entity.getTimestamp());
        if (entity.getPhotoUri() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getPhotoUri());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(15);
        } else {
          statement.bindString(15, entity.getNotes());
        }
        final int _tmp_1 = entity.isFromGemini() ? 1 : 0;
        statement.bindLong(16, _tmp_1);
        if (entity.getGeminiAnalysisId() == null) {
          statement.bindNull(17);
        } else {
          statement.bindLong(17, entity.getGeminiAnalysisId());
        }
        statement.bindLong(18, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM diary_entries WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllForDate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM diary_entries WHERE date = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DiaryEntry entry, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDiaryEntry.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<DiaryEntry> entries,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfDiaryEntry.insertAndReturnIdsList(entries);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final DiaryEntry entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDiaryEntry.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DiaryEntry entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDiaryEntry.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllForDate(final String date, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllForDate.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, date);
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
          __preparedStmtOfDeleteAllForDate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super DiaryEntry> $completion) {
    final String _sql = "SELECT * FROM diary_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DiaryEntry>() {
      @Override
      @Nullable
      public DiaryEntry call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "fdcId");
          final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfServingsConsumed = CursorUtil.getColumnIndexOrThrow(_cursor, "servingsConsumed");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUri");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsFromGemini = CursorUtil.getColumnIndexOrThrow(_cursor, "isFromGemini");
          final int _cursorIndexOfGeminiAnalysisId = CursorUtil.getColumnIndexOrThrow(_cursor, "geminiAnalysisId");
          final DiaryEntry _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final MealType _tmpMealType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMealType);
            _tmpMealType = __converters.toMealType(_tmp);
            final Integer _tmpFdcId;
            if (_cursor.isNull(_cursorIndexOfFdcId)) {
              _tmpFdcId = null;
            } else {
              _tmpFdcId = _cursor.getInt(_cursorIndexOfFdcId);
            }
            final String _tmpFoodName;
            _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final float _tmpServingsConsumed;
            _tmpServingsConsumed = _cursor.getFloat(_cursorIndexOfServingsConsumed);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpIsFromGemini;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFromGemini);
            _tmpIsFromGemini = _tmp_1 != 0;
            final Long _tmpGeminiAnalysisId;
            if (_cursor.isNull(_cursorIndexOfGeminiAnalysisId)) {
              _tmpGeminiAnalysisId = null;
            } else {
              _tmpGeminiAnalysisId = _cursor.getLong(_cursorIndexOfGeminiAnalysisId);
            }
            _result = new DiaryEntry(_tmpId,_tmpDate,_tmpMealType,_tmpFdcId,_tmpFoodName,_tmpServingSize,_tmpServingUnit,_tmpServingsConsumed,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpTimestamp,_tmpPhotoUri,_tmpNotes,_tmpIsFromGemini,_tmpGeminiAnalysisId);
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
  public Flow<List<DiaryEntry>> getEntriesForDate(final String date) {
    final String _sql = "SELECT * FROM diary_entries WHERE date = ? ORDER BY mealType, timestamp";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diary_entries"}, new Callable<List<DiaryEntry>>() {
      @Override
      @NonNull
      public List<DiaryEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "fdcId");
          final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfServingsConsumed = CursorUtil.getColumnIndexOrThrow(_cursor, "servingsConsumed");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUri");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsFromGemini = CursorUtil.getColumnIndexOrThrow(_cursor, "isFromGemini");
          final int _cursorIndexOfGeminiAnalysisId = CursorUtil.getColumnIndexOrThrow(_cursor, "geminiAnalysisId");
          final List<DiaryEntry> _result = new ArrayList<DiaryEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DiaryEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final MealType _tmpMealType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMealType);
            _tmpMealType = __converters.toMealType(_tmp);
            final Integer _tmpFdcId;
            if (_cursor.isNull(_cursorIndexOfFdcId)) {
              _tmpFdcId = null;
            } else {
              _tmpFdcId = _cursor.getInt(_cursorIndexOfFdcId);
            }
            final String _tmpFoodName;
            _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final float _tmpServingsConsumed;
            _tmpServingsConsumed = _cursor.getFloat(_cursorIndexOfServingsConsumed);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpIsFromGemini;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFromGemini);
            _tmpIsFromGemini = _tmp_1 != 0;
            final Long _tmpGeminiAnalysisId;
            if (_cursor.isNull(_cursorIndexOfGeminiAnalysisId)) {
              _tmpGeminiAnalysisId = null;
            } else {
              _tmpGeminiAnalysisId = _cursor.getLong(_cursorIndexOfGeminiAnalysisId);
            }
            _item = new DiaryEntry(_tmpId,_tmpDate,_tmpMealType,_tmpFdcId,_tmpFoodName,_tmpServingSize,_tmpServingUnit,_tmpServingsConsumed,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpTimestamp,_tmpPhotoUri,_tmpNotes,_tmpIsFromGemini,_tmpGeminiAnalysisId);
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
  public Flow<List<DiaryEntry>> getEntriesForMeal(final String date, final MealType mealType) {
    final String _sql = "SELECT * FROM diary_entries WHERE date = ? AND mealType = ? ORDER BY timestamp";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    _argIndex = 2;
    final String _tmp = __converters.fromMealType(mealType);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diary_entries"}, new Callable<List<DiaryEntry>>() {
      @Override
      @NonNull
      public List<DiaryEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "fdcId");
          final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfServingsConsumed = CursorUtil.getColumnIndexOrThrow(_cursor, "servingsConsumed");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUri");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsFromGemini = CursorUtil.getColumnIndexOrThrow(_cursor, "isFromGemini");
          final int _cursorIndexOfGeminiAnalysisId = CursorUtil.getColumnIndexOrThrow(_cursor, "geminiAnalysisId");
          final List<DiaryEntry> _result = new ArrayList<DiaryEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DiaryEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final MealType _tmpMealType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfMealType);
            _tmpMealType = __converters.toMealType(_tmp_1);
            final Integer _tmpFdcId;
            if (_cursor.isNull(_cursorIndexOfFdcId)) {
              _tmpFdcId = null;
            } else {
              _tmpFdcId = _cursor.getInt(_cursorIndexOfFdcId);
            }
            final String _tmpFoodName;
            _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final float _tmpServingsConsumed;
            _tmpServingsConsumed = _cursor.getFloat(_cursorIndexOfServingsConsumed);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpIsFromGemini;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsFromGemini);
            _tmpIsFromGemini = _tmp_2 != 0;
            final Long _tmpGeminiAnalysisId;
            if (_cursor.isNull(_cursorIndexOfGeminiAnalysisId)) {
              _tmpGeminiAnalysisId = null;
            } else {
              _tmpGeminiAnalysisId = _cursor.getLong(_cursorIndexOfGeminiAnalysisId);
            }
            _item = new DiaryEntry(_tmpId,_tmpDate,_tmpMealType,_tmpFdcId,_tmpFoodName,_tmpServingSize,_tmpServingUnit,_tmpServingsConsumed,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpTimestamp,_tmpPhotoUri,_tmpNotes,_tmpIsFromGemini,_tmpGeminiAnalysisId);
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
  public Flow<DailyTotals> getDailyTotals(final String date) {
    final String _sql = "\n"
            + "        SELECT \n"
            + "            COALESCE(SUM(calories), 0) as totalCalories,\n"
            + "            COALESCE(SUM(protein), 0) as totalProtein,\n"
            + "            COALESCE(SUM(carbs), 0) as totalCarbs,\n"
            + "            COALESCE(SUM(fat), 0) as totalFat\n"
            + "        FROM diary_entries \n"
            + "        WHERE date = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diary_entries"}, new Callable<DailyTotals>() {
      @Override
      @NonNull
      public DailyTotals call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTotalCalories = 0;
          final int _cursorIndexOfTotalProtein = 1;
          final int _cursorIndexOfTotalCarbs = 2;
          final int _cursorIndexOfTotalFat = 3;
          final DailyTotals _result;
          if (_cursor.moveToFirst()) {
            final float _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
            final float _tmpTotalProtein;
            _tmpTotalProtein = _cursor.getFloat(_cursorIndexOfTotalProtein);
            final float _tmpTotalCarbs;
            _tmpTotalCarbs = _cursor.getFloat(_cursorIndexOfTotalCarbs);
            final float _tmpTotalFat;
            _tmpTotalFat = _cursor.getFloat(_cursorIndexOfTotalFat);
            _result = new DailyTotals(_tmpTotalCalories,_tmpTotalProtein,_tmpTotalCarbs,_tmpTotalFat);
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
  public Flow<DailyTotals> getMealTotals(final String date, final MealType mealType) {
    final String _sql = "\n"
            + "        SELECT \n"
            + "            COALESCE(SUM(calories), 0) as totalCalories,\n"
            + "            COALESCE(SUM(protein), 0) as totalProtein,\n"
            + "            COALESCE(SUM(carbs), 0) as totalCarbs,\n"
            + "            COALESCE(SUM(fat), 0) as totalFat\n"
            + "        FROM diary_entries \n"
            + "        WHERE date = ? AND mealType = ?\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    _argIndex = 2;
    final String _tmp = __converters.fromMealType(mealType);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diary_entries"}, new Callable<DailyTotals>() {
      @Override
      @NonNull
      public DailyTotals call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfTotalCalories = 0;
          final int _cursorIndexOfTotalProtein = 1;
          final int _cursorIndexOfTotalCarbs = 2;
          final int _cursorIndexOfTotalFat = 3;
          final DailyTotals _result;
          if (_cursor.moveToFirst()) {
            final float _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
            final float _tmpTotalProtein;
            _tmpTotalProtein = _cursor.getFloat(_cursorIndexOfTotalProtein);
            final float _tmpTotalCarbs;
            _tmpTotalCarbs = _cursor.getFloat(_cursorIndexOfTotalCarbs);
            final float _tmpTotalFat;
            _tmpTotalFat = _cursor.getFloat(_cursorIndexOfTotalFat);
            _result = new DailyTotals(_tmpTotalCalories,_tmpTotalProtein,_tmpTotalCarbs,_tmpTotalFat);
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
  public Object getRecentDates(final int limit,
      final Continuation<? super List<String>> $completion) {
    final String _sql = "SELECT DISTINCT date FROM diary_entries ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
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
  public Flow<List<DiaryEntry>> getEntriesInRange(final String startDate, final String endDate) {
    final String _sql = "SELECT * FROM diary_entries WHERE date BETWEEN ? AND ? ORDER BY date, mealType, timestamp";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diary_entries"}, new Callable<List<DiaryEntry>>() {
      @Override
      @NonNull
      public List<DiaryEntry> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfFdcId = CursorUtil.getColumnIndexOrThrow(_cursor, "fdcId");
          final int _cursorIndexOfFoodName = CursorUtil.getColumnIndexOrThrow(_cursor, "foodName");
          final int _cursorIndexOfServingSize = CursorUtil.getColumnIndexOrThrow(_cursor, "servingSize");
          final int _cursorIndexOfServingUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "servingUnit");
          final int _cursorIndexOfServingsConsumed = CursorUtil.getColumnIndexOrThrow(_cursor, "servingsConsumed");
          final int _cursorIndexOfCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "calories");
          final int _cursorIndexOfProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "protein");
          final int _cursorIndexOfCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "carbs");
          final int _cursorIndexOfFat = CursorUtil.getColumnIndexOrThrow(_cursor, "fat");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfPhotoUri = CursorUtil.getColumnIndexOrThrow(_cursor, "photoUri");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfIsFromGemini = CursorUtil.getColumnIndexOrThrow(_cursor, "isFromGemini");
          final int _cursorIndexOfGeminiAnalysisId = CursorUtil.getColumnIndexOrThrow(_cursor, "geminiAnalysisId");
          final List<DiaryEntry> _result = new ArrayList<DiaryEntry>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DiaryEntry _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final MealType _tmpMealType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfMealType);
            _tmpMealType = __converters.toMealType(_tmp);
            final Integer _tmpFdcId;
            if (_cursor.isNull(_cursorIndexOfFdcId)) {
              _tmpFdcId = null;
            } else {
              _tmpFdcId = _cursor.getInt(_cursorIndexOfFdcId);
            }
            final String _tmpFoodName;
            _tmpFoodName = _cursor.getString(_cursorIndexOfFoodName);
            final float _tmpServingSize;
            _tmpServingSize = _cursor.getFloat(_cursorIndexOfServingSize);
            final String _tmpServingUnit;
            _tmpServingUnit = _cursor.getString(_cursorIndexOfServingUnit);
            final float _tmpServingsConsumed;
            _tmpServingsConsumed = _cursor.getFloat(_cursorIndexOfServingsConsumed);
            final float _tmpCalories;
            _tmpCalories = _cursor.getFloat(_cursorIndexOfCalories);
            final float _tmpProtein;
            _tmpProtein = _cursor.getFloat(_cursorIndexOfProtein);
            final float _tmpCarbs;
            _tmpCarbs = _cursor.getFloat(_cursorIndexOfCarbs);
            final float _tmpFat;
            _tmpFat = _cursor.getFloat(_cursorIndexOfFat);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpPhotoUri;
            if (_cursor.isNull(_cursorIndexOfPhotoUri)) {
              _tmpPhotoUri = null;
            } else {
              _tmpPhotoUri = _cursor.getString(_cursorIndexOfPhotoUri);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            final boolean _tmpIsFromGemini;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFromGemini);
            _tmpIsFromGemini = _tmp_1 != 0;
            final Long _tmpGeminiAnalysisId;
            if (_cursor.isNull(_cursorIndexOfGeminiAnalysisId)) {
              _tmpGeminiAnalysisId = null;
            } else {
              _tmpGeminiAnalysisId = _cursor.getLong(_cursorIndexOfGeminiAnalysisId);
            }
            _item = new DiaryEntry(_tmpId,_tmpDate,_tmpMealType,_tmpFdcId,_tmpFoodName,_tmpServingSize,_tmpServingUnit,_tmpServingsConsumed,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpTimestamp,_tmpPhotoUri,_tmpNotes,_tmpIsFromGemini,_tmpGeminiAnalysisId);
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
  public Flow<List<DateCalorie>> getCalorieHistory(final String startDate, final String endDate) {
    final String _sql = "\n"
            + "        SELECT date, SUM(calories) as totalCalories\n"
            + "        FROM diary_entries \n"
            + "        WHERE date BETWEEN ? AND ?\n"
            + "        GROUP BY date\n"
            + "        ORDER BY date ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindString(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"diary_entries"}, new Callable<List<DateCalorie>>() {
      @Override
      @NonNull
      public List<DateCalorie> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfDate = 0;
          final int _cursorIndexOfTotalCalories = 1;
          final List<DateCalorie> _result = new ArrayList<DateCalorie>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DateCalorie _item;
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final float _tmpTotalCalories;
            _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
            _item = new DateCalorie(_tmpDate,_tmpTotalCalories);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
