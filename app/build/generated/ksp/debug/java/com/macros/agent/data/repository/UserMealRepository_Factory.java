package com.macros.agent.data.repository;

import com.macros.agent.data.local.dao.UserMealDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class UserMealRepository_Factory implements Factory<UserMealRepository> {
  private final Provider<UserMealDao> userMealDaoProvider;

  public UserMealRepository_Factory(Provider<UserMealDao> userMealDaoProvider) {
    this.userMealDaoProvider = userMealDaoProvider;
  }

  @Override
  public UserMealRepository get() {
    return newInstance(userMealDaoProvider.get());
  }

  public static UserMealRepository_Factory create(Provider<UserMealDao> userMealDaoProvider) {
    return new UserMealRepository_Factory(userMealDaoProvider);
  }

  public static UserMealRepository newInstance(UserMealDao userMealDao) {
    return new UserMealRepository(userMealDao);
  }
}
