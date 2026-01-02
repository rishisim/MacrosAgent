package com.macros.agent.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.macros.agent.data.local.entity.UserMeal;
import com.macros.agent.data.local.entity.UserMealItem;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class UserMealDao_Impl implements UserMealDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserMeal> __insertionAdapterOfUserMeal;

  private final EntityInsertionAdapter<UserMealItem> __insertionAdapterOfUserMealItem;

  private final EntityDeletionOrUpdateAdapter<UserMeal> __deletionAdapterOfUserMeal;

  private final SharedSQLiteStatement __preparedStmtOfDeleteItemsForMeal;

  public UserMealDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserMeal = new EntityInsertionAdapter<UserMeal>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_meals` (`id`,`name`,`totalCalories`,`totalProtein`,`totalCarbs`,`totalFat`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserMeal entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getTotalCalories());
        statement.bindDouble(4, entity.getTotalProtein());
        statement.bindDouble(5, entity.getTotalCarbs());
        statement.bindDouble(6, entity.getTotalFat());
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfUserMealItem = new EntityInsertionAdapter<UserMealItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_meal_items` (`id`,`mealId`,`foodName`,`servingSize`,`servingUnit`,`servingsConsumed`,`calories`,`protein`,`carbs`,`fat`,`fdcId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserMealItem entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMealId());
        statement.bindString(3, entity.getFoodName());
        statement.bindDouble(4, entity.getServingSize());
        statement.bindString(5, entity.getServingUnit());
        statement.bindDouble(6, entity.getServingsConsumed());
        statement.bindDouble(7, entity.getCalories());
        statement.bindDouble(8, entity.getProtein());
        statement.bindDouble(9, entity.getCarbs());
        statement.bindDouble(10, entity.getFat());
        if (entity.getFdcId() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getFdcId());
        }
      }
    };
    this.__deletionAdapterOfUserMeal = new EntityDeletionOrUpdateAdapter<UserMeal>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `user_meals` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserMeal entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteItemsForMeal = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM user_meal_items WHERE mealId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMeal(final UserMeal meal, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfUserMeal.insertAndReturnId(meal);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertItems(final List<UserMealItem> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserMealItem.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMeal(final UserMeal meal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfUserMeal.handle(meal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteItemsForMeal(final long mealId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteItemsForMeal.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, mealId);
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
          __preparedStmtOfDeleteItemsForMeal.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<UserMealWithItems>> getAllMealsWithItems() {
    final String _sql = "SELECT * FROM user_meals";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"user_meal_items",
        "user_meals"}, new Callable<List<UserMealWithItems>>() {
      @Override
      @NonNull
      public List<UserMealWithItems> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
            final int _cursorIndexOfTotalProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProtein");
            final int _cursorIndexOfTotalCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbs");
            final int _cursorIndexOfTotalFat = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFat");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final LongSparseArray<ArrayList<UserMealItem>> _collectionItems = new LongSparseArray<ArrayList<UserMealItem>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionItems.containsKey(_tmpKey)) {
                _collectionItems.put(_tmpKey, new ArrayList<UserMealItem>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipuserMealItemsAscomMacrosAgentDataLocalEntityUserMealItem(_collectionItems);
            final List<UserMealWithItems> _result = new ArrayList<UserMealWithItems>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final UserMealWithItems _item;
              final UserMeal _tmpMeal;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final float _tmpTotalCalories;
              _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
              final float _tmpTotalProtein;
              _tmpTotalProtein = _cursor.getFloat(_cursorIndexOfTotalProtein);
              final float _tmpTotalCarbs;
              _tmpTotalCarbs = _cursor.getFloat(_cursorIndexOfTotalCarbs);
              final float _tmpTotalFat;
              _tmpTotalFat = _cursor.getFloat(_cursorIndexOfTotalFat);
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpMeal = new UserMeal(_tmpId,_tmpName,_tmpTotalCalories,_tmpTotalProtein,_tmpTotalCarbs,_tmpTotalFat,_tmpCreatedAt);
              final ArrayList<UserMealItem> _tmpItemsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpItemsCollection = _collectionItems.get(_tmpKey_1);
              _item = new UserMealWithItems(_tmpMeal,_tmpItemsCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getMealWithItems(final long id,
      final Continuation<? super UserMealWithItems> $completion) {
    final String _sql = "SELECT * FROM user_meals WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, true, _cancellationSignal, new Callable<UserMealWithItems>() {
      @Override
      @Nullable
      public UserMealWithItems call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
            final int _cursorIndexOfTotalCalories = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCalories");
            final int _cursorIndexOfTotalProtein = CursorUtil.getColumnIndexOrThrow(_cursor, "totalProtein");
            final int _cursorIndexOfTotalCarbs = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCarbs");
            final int _cursorIndexOfTotalFat = CursorUtil.getColumnIndexOrThrow(_cursor, "totalFat");
            final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
            final LongSparseArray<ArrayList<UserMealItem>> _collectionItems = new LongSparseArray<ArrayList<UserMealItem>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionItems.containsKey(_tmpKey)) {
                _collectionItems.put(_tmpKey, new ArrayList<UserMealItem>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipuserMealItemsAscomMacrosAgentDataLocalEntityUserMealItem(_collectionItems);
            final UserMealWithItems _result;
            if (_cursor.moveToFirst()) {
              final UserMeal _tmpMeal;
              final long _tmpId;
              _tmpId = _cursor.getLong(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final float _tmpTotalCalories;
              _tmpTotalCalories = _cursor.getFloat(_cursorIndexOfTotalCalories);
              final float _tmpTotalProtein;
              _tmpTotalProtein = _cursor.getFloat(_cursorIndexOfTotalProtein);
              final float _tmpTotalCarbs;
              _tmpTotalCarbs = _cursor.getFloat(_cursorIndexOfTotalCarbs);
              final float _tmpTotalFat;
              _tmpTotalFat = _cursor.getFloat(_cursorIndexOfTotalFat);
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              _tmpMeal = new UserMeal(_tmpId,_tmpName,_tmpTotalCalories,_tmpTotalProtein,_tmpTotalCarbs,_tmpTotalFat,_tmpCreatedAt);
              final ArrayList<UserMealItem> _tmpItemsCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpItemsCollection = _collectionItems.get(_tmpKey_1);
              _result = new UserMealWithItems(_tmpMeal,_tmpItemsCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
            _statement.release();
          }
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipuserMealItemsAscomMacrosAgentDataLocalEntityUserMealItem(
      @NonNull final LongSparseArray<ArrayList<UserMealItem>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipuserMealItemsAscomMacrosAgentDataLocalEntityUserMealItem(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`mealId`,`foodName`,`servingSize`,`servingUnit`,`servingsConsumed`,`calories`,`protein`,`carbs`,`fat`,`fdcId` FROM `user_meal_items` WHERE `mealId` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "mealId");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfMealId = 1;
      final int _cursorIndexOfFoodName = 2;
      final int _cursorIndexOfServingSize = 3;
      final int _cursorIndexOfServingUnit = 4;
      final int _cursorIndexOfServingsConsumed = 5;
      final int _cursorIndexOfCalories = 6;
      final int _cursorIndexOfProtein = 7;
      final int _cursorIndexOfCarbs = 8;
      final int _cursorIndexOfFat = 9;
      final int _cursorIndexOfFdcId = 10;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<UserMealItem> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final UserMealItem _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpMealId;
          _tmpMealId = _cursor.getLong(_cursorIndexOfMealId);
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
          final Integer _tmpFdcId;
          if (_cursor.isNull(_cursorIndexOfFdcId)) {
            _tmpFdcId = null;
          } else {
            _tmpFdcId = _cursor.getInt(_cursorIndexOfFdcId);
          }
          _item_1 = new UserMealItem(_tmpId,_tmpMealId,_tmpFoodName,_tmpServingSize,_tmpServingUnit,_tmpServingsConsumed,_tmpCalories,_tmpProtein,_tmpCarbs,_tmpFat,_tmpFdcId);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
