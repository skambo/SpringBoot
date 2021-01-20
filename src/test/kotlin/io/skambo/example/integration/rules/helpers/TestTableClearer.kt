package io.skambo.example.integration.rules.helpers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

@Service
class TestTableClearer {
    private val logger: Logger = LoggerFactory.getLogger(TestTableClearer::class.java)

    private val DB_CHANGELOG_TABLES: List<String> = listOf("DATABASECHANGELOG", "DATABASECHANGELOGLOCK")

    @Autowired
    private lateinit var dataSource: DataSource

    private lateinit var connection: Connection

    fun clearTables(){
        try{
            connection = dataSource.connection
            clear(getTableNames())
            connection.close()
        } catch(exception: SQLException){
            throw RuntimeException(exception)
        }
    }

    private fun getTableNames(): List<String> {
        val tableNames: MutableList<String> = mutableListOf()
        val resultSet: ResultSet = connection.metaData.getTables(
            connection.getCatalog(),
            null,
            null,
            arrayOf("TABLE"))

        while(resultSet.next()){
            tableNames.add(resultSet.getString("TABLE_NAME"))
        }

        // we cannot clear DB change log tables because they track migrations that have already ran
        tableNames.removeAll(DB_CHANGELOG_TABLES)
        tableNames.remove("hibernate_sequence")
        return tableNames
    }

    private fun clear(tableNames: List<String>){
        // we cannot clear DB change log tables 
        tableNames.toMutableList().removeAll(DB_CHANGELOG_TABLES)
        logger.debug("Executing SQL")
        buildSqlStatement(tableNames).executeBatch()
    }

    private fun buildSqlStatement(tableNames: List<String>): Statement {
        val statement: Statement = connection.createStatement()

        statement.addBatch(sql("SET FOREIGN_KEY_CHECKS = 0"));
        addDeleteStatements(tableNames, statement);
        statement.addBatch(sql("SET FOREIGN_KEY_CHECKS = 1"));

        return statement;
    }

    private fun addDeleteStatements(tableNames: List<String>, statement: Statement){
        tableNames.forEach{ tableName ->
            try {
                statement.addBatch(sql("DELETE FROM " + tableName))
            } catch (e:SQLException) {
                throw RuntimeException(e)
            }
        }
    }

    private fun sql(sql: String): String{
        logger.debug("Adding SQL: {}", sql)
        return sql
    }
}