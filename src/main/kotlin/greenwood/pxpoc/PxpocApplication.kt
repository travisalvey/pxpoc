package greenwood.pxpoc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PxpocApplication

fun main(args: Array<String>) {
	runApplication<PxpocApplication>(*args)
}
