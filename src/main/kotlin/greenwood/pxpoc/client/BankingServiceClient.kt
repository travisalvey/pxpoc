package greenwood.pxpoc.client

import greenwood.common.client.galileo.model.GalileoAccountStatus
import greenwood.common.model.PrimaryAccount
import greenwood.common.model.constants.AccountType
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import org.javamoney.moneta.Money
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface BankingServiceClient {

    //TODO: This is just a placeholder.  Likely we'll need ot add an endpoint to Banking/Internal to grab transactions
    @GET("/internal/customer/{userId}/primaryAccount")
    fun getCustomerPrimaryAccount(@Path("userId") userId: UUID): Call<PrimaryAccount>

    //TODO: We probably just want to get the account - banking -> /accounts/{accountId}, or just /accounts  Retrieving
    // Transactions in the  banking service generally pulls the account, and extracts from there
    // Likely will need a new internal endpoint to expose this

    //TODO: Will need to duplicate DTOs or move them from banking to Common
    @GET("/internal/customer/{userId}/accounts")
    fun getAccountsForUser(@Path("userId") userId: UUID): Call<AccountsCollectionDTO>
}

//TODO: Pulled these in just for experimentation purposes, will likely want to move them from Banking to Common

data class AccountDTO(

    @Schema(description = "Primary account identifier for calls like get balance, transactions, etc")
    val id: UUID,

    @Schema(description = "A textual description of the account type, such as 'Savings Account'")
    val title: String,

    @Schema(description = "An account identifier that can be used for direct deposits")
    val accountNumber: String,

    @Schema(description = "A routing number that can be used for direct deposits")
    val routingNumber: String,

    @Schema(description = "The type of this account (Basic DDA, Premium DDA, Savings, etc)")
    val accountType: AccountType,

    @Schema(description = "Account status eg: ACTIVE, DISABLED, etc")
    val galileoAccountStatus: GalileoAccountStatus,

    @Schema(description = "Account status eg: ACTIVE, DISABLED, etc", deprecated = true)
    val status: GalileoAccountStatus? = galileoAccountStatus,

    @Schema(description = "Account balance, includes pending transactions")
    val balance: Money,

    @Schema(description = "Date that the last statement was generated on")
    val lastStatementDate: LocalDate? = null,

    @Schema(description = "List of account transactions, includes both pending and settled types")
    val transactions: List<TransactionDTO>,

    @Schema(description = "Total number of transactions in the requested date window")
    val totalTransactionCount: Int? = null,

    @Schema(description = "Annual Percentage Yield, only returned for savings accounts")
    val apy: String? = null,

    @Schema(description = "Funding state information for the account")
    val fundingInfo: AccountFundingInfoDTO? = null
)

data class AccountsCollectionDTO(
    @Schema(description = "Total balance which is the sum of balances of all the underlying accounts")
    val totalBalance: Money,

    @Schema(description = "List of Account objects that includes balance and transactions")
    val accounts: List<AccountDTO>
)

data class TransactionDTO(

    @Schema(description = "Dollar amount of the transactions")
    val amount: Money,

    @Schema(description = "Textual description of the transaction")
    val description: String? = null,

    @Schema(description = "Textual description of the type, such as Payment, Pre-auth, ACH Transfer, etc")
    val transactionType: String? = null,

    @Schema(description = "Settled status of the transaction, indicates whether it's settled or pending")
    val isSettled: Boolean,

    @Schema(description = "The date and time when the transaction happened")
    val transactionTime: OffsetDateTime? = null,

    @Schema(description = "Balance of the account after this transaction completes. Not always available")
    val accountBalance: Money? = null,

    @Schema(description = "Optional check id. If present can be used to fetch check images")
    val checkId: String? = null,

    @Schema(description = "Details related to a transaction as key/value pairs")
    val details: List<Detail> = mutableListOf()
) {

    enum class DetailKey(val label: String) {
        TRANSACTION_DETAIL_KEY("Details"),
        TRANSACTION_MERCHANT_INFO_KEY("Merchant")
    }

    data class Detail(
        @Schema(description = "Key corresponding to the detail of the transaction")
        val key: DetailKey,

        @Schema(description = "Label for the detail attribute")
        val label: String,

        @Schema(description = "Value corresponding to the detail key")
        val value: String
    )
}

data class AccountFundingInfoDTO(

    @Schema(description = "Account ID")
    val id: UUID? = null,

    @Schema(description = "Funding state that the account is in")
    val fundingState: FundingState,

    @Schema(description = "Whether the funding state has been acknowledged by the customer/system")
    val fundingStateAcknowledged: Boolean,

    @Schema(description = "Details of the Payment Session")
    val paymentDetails: PaymentSessionDetails? = null
)

enum class FundingState {
    NOT_REQUIRED, REQUIRED, PROCESSING, PASSED, FAILED
}

data class PaymentSessionDetails(
    @Schema(description = "Stripe Payment Session Id")
    val paymentSessionId: String,
    @Schema(description = "Payment Session Type - PAYMENT_INTENT, SETUP_INTENT")
    val paymentSessionType: PaymentSessionType
)

enum class PaymentSessionType {
    PAYMENT_INTENT,
    SETUP_INTENT
}

