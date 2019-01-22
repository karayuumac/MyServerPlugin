package data.migration

import data.migration.component.Migration
import org.bukkit.entity.Player

/**
 * @author karayuu
 */

class Create_user_table : Migration("user_table_mining") {
    var mining_all = table.int("mining_all")
}

object TableMigratory {
    /** Migrationを行うクラスを指定します. */
    private val tables = listOf(
        Create_user_table::class.java
    )

    private val tables_instances = mutableListOf<Migration>()

    /**
     * [tables]で指定した全てのテーブルの作成を行います.
     */
    fun migrate() {
        tables.forEach {
            //インスタンス作成,migrate呼び出しにより,migrationが実行される.
            val migration = it.newInstance()
            tables_instances.add(migration)
            migration.migrate()
        }
    }

    /**
     * [tables]で指定したすべてのテーブルから[player]のデータを取得します.
     * 非同期下で実行して下さい.
     */
    fun load(player: Player): List<Migration> {
        val list = mutableListOf<Migration>()
        tables_instances.forEach {
            val data = it.createAndLoad(player, it.javaClass)
            if (data != null) {
                list.add(data)
            }
        }
        return list.toList()
    }
}
