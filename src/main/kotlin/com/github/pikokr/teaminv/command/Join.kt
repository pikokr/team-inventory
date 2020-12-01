package com.github.pikokr.teaminv.command

import com.github.noonmaru.kommand.KommandContext
import com.github.pikokr.teaminv.plugin.TeamInventory
import org.bukkit.entity.Player

fun join(ctx: KommandContext) {
    val pl = TeamInventory.instance
    if (TeamInventory.users.find { it == (ctx.sender as Player).uniqueId.toString() } == null) {
        ctx.sender.sendMessage("팀 생성/가입")
    } else {
        ctx.sender.sendMessage("이미 다른 팀에 가입되어 있네요! 이미 가입한 팀에서 나갈수 없어요!(아직 구현 안됨)")
    }
}