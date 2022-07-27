package greenwood.pxpoc.aggregation

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

data class TransactionsResponse(
    val total: Int,
    val plaidTransactions: List<PlaidTransaction>
)

//TODO: There are many other optional fields
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PlaidTransaction(
    val id: String,
    val accountId: String,
    val description: String,
    val amount: String,
    val pending: Boolean,
    val settledAt: String,
    val transactedAt: String,
    val spenderIdentityId: String? = null,
    val currency: String = "USD",
    val rewardCurrency: String? = null,
    val endingBalance: String? = null
)