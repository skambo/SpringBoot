package io.skambo.example.integration.rules

import io.skambo.example.integration.rules.helpers.TestTableClearer
import org.junit.rules.ExternalResource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ClearDatabaseRule: ExternalResource() {
    @Autowired
    private lateinit var testTableClearer: TestTableClearer

    override fun before(){
        testTableClearer.clearTables()
    }
}