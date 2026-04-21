# HStatistiquesFdrationApi

All URIs are relative to *https://api.federation-agricole.mg/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getRapportFederation**](HStatistiquesFdrationApi.md#getRapportFederation) | **GET** /federation/statistiques | Rapport global mensuel ou annuel de la fédération |


<a id="getRapportFederation"></a>
# **getRapportFederation**
> RapportFederation getRapportFederation(typePeriode, dateDebut, dateFin)

Rapport global mensuel ou annuel de la fédération

Taux d’assiduité, % de membres à jour de cotisation, nouveaux adhérents par collectivité

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.HStatistiquesFdrationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    HStatistiquesFdrationApi apiInstance = new HStatistiquesFdrationApi(defaultClient);
    String typePeriode = "mensuel"; // String | 
    LocalDate dateDebut = LocalDate.now(); // LocalDate | 
    LocalDate dateFin = LocalDate.now(); // LocalDate | 
    try {
      RapportFederation result = apiInstance.getRapportFederation(typePeriode, dateDebut, dateFin);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling HStatistiquesFdrationApi#getRapportFederation");
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
| **typePeriode** | **String**|  | [enum: mensuel, annuel] |
| **dateDebut** | **LocalDate**|  | |
| **dateFin** | **LocalDate**|  | |

### Return type

[**RapportFederation**](RapportFederation.md)

### Authorization

[BearerAuth](../README.md#BearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Rapport global de la fédération |  -  |

