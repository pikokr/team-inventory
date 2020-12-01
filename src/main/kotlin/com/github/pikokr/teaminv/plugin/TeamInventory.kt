package com.github.pikokr.teaminv.plugin

import com.github.noonmaru.kommand.argument.player
import com.github.noonmaru.kommand.argument.string
import com.github.noonmaru.kommand.kommand
import com.github.pikokr.teaminv.command.accept
import com.github.pikokr.teaminv.command.join
import com.github.pikokr.teaminv.listener.InvListener
import com.github.pikokr.teaminv.team
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.Method

internal fun Class<*>.field(name: String): Field {
    return getDeclaredField(name).apply {
        isAccessible = true
    }
}

internal fun Class<*>.method(name: String, vararg types: Class<*>) : Method {
    return getDeclaredMethod(name, *types).apply {
        isAccessible = true
    }
}

internal fun Player.isLocked() : Boolean {
    return TeamInventory.locks.contains(this)
}

class TeamInventory : JavaPlugin() {
    companion object {
        lateinit var users: HashSet<String>
        lateinit var teams: HashSet<String>
        lateinit var teamsConf: YamlConfiguration
        lateinit var usersConf: YamlConfiguration
        val locks = HashSet<Player>()

        internal lateinit var instance: TeamInventory
        // 패트릭님 감사합니다
        // 해당 플러그인에 필요한, 서버 버전마다 달라지는 클래스들
        internal lateinit var craftItemStackClass: Class<*>
        internal lateinit var craftPlayerClass: Class<*>
        internal lateinit var entityHumanClass: Class<*>
        internal lateinit var itemStackClass: Class<*>
        internal lateinit var nonNullListClass: Class<*>
        internal lateinit var playerInventoryClass: Class<*>
    }

    val usersFile = File(dataFolder, "users.yml")
    val teamsFile = File(dataFolder, "teams.yml")

    val invites = HashMap<Player, String>()

    override fun onEnable() {
        instance = this
        // `v1_xx_Rx` 형태의 Minecraft 버전 반환
        val version = with("v\\d+_\\d+_R\\d+".toPattern().matcher(Bukkit.getServer()::class.java.`package`.name)) {
            when {
                find() -> group() //
                else -> throw NoSuchElementException("버전을 찾을 수 없습니다.")
            }
        }

        // 버전을 onEnable 밖에 선언하는 것을 방지하기 위해 onEnable 내부에 메소드 정의

        // net.minecraft.server 클래스 가져오기
        fun nms(className: String): Class<*> {
            return Class.forName("net.minecraft.server.$version.$className")
        }

        // org.bukkit.craftbukkit 클래스 가져오기
        fun craftBukkit(className: String): Class<*> {
            return Class.forName("org.bukkit.craftbukkit.$version.$className")
        }

        // 위에서 lateinit 으로 생성해둔 클래스들 정의하기
        craftItemStackClass = craftBukkit("inventory.CraftItemStack")
        craftPlayerClass = craftBukkit("entity.CraftPlayer")
        entityHumanClass = nms("EntityHuman")
        itemStackClass = nms("ItemStack")
        nonNullListClass = nms("NonNullList")
        playerInventoryClass = nms("PlayerInventory")

        load()

        // Listener 클래스 분리
        server.pluginManager.registerEvents(InvListener(), this)


        kommand {
            register("tinv") {
                then("join") {
                    then("name" to team()) {
                        executes {
                            join(it)
                        }
                    }
                }
                then("accept") {
                    then("player" to player()) {
                        executes {
                            accept(it)
                        }
                    }
                }
            }
        }
    }

    override fun onDisable() {
        save()
    }


    fun load() {
        @Suppress("UNCHECKED_CAST")
        fun ConfigurationSection.loadItemStackList(name: String, list: AbstractList<*>) {
            val items = getMapList(name).map { args ->
                val itemStack = ItemStack.deserialize(args as Map<String, Any>)

                // net.minecraft.server.ItemStack 객체 가져오기 -> CraftItemStack::asNMSCopy
                craftItemStackClass.method("asNMSCopy", ItemStack::class.java).invoke(null, itemStack)
            } as List<*>

            for (i in 0 until minOf(list.count(), items.count())) {
                nonNullListClass.method("set", Int::class.java, Any::class.java).invoke(list, i, items[i])
            }
        }
        usersConf = if (usersFile.exists()) YamlConfiguration.loadConfiguration(usersFile) else YamlConfiguration()
        teamsConf = if (teamsFile.exists()) YamlConfiguration.loadConfiguration(teamsFile) else YamlConfiguration()
        if (!usersConf.contains("users")) usersConf.set("users", arrayOf<String>())
        if (!teamsConf.contains("teams")) teamsConf.set("teams", arrayOf<String>())
        users = HashSet()
        teams = HashSet()
        for (i in usersConf.getStringList("users")) users.add(i)
        for (i in teamsConf.getStringList("teams")) teams.add(i)
        Bukkit.getOnlinePlayers().forEach {
            if (users.find { user -> user == it.uniqueId.toString() } == null) {
                lock(it)
            }
        }
        for (user in users) {
            if (usersConf.getString(user) == null) users.remove(user)
        }
    }

    fun lock(player: Player) {
        locks.add(player)
        val item = ItemStack(Material.BARRIER)
        item.itemMeta = item.itemMeta.apply {
            setDisplayName("/tinv join <팀이름> 명령어로 팀을 설정해주세요")
        }
        for (i in 0..player.inventory.contents.size) {
            player.inventory.setItem(i, item.clone())
        }
    }

    fun save() {
        usersFile.also { it.parentFile.mkdirs() }
        teamsFile.also { it.parentFile.mkdirs() }
        usersConf.set("users", users.toTypedArray())
        teamsConf.set("teams", teams.toTypedArray())
        usersConf.save(usersFile)
        teamsConf.save(teamsFile)
    }
}