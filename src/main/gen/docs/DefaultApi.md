# DefaultApi

All URIs are relative to *https://localhost:8080*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**collectivitiesIdIdentifiersPatch**](DefaultApi.md#collectivitiesIdIdentifiersPatch) | **PATCH** /collectivities/{id}/identifiers | Assign unique number and name to a collective |
| [**collectivitiesPost**](DefaultApi.md#collectivitiesPost) | **POST** /collectivities | Create list of collectivities |
| [**membersPost**](DefaultApi.md#membersPost) | **POST** /members | Create list of members |


<a id="collectivitiesIdIdentifiersPatch"></a>
# **collectivitiesIdIdentifiersPatch**
> Collectivity collectivitiesIdIdentifiersPatch(id, collectiveIdentifiersAssignment)

Assign unique number and name to a collective

This endpoint allows the federation to assign a **unique number** and **unique name** to an existing collective. - Once assigned, these values **cannot be changed**. - The number and name must be unique across all collectivities. - If the collective already has a number or name, the request will be rejected. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    String id = "id_example"; // String | The internal ID of the collective (returned at creation)
    CollectiveIdentifiersAssignment collectiveIdentifiersAssignment = new CollectiveIdentifiersAssignment(); // CollectiveIdentifiersAssignment | 
    try {
      Collectivity result = apiInstance.collectivitiesIdIdentifiersPatch(id, collectiveIdentifiersAssignment);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#collectivitiesIdIdentifiersPatch");
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
| **id** | **String**| The internal ID of the collective (returned at creation) | |
| **collectiveIdentifiersAssignment** | [**CollectiveIdentifiersAssignment**](CollectiveIdentifiersAssignment.md)|  | |

### Return type

[**Collectivity**](Collectivity.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Identifiers successfully assigned. Returns the updated collective. |  -  |
| **400** | Bad request. Possible reasons: - Number or name already exists. - Collective already has a number/name assigned. - Invalid format.  |  -  |
| **404** | Collective not found. |  -  |

<a id="collectivitiesPost"></a>
# **collectivitiesPost**
> List&lt;Collectivity&gt; collectivitiesPost(createCollectivity)

Create list of collectivities

During collectivity request body creation, only members ID are provided to identify members.  But the response content includes members information, not only their IDs. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    List<CreateCollectivity> createCollectivity = Arrays.asList(); // List<CreateCollectivity> | 
    try {
      List<Collectivity> result = apiInstance.collectivitiesPost(createCollectivity);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#collectivitiesPost");
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
| **createCollectivity** | [**List&lt;CreateCollectivity&gt;**](CreateCollectivity.md)|  | [optional] |

### Return type

[**List&lt;Collectivity&gt;**](Collectivity.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | List of created collectivities |  -  |
| **400** | Collectivity without federation approval or structure missing. |  -  |
| **404** | Member not found. |  -  |

<a id="membersPost"></a>
# **membersPost**
> List&lt;Member&gt; membersPost(createMember)

Create list of members

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.DefaultApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("https://localhost:8080");

    DefaultApi apiInstance = new DefaultApi(defaultClient);
    List<CreateMember> createMember = Arrays.asList(); // List<CreateMember> | 
    try {
      List<Member> result = apiInstance.membersPost(createMember);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling DefaultApi#membersPost");
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
| **createMember** | [**List&lt;CreateMember&gt;**](CreateMember.md)|  | [optional] |

### Return type

[**List&lt;Member&gt;**](Member.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | List of created members |  -  |
| **400** | The following case must return a bad request exception :  - Member with bad referees or without proper payment. - Membership dues not paid or registration fee not paid.  |  -  |
| **404** | Either collectivity or member not found. |  -  |

