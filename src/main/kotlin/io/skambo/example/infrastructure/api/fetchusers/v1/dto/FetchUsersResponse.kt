/**
* SpringBoot
* SpringBoot Examples.
*
* The version of the OpenAPI document: 1.0.0
* 
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package io.skambo.example.infrastructure.api.fetchusers.v1.dto

import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.UserDTO

import com.squareup.moshi.Json
/**
 * 
 * @param header 
 * @param page 
 * @param numberOfUsers 
 * @param totalPages 
 * @param users 
 */
data class FetchUsersResponse (
    @Json(name = "header")
    val header: io.skambo.example.infrastructure.api.common.dto.v1.Header,
    @Json(name = "page")
    val page: kotlin.Int? = null,
    @Json(name = "numberOfUsers")
    val numberOfUsers: kotlin.Int? = null,
    @Json(name = "totalPages")
    val totalPages: kotlin.Int? = null,
    @Json(name = "users")
    val users: kotlin.Array<io.skambo.example.infrastructure.api.common.dto.v1.UserDTO>? = null
) {

}

