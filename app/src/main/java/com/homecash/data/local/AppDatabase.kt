package com.homecash.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.homecash.data.local.dao.*
import com.homecash.data.local.entity.*
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [
        UserEntity::class,
        DeviceEntity::class,
        LineEntity::class,
        WalletEntity::class,
        TransactionEntity::class,
        FlagEntity::class,
        CategoryRuleEntity::class,
        ShiftCloseEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun deviceDao(): DeviceDao
    abstract fun lineDao(): LineDao
    abstract fun walletDao(): WalletDao
    abstract fun transactionDao(): TransactionDao
    abstract fun flagDao(): FlagDao
    abstract fun categoryRuleDao(): CategoryRuleDao
    abstract fun shiftCloseDao(): ShiftCloseDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP INDEX IF EXISTS index_transactions_fingerprint")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_transactions_userId_fingerprint ON transactions(userId, fingerprint)")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase = DatabasePassphraseManager.getPassphrase(context.applicationContext)
                val factory = SupportFactory(passphrase)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "homecash_database.db"
                )
                    .openHelperFactory(factory)
                    // Preserve financial records when migrating the transaction fingerprint index.
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
