import command.CommandHandler
import config.ConfigHandler
import data.migration.Create_user_table
import data.migration.TableMigratory
import data.migration.component.Migration
import extension.info
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.reflect.full.memberProperties

/**
 * Created by karayuu on 2019/01/02
 */
class AutoFarming : JavaPlugin() {
    override fun onEnable() {
        plugin = this

        Bukkit.getPluginManager().registerEvents(PlayerInteractListener, this)
        Bukkit.getPluginManager().registerEvents(PlayerDataListener, this)

        CommandHandler.register()

        ConfigHandler.register()

        //テーブル作成を請け負う.
        TableMigratory.migrate()
    }

    companion object {
        lateinit var plugin: AutoFarming
        val playerData = mutableMapOf<UUID, List<Migration>>()

        fun runTaskAsynchronously(runnable: Runnable) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
        }
    }
}

object PlayerInteractListener : Listener {
    @EventHandler
    fun onPlayerRightclickCrop(e: PlayerInteractEvent) {
        val block = e.clickedBlock ?: return
        val world = block.world

        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        if (block.type != Material.WHEAT) return
        if (block.data != 7.toByte()) return

        val age = block.blockData as Ageable
        age.age = 0
        block.blockData = age

        world.dropItem(block.location, ItemStack(Material.WHEAT, 1))
    }
}

object PlayerDataListener : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        AutoFarming.runTaskAsynchronously(Runnable {
            val data = TableMigratory.load(player)
            AutoFarming.playerData[player.uniqueId] = data
        })
    }
}

fun main(args: Array<String>) {
    println(Create_user_table::class.memberProperties.map { it.name })
}
