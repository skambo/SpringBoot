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
package io.skambo.example.infrastructure.api.updateuser.v1.dto

import io.skambo.example.infrastructure.api.common.dto.v1.Header

import com.squareup.moshi.Json
/**
 * 
 * @param header 
 * @param name 
 * @param dateOfBirth 
 * @param city 
 */
data class UpdateUserRequest (
    @Json(name = "header")
    val header: io.skambo.example.infrastructure.api.common.dto.v1.Header,
    @Json(name = "name")
    val name: kotlin.String? = null,
    @Json(name = "dateOfBirth")
    val dateOfBirth: java.time.OffsetDateTime? = null,
    @Json(name = "city")
    val city: kotlin.String? = null
) {

}

