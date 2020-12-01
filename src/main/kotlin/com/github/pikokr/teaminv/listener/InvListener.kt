package com.github.pikokr.teaminv.listener

import com.github.pikokr.teaminv.plugin.TeamInventory
import com.github.pikokr.teaminv.plugin.isLocked
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.world.WorldSaveEvent

class InvListener : Listener {
    @EventHandler
    fun onSave(event: WorldSaveEvent) {
        TeamInventory.instance.save()
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (TeamInventory.users.find { it === event.player.uniqueId.toString() } == null) {
            TeamInventory.instance.lock(event.player)
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if ((event.whoClicked as Player).isLocked()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        if (event.player.isLocked()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDrop(event: PlayerDropItemEvent) {
        if (event.player.isLocked()) {
            event.isCancelled = true
        }
    }
}