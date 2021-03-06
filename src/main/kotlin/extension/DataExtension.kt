package extension

import data.CannotFindPlayerDataException
import data.migration.component.Migration
import org.bukkit.entity.Player
import java.util.*

/**
 * @author karayuu
 */
/**
 * [clazz]で指定したクラスのMigrationを取得します.
 */
@Suppress("UNCHECKED_CAST")
fun <T: Migration> List<Migration>.find(clazz: Class<T>): T? {
    val list = this
    for (migration in list) {
        if (migration.javaClass == clazz) {
            return migration as T
        }
    }
    return null
}

fun <T: Migration> Map<UUID, List<Migration>>.find(player: Player, clazz: Class<T>): T {
    val list = this[player.uniqueId] ?: throw CannotFindPlayerDataException(player)
    return list.find(clazz)!!
}

/**
 * [player]のデータをSQLに保存し,[playerdata]Mapから削除します.
 * 非同期下で実行してください.
 */
fun Map<UUID, List<Migration>>.save(player: Player) {
    val list = this[player.uniqueId] ?: throw CannotFindPlayerDataException(player)
    list.forEach { it.update(player) }
    AutoFarming.playerData.remove(player.uniqueId)
}

/**
 * [player]のデータをSQLに保存し,[playerdata]Mapから削除します.
 * 非同期下で実行してください.
 */
fun Player.save() {
    val data = AutoFarming.playerData[this.uniqueId] ?: throw CannotFindPlayerDataException(player)
    data.forEach { it.update(this) }
    AutoFarming.playerData.remove(player.uniqueId)
}

fun <T: Migration> Player.find(clazz: Class<T>): T {
    return AutoFarming.playerData.find(this, clazz)
}
