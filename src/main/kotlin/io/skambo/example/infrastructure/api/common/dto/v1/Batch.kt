/**
* 
* 
*
* 
* 
*
* NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
* https://openapi-generator.tech
* Do not edit the class manually.
*/
package io.skambo.example.infrastructure.api.common.dto.v1


import com.squareup.moshi.Json
/**
 * 
 * @param batchId 
 * @param messageSequenceNumber 
 * @param totalMessages 
 */
data class Batch (
    @Json(name = "batchId")
    val batchId: kotlin.String,
    @Json(name = "messageSequenceNumber")
    val messageSequenceNumber: kotlin.Int,
    @Json(name = "totalMessages")
    val totalMessages: kotlin.Int
) {

}

