package com.github.pikokr.teaminv

import com.github.pikokr.teaminv.plugin.TeamInventory
import com.github.pikokr.teaminv.plugin.field
import com.google.common.collect.ImmutableList

class TInventory {
    val inv = TeamInventory.playerInventoryClass.getConstructor(TeamInventory.entityHumanClass).newInstance(null)

    // net.minecraft.server.NonNullList extends java.util.AbstractList
    // NonNullList 는 서버 버전마다 달라지는 net.minecraft.server 클래스이므로 부모 클래스 사용
    val items: AbstractList<*>
    val armor: AbstractList<*>
    val extraSlots: AbstractList<*>
    val contents: List<AbstractList<*>>

    init {
        inv.javaClass.run {
            items = field("items").get(inv) as AbstractList<*>
            armor = field("armor").get(inv) as AbstractList<*>
            extraSlots = field("extraSlots").get(inv) as AbstractList<*>
        }

        contents = ImmutableList.of(items, armor, extraSlots)
    }
}