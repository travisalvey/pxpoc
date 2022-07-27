package greenwood.pxpoc.client

import greenwood.common.model.CustomerResponse
import java.util.UUID
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface CustomerServiceClient {
    @GET("/internal/customer/{userId}")
    fun getCustomerData(@Path("userId") userId: UUID): Call<CustomerResponse>
}