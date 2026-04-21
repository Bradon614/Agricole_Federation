# DTrsorerieApi

All URIs are relative to *https://api.federation-agricole.mg/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createCompte**](DTrsorerieApi.md#createCompte) | **POST** /comptes | Créer un compte (Caisse, Bancaire ou Mobile Money) |
| [**getComptesCollectivite**](DTrsorerieApi.md#getComptesCollectivite) | **GET** /collectivites/{id_collectivite}/comptes | Lister les comptes d’une collectivité |


<a id="createCompte"></a>
# **createCompte**
> createCompte(compteCreationRequest)

Créer un compte (Caisse, Bancaire ou Mobile Money)

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DTrsorerieApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    DTrsorerieApi apiInstance = new DTrsorerieApi(defaultClient);
    CompteCreationRequest compteCreationRequest = new CompteCreationRequest(); // CompteCreationRequest | 
    try {
      apiInstance.createCompte(compteCreationRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling DTrsorerieApi#createCompte");
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
| **compteCreationRequest** | [**CompteCreationRequest**](CompteCreationRequest.md)|  | |

### Return type

null (empty response body)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Compte créé |  -  |

<a id="getComptesCollectivite"></a>
# **getComptesCollectivite**
> List&lt;CompteDetaille&gt; getComptesCollectivite(idCollectivite)

Lister les comptes d’une collectivité

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DTrsorerieApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    DTrsorerieApi apiInstance = new DTrsorerieApi(defaultClient);
    Integer idCollectivite = 56; // Integer | 
    try {
      List<CompteDetaille> result = apiInstance.getComptesCollectivite(idCollectivite);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DTrsorerieApi#getComptesCollectivite");
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

[**List&lt;CompteDetaille&gt;**](CompteDetaille.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Liste des comptes |  -  |

