package com.github.pikokr.teaminv

import com.github.noonmaru.kommand.KommandContext
import com.github.noonmaru.kommand.argument.KommandArgument
import com.github.noonmaru.kommand.argument.suggestions
import com.github.pikokr.teaminv.plugin.TeamInventory

class TeamArgument internal constructor(
        private val values: () -> Collection<String>
) : KommandArgument<String> {
    override fun parse(context: KommandContext, param: String): String? {
        val values = values()

        return param.takeIf { values.isEmpty() || param in values }
    }

    override fun listSuggestion(context: KommandContext, target: String): Collection<String> {
        return values().suggestions(target)
    }

    companion object {
        internal val teamArgument = TeamArgument {
            TeamInventory.teams.toList()
        }
    }
}

fun team() = TeamArgument.teamArgument
