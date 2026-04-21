# ACollectivitsApi

All URIs are relative to *https://api.federation-agricole.mg/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**autoriserCollectivite**](ACollectivitsApi.md#autoriserCollectivite) | **PATCH** /collectivites/{id_collectivite}/autoriser | Autoriser l’ouverture d’une collectivité |
| [**createCollectivite**](ACollectivitsApi.md#createCollectivite) | **POST** /collectivites | Créer une nouvelle collectivité (demande d’ouverture) |


<a id="autoriserCollectivite"></a>
# **autoriserCollectivite**
> autoriserCollectivite(idCollectivite)

Autoriser l’ouverture d’une collectivité

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ACollectivitsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    ACollectivitsApi apiInstance = new ACollectivitsApi(defaultClient);
    Integer idCollectivite = 56; // Integer | 
    try {
      apiInstance.autoriserCollectivite(idCollectivite);
    } catch (ApiException e) {
      System.err.println("Exception when calling ACollectivitsApi#autoriserCollectivite");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **idCollectivite** | **Integer**|  | |

### Return type

null (empty response body)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Collectivité autorisée |  -  |

<a id="createCollectivite"></a>
# **createCollectivite**
> Collectivite createCollectivite(collectiviteCreationRequest)

Créer une nouvelle collectivité (demande d’ouverture)

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.ACollectivitsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    ACollectivitsApi apiInstance = new ACollectivitsApi(defaultClient);
    CollectiviteCreationRequest collectiviteCreationRequest = new CollectiviteCreationRequest(); // CollectiviteCreationRequest | 
    try {
      Collectivite result = apiInstance.createCollectivite(collectiviteCreationRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling ACollectivitsApi#createCollectivite");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **collectiviteCreationRequest** | [**CollectiviteCreationRequest**](CollectiviteCreationRequest.md)|  | |

### Return type

[**Collectivite**](Collectivite.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Collectivité créée avec succès |  -  |

