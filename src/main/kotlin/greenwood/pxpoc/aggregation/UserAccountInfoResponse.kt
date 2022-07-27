package greenwood.pxpoc.aggregation

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserAccountInfoResponse(
    val plaidAccounts: List<PlaidAccount>,
    val apiVersion: String,
    val identities: List<PlaidIdentity>,
    val securities: List<PlaidSecurity> = emptyList(),  //documentation says this is required, so creating empty list
    val userIdentityId: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PlaidAccount(
    val id: String,
    val ownerIdentityIds: List<String>,
    val status: String,
    val type: String,
    val subtype: String,
    val officialName: String,
    val displayMask: String,
    val currentBalance: String,
    val availableBalance: String,
    val transferCodes: TransferCodes? = null,

    val lastActivityAt: String? = null,
    val ownershipType: String? = null,
    val currency: String = "USD",
    val holdings: List<Holding> = emptyList(),
    val interestRate: String? = null,
    val name: String? = null,
    val nonOwnerIdentityIds: List<String>? = null,
    val openingDate: String? = null,
    val statements: List<Any>? = null,
    val taxAdvantaged: Boolean? = null
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PlaidIdentity(
    val email: String,
    val id: String,
    val name: String
)


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Ach(
    val accountNumber: String,
    val routingNumber: String,
    val supportsCredit: Boolean,
    val supportsDebit: Boolean
)

data class TransferCodes(
    val ach: Ach
)

enum class AccountStatus(val status: String) {
    ACTIVE("active"),
    INACTIVE("inactive"),
    FROZEN("frozen"),
    LOCKED("locked"),
    FLAGGED("flagged"),
    RESTRICTED("restricted"),
    CLOSED("closed")
}

//TODO create an EnumMap to map galileo statuses to Plaid statuses -- tho, wow, they don't match at all.

//TODO: I don't think these apply for us yet
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Holding(
    val quantity: String,
    val securityId: String
)

//TODO: I don't think these apply for us yet
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class PlaidSecurity(
    val id: String,
    val name: String,
    val symbol: String,
    val isCashEquivalent: Boolean,
    val currentAsOf: String,
    val currentPrice: String,
    val type: String
)

//Documentation is tough to follow
/*------------------------------------------------------------*/
/*
identities          BasicIdentity[]      List of Identity instances necessary to fully resolve all records in accounts.
user_identity_id    identityId           Indicate the Identity corresponding to the currently logged-in user.
securities          Security[]           List of Securities necessary to fully resolve all records in accounts.
accounts            BaseAccount[]        List of all accounts for which this user is an owner, or interested non-owner.
 */

/*
BasicIdentity
id      identityId      Permanent identity identier.
name    string          The display name of this entity (insucient for KYC purposes).
email   string          The email address where this entity can be contacted.
 */

/* Account Status -- create an enum, then we'll translate from galileo statues
active      Account has current activity and there are no issues outstanding.
inactive    Account has seen no activity for a standard period dened by the institution, and may be subject to fees or at risk of closure.
frozen      Access to and transaction against the account is prohibited due to legal or compliance concerns, e.g. assets subject to OFAC sanctions enforcement, assets subject to a discovery subpoena, etc.
locked      Access to the account is prohibited due to security concerns, e.g. fraudulent activity observed.
flagged     All activity is prohibited due to regulatory or policy requirements, e.g. monthly withdrawals from a savings account in excess of FRB Regulation D.
restricted  Activity on the account is restricted, e.g. withdrawals are subject to a daily limit, until the user meets certain conditions.
closed      The account is closed.
 */

/* Galileo Account Statuses
    CANCELED("C"),
    CANCELED_WITHOUT_REFUND("Z"),
    SUSPENDED("K"),
    DISABLED("D"),
    LOST("L"),
    ACTIVE("N"),
    SUBMITTED("V"),
    UNKNOWN("");
 */

/*
id                  accountId Permanent  account  identier. ✓ R13oiR6lC5jNC5jK
last_activity_at    iso8601 Date  of  most  recent  change  to,  or  activity  on  this account,  e.g.  new  transactions,  or  changes  to  account metadata.  Used  to  provide  hints  for  optimal  schedulingof  updates.
2020-04-21T12:45:00+00:00
ownership_type accountOwnership Indicates  the  ownership  type  of  the  account,  not  the
relationship  the  current  user  has  over  the  account.
individual
8.3.5 BaseAccount
PLAID EXCHANGE API DOCUMENTATION
PLAID PRIVATE & CONFIDENTIAL INFORMATION
2021-03-26
PLAID PRIVATE & CONFIDENTIAL INFORMATION 32 / 47
NAME TYPE DESCRIPTION REQUIRED EXAMPLE
owner_identity_ids identityId[] References  to  the  Identities  for  the  owner(s)  of  this
account.
✓
non_owner_identity_ids identityId[] References  to  the  Identities  for  non-owner(s)  related
to  this  account,  e.g.  trustees,  beneciaries.
status accountStatus Status  of  this  account. ✓ closed
type accountType Major  classication  of  this  account. ✓ depository
subtype accountSubtype Minor  classication  of  this  account. ✓ checking
name string The  account’s  user-given  name,  if  the  institution
supports  naming  of  accounts
Vacation  money
official_name string The  account’s  marketing  or  brand  name. ✓ Pro  Checking
display_mask string A  short  alpha-numeric  string  to  assist  users  in
identifying  the  account,  e.g.  last  four  digits  of  the
account  number.
✓ 9833
S01
opening_date iso8601 The  date  on  which  the  account  was  opened 2018-01-31
current_balance amount The  total  balance  in  the  account,  typically  including
pending  transactions.
See  individual  account  types  for  specic  denitions  of
this  value.
✓ 850.55
available_balance amount The  immediately  available  balance  in  the  account,
typically  the  amount  available  to  withdraw  at  the
moment.
✓ 149.45
tax_advantaged boolean Indicates  whether  some  activity  on  the  account  —
deposits,  gains,  etc.  —  benets  from  tax  deferral  or
exemption  e.g.  HSA,  IRA,  401(k)  accounts.
currency iso4217 The  ISO-4217  currency  in  which  this  account’s
transactions  and  balances  are  denominated.  One  of
either  the  currency  or  non_iso_currency  elds  is
required.
USD
non_iso_currency string If  the  account  is  denominated  in  a  non-ISO  currency,
provide  the  currency’s  symbol.
BTC
Reward  Points
 */