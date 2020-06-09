import java.time.Year
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object Env {
	val now = ZonedDateTime.now()
	val year = Year.now();
	val copyrightYear = "2007-${year}"
	val manualDate = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(now)

	val dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
}

object Jenetics {
	val Version = "6.1.0-SNAPSHOT"
	val Id = "jenetics"
	val Name = "Jenetics"
	val Group = "io.jenetics"
	val Description = "Jenetics - Java Genetic Algorithm Library"
	val Author = "Franz Wilhelmstötter"
	val Email = "franz.wilhelmstoetter@gmail.com"
	val Url = "https://jenetics.io"
	val MavenScmUrl = "https://github.com/jenetics/jenetics"
	val MavenScmConnection = "scm:git:https://github.com/jenetics/jenetics.git"
	val MavenScmDeveloperConnection = "scm:git:https://github.com/jenetics/jenetics.git"

	val manualIdentifier = "${Version}—${Env.manualDate}"

//	val MANIFEST_ATTRIBUTES = mapOf(
//		"Implementation-Title" to project.name,
//		"Implementation-Version" to project.version.toString(),
//		"Implementation-URL" to Jenetics.Url,
//		"Implementation-Vendor" to Jenetics.Name,
//		"ProjectName" to Jenetics.Name,
//		"Version" to project.version.toString(),
//		"Maintainer" to Jenetics.Author,
//		"Project" to project.name,
//		"Project-Version" to project.version,
//		"Built-By" to System.getProperty("user.name"),
//		"Build-Timestamp" to Env.dateformat.format(Env.now),
//		"Created-By" to "Gradle ${gradle.gradleVersion}",
//		"Build-Jdk" to "${System.getProperty("java.vm.name")} " +
//			"(${System.getProperty("java.vm.vendor")} " +
//			"${System.getProperty("java.vm.version")})",
//		"Build-OS" to "${System.getProperty("os.name")} " +
//			"${System.getProperty("os.arch")} " +
//			System.getProperty("os.version")
//	)

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
