package greenwood.pxpoc.aggregation

import greenwood.common.util.getValueOrThrow
import greenwood.pxpoc.client.BankingServiceClient
import greenwood.pxpoc.client.CustomerServiceClient
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class AggregationService(
    val bankingServiceClient: BankingServiceClient,
    val customerServiceClient: CustomerServiceClient
) {

    fun retrieveUserAccountInfo(userId: UUID): UserAccountInfoResponse {
        var customerData = getValueOrThrow(
            customerServiceClient.getCustomerData(userId).execute().body(),
            "Could not fetch customer data for userId $userId"
        )
        var accounts = getValueOrThrow(
            bankingServiceClient.getAccountsForUser(userId).execute().body(),
            "Could not fetch Primary Account data  for userId $userId"
        )
        //TODO: Call other endpoints to fill our required data fields
        //TODO: Map to Plaid response

        val plaidAccounts = mutableListOf<PlaidAccount>()
        accounts.accounts.forEach { a ->
            PlaidAccount(
                id = a.id.toString(),
                ownerIdentityIds = listOf(customerData.userId.toString()),
                status = a.status.toString(),
                type = "",
                subtype = "",
                officialName = "",
                displayMask = "",
                currentBalance = "",
                availableBalance = ""

            )

        }

        throw NotImplementedError("WIP")
    }

    fun retrieveUserTransactions(userId: UUID): TransactionsResponse {
        var accounts = getValueOrThrow(
            bankingServiceClient.getAccountsForUser(userId).execute().body(),
            "Could not fetch Primary Account data  for userId $userId"
        )

        val plaidTransactions = mutableListOf<PlaidTransaction>()
        accounts.accounts.forEach { a ->
            a.transactions.forEach { t ->
                plaidTransactions.add(
                    PlaidTransaction(
                        id = "",
                        accountId = a.id.toString(),  //TODO: Or is this a.accountNumber??
                        amount = t.amount.numberStripped.toString(),
                        description = t.description ?: "N/A",
                        pending = !t.isSettled,
                        transactedAt = t.transactionTime?.toLocalDate().toString(),
                        settledAt = "",
                        endingBalance = t.accountBalance?.numberStripped.toString()
                        //currency = "USD"       //assuming always USD for now, but should use Monetary constant

                    )
                )
            }
        }
        //TODO convert accounts/transactions to transactionResponse
        TransactionsResponse(
            plaidTransactions.size,
            plaidTransactions
        )
        throw NotImplementedError("WIP")
    }
}