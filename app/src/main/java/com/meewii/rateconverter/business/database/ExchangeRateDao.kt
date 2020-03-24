package com.meewii.rateconverter.business.database

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import com.meewii.rateconverter.business.database.ExchangeRateDao.Companion.EXCHANGE_RATE_COLUMN_BASE_CURRENCY_ID
import com.meewii.rateconverter.business.database.ExchangeRateDao.Companion.EXCHANGE_RATE_COLUMN_LAST_UPDATE
import com.meewii.rateconverter.business.database.ExchangeRateDao.Companion.EXCHANGE_RATE_COLUMN_RATES
import com.meewii.rateconverter.business.database.ExchangeRateDao.Companion.EXCHANGE_RATE_TABLE_NAME
import io.reactivex.Single
import org.threeten.bp.LocalDateTime

@Dao
interface ExchangeRateDao {

  companion object {
    const val EXCHANGE_RATE_TABLE_NAME = "EXCHANGE_RATES"
    const val EXCHANGE_RATE_COLUMN_BASE_CURRENCY_ID = "BASE_CURRENCY_ID"
    const val EXCHANGE_RATE_COLUMN_LAST_UPDATE = "LAST_UPDATE"
    const val EXCHANGE_RATE_COLUMN_RATES = "RATES"
  }

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(rates: ExchangeRateEntity)

  @Transaction
  @Query("SELECT * FROM $EXCHANGE_RATE_TABLE_NAME WHERE $EXCHANGE_RATE_COLUMN_BASE_CURRENCY_ID = :baseCurrencyId")
  fun getRatesForBase(baseCurrencyId: String): Single<ExchangeRateEntity>

}

@Entity(tableName = EXCHANGE_RATE_TABLE_NAME)
data class ExchangeRateEntity(
  @PrimaryKey @ColumnInfo(name = EXCHANGE_RATE_COLUMN_BASE_CURRENCY_ID) var id: String,
  @ColumnInfo(name = EXCHANGE_RATE_COLUMN_LAST_UPDATE) val updatedAt: LocalDateTime,
  @ColumnInfo(name = EXCHANGE_RATE_COLUMN_RATES) val rates: Map<String, Double>
)