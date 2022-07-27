package greenwood.pxpoc.aggregation

import java.util.UUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class AggregationController(
    val aggregationService: AggregationService
) {

    //GET /users/:user_id:
    //GET /users/:user_id:/transactions

    @GetMapping(
        "/{userId}",
        produces = ["application/json"]
    )
    fun getUserAccountInfo(@PathVariable(name = "userId") userId: UUID): UserAccountInfoResponse {

        return aggregationService.retrieveUserAccountInfo(userId)
    }

    @GetMapping(
        "/{userId}/transactions",
        produces = ["application/json"]
    )
    fun getUserTransactions(@PathVariable(name = "userId") userId: UUID): TransactionsResponse {
        return aggregationService.retrieveUserTransactions(userId)
    }
    
    /* Query Params, I think for transactions
start       The number of transactions offset from the beginning of the result set. This is NOT the "page" in the pagination ow.
limit       When paginating, size of pages. 500
start_date  Oldest posting date from which to start returning transactions. (30 days ago)
end_date    Most recent posting date for which transactions may be included. (current date)
     */


}