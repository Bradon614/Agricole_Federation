# GStatistiquesCollectivitApi

All URIs are relative to *https://api.federation-agricole.mg/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getRapportMensuelCollectivite**](GStatistiquesCollectivitApi.md#getRapportMensuelCollectivite) | **GET** /collectivites/{id_collectivite}/statistiques/mensuelles | Rapport mensuel d’une collectivité |


<a id="getRapportMensuelCollectivite"></a>
# **getRapportMensuelCollectivite**
> RapportMensuelCollectivite getRapportMensuelCollectivite(idCollectivite, mois)

Rapport mensuel d’une collectivité

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.GStatistiquesCollectivitApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    GStatistiquesCollectivitApi apiInstance = new GStatistiquesCollectivitApi(defaultClient);
    Integer idCollectivite = 56; // Integer | 
    String mois = "mois_example"; // String | 
    try {
      RapportMensuelCollectivite result = apiInstance.getRapportMensuelCollectivite(idCollectivite, mois);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling GStatistiquesCollectivitApi#getRapportMensuelCollectivite");
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
| **mois** | **String**|  | |

### Return type

[**RapportMensuelCollectivite**](RapportMensuelCollectivite.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Statistiques mensuelles |  -  |

