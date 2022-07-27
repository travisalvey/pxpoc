package greenwood.pxpoc.aggregation

data class UserAccountInfoResponse1(
    val temp: String
)


/*{
  "user_identity_id": "d7f1b8b9-0006-4135-91c0-b5532045a314",
  "identities": [
    {
      "id": "d7f1b8b9-0006-4135-91c0-b5532045a314",
      "name": "Jane Doe",
      "email": "jane@plaid.com"
    }
  ],
  "accounts": [
    {
      "id": "string",
      "isin": "string",
      "name": "CISCO SYSTEMS INC",
      "symbol": "CSCO",
      "is_cash_equivalent": true,
      "current_price": "100.95",
      "current_as_of": "2018-08-28",
      "close_price": "100.95",
      "type": "cash",
      "currency": "USD",
      "non_iso_currency": "string"
    }
  ],
  "securities": [
    {
      "id": "R13oiR6lC5jNC5jK",
      "last_activity_at": "2018-08-28",
      "ownership_type": "individual",
      "owner_identity_ids": [
        "string"
      ],
      "non_owner_identity_ids": [
        "string"
      ],
      "status": "active",
      "type": "depository",
      "subtype": "cash management",
      "name": "Vacation Money",
      "official_name": "Pro Checking",
      "display_mask": "9833",
      "opening_date": "2018-08-28",
      "current_balance": "100.95",
      "available_balance": "100.95",
      "tax_advantaged": true,
      "currency": "USD",
      "non_iso_currency": "string"
    }
  ]
}
 */

/*
identities          BasicIdentity[]      List of Identity instances necessary to fully resolve all records in accounts.
user_identity_id    identityId           Indicate the Identity corresponding to the currently logged-in user.
securities          Security[]           List of Securities necessary to fully resolve all records in accounts.
accounts            BaseAccount[]        List of all accounts for which this user is an owner, or interested non-owner.
 */

/*
BasicIdentity
id      identityId   Permanent identity identier. ✓
name    string     The display name of this entity (insucient for KYC purposes). ✓ Jane Doe
email   string    The email address where this entity can be contacted.
 */

/*
id
isin
name
symbol
is_cash_equivalent
current_price
current_as_of
close_price
type
currency
non_iso_currency
 */