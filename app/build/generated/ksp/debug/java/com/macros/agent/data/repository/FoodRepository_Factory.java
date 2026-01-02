package com.macros.agent.data.repository;

import com.macros.agent.data.local.dao.FoodDao;
import com.macros.agent.data.remote.api.UsdaApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class FoodRepository_Factory implements Factory<FoodRepository> {
  private final Provider<FoodDao> foodDaoProvider;

  private final Provider<UsdaApiService> usdaApiServiceProvider;

  private final Provider<String> apiKeyProvider;

  public FoodRepository_Factory(Provider<FoodDao> foodDaoProvider,
      Provider<UsdaApiService> usdaApiServiceProvider, Provider<String> apiKeyProvider) {
    this.foodDaoProvider = foodDaoProvider;
    this.usdaApiServiceProvider = usdaApiServiceProvider;
    this.apiKeyProvider = apiKeyProvider;
  }

  @Override
  public FoodRepository get() {
    return newInstance(foodDaoProvider.get(), usdaApiServiceProvider.get(), apiKeyProvider.get());
  }

  public static FoodRepository_Factory create(Provider<FoodDao> foodDaoProvider,
      Provider<UsdaApiService> usdaApiServiceProvider, Provider<String> apiKeyProvider) {
    return new FoodRepository_Factory(foodDaoProvider, usdaApiServiceProvider, apiKeyProvider);
  }

  public static FoodRepository newInstance(FoodDao foodDao, UsdaApiService usdaApiService,
      String apiKey) {
    return new FoodRepository(foodDao, usdaApiService, apiKey);
  }
}
