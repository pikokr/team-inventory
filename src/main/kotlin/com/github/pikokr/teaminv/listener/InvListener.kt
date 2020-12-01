package com.github.pikokr.teaminv.listener

import com.github.pikokr.teaminv.plugin.TeamInventory
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldSaveEvent

class InvListener : Listener {
    @EventHandler
    fun onSave(event: WorldSaveEvent) {
        TeamInventory.instance.save()
    }
}