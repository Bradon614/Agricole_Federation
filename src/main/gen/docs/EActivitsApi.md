# EActivitsApi

All URIs are relative to *https://api.federation-agricole.mg/v1*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createActivite**](EActivitsApi.md#createActivite) | **POST** /activites | Créer une activité |


<a id="createActivite"></a>
# **createActivite**
> createActivite(activiteCreationRequest)

Créer une activité

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.EActivitsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://api.federation-agricole.mg/v1");
    
    // Configure HTTP bearer authorization: BearerAuth
    HttpBearerAuth BearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("BearerAuth");
    BearerAuth.setBearerToken("BEARER TOKEN");

    EActivitsApi apiInstance = new EActivitsApi(defaultClient);
    ActiviteCreationRequest activiteCreationRequest = new ActiviteCreationRequest(); // ActiviteCreationRequest | 
    try {
      apiInstance.createActivite(activiteCreationRequest);
    } catch (ApiException e) {
      System.err.println("Exception when calling EActivitsApi#createActivite");
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
| **activiteCreationRequest** | [**ActiviteCreationRequest**](ActiviteCreationRequest.md)|  | |

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
| **201** | Activité créée |  -  |

