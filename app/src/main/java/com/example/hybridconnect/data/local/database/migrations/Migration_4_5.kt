package com.example.hybridconnect.data.local.database.migrations

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec


@RenameColumn(
    tableName = "transactions",
    fromColumnName = "forwarded",
    toColumnName = "isForwarded"
)
class Migration_4_5 : AutoMigrationSpec