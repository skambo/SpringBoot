package io.skambo.example

import liquibase.exception.LiquibaseException
import liquibase.integration.spring.SpringLiquibase
import org.slf4j.LoggerFactory
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.core.io.DefaultResourceLoader
import javax.sql.DataSource

/**
 * Class used as an entry point to run database migrations
 *
 * @author kelvin.wahome
 */
class MigrationApplication {
    companion object{
        private val LOGGER = LoggerFactory.getLogger(MigrationApplication::class.java)
        private const val DATABASE_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver"
        private const val MASTER_CHANGE_LOG_PATH = "classpath:db/db.changelog-master.xml"
        private const val EXPECTED_NUMBER_OF_ARGUMENTS = 3

        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size < EXPECTED_NUMBER_OF_ARGUMENTS) {
                val message = String.format(
                    "Insufficient number of args received. Expected %s but received %s arguments. "
                            + "Required args: dbUrl, dbUser, dbPassword ", args.size, EXPECTED_NUMBER_OF_ARGUMENTS
                )
                LOGGER.error(message)
                throw IllegalArgumentException(message)
            }
            val migrationApplication = MigrationApplication()
            val dbUrl = args[0]
            val dbUser = args[1]
            val dbPassword = args[2]
            migrationApplication.migrate(dbUrl, dbUser, dbPassword)
        }
    }

    private fun migrate(dbUrl: String, username: String, password: String) {
        try {
            LOGGER.info(String.format("Running migrations on [%s] with user [%s] started", dbUrl, username))
            val liquibase = SpringLiquibase()
            liquibase.dataSource = getDataSource(dbUrl, username, password)
            liquibase.changeLog = MASTER_CHANGE_LOG_PATH
            liquibase.setResourceLoader(DefaultResourceLoader())
            liquibase.setShouldRun(true)
            liquibase.afterPropertiesSet()
            LOGGER.info(String.format("Migrations on [%s] with user [%s] completed", dbUrl, username))
        } catch (e: LiquibaseException) {
            LOGGER.error("An error has occurred while applying database migrations", e)
            throw e
        }
    }

    private fun getDataSource(dbUrl: String, username: String, password: String): DataSource {
        return DataSourceBuilder
            .create()
            .url(dbUrl)
            .username(username)
            .password(password)
            .driverClassName(DATABASE_DRIVER_CLASS)
            .build()
    }
}