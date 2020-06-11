import java.time.Year
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Common environment values.
 */
object Env {
	val NOW = ZonedDateTime.now()

	val YEAR = Year.now();

	val COPYRIGHT_YEAR = "2007-${YEAR}"

	val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

	val MANUAL_DATE = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(NOW)

	val BUILD_DATE = DATE_FORMAT.format(NOW)

	val BUILD_JDK = System.getProperty("java.version")

	val BUILD_OS_NAME = System.getProperty("os.name")

	val BUILD_OS_ARCH = System.getProperty("os.arch")

	val BUILD_OS_VERSION = System.getProperty("os.version")

	val BUILD_BY = System.getProperty("user.name")

}

object Jenetics {
	const val VERSION = "6.1.0-SNAPSHOT"
	const val ID = "jenetics"
	const val NAME = "Jenetics"
	const val GROUP = "io.jenetics"
	const val DESCRIPTION = "Jenetics - Java Genetic Algorithm Library"
	const val AUTHOR = "Franz Wilhelmstötter"
	const val EMAIL = "franz.wilhelmstoetter@gmail.com"
	const val URL = "https://jenetics.io"



	val manualIdentifier = "${VERSION}—${Env.MANUAL_DATE}"

	object Ext {
		val Name = "Jenetics Extensions"
	}

	object Prog {
		val Name = "Jenetics Genetic Programming"
	}

	object Xml {
		val Name = "Jenetics XML Marshalling"
	}

	object Example {
		val Name = "Jenetics Examples"
	}
}

/**
 * Environment variables for publishing to Maven Central.
 */
object Maven {
	const val SNAPSHOT_URL = "https://oss.sonatype.org/content/repositories/snapshots/"
	const val RELEASE_URL = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

	const val SCM_URL = "https://github.com/jenetics/jenetics"
	const val SCM_CONNECTION = "scm:git:https://github.com/jenetics/jenetics.git"
	const val DEVELOPER_CONNECTION = "scm:git:https://github.com/jenetics/jenetics.git"
}
