package com.github.pikokr.teaminv.command

import com.github.noonmaru.kommand.KommandContext
import com.github.pikokr.teaminv.plugin.TeamInventory
import org.bukkit.entity.Player

fun accept(ctx: KommandContext) {
    val pl = TeamInventory.instance
    val invites = pl.invites
    val player = ctx.parseArgument<Player>("player")
    println(invites[player])
}