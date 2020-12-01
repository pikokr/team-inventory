package com.github.pikokr.teaminv.command

import com.github.noonmaru.kommand.KommandContext
import com.github.pikokr.teaminv.plugin.TeamInventory
import org.bukkit.entity.Player

fun join(ctx: KommandContext) {
    val pl = TeamInventory.instance
    println(TeamInventory.users)
    println(TeamInventory.teams)
    if (TeamInventory.users.find { it == (ctx.sender as Player).uniqueId.toString() } == null) {
        val name = ctx.getArgument("name")
        if (TeamInventory.teams.find { it === name } == null) {
            TeamInventory.teams.add(name)
            TeamInventory.users.add((ctx.sender as Player).uniqueId.toString())
            TeamInventory.teamsConf.set("$name.owner", (ctx.sender as Player).uniqueId.toString())
            TeamInventory.usersConf.set("${(ctx.sender as Player).uniqueId}", name)
            ctx.sender.sendMessage("팀을 생성했습니다")
        } else {
            ctx.sender.sendMessage("가입")
        }
    } else {
        ctx.sender.sendMessage("이미 다른 팀에 가입되어 있네요! 이미 가입한 팀에서 나갈수 없어요!(아직 구현 안됨)")
    }
}