package com.macros.agent.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.macros.agent.data.local.dao.DiaryDao;
import com.macros.agent.data.local.dao.DiaryDao_Impl;
import com.macros.agent.data.local.dao.ExerciseDao;
import com.macros.agent.data.local.dao.ExerciseDao_Impl;
import com.macros.agent.data.local.dao.FoodDao;
import com.macros.agent.data.local.dao.FoodDao_Impl;
import com.macros.agent.data.local.dao.GeminiAnalysisDao;
import com.macros.agent.data.local.dao.GeminiAnalysisDao_Impl;
import com.macros.agent.data.local.dao.GoalsDao;
import com.macros.agent.data.local.dao.GoalsDao_Impl;
import com.macros.agent.data.local.dao.UserMealDao;
import com.macros.agent.data.local.dao.UserMealDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MacrosDatabase_Impl extends MacrosDatabase {
  private volatile FoodDao _foodDao;

  private volatile DiaryDao _diaryDao;

  private volatile GoalsDao _goalsDao;

  private volatile ExerciseDao _exerciseDao;

  private volatile GeminiAnalysisDao _geminiAnalysisDao;

  private volatile UserMealDao _userMealDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `foods` (`fdcId` INTEGER NOT NULL, `description` TEXT NOT NULL, `brandOwner` TEXT, `brandName` TEXT, `calories` REAL NOT NULL, `protein` REAL NOT NULL, `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `fiber` REAL NOT NULL, `sugar` REAL NOT NULL, `sodium` REAL NOT NULL, `servingSize` REAL NOT NULL, `servingUnit` TEXT NOT NULL, `category` TEXT, `ingredients` TEXT, `barcode` TEXT, `lastUsed` INTEGER NOT NULL, `useCount` INTEGER NOT NULL, `isFavorite` INTEGER NOT NULL, `isCustom` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`fdcId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `diary_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `mealType` TEXT NOT NULL, `fdcId` INTEGER, `foodName` TEXT NOT NULL, `servingSize` REAL NOT NULL, `servingUnit` TEXT NOT NULL, `servingsConsumed` REAL NOT NULL, `calories` REAL NOT NULL, `protein` REAL NOT NULL, `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `timestamp` INTEGER NOT NULL, `photoUri` TEXT, `notes` TEXT, `isFromGemini` INTEGER NOT NULL, `geminiAnalysisId` INTEGER, FOREIGN KEY(`fdcId`) REFERENCES `foods`(`fdcId`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_diary_entries_date` ON `diary_entries` (`date`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_diary_entries_fdcId` ON `diary_entries` (`fdcId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_diary_entries_mealType` ON `diary_entries` (`mealType`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `gemini_analyses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `photoUri` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `rawResponse` TEXT NOT NULL, `detectedFoodsJson` TEXT NOT NULL, `totalCalories` REAL NOT NULL, `totalProtein` REAL NOT NULL, `totalCarbs` REAL NOT NULL, `totalFat` REAL NOT NULL, `confidence` REAL NOT NULL, `wasAccepted` INTEGER NOT NULL, `wasModified` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_goals` (`id` INTEGER NOT NULL, `dailyCalories` INTEGER NOT NULL, `proteinGrams` INTEGER NOT NULL, `carbsGrams` INTEGER NOT NULL, `fatGrams` INTEGER NOT NULL, `fiberGrams` INTEGER NOT NULL, `sugarGrams` INTEGER NOT NULL, `sodiumMg` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `exercise_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `source` TEXT NOT NULL, `activityName` TEXT NOT NULL, `caloriesBurned` INTEGER NOT NULL, `durationMinutes` INTEGER, `steps` INTEGER, `timestamp` INTEGER NOT NULL, `googleFitActivityId` TEXT, `notes` TEXT)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_exercise_entries_date` ON `exercise_entries` (`date`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_meals` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `totalCalories` REAL NOT NULL, `totalProtein` REAL NOT NULL, `totalCarbs` REAL NOT NULL, `totalFat` REAL NOT NULL, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_meal_items` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `mealId` INTEGER NOT NULL, `foodName` TEXT NOT NULL, `servingSize` REAL NOT NULL, `servingUnit` TEXT NOT NULL, `servingsConsumed` REAL NOT NULL, `calories` REAL NOT NULL, `protein` REAL NOT NULL, `carbs` REAL NOT NULL, `fat` REAL NOT NULL, `fdcId` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4d92d7600a7d4ebf534b2d52b81f815a')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `foods`");
        db.execSQL("DROP TABLE IF EXISTS `diary_entries`");
        db.execSQL("DROP TABLE IF EXISTS `gemini_analyses`");
        db.execSQL("DROP TABLE IF EXISTS `user_goals`");
        db.execSQL("DROP TABLE IF EXISTS `exercise_entries`");
        db.execSQL("DROP TABLE IF EXISTS `user_meals`");
        db.execSQL("DROP TABLE IF EXISTS `user_meal_items`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsFoods = new HashMap<String, TableInfo.Column>(21);
        _columnsFoods.put("fdcId", new TableInfo.Column("fdcId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("brandOwner", new TableInfo.Column("brandOwner", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("brandName", new TableInfo.Column("brandName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("calories", new TableInfo.Column("calories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("carbs", new TableInfo.Column("carbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("fiber", new TableInfo.Column("fiber", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("sugar", new TableInfo.Column("sugar", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("sodium", new TableInfo.Column("sodium", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("servingSize", new TableInfo.Column("servingSize", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("servingUnit", new TableInfo.Column("servingUnit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("category", new TableInfo.Column("category", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("ingredients", new TableInfo.Column("ingredients", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("barcode", new TableInfo.Column("barcode", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("lastUsed", new TableInfo.Column("lastUsed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("useCount", new TableInfo.Column("useCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("isCustom", new TableInfo.Column("isCustom", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFoods.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFoods = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFoods = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFoods = new TableInfo("foods", _columnsFoods, _foreignKeysFoods, _indicesFoods);
        final TableInfo _existingFoods = TableInfo.read(db, "foods");
        if (!_infoFoods.equals(_existingFoods)) {
          return new RoomOpenHelper.ValidationResult(false, "foods(com.macros.agent.data.local.entity.Food).\n"
                  + " Expected:\n" + _infoFoods + "\n"
                  + " Found:\n" + _existingFoods);
        }
        final HashMap<String, TableInfo.Column> _columnsDiaryEntries = new HashMap<String, TableInfo.Column>(17);
        _columnsDiaryEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("mealType", new TableInfo.Column("mealType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("fdcId", new TableInfo.Column("fdcId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("foodName", new TableInfo.Column("foodName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("servingSize", new TableInfo.Column("servingSize", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("servingUnit", new TableInfo.Column("servingUnit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("servingsConsumed", new TableInfo.Column("servingsConsumed", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("calories", new TableInfo.Column("calories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("carbs", new TableInfo.Column("carbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("photoUri", new TableInfo.Column("photoUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("isFromGemini", new TableInfo.Column("isFromGemini", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiaryEntries.put("geminiAnalysisId", new TableInfo.Column("geminiAnalysisId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDiaryEntries = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDiaryEntries.add(new TableInfo.ForeignKey("foods", "SET NULL", "NO ACTION", Arrays.asList("fdcId"), Arrays.asList("fdcId")));
        final HashSet<TableInfo.Index> _indicesDiaryEntries = new HashSet<TableInfo.Index>(3);
        _indicesDiaryEntries.add(new TableInfo.Index("index_diary_entries_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        _indicesDiaryEntries.add(new TableInfo.Index("index_diary_entries_fdcId", false, Arrays.asList("fdcId"), Arrays.asList("ASC")));
        _indicesDiaryEntries.add(new TableInfo.Index("index_diary_entries_mealType", false, Arrays.asList("mealType"), Arrays.asList("ASC")));
        final TableInfo _infoDiaryEntries = new TableInfo("diary_entries", _columnsDiaryEntries, _foreignKeysDiaryEntries, _indicesDiaryEntries);
        final TableInfo _existingDiaryEntries = TableInfo.read(db, "diary_entries");
        if (!_infoDiaryEntries.equals(_existingDiaryEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "diary_entries(com.macros.agent.data.local.entity.DiaryEntry).\n"
                  + " Expected:\n" + _infoDiaryEntries + "\n"
                  + " Found:\n" + _existingDiaryEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsGeminiAnalyses = new HashMap<String, TableInfo.Column>(12);
        _columnsGeminiAnalyses.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("photoUri", new TableInfo.Column("photoUri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("rawResponse", new TableInfo.Column("rawResponse", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("detectedFoodsJson", new TableInfo.Column("detectedFoodsJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("totalCalories", new TableInfo.Column("totalCalories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("totalProtein", new TableInfo.Column("totalProtein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("totalCarbs", new TableInfo.Column("totalCarbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("totalFat", new TableInfo.Column("totalFat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("wasAccepted", new TableInfo.Column("wasAccepted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGeminiAnalyses.put("wasModified", new TableInfo.Column("wasModified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGeminiAnalyses = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGeminiAnalyses = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGeminiAnalyses = new TableInfo("gemini_analyses", _columnsGeminiAnalyses, _foreignKeysGeminiAnalyses, _indicesGeminiAnalyses);
        final TableInfo _existingGeminiAnalyses = TableInfo.read(db, "gemini_analyses");
        if (!_infoGeminiAnalyses.equals(_existingGeminiAnalyses)) {
          return new RoomOpenHelper.ValidationResult(false, "gemini_analyses(com.macros.agent.data.local.entity.GeminiAnalysis).\n"
                  + " Expected:\n" + _infoGeminiAnalyses + "\n"
                  + " Found:\n" + _existingGeminiAnalyses);
        }
        final HashMap<String, TableInfo.Column> _columnsUserGoals = new HashMap<String, TableInfo.Column>(9);
        _columnsUserGoals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("dailyCalories", new TableInfo.Column("dailyCalories", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("proteinGrams", new TableInfo.Column("proteinGrams", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("carbsGrams", new TableInfo.Column("carbsGrams", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("fatGrams", new TableInfo.Column("fatGrams", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("fiberGrams", new TableInfo.Column("fiberGrams", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("sugarGrams", new TableInfo.Column("sugarGrams", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("sodiumMg", new TableInfo.Column("sodiumMg", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserGoals.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserGoals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserGoals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserGoals = new TableInfo("user_goals", _columnsUserGoals, _foreignKeysUserGoals, _indicesUserGoals);
        final TableInfo _existingUserGoals = TableInfo.read(db, "user_goals");
        if (!_infoUserGoals.equals(_existingUserGoals)) {
          return new RoomOpenHelper.ValidationResult(false, "user_goals(com.macros.agent.data.local.entity.UserGoals).\n"
                  + " Expected:\n" + _infoUserGoals + "\n"
                  + " Found:\n" + _existingUserGoals);
        }
        final HashMap<String, TableInfo.Column> _columnsExerciseEntries = new HashMap<String, TableInfo.Column>(10);
        _columnsExerciseEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("activityName", new TableInfo.Column("activityName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("caloriesBurned", new TableInfo.Column("caloriesBurned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("durationMinutes", new TableInfo.Column("durationMinutes", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("steps", new TableInfo.Column("steps", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("googleFitActivityId", new TableInfo.Column("googleFitActivityId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsExerciseEntries.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysExerciseEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesExerciseEntries = new HashSet<TableInfo.Index>(1);
        _indicesExerciseEntries.add(new TableInfo.Index("index_exercise_entries_date", false, Arrays.asList("date"), Arrays.asList("ASC")));
        final TableInfo _infoExerciseEntries = new TableInfo("exercise_entries", _columnsExerciseEntries, _foreignKeysExerciseEntries, _indicesExerciseEntries);
        final TableInfo _existingExerciseEntries = TableInfo.read(db, "exercise_entries");
        if (!_infoExerciseEntries.equals(_existingExerciseEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "exercise_entries(com.macros.agent.data.local.entity.ExerciseEntry).\n"
                  + " Expected:\n" + _infoExerciseEntries + "\n"
                  + " Found:\n" + _existingExerciseEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsUserMeals = new HashMap<String, TableInfo.Column>(7);
        _columnsUserMeals.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMeals.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMeals.put("totalCalories", new TableInfo.Column("totalCalories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMeals.put("totalProtein", new TableInfo.Column("totalProtein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMeals.put("totalCarbs", new TableInfo.Column("totalCarbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMeals.put("totalFat", new TableInfo.Column("totalFat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMeals.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserMeals = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserMeals = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserMeals = new TableInfo("user_meals", _columnsUserMeals, _foreignKeysUserMeals, _indicesUserMeals);
        final TableInfo _existingUserMeals = TableInfo.read(db, "user_meals");
        if (!_infoUserMeals.equals(_existingUserMeals)) {
          return new RoomOpenHelper.ValidationResult(false, "user_meals(com.macros.agent.data.local.entity.UserMeal).\n"
                  + " Expected:\n" + _infoUserMeals + "\n"
                  + " Found:\n" + _existingUserMeals);
        }
        final HashMap<String, TableInfo.Column> _columnsUserMealItems = new HashMap<String, TableInfo.Column>(11);
        _columnsUserMealItems.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMealItems.put("mealId", new TableInfo.Column("mealId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMealItems.put("foodName", new TableInfo.Column("foodName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMealItems.put("servingSize", new TableInfo.Column("servingSize", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMealItems.put("servingUnit", new TableInfo.Column("servingUnit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMealItems.put("servingsConsumed", new TableInfo.Column("servingsConsumed", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMealItems.put("calories", new TableInfo.Column("calories", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMealItems.put("protein", new TableInfo.Column("protein", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMealItems.put("carbs", new TableInfo.Column("carbs", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMealItems.put("fat", new TableInfo.Column("fat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserMealItems.put("fdcId", new TableInfo.Column("fdcId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserMealItems = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserMealItems = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserMealItems = new TableInfo("user_meal_items", _columnsUserMealItems, _foreignKeysUserMealItems, _indicesUserMealItems);
        final TableInfo _existingUserMealItems = TableInfo.read(db, "user_meal_items");
        if (!_infoUserMealItems.equals(_existingUserMealItems)) {
          return new RoomOpenHelper.ValidationResult(false, "user_meal_items(com.macros.agent.data.local.entity.UserMealItem).\n"
                  + " Expected:\n" + _infoUserMealItems + "\n"
                  + " Found:\n" + _existingUserMealItems);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4d92d7600a7d4ebf534b2d52b81f815a", "a55e18e8fea433d0f0000ca399412767");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "foods","diary_entries","gemini_analyses","user_goals","exercise_entries","user_meals","user_meal_items");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `foods`");
      _db.execSQL("DELETE FROM `diary_entries`");
      _db.execSQL("DELETE FROM `gemini_analyses`");
      _db.execSQL("DELETE FROM `user_goals`");
      _db.execSQL("DELETE FROM `exercise_entries`");
      _db.execSQL("DELETE FROM `user_meals`");
      _db.execSQL("DELETE FROM `user_meal_items`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(FoodDao.class, FoodDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DiaryDao.class, DiaryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GoalsDao.class, GoalsDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ExerciseDao.class, ExerciseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GeminiAnalysisDao.class, GeminiAnalysisDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserMealDao.class, UserMealDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public FoodDao foodDao() {
    if (_foodDao != null) {
      return _foodDao;
    } else {
      synchronized(this) {
        if(_foodDao == null) {
          _foodDao = new FoodDao_Impl(this);
        }
        return _foodDao;
      }
    }
  }

  @Override
  public DiaryDao diaryDao() {
    if (_diaryDao != null) {
      return _diaryDao;
    } else {
      synchronized(this) {
        if(_diaryDao == null) {
          _diaryDao = new DiaryDao_Impl(this);
        }
        return _diaryDao;
      }
    }
  }

  @Override
  public GoalsDao goalsDao() {
    if (_goalsDao != null) {
      return _goalsDao;
    } else {
      synchronized(this) {
        if(_goalsDao == null) {
          _goalsDao = new GoalsDao_Impl(this);
        }
        return _goalsDao;
      }
    }
  }

  @Override
  public ExerciseDao exerciseDao() {
    if (_exerciseDao != null) {
      return _exerciseDao;
    } else {
      synchronized(this) {
        if(_exerciseDao == null) {
          _exerciseDao = new ExerciseDao_Impl(this);
        }
        return _exerciseDao;
      }
    }
  }

  @Override
  public GeminiAnalysisDao geminiAnalysisDao() {
    if (_geminiAnalysisDao != null) {
      return _geminiAnalysisDao;
    } else {
      synchronized(this) {
        if(_geminiAnalysisDao == null) {
          _geminiAnalysisDao = new GeminiAnalysisDao_Impl(this);
        }
        return _geminiAnalysisDao;
      }
    }
  }

  @Override
  public UserMealDao userMealDao() {
    if (_userMealDao != null) {
      return _userMealDao;
    } else {
      synchronized(this) {
        if(_userMealDao == null) {
          _userMealDao = new UserMealDao_Impl(this);
        }
        return _userMealDao;
      }
    }
  }
}
